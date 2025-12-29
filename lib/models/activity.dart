import 'package:drift/drift.dart';

enum SportType {
  run,
  ride,
  walk,
  hike,
  trailRun,
  virtualRide,
  indoorRide,
  swim,
  other;

  String get displayName {
    switch (this) {
      case SportType.run:
        return 'Run';
      case SportType.ride:
        return 'Ride';
      case SportType.walk:
        return 'Walk';
      case SportType.hike:
        return 'Hike';
      case SportType.trailRun:
        return 'Trail Run';
      case SportType.virtualRide:
        return 'Virtual Ride';
      case SportType.indoorRide:
        return 'Indoor Ride';
      case SportType.swim:
        return 'Swim';
      case SportType.other:
        return 'Other';
    }
  }

  static SportType fromInt(int value) {
    return SportType.values[value.clamp(0, SportType.values.length - 1)];
  }

  int toInt() => index;
}

class SportTypeConverter extends TypeConverter<SportType, int> {
  const SportTypeConverter();

  @override
  SportType fromSql(int fromDb) => SportType.fromInt(fromDb);

  @override
  int toSql(SportType value) => value.toInt();
}

class Activities extends Table {
  TextColumn get id => text()();
  TextColumn get name => text()();
  IntColumn get sportType => integer().map(const SportTypeConverter())();
  DateTimeColumn get startDate => dateTime()();
  TextColumn get timezone => text().nullable()();
  IntColumn get movingTimeSeconds => integer()();
  IntColumn get elapsedTimeSeconds => integer()();
  RealColumn get distanceMeters => real().nullable()();
  RealColumn get elevationGainMeters => real().nullable()();
  RealColumn get elevationLossMeters => real().nullable()();
  RealColumn get averageSpeedMps => real().nullable()();
  RealColumn get maxSpeedMps => real().nullable()();
  IntColumn get averageHeartRateBpm => integer().nullable()();
  IntColumn get maxHeartRateBpm => integer().nullable()();
  RealColumn get averageCadenceRpm => real().nullable()();
  IntColumn get averagePowerWatts => integer().nullable()();
  IntColumn get maxPowerWatts => integer().nullable()();
  IntColumn get calories => integer().nullable()();
  TextColumn get description => text().nullable()();
  TextColumn get source => text().nullable()();
  TextColumn get sourceId => text().nullable()();
  DateTimeColumn get createdAt => dateTime()();
  DateTimeColumn get updatedAt => dateTime()();

  @override
  Set<Column> get primaryKey => {id};
}

class ActivityStreams extends Table {
  TextColumn get activityId => text()();
  IntColumn get timeOffsetSeconds => integer()();
  RealColumn get latitude => real().nullable()();
  RealColumn get longitude => real().nullable()();
  RealColumn get altitudeMeters => real().nullable()();
  IntColumn get heartRateBpm => integer().nullable()();
  RealColumn get cadenceRpm => real().nullable()();
  IntColumn get powerWatts => integer().nullable()();
  RealColumn get speedMps => real().nullable()();
  RealColumn get grade => real().nullable()();
  RealColumn get distanceMeters => real().nullable()();

  @override
  Set<Column> get primaryKey => {activityId, timeOffsetSeconds};
}
