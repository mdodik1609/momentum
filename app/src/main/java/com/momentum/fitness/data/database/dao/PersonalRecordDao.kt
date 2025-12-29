package com.momentum.fitness.data.database.dao

import androidx.room.*
import com.momentum.fitness.data.database.entity.PersonalRecordEntity
import com.momentum.fitness.data.model.SportType
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalRecordDao {
    @Query("SELECT * FROM personal_records ORDER BY achievedAt DESC")
    fun getAllRecords(): Flow<List<PersonalRecordEntity>>

    @Query("SELECT * FROM personal_records WHERE sportType = :sportType ORDER BY achievedAt DESC")
    fun getRecordsBySportType(sportType: SportType): Flow<List<PersonalRecordEntity>>

    @Query("SELECT * FROM personal_records WHERE recordType = :recordType AND sportType = :sportType ORDER BY value ASC LIMIT 1")
    suspend fun getBestRecord(recordType: String, sportType: SportType): PersonalRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: PersonalRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<PersonalRecordEntity>)

    @Delete
    suspend fun deleteRecord(record: PersonalRecordEntity)
}


