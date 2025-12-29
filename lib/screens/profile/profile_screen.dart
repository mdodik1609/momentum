import 'package:flutter/material.dart';
import '../../theme/app_theme.dart';

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Profile'),
        actions: [
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {
              // Settings
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
            // Sections
            ListTile(
              leading: const Icon(Icons.fitness_center),
              title: const Text('Personal Records'),
              trailing: const Icon(Icons.chevron_right),
              onTap: () {
                // Navigate to PRs
              },
            ),
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
                // Navigate to settings
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




