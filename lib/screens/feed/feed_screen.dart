import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import '../../providers/activities_provider.dart';
import '../../theme/app_theme.dart';
import '../../models/activity.dart';

class FeedScreen extends ConsumerWidget {
  const FeedScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final activitiesAsync = ref.watch(activitiesProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Activity Feed'),
        actions: [
          IconButton(
            icon: const Icon(Icons.search),
            onPressed: () {
              // Search
            },
          ),
          IconButton(
            icon: const Icon(Icons.filter_list),
            onPressed: () {
              // Filter
            },
          ),
        ],
      ),
      body: activitiesAsync.when(
        data: (activities) {
          if (activities.isEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(
                    Icons.directions_run,
                    size: 64,
                    color: AppColors.stravaGray,
                  ),
                  const SizedBox(height: 16),
                  Text(
                    'No activities yet',
                    style: Theme.of(context).textTheme.titleLarge,
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Import activities from D:\\data\\activities',
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      color: AppColors.stravaGray,
                    ),
                  ),
                ],
              ),
            );
          }
          return ListView.builder(
            padding: const EdgeInsets.all(16),
            itemCount: activities.length,
            itemBuilder: (context, index) {
              final activity = activities[index];
              return Card(
                margin: const EdgeInsets.only(bottom: 12),
                child: ListTile(
                  leading: CircleAvatar(
                    backgroundColor: AppColors.stravaOrange,
                    child: Icon(
                      _getSportIcon(activity.sportType),
                      color: Colors.white,
                    ),
                  ),
                  title: Text(activity.name),
                  subtitle: Text(
                    '${DateFormat('MMM d, y').format(activity.startDate)} • '
                    '${_formatDistance(activity.distanceMeters)} • '
                    '${_formatTime(activity.movingTimeSeconds)}',
                  ),
                  trailing: const Icon(Icons.chevron_right),
                  onTap: () {
                    // Navigate to activity detail
                  },
                ),
              );
            },
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (error, stack) => Center(child: Text('Error: $error')),
      ),
    );
  }

  IconData _getSportIcon(SportType sportType) {
    switch (sportType) {
      case SportType.run:
      case SportType.trailRun:
        return Icons.directions_run;
      case SportType.ride:
      case SportType.virtualRide:
      case SportType.indoorRide:
        return Icons.directions_bike;
      case SportType.walk:
        return Icons.directions_walk;
      case SportType.hike:
        return Icons.terrain;
      case SportType.swim:
        return Icons.pool;
      default:
        return Icons.fitness_center;
    }
  }

  String _formatDistance(double? meters) {
    if (meters == null || meters == 0) return '0 km';
    if (meters < 1000) return '${meters.toStringAsFixed(0)} m';
    return '${(meters / 1000).toStringAsFixed(2)} km';
  }

  String _formatTime(int seconds) {
    final hours = seconds ~/ 3600;
    final minutes = (seconds % 3600) ~/ 60;
    final secs = seconds % 60;
    if (hours > 0) {
      return '${hours}h ${minutes}m';
    }
    return '${minutes}m ${secs}s';
  }
}
