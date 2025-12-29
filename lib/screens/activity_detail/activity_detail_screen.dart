import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:intl/intl.dart';
import 'package:drift/drift.dart' as drift;
import '../../database/database.dart';
import '../../providers/database_provider.dart';
import '../../providers/export_provider.dart';
import '../../widgets/activity_chart.dart';
import '../../widgets/activity_map.dart';
import '../../theme/app_theme.dart';
import '../../models/activity.dart' show SportType;

class ActivityDetailScreen extends ConsumerStatefulWidget {
  final String activityId;
  
  const ActivityDetailScreen({
    super.key,
    required this.activityId,
  });
  
  @override
  ConsumerState<ActivityDetailScreen> createState() => _ActivityDetailScreenState();
}

class _ActivityDetailScreenState extends ConsumerState<ActivityDetailScreen> {
  Activity? _activity;
  List<ActivityStream> _streams = [];
  bool _isLoading = true;
  
  @override
  void initState() {
    super.initState();
    _loadActivity();
  }
  
  Future<void> _loadActivity() async {
    final db = ref.read(databaseProvider);
    
    final activity = await (db.select(db.activities)
          ..where((a) => a.id.equals(widget.activityId)))
        .getSingleOrNull();
    
    if (activity != null) {
      final streams = await (db.select(db.activityStreams)
            ..where((s) => s.activityId.equals(widget.activityId))
            ..orderBy([(s) => drift.OrderingTerm.asc(s.timeOffsetSeconds)]))
          .get();
      
      setState(() {
        _activity = activity;
        _streams = streams;
        _isLoading = false;
      });
    } else {
      setState(() {
        _isLoading = false;
      });
    }
  }
  
  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return Scaffold(
        appBar: AppBar(title: const Text('Activity Details')),
        body: const Center(child: CircularProgressIndicator()),
      );
    }
    
    if (_activity == null) {
      return Scaffold(
        appBar: AppBar(title: const Text('Activity Details')),
        body: const Center(child: Text('Activity not found')),
      );
    }
    
    return Scaffold(
      appBar: AppBar(
        title: Text(_activity!.name),
        actions: [
          PopupMenuButton<String>(
            onSelected: (value) async {
              final exportService = ref.read(exportServiceProvider);
              try {
                File? exportedFile;
                if (value == 'gpx') {
                  exportedFile = await exportService.exportToGpx(widget.activityId);
                } else if (value == 'tcx') {
                  exportedFile = await exportService.exportToTcx(widget.activityId);
                }
                
                if (exportedFile != null && context.mounted) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text('Exported to ${exportedFile.path}'),
                      backgroundColor: AppColors.stravaGreen,
                      behavior: SnackBarBehavior.floating,
                    ),
                  );
                }
              } catch (e) {
                if (context.mounted) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text('Export failed: $e'),
                      backgroundColor: AppColors.stravaRed,
                      behavior: SnackBarBehavior.floating,
                    ),
                  );
                }
              }
            },
            itemBuilder: (context) => [
              const PopupMenuItem(
                value: 'gpx',
                child: Row(
                  children: [
                    Icon(Icons.file_download, size: 20),
                    SizedBox(width: 8),
                    Text('Export as GPX'),
                  ],
                ),
              ),
              const PopupMenuItem(
                value: 'tcx',
                child: Row(
                  children: [
                    Icon(Icons.file_download, size: 20),
                    SizedBox(width: 8),
                    Text('Export as TCX'),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Activity Header
            _buildActivityHeader(),
            const SizedBox(height: 24),
            
            // Stats Grid
            _buildStatsGrid(),
            const SizedBox(height: 24),
            
            // Map
            if (_hasGpsData()) ...[
              Text(
                'Route Map',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 16),
              ActivityMap(streams: _streams),
              const SizedBox(height: 24),
            ],
            
            // Map
            if (_hasGPSData())
              _buildMap(),
            const SizedBox(height: 24),
            
            // Charts
            if (_streams.isNotEmpty) ...[
              Text(
                'Charts',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 16),
              
              // Elevation Chart
              if (_hasElevationData())
                _buildElevationChart(),
              const SizedBox(height: 16),
              
              // Heart Rate Chart
              if (_hasHeartRateData())
                _buildHeartRateChart(),
              const SizedBox(height: 16),
              
              // Pace/Speed Chart
              if (_hasSpeedData())
                _buildPaceChart(),
              const SizedBox(height: 16),
              
              // Power Chart
              if (_hasPowerData())
                _buildPowerChart(),
            ],
          ],
        ),
      ),
    );
  }
  
  Widget _buildActivityHeader() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                CircleAvatar(
                  backgroundColor: AppColors.stravaOrange,
                  child: Icon(
                    _getSportIcon(_activity!.sportType),
                    color: Colors.white,
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        _activity!.name,
                        style: Theme.of(context).textTheme.titleLarge?.copyWith(
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      Text(
                        DateFormat('MMM d, y • h:mm a').format(_activity!.startDate),
                        style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          color: AppColors.stravaGray,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
  
  Widget _buildStatsGrid() {
    return GridView.count(
      crossAxisCount: 2,
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      mainAxisSpacing: 12,
      crossAxisSpacing: 12,
      childAspectRatio: 1.5,
      children: [
        _StatCard(
          title: 'Distance',
          value: _formatDistance(_activity!.distanceMeters),
          icon: Icons.straighten,
        ),
        _StatCard(
          title: 'Time',
          value: _formatTime(_activity!.movingTimeSeconds),
          icon: Icons.timer,
        ),
        _StatCard(
          title: 'Elevation',
          value: _formatElevation(_activity!.elevationGainMeters),
          icon: Icons.terrain,
        ),
        _StatCard(
          title: 'Avg Pace',
          value: _formatPace(_activity!.averageSpeedMps),
          icon: Icons.speed,
        ),
        if (_activity!.averageHeartRateBpm != null)
          _StatCard(
            title: 'Avg HR',
            value: '${_activity!.averageHeartRateBpm} bpm',
            icon: Icons.favorite,
          ),
        if (_activity!.averagePowerWatts != null)
          _StatCard(
            title: 'Avg Power',
            value: '${_activity!.averagePowerWatts} W',
            icon: Icons.bolt,
          ),
      ],
    );
  }
  
  Widget _buildElevationChart() {
    final elevationData = _streams
        .where((s) => s.altitudeMeters != null)
        .map((s) => s.altitudeMeters!)
        .toList();
    
    final distanceData = _streams
        .where((s) => s.distanceMeters != null)
        .map((s) => s.distanceMeters! / 1000) // Convert to km
        .toList();
    
    if (elevationData.isEmpty) return const SizedBox.shrink();
    
    return ActivityChart(
      title: 'Elevation Profile',
      data: elevationData,
      xAxisLabels: distanceData.map((d) => '${d.toStringAsFixed(1)} km').toList(),
      yAxisLabel: 'Elevation (m)',
      lineColor: AppColors.stravaGreen,
      yAxisFormatter: (value) => '${value.toInt()}m',
    );
  }
  
  Widget _buildHeartRateChart() {
    final hrData = _streams
        .where((s) => s.heartRateBpm != null)
        .map((s) => s.heartRateBpm!.toDouble())
        .toList();
    
    final distanceData = _streams
        .where((s) => s.distanceMeters != null && s.heartRateBpm != null)
        .map((s) => s.distanceMeters! / 1000)
        .toList();
    
    if (hrData.isEmpty) return const SizedBox.shrink();
    
    return ActivityChart(
      title: 'Heart Rate',
      data: hrData,
      xAxisLabels: distanceData.map((d) => '${d.toStringAsFixed(1)} km').toList(),
      yAxisLabel: 'Heart Rate (bpm)',
      lineColor: AppColors.stravaRed,
      yAxisFormatter: (value) => '${value.toInt()}',
    );
  }
  
  Widget _buildPaceChart() {
    final speedData = _streams
        .where((s) => s.speedMps != null && s.speedMps! > 0)
        .map((s) => 1000 / s.speedMps!) // Convert m/s to seconds per km
        .toList();
    
    final distanceData = _streams
        .where((s) => s.speedMps != null && s.speedMps! > 0)
        .map((s) => s.distanceMeters! / 1000)
        .toList();
    
    if (speedData.isEmpty) return const SizedBox.shrink();
    
    return ActivityChart(
      title: 'Pace',
      data: speedData,
      xAxisLabels: distanceData.map((d) => '${d.toStringAsFixed(1)} km').toList(),
      yAxisLabel: 'Pace (min/km)',
      lineColor: AppColors.stravaOrange,
      yAxisFormatter: (value) {
        final minutes = (value ~/ 60);
        final seconds = (value % 60).toInt();
        return '$minutes:${seconds.toString().padLeft(2, '0')}';
      },
    );
  }
  
  Widget _buildPowerChart() {
    final powerData = _streams
        .where((s) => s.powerWatts != null)
        .map((s) => s.powerWatts!.toDouble())
        .toList();
    
    final distanceData = _streams
        .where((s) => s.powerWatts != null)
        .map((s) => s.distanceMeters! / 1000)
        .toList();
    
    if (powerData.isEmpty) return const SizedBox.shrink();
    
    return ActivityChart(
      title: 'Power',
      data: powerData,
      xAxisLabels: distanceData.map((d) => '${d.toStringAsFixed(1)} km').toList(),
      yAxisLabel: 'Power (W)',
      lineColor: AppColors.stravaOrange,
      yAxisFormatter: (value) => '${value.toInt()}',
    );
  }
  
  bool _hasElevationData() {
    return _streams.any((s) => s.altitudeMeters != null);
  }
  
  bool _hasHeartRateData() {
    return _streams.any((s) => s.heartRateBpm != null);
  }
  
  bool _hasSpeedData() {
    return _streams.any((s) => s.speedMps != null && s.speedMps! > 0);
  }
  
  bool _hasPowerData() {
    return _streams.any((s) => s.powerWatts != null);
  }
  
  bool _hasGPSData() {
    return _streams.any((s) => s.latitude != null && s.longitude != null);
  }
  
  Widget _buildMap() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Route Map',
          style: Theme.of(context).textTheme.titleLarge?.copyWith(
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 16),
        ActivityMap(streams: _streams),
      ],
    );
  }
  
  bool _hasGpsData() {
    return _streams.any((s) => s.latitude != null && s.longitude != null);
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
    if (hours > 0) {
      return '${hours}h ${minutes}m';
    }
    return '${minutes}m';
  }
  
  String _formatElevation(double? meters) {
    if (meters == null || meters == 0) return '0 m';
    return '${meters.toStringAsFixed(0)} m';
  }
  
  String _formatPace(double? mps) {
    if (mps == null || mps == 0) return '-';
    final secondsPerKm = 1000 / mps;
    final minutes = (secondsPerKm ~/ 60);
    final seconds = (secondsPerKm % 60).toInt();
    return '$minutes:${seconds.toString().padLeft(2, '0')}/km';
  }
}

class _StatCard extends StatelessWidget {
  final String title;
  final String value;
  final IconData icon;
  
  const _StatCard({
    required this.title,
    required this.value,
    required this.icon,
  });
  
  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(icon, color: AppColors.stravaOrange, size: 32),
            const SizedBox(height: 8),
            Text(
              value,
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              title,
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                color: AppColors.stravaGray,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

