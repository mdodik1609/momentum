import 'package:workmanager/workmanager.dart';
import 'package:dio/dio.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../database/database.dart';
import 'strava_service.dart';

/// Background sync service for Strava activities
class BackgroundSyncService {
  static const String taskName = 'stravaSyncTask';
  
  /// Initialize background sync
  static Future<void> initialize() async {
    await Workmanager().initialize(
      callbackDispatcher,
      isInDebugMode: false,
    );
  }
  
  /// Register periodic sync task
  static Future<void> registerPeriodicSync() async {
    await Workmanager().registerPeriodicTask(
      taskName,
      taskName,
      frequency: const Duration(hours: 6), // Sync every 6 hours
      constraints: Constraints(
        networkType: NetworkType.connected,
        requiresBatteryNotLow: false,
        requiresCharging: false,
        requiresDeviceIdle: false,
        requiresStorageNotLow: false,
      ),
    );
  }
  
  /// Cancel periodic sync
  static Future<void> cancelSync() async {
    await Workmanager().cancelByUniqueName(taskName);
  }
}

/// Background task callback
@pragma('vm:entry-point')
void callbackDispatcher() {
  Workmanager().executeTask((task, inputData) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final accessToken = prefs.getString(StravaService.accessTokenKey);
      
      if (accessToken == null) {
        return Future.value(false);
      }
      
      final dio = Dio();
      final stravaService = StravaService(dio, prefs);
      final database = AppDatabase();
      
      // Sync activities from last 7 days
      final after = DateTime.now().subtract(const Duration(days: 7));
      final imported = await stravaService.importActivitiesFromStrava(
        database,
        after: after,
      );
      
      await database.close();
      
      return Future.value(true);
    } catch (e) {
      print('Background sync error: $e');
      return Future.value(false);
    }
  });
}

