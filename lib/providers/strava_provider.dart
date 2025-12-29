import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:dio/dio.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../services/strava_service.dart';

final dioProvider = Provider<Dio>((ref) {
  return Dio();
});

final sharedPreferencesProvider = FutureProvider<SharedPreferences>((ref) async {
  return SharedPreferences.getInstance();
});

final stravaServiceProvider = FutureProvider<StravaService>((ref) async {
  final dio = ref.watch(dioProvider);
  final prefs = await ref.watch(sharedPreferencesProvider.future);
  return StravaService(dio, prefs);
});

