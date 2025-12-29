import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../services/file_import_service.dart';
import 'database_provider.dart';

final fileImportServiceProvider = Provider<FileImportService>((ref) {
  return FileImportService(ref.watch(databaseProvider));
});




