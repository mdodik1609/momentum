import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../providers/analytics_provider.dart';
import '../../providers/personal_records_provider.dart';
import '../../widgets/heart_rate_zones_chart.dart';
import '../../widgets/activity_chart.dart';
import '../../theme/app_theme.dart';
import 'package:intl/intl.dart';

class ExploreScreen extends ConsumerWidget {
  const ExploreScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final hrZones = ref.watch(heartRateZonesProvider);
    final personalRecords = ref.watch(personalRecordsProvider);
    final activityTrend = ref.watch(activityTrendProvider);
    
    return Scaffold(
      appBar: AppBar(
        title: const Text('Analytics'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Heart Rate Zones
            hrZones.when(
              data: (zones) => HeartRateZonesChart(zones: zones),
              loading: () => const Card(
                child: Padding(
                  padding: EdgeInsets.all(24.0),
                  child: Center(child: CircularProgressIndicator()),
                ),
              ),
              error: (_, __) => const SizedBox.shrink(),
            ),
            const SizedBox(height: 24),
            
            // Activity Trend
            activityTrend.when(
              data: (trends) {
                if (trends.isEmpty) return const SizedBox.shrink();
                return TimeSeriesChart(
                  title: 'Activity Trend (Last 30 Days)',
                  dates: trends.map((t) => t.date).toList(),
                  values: trends.map((t) => t.distance / 1000).toList(),
                  yAxisLabel: 'Distance (km)',
                  yAxisFormatter: (value) => '${value.toStringAsFixed(1)}',
                );
              },
              loading: () => const SizedBox.shrink(),
              error: (_, __) => const SizedBox.shrink(),
            ),
            const SizedBox(height: 24),
            
            // Personal Records
            personalRecords.when(
              data: (records) {
                if (records.isEmpty) {
                  return Card(
                    child: Padding(
                      padding: const EdgeInsets.all(24.0),
                      child: Center(
                        child: Text(
                          'No personal records yet',
                          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                            color: AppColors.stravaGray,
                          ),
                        ),
                      ),
                    ),
                  );
                }
                
                return Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Personal Records',
                      style: Theme.of(context).textTheme.titleLarge?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 16),
                    ...records.map((record) => Card(
                      margin: const EdgeInsets.only(bottom: 12),
                      child: ListTile(
                        leading: CircleAvatar(
                          backgroundColor: AppColors.stravaOrange,
                          child: const Icon(Icons.emoji_events, color: Colors.white),
                        ),
                        title: Text(
                          record.distanceLabel,
                          style: const TextStyle(fontWeight: FontWeight.bold),
                        ),
                        subtitle: Text(record.activityName),
                        trailing: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          crossAxisAlignment: CrossAxisAlignment.end,
                          children: [
                            Text(
                              record.timeFormatted,
                              style: const TextStyle(
                                fontWeight: FontWeight.bold,
                                color: AppColors.stravaOrange,
                              ),
                            ),
                            Text(
                              record.paceFormatted,
                              style: const TextStyle(
                                fontSize: 12,
                                color: AppColors.stravaGray,
                              ),
                            ),
                          ],
                        ),
                      ),
                    )),
                  ],
                );
              },
              loading: () => const Card(
                child: Padding(
                  padding: EdgeInsets.all(24.0),
                  child: Center(child: CircularProgressIndicator()),
                ),
              ),
              error: (_, __) => const SizedBox.shrink(),
            ),
          ],
        ),
      ),
    );
  }
}
