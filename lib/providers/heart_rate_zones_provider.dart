import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../database/database.dart';
import 'database_provider.dart';

/// Heart rate zone distribution provider
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

