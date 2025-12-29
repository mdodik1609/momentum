import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:drift/drift.dart' as drift;
import '../database/database.dart';
import 'database_provider.dart';

/// Personal records provider
final personalRecordsProvider = FutureProvider<List<PersonalRecord>>((ref) async {
  final db = ref.watch(databaseProvider);
  
  final activities = await (db.select(db.activities)
        ..where((a) => a.distanceMeters.isNotNull() & a.distanceMeters.isBiggerThanValue(0))
        ..orderBy([(a) => drift.OrderingTerm.desc(a.startDate)]))
      .get();
  
  final records = <String, PersonalRecord>{};
  
  // Standard distances in meters
  final standardDistances = {
    '1K': 1000,
    '5K': 5000,
    '10K': 10000,
    'Half Marathon': 21097.5,
    'Marathon': 42195,
  };
  
  for (final activity in activities) {
    if (activity.distanceMeters == null) continue;
    
    final distance = activity.distanceMeters!;
    final time = activity.movingTimeSeconds;
    final pace = time / (distance / 1000); // seconds per km
    
    // Check standard distances (within 5% tolerance)
    for (final entry in standardDistances.entries) {
      final target = entry.value;
      if (distance >= target * 0.95 && distance <= target * 1.05) {
        final key = entry.key;
        if (!records.containsKey(key) || records[key]!.pace > pace) {
          records[key] = PersonalRecord(
            distance: distance,
            time: time,
            pace: pace,
            activityId: activity.id,
            activityName: activity.name,
            date: activity.startDate,
          );
        }
      }
    }
    
    // Check for best time at this exact distance (rounded to nearest 100m)
    final roundedDistance = (distance / 100).round() * 100;
    final distanceKey = '${(roundedDistance / 1000).toStringAsFixed(1)}K';
    
    if (!records.containsKey(distanceKey) || records[distanceKey]!.pace > pace) {
      records[distanceKey] = PersonalRecord(
        distance: distance,
        time: time,
        pace: pace,
        activityId: activity.id,
        activityName: activity.name,
        date: activity.startDate,
      );
    }
  }
  
  return records.values.toList()
    ..sort((a, b) => a.distance.compareTo(b.distance));
});

class PersonalRecord {
  final double distance;
  final int time;
  final double pace; // seconds per km
  final String activityId;
  final String activityName;
  final DateTime date;
  
  PersonalRecord({
    required this.distance,
    required this.time,
    required this.pace,
    required this.activityId,
    required this.activityName,
    required this.date,
  });
  
  String get distanceLabel {
    if (distance < 1000) return '${distance.toInt()}m';
    return '${(distance / 1000).toStringAsFixed(1)}K';
  }
  
  String get timeFormatted {
    final hours = time ~/ 3600;
    final minutes = (time % 3600) ~/ 60;
    final seconds = time % 60;
    
    if (hours > 0) {
      return '${hours}:${minutes.toString().padLeft(2, '0')}:${seconds.toString().padLeft(2, '0')}';
    }
    return '${minutes}:${seconds.toString().padLeft(2, '0')}';
  }
  
  String get paceFormatted {
    final minutes = (pace ~/ 60);
    final seconds = (pace % 60).toInt();
    return '$minutes:${seconds.toString().padLeft(2, '0')}/km';
  }
}
