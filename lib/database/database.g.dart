// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'database.dart';

// ignore_for_file: type=lint
class $ActivitiesTable extends Activities
    with TableInfo<$ActivitiesTable, Activity> {
  @override
  final GeneratedDatabase attachedDatabase;
  final String? _alias;
  $ActivitiesTable(this.attachedDatabase, [this._alias]);
  static const VerificationMeta _idMeta = const VerificationMeta('id');
  @override
  late final GeneratedColumn<String> id = GeneratedColumn<String>(
    'id',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _nameMeta = const VerificationMeta('name');
  @override
  late final GeneratedColumn<String> name = GeneratedColumn<String>(
    'name',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  @override
  late final GeneratedColumnWithTypeConverter<SportType, int> sportType =
      GeneratedColumn<int>(
        'sport_type',
        aliasedName,
        false,
        type: DriftSqlType.int,
        requiredDuringInsert: true,
      ).withConverter<SportType>($ActivitiesTable.$convertersportType);
  static const VerificationMeta _startDateMeta = const VerificationMeta(
    'startDate',
  );
  @override
  late final GeneratedColumn<DateTime> startDate = GeneratedColumn<DateTime>(
    'start_date',
    aliasedName,
    false,
    type: DriftSqlType.dateTime,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _timezoneMeta = const VerificationMeta(
    'timezone',
  );
  @override
  late final GeneratedColumn<String> timezone = GeneratedColumn<String>(
    'timezone',
    aliasedName,
    true,
    type: DriftSqlType.string,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _movingTimeSecondsMeta = const VerificationMeta(
    'movingTimeSeconds',
  );
  @override
  late final GeneratedColumn<int> movingTimeSeconds = GeneratedColumn<int>(
    'moving_time_seconds',
    aliasedName,
    false,
    type: DriftSqlType.int,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _elapsedTimeSecondsMeta =
      const VerificationMeta('elapsedTimeSeconds');
  @override
  late final GeneratedColumn<int> elapsedTimeSeconds = GeneratedColumn<int>(
    'elapsed_time_seconds',
    aliasedName,
    false,
    type: DriftSqlType.int,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _distanceMetersMeta = const VerificationMeta(
    'distanceMeters',
  );
  @override
  late final GeneratedColumn<double> distanceMeters = GeneratedColumn<double>(
    'distance_meters',
    aliasedName,
    true,
    type: DriftSqlType.double,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _elevationGainMetersMeta =
      const VerificationMeta('elevationGainMeters');
  @override
  late final GeneratedColumn<double> elevationGainMeters =
      GeneratedColumn<double>(
        'elevation_gain_meters',
        aliasedName,
        true,
        type: DriftSqlType.double,
        requiredDuringInsert: false,
      );
  static const VerificationMeta _elevationLossMetersMeta =
      const VerificationMeta('elevationLossMeters');
  @override
  late final GeneratedColumn<double> elevationLossMeters =
      GeneratedColumn<double>(
        'elevation_loss_meters',
        aliasedName,
        true,
        type: DriftSqlType.double,
        requiredDuringInsert: false,
      );
  static const VerificationMeta _averageSpeedMpsMeta = const VerificationMeta(
    'averageSpeedMps',
  );
  @override
  late final GeneratedColumn<double> averageSpeedMps = GeneratedColumn<double>(
    'average_speed_mps',
    aliasedName,
    true,
    type: DriftSqlType.double,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _maxSpeedMpsMeta = const VerificationMeta(
    'maxSpeedMps',
  );
  @override
  late final GeneratedColumn<double> maxSpeedMps = GeneratedColumn<double>(
    'max_speed_mps',
    aliasedName,
    true,
    type: DriftSqlType.double,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _averageHeartRateBpmMeta =
      const VerificationMeta('averageHeartRateBpm');
  @override
  late final GeneratedColumn<int> averageHeartRateBpm = GeneratedColumn<int>(
    'average_heart_rate_bpm',
    aliasedName,
    true,
    type: DriftSqlType.int,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _maxHeartRateBpmMeta = const VerificationMeta(
    'maxHeartRateBpm',
  );
  @override
  late final GeneratedColumn<int> maxHeartRateBpm = GeneratedColumn<int>(
    'max_heart_rate_bpm',
    aliasedName,
    true,
    type: DriftSqlType.int,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _averageCadenceRpmMeta = const VerificationMeta(
    'averageCadenceRpm',
  );
  @override
  late final GeneratedColumn<double> averageCadenceRpm =
      GeneratedColumn<double>(
        'average_cadence_rpm',
        aliasedName,
        true,
        type: DriftSqlType.double,
        requiredDuringInsert: false,
      );
  static const VerificationMeta _averagePowerWattsMeta = const VerificationMeta(
    'averagePowerWatts',
  );
  @override
  late final GeneratedColumn<int> averagePowerWatts = GeneratedColumn<int>(
    'average_power_watts',
    aliasedName,
    true,
    type: DriftSqlType.int,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _maxPowerWattsMeta = const VerificationMeta(
    'maxPowerWatts',
  );
  @override
  late final GeneratedColumn<int> maxPowerWatts = GeneratedColumn<int>(
    'max_power_watts',
    aliasedName,
    true,
    type: DriftSqlType.int,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _caloriesMeta = const VerificationMeta(
    'calories',
  );
  @override
  late final GeneratedColumn<int> calories = GeneratedColumn<int>(
    'calories',
    aliasedName,
    true,
    type: DriftSqlType.int,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _descriptionMeta = const VerificationMeta(
    'description',
  );
  @override
  late final GeneratedColumn<String> description = GeneratedColumn<String>(
    'description',
    aliasedName,
    true,
    type: DriftSqlType.string,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _sourceMeta = const VerificationMeta('source');
  @override
  late final GeneratedColumn<String> source = GeneratedColumn<String>(
    'source',
    aliasedName,
    true,
    type: DriftSqlType.string,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _sourceIdMeta = const VerificationMeta(
    'sourceId',
  );
  @override
  late final GeneratedColumn<String> sourceId = GeneratedColumn<String>(
    'source_id',
    aliasedName,
    true,
    type: DriftSqlType.string,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _createdAtMeta = const VerificationMeta(
    'createdAt',
  );
  @override
  late final GeneratedColumn<DateTime> createdAt = GeneratedColumn<DateTime>(
    'created_at',
    aliasedName,
    false,
    type: DriftSqlType.dateTime,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _updatedAtMeta = const VerificationMeta(
    'updatedAt',
  );
  @override
  late final GeneratedColumn<DateTime> updatedAt = GeneratedColumn<DateTime>(
    'updated_at',
    aliasedName,
    false,
    type: DriftSqlType.dateTime,
    requiredDuringInsert: true,
  );
  @override
  List<GeneratedColumn> get $columns => [
    id,
    name,
    sportType,
    startDate,
    timezone,
    movingTimeSeconds,
    elapsedTimeSeconds,
    distanceMeters,
    elevationGainMeters,
    elevationLossMeters,
    averageSpeedMps,
    maxSpeedMps,
    averageHeartRateBpm,
    maxHeartRateBpm,
    averageCadenceRpm,
    averagePowerWatts,
    maxPowerWatts,
    calories,
    description,
    source,
    sourceId,
    createdAt,
    updatedAt,
  ];
  @override
  String get aliasedName => _alias ?? actualTableName;
  @override
  String get actualTableName => $name;
  static const String $name = 'activities';
  @override
  VerificationContext validateIntegrity(
    Insertable<Activity> instance, {
    bool isInserting = false,
  }) {
    final context = VerificationContext();
    final data = instance.toColumns(true);
    if (data.containsKey('id')) {
      context.handle(_idMeta, id.isAcceptableOrUnknown(data['id']!, _idMeta));
    } else if (isInserting) {
      context.missing(_idMeta);
    }
    if (data.containsKey('name')) {
      context.handle(
        _nameMeta,
        name.isAcceptableOrUnknown(data['name']!, _nameMeta),
      );
    } else if (isInserting) {
      context.missing(_nameMeta);
    }
    if (data.containsKey('start_date')) {
      context.handle(
        _startDateMeta,
        startDate.isAcceptableOrUnknown(data['start_date']!, _startDateMeta),
      );
    } else if (isInserting) {
      context.missing(_startDateMeta);
    }
    if (data.containsKey('timezone')) {
      context.handle(
        _timezoneMeta,
        timezone.isAcceptableOrUnknown(data['timezone']!, _timezoneMeta),
      );
    }
    if (data.containsKey('moving_time_seconds')) {
      context.handle(
        _movingTimeSecondsMeta,
        movingTimeSeconds.isAcceptableOrUnknown(
          data['moving_time_seconds']!,
          _movingTimeSecondsMeta,
        ),
      );
    } else if (isInserting) {
      context.missing(_movingTimeSecondsMeta);
    }
    if (data.containsKey('elapsed_time_seconds')) {
      context.handle(
        _elapsedTimeSecondsMeta,
        elapsedTimeSeconds.isAcceptableOrUnknown(
          data['elapsed_time_seconds']!,
          _elapsedTimeSecondsMeta,
        ),
      );
    } else if (isInserting) {
      context.missing(_elapsedTimeSecondsMeta);
    }
    if (data.containsKey('distance_meters')) {
      context.handle(
        _distanceMetersMeta,
        distanceMeters.isAcceptableOrUnknown(
          data['distance_meters']!,
          _distanceMetersMeta,
        ),
      );
    }
    if (data.containsKey('elevation_gain_meters')) {
      context.handle(
        _elevationGainMetersMeta,
        elevationGainMeters.isAcceptableOrUnknown(
          data['elevation_gain_meters']!,
          _elevationGainMetersMeta,
        ),
      );
    }
    if (data.containsKey('elevation_loss_meters')) {
      context.handle(
        _elevationLossMetersMeta,
        elevationLossMeters.isAcceptableOrUnknown(
          data['elevation_loss_meters']!,
          _elevationLossMetersMeta,
        ),
      );
    }
    if (data.containsKey('average_speed_mps')) {
      context.handle(
        _averageSpeedMpsMeta,
        averageSpeedMps.isAcceptableOrUnknown(
          data['average_speed_mps']!,
          _averageSpeedMpsMeta,
        ),
      );
    }
    if (data.containsKey('max_speed_mps')) {
      context.handle(
        _maxSpeedMpsMeta,
        maxSpeedMps.isAcceptableOrUnknown(
          data['max_speed_mps']!,
          _maxSpeedMpsMeta,
        ),
      );
    }
    if (data.containsKey('average_heart_rate_bpm')) {
      context.handle(
        _averageHeartRateBpmMeta,
        averageHeartRateBpm.isAcceptableOrUnknown(
          data['average_heart_rate_bpm']!,
          _averageHeartRateBpmMeta,
        ),
      );
    }
    if (data.containsKey('max_heart_rate_bpm')) {
      context.handle(
        _maxHeartRateBpmMeta,
        maxHeartRateBpm.isAcceptableOrUnknown(
          data['max_heart_rate_bpm']!,
          _maxHeartRateBpmMeta,
        ),
      );
    }
    if (data.containsKey('average_cadence_rpm')) {
      context.handle(
        _averageCadenceRpmMeta,
        averageCadenceRpm.isAcceptableOrUnknown(
          data['average_cadence_rpm']!,
          _averageCadenceRpmMeta,
        ),
      );
    }
    if (data.containsKey('average_power_watts')) {
      context.handle(
        _averagePowerWattsMeta,
        averagePowerWatts.isAcceptableOrUnknown(
          data['average_power_watts']!,
          _averagePowerWattsMeta,
        ),
      );
    }
    if (data.containsKey('max_power_watts')) {
      context.handle(
        _maxPowerWattsMeta,
        maxPowerWatts.isAcceptableOrUnknown(
          data['max_power_watts']!,
          _maxPowerWattsMeta,
        ),
      );
    }
    if (data.containsKey('calories')) {
      context.handle(
        _caloriesMeta,
        calories.isAcceptableOrUnknown(data['calories']!, _caloriesMeta),
      );
    }
    if (data.containsKey('description')) {
      context.handle(
        _descriptionMeta,
        description.isAcceptableOrUnknown(
          data['description']!,
          _descriptionMeta,
        ),
      );
    }
    if (data.containsKey('source')) {
      context.handle(
        _sourceMeta,
        source.isAcceptableOrUnknown(data['source']!, _sourceMeta),
      );
    }
    if (data.containsKey('source_id')) {
      context.handle(
        _sourceIdMeta,
        sourceId.isAcceptableOrUnknown(data['source_id']!, _sourceIdMeta),
      );
    }
    if (data.containsKey('created_at')) {
      context.handle(
        _createdAtMeta,
        createdAt.isAcceptableOrUnknown(data['created_at']!, _createdAtMeta),
      );
    } else if (isInserting) {
      context.missing(_createdAtMeta);
    }
    if (data.containsKey('updated_at')) {
      context.handle(
        _updatedAtMeta,
        updatedAt.isAcceptableOrUnknown(data['updated_at']!, _updatedAtMeta),
      );
    } else if (isInserting) {
      context.missing(_updatedAtMeta);
    }
    return context;
  }

  @override
  Set<GeneratedColumn> get $primaryKey => {id};
  @override
  Activity map(Map<String, dynamic> data, {String? tablePrefix}) {
    final effectivePrefix = tablePrefix != null ? '$tablePrefix.' : '';
    return Activity(
      id: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}id'],
      )!,
      name: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}name'],
      )!,
      sportType: $ActivitiesTable.$convertersportType.fromSql(
        attachedDatabase.typeMapping.read(
          DriftSqlType.int,
          data['${effectivePrefix}sport_type'],
        )!,
      ),
      startDate: attachedDatabase.typeMapping.read(
        DriftSqlType.dateTime,
        data['${effectivePrefix}start_date'],
      )!,
      timezone: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}timezone'],
      ),
      movingTimeSeconds: attachedDatabase.typeMapping.read(
        DriftSqlType.int,
        data['${effectivePrefix}moving_time_seconds'],
      )!,
      elapsedTimeSeconds: attachedDatabase.typeMapping.read(
        DriftSqlType.int,
        data['${effectivePrefix}elapsed_time_seconds'],
      )!,
      distanceMeters: attachedDatabase.typeMapping.read(
        DriftSqlType.double,
        data['${effectivePrefix}distance_meters'],
      ),
      elevationGainMeters: attachedDatabase.typeMapping.read(
        DriftSqlType.double,
        data['${effectivePrefix}elevation_gain_meters'],
      ),
      elevationLossMeters: attachedDatabase.typeMapping.read(
        DriftSqlType.double,
        data['${effectivePrefix}elevation_loss_meters'],
      ),
      averageSpeedMps: attachedDatabase.typeMapping.read(
        DriftSqlType.double,
        data['${effectivePrefix}average_speed_mps'],
      ),
      maxSpeedMps: attachedDatabase.typeMapping.read(
        DriftSqlType.double,
        data['${effectivePrefix}max_speed_mps'],
      ),
      averageHeartRateBpm: attachedDatabase.typeMapping.read(
        DriftSqlType.int,
        data['${effectivePrefix}average_heart_rate_bpm'],
      ),
      maxHeartRateBpm: attachedDatabase.typeMapping.read(
        DriftSqlType.int,
        data['${effectivePrefix}max_heart_rate_bpm'],
      ),
      averageCadenceRpm: attachedDatabase.typeMapping.read(
        DriftSqlType.double,
        data['${effectivePrefix}average_cadence_rpm'],
      ),
      averagePowerWatts: attachedDatabase.typeMapping.read(
        DriftSqlType.int,
        data['${effectivePrefix}average_power_watts'],
      ),
      maxPowerWatts: attachedDatabase.typeMapping.read(
        DriftSqlType.int,
        data['${effectivePrefix}max_power_watts'],
      ),
      calories: attachedDatabase.typeMapping.read(
        DriftSqlType.int,
        data['${effectivePrefix}calories'],
      ),
      description: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}description'],
      ),
      source: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}source'],
      ),
      sourceId: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}source_id'],
      ),
      createdAt: attachedDatabase.typeMapping.read(
        DriftSqlType.dateTime,
        data['${effectivePrefix}created_at'],
      )!,
      updatedAt: attachedDatabase.typeMapping.read(
        DriftSqlType.dateTime,
        data['${effectivePrefix}updated_at'],
      )!,
    );
  }

  @override
  $ActivitiesTable createAlias(String alias) {
    return $ActivitiesTable(attachedDatabase, alias);
  }

  static TypeConverter<SportType, int> $convertersportType =
      const SportTypeConverter();
}

class Activity extends DataClass implements Insertable<Activity> {
  final String id;
  final String name;
  final SportType sportType;
  final DateTime startDate;
  final String? timezone;
  final int movingTimeSeconds;
  final int elapsedTimeSeconds;
  final double? distanceMeters;
  final double? elevationGainMeters;
  final double? elevationLossMeters;
  final double? averageSpeedMps;
  final double? maxSpeedMps;
  final int? averageHeartRateBpm;
  final int? maxHeartRateBpm;
  final double? averageCadenceRpm;
  final int? averagePowerWatts;
  final int? maxPowerWatts;
  final int? calories;
  final String? description;
  final String? source;
  final String? sourceId;
  final DateTime createdAt;
  final DateTime updatedAt;
  const Activity({
    required this.id,
    required this.name,
    required this.sportType,
    required this.startDate,
    this.timezone,
    required this.movingTimeSeconds,
    required this.elapsedTimeSeconds,
    this.distanceMeters,
    this.elevationGainMeters,
    this.elevationLossMeters,
    this.averageSpeedMps,
    this.maxSpeedMps,
    this.averageHeartRateBpm,
    this.maxHeartRateBpm,
    this.averageCadenceRpm,
    this.averagePowerWatts,
    this.maxPowerWatts,
    this.calories,
    this.description,
    this.source,
    this.sourceId,
    required this.createdAt,
    required this.updatedAt,
  });
  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    map['id'] = Variable<String>(id);
    map['name'] = Variable<String>(name);
    {
      map['sport_type'] = Variable<int>(
        $ActivitiesTable.$convertersportType.toSql(sportType),
      );
    }
    map['start_date'] = Variable<DateTime>(startDate);
    if (!nullToAbsent || timezone != null) {
      map['timezone'] = Variable<String>(timezone);
    }
    map['moving_time_seconds'] = Variable<int>(movingTimeSeconds);
    map['elapsed_time_seconds'] = Variable<int>(elapsedTimeSeconds);
    if (!nullToAbsent || distanceMeters != null) {
      map['distance_meters'] = Variable<double>(distanceMeters);
    }
    if (!nullToAbsent || elevationGainMeters != null) {
      map['elevation_gain_meters'] = Variable<double>(elevationGainMeters);
    }
    if (!nullToAbsent || elevationLossMeters != null) {
      map['elevation_loss_meters'] = Variable<double>(elevationLossMeters);
    }
    if (!nullToAbsent || averageSpeedMps != null) {
      map['average_speed_mps'] = Variable<double>(averageSpeedMps);
    }
    if (!nullToAbsent || maxSpeedMps != null) {
      map['max_speed_mps'] = Variable<double>(maxSpeedMps);
    }
    if (!nullToAbsent || averageHeartRateBpm != null) {
      map['average_heart_rate_bpm'] = Variable<int>(averageHeartRateBpm);
    }
    if (!nullToAbsent || maxHeartRateBpm != null) {
      map['max_heart_rate_bpm'] = Variable<int>(maxHeartRateBpm);
    }
    if (!nullToAbsent || averageCadenceRpm != null) {
      map['average_cadence_rpm'] = Variable<double>(averageCadenceRpm);
    }
    if (!nullToAbsent || averagePowerWatts != null) {
      map['average_power_watts'] = Variable<int>(averagePowerWatts);
    }
    if (!nullToAbsent || maxPowerWatts != null) {
      map['max_power_watts'] = Variable<int>(maxPowerWatts);
    }
    if (!nullToAbsent || calories != null) {
      map['calories'] = Variable<int>(calories);
    }
    if (!nullToAbsent || description != null) {
      map['description'] = Variable<String>(description);
    }
    if (!nullToAbsent || source != null) {
      map['source'] = Variable<String>(source);
    }
    if (!nullToAbsent || sourceId != null) {
      map['source_id'] = Variable<String>(sourceId);
    }
    map['created_at'] = Variable<DateTime>(createdAt);
    map['updated_at'] = Variable<DateTime>(updatedAt);
    return map;
  }

  ActivitiesCompanion toCompanion(bool nullToAbsent) {
    return ActivitiesCompanion(
      id: Value(id),
      name: Value(name),
      sportType: Value(sportType),
      startDate: Value(startDate),
      timezone: timezone == null && nullToAbsent
          ? const Value.absent()
          : Value(timezone),
      movingTimeSeconds: Value(movingTimeSeconds),
      elapsedTimeSeconds: Value(elapsedTimeSeconds),
      distanceMeters: distanceMeters == null && nullToAbsent
          ? const Value.absent()
          : Value(distanceMeters),
      elevationGainMeters: elevationGainMeters == null && nullToAbsent
          ? const Value.absent()
          : Value(elevationGainMeters),
      elevationLossMeters: elevationLossMeters == null && nullToAbsent
          ? const Value.absent()
          : Value(elevationLossMeters),
      averageSpeedMps: averageSpeedMps == null && nullToAbsent
          ? const Value.absent()
          : Value(averageSpeedMps),
      maxSpeedMps: maxSpeedMps == null && nullToAbsent
          ? const Value.absent()
          : Value(maxSpeedMps),
      averageHeartRateBpm: averageHeartRateBpm == null && nullToAbsent
          ? const Value.absent()
          : Value(averageHeartRateBpm),
      maxHeartRateBpm: maxHeartRateBpm == null && nullToAbsent
          ? const Value.absent()
          : Value(maxHeartRateBpm),
      averageCadenceRpm: averageCadenceRpm == null && nullToAbsent
          ? const Value.absent()
          : Value(averageCadenceRpm),
      averagePowerWatts: averagePowerWatts == null && nullToAbsent
          ? const Value.absent()
          : Value(averagePowerWatts),
      maxPowerWatts: maxPowerWatts == null && nullToAbsent
          ? const Value.absent()
          : Value(maxPowerWatts),
      calories: calories == null && nullToAbsent
          ? const Value.absent()
          : Value(calories),
      description: description == null && nullToAbsent
          ? const Value.absent()
          : Value(description),
      source: source == null && nullToAbsent
          ? const Value.absent()
          : Value(source),
      sourceId: sourceId == null && nullToAbsent
          ? const Value.absent()
          : Value(sourceId),
      createdAt: Value(createdAt),
      updatedAt: Value(updatedAt),
    );
  }

  factory Activity.fromJson(
    Map<String, dynamic> json, {
    ValueSerializer? serializer,
  }) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return Activity(
      id: serializer.fromJson<String>(json['id']),
      name: serializer.fromJson<String>(json['name']),
      sportType: serializer.fromJson<SportType>(json['sportType']),
      startDate: serializer.fromJson<DateTime>(json['startDate']),
      timezone: serializer.fromJson<String?>(json['timezone']),
      movingTimeSeconds: serializer.fromJson<int>(json['movingTimeSeconds']),
      elapsedTimeSeconds: serializer.fromJson<int>(json['elapsedTimeSeconds']),
      distanceMeters: serializer.fromJson<double?>(json['distanceMeters']),
      elevationGainMeters: serializer.fromJson<double?>(
        json['elevationGainMeters'],
      ),
      elevationLossMeters: serializer.fromJson<double?>(
        json['elevationLossMeters'],
      ),
      averageSpeedMps: serializer.fromJson<double?>(json['averageSpeedMps']),
      maxSpeedMps: serializer.fromJson<double?>(json['maxSpeedMps']),
      averageHeartRateBpm: serializer.fromJson<int?>(
        json['averageHeartRateBpm'],
      ),
      maxHeartRateBpm: serializer.fromJson<int?>(json['maxHeartRateBpm']),
      averageCadenceRpm: serializer.fromJson<double?>(
        json['averageCadenceRpm'],
      ),
      averagePowerWatts: serializer.fromJson<int?>(json['averagePowerWatts']),
      maxPowerWatts: serializer.fromJson<int?>(json['maxPowerWatts']),
      calories: serializer.fromJson<int?>(json['calories']),
      description: serializer.fromJson<String?>(json['description']),
      source: serializer.fromJson<String?>(json['source']),
      sourceId: serializer.fromJson<String?>(json['sourceId']),
      createdAt: serializer.fromJson<DateTime>(json['createdAt']),
      updatedAt: serializer.fromJson<DateTime>(json['updatedAt']),
    );
  }
  @override
  Map<String, dynamic> toJson({ValueSerializer? serializer}) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return <String, dynamic>{
      'id': serializer.toJson<String>(id),
      'name': serializer.toJson<String>(name),
      'sportType': serializer.toJson<SportType>(sportType),
      'startDate': serializer.toJson<DateTime>(startDate),
      'timezone': serializer.toJson<String?>(timezone),
      'movingTimeSeconds': serializer.toJson<int>(movingTimeSeconds),
      'elapsedTimeSeconds': serializer.toJson<int>(elapsedTimeSeconds),
      'distanceMeters': serializer.toJson<double?>(distanceMeters),
      'elevationGainMeters': serializer.toJson<double?>(elevationGainMeters),
      'elevationLossMeters': serializer.toJson<double?>(elevationLossMeters),
      'averageSpeedMps': serializer.toJson<double?>(averageSpeedMps),
      'maxSpeedMps': serializer.toJson<double?>(maxSpeedMps),
      'averageHeartRateBpm': serializer.toJson<int?>(averageHeartRateBpm),
      'maxHeartRateBpm': serializer.toJson<int?>(maxHeartRateBpm),
      'averageCadenceRpm': serializer.toJson<double?>(averageCadenceRpm),
      'averagePowerWatts': serializer.toJson<int?>(averagePowerWatts),
      'maxPowerWatts': serializer.toJson<int?>(maxPowerWatts),
      'calories': serializer.toJson<int?>(calories),
      'description': serializer.toJson<String?>(description),
      'source': serializer.toJson<String?>(source),
      'sourceId': serializer.toJson<String?>(sourceId),
      'createdAt': serializer.toJson<DateTime>(createdAt),
      'updatedAt': serializer.toJson<DateTime>(updatedAt),
    };
  }

  Activity copyWith({
    String? id,
    String? name,
    SportType? sportType,
    DateTime? startDate,
    Value<String?> timezone = const Value.absent(),
    int? movingTimeSeconds,
    int? elapsedTimeSeconds,
    Value<double?> distanceMeters = const Value.absent(),
    Value<double?> elevationGainMeters = const Value.absent(),
    Value<double?> elevationLossMeters = const Value.absent(),
    Value<double?> averageSpeedMps = const Value.absent(),
    Value<double?> maxSpeedMps = const Value.absent(),
    Value<int?> averageHeartRateBpm = const Value.absent(),
    Value<int?> maxHeartRateBpm = const Value.absent(),
    Value<double?> averageCadenceRpm = const Value.absent(),
    Value<int?> averagePowerWatts = const Value.absent(),
    Value<int?> maxPowerWatts = const Value.absent(),
    Value<int?> calories = const Value.absent(),
    Value<String?> description = const Value.absent(),
    Value<String?> source = const Value.absent(),
    Value<String?> sourceId = const Value.absent(),
    DateTime? createdAt,
    DateTime? updatedAt,
  }) => Activity(
    id: id ?? this.id,
    name: name ?? this.name,
    sportType: sportType ?? this.sportType,
    startDate: startDate ?? this.startDate,
    timezone: timezone.present ? timezone.value : this.timezone,
    movingTimeSeconds: movingTimeSeconds ?? this.movingTimeSeconds,
    elapsedTimeSeconds: elapsedTimeSeconds ?? this.elapsedTimeSeconds,
    distanceMeters: distanceMeters.present
        ? distanceMeters.value
        : this.distanceMeters,
    elevationGainMeters: elevationGainMeters.present
        ? elevationGainMeters.value
        : this.elevationGainMeters,
    elevationLossMeters: elevationLossMeters.present
        ? elevationLossMeters.value
        : this.elevationLossMeters,
    averageSpeedMps: averageSpeedMps.present
        ? averageSpeedMps.value
        : this.averageSpeedMps,
    maxSpeedMps: maxSpeedMps.present ? maxSpeedMps.value : this.maxSpeedMps,
    averageHeartRateBpm: averageHeartRateBpm.present
        ? averageHeartRateBpm.value
        : this.averageHeartRateBpm,
    maxHeartRateBpm: maxHeartRateBpm.present
        ? maxHeartRateBpm.value
        : this.maxHeartRateBpm,
    averageCadenceRpm: averageCadenceRpm.present
        ? averageCadenceRpm.value
        : this.averageCadenceRpm,
    averagePowerWatts: averagePowerWatts.present
        ? averagePowerWatts.value
        : this.averagePowerWatts,
    maxPowerWatts: maxPowerWatts.present
        ? maxPowerWatts.value
        : this.maxPowerWatts,
    calories: calories.present ? calories.value : this.calories,
    description: description.present ? description.value : this.description,
    source: source.present ? source.value : this.source,
    sourceId: sourceId.present ? sourceId.value : this.sourceId,
    createdAt: createdAt ?? this.createdAt,
    updatedAt: updatedAt ?? this.updatedAt,
  );
  Activity copyWithCompanion(ActivitiesCompanion data) {
    return Activity(
      id: data.id.present ? data.id.value : this.id,
      name: data.name.present ? data.name.value : this.name,
      sportType: data.sportType.present ? data.sportType.value : this.sportType,
      startDate: data.startDate.present ? data.startDate.value : this.startDate,
      timezone: data.timezone.present ? data.timezone.value : this.timezone,
      movingTimeSeconds: data.movingTimeSeconds.present
          ? data.movingTimeSeconds.value
          : this.movingTimeSeconds,
      elapsedTimeSeconds: data.elapsedTimeSeconds.present
          ? data.elapsedTimeSeconds.value
          : this.elapsedTimeSeconds,
      distanceMeters: data.distanceMeters.present
          ? data.distanceMeters.value
          : this.distanceMeters,
      elevationGainMeters: data.elevationGainMeters.present
          ? data.elevationGainMeters.value
          : this.elevationGainMeters,
      elevationLossMeters: data.elevationLossMeters.present
          ? data.elevationLossMeters.value
          : this.elevationLossMeters,
      averageSpeedMps: data.averageSpeedMps.present
          ? data.averageSpeedMps.value
          : this.averageSpeedMps,
      maxSpeedMps: data.maxSpeedMps.present
          ? data.maxSpeedMps.value
          : this.maxSpeedMps,
      averageHeartRateBpm: data.averageHeartRateBpm.present
          ? data.averageHeartRateBpm.value
          : this.averageHeartRateBpm,
      maxHeartRateBpm: data.maxHeartRateBpm.present
          ? data.maxHeartRateBpm.value
          : this.maxHeartRateBpm,
      averageCadenceRpm: data.averageCadenceRpm.present
          ? data.averageCadenceRpm.value
          : this.averageCadenceRpm,
      averagePowerWatts: data.averagePowerWatts.present
          ? data.averagePowerWatts.value
          : this.averagePowerWatts,
      maxPowerWatts: data.maxPowerWatts.present
          ? data.maxPowerWatts.value
          : this.maxPowerWatts,
      calories: data.calories.present ? data.calories.value : this.calories,
      description: data.description.present
          ? data.description.value
          : this.description,
      source: data.source.present ? data.source.value : this.source,
      sourceId: data.sourceId.present ? data.sourceId.value : this.sourceId,
      createdAt: data.createdAt.present ? data.createdAt.value : this.createdAt,
      updatedAt: data.updatedAt.present ? data.updatedAt.value : this.updatedAt,
    );
  }

  @override
  String toString() {
    return (StringBuffer('Activity(')
          ..write('id: $id, ')
          ..write('name: $name, ')
          ..write('sportType: $sportType, ')
          ..write('startDate: $startDate, ')
          ..write('timezone: $timezone, ')
          ..write('movingTimeSeconds: $movingTimeSeconds, ')
          ..write('elapsedTimeSeconds: $elapsedTimeSeconds, ')
          ..write('distanceMeters: $distanceMeters, ')
          ..write('elevationGainMeters: $elevationGainMeters, ')
          ..write('elevationLossMeters: $elevationLossMeters, ')
          ..write('averageSpeedMps: $averageSpeedMps, ')
          ..write('maxSpeedMps: $maxSpeedMps, ')
          ..write('averageHeartRateBpm: $averageHeartRateBpm, ')
          ..write('maxHeartRateBpm: $maxHeartRateBpm, ')
          ..write('averageCadenceRpm: $averageCadenceRpm, ')
          ..write('averagePowerWatts: $averagePowerWatts, ')
          ..write('maxPowerWatts: $maxPowerWatts, ')
          ..write('calories: $calories, ')
          ..write('description: $description, ')
          ..write('source: $source, ')
          ..write('sourceId: $sourceId, ')
          ..write('createdAt: $createdAt, ')
          ..write('updatedAt: $updatedAt')
          ..write(')'))
        .toString();
  }

  @override
  int get hashCode => Object.hashAll([
    id,
    name,
    sportType,
    startDate,
    timezone,
    movingTimeSeconds,
    elapsedTimeSeconds,
    distanceMeters,
    elevationGainMeters,
    elevationLossMeters,
    averageSpeedMps,
    maxSpeedMps,
    averageHeartRateBpm,
    maxHeartRateBpm,
    averageCadenceRpm,
    averagePowerWatts,
    maxPowerWatts,
    calories,
    description,
    source,
    sourceId,
    createdAt,
    updatedAt,
  ]);
  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      (other is Activity &&
          other.id == this.id &&
          other.name == this.name &&
          other.sportType == this.sportType &&
          other.startDate == this.startDate &&
          other.timezone == this.timezone &&
          other.movingTimeSeconds == this.movingTimeSeconds &&
          other.elapsedTimeSeconds == this.elapsedTimeSeconds &&
          other.distanceMeters == this.distanceMeters &&
          other.elevationGainMeters == this.elevationGainMeters &&
          other.elevationLossMeters == this.elevationLossMeters &&
          other.averageSpeedMps == this.averageSpeedMps &&
          other.maxSpeedMps == this.maxSpeedMps &&
          other.averageHeartRateBpm == this.averageHeartRateBpm &&
          other.maxHeartRateBpm == this.maxHeartRateBpm &&
          other.averageCadenceRpm == this.averageCadenceRpm &&
          other.averagePowerWatts == this.averagePowerWatts &&
          other.maxPowerWatts == this.maxPowerWatts &&
          other.calories == this.calories &&
          other.description == this.description &&
          other.source == this.source &&
          other.sourceId == this.sourceId &&
          other.createdAt == this.createdAt &&
          other.updatedAt == this.updatedAt);
}

class ActivitiesCompanion extends UpdateCompanion<Activity> {
  final Value<String> id;
  final Value<String> name;
  final Value<SportType> sportType;
  final Value<DateTime> startDate;
  final Value<String?> timezone;
  final Value<int> movingTimeSeconds;
  final Value<int> elapsedTimeSeconds;
  final Value<double?> distanceMeters;
  final Value<double?> elevationGainMeters;
  final Value<double?> elevationLossMeters;
  final Value<double?> averageSpeedMps;
  final Value<double?> maxSpeedMps;
  final Value<int?> averageHeartRateBpm;
  final Value<int?> maxHeartRateBpm;
  final Value<double?> averageCadenceRpm;
  final Value<int?> averagePowerWatts;
  final Value<int?> maxPowerWatts;
  final Value<int?> calories;
  final Value<String?> description;
  final Value<String?> source;
  final Value<String?> sourceId;
  final Value<DateTime> createdAt;
  final Value<DateTime> updatedAt;
  final Value<int> rowid;
  const ActivitiesCompanion({
    this.id = const Value.absent(),
    this.name = const Value.absent(),
    this.sportType = const Value.absent(),
    this.startDate = const Value.absent(),
    this.timezone = const Value.absent(),
    this.movingTimeSeconds = const Value.absent(),
    this.elapsedTimeSeconds = const Value.absent(),
    this.distanceMeters = const Value.absent(),
    this.elevationGainMeters = const Value.absent(),
    this.elevationLossMeters = const Value.absent(),
    this.averageSpeedMps = const Value.absent(),
    this.maxSpeedMps = const Value.absent(),
    this.averageHeartRateBpm = const Value.absent(),
    this.maxHeartRateBpm = const Value.absent(),
    this.averageCadenceRpm = const Value.absent(),
    this.averagePowerWatts = const Value.absent(),
    this.maxPowerWatts = const Value.absent(),
    this.calories = const Value.absent(),
    this.description = const Value.absent(),
    this.source = const Value.absent(),
    this.sourceId = const Value.absent(),
    this.createdAt = const Value.absent(),
    this.updatedAt = const Value.absent(),
    this.rowid = const Value.absent(),
  });
  ActivitiesCompanion.insert({
    required String id,
    required String name,
    required SportType sportType,
    required DateTime startDate,
    this.timezone = const Value.absent(),
    required int movingTimeSeconds,
    required int elapsedTimeSeconds,
    this.distanceMeters = const Value.absent(),
    this.elevationGainMeters = const Value.absent(),
    this.elevationLossMeters = const Value.absent(),
    this.averageSpeedMps = const Value.absent(),
    this.maxSpeedMps = const Value.absent(),
    this.averageHeartRateBpm = const Value.absent(),
    this.maxHeartRateBpm = const Value.absent(),
    this.averageCadenceRpm = const Value.absent(),
    this.averagePowerWatts = const Value.absent(),
    this.maxPowerWatts = const Value.absent(),
    this.calories = const Value.absent(),
    this.description = const Value.absent(),
    this.source = const Value.absent(),
    this.sourceId = const Value.absent(),
    required DateTime createdAt,
    required DateTime updatedAt,
    this.rowid = const Value.absent(),
  }) : id = Value(id),
       name = Value(name),
       sportType = Value(sportType),
       startDate = Value(startDate),
       movingTimeSeconds = Value(movingTimeSeconds),
       elapsedTimeSeconds = Value(elapsedTimeSeconds),
       createdAt = Value(createdAt),
       updatedAt = Value(updatedAt);
  static Insertable<Activity> custom({
    Expression<String>? id,
    Expression<String>? name,
    Expression<int>? sportType,
    Expression<DateTime>? startDate,
    Expression<String>? timezone,
    Expression<int>? movingTimeSeconds,
    Expression<int>? elapsedTimeSeconds,
    Expression<double>? distanceMeters,
    Expression<double>? elevationGainMeters,
    Expression<double>? elevationLossMeters,
    Expression<double>? averageSpeedMps,
    Expression<double>? maxSpeedMps,
    Expression<int>? averageHeartRateBpm,
    Expression<int>? maxHeartRateBpm,
    Expression<double>? averageCadenceRpm,
    Expression<int>? averagePowerWatts,
    Expression<int>? maxPowerWatts,
    Expression<int>? calories,
    Expression<String>? description,
    Expression<String>? source,
    Expression<String>? sourceId,
    Expression<DateTime>? createdAt,
    Expression<DateTime>? updatedAt,
    Expression<int>? rowid,
  }) {
    return RawValuesInsertable({
      if (id != null) 'id': id,
      if (name != null) 'name': name,
      if (sportType != null) 'sport_type': sportType,
      if (startDate != null) 'start_date': startDate,
      if (timezone != null) 'timezone': timezone,
      if (movingTimeSeconds != null) 'moving_time_seconds': movingTimeSeconds,
      if (elapsedTimeSeconds != null)
        'elapsed_time_seconds': elapsedTimeSeconds,
      if (distanceMeters != null) 'distance_meters': distanceMeters,
      if (elevationGainMeters != null)
        'elevation_gain_meters': elevationGainMeters,
      if (elevationLossMeters != null)
        'elevation_loss_meters': elevationLossMeters,
      if (averageSpeedMps != null) 'average_speed_mps': averageSpeedMps,
      if (maxSpeedMps != null) 'max_speed_mps': maxSpeedMps,
      if (averageHeartRateBpm != null)
        'average_heart_rate_bpm': averageHeartRateBpm,
      if (maxHeartRateBpm != null) 'max_heart_rate_bpm': maxHeartRateBpm,
      if (averageCadenceRpm != null) 'average_cadence_rpm': averageCadenceRpm,
      if (averagePowerWatts != null) 'average_power_watts': averagePowerWatts,
      if (maxPowerWatts != null) 'max_power_watts': maxPowerWatts,
      if (calories != null) 'calories': calories,
      if (description != null) 'description': description,
      if (source != null) 'source': source,
      if (sourceId != null) 'source_id': sourceId,
      if (createdAt != null) 'created_at': createdAt,
      if (updatedAt != null) 'updated_at': updatedAt,
      if (rowid != null) 'rowid': rowid,
    });
  }

  ActivitiesCompanion copyWith({
    Value<String>? id,
    Value<String>? name,
    Value<SportType>? sportType,
    Value<DateTime>? startDate,
    Value<String?>? timezone,
    Value<int>? movingTimeSeconds,
    Value<int>? elapsedTimeSeconds,
    Value<double?>? distanceMeters,
    Value<double?>? elevationGainMeters,
    Value<double?>? elevationLossMeters,
    Value<double?>? averageSpeedMps,
    Value<double?>? maxSpeedMps,
    Value<int?>? averageHeartRateBpm,
    Value<int?>? maxHeartRateBpm,
    Value<double?>? averageCadenceRpm,
    Value<int?>? averagePowerWatts,
    Value<int?>? maxPowerWatts,
    Value<int?>? calories,
    Value<String?>? description,
    Value<String?>? source,
    Value<String?>? sourceId,
    Value<DateTime>? createdAt,
    Value<DateTime>? updatedAt,
    Value<int>? rowid,
  }) {
    return ActivitiesCompanion(
      id: id ?? this.id,
      name: name ?? this.name,
      sportType: sportType ?? this.sportType,
      startDate: startDate ?? this.startDate,
      timezone: timezone ?? this.timezone,
      movingTimeSeconds: movingTimeSeconds ?? this.movingTimeSeconds,
      elapsedTimeSeconds: elapsedTimeSeconds ?? this.elapsedTimeSeconds,
      distanceMeters: distanceMeters ?? this.distanceMeters,
      elevationGainMeters: elevationGainMeters ?? this.elevationGainMeters,
      elevationLossMeters: elevationLossMeters ?? this.elevationLossMeters,
      averageSpeedMps: averageSpeedMps ?? this.averageSpeedMps,
      maxSpeedMps: maxSpeedMps ?? this.maxSpeedMps,
      averageHeartRateBpm: averageHeartRateBpm ?? this.averageHeartRateBpm,
      maxHeartRateBpm: maxHeartRateBpm ?? this.maxHeartRateBpm,
      averageCadenceRpm: averageCadenceRpm ?? this.averageCadenceRpm,
      averagePowerWatts: averagePowerWatts ?? this.averagePowerWatts,
      maxPowerWatts: maxPowerWatts ?? this.maxPowerWatts,
      calories: calories ?? this.calories,
      description: description ?? this.description,
      source: source ?? this.source,
      sourceId: sourceId ?? this.sourceId,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
      rowid: rowid ?? this.rowid,
    );
  }

  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    if (id.present) {
      map['id'] = Variable<String>(id.value);
    }
    if (name.present) {
      map['name'] = Variable<String>(name.value);
    }
    if (sportType.present) {
      map['sport_type'] = Variable<int>(
        $ActivitiesTable.$convertersportType.toSql(sportType.value),
      );
    }
    if (startDate.present) {
      map['start_date'] = Variable<DateTime>(startDate.value);
    }
    if (timezone.present) {
      map['timezone'] = Variable<String>(timezone.value);
    }
    if (movingTimeSeconds.present) {
      map['moving_time_seconds'] = Variable<int>(movingTimeSeconds.value);
    }
    if (elapsedTimeSeconds.present) {
      map['elapsed_time_seconds'] = Variable<int>(elapsedTimeSeconds.value);
    }
    if (distanceMeters.present) {
      map['distance_meters'] = Variable<double>(distanceMeters.value);
    }
    if (elevationGainMeters.present) {
      map['elevation_gain_meters'] = Variable<double>(
        elevationGainMeters.value,
      );
    }
    if (elevationLossMeters.present) {
      map['elevation_loss_meters'] = Variable<double>(
        elevationLossMeters.value,
      );
    }
    if (averageSpeedMps.present) {
      map['average_speed_mps'] = Variable<double>(averageSpeedMps.value);
    }
    if (maxSpeedMps.present) {
      map['max_speed_mps'] = Variable<double>(maxSpeedMps.value);
    }
    if (averageHeartRateBpm.present) {
      map['average_heart_rate_bpm'] = Variable<int>(averageHeartRateBpm.value);
    }
    if (maxHeartRateBpm.present) {
      map['max_heart_rate_bpm'] = Variable<int>(maxHeartRateBpm.value);
    }
    if (averageCadenceRpm.present) {
      map['average_cadence_rpm'] = Variable<double>(averageCadenceRpm.value);
    }
    if (averagePowerWatts.present) {
      map['average_power_watts'] = Variable<int>(averagePowerWatts.value);
    }
    if (maxPowerWatts.present) {
      map['max_power_watts'] = Variable<int>(maxPowerWatts.value);
    }
    if (calories.present) {
      map['calories'] = Variable<int>(calories.value);
    }
    if (description.present) {
      map['description'] = Variable<String>(description.value);
    }
    if (source.present) {
      map['source'] = Variable<String>(source.value);
    }
    if (sourceId.present) {
      map['source_id'] = Variable<String>(sourceId.value);
    }
    if (createdAt.present) {
      map['created_at'] = Variable<DateTime>(createdAt.value);
    }
    if (updatedAt.present) {
      map['updated_at'] = Variable<DateTime>(updatedAt.value);
    }
    if (rowid.present) {
      map['rowid'] = Variable<int>(rowid.value);
    }
    return map;
  }

  @override
  String toString() {
    return (StringBuffer('ActivitiesCompanion(')
          ..write('id: $id, ')
          ..write('name: $name, ')
          ..write('sportType: $sportType, ')
          ..write('startDate: $startDate, ')
          ..write('timezone: $timezone, ')
          ..write('movingTimeSeconds: $movingTimeSeconds, ')
          ..write('elapsedTimeSeconds: $elapsedTimeSeconds, ')
          ..write('distanceMeters: $distanceMeters, ')
          ..write('elevationGainMeters: $elevationGainMeters, ')
          ..write('elevationLossMeters: $elevationLossMeters, ')
          ..write('averageSpeedMps: $averageSpeedMps, ')
          ..write('maxSpeedMps: $maxSpeedMps, ')
          ..write('averageHeartRateBpm: $averageHeartRateBpm, ')
          ..write('maxHeartRateBpm: $maxHeartRateBpm, ')
          ..write('averageCadenceRpm: $averageCadenceRpm, ')
          ..write('averagePowerWatts: $averagePowerWatts, ')
          ..write('maxPowerWatts: $maxPowerWatts, ')
          ..write('calories: $calories, ')
          ..write('description: $description, ')
          ..write('source: $source, ')
          ..write('sourceId: $sourceId, ')
          ..write('createdAt: $createdAt, ')
          ..write('updatedAt: $updatedAt, ')
          ..write('rowid: $rowid')
          ..write(')'))
        .toString();
  }
}

class $ActivityStreamsTable extends ActivityStreams
    with TableInfo<$ActivityStreamsTable, ActivityStream> {
  @override
  final GeneratedDatabase attachedDatabase;
  final String? _alias;
  $ActivityStreamsTable(this.attachedDatabase, [this._alias]);
  static const VerificationMeta _activityIdMeta = const VerificationMeta(
    'activityId',
  );
  @override
  late final GeneratedColumn<String> activityId = GeneratedColumn<String>(
    'activity_id',
    aliasedName,
    false,
    type: DriftSqlType.string,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _timeOffsetSecondsMeta = const VerificationMeta(
    'timeOffsetSeconds',
  );
  @override
  late final GeneratedColumn<int> timeOffsetSeconds = GeneratedColumn<int>(
    'time_offset_seconds',
    aliasedName,
    false,
    type: DriftSqlType.int,
    requiredDuringInsert: true,
  );
  static const VerificationMeta _latitudeMeta = const VerificationMeta(
    'latitude',
  );
  @override
  late final GeneratedColumn<double> latitude = GeneratedColumn<double>(
    'latitude',
    aliasedName,
    true,
    type: DriftSqlType.double,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _longitudeMeta = const VerificationMeta(
    'longitude',
  );
  @override
  late final GeneratedColumn<double> longitude = GeneratedColumn<double>(
    'longitude',
    aliasedName,
    true,
    type: DriftSqlType.double,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _altitudeMetersMeta = const VerificationMeta(
    'altitudeMeters',
  );
  @override
  late final GeneratedColumn<double> altitudeMeters = GeneratedColumn<double>(
    'altitude_meters',
    aliasedName,
    true,
    type: DriftSqlType.double,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _heartRateBpmMeta = const VerificationMeta(
    'heartRateBpm',
  );
  @override
  late final GeneratedColumn<int> heartRateBpm = GeneratedColumn<int>(
    'heart_rate_bpm',
    aliasedName,
    true,
    type: DriftSqlType.int,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _cadenceRpmMeta = const VerificationMeta(
    'cadenceRpm',
  );
  @override
  late final GeneratedColumn<double> cadenceRpm = GeneratedColumn<double>(
    'cadence_rpm',
    aliasedName,
    true,
    type: DriftSqlType.double,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _powerWattsMeta = const VerificationMeta(
    'powerWatts',
  );
  @override
  late final GeneratedColumn<int> powerWatts = GeneratedColumn<int>(
    'power_watts',
    aliasedName,
    true,
    type: DriftSqlType.int,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _speedMpsMeta = const VerificationMeta(
    'speedMps',
  );
  @override
  late final GeneratedColumn<double> speedMps = GeneratedColumn<double>(
    'speed_mps',
    aliasedName,
    true,
    type: DriftSqlType.double,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _gradeMeta = const VerificationMeta('grade');
  @override
  late final GeneratedColumn<double> grade = GeneratedColumn<double>(
    'grade',
    aliasedName,
    true,
    type: DriftSqlType.double,
    requiredDuringInsert: false,
  );
  static const VerificationMeta _distanceMetersMeta = const VerificationMeta(
    'distanceMeters',
  );
  @override
  late final GeneratedColumn<double> distanceMeters = GeneratedColumn<double>(
    'distance_meters',
    aliasedName,
    true,
    type: DriftSqlType.double,
    requiredDuringInsert: false,
  );
  @override
  List<GeneratedColumn> get $columns => [
    activityId,
    timeOffsetSeconds,
    latitude,
    longitude,
    altitudeMeters,
    heartRateBpm,
    cadenceRpm,
    powerWatts,
    speedMps,
    grade,
    distanceMeters,
  ];
  @override
  String get aliasedName => _alias ?? actualTableName;
  @override
  String get actualTableName => $name;
  static const String $name = 'activity_streams';
  @override
  VerificationContext validateIntegrity(
    Insertable<ActivityStream> instance, {
    bool isInserting = false,
  }) {
    final context = VerificationContext();
    final data = instance.toColumns(true);
    if (data.containsKey('activity_id')) {
      context.handle(
        _activityIdMeta,
        activityId.isAcceptableOrUnknown(data['activity_id']!, _activityIdMeta),
      );
    } else if (isInserting) {
      context.missing(_activityIdMeta);
    }
    if (data.containsKey('time_offset_seconds')) {
      context.handle(
        _timeOffsetSecondsMeta,
        timeOffsetSeconds.isAcceptableOrUnknown(
          data['time_offset_seconds']!,
          _timeOffsetSecondsMeta,
        ),
      );
    } else if (isInserting) {
      context.missing(_timeOffsetSecondsMeta);
    }
    if (data.containsKey('latitude')) {
      context.handle(
        _latitudeMeta,
        latitude.isAcceptableOrUnknown(data['latitude']!, _latitudeMeta),
      );
    }
    if (data.containsKey('longitude')) {
      context.handle(
        _longitudeMeta,
        longitude.isAcceptableOrUnknown(data['longitude']!, _longitudeMeta),
      );
    }
    if (data.containsKey('altitude_meters')) {
      context.handle(
        _altitudeMetersMeta,
        altitudeMeters.isAcceptableOrUnknown(
          data['altitude_meters']!,
          _altitudeMetersMeta,
        ),
      );
    }
    if (data.containsKey('heart_rate_bpm')) {
      context.handle(
        _heartRateBpmMeta,
        heartRateBpm.isAcceptableOrUnknown(
          data['heart_rate_bpm']!,
          _heartRateBpmMeta,
        ),
      );
    }
    if (data.containsKey('cadence_rpm')) {
      context.handle(
        _cadenceRpmMeta,
        cadenceRpm.isAcceptableOrUnknown(data['cadence_rpm']!, _cadenceRpmMeta),
      );
    }
    if (data.containsKey('power_watts')) {
      context.handle(
        _powerWattsMeta,
        powerWatts.isAcceptableOrUnknown(data['power_watts']!, _powerWattsMeta),
      );
    }
    if (data.containsKey('speed_mps')) {
      context.handle(
        _speedMpsMeta,
        speedMps.isAcceptableOrUnknown(data['speed_mps']!, _speedMpsMeta),
      );
    }
    if (data.containsKey('grade')) {
      context.handle(
        _gradeMeta,
        grade.isAcceptableOrUnknown(data['grade']!, _gradeMeta),
      );
    }
    if (data.containsKey('distance_meters')) {
      context.handle(
        _distanceMetersMeta,
        distanceMeters.isAcceptableOrUnknown(
          data['distance_meters']!,
          _distanceMetersMeta,
        ),
      );
    }
    return context;
  }

  @override
  Set<GeneratedColumn> get $primaryKey => {activityId, timeOffsetSeconds};
  @override
  ActivityStream map(Map<String, dynamic> data, {String? tablePrefix}) {
    final effectivePrefix = tablePrefix != null ? '$tablePrefix.' : '';
    return ActivityStream(
      activityId: attachedDatabase.typeMapping.read(
        DriftSqlType.string,
        data['${effectivePrefix}activity_id'],
      )!,
      timeOffsetSeconds: attachedDatabase.typeMapping.read(
        DriftSqlType.int,
        data['${effectivePrefix}time_offset_seconds'],
      )!,
      latitude: attachedDatabase.typeMapping.read(
        DriftSqlType.double,
        data['${effectivePrefix}latitude'],
      ),
      longitude: attachedDatabase.typeMapping.read(
        DriftSqlType.double,
        data['${effectivePrefix}longitude'],
      ),
      altitudeMeters: attachedDatabase.typeMapping.read(
        DriftSqlType.double,
        data['${effectivePrefix}altitude_meters'],
      ),
      heartRateBpm: attachedDatabase.typeMapping.read(
        DriftSqlType.int,
        data['${effectivePrefix}heart_rate_bpm'],
      ),
      cadenceRpm: attachedDatabase.typeMapping.read(
        DriftSqlType.double,
        data['${effectivePrefix}cadence_rpm'],
      ),
      powerWatts: attachedDatabase.typeMapping.read(
        DriftSqlType.int,
        data['${effectivePrefix}power_watts'],
      ),
      speedMps: attachedDatabase.typeMapping.read(
        DriftSqlType.double,
        data['${effectivePrefix}speed_mps'],
      ),
      grade: attachedDatabase.typeMapping.read(
        DriftSqlType.double,
        data['${effectivePrefix}grade'],
      ),
      distanceMeters: attachedDatabase.typeMapping.read(
        DriftSqlType.double,
        data['${effectivePrefix}distance_meters'],
      ),
    );
  }

  @override
  $ActivityStreamsTable createAlias(String alias) {
    return $ActivityStreamsTable(attachedDatabase, alias);
  }
}

class ActivityStream extends DataClass implements Insertable<ActivityStream> {
  final String activityId;
  final int timeOffsetSeconds;
  final double? latitude;
  final double? longitude;
  final double? altitudeMeters;
  final int? heartRateBpm;
  final double? cadenceRpm;
  final int? powerWatts;
  final double? speedMps;
  final double? grade;
  final double? distanceMeters;
  const ActivityStream({
    required this.activityId,
    required this.timeOffsetSeconds,
    this.latitude,
    this.longitude,
    this.altitudeMeters,
    this.heartRateBpm,
    this.cadenceRpm,
    this.powerWatts,
    this.speedMps,
    this.grade,
    this.distanceMeters,
  });
  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    map['activity_id'] = Variable<String>(activityId);
    map['time_offset_seconds'] = Variable<int>(timeOffsetSeconds);
    if (!nullToAbsent || latitude != null) {
      map['latitude'] = Variable<double>(latitude);
    }
    if (!nullToAbsent || longitude != null) {
      map['longitude'] = Variable<double>(longitude);
    }
    if (!nullToAbsent || altitudeMeters != null) {
      map['altitude_meters'] = Variable<double>(altitudeMeters);
    }
    if (!nullToAbsent || heartRateBpm != null) {
      map['heart_rate_bpm'] = Variable<int>(heartRateBpm);
    }
    if (!nullToAbsent || cadenceRpm != null) {
      map['cadence_rpm'] = Variable<double>(cadenceRpm);
    }
    if (!nullToAbsent || powerWatts != null) {
      map['power_watts'] = Variable<int>(powerWatts);
    }
    if (!nullToAbsent || speedMps != null) {
      map['speed_mps'] = Variable<double>(speedMps);
    }
    if (!nullToAbsent || grade != null) {
      map['grade'] = Variable<double>(grade);
    }
    if (!nullToAbsent || distanceMeters != null) {
      map['distance_meters'] = Variable<double>(distanceMeters);
    }
    return map;
  }

  ActivityStreamsCompanion toCompanion(bool nullToAbsent) {
    return ActivityStreamsCompanion(
      activityId: Value(activityId),
      timeOffsetSeconds: Value(timeOffsetSeconds),
      latitude: latitude == null && nullToAbsent
          ? const Value.absent()
          : Value(latitude),
      longitude: longitude == null && nullToAbsent
          ? const Value.absent()
          : Value(longitude),
      altitudeMeters: altitudeMeters == null && nullToAbsent
          ? const Value.absent()
          : Value(altitudeMeters),
      heartRateBpm: heartRateBpm == null && nullToAbsent
          ? const Value.absent()
          : Value(heartRateBpm),
      cadenceRpm: cadenceRpm == null && nullToAbsent
          ? const Value.absent()
          : Value(cadenceRpm),
      powerWatts: powerWatts == null && nullToAbsent
          ? const Value.absent()
          : Value(powerWatts),
      speedMps: speedMps == null && nullToAbsent
          ? const Value.absent()
          : Value(speedMps),
      grade: grade == null && nullToAbsent
          ? const Value.absent()
          : Value(grade),
      distanceMeters: distanceMeters == null && nullToAbsent
          ? const Value.absent()
          : Value(distanceMeters),
    );
  }

  factory ActivityStream.fromJson(
    Map<String, dynamic> json, {
    ValueSerializer? serializer,
  }) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return ActivityStream(
      activityId: serializer.fromJson<String>(json['activityId']),
      timeOffsetSeconds: serializer.fromJson<int>(json['timeOffsetSeconds']),
      latitude: serializer.fromJson<double?>(json['latitude']),
      longitude: serializer.fromJson<double?>(json['longitude']),
      altitudeMeters: serializer.fromJson<double?>(json['altitudeMeters']),
      heartRateBpm: serializer.fromJson<int?>(json['heartRateBpm']),
      cadenceRpm: serializer.fromJson<double?>(json['cadenceRpm']),
      powerWatts: serializer.fromJson<int?>(json['powerWatts']),
      speedMps: serializer.fromJson<double?>(json['speedMps']),
      grade: serializer.fromJson<double?>(json['grade']),
      distanceMeters: serializer.fromJson<double?>(json['distanceMeters']),
    );
  }
  @override
  Map<String, dynamic> toJson({ValueSerializer? serializer}) {
    serializer ??= driftRuntimeOptions.defaultSerializer;
    return <String, dynamic>{
      'activityId': serializer.toJson<String>(activityId),
      'timeOffsetSeconds': serializer.toJson<int>(timeOffsetSeconds),
      'latitude': serializer.toJson<double?>(latitude),
      'longitude': serializer.toJson<double?>(longitude),
      'altitudeMeters': serializer.toJson<double?>(altitudeMeters),
      'heartRateBpm': serializer.toJson<int?>(heartRateBpm),
      'cadenceRpm': serializer.toJson<double?>(cadenceRpm),
      'powerWatts': serializer.toJson<int?>(powerWatts),
      'speedMps': serializer.toJson<double?>(speedMps),
      'grade': serializer.toJson<double?>(grade),
      'distanceMeters': serializer.toJson<double?>(distanceMeters),
    };
  }

  ActivityStream copyWith({
    String? activityId,
    int? timeOffsetSeconds,
    Value<double?> latitude = const Value.absent(),
    Value<double?> longitude = const Value.absent(),
    Value<double?> altitudeMeters = const Value.absent(),
    Value<int?> heartRateBpm = const Value.absent(),
    Value<double?> cadenceRpm = const Value.absent(),
    Value<int?> powerWatts = const Value.absent(),
    Value<double?> speedMps = const Value.absent(),
    Value<double?> grade = const Value.absent(),
    Value<double?> distanceMeters = const Value.absent(),
  }) => ActivityStream(
    activityId: activityId ?? this.activityId,
    timeOffsetSeconds: timeOffsetSeconds ?? this.timeOffsetSeconds,
    latitude: latitude.present ? latitude.value : this.latitude,
    longitude: longitude.present ? longitude.value : this.longitude,
    altitudeMeters: altitudeMeters.present
        ? altitudeMeters.value
        : this.altitudeMeters,
    heartRateBpm: heartRateBpm.present ? heartRateBpm.value : this.heartRateBpm,
    cadenceRpm: cadenceRpm.present ? cadenceRpm.value : this.cadenceRpm,
    powerWatts: powerWatts.present ? powerWatts.value : this.powerWatts,
    speedMps: speedMps.present ? speedMps.value : this.speedMps,
    grade: grade.present ? grade.value : this.grade,
    distanceMeters: distanceMeters.present
        ? distanceMeters.value
        : this.distanceMeters,
  );
  ActivityStream copyWithCompanion(ActivityStreamsCompanion data) {
    return ActivityStream(
      activityId: data.activityId.present
          ? data.activityId.value
          : this.activityId,
      timeOffsetSeconds: data.timeOffsetSeconds.present
          ? data.timeOffsetSeconds.value
          : this.timeOffsetSeconds,
      latitude: data.latitude.present ? data.latitude.value : this.latitude,
      longitude: data.longitude.present ? data.longitude.value : this.longitude,
      altitudeMeters: data.altitudeMeters.present
          ? data.altitudeMeters.value
          : this.altitudeMeters,
      heartRateBpm: data.heartRateBpm.present
          ? data.heartRateBpm.value
          : this.heartRateBpm,
      cadenceRpm: data.cadenceRpm.present
          ? data.cadenceRpm.value
          : this.cadenceRpm,
      powerWatts: data.powerWatts.present
          ? data.powerWatts.value
          : this.powerWatts,
      speedMps: data.speedMps.present ? data.speedMps.value : this.speedMps,
      grade: data.grade.present ? data.grade.value : this.grade,
      distanceMeters: data.distanceMeters.present
          ? data.distanceMeters.value
          : this.distanceMeters,
    );
  }

  @override
  String toString() {
    return (StringBuffer('ActivityStream(')
          ..write('activityId: $activityId, ')
          ..write('timeOffsetSeconds: $timeOffsetSeconds, ')
          ..write('latitude: $latitude, ')
          ..write('longitude: $longitude, ')
          ..write('altitudeMeters: $altitudeMeters, ')
          ..write('heartRateBpm: $heartRateBpm, ')
          ..write('cadenceRpm: $cadenceRpm, ')
          ..write('powerWatts: $powerWatts, ')
          ..write('speedMps: $speedMps, ')
          ..write('grade: $grade, ')
          ..write('distanceMeters: $distanceMeters')
          ..write(')'))
        .toString();
  }

  @override
  int get hashCode => Object.hash(
    activityId,
    timeOffsetSeconds,
    latitude,
    longitude,
    altitudeMeters,
    heartRateBpm,
    cadenceRpm,
    powerWatts,
    speedMps,
    grade,
    distanceMeters,
  );
  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      (other is ActivityStream &&
          other.activityId == this.activityId &&
          other.timeOffsetSeconds == this.timeOffsetSeconds &&
          other.latitude == this.latitude &&
          other.longitude == this.longitude &&
          other.altitudeMeters == this.altitudeMeters &&
          other.heartRateBpm == this.heartRateBpm &&
          other.cadenceRpm == this.cadenceRpm &&
          other.powerWatts == this.powerWatts &&
          other.speedMps == this.speedMps &&
          other.grade == this.grade &&
          other.distanceMeters == this.distanceMeters);
}

class ActivityStreamsCompanion extends UpdateCompanion<ActivityStream> {
  final Value<String> activityId;
  final Value<int> timeOffsetSeconds;
  final Value<double?> latitude;
  final Value<double?> longitude;
  final Value<double?> altitudeMeters;
  final Value<int?> heartRateBpm;
  final Value<double?> cadenceRpm;
  final Value<int?> powerWatts;
  final Value<double?> speedMps;
  final Value<double?> grade;
  final Value<double?> distanceMeters;
  final Value<int> rowid;
  const ActivityStreamsCompanion({
    this.activityId = const Value.absent(),
    this.timeOffsetSeconds = const Value.absent(),
    this.latitude = const Value.absent(),
    this.longitude = const Value.absent(),
    this.altitudeMeters = const Value.absent(),
    this.heartRateBpm = const Value.absent(),
    this.cadenceRpm = const Value.absent(),
    this.powerWatts = const Value.absent(),
    this.speedMps = const Value.absent(),
    this.grade = const Value.absent(),
    this.distanceMeters = const Value.absent(),
    this.rowid = const Value.absent(),
  });
  ActivityStreamsCompanion.insert({
    required String activityId,
    required int timeOffsetSeconds,
    this.latitude = const Value.absent(),
    this.longitude = const Value.absent(),
    this.altitudeMeters = const Value.absent(),
    this.heartRateBpm = const Value.absent(),
    this.cadenceRpm = const Value.absent(),
    this.powerWatts = const Value.absent(),
    this.speedMps = const Value.absent(),
    this.grade = const Value.absent(),
    this.distanceMeters = const Value.absent(),
    this.rowid = const Value.absent(),
  }) : activityId = Value(activityId),
       timeOffsetSeconds = Value(timeOffsetSeconds);
  static Insertable<ActivityStream> custom({
    Expression<String>? activityId,
    Expression<int>? timeOffsetSeconds,
    Expression<double>? latitude,
    Expression<double>? longitude,
    Expression<double>? altitudeMeters,
    Expression<int>? heartRateBpm,
    Expression<double>? cadenceRpm,
    Expression<int>? powerWatts,
    Expression<double>? speedMps,
    Expression<double>? grade,
    Expression<double>? distanceMeters,
    Expression<int>? rowid,
  }) {
    return RawValuesInsertable({
      if (activityId != null) 'activity_id': activityId,
      if (timeOffsetSeconds != null) 'time_offset_seconds': timeOffsetSeconds,
      if (latitude != null) 'latitude': latitude,
      if (longitude != null) 'longitude': longitude,
      if (altitudeMeters != null) 'altitude_meters': altitudeMeters,
      if (heartRateBpm != null) 'heart_rate_bpm': heartRateBpm,
      if (cadenceRpm != null) 'cadence_rpm': cadenceRpm,
      if (powerWatts != null) 'power_watts': powerWatts,
      if (speedMps != null) 'speed_mps': speedMps,
      if (grade != null) 'grade': grade,
      if (distanceMeters != null) 'distance_meters': distanceMeters,
      if (rowid != null) 'rowid': rowid,
    });
  }

  ActivityStreamsCompanion copyWith({
    Value<String>? activityId,
    Value<int>? timeOffsetSeconds,
    Value<double?>? latitude,
    Value<double?>? longitude,
    Value<double?>? altitudeMeters,
    Value<int?>? heartRateBpm,
    Value<double?>? cadenceRpm,
    Value<int?>? powerWatts,
    Value<double?>? speedMps,
    Value<double?>? grade,
    Value<double?>? distanceMeters,
    Value<int>? rowid,
  }) {
    return ActivityStreamsCompanion(
      activityId: activityId ?? this.activityId,
      timeOffsetSeconds: timeOffsetSeconds ?? this.timeOffsetSeconds,
      latitude: latitude ?? this.latitude,
      longitude: longitude ?? this.longitude,
      altitudeMeters: altitudeMeters ?? this.altitudeMeters,
      heartRateBpm: heartRateBpm ?? this.heartRateBpm,
      cadenceRpm: cadenceRpm ?? this.cadenceRpm,
      powerWatts: powerWatts ?? this.powerWatts,
      speedMps: speedMps ?? this.speedMps,
      grade: grade ?? this.grade,
      distanceMeters: distanceMeters ?? this.distanceMeters,
      rowid: rowid ?? this.rowid,
    );
  }

  @override
  Map<String, Expression> toColumns(bool nullToAbsent) {
    final map = <String, Expression>{};
    if (activityId.present) {
      map['activity_id'] = Variable<String>(activityId.value);
    }
    if (timeOffsetSeconds.present) {
      map['time_offset_seconds'] = Variable<int>(timeOffsetSeconds.value);
    }
    if (latitude.present) {
      map['latitude'] = Variable<double>(latitude.value);
    }
    if (longitude.present) {
      map['longitude'] = Variable<double>(longitude.value);
    }
    if (altitudeMeters.present) {
      map['altitude_meters'] = Variable<double>(altitudeMeters.value);
    }
    if (heartRateBpm.present) {
      map['heart_rate_bpm'] = Variable<int>(heartRateBpm.value);
    }
    if (cadenceRpm.present) {
      map['cadence_rpm'] = Variable<double>(cadenceRpm.value);
    }
    if (powerWatts.present) {
      map['power_watts'] = Variable<int>(powerWatts.value);
    }
    if (speedMps.present) {
      map['speed_mps'] = Variable<double>(speedMps.value);
    }
    if (grade.present) {
      map['grade'] = Variable<double>(grade.value);
    }
    if (distanceMeters.present) {
      map['distance_meters'] = Variable<double>(distanceMeters.value);
    }
    if (rowid.present) {
      map['rowid'] = Variable<int>(rowid.value);
    }
    return map;
  }

  @override
  String toString() {
    return (StringBuffer('ActivityStreamsCompanion(')
          ..write('activityId: $activityId, ')
          ..write('timeOffsetSeconds: $timeOffsetSeconds, ')
          ..write('latitude: $latitude, ')
          ..write('longitude: $longitude, ')
          ..write('altitudeMeters: $altitudeMeters, ')
          ..write('heartRateBpm: $heartRateBpm, ')
          ..write('cadenceRpm: $cadenceRpm, ')
          ..write('powerWatts: $powerWatts, ')
          ..write('speedMps: $speedMps, ')
          ..write('grade: $grade, ')
          ..write('distanceMeters: $distanceMeters, ')
          ..write('rowid: $rowid')
          ..write(')'))
        .toString();
  }
}

abstract class _$AppDatabase extends GeneratedDatabase {
  _$AppDatabase(QueryExecutor e) : super(e);
  $AppDatabaseManager get managers => $AppDatabaseManager(this);
  late final $ActivitiesTable activities = $ActivitiesTable(this);
  late final $ActivityStreamsTable activityStreams = $ActivityStreamsTable(
    this,
  );
  @override
  Iterable<TableInfo<Table, Object?>> get allTables =>
      allSchemaEntities.whereType<TableInfo<Table, Object?>>();
  @override
  List<DatabaseSchemaEntity> get allSchemaEntities => [
    activities,
    activityStreams,
  ];
}

typedef $$ActivitiesTableCreateCompanionBuilder =
    ActivitiesCompanion Function({
      required String id,
      required String name,
      required SportType sportType,
      required DateTime startDate,
      Value<String?> timezone,
      required int movingTimeSeconds,
      required int elapsedTimeSeconds,
      Value<double?> distanceMeters,
      Value<double?> elevationGainMeters,
      Value<double?> elevationLossMeters,
      Value<double?> averageSpeedMps,
      Value<double?> maxSpeedMps,
      Value<int?> averageHeartRateBpm,
      Value<int?> maxHeartRateBpm,
      Value<double?> averageCadenceRpm,
      Value<int?> averagePowerWatts,
      Value<int?> maxPowerWatts,
      Value<int?> calories,
      Value<String?> description,
      Value<String?> source,
      Value<String?> sourceId,
      required DateTime createdAt,
      required DateTime updatedAt,
      Value<int> rowid,
    });
typedef $$ActivitiesTableUpdateCompanionBuilder =
    ActivitiesCompanion Function({
      Value<String> id,
      Value<String> name,
      Value<SportType> sportType,
      Value<DateTime> startDate,
      Value<String?> timezone,
      Value<int> movingTimeSeconds,
      Value<int> elapsedTimeSeconds,
      Value<double?> distanceMeters,
      Value<double?> elevationGainMeters,
      Value<double?> elevationLossMeters,
      Value<double?> averageSpeedMps,
      Value<double?> maxSpeedMps,
      Value<int?> averageHeartRateBpm,
      Value<int?> maxHeartRateBpm,
      Value<double?> averageCadenceRpm,
      Value<int?> averagePowerWatts,
      Value<int?> maxPowerWatts,
      Value<int?> calories,
      Value<String?> description,
      Value<String?> source,
      Value<String?> sourceId,
      Value<DateTime> createdAt,
      Value<DateTime> updatedAt,
      Value<int> rowid,
    });

class $$ActivitiesTableFilterComposer
    extends Composer<_$AppDatabase, $ActivitiesTable> {
  $$ActivitiesTableFilterComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnFilters<String> get id => $composableBuilder(
    column: $table.id,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get name => $composableBuilder(
    column: $table.name,
    builder: (column) => ColumnFilters(column),
  );

  ColumnWithTypeConverterFilters<SportType, SportType, int> get sportType =>
      $composableBuilder(
        column: $table.sportType,
        builder: (column) => ColumnWithTypeConverterFilters(column),
      );

  ColumnFilters<DateTime> get startDate => $composableBuilder(
    column: $table.startDate,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get timezone => $composableBuilder(
    column: $table.timezone,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<int> get movingTimeSeconds => $composableBuilder(
    column: $table.movingTimeSeconds,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<int> get elapsedTimeSeconds => $composableBuilder(
    column: $table.elapsedTimeSeconds,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<double> get distanceMeters => $composableBuilder(
    column: $table.distanceMeters,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<double> get elevationGainMeters => $composableBuilder(
    column: $table.elevationGainMeters,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<double> get elevationLossMeters => $composableBuilder(
    column: $table.elevationLossMeters,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<double> get averageSpeedMps => $composableBuilder(
    column: $table.averageSpeedMps,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<double> get maxSpeedMps => $composableBuilder(
    column: $table.maxSpeedMps,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<int> get averageHeartRateBpm => $composableBuilder(
    column: $table.averageHeartRateBpm,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<int> get maxHeartRateBpm => $composableBuilder(
    column: $table.maxHeartRateBpm,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<double> get averageCadenceRpm => $composableBuilder(
    column: $table.averageCadenceRpm,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<int> get averagePowerWatts => $composableBuilder(
    column: $table.averagePowerWatts,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<int> get maxPowerWatts => $composableBuilder(
    column: $table.maxPowerWatts,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<int> get calories => $composableBuilder(
    column: $table.calories,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get description => $composableBuilder(
    column: $table.description,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get source => $composableBuilder(
    column: $table.source,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<String> get sourceId => $composableBuilder(
    column: $table.sourceId,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<DateTime> get createdAt => $composableBuilder(
    column: $table.createdAt,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<DateTime> get updatedAt => $composableBuilder(
    column: $table.updatedAt,
    builder: (column) => ColumnFilters(column),
  );
}

class $$ActivitiesTableOrderingComposer
    extends Composer<_$AppDatabase, $ActivitiesTable> {
  $$ActivitiesTableOrderingComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnOrderings<String> get id => $composableBuilder(
    column: $table.id,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get name => $composableBuilder(
    column: $table.name,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<int> get sportType => $composableBuilder(
    column: $table.sportType,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<DateTime> get startDate => $composableBuilder(
    column: $table.startDate,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get timezone => $composableBuilder(
    column: $table.timezone,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<int> get movingTimeSeconds => $composableBuilder(
    column: $table.movingTimeSeconds,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<int> get elapsedTimeSeconds => $composableBuilder(
    column: $table.elapsedTimeSeconds,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<double> get distanceMeters => $composableBuilder(
    column: $table.distanceMeters,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<double> get elevationGainMeters => $composableBuilder(
    column: $table.elevationGainMeters,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<double> get elevationLossMeters => $composableBuilder(
    column: $table.elevationLossMeters,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<double> get averageSpeedMps => $composableBuilder(
    column: $table.averageSpeedMps,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<double> get maxSpeedMps => $composableBuilder(
    column: $table.maxSpeedMps,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<int> get averageHeartRateBpm => $composableBuilder(
    column: $table.averageHeartRateBpm,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<int> get maxHeartRateBpm => $composableBuilder(
    column: $table.maxHeartRateBpm,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<double> get averageCadenceRpm => $composableBuilder(
    column: $table.averageCadenceRpm,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<int> get averagePowerWatts => $composableBuilder(
    column: $table.averagePowerWatts,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<int> get maxPowerWatts => $composableBuilder(
    column: $table.maxPowerWatts,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<int> get calories => $composableBuilder(
    column: $table.calories,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get description => $composableBuilder(
    column: $table.description,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get source => $composableBuilder(
    column: $table.source,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<String> get sourceId => $composableBuilder(
    column: $table.sourceId,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<DateTime> get createdAt => $composableBuilder(
    column: $table.createdAt,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<DateTime> get updatedAt => $composableBuilder(
    column: $table.updatedAt,
    builder: (column) => ColumnOrderings(column),
  );
}

class $$ActivitiesTableAnnotationComposer
    extends Composer<_$AppDatabase, $ActivitiesTable> {
  $$ActivitiesTableAnnotationComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  GeneratedColumn<String> get id =>
      $composableBuilder(column: $table.id, builder: (column) => column);

  GeneratedColumn<String> get name =>
      $composableBuilder(column: $table.name, builder: (column) => column);

  GeneratedColumnWithTypeConverter<SportType, int> get sportType =>
      $composableBuilder(column: $table.sportType, builder: (column) => column);

  GeneratedColumn<DateTime> get startDate =>
      $composableBuilder(column: $table.startDate, builder: (column) => column);

  GeneratedColumn<String> get timezone =>
      $composableBuilder(column: $table.timezone, builder: (column) => column);

  GeneratedColumn<int> get movingTimeSeconds => $composableBuilder(
    column: $table.movingTimeSeconds,
    builder: (column) => column,
  );

  GeneratedColumn<int> get elapsedTimeSeconds => $composableBuilder(
    column: $table.elapsedTimeSeconds,
    builder: (column) => column,
  );

  GeneratedColumn<double> get distanceMeters => $composableBuilder(
    column: $table.distanceMeters,
    builder: (column) => column,
  );

  GeneratedColumn<double> get elevationGainMeters => $composableBuilder(
    column: $table.elevationGainMeters,
    builder: (column) => column,
  );

  GeneratedColumn<double> get elevationLossMeters => $composableBuilder(
    column: $table.elevationLossMeters,
    builder: (column) => column,
  );

  GeneratedColumn<double> get averageSpeedMps => $composableBuilder(
    column: $table.averageSpeedMps,
    builder: (column) => column,
  );

  GeneratedColumn<double> get maxSpeedMps => $composableBuilder(
    column: $table.maxSpeedMps,
    builder: (column) => column,
  );

  GeneratedColumn<int> get averageHeartRateBpm => $composableBuilder(
    column: $table.averageHeartRateBpm,
    builder: (column) => column,
  );

  GeneratedColumn<int> get maxHeartRateBpm => $composableBuilder(
    column: $table.maxHeartRateBpm,
    builder: (column) => column,
  );

  GeneratedColumn<double> get averageCadenceRpm => $composableBuilder(
    column: $table.averageCadenceRpm,
    builder: (column) => column,
  );

  GeneratedColumn<int> get averagePowerWatts => $composableBuilder(
    column: $table.averagePowerWatts,
    builder: (column) => column,
  );

  GeneratedColumn<int> get maxPowerWatts => $composableBuilder(
    column: $table.maxPowerWatts,
    builder: (column) => column,
  );

  GeneratedColumn<int> get calories =>
      $composableBuilder(column: $table.calories, builder: (column) => column);

  GeneratedColumn<String> get description => $composableBuilder(
    column: $table.description,
    builder: (column) => column,
  );

  GeneratedColumn<String> get source =>
      $composableBuilder(column: $table.source, builder: (column) => column);

  GeneratedColumn<String> get sourceId =>
      $composableBuilder(column: $table.sourceId, builder: (column) => column);

  GeneratedColumn<DateTime> get createdAt =>
      $composableBuilder(column: $table.createdAt, builder: (column) => column);

  GeneratedColumn<DateTime> get updatedAt =>
      $composableBuilder(column: $table.updatedAt, builder: (column) => column);
}

class $$ActivitiesTableTableManager
    extends
        RootTableManager<
          _$AppDatabase,
          $ActivitiesTable,
          Activity,
          $$ActivitiesTableFilterComposer,
          $$ActivitiesTableOrderingComposer,
          $$ActivitiesTableAnnotationComposer,
          $$ActivitiesTableCreateCompanionBuilder,
          $$ActivitiesTableUpdateCompanionBuilder,
          (Activity, BaseReferences<_$AppDatabase, $ActivitiesTable, Activity>),
          Activity,
          PrefetchHooks Function()
        > {
  $$ActivitiesTableTableManager(_$AppDatabase db, $ActivitiesTable table)
    : super(
        TableManagerState(
          db: db,
          table: table,
          createFilteringComposer: () =>
              $$ActivitiesTableFilterComposer($db: db, $table: table),
          createOrderingComposer: () =>
              $$ActivitiesTableOrderingComposer($db: db, $table: table),
          createComputedFieldComposer: () =>
              $$ActivitiesTableAnnotationComposer($db: db, $table: table),
          updateCompanionCallback:
              ({
                Value<String> id = const Value.absent(),
                Value<String> name = const Value.absent(),
                Value<SportType> sportType = const Value.absent(),
                Value<DateTime> startDate = const Value.absent(),
                Value<String?> timezone = const Value.absent(),
                Value<int> movingTimeSeconds = const Value.absent(),
                Value<int> elapsedTimeSeconds = const Value.absent(),
                Value<double?> distanceMeters = const Value.absent(),
                Value<double?> elevationGainMeters = const Value.absent(),
                Value<double?> elevationLossMeters = const Value.absent(),
                Value<double?> averageSpeedMps = const Value.absent(),
                Value<double?> maxSpeedMps = const Value.absent(),
                Value<int?> averageHeartRateBpm = const Value.absent(),
                Value<int?> maxHeartRateBpm = const Value.absent(),
                Value<double?> averageCadenceRpm = const Value.absent(),
                Value<int?> averagePowerWatts = const Value.absent(),
                Value<int?> maxPowerWatts = const Value.absent(),
                Value<int?> calories = const Value.absent(),
                Value<String?> description = const Value.absent(),
                Value<String?> source = const Value.absent(),
                Value<String?> sourceId = const Value.absent(),
                Value<DateTime> createdAt = const Value.absent(),
                Value<DateTime> updatedAt = const Value.absent(),
                Value<int> rowid = const Value.absent(),
              }) => ActivitiesCompanion(
                id: id,
                name: name,
                sportType: sportType,
                startDate: startDate,
                timezone: timezone,
                movingTimeSeconds: movingTimeSeconds,
                elapsedTimeSeconds: elapsedTimeSeconds,
                distanceMeters: distanceMeters,
                elevationGainMeters: elevationGainMeters,
                elevationLossMeters: elevationLossMeters,
                averageSpeedMps: averageSpeedMps,
                maxSpeedMps: maxSpeedMps,
                averageHeartRateBpm: averageHeartRateBpm,
                maxHeartRateBpm: maxHeartRateBpm,
                averageCadenceRpm: averageCadenceRpm,
                averagePowerWatts: averagePowerWatts,
                maxPowerWatts: maxPowerWatts,
                calories: calories,
                description: description,
                source: source,
                sourceId: sourceId,
                createdAt: createdAt,
                updatedAt: updatedAt,
                rowid: rowid,
              ),
          createCompanionCallback:
              ({
                required String id,
                required String name,
                required SportType sportType,
                required DateTime startDate,
                Value<String?> timezone = const Value.absent(),
                required int movingTimeSeconds,
                required int elapsedTimeSeconds,
                Value<double?> distanceMeters = const Value.absent(),
                Value<double?> elevationGainMeters = const Value.absent(),
                Value<double?> elevationLossMeters = const Value.absent(),
                Value<double?> averageSpeedMps = const Value.absent(),
                Value<double?> maxSpeedMps = const Value.absent(),
                Value<int?> averageHeartRateBpm = const Value.absent(),
                Value<int?> maxHeartRateBpm = const Value.absent(),
                Value<double?> averageCadenceRpm = const Value.absent(),
                Value<int?> averagePowerWatts = const Value.absent(),
                Value<int?> maxPowerWatts = const Value.absent(),
                Value<int?> calories = const Value.absent(),
                Value<String?> description = const Value.absent(),
                Value<String?> source = const Value.absent(),
                Value<String?> sourceId = const Value.absent(),
                required DateTime createdAt,
                required DateTime updatedAt,
                Value<int> rowid = const Value.absent(),
              }) => ActivitiesCompanion.insert(
                id: id,
                name: name,
                sportType: sportType,
                startDate: startDate,
                timezone: timezone,
                movingTimeSeconds: movingTimeSeconds,
                elapsedTimeSeconds: elapsedTimeSeconds,
                distanceMeters: distanceMeters,
                elevationGainMeters: elevationGainMeters,
                elevationLossMeters: elevationLossMeters,
                averageSpeedMps: averageSpeedMps,
                maxSpeedMps: maxSpeedMps,
                averageHeartRateBpm: averageHeartRateBpm,
                maxHeartRateBpm: maxHeartRateBpm,
                averageCadenceRpm: averageCadenceRpm,
                averagePowerWatts: averagePowerWatts,
                maxPowerWatts: maxPowerWatts,
                calories: calories,
                description: description,
                source: source,
                sourceId: sourceId,
                createdAt: createdAt,
                updatedAt: updatedAt,
                rowid: rowid,
              ),
          withReferenceMapper: (p0) => p0
              .map((e) => (e.readTable(table), BaseReferences(db, table, e)))
              .toList(),
          prefetchHooksCallback: null,
        ),
      );
}

typedef $$ActivitiesTableProcessedTableManager =
    ProcessedTableManager<
      _$AppDatabase,
      $ActivitiesTable,
      Activity,
      $$ActivitiesTableFilterComposer,
      $$ActivitiesTableOrderingComposer,
      $$ActivitiesTableAnnotationComposer,
      $$ActivitiesTableCreateCompanionBuilder,
      $$ActivitiesTableUpdateCompanionBuilder,
      (Activity, BaseReferences<_$AppDatabase, $ActivitiesTable, Activity>),
      Activity,
      PrefetchHooks Function()
    >;
typedef $$ActivityStreamsTableCreateCompanionBuilder =
    ActivityStreamsCompanion Function({
      required String activityId,
      required int timeOffsetSeconds,
      Value<double?> latitude,
      Value<double?> longitude,
      Value<double?> altitudeMeters,
      Value<int?> heartRateBpm,
      Value<double?> cadenceRpm,
      Value<int?> powerWatts,
      Value<double?> speedMps,
      Value<double?> grade,
      Value<double?> distanceMeters,
      Value<int> rowid,
    });
typedef $$ActivityStreamsTableUpdateCompanionBuilder =
    ActivityStreamsCompanion Function({
      Value<String> activityId,
      Value<int> timeOffsetSeconds,
      Value<double?> latitude,
      Value<double?> longitude,
      Value<double?> altitudeMeters,
      Value<int?> heartRateBpm,
      Value<double?> cadenceRpm,
      Value<int?> powerWatts,
      Value<double?> speedMps,
      Value<double?> grade,
      Value<double?> distanceMeters,
      Value<int> rowid,
    });

class $$ActivityStreamsTableFilterComposer
    extends Composer<_$AppDatabase, $ActivityStreamsTable> {
  $$ActivityStreamsTableFilterComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnFilters<String> get activityId => $composableBuilder(
    column: $table.activityId,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<int> get timeOffsetSeconds => $composableBuilder(
    column: $table.timeOffsetSeconds,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<double> get latitude => $composableBuilder(
    column: $table.latitude,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<double> get longitude => $composableBuilder(
    column: $table.longitude,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<double> get altitudeMeters => $composableBuilder(
    column: $table.altitudeMeters,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<int> get heartRateBpm => $composableBuilder(
    column: $table.heartRateBpm,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<double> get cadenceRpm => $composableBuilder(
    column: $table.cadenceRpm,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<int> get powerWatts => $composableBuilder(
    column: $table.powerWatts,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<double> get speedMps => $composableBuilder(
    column: $table.speedMps,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<double> get grade => $composableBuilder(
    column: $table.grade,
    builder: (column) => ColumnFilters(column),
  );

  ColumnFilters<double> get distanceMeters => $composableBuilder(
    column: $table.distanceMeters,
    builder: (column) => ColumnFilters(column),
  );
}

class $$ActivityStreamsTableOrderingComposer
    extends Composer<_$AppDatabase, $ActivityStreamsTable> {
  $$ActivityStreamsTableOrderingComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  ColumnOrderings<String> get activityId => $composableBuilder(
    column: $table.activityId,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<int> get timeOffsetSeconds => $composableBuilder(
    column: $table.timeOffsetSeconds,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<double> get latitude => $composableBuilder(
    column: $table.latitude,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<double> get longitude => $composableBuilder(
    column: $table.longitude,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<double> get altitudeMeters => $composableBuilder(
    column: $table.altitudeMeters,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<int> get heartRateBpm => $composableBuilder(
    column: $table.heartRateBpm,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<double> get cadenceRpm => $composableBuilder(
    column: $table.cadenceRpm,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<int> get powerWatts => $composableBuilder(
    column: $table.powerWatts,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<double> get speedMps => $composableBuilder(
    column: $table.speedMps,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<double> get grade => $composableBuilder(
    column: $table.grade,
    builder: (column) => ColumnOrderings(column),
  );

  ColumnOrderings<double> get distanceMeters => $composableBuilder(
    column: $table.distanceMeters,
    builder: (column) => ColumnOrderings(column),
  );
}

class $$ActivityStreamsTableAnnotationComposer
    extends Composer<_$AppDatabase, $ActivityStreamsTable> {
  $$ActivityStreamsTableAnnotationComposer({
    required super.$db,
    required super.$table,
    super.joinBuilder,
    super.$addJoinBuilderToRootComposer,
    super.$removeJoinBuilderFromRootComposer,
  });
  GeneratedColumn<String> get activityId => $composableBuilder(
    column: $table.activityId,
    builder: (column) => column,
  );

  GeneratedColumn<int> get timeOffsetSeconds => $composableBuilder(
    column: $table.timeOffsetSeconds,
    builder: (column) => column,
  );

  GeneratedColumn<double> get latitude =>
      $composableBuilder(column: $table.latitude, builder: (column) => column);

  GeneratedColumn<double> get longitude =>
      $composableBuilder(column: $table.longitude, builder: (column) => column);

  GeneratedColumn<double> get altitudeMeters => $composableBuilder(
    column: $table.altitudeMeters,
    builder: (column) => column,
  );

  GeneratedColumn<int> get heartRateBpm => $composableBuilder(
    column: $table.heartRateBpm,
    builder: (column) => column,
  );

  GeneratedColumn<double> get cadenceRpm => $composableBuilder(
    column: $table.cadenceRpm,
    builder: (column) => column,
  );

  GeneratedColumn<int> get powerWatts => $composableBuilder(
    column: $table.powerWatts,
    builder: (column) => column,
  );

  GeneratedColumn<double> get speedMps =>
      $composableBuilder(column: $table.speedMps, builder: (column) => column);

  GeneratedColumn<double> get grade =>
      $composableBuilder(column: $table.grade, builder: (column) => column);

  GeneratedColumn<double> get distanceMeters => $composableBuilder(
    column: $table.distanceMeters,
    builder: (column) => column,
  );
}

class $$ActivityStreamsTableTableManager
    extends
        RootTableManager<
          _$AppDatabase,
          $ActivityStreamsTable,
          ActivityStream,
          $$ActivityStreamsTableFilterComposer,
          $$ActivityStreamsTableOrderingComposer,
          $$ActivityStreamsTableAnnotationComposer,
          $$ActivityStreamsTableCreateCompanionBuilder,
          $$ActivityStreamsTableUpdateCompanionBuilder,
          (
            ActivityStream,
            BaseReferences<
              _$AppDatabase,
              $ActivityStreamsTable,
              ActivityStream
            >,
          ),
          ActivityStream,
          PrefetchHooks Function()
        > {
  $$ActivityStreamsTableTableManager(
    _$AppDatabase db,
    $ActivityStreamsTable table,
  ) : super(
        TableManagerState(
          db: db,
          table: table,
          createFilteringComposer: () =>
              $$ActivityStreamsTableFilterComposer($db: db, $table: table),
          createOrderingComposer: () =>
              $$ActivityStreamsTableOrderingComposer($db: db, $table: table),
          createComputedFieldComposer: () =>
              $$ActivityStreamsTableAnnotationComposer($db: db, $table: table),
          updateCompanionCallback:
              ({
                Value<String> activityId = const Value.absent(),
                Value<int> timeOffsetSeconds = const Value.absent(),
                Value<double?> latitude = const Value.absent(),
                Value<double?> longitude = const Value.absent(),
                Value<double?> altitudeMeters = const Value.absent(),
                Value<int?> heartRateBpm = const Value.absent(),
                Value<double?> cadenceRpm = const Value.absent(),
                Value<int?> powerWatts = const Value.absent(),
                Value<double?> speedMps = const Value.absent(),
                Value<double?> grade = const Value.absent(),
                Value<double?> distanceMeters = const Value.absent(),
                Value<int> rowid = const Value.absent(),
              }) => ActivityStreamsCompanion(
                activityId: activityId,
                timeOffsetSeconds: timeOffsetSeconds,
                latitude: latitude,
                longitude: longitude,
                altitudeMeters: altitudeMeters,
                heartRateBpm: heartRateBpm,
                cadenceRpm: cadenceRpm,
                powerWatts: powerWatts,
                speedMps: speedMps,
                grade: grade,
                distanceMeters: distanceMeters,
                rowid: rowid,
              ),
          createCompanionCallback:
              ({
                required String activityId,
                required int timeOffsetSeconds,
                Value<double?> latitude = const Value.absent(),
                Value<double?> longitude = const Value.absent(),
                Value<double?> altitudeMeters = const Value.absent(),
                Value<int?> heartRateBpm = const Value.absent(),
                Value<double?> cadenceRpm = const Value.absent(),
                Value<int?> powerWatts = const Value.absent(),
                Value<double?> speedMps = const Value.absent(),
                Value<double?> grade = const Value.absent(),
                Value<double?> distanceMeters = const Value.absent(),
                Value<int> rowid = const Value.absent(),
              }) => ActivityStreamsCompanion.insert(
                activityId: activityId,
                timeOffsetSeconds: timeOffsetSeconds,
                latitude: latitude,
                longitude: longitude,
                altitudeMeters: altitudeMeters,
                heartRateBpm: heartRateBpm,
                cadenceRpm: cadenceRpm,
                powerWatts: powerWatts,
                speedMps: speedMps,
                grade: grade,
                distanceMeters: distanceMeters,
                rowid: rowid,
              ),
          withReferenceMapper: (p0) => p0
              .map((e) => (e.readTable(table), BaseReferences(db, table, e)))
              .toList(),
          prefetchHooksCallback: null,
        ),
      );
}

typedef $$ActivityStreamsTableProcessedTableManager =
    ProcessedTableManager<
      _$AppDatabase,
      $ActivityStreamsTable,
      ActivityStream,
      $$ActivityStreamsTableFilterComposer,
      $$ActivityStreamsTableOrderingComposer,
      $$ActivityStreamsTableAnnotationComposer,
      $$ActivityStreamsTableCreateCompanionBuilder,
      $$ActivityStreamsTableUpdateCompanionBuilder,
      (
        ActivityStream,
        BaseReferences<_$AppDatabase, $ActivityStreamsTable, ActivityStream>,
      ),
      ActivityStream,
      PrefetchHooks Function()
    >;

class $AppDatabaseManager {
  final _$AppDatabase _db;
  $AppDatabaseManager(this._db);
  $$ActivitiesTableTableManager get activities =>
      $$ActivitiesTableTableManager(_db, _db.activities);
  $$ActivityStreamsTableTableManager get activityStreams =>
      $$ActivityStreamsTableTableManager(_db, _db.activityStreams);
}
