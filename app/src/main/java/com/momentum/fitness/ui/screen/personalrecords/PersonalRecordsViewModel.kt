package com.momentum.fitness.ui.screen.personalrecords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.fitness.data.database.entity.PersonalRecordEntity
import com.momentum.fitness.data.model.SportType
import com.momentum.fitness.data.repository.PersonalRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class PersonalRecordsViewModel @Inject constructor(
    private val personalRecordRepository: PersonalRecordRepository
) : ViewModel() {

    val allRecords: StateFlow<List<PersonalRecordEntity>> = personalRecordRepository.getAllRecords()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getRecordsBySport(sportType: SportType): Flow<List<PersonalRecordEntity>> {
        return personalRecordRepository.getRecordsBySportType(sportType)
    }
}
