import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../theme/app_theme.dart';
import '../../providers/personal_records_provider.dart';
import '../../providers/analytics_provider.dart';
import '../../widgets/heart_rate_zones_chart.dart';
import '../settings/settings_screen.dart';
import 'package:intl/intl.dart';

class ProfileScreen extends ConsumerWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final heartRateZones = ref.watch(heartRateZonesProvider);
    final personalRecords = ref.watch(personalRecordsProvider);
    return Scaffold(
      appBar: AppBar(
        title: const Text('Profile'),
        actions: [
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => const SettingsScreen(),
                ),
              );
            },
          ),
        ],
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            // Profile Header
            Container(
              padding: const EdgeInsets.all(24),
              child: Column(
                children: [
                  const CircleAvatar(
                    radius: 50,
                    backgroundColor: AppColors.stravaOrange,
                    child: Icon(Icons.person, size: 50, color: Colors.white),
                  ),
                  const SizedBox(height: 16),
                  Text(
                    'Athlete',
                    style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Member since 2024',
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      color: AppColors.stravaGray,
                    ),
                  ),
                ],
              ),
            ),
            // Stats Grid
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Row(
                children: [
                  Expanded(
                    child: _ProfileStatCard(value: '0', label: 'Activities'),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: _ProfileStatCard(
                      value: '0 km',
                      label: 'Total Distance',
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: _ProfileStatCard(value: '0', label: 'Streak'),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),
            
            // Heart Rate Zones
            heartRateZones.when(
              data: (zones) => Padding(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: HeartRateZonesChart(zones: zones),
              ),
              loading: () => const SizedBox.shrink(),
              error: (_, __) => const SizedBox.shrink(),
            ),
            const SizedBox(height: 24),
            
            // Personal Records
            personalRecords.when(
              data: (records) {
                if (records.isEmpty) {
                  return const SizedBox.shrink();
                }
                return Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      child: Text(
                        'Personal Records',
                        style: Theme.of(context).textTheme.titleLarge?.copyWith(
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                    const SizedBox(height: 16),
                    ...records.map((record) => Card(
                      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
                      child: ListTile(
                        leading: CircleAvatar(
                          backgroundColor: AppColors.stravaOrange,
                          child: const Icon(Icons.emoji_events, color: Colors.white),
                        ),
                        title: Text(record.distanceLabel),
                        subtitle: Text(record.activityName),
                        trailing: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          crossAxisAlignment: CrossAxisAlignment.end,
                          children: [
                            Text(
                              record.timeFormatted,
                              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                                fontWeight: FontWeight.bold,
                                color: AppColors.stravaOrange,
                              ),
                            ),
                            Text(
                              record.paceFormatted,
                              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                color: AppColors.stravaGray,
                              ),
                            ),
                          ],
                        ),
                      ),
                    )),
                    const SizedBox(height: 24),
                  ],
                );
              },
              loading: () => const Center(child: CircularProgressIndicator()),
              error: (_, __) => const SizedBox.shrink(),
            ),
            
            // Sections
            ListTile(
              leading: const Icon(Icons.analytics),
              title: const Text('Analytics'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () {
                // Navigate to analytics
              },
            ),
            ListTile(
              leading: const Icon(Icons.settings),
              title: const Text('Settings'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const SettingsScreen(),
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}

class _ProfileStatCard extends StatelessWidget {
  final String value;
  final String label;

  const _ProfileStatCard({required this.value, required this.label});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Text(
              value,
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                fontWeight: FontWeight.bold,
                color: AppColors.stravaOrange,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              label,
              style: Theme.of(
                context,
              ).textTheme.bodySmall?.copyWith(color: AppColors.stravaGray),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      ),
    );
  }
}




