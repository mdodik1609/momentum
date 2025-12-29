import 'package:flutter/material.dart';
import 'package:fl_chart/fl_chart.dart';
import '../theme/app_theme.dart';

/// Heart rate zone distribution chart
class HeartRateZonesChart extends StatelessWidget {
  final Map<String, int> zones;
  final int maxHeartRate;
  
  const HeartRateZonesChart({
    super.key,
    required this.zones,
    this.maxHeartRate = 200,
  });
  
  @override
  Widget build(BuildContext context) {
    if (zones.isEmpty || zones.values.every((v) => v == 0)) {
      return Card(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Center(
            child: Text(
              'No heart rate data available',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: AppColors.stravaGray,
              ),
            ),
          ),
        ),
      );
    }
    
    final total = zones.values.fold<int>(0, (sum, value) => sum + value);
    if (total == 0) {
      return Card(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Center(
            child: Text(
              'No heart rate data available',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: AppColors.stravaGray,
              ),
            ),
          ),
        ),
      );
    }
    
    final zoneData = [
      _ZoneData('Zone 1\n(50-60%)', zones['Zone 1 (50-60%)'] ?? 0, const Color(0xFF4CAF50)),
      _ZoneData('Zone 2\n(60-70%)', zones['Zone 2 (60-70%)'] ?? 0, const Color(0xFF8BC34A)),
      _ZoneData('Zone 3\n(70-80%)', zones['Zone 3 (70-80%)'] ?? 0, const Color(0xFFFFC107)),
      _ZoneData('Zone 4\n(80-90%)', zones['Zone 4 (80-90%)'] ?? 0, const Color(0xFFFF9800)),
      _ZoneData('Zone 5\n(90-100%)', zones['Zone 5 (90-100%)'] ?? 0, const Color(0xFFF44336)),
    ];
    
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Heart Rate Zones',
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            SizedBox(
              height: 200,
              child: BarChart(
                BarChartData(
                  alignment: BarChartAlignment.spaceAround,
                  maxY: total.toDouble() * 1.2,
                  barTouchData: BarTouchData(
                    enabled: true,
                    touchTooltipData: BarTouchTooltipData(
                      tooltipRoundedRadius: 8,
                    ),
                  ),
                  titlesData: FlTitlesData(
                    show: true,
                    bottomTitles: AxisTitles(
                      sideTitles: SideTitles(
                        showTitles: true,
                        getTitlesWidget: (value, meta) {
                          final index = value.toInt();
                          if (index >= 0 && index < zoneData.length) {
                            return Padding(
                              padding: const EdgeInsets.only(top: 8.0),
                              child: Text(
                                zoneData[index].label.split('\n').first,
                                style: const TextStyle(
                                  color: AppColors.stravaGray,
                                  fontSize: 10,
                                ),
                                textAlign: TextAlign.center,
                              ),
                            );
                          }
                          return const Text('');
                        },
                      ),
                    ),
                    leftTitles: AxisTitles(
                      sideTitles: SideTitles(
                        showTitles: true,
                        reservedSize: 40,
                        getTitlesWidget: (value, meta) {
                          return Text(
                            value.toInt().toString(),
                            style: const TextStyle(
                              color: AppColors.stravaGray,
                              fontSize: 10,
                            ),
                          );
                        },
                      ),
                    ),
                    rightTitles: const AxisTitles(
                      sideTitles: SideTitles(showTitles: false),
                    ),
                    topTitles: const AxisTitles(
                      sideTitles: SideTitles(showTitles: false),
                    ),
                  ),
                  gridData: FlGridData(
                    show: true,
                    drawVerticalLine: false,
                    getDrawingHorizontalLine: (value) {
                      return FlLine(
                        color: AppColors.stravaLightGray,
                        strokeWidth: 1,
                      );
                    },
                  ),
                  borderData: FlBorderData(
                    show: true,
                    border: Border.all(
                      color: AppColors.stravaLightGray,
                      width: 1,
                    ),
                  ),
                  barGroups: zoneData.asMap().entries.map((entry) {
                    final index = entry.key;
                    final data = entry.value;
                    final percentage = (data.value / total) * 100;
                    
                    return BarChartGroupData(
                      x: index,
                      barRods: [
                        BarChartRodData(
                          toY: data.value.toDouble(),
                          color: data.color,
                          width: 20,
                          borderRadius: const BorderRadius.vertical(
                            top: Radius.circular(4),
                          ),
                        ),
                      ],
                      barsSpace: 8,
                    );
                  }).toList(),
                ),
              ),
            ),
            const SizedBox(height: 16),
            // Legend with percentages
            Wrap(
              spacing: 16,
              runSpacing: 8,
              children: zoneData.map((zone) {
                final percentage = (zone.value / total) * 100;
                return Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Container(
                      width: 16,
                      height: 16,
                      decoration: BoxDecoration(
                        color: zone.color,
                        borderRadius: BorderRadius.circular(4),
                      ),
                    ),
                    const SizedBox(width: 4),
                    Text(
                      '${zone.label.split('\n').first}: ${percentage.toStringAsFixed(1)}%',
                      style: const TextStyle(
                        fontSize: 12,
                        color: AppColors.stravaGray,
                      ),
                    ),
                  ],
                );
              }).toList(),
            ),
          ],
        ),
      ),
    );
  }
}

class _ZoneData {
  final String label;
  final int value;
  final Color color;
  
  _ZoneData(this.label, this.value, this.color);
}
