import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:drift/drift.dart' as drift;
import '../database/database.dart';
import 'database_provider.dart';

/// Weekly statistics provider
final weeklyStatsProvider = FutureProvider<ActivityStats>((ref) async {
  final db = ref.watch(databaseProvider);
  final now = DateTime.now();
  final weekStart = DateTime(
    now.year,
    now.month,
    now.day,
  ).subtract(Duration(days: now.weekday - 1));
  
  final activities = await (db.select(db.activities)
        ..where((a) => a.startDate.isBiggerOrEqualValue(weekStart))
        ..orderBy([(a) => drift.OrderingTerm.desc(a.startDate)]))
      .get();
  
  return _calculateStats(activities);
});

/// Monthly statistics provider
final monthlyStatsProvider = FutureProvider<ActivityStats>((ref) async {
  final db = ref.watch(databaseProvider);
  final now = DateTime.now();
  final monthStart = DateTime(now.year, now.month, 1);
  
  final activities = await (db.select(db.activities)
        ..where((a) => a.startDate.isBiggerOrEqualValue(monthStart))
        ..orderBy([(a) => drift.OrderingTerm.desc(a.startDate)]))
      .get();
  
  return _calculateStats(activities);
});

/// Yearly statistics provider
final yearlyStatsProvider = FutureProvider<ActivityStats>((ref) async {
  final db = ref.watch(databaseProvider);
  final now = DateTime.now();
  final yearStart = DateTime(now.year, 1, 1);
  
  final activities = await (db.select(db.activities)
        ..where((a) => a.startDate.isBiggerOrEqualValue(yearStart))
        ..orderBy([(a) => drift.OrderingTerm.desc(a.startDate)]))
      .get();
  
  return _calculateStats(activities);
});

/// Activity trend data for charts
final activityTrendProvider = FutureProvider<List<ActivityTrendPoint>>((ref) async {
  final db = ref.watch(databaseProvider);
  
  // Get activities from last 30 days
  final thirtyDaysAgo = DateTime.now().subtract(const Duration(days: 30));
  final activities = await (db.select(db.activities)
        ..where((a) => a.startDate.isBiggerOrEqualValue(thirtyDaysAgo))
        ..orderBy([(a) => drift.OrderingTerm.asc(a.startDate)]))
      .get();
  
  // Group by date
  final Map<DateTime, List<Activity>> activitiesByDate = {};
  for (final activity in activities) {
    final date = DateTime(
      activity.startDate.year,
      activity.startDate.month,
      activity.startDate.day,
    );
    activitiesByDate.putIfAbsent(date, () => []).add(activity);
  }
  
  return activitiesByDate.entries.map((entry) {
    final totalDistance = entry.value
        .fold<double>(0, (sum, a) => sum + (a.distanceMeters ?? 0));
    final totalTime = entry.value
        .fold<int>(0, (sum, a) => sum + a.movingTimeSeconds);
    final totalElevation = entry.value
        .fold<double>(0, (sum, a) => sum + (a.elevationGainMeters ?? 0));
    
    return ActivityTrendPoint(
      date: entry.key,
      distance: totalDistance,
      time: totalTime,
      elevation: totalElevation,
      activityCount: entry.value.length,
    );
  }).toList()
    ..sort((a, b) => a.date.compareTo(b.date));
});

/// Heart rate zone distribution
final heartRateZonesProvider = FutureProvider<Map<String, int>>((ref) async {
  final db = ref.watch(databaseProvider);
  
  final streams = await (db.select(db.activityStreams)
        ..where((s) => s.heartRateBpm.isNotNull()))
      .get();
  
  final zones = <String, int>{
    'Zone 1 (50-60%)': 0,
    'Zone 2 (60-70%)': 0,
    'Zone 3 (70-80%)': 0,
    'Zone 4 (80-90%)': 0,
    'Zone 5 (90-100%)': 0,
  };
  
  // Assuming max HR of 200 (should be configurable)
  const maxHR = 200;
  
  for (final stream in streams) {
    if (stream.heartRateBpm == null) continue;
    final hr = stream.heartRateBpm!;
    final percentage = (hr / maxHR) * 100;
    
    if (percentage < 60) {
      zones['Zone 1 (50-60%)'] = zones['Zone 1 (50-60%)']! + 1;
    } else if (percentage < 70) {
      zones['Zone 2 (60-70%)'] = zones['Zone 2 (60-70%)']! + 1;
    } else if (percentage < 80) {
      zones['Zone 3 (70-80%)'] = zones['Zone 3 (70-80%)']! + 1;
    } else if (percentage < 90) {
      zones['Zone 4 (80-90%)'] = zones['Zone 4 (80-90%)']! + 1;
    } else {
      zones['Zone 5 (90-100%)'] = zones['Zone 5 (90-100%)']! + 1;
    }
  }
  
  return zones;
});

ActivityStats _calculateStats(List<Activity> activities) {
  if (activities.isEmpty) {
    return ActivityStats(
      totalDistance: 0,
      totalTime: 0,
      totalElevation: 0,
      activityCount: 0,
      averagePace: 0,
      averageHeartRate: null,
    );
  }
  
  final totalDistance = activities
      .fold<double>(0, (sum, a) => sum + (a.distanceMeters ?? 0));
  final totalTime = activities
      .fold<int>(0, (sum, a) => sum + a.movingTimeSeconds);
  final totalElevation = activities
      .fold<double>(0, (sum, a) => sum + (a.elevationGainMeters ?? 0));
  
  final activitiesWithPace = activities
      .where((a) => a.averageSpeedMps != null && a.averageSpeedMps! > 0)
      .toList();
  final averagePace = activitiesWithPace.isNotEmpty
      ? activitiesWithPace
          .map((a) => 1000 / a.averageSpeedMps!)
          .reduce((a, b) => a + b) /
          activitiesWithPace.length
      : 0.0;
  
  final activitiesWithHR = activities
      .where((a) => a.averageHeartRateBpm != null)
      .toList();
  final averageHeartRate = activitiesWithHR.isNotEmpty
      ? (activitiesWithHR
              .map((a) => a.averageHeartRateBpm!)
              .reduce((a, b) => a + b) /
          activitiesWithHR.length)
          .round()
      : null;
  
  return ActivityStats(
    totalDistance: totalDistance,
    totalTime: totalTime,
    totalElevation: totalElevation,
    activityCount: activities.length,
    averagePace: averagePace,
    averageHeartRate: averageHeartRate,
  );
}

class ActivityStats {
  final double totalDistance;
  final int totalTime;
  final double totalElevation;
  final int activityCount;
  final double averagePace;
  final int? averageHeartRate;
  
  ActivityStats({
    required this.totalDistance,
    required this.totalTime,
    required this.totalElevation,
    required this.activityCount,
    required this.averagePace,
    this.averageHeartRate,
  });
}

class ActivityTrendPoint {
  final DateTime date;
  final double distance;
  final int time;
  final double elevation;
  final int activityCount;
  
  ActivityTrendPoint({
    required this.date,
    required this.distance,
    required this.time,
    required this.elevation,
    required this.activityCount,
  });
}

