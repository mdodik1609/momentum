import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../services/export_service.dart';
import 'database_provider.dart';

final exportServiceProvider = Provider<ExportService>((ref) {
  return ExportService(ref.watch(databaseProvider));
});

