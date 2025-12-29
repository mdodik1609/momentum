import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:file_picker/file_picker.dart';
import 'package:intl/intl.dart';
import '../../theme/app_theme.dart';
import '../../providers/file_import_provider.dart';
import '../../providers/analytics_provider.dart';
import '../../widgets/activity_chart.dart';
import '../strava_auth/strava_auth_screen.dart';

class HomeScreen extends ConsumerWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final weeklyStats = ref.watch(weeklyStatsProvider);
    final monthlyStats = ref.watch(monthlyStatsProvider);
    final activityTrend = ref.watch(activityTrendProvider);
    
    return Scaffold(
      appBar: AppBar(
        title: const Text('Momentum'),
        backgroundColor: AppColors.stravaWhite,
        foregroundColor: AppColors.stravaBlack,
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {
              // Navigate to settings
            },
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Weekly Stats Cards
            weeklyStats.when(
              data: (stats) => Column(
                children: [
                  Row(
                    children: [
                      Expanded(
                        child: _StatCard(
                          title: 'This Week',
                          value: _formatDistance(stats.totalDistance),
                          subtitle: 'Distance',
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: _StatCard(
                          title: _formatTime(stats.totalTime),
                          value: 'Time',
                          subtitle: 'Moving',
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 16),
                  Row(
                    children: [
                      Expanded(
                        child: _StatCard(
                          title: _formatElevation(stats.totalElevation),
                          value: 'Elevation',
                          subtitle: 'Gain',
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: _StatCard(
                          title: '${stats.activityCount}',
                          value: 'Activities',
                          subtitle: 'This Week',
                        ),
                      ),
                    ],
                  ),
                ],
              ),
              loading: () => const Center(child: CircularProgressIndicator()),
              error: (_, __) => const SizedBox.shrink(),
            ),
            const SizedBox(height: 24),
            
            // Monthly Stats
            monthlyStats.when(
              data: (stats) => Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'This Month',
                    style: Theme.of(context).textTheme.titleLarge?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 12),
                  Row(
                    children: [
                      Expanded(
                        child: _StatCard(
                          title: _formatDistance(stats.totalDistance),
                          value: 'Distance',
                          subtitle: 'Total',
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: _StatCard(
                          title: '${stats.activityCount}',
                          value: 'Activities',
                          subtitle: 'Count',
                        ),
                      ),
                    ],
                  ),
                ],
              ),
              loading: () => const SizedBox.shrink(),
              error: (_, __) => const SizedBox.shrink(),
            ),
            const SizedBox(height: 24),
            
            // Activity Trend Chart
            activityTrend.when(
              data: (trends) {
                if (trends.isEmpty) return const SizedBox.shrink();
                return Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Activity Trend (Last 30 Days)',
                      style: Theme.of(context).textTheme.titleLarge?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 16),
                    TimeSeriesChart(
                      title: 'Daily Distance',
                      dates: trends.map((t) => t.date).toList(),
                      values: trends.map((t) => t.distance / 1000).toList(), // Convert to km
                      yAxisLabel: 'Distance (km)',
                      yAxisFormatter: (value) => '${value.toStringAsFixed(1)}',
                    ),
                  ],
                );
              },
              loading: () => const SizedBox.shrink(),
              error: (_, __) => const SizedBox.shrink(),
            ),
            const SizedBox(height: 24),
            
            // Quick Actions
            Text(
              'Quick Actions',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 12),
            // File import button
            ElevatedButton.icon(
              onPressed: () async {
                try {
                  final filePicker = FilePicker.platform;
                  final result = await filePicker.pickFiles(
                    type: FileType.custom,
                    allowedExtensions: ['gpx', 'tcx', 'fit'],
                    allowMultiple: true,
                  );
                  
                  if (result == null || result.files.isEmpty) return;
                  
                  if (context.mounted) {
                    showDialog(
                      context: context,
                      barrierDismissible: false,
                      builder: (context) => const Center(
                        child: CircularProgressIndicator(
                          color: AppColors.stravaOrange,
                        ),
                      ),
                    );
                  }
                  
                  final service = ref.read(fileImportServiceProvider);
                  int importedCount = 0;
                  
                  for (final file in result.files) {
                    if (file.path != null) {
                      final fileObj = File(file.path!);
                      try {
                        if (file.path!.endsWith('.gpx')) {
                          await service.importGpxFile(fileObj);
                          importedCount++;
                        } else if (file.path!.endsWith('.tcx')) {
                          await service.importTcxFile(fileObj);
                          importedCount++;
                        } else if (file.path!.endsWith('.fit')) {
                          await service.importFitFile(fileObj);
                          importedCount++;
                        }
                      } catch (e) {
                        print('Error importing ${file.path}: $e');
                      }
                    }
                  }
                  
                  if (context.mounted) {
                    Navigator.pop(context);
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(
                        content: Text('Imported $importedCount activities'),
                        backgroundColor: AppColors.stravaGreen,
                        behavior: SnackBarBehavior.floating,
                      ),
                    );
                  }
                } catch (e) {
                  if (context.mounted) {
                    Navigator.pop(context);
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(
                        content: Text('Error: $e'),
                        backgroundColor: AppColors.stravaRed,
                        behavior: SnackBarBehavior.floating,
                      ),
                    );
                  }
                }
              },
              icon: const Icon(Icons.upload_file),
              label: const Text('Import Files'),
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.stravaOrange,
                foregroundColor: AppColors.stravaWhite,
                minimumSize: const Size(double.infinity, 48),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(8),
                ),
              ),
            ),
            const SizedBox(height: 12),
            // Strava sync button
            OutlinedButton.icon(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const StravaAuthScreen(),
                  ),
                );
              },
              icon: const Icon(Icons.sync),
              label: const Text('Sync with Strava'),
              style: OutlinedButton.styleFrom(
                foregroundColor: AppColors.stravaOrange,
                side: const BorderSide(color: AppColors.stravaOrange),
                minimumSize: const Size(double.infinity, 48),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(8),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
  
  String _formatDistance(double meters) {
    if (meters < 1000) return '${meters.toStringAsFixed(0)} m';
    return '${(meters / 1000).toStringAsFixed(1)} km';
  }
  
  String _formatTime(int seconds) {
    final hours = seconds ~/ 3600;
    final minutes = (seconds % 3600) ~/ 60;
    if (hours > 0) {
      return '${hours}h ${minutes}m';
    }
    return '${minutes}m';
  }
  
  String _formatElevation(double meters) {
    return '${meters.toStringAsFixed(0)} m';
  }
}

class _StatCard extends StatelessWidget {
  final String title;
  final String value;
  final String subtitle;

  const _StatCard({
    required this.title,
    required this.value,
    required this.subtitle,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              title,
              style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                fontWeight: FontWeight.bold,
                color: AppColors.stravaOrange,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              value,
              style: Theme.of(
                context,
              ).textTheme.bodyMedium?.copyWith(color: AppColors.stravaGray),
            ),
            Text(
              subtitle,
              style: Theme.of(
                context,
              ).textTheme.bodySmall?.copyWith(color: AppColors.stravaGray),
            ),
          ],
        ),
      ),
    );
  }
}
