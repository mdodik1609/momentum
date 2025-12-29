import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:drift/drift.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../database/database.dart';
import '../models/activity.dart' show SportType;

/// Strava API Service for fetching activities and syncing data
/// Based on Strava API v3: https://developers.strava.com/docs/reference/
class StravaService {
  static const String _baseUrl = 'https://www.strava.com/api/v3';
  static const String _authUrl = 'https://www.strava.com/oauth';
  
  final Dio _dio;
  final SharedPreferences _prefs;
  
  StravaService(this._dio, this._prefs);
  
  // OAuth2 Configuration
  static const String clientIdKey = 'strava_client_id';
  static const String clientSecretKey = 'strava_client_secret';
  static const String accessTokenKey = 'strava_access_token';
  static const String refreshTokenKey = 'strava_refresh_token';
  static const String tokenExpiresAtKey = 'strava_token_expires_at';
  
  /// Initialize OAuth2 flow
  /// Returns the authorization URL
  String getAuthorizationUrl(String clientId, String redirectUri) {
    return '$_authUrl/authorize?'
        'client_id=$clientId&'
        'redirect_uri=${Uri.encodeComponent(redirectUri)}&'
        'response_type=code&'
        'scope=activity:read_all,profile:read_all';
  }
  
  /// Exchange authorization code for access token
  Future<bool> exchangeCodeForToken(
    String code,
    String clientId,
    String clientSecret,
    String redirectUri,
  ) async {
    try {
      final response = await _dio.post(
        '$_authUrl/token',
        data: {
          'client_id': clientId,
          'client_secret': clientSecret,
          'code': code,
          'grant_type': 'authorization_code',
          'redirect_uri': redirectUri,
        },
      );
      
      await _saveTokens(response.data);
      await _prefs.setString(clientIdKey, clientId);
      await _prefs.setString(clientSecretKey, clientSecret);
      
      return true;
    } catch (e) {
      print('Error exchanging code for token: $e');
      return false;
    }
  }
  
  /// Refresh access token using refresh token
  Future<bool> refreshAccessToken() async {
    final refreshToken = _prefs.getString(refreshTokenKey);
    final clientId = _prefs.getString(clientIdKey);
    final clientSecret = _prefs.getString(clientSecretKey);
    
    if (refreshToken == null || clientId == null || clientSecret == null) {
      return false;
    }
    
    try {
      final response = await _dio.post(
        '$_authUrl/token',
        data: {
          'client_id': clientId,
          'client_secret': clientSecret,
          'refresh_token': refreshToken,
          'grant_type': 'refresh_token',
        },
      );
      
      await _saveTokens(response.data);
      return true;
    } catch (e) {
      print('Error refreshing token: $e');
      return false;
    }
  }
  
  /// Save tokens to SharedPreferences
  Future<void> _saveTokens(Map<String, dynamic> data) async {
    await _prefs.setString(accessTokenKey, data['access_token']);
    await _prefs.setString(refreshTokenKey, data['refresh_token']);
    await _prefs.setInt(
      tokenExpiresAtKey,
      DateTime.now().add(Duration(seconds: data['expires_in'])).millisecondsSinceEpoch,
    );
  }
  
  /// Get valid access token (refresh if needed)
  Future<String?> getValidAccessToken() async {
    final token = _prefs.getString(accessTokenKey);
    final expiresAt = _prefs.getInt(tokenExpiresAtKey);
    
    if (token == null || expiresAt == null) {
      return null;
    }
    
    // Refresh if token expires in less than 5 minutes
    if (DateTime.now().millisecondsSinceEpoch >= expiresAt - 300000) {
      final refreshed = await refreshAccessToken();
      if (!refreshed) {
        return null;
      }
      return _prefs.getString(accessTokenKey);
    }
    
    return token;
  }
  
  /// Check if user is authenticated
  bool isAuthenticated() {
    return _prefs.getString(accessTokenKey) != null;
  }
  
  /// Logout
  Future<void> logout() async {
    await _prefs.remove(accessTokenKey);
    await _prefs.remove(refreshTokenKey);
    await _prefs.remove(tokenExpiresAtKey);
  }
  
  /// Fetch activities from Strava
  Future<List<Map<String, dynamic>>> fetchActivities({
    int perPage = 30,
    int page = 1,
    DateTime? after,
    DateTime? before,
  }) async {
    final token = await getValidAccessToken();
    if (token == null) {
      throw Exception('Not authenticated');
    }
    
    try {
      final queryParams = <String, dynamic>{
        'per_page': perPage,
        'page': page,
      };
      
      if (after != null) {
        queryParams['after'] = after.millisecondsSinceEpoch ~/ 1000;
      }
      if (before != null) {
        queryParams['before'] = before.millisecondsSinceEpoch ~/ 1000;
      }
      
      final response = await _dio.get(
        '$_baseUrl/athlete/activities',
        queryParameters: queryParams,
        options: Options(
          headers: {'Authorization': 'Bearer $token'},
        ),
      );
      
      return List<Map<String, dynamic>>.from(response.data);
    } catch (e) {
      print('Error fetching activities: $e');
      rethrow;
    }
  }
  
  /// Fetch detailed activity data including streams
  Future<Map<String, dynamic>> fetchActivityDetails(int activityId) async {
    final token = await getValidAccessToken();
    if (token == null) {
      throw Exception('Not authenticated');
    }
    
    try {
      final response = await _dio.get(
        '$_baseUrl/activities/$activityId',
        options: Options(
          headers: {'Authorization': 'Bearer $token'},
        ),
      );
      
      return response.data as Map<String, dynamic>;
    } catch (e) {
      print('Error fetching activity details: $e');
      rethrow;
    }
  }
  
  /// Fetch activity streams (GPS, heart rate, etc.)
  Future<Map<String, List<dynamic>>> fetchActivityStreams(
    int activityId, {
    List<String> keys = const ['time', 'distance', 'latlng', 'altitude', 'heartrate', 'cadence', 'watts', 'velocity_smooth', 'grade_smooth'],
  }) async {
    final token = await getValidAccessToken();
    if (token == null) {
      throw Exception('Not authenticated');
    }
    
    try {
      final response = await _dio.get(
        '$_baseUrl/activities/$activityId/streams',
        queryParameters: {'keys': keys.join(',')},
        options: Options(
          headers: {'Authorization': 'Bearer $token'},
        ),
      );
      
      final streams = <String, List<dynamic>>{};
      for (final stream in response.data) {
        streams[stream['type']] = stream['data'];
      }
      
      return streams;
    } catch (e) {
      print('Error fetching activity streams: $e');
      rethrow;
    }
  }
  
  /// Convert Strava activity to database Activity
  static SportType _mapStravaSportType(String? type) {
    switch (type?.toLowerCase()) {
      case 'run':
      case 'trailrun':
        return SportType.run;
      case 'ride':
      case 'virtualride':
        return SportType.ride;
      case 'walk':
        return SportType.walk;
      case 'hike':
        return SportType.hike;
      case 'swim':
        return SportType.swim;
      default:
        return SportType.other;
    }
  }
  
  /// Import activities from Strava to database
  Future<int> importActivitiesFromStrava(AppDatabase database, {
    int perPage = 200,
    DateTime? after,
  }) async {
    int imported = 0;
    int page = 1;
    bool hasMore = true;
    
    while (hasMore) {
      final activities = await fetchActivities(
        perPage: perPage,
        page: page,
        after: after,
      );
      
      if (activities.isEmpty) {
        hasMore = false;
        break;
      }
      
      for (final activityData in activities) {
        try {
          final activityId = activityData['id'].toString();
          
          // Check if activity already exists
          final existing = await (database.select(database.activities)
                ..where((a) => a.id.equals(activityId)))
              .getSingleOrNull();
          
          if (existing != null) {
            continue; // Skip if already imported
          }
          
          // Parse activity data
          final startDate = DateTime.parse(activityData['start_date']);
          final sportType = _mapStravaSportType(activityData['type']);
          
          final activity = ActivitiesCompanion.insert(
            id: activityId,
            name: activityData['name'] ?? 'Untitled Activity',
            sportType: sportType,
            startDate: startDate.toUtc(),
            timezone: activityData['timezone'],
            movingTimeSeconds: activityData['moving_time'] ?? 0,
            elapsedTimeSeconds: activityData['elapsed_time'] ?? 0,
            distanceMeters: (activityData['distance'] as num?)?.toDouble() != null 
                ? Value((activityData['distance'] as num).toDouble()) 
                : const Value.absent(),
            elevationGainMeters: (activityData['total_elevation_gain'] as num?)?.toDouble() != null
                ? Value((activityData['total_elevation_gain'] as num).toDouble())
                : const Value.absent(),
            averageSpeedMps: (activityData['average_speed'] as num?)?.toDouble() != null
                ? Value((activityData['average_speed'] as num).toDouble())
                : const Value.absent(),
            maxSpeedMps: (activityData['max_speed'] as num?)?.toDouble() != null
                ? Value((activityData['max_speed'] as num).toDouble())
                : const Value.absent(),
            averageHeartRateBpm: activityData['average_heartrate']?.toInt() != null
                ? Value(activityData['average_heartrate'].toInt())
                : const Value.absent(),
            maxHeartRateBpm: activityData['max_heartrate']?.toInt() != null
                ? Value(activityData['max_heartrate'].toInt())
                : const Value.absent(),
            averageCadenceRpm: (activityData['average_cadence'] as num?)?.toDouble() != null
                ? Value((activityData['average_cadence'] as num).toDouble())
                : const Value.absent(),
            averagePowerWatts: activityData['average_watts']?.toInt() != null
                ? Value(activityData['average_watts'].toInt())
                : const Value.absent(),
            maxPowerWatts: activityData['max_watts']?.toInt() != null
                ? Value(activityData['max_watts'].toInt())
                : const Value.absent(),
            calories: activityData['calories']?.toInt() != null
                ? Value(activityData['calories'].toInt())
                : const Value.absent(),
            description: activityData['description'] != null
                ? Value(activityData['description'])
                : const Value.absent(),
            source: const Value('strava'),
            sourceId: Value(activityId),
            createdAt: DateTime.now().toUtc(),
            updatedAt: DateTime.now().toUtc(),
          );
          
          await database.into(database.activities).insert(activity);
          imported++;
          
          // Fetch and import streams if available
          try {
            final streams = await fetchActivityStreams(activityData['id']);
            await _importStreams(database, activityId, streams);
          } catch (e) {
            print('Error importing streams for activity $activityId: $e');
          }
        } catch (e) {
          print('Error importing activity: $e');
        }
      }
      
      if (activities.length < perPage) {
        hasMore = false;
      } else {
        page++;
      }
    }
    
    return imported;
  }
  
  /// Import activity streams to database
  Future<void> _importStreams(
    AppDatabase database,
    String activityId,
    Map<String, List<dynamic>> streams,
  ) async {
    final timeStream = streams['time'];
    final distanceStream = streams['distance'];
    final latlngStream = streams['latlng'];
    final altitudeStream = streams['altitude'];
    final heartrateStream = streams['heartrate'];
    final cadenceStream = streams['cadence'];
    final wattsStream = streams['watts'];
    final velocityStream = streams['velocity_smooth'];
    final gradeStream = streams['grade_smooth'];
    
    if (timeStream == null || timeStream.isEmpty) {
      return;
    }
    
    final streamCompanions = <ActivityStreamsCompanion>[];
    
    for (int i = 0; i < timeStream.length; i++) {
      final timeOffset = (timeStream[i] as num).toInt();
      final latlng = i < (latlngStream?.length ?? 0) 
          ? latlngStream![i] as List<dynamic>? 
          : null;
      
      streamCompanions.add(
        ActivityStreamsCompanion.insert(
          activityId: activityId,
          timeOffsetSeconds: timeOffset,
          latitude: latlng != null && latlng.isNotEmpty 
              ? Value((latlng[0] as num).toDouble()) 
              : const Value.absent(),
          longitude: latlng != null && latlng.length > 1 
              ? Value((latlng[1] as num).toDouble()) 
              : const Value.absent(),
          altitudeMeters: i < (altitudeStream?.length ?? 0)
              ? Value((altitudeStream![i] as num).toDouble())
              : const Value.absent(),
          heartRateBpm: i < (heartrateStream?.length ?? 0)
              ? Value((heartrateStream![i] as num).toInt())
              : const Value.absent(),
          cadenceRpm: i < (cadenceStream?.length ?? 0)
              ? Value((cadenceStream![i] as num).toDouble())
              : const Value.absent(),
          powerWatts: i < (wattsStream?.length ?? 0)
              ? Value((wattsStream![i] as num).toInt())
              : const Value.absent(),
          speedMps: i < (velocityStream?.length ?? 0)
              ? Value((velocityStream![i] as num).toDouble())
              : const Value.absent(),
          grade: i < (gradeStream?.length ?? 0)
              ? Value((gradeStream![i] as num).toDouble())
              : const Value.absent(),
          distanceMeters: i < (distanceStream?.length ?? 0)
              ? Value((distanceStream![i] as num).toDouble())
              : const Value.absent(),
        ),
      );
    }
    
    if (streamCompanions.isNotEmpty) {
      await database.batch((batch) {
        batch.insertAll(database.activityStreams, streamCompanions);
      });
    }
  }
}

