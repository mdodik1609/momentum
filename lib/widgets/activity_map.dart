import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:latlong2/latlong.dart';
import '../database/database.dart';
import '../theme/app_theme.dart';

/// Map widget for displaying activity GPS track
class ActivityMap extends StatelessWidget {
  final List<ActivityStream> streams;
  final double? height;
  
  const ActivityMap({
    super.key,
    required this.streams,
    this.height = 300,
  });
  
  @override
  Widget build(BuildContext context) {
    final gpsPoints = streams
        .where((s) => s.latitude != null && s.longitude != null)
        .map((s) => LatLng(s.latitude!, s.longitude!))
        .toList();
    
    if (gpsPoints.isEmpty) {
      return Card(
        child: SizedBox(
          height: height,
          child: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(
                  Icons.map_outlined,
                  size: 48,
                  color: AppColors.stravaGray,
                ),
                const SizedBox(height: 8),
                Text(
                  'No GPS data available',
                  style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: AppColors.stravaGray,
                  ),
                ),
              ],
            ),
          ),
        ),
      );
    }
    
    final bounds = _calculateBounds(gpsPoints);
    final center = bounds.center;
    
    return Card(
      child: SizedBox(
        height: height,
        child: FlutterMap(
          options: MapOptions(
            initialCenter: center,
            initialZoom: _calculateZoom(bounds),
            minZoom: 5,
            maxZoom: 18,
            interactionOptions: const InteractionOptions(
              flags: InteractiveFlag.all & ~InteractiveFlag.rotate,
            ),
          ),
          children: [
            TileLayer(
              urlTemplate: 'https://tile.openstreetmap.org/{z}/{x}/{y}.png',
              userAgentPackageName: 'com.momentum.app',
            ),
            PolylineLayer(
              polylines: [
                Polyline(
                  points: gpsPoints,
                  strokeWidth: 3,
                  color: AppColors.stravaOrange,
                ),
              ],
            ),
            MarkerLayer(
              markers: [
                // Start marker
                Marker(
                  point: gpsPoints.first,
                  width: 20,
                  height: 20,
                  child: Container(
                    decoration: BoxDecoration(
                      color: AppColors.stravaGreen,
                      shape: BoxShape.circle,
                      border: Border.all(color: Colors.white, width: 2),
                    ),
                  ),
                ),
                // End marker
                if (gpsPoints.length > 1)
                  Marker(
                    point: gpsPoints.last,
                    width: 20,
                    height: 20,
                    child: Container(
                      decoration: BoxDecoration(
                        color: AppColors.stravaRed,
                        shape: BoxShape.circle,
                        border: Border.all(color: Colors.white, width: 2),
                      ),
                    ),
                  ),
              ],
            ),
          ],
        ),
      ),
    );
  }
  
  LatLngBounds _calculateBounds(List<LatLng> points) {
    if (points.isEmpty) {
      return LatLngBounds(
        const LatLng(0, 0),
        const LatLng(0, 0),
      );
    }
    
    double minLat = points.first.latitude;
    double maxLat = points.first.latitude;
    double minLon = points.first.longitude;
    double maxLon = points.first.longitude;
    
    for (final point in points) {
      if (point.latitude < minLat) minLat = point.latitude;
      if (point.latitude > maxLat) maxLat = point.latitude;
      if (point.longitude < minLon) minLon = point.longitude;
      if (point.longitude > maxLon) maxLon = point.longitude;
    }
    
    return LatLngBounds(
      LatLng(minLat, minLon),
      LatLng(maxLat, maxLon),
    );
  }
  
  double _calculateZoom(LatLngBounds bounds) {
    final latDiff = bounds.north - bounds.south;
    final lonDiff = bounds.east - bounds.west;
    final maxDiff = latDiff > lonDiff ? latDiff : lonDiff;
    
    if (maxDiff > 1.0) return 8;
    if (maxDiff > 0.5) return 9;
    if (maxDiff > 0.2) return 10;
    if (maxDiff > 0.1) return 11;
    if (maxDiff > 0.05) return 12;
    if (maxDiff > 0.02) return 13;
    if (maxDiff > 0.01) return 14;
    return 15;
  }
}
