package com.momentum.fitness.data.repository

import com.momentum.fitness.data.database.dao.PersonalRecordDao
import com.momentum.fitness.data.database.entity.PersonalRecordEntity
import com.momentum.fitness.data.model.SportType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonalRecordRepository @Inject constructor(
    private val personalRecordDao: PersonalRecordDao
) {
    fun getAllRecords(): Flow<List<PersonalRecordEntity>> = personalRecordDao.getAllRecords()

    fun getRecordsBySportType(sportType: SportType): Flow<List<PersonalRecordEntity>> =
        personalRecordDao.getRecordsBySportType(sportType)

    suspend fun getBestRecord(recordType: String, sportType: SportType): PersonalRecordEntity? =
        personalRecordDao.getBestRecord(recordType, sportType)

    suspend fun insertRecord(record: PersonalRecordEntity) = personalRecordDao.insertRecord(record)

    suspend fun insertRecords(records: List<PersonalRecordEntity>) = personalRecordDao.insertRecords(records)
}







