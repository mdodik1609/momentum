import 'dart:io';
import 'package:path_provider/path_provider.dart';
import 'package:path/path.dart' as p;
import 'package:intl/intl.dart';
import 'package:drift/drift.dart' as drift;
import '../database/database.dart';
import '../models/activity.dart' show SportType;

/// Service for exporting activities to various formats
class ExportService {
  final AppDatabase database;
  
  ExportService(this.database);
  
  /// Export activity to GPX format
  Future<File> exportToGpx(String activityId) async {
    final activity = await (database.select(database.activities)
          ..where((a) => a.id.equals(activityId)))
        .getSingleOrNull();
    
    if (activity == null) {
      throw Exception('Activity not found');
    }
    
    final streams = await (database.select(database.activityStreams)
          ..where((s) => s.activityId.equals(activityId))
          ..orderBy([(s) => drift.OrderingTerm.asc(s.timeOffsetSeconds)]))
        .get();
    
    final gpxPoints = streams
        .where((s) => s.latitude != null && s.longitude != null)
        .toList();
    
    if (gpxPoints.isEmpty) {
      throw Exception('No GPS data available for export');
    }
    
    final gpx = StringBuffer();
    gpx.writeln('<?xml version="1.0" encoding="UTF-8"?>');
    gpx.writeln('<gpx version="1.1" creator="Momentum">');
    gpx.writeln('  <metadata>');
    gpx.writeln('    <name>${_escapeXml(activity.name)}</name>');
    gpx.writeln('    <time>${_formatIso8601(activity.startDate)}</time>');
    gpx.writeln('  </metadata>');
    gpx.writeln('  <trk>');
    gpx.writeln('    <name>${_escapeXml(activity.name)}</name>');
    gpx.writeln('    <type>${activity.sportType.displayName}</type>');
    gpx.writeln('    <trkseg>');
    
    DateTime currentTime = activity.startDate;
    for (final point in gpxPoints) {
      gpx.writeln('      <trkpt lat="${point.latitude}" lon="${point.longitude}">');
      if (point.altitudeMeters != null) {
        gpx.writeln('        <ele>${point.altitudeMeters}</ele>');
      }
      gpx.writeln('        <time>${_formatIso8601(currentTime)}</time>');
      if (point.heartRateBpm != null) {
        gpx.writeln('        <extensions>');
        gpx.writeln('          <heartrate>${point.heartRateBpm}</heartrate>');
        gpx.writeln('        </extensions>');
      }
      gpx.writeln('      </trkpt>');
      
      // Estimate time increment (assuming 1 second intervals)
      currentTime = currentTime.add(const Duration(seconds: 1));
    }
    
    gpx.writeln('    </trkseg>');
    gpx.writeln('  </trk>');
    gpx.writeln('</gpx>');
    
    // Save to file
    final directory = await getApplicationDocumentsDirectory();
    final exportDir = Directory(p.join(directory.path, 'exports'));
    if (!await exportDir.exists()) {
      await exportDir.create(recursive: true);
    }
    
    final fileName = '${activity.name.replaceAll(RegExp(r'[^\w\s-]'), '_')}_${activityId}.gpx';
    final file = File(p.join(exportDir.path, fileName));
    await file.writeAsString(gpx.toString());
    
    return file;
  }
  
  /// Export activity to TCX format
  Future<File> exportToTcx(String activityId) async {
    final activity = await (database.select(database.activities)
          ..where((a) => a.id.equals(activityId)))
        .getSingleOrNull();
    
    if (activity == null) {
      throw Exception('Activity not found');
    }
    
    final streams = await (database.select(database.activityStreams)
          ..where((s) => s.activityId.equals(activityId))
          ..orderBy([(s) => drift.OrderingTerm.asc(s.timeOffsetSeconds)]))
        .get();
    
    final tcx = StringBuffer();
    tcx.writeln('<?xml version="1.0" encoding="UTF-8"?>');
    tcx.writeln('<TrainingCenterDatabase xmlns="http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2">');
    tcx.writeln('  <Activities>');
    tcx.writeln('    <Activity Sport="${_mapSportTypeToTcx(activity.sportType)}">');
    tcx.writeln('      <Id>${_formatIso8601(activity.startDate)}</Id>');
    
    if (streams.isNotEmpty) {
      tcx.writeln('      <Lap StartTime="${_formatIso8601(activity.startDate)}">');
      tcx.writeln('        <TotalTimeSeconds>${activity.movingTimeSeconds}</TotalTimeSeconds>');
      if (activity.distanceMeters != null) {
        tcx.writeln('        <DistanceMeters>${activity.distanceMeters}</DistanceMeters>');
      }
      if (activity.averageHeartRateBpm != null) {
        tcx.writeln('        <AverageHeartRateBpm>');
        tcx.writeln('          <Value>${activity.averageHeartRateBpm}</Value>');
        tcx.writeln('        </AverageHeartRateBpm>');
      }
      if (activity.maxHeartRateBpm != null) {
        tcx.writeln('        <MaximumHeartRateBpm>');
        tcx.writeln('          <Value>${activity.maxHeartRateBpm}</Value>');
        tcx.writeln('        </MaximumHeartRateBpm>');
      }
      if (activity.calories != null) {
        tcx.writeln('        <Calories>${activity.calories}</Calories>');
      }
      tcx.writeln('        <Intensity>Active</Intensity>');
      tcx.writeln('        <TriggerMethod>Manual</TriggerMethod>');
      
      // Track points
      tcx.writeln('        <Track>');
      DateTime currentTime = activity.startDate;
      for (final point in streams) {
        if (point.latitude != null && point.longitude != null) {
          tcx.writeln('          <Trackpoint>');
          tcx.writeln('            <Time>${_formatIso8601(currentTime)}</Time>');
          tcx.writeln('            <Position>');
          tcx.writeln('              <LatitudeDegrees>${point.latitude}</LatitudeDegrees>');
          tcx.writeln('              <LongitudeDegrees>${point.longitude}</LongitudeDegrees>');
          tcx.writeln('            </Position>');
          if (point.altitudeMeters != null) {
            tcx.writeln('            <AltitudeMeters>${point.altitudeMeters}</AltitudeMeters>');
          }
          if (point.heartRateBpm != null) {
            tcx.writeln('            <HeartRateBpm>');
            tcx.writeln('              <Value>${point.heartRateBpm}</Value>');
            tcx.writeln('            </HeartRateBpm>');
          }
          if (point.cadenceRpm != null) {
            tcx.writeln('            <Cadence>${(point.cadenceRpm! * 2).toInt()}</Cadence>');
          }
          tcx.writeln('          </Trackpoint>');
          
          currentTime = currentTime.add(const Duration(seconds: 1));
        }
      }
      tcx.writeln('        </Track>');
      tcx.writeln('      </Lap>');
    }
    
    tcx.writeln('    </Activity>');
    tcx.writeln('  </Activities>');
    tcx.writeln('</TrainingCenterDatabase>');
    
    // Save to file
    final directory = await getApplicationDocumentsDirectory();
    final exportDir = Directory(p.join(directory.path, 'exports'));
    if (!await exportDir.exists()) {
      await exportDir.create(recursive: true);
    }
    
    final fileName = '${activity.name.replaceAll(RegExp(r'[^\w\s-]'), '_')}_${activityId}.tcx';
    final file = File(p.join(exportDir.path, fileName));
    await file.writeAsString(tcx.toString());
    
    return file;
  }
  
  /// Export all activities summary to CSV
  Future<File> exportSummaryToCsv() async {
    final activities = await (database.select(database.activities)
          ..orderBy([(a) => drift.OrderingTerm.desc(a.startDate)]))
        .get();
    
    final csv = StringBuffer();
    csv.writeln('Date,Name,Sport,Distance (km),Time (min),Elevation (m),Avg HR,Avg Pace (min/km)');
    
    for (final activity in activities) {
      final distance = activity.distanceMeters != null 
          ? (activity.distanceMeters! / 1000).toStringAsFixed(2)
          : '';
      final time = (activity.movingTimeSeconds / 60).toStringAsFixed(1);
      final elevation = activity.elevationGainMeters != null
          ? activity.elevationGainMeters!.toStringAsFixed(0)
          : '';
      final avgHr = activity.averageHeartRateBpm?.toString() ?? '';
      final pace = activity.averageSpeedMps != null && activity.averageSpeedMps! > 0
          ? (1000 / activity.averageSpeedMps! / 60).toStringAsFixed(2)
          : '';
      
      csv.writeln(
        '${DateFormat('yyyy-MM-dd HH:mm').format(activity.startDate)},'
        '${_escapeCsv(activity.name)},'
        '${activity.sportType.displayName},'
        '$distance,'
        '$time,'
        '$elevation,'
        '$avgHr,'
        '$pace'
      );
    }
    
    final directory = await getApplicationDocumentsDirectory();
    final exportDir = Directory(p.join(directory.path, 'exports'));
    if (!await exportDir.exists()) {
      await exportDir.create(recursive: true);
    }
    
    final fileName = 'activities_summary_${DateFormat('yyyyMMdd_HHmmss').format(DateTime.now())}.csv';
    final file = File(p.join(exportDir.path, fileName));
    await file.writeAsString(csv.toString());
    
    return file;
  }
  
  String _escapeXml(String text) {
    return text
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&apos;');
  }
  
  String _escapeCsv(String text) {
    if (text.contains(',') || text.contains('"') || text.contains('\n')) {
      return '"${text.replaceAll('"', '""')}"';
    }
    return text;
  }
  
  String _formatIso8601(DateTime date) {
    return DateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date.toUtc());
  }
  
  String _mapSportTypeToTcx(SportType sportType) {
    switch (sportType) {
      case SportType.run:
      case SportType.trailRun:
        return 'Running';
      case SportType.ride:
      case SportType.virtualRide:
      case SportType.indoorRide:
        return 'Biking';
      case SportType.walk:
        return 'Walking';
      case SportType.hike:
        return 'Hiking';
      case SportType.swim:
        return 'Other';
      default:
        return 'Other';
    }
  }
}

