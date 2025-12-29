import 'dart:io';
import 'dart:math' as math;
import 'package:xml/xml.dart';
import 'package:drift/drift.dart';
import '../models/activity.dart' show SportType;
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
          (file) => file.path.endsWith('.gpx') || 
                    file.path.endsWith('.tcx') || 
                    file.path.endsWith('.fit'),
        )
        .toList();

    int importedCount = 0;

    for (final file in files) {
      try {
        if (file.path.endsWith('.gpx')) {
          await importGpxFile(file);
          importedCount++;
        } else if (file.path.endsWith('.tcx')) {
          await importTcxFile(file);
          importedCount++;
        } else if (file.path.endsWith('.fit')) {
          await importFitFile(file);
          importedCount++;
        }
      } catch (e) {
        print('Error importing ${file.path}: $e');
      }
    }

    return importedCount;
  }

  Future<void> importGpxFile(File file) async {
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

    // Generate unique activity ID
    final activityId = '${startDate.millisecondsSinceEpoch}_${file.path.hashCode}';

    // Calculate distance and time
    double totalDistance = 0.0;
    double? lastLat, lastLon;

    final streams = <ActivityStreamsCompanion>[];
    int timeOffset = 0;

    for (final trkpt in trkpts) {
      final pointLat = double.tryParse(trkpt.getAttribute('lat') ?? '');
      final pointLon = double.tryParse(trkpt.getAttribute('lon') ?? '');
      final ele = double.tryParse(
        trkpt.findElements('ele').firstOrNull?.innerText ?? '',
      );

      if (pointLat == null || pointLon == null) continue;

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
          activityId: activityId,
          timeOffsetSeconds: timeOffset,
          latitude: Value(pointLat),
          longitude: Value(pointLon),
          altitudeMeters: ele != null ? Value(ele) : const Value.absent(),
          distanceMeters: Value(totalDistance),
        ),
      );

      lastLat = pointLat;
      lastLon = pointLon;
      timeOffset++;
    }

    if (streams.isEmpty) return;

    final movingTime = streams.length;
    final elevationGain = _calculateElevationGain(streams);
    final now = DateTime.now().toUtc();
    
    final activity = ActivitiesCompanion.insert(
      id: activityId,
      name: name,
      sportType: SportType.run,
      startDate: startDate,
      movingTimeSeconds: movingTime,
      elapsedTimeSeconds: movingTime,
      distanceMeters: totalDistance > 0 ? Value(totalDistance) : const Value.absent(),
      elevationGainMeters: elevationGain > 0 ? Value(elevationGain) : const Value.absent(),
      createdAt: now,
      updatedAt: now,
    );

    await database.into(database.activities).insert(activity);
    await database.batch((batch) {
      batch.insertAll(database.activityStreams, streams);
    });
  }

  Future<void> importTcxFile(File file) async {
    final content = await file.readAsString();
    final document = XmlDocument.parse(content);

    final activity = document.findAllElements('Activity').firstOrNull;
    if (activity == null) return;

    final sportType = activity.getAttribute('Sport') ?? 'Other';
    final name = activity.findElements('Id').firstOrNull?.innerText ?? 'TCX Activity';
    final startDateText = activity.findElements('Id').firstOrNull?.innerText;
    final startDate = startDateText != null
        ? DateTime.parse(startDateText).toUtc()
        : DateTime.now().toUtc();

    final activityId = '${startDate.millisecondsSinceEpoch}_${file.path.hashCode}';

    final lap = activity.findElements('Lap').firstOrNull;
    if (lap == null) return;

    final track = lap.findElements('Track').firstOrNull;
    if (track == null) return;

    final trackpoints = track.findElements('Trackpoint').toList();
    if (trackpoints.isEmpty) return;

    double totalDistance = 0.0;
    double? lastLat, lastLon;
    final streams = <ActivityStreamsCompanion>[];
    int timeOffset = 0;

    for (final trackpoint in trackpoints) {
      final position = trackpoint.findElements('Position').firstOrNull;
      if (position == null) continue;

      final latText = position.findElements('LatitudeDegrees').firstOrNull?.innerText;
      final lonText = position.findElements('LongitudeDegrees').firstOrNull?.innerText;
      final altText = trackpoint.findElements('AltitudeMeters').firstOrNull?.innerText;
      final hrText = trackpoint.findElements('HeartRateBpm').firstOrNull?.findElements('Value').firstOrNull?.innerText;

      final pointLat = latText != null ? double.tryParse(latText) : null;
      final pointLon = lonText != null ? double.tryParse(lonText) : null;
      final ele = altText != null ? double.tryParse(altText) : null;
      final hr = hrText != null ? int.tryParse(hrText) : null;

      if (pointLat == null || pointLon == null) continue;

      if (lastLat != null && lastLon != null) {
        totalDistance += _calculateDistance(lastLat, lastLon, pointLat, pointLon);
      }

      streams.add(
        ActivityStreamsCompanion.insert(
          activityId: activityId,
          timeOffsetSeconds: timeOffset,
          latitude: Value(pointLat),
          longitude: Value(pointLon),
          altitudeMeters: ele != null ? Value(ele) : const Value.absent(),
          heartRateBpm: hr != null ? Value(hr) : const Value.absent(),
          distanceMeters: Value(totalDistance),
        ),
      );

      lastLat = pointLat;
      lastLon = pointLon;
      timeOffset++;
    }

    if (streams.isEmpty) return;

    final movingTime = streams.length;
    final elevationGain = _calculateElevationGain(streams);
    final now = DateTime.now().toUtc();

    final sport = _mapTcxSportToSportType(sportType);
    
    final activityData = ActivitiesCompanion.insert(
      id: activityId,
      name: name,
      sportType: sport,
      startDate: startDate,
      movingTimeSeconds: movingTime,
      elapsedTimeSeconds: movingTime,
      distanceMeters: totalDistance > 0 ? Value(totalDistance) : const Value.absent(),
      elevationGainMeters: elevationGain > 0 ? Value(elevationGain) : const Value.absent(),
      createdAt: now,
      updatedAt: now,
    );

    await database.into(database.activities).insert(activityData);
    await database.batch((batch) {
      batch.insertAll(database.activityStreams, streams);
    });
  }

  Future<void> importFitFile(File file) async {
    // Basic FIT file import
    // FIT files are binary format, this is a simplified implementation
    // For full FIT support, consider using a dedicated FIT parser library
    final bytes = await file.readAsBytes();
    
    if (bytes.length < 14) {
      throw Exception('Invalid FIT file: too short');
    }

    // Check FIT file header (14 bytes)
    if (bytes[0] != 0x0E || bytes[8] != 0x2E) {
      throw Exception('Invalid FIT file: incorrect header');
    }

    // For now, we'll create a placeholder activity
    // Full FIT parsing requires parsing binary messages which is complex
    final startDate = DateTime.now().toUtc();
    final activityId = '${startDate.millisecondsSinceEpoch}_${file.path.hashCode}';
    final name = file.path.split('/').last.replaceAll('.fit', '');

    // Create a basic activity entry
    // Note: Full FIT parsing would extract GPS, HR, power, etc. from binary messages
    final activity = ActivitiesCompanion.insert(
      id: activityId,
      name: name,
      sportType: SportType.run, // Default, would be parsed from FIT file
      startDate: startDate,
      movingTimeSeconds: 0,
      elapsedTimeSeconds: 0,
      createdAt: startDate,
      updatedAt: startDate,
      source: const Value('fit_file'),
    );

    await database.into(database.activities).insert(activity);
    
    // TODO: Implement full FIT binary parsing for GPS tracks, HR, power data
    // This requires parsing FIT file structure with message definitions
  }

  SportType _mapTcxSportToSportType(String tcxSport) {
    switch (tcxSport.toLowerCase()) {
      case 'running':
        return SportType.run;
      case 'biking':
      case 'cycling':
        return SportType.ride;
      case 'walking':
        return SportType.walk;
      case 'hiking':
        return SportType.hike;
      case 'swimming':
        return SportType.swim;
      default:
        return SportType.other;
    }
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
      final alt = stream.altitudeMeters.present ? stream.altitudeMeters.value : null;
      if (alt != null && lastAlt != null && alt > lastAlt) {
        gain += alt - lastAlt;
      }
      if (alt != null) {
        lastAlt = alt;
      }
    }

    return gain;
  }
}
