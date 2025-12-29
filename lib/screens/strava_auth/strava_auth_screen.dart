import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:url_launcher/url_launcher.dart';
import '../../providers/strava_provider.dart';
import '../../providers/database_provider.dart';
import '../../services/strava_service.dart';
import '../../theme/app_theme.dart';

class StravaAuthScreen extends ConsumerStatefulWidget {
  const StravaAuthScreen({super.key});

  @override
  ConsumerState<StravaAuthScreen> createState() => _StravaAuthScreenState();
}

class _StravaAuthScreenState extends ConsumerState<StravaAuthScreen> {
  final _clientIdController = TextEditingController();
  final _clientSecretController = TextEditingController();
  bool _isLoading = false;
  String? _error;

  @override
  void dispose() {
    _clientIdController.dispose();
    _clientSecretController.dispose();
    super.dispose();
  }

  Future<void> _authenticate() async {
    if (_clientIdController.text.isEmpty || _clientSecretController.text.isEmpty) {
      setState(() {
        _error = 'Please enter both Client ID and Client Secret';
      });
      return;
    }

    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      final stravaService = await ref.read(stravaServiceProvider.future);
      final redirectUri = 'momentum://strava-callback';
      
      final authUrl = stravaService.getAuthorizationUrl(
        _clientIdController.text,
        redirectUri,
      );

      if (await canLaunchUrl(Uri.parse(authUrl))) {
        await launchUrl(Uri.parse(authUrl), mode: LaunchMode.externalApplication);
        
        // Show dialog to enter authorization code
        if (mounted) {
          _showCodeInputDialog(stravaService);
        }
      } else {
        setState(() {
          _error = 'Could not launch browser. Please check your internet connection.';
          _isLoading = false;
        });
      }
    } catch (e) {
      setState(() {
        _error = 'Error: $e';
        _isLoading = false;
      });
    }
  }

  Future<void> _showCodeInputDialog(StravaService stravaService) async {
    final codeController = TextEditingController();
    
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => AlertDialog(
        title: const Text('Enter Authorization Code'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Text(
              'After authorizing, Strava will redirect you. Copy the "code" parameter from the URL and paste it here.',
            ),
            const SizedBox(height: 16),
            TextField(
              controller: codeController,
              decoration: const InputDecoration(
                labelText: 'Authorization Code',
                hintText: 'Paste code here',
                border: OutlineInputBorder(),
              ),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              setState(() {
                _isLoading = false;
              });
            },
            child: const Text('Cancel'),
          ),
          ElevatedButton(
            onPressed: () async {
              final code = codeController.text.trim();
              if (code.isEmpty) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Please enter the authorization code')),
                );
                return;
              }

              Navigator.pop(context);
              setState(() {
                _isLoading = true;
              });

              try {
                final redirectUri = 'momentum://strava-callback';
                final success = await stravaService.exchangeCodeForToken(
                  code,
                  _clientIdController.text,
                  _clientSecretController.text,
                  redirectUri,
                );

                if (success && mounted) {
                  // Sync activities
                  final db = ref.read(databaseProvider);
                  final count = await stravaService.importActivitiesFromStrava(db);
                  
                  if (mounted) {
                    Navigator.pop(context);
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(
                        content: Text('Successfully synced $count activities from Strava!'),
                        backgroundColor: AppColors.stravaGreen,
                      ),
                    );
                  }
                } else {
                  setState(() {
                    _error = 'Failed to authenticate. Please check your credentials.';
                    _isLoading = false;
                  });
                }
              } catch (e) {
                setState(() {
                  _error = 'Error: $e';
                  _isLoading = false;
                });
              }
            },
            child: const Text('Authorize'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Connect Strava'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Connect to Strava',
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'Sync your activities from Strava to Momentum',
              style: TextStyle(
                color: AppColors.stravaGray,
                fontSize: 16,
              ),
            ),
            const SizedBox(height: 32),
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      'How to get Strava API credentials:',
                      style: TextStyle(
                        fontWeight: FontWeight.bold,
                        fontSize: 16,
                      ),
                    ),
                    const SizedBox(height: 12),
                    const Text('1. Go to https://www.strava.com/settings/api'),
                    const Text('2. Create a new application'),
                    const Text('3. Copy your Client ID and Client Secret'),
                    const Text('4. Set Authorization Callback Domain to: momentum'),
                    const SizedBox(height: 16),
                    TextButton.icon(
                      onPressed: () async {
                        const url = 'https://www.strava.com/settings/api';
                        if (await canLaunchUrl(Uri.parse(url))) {
                          await launchUrl(Uri.parse(url));
                        }
                      },
                      icon: const Icon(Icons.open_in_new),
                      label: const Text('Open Strava Settings'),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 24),
            TextField(
              controller: _clientIdController,
              decoration: const InputDecoration(
                labelText: 'Client ID',
                hintText: 'Enter your Strava Client ID',
                border: OutlineInputBorder(),
              ),
              keyboardType: TextInputType.number,
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _clientSecretController,
              decoration: const InputDecoration(
                labelText: 'Client Secret',
                hintText: 'Enter your Strava Client Secret',
                border: OutlineInputBorder(),
                helperText: 'This is stored securely on your device',
              ),
              obscureText: true,
            ),
            if (_error != null) ...[
              const SizedBox(height: 16),
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: AppColors.stravaRed.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: AppColors.stravaRed),
                ),
                child: Row(
                  children: [
                    Icon(Icons.error_outline, color: AppColors.stravaRed),
                    const SizedBox(width: 8),
                    Expanded(
                      child: Text(
                        _error!,
                        style: TextStyle(color: AppColors.stravaRed),
                      ),
                    ),
                  ],
                ),
              ),
            ],
            const SizedBox(height: 24),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _isLoading ? null : _authenticate,
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.stravaOrange,
                  foregroundColor: AppColors.stravaWhite,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                ),
                child: _isLoading
                    ? const SizedBox(
                        height: 20,
                        width: 20,
                        child: CircularProgressIndicator(
                          strokeWidth: 2,
                          valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                        ),
                      )
                    : const Text('Connect to Strava'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

