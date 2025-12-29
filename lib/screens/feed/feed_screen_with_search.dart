import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import '../../providers/activities_provider.dart';
import '../../theme/app_theme.dart';
import '../../database/database.dart';
import '../../models/activity.dart' show SportType;
import '../activity_detail/activity_detail_screen.dart';

/// Provider for filtered activities
final filteredActivitiesProvider = Provider.family<List<Activity>, String>((ref, query) {
  final activitiesAsync = ref.watch(activitiesProvider);
  return activitiesAsync.when(
    data: (activities) {
      if (query.isEmpty) return activities;
      final lowerQuery = query.toLowerCase();
      return activities.where((activity) {
        return activity.name.toLowerCase().contains(lowerQuery) ||
               activity.sportType.displayName.toLowerCase().contains(lowerQuery);
      }).toList();
    },
    loading: () => [],
    error: (_, __) => [],
  );
});

/// Provider for sport type filter
final sportTypeFilterProvider = StateProvider<SportType?>((ref) => null);

class FeedScreen extends ConsumerStatefulWidget {
  const FeedScreen({super.key});

  @override
  ConsumerState<FeedScreen> createState() => _FeedScreenState();
}

class _FeedScreenState extends ConsumerState<FeedScreen> {
  final TextEditingController _searchController = TextEditingController();
  String _searchQuery = '';

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final sportFilter = ref.watch(sportTypeFilterProvider);
    final activitiesAsync = ref.watch(activitiesProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Activity Feed'),
        actions: [
          IconButton(
            icon: const Icon(Icons.search),
            onPressed: () {
              showSearch(
                context: context,
                delegate: ActivitySearchDelegate(ref),
              );
            },
          ),
          PopupMenuButton<SportType?>(
            icon: const Icon(Icons.filter_list),
            onSelected: (value) {
              ref.read(sportTypeFilterProvider.notifier).state = value;
            },
            itemBuilder: (context) => [
              const PopupMenuItem(
                value: null,
                child: Text('All Sports'),
              ),
              ...SportType.values.map((sport) => PopupMenuItem(
                value: sport,
                child: Text(sport.displayName),
              )),
            ],
          ),
        ],
      ),
      body: activitiesAsync.when(
        data: (allActivities) {
          // Apply filters
          var filtered = allActivities;
          
          if (sportFilter != null) {
            filtered = filtered.where((a) => a.sportType == sportFilter).toList();
          }
          
          if (filtered.isEmpty) {
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
                    sportFilter != null 
                        ? 'No ${sportFilter.displayName} activities'
                        : 'No activities yet',
                    style: Theme.of(context).textTheme.titleLarge,
                  ),
                ],
              ),
            );
          }
          
          return ListView.builder(
            padding: const EdgeInsets.all(16),
            itemCount: filtered.length,
            itemBuilder: (context, index) {
              final activity = filtered[index];
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
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => ActivityDetailScreen(
                          activityId: activity.id,
                        ),
                      ),
                    );
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

/// Search delegate for activities
class ActivitySearchDelegate extends SearchDelegate<String> {
  final WidgetRef ref;

  ActivitySearchDelegate(this.ref);

  @override
  List<Widget> buildActions(BuildContext context) {
    return [
      IconButton(
        icon: const Icon(Icons.clear),
        onPressed: () {
          query = '';
        },
      ),
    ];
  }

  @override
  Widget buildLeading(BuildContext context) {
    return IconButton(
      icon: const Icon(Icons.arrow_back),
      onPressed: () {
        close(context, '');
      },
    );
  }

  @override
  Widget buildResults(BuildContext context) {
    return _buildSearchResults();
  }

  @override
  Widget buildSuggestions(BuildContext context) {
    return _buildSearchResults();
  }

  Widget _buildSearchResults() {
    final activitiesAsync = ref.watch(activitiesProvider);
    
    return activitiesAsync.when(
      data: (activities) {
        final filtered = query.isEmpty
            ? activities
            : activities.where((activity) {
                final lowerQuery = query.toLowerCase();
                return activity.name.toLowerCase().contains(lowerQuery) ||
                       activity.sportType.displayName.toLowerCase().contains(lowerQuery);
              }).toList();

        if (filtered.isEmpty) {
          return const Center(
            child: Text('No activities found'),
          );
        }

        return ListView.builder(
          itemCount: filtered.length,
          itemBuilder: (context, index) {
            final activity = filtered[index];
            return ListTile(
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
                '${_formatDistance(activity.distanceMeters)}',
              ),
              onTap: () {
                close(context, '');
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => ActivityDetailScreen(
                      activityId: activity.id,
                    ),
                  ),
                );
              },
            );
          },
        );
      },
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (error, stack) => Center(child: Text('Error: $error')),
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
}

