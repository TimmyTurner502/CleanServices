package com.sjocol.cleanservices.presentation.house

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjocol.cleanservices.data.repository.HouseRepository
import com.sjocol.cleanservices.data.repository.WorkEntryRepository
import com.sjocol.cleanservices.domain.model.House
import com.sjocol.cleanservices.domain.model.WorkEntry
import com.sjocol.cleanservices.domain.model.WorkType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class HouseDetailViewModel @Inject constructor(
    private val houseRepo: HouseRepository,
    private val entryRepo: WorkEntryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val houseId: Long = savedStateHandle.get<Long>("houseId") ?: 0L
    private val _house = MutableStateFlow<House?>(null)
    val house: StateFlow<House?> = _house.asStateFlow()

    private val _filterType = MutableStateFlow<WorkType?>(null) // null = todos
    val filterType: StateFlow<WorkType?> = _filterType.asStateFlow()

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    val entries: StateFlow<List<WorkEntry>> = _currentMonth
        .flatMapLatest { ym ->
            val from = ym.atDay(1).toString()
            val to = ym.atEndOfMonth().toString()
            entryRepo.observeByHouseAndDateRange(houseId, from, to)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            _house.value = houseRepo.getById(houseId)
        }
    }

    fun setFilter(type: WorkType?) { _filterType.value = type }
    fun nextMonth() { _currentMonth.value = _currentMonth.value.plusMonths(1) }
    fun prevMonth() { _currentMonth.value = _currentMonth.value.minusMonths(1) }
} 