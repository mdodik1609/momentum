package com.momentum.fitness.data.repository

import com.momentum.fitness.data.database.dao.UserSettingsDao
import com.momentum.fitness.data.database.entity.UserSettingsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSettingsRepository @Inject constructor(
    private val userSettingsDao: UserSettingsDao
) {
    fun getSettings(): Flow<UserSettingsEntity?> = userSettingsDao.getSettings()

    suspend fun getSettingsSync(): UserSettingsEntity {
        return userSettingsDao.getSettingsSync() ?: run {
            val default = UserSettingsEntity()
            userSettingsDao.insertSettings(default)
            default
        }
    }

    suspend fun updateSettings(settings: UserSettingsEntity) = userSettingsDao.updateSettings(settings)

    suspend fun insertSettings(settings: UserSettingsEntity) = userSettingsDao.insertSettings(settings)
}







