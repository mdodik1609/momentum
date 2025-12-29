import 'dart:io';
import 'dart:math' as math;
import 'package:xml/xml.dart';
import 'package:drift/drift.dart';
import '../models/activity.dart';
import '../database/database.dart';

class FileImportService {
  final AppDatabase database;

  FileImportService(this.database);

  Future<int> importFromDirectory(String directoryPath) async {
    final directory = Directory(directoryPath);
    if (!await directory.exists()) {
      throw Exception('Directory does not exist: $directoryPath');
    }

    final files = directory
        .listSync()
        .whereType<File>()
        .where(
          (file) => file.path.endsWith('.gpx') || file.path.endsWith('.tcx'),
        )
        .toList();

    int importedCount = 0;

    for (final file in files) {
      try {
        if (file.path.endsWith('.gpx')) {
          await _importGpxFile(file);
          importedCount++;
        } else if (file.path.endsWith('.tcx')) {
          await _importTcxFile(file);
          importedCount++;
        }
      } catch (e) {
        print('Error importing ${file.path}: $e');
      }
    }

    return importedCount;
  }

  Future<void> _importGpxFile(File file) async {
    final content = await file.readAsString();
    final document = XmlDocument.parse(content);

    final trk = document.findAllElements('trk').firstOrNull;
    if (trk == null) return;

    final name =
        trk.findElements('name').firstOrNull?.innerText ?? 'GPX Activity';
    final trkseg = trk.findElements('trkseg').firstOrNull;

    if (trkseg == null) return;

    final trkpts = trkseg.findElements('trkpt').toList();
    if (trkpts.isEmpty) return;

    // Parse first point for start time
    final firstPoint = trkpts.first;
    final timeText = firstPoint.findElements('time').firstOrNull?.innerText;
    final startDate = timeText != null
        ? DateTime.parse(timeText).toUtc()
        : DateTime.now().toUtc();

    // Calculate distance and time
    double totalDistance = 0.0;
    double? lastLat, lastLon;

    final streams = <ActivityStreamsCompanion>[];
    int timeOffset = 0;

    for (final trkpt in trkpts) {
      final pointLat = double.tryParse(trkpt.getAttribute('lat') ?? '') ?? 0.0;
      final pointLon = double.tryParse(trkpt.getAttribute('lon') ?? '') ?? 0.0;
      final ele = double.tryParse(
        trkpt.findElements('ele').firstOrNull?.innerText ?? '',
      );

      if (lastLat != null && lastLon != null) {
        totalDistance += _calculateDistance(
          lastLat,
          lastLon,
          pointLat,
          pointLon,
        );
      }

      streams.add(
        ActivityStreamsCompanion.insert(
          activityId: file.path.split('/').last.split('\\').last,
          timeOffsetSeconds: timeOffset,
          latitude: Value(pointLat),
          longitude: Value(pointLon),
          altitudeMeters: Value(ele),
          distanceMeters: Value(totalDistance),
        ),
      );

      lastLat = pointLat;
      lastLon = pointLon;
      timeOffset++;
    }

    final movingTime = streams.length;
    final activity = ActivitiesCompanion.insert(
      id: file.path.split('/').last.split('\\').last,
      name: name,
      sportType: SportType.run,
      startDate: startDate,
      movingTimeSeconds: movingTime,
      elapsedTimeSeconds: movingTime,
      distanceMeters: Value(totalDistance),
      elevationGainMeters: Value(_calculateElevationGain(streams)),
      createdAt: DateTime.now().toUtc(),
      updatedAt: DateTime.now().toUtc(),
    );

    await database.into(database.activities).insert(activity);
    await database.batch((batch) {
      batch.insertAll(database.activityStreams, streams);
    });
  }

  Future<void> _importTcxFile(File file) async {
    // TCX import similar to GPX
    // Implementation would parse TCX XML format
    print('TCX import not yet implemented for ${file.path}');
  }

  double _calculateDistance(
    double lat1,
    double lon1,
    double lat2,
    double lon2,
  ) {
    const double earthRadius = 6371000; // meters
    final dLat = _toRadians(lat2 - lat1);
    final dLon = _toRadians(lon2 - lon1);

    final sinDLat = math.sin(dLat / 2);
    final sinDLon = math.sin(dLon / 2);
    final cosLat1 = math.cos(_toRadians(lat1));
    final cosLat2 = math.cos(_toRadians(lat2));

    final a = sinDLat * sinDLat + cosLat1 * cosLat2 * sinDLon * sinDLon;
    final c = 2 * math.asin(math.sqrt(a));

    return earthRadius * c;
  }

  double _toRadians(double degrees) => degrees * (math.pi / 180.0);

  double _calculateElevationGain(List<ActivityStreamsCompanion> streams) {
    double gain = 0.0;
    double? lastAlt;

    for (final stream in streams) {
      final alt = stream.altitudeMeters.value;
      if (alt != null && lastAlt != null && alt > lastAlt) {
        gain += alt - lastAlt;
      }
      lastAlt = alt;
    }

    return gain;
  }
}
