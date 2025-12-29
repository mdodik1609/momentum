package com.momentum.fitness.data.database.converter

import androidx.room.TypeConverter
import com.momentum.fitness.data.model.SportType

class SportTypeConverter {
    @TypeConverter
    fun fromSportType(value: SportType): String {
        return value.name
    }

    @TypeConverter
    fun toSportType(value: String): SportType {
        return SportType.valueOf(value)
    }
}







