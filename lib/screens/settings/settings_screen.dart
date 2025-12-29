import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../theme/app_theme.dart';
import '../../services/background_sync_service.dart';
import '../strava_auth/strava_auth_screen.dart';

class SettingsScreen extends ConsumerStatefulWidget {
  const SettingsScreen({super.key});

  @override
  ConsumerState<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends ConsumerState<SettingsScreen> {
  bool _backgroundSyncEnabled = false;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadSettings();
  }

  Future<void> _loadSettings() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      _backgroundSyncEnabled = prefs.getBool('background_sync_enabled') ?? false;
      _isLoading = false;
    });
  }

  Future<void> _toggleBackgroundSync(bool value) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('background_sync_enabled', value);
    
    if (value) {
      await BackgroundSyncService.registerPeriodicSync();
    } else {
      await BackgroundSyncService.cancelSync();
    }
    
    setState(() {
      _backgroundSyncEnabled = value;
    });
    
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            value 
                ? 'Background sync enabled (every 6 hours)'
                : 'Background sync disabled',
          ),
          backgroundColor: AppColors.stravaGreen,
          behavior: SnackBarBehavior.floating,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return Scaffold(
        appBar: AppBar(title: const Text('Settings')),
        body: const Center(child: CircularProgressIndicator()),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text('Settings'),
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          // Strava Section
          Card(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Padding(
                  padding: const EdgeInsets.all(16),
                  child: Text(
                    'Strava Integration',
                    style: Theme.of(context).textTheme.titleLarge?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                ListTile(
                  leading: const Icon(Icons.link, color: AppColors.stravaOrange),
                  title: const Text('Connect to Strava'),
                  subtitle: const Text('Sync activities from Strava'),
                  trailing: const Icon(Icons.chevron_right),
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => const StravaAuthScreen(),
                      ),
                    );
                  },
                ),
                SwitchListTile(
                  secondary: const Icon(Icons.sync, color: AppColors.stravaOrange),
                  title: const Text('Background Sync'),
                  subtitle: const Text('Automatically sync activities every 6 hours'),
                  value: _backgroundSyncEnabled,
                  onChanged: _toggleBackgroundSync,
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),
          
          // Data Section
          Card(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Padding(
                  padding: const EdgeInsets.all(16),
                  child: Text(
                    'Data Management',
                    style: Theme.of(context).textTheme.titleLarge?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                ListTile(
                  leading: const Icon(Icons.info_outline),
                  title: const Text('About'),
                  subtitle: const Text('App version and information'),
                  trailing: const Icon(Icons.chevron_right),
                  onTap: () {
                    showAboutDialog(
                      context: context,
                      applicationName: 'Momentum',
                      applicationVersion: '1.0.0',
                      applicationIcon: const Icon(
                        Icons.directions_run,
                        size: 48,
                        color: AppColors.stravaOrange,
                      ),
                      children: [
                        const Text(
                          'A powerful, offline-first fitness tracking app.\n\n'
                          'Built with Flutter for cross-platform support.',
                        ),
                      ],
                    );
                  },
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),
          
          // Privacy Section
          Card(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Padding(
                  padding: const EdgeInsets.all(16),
                  child: Text(
                    'Privacy',
                    style: Theme.of(context).textTheme.titleLarge?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                const ListTile(
                  leading: Icon(Icons.lock, color: AppColors.stravaGreen),
                  title: Text('Local Storage Only'),
                  subtitle: Text('All data is stored locally on your device'),
                ),
                const ListTile(
                  leading: Icon(Icons.cloud_off, color: AppColors.stravaGreen),
                  title: Text('No Cloud Sync'),
                  subtitle: Text('Your data never leaves your device'),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

