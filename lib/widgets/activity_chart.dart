import 'package:flutter/material.dart';
import 'package:fl_chart/fl_chart.dart';
import '../theme/app_theme.dart';

/// Chart widget for displaying activity data over time/distance
class ActivityChart extends StatelessWidget {
  final String title;
  final List<double> data;
  final List<String>? xAxisLabels;
  final String yAxisLabel;
  final Color lineColor;
  final bool showXAxisAsDistance;
  final double? maxDistance;
  final String Function(double) yAxisFormatter;
  
  const ActivityChart({
    super.key,
    required this.title,
    required this.data,
    this.xAxisLabels,
    this.yAxisLabel = '',
    this.lineColor = AppColors.stravaOrange,
    this.showXAxisAsDistance = false,
    this.maxDistance,
    required this.yAxisFormatter,
  });
  
  @override
  Widget build(BuildContext context) {
    if (data.isEmpty) {
      return Card(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Center(
            child: Text(
              'No data available',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: AppColors.stravaGray,
              ),
            ),
          ),
        ),
      );
    }
    
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              title,
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            SizedBox(
              height: 200,
              child: LineChart(
                LineChartData(
                  gridData: FlGridData(
                    show: true,
                    drawVerticalLine: false,
                    horizontalInterval: _calculateInterval(data),
                    getDrawingHorizontalLine: (value) {
                      return FlLine(
                        color: AppColors.stravaLightGray,
                        strokeWidth: 1,
                      );
                    },
                  ),
                  titlesData: FlTitlesData(
                    show: true,
                    rightTitles: const AxisTitles(
                      sideTitles: SideTitles(showTitles: false),
                    ),
                    topTitles: const AxisTitles(
                      sideTitles: SideTitles(showTitles: false),
                    ),
                    bottomTitles: AxisTitles(
                      sideTitles: SideTitles(
                        showTitles: true,
                        reservedSize: 30,
                        interval: _calculateXInterval(data.length),
                        getTitlesWidget: (value, meta) {
                          if (xAxisLabels != null && value.toInt() < xAxisLabels!.length) {
                            return Padding(
                              padding: const EdgeInsets.only(top: 8.0),
                              child: Text(
                                xAxisLabels![value.toInt()],
                                style: const TextStyle(
                                  color: AppColors.stravaGray,
                                  fontSize: 10,
                                ),
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
                        reservedSize: 50,
                        interval: _calculateInterval(data),
                        getTitlesWidget: (value, meta) {
                          return Text(
                            yAxisFormatter(value),
                            style: const TextStyle(
                              color: AppColors.stravaGray,
                              fontSize: 10,
                            ),
                          );
                        },
                      ),
                    ),
                  ),
                  borderData: FlBorderData(
                    show: true,
                    border: Border.all(
                      color: AppColors.stravaLightGray,
                      width: 1,
                    ),
                  ),
                  minX: 0,
                  maxX: (data.length - 1).toDouble(),
                  minY: _calculateMinY(data),
                  maxY: _calculateMaxY(data),
                  lineBarsData: [
                    LineChartBarData(
                      spots: List.generate(
                        data.length,
                        (index) => FlSpot(index.toDouble(), data[index]),
                      ),
                      isCurved: true,
                      color: lineColor,
                      barWidth: 2,
                      isStrokeCapRound: true,
                      dotData: const FlDotData(show: false),
                      belowBarData: BarAreaData(
                        show: true,
                        color: lineColor.withOpacity(0.1),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
  
  double _calculateInterval(List<double> data) {
    if (data.isEmpty) return 1;
    final max = data.reduce((a, b) => a > b ? a : b);
    final min = data.reduce((a, b) => a < b ? a : b);
    final range = max - min;
    if (range == 0) return 1;
    return range / 5;
  }
  
  double _calculateXInterval(int length) {
    if (length <= 10) return 1;
    if (length <= 50) return length / 5;
    return length / 10;
  }
  
  double _calculateMinY(List<double> data) {
    if (data.isEmpty) return 0;
    final min = data.reduce((a, b) => a < b ? a : b);
    return min * 0.9; // Add 10% padding
  }
  
  double _calculateMaxY(List<double> data) {
    if (data.isEmpty) return 100;
    final max = data.reduce((a, b) => a > b ? a : b);
    return max * 1.1; // Add 10% padding
  }
}

/// Multi-line chart for comparing multiple metrics
class MultiMetricChart extends StatelessWidget {
  final String title;
  final Map<String, List<double>> dataSeries;
  final Map<String, Color> colors;
  final String Function(double) yAxisFormatter;
  
  const MultiMetricChart({
    super.key,
    required this.title,
    required this.dataSeries,
    required this.colors,
    required this.yAxisFormatter,
  });
  
  @override
  Widget build(BuildContext context) {
    if (dataSeries.isEmpty || dataSeries.values.first.isEmpty) {
      return Card(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Center(
            child: Text(
              'No data available',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: AppColors.stravaGray,
              ),
            ),
          ),
        ),
      );
    }
    
    final maxLength = dataSeries.values.map((e) => e.length).reduce((a, b) => a > b ? a : b);
    final allValues = dataSeries.values.expand((e) => e).toList();
    final minY = allValues.isEmpty ? 0 : allValues.reduce((a, b) => a < b ? a : b) * 0.9;
    final maxY = allValues.isEmpty ? 100 : allValues.reduce((a, b) => a > b ? a : b) * 1.1;
    
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              title,
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            // Legend
            Wrap(
              spacing: 16,
              children: dataSeries.keys.map((key) {
                return Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Container(
                      width: 12,
                      height: 12,
                      decoration: BoxDecoration(
                        color: colors[key] ?? AppColors.stravaOrange,
                        shape: BoxShape.circle,
                      ),
                    ),
                    const SizedBox(width: 4),
                    Text(
                      key,
                      style: const TextStyle(
                        fontSize: 12,
                        color: AppColors.stravaGray,
                      ),
                    ),
                  ],
                );
              }).toList(),
            ),
            const SizedBox(height: 16),
            SizedBox(
              height: 200,
              child: LineChart(
                LineChartData(
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
                  titlesData: FlTitlesData(
                    show: true,
                    rightTitles: const AxisTitles(
                      sideTitles: SideTitles(showTitles: false),
                    ),
                    topTitles: const AxisTitles(
                      sideTitles: SideTitles(showTitles: false),
                    ),
                    bottomTitles: const AxisTitles(
                      sideTitles: SideTitles(showTitles: false),
                    ),
                    leftTitles: AxisTitles(
                      sideTitles: SideTitles(
                        showTitles: true,
                        reservedSize: 50,
                        getTitlesWidget: (value, meta) {
                          return Text(
                            yAxisFormatter(value),
                            style: const TextStyle(
                              color: AppColors.stravaGray,
                              fontSize: 10,
                            ),
                          );
                        },
                      ),
                    ),
                  ),
                  borderData: FlBorderData(
                    show: true,
                    border: Border.all(
                      color: AppColors.stravaLightGray,
                      width: 1,
                    ),
                  ),
                  minX: 0,
                  maxX: (maxLength - 1).toDouble(),
                  minY: minY.toDouble(),
                  maxY: maxY.toDouble(),
                  lineBarsData: dataSeries.entries.map((entry) {
                    return LineChartBarData(
                      spots: List.generate(
                        entry.value.length,
                        (index) => FlSpot(index.toDouble(), entry.value[index]),
                      ),
                      isCurved: true,
                      color: colors[entry.key] ?? AppColors.stravaOrange,
                      barWidth: 2,
                      isStrokeCapRound: true,
                      dotData: const FlDotData(show: false),
                    );
                  }).toList(),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

/// Time series chart for trends over time
class TimeSeriesChart extends StatelessWidget {
  final String title;
  final List<DateTime> dates;
  final List<double> values;
  final String yAxisLabel;
  final Color lineColor;
  final String Function(double) yAxisFormatter;
  
  const TimeSeriesChart({
    super.key,
    required this.title,
    required this.dates,
    required this.values,
    this.yAxisLabel = '',
    this.lineColor = AppColors.stravaOrange,
    required this.yAxisFormatter,
  });
  
  @override
  Widget build(BuildContext context) {
    if (values.isEmpty || dates.isEmpty) {
      return Card(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Center(
            child: Text(
              'No data available',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: AppColors.stravaGray,
              ),
            ),
          ),
        ),
      );
    }
    
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              title,
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            SizedBox(
              height: 200,
              child: LineChart(
                LineChartData(
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
                  titlesData: FlTitlesData(
                    show: true,
                    rightTitles: const AxisTitles(
                      sideTitles: SideTitles(showTitles: false),
                    ),
                    topTitles: const AxisTitles(
                      sideTitles: SideTitles(showTitles: false),
                    ),
                    bottomTitles: AxisTitles(
                      sideTitles: SideTitles(
                        showTitles: true,
                        reservedSize: 30,
                        interval: _calculateXInterval(dates.length),
                        getTitlesWidget: (value, meta) {
                          final index = value.toInt();
                          if (index >= 0 && index < dates.length) {
                            return Padding(
                              padding: const EdgeInsets.only(top: 8.0),
                              child: Text(
                                _formatDate(dates[index]),
                                style: const TextStyle(
                                  color: AppColors.stravaGray,
                                  fontSize: 10,
                                ),
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
                        reservedSize: 50,
                        getTitlesWidget: (value, meta) {
                          return Text(
                            yAxisFormatter(value),
                            style: const TextStyle(
                              color: AppColors.stravaGray,
                              fontSize: 10,
                            ),
                          );
                        },
                      ),
                    ),
                  ),
                  borderData: FlBorderData(
                    show: true,
                    border: Border.all(
                      color: AppColors.stravaLightGray,
                      width: 1,
                    ),
                  ),
                  minX: 0,
                  maxX: (values.length - 1).toDouble(),
                  minY: _calculateMinY(values),
                  maxY: _calculateMaxY(values),
                  lineBarsData: [
                    LineChartBarData(
                      spots: List.generate(
                        values.length,
                        (index) => FlSpot(index.toDouble(), values[index]),
                      ),
                      isCurved: true,
                      color: lineColor,
                      barWidth: 2,
                      isStrokeCapRound: true,
                      dotData: const FlDotData(show: false),
                      belowBarData: BarAreaData(
                        show: true,
                        color: lineColor.withOpacity(0.1),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
  
  double _calculateXInterval(int length) {
    if (length <= 7) return 1;
    if (length <= 30) return length / 5;
    return length / 10;
  }
  
  double _calculateMinY(List<double> values) {
    if (values.isEmpty) return 0;
    final min = values.reduce((a, b) => a < b ? a : b);
    return min * 0.9;
  }
  
  double _calculateMaxY(List<double> values) {
    if (values.isEmpty) return 100;
    final max = values.reduce((a, b) => a > b ? a : b);
    return max * 1.1;
  }
  
  String _formatDate(DateTime date) {
    return '${date.month}/${date.day}';
  }
}

