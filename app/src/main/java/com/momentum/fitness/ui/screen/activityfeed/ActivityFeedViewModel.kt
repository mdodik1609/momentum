package com.momentum.fitness.ui.screen.activityfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.fitness.data.database.entity.ActivityEntity
import com.momentum.fitness.data.model.SportType
import com.momentum.fitness.data.repository.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.Instant
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ActivityFeedViewModel @Inject constructor(
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sportTypeFilter = MutableStateFlow<SportType?>(null)
    val sportTypeFilter: StateFlow<SportType?> = _sportTypeFilter.asStateFlow()

    private val _dateRangeFilter = MutableStateFlow<Pair<Instant, Instant>?>(null)
    val dateRangeFilter: StateFlow<Pair<Instant, Instant>?> = _dateRangeFilter.asStateFlow()

    val activities: StateFlow<List<ActivityEntity>> = combine(
        _searchQuery,
        _sportTypeFilter,
        _dateRangeFilter
    ) { query, sportType, dateRange ->
        Triple(
            query.takeIf { it.isNotBlank() },
            sportType,
            dateRange
        )
    }.flatMapLatest { (query, sportType, dateRange) ->
        try {
            activityRepository.searchActivities(
                query = query,
                sportType = sportType,
                startDate = dateRange?.first,
                endDate = dateRange?.second
            ).catch { e ->
                emit(emptyList())
            }
        } catch (e: Exception) {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSportTypeFilter(sportType: SportType?) {
        _sportTypeFilter.value = sportType
    }

    fun setDateRangeFilter(startDate: Instant?, endDate: Instant?) {
        _dateRangeFilter.value = if (startDate != null && endDate != null) {
            Pair(startDate, endDate)
        } else {
            null
        }
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _sportTypeFilter.value = null
        _dateRangeFilter.value = null
    }
}
