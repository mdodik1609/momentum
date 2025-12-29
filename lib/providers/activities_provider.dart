import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:drift/drift.dart' as drift;
import '../database/database.dart';
import 'database_provider.dart';

final activitiesProvider = StreamProvider<List<Activity>>((ref) {
  final db = ref.watch(databaseProvider);
  return (db.select(
    db.activities,
  )..orderBy([(a) => drift.OrderingTerm.desc(a.startDate)])).watch();
});

final activityCountProvider = FutureProvider<int>((ref) async {
  final db = ref.watch(databaseProvider);
  return db
      .select(db.activities, distinct: true)
      .get()
      .then((list) => list.length);
});
