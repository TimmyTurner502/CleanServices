package com.sjocol.cleanservices.presentation.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjocol.cleanservices.data.repository.HouseRepository
import com.sjocol.cleanservices.data.repository.WorkEntryRepository
import com.sjocol.cleanservices.domain.model.WorkEntry
import com.sjocol.cleanservices.domain.model.WorkType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class GlobalEventsViewModel @Inject constructor(
    private val houseRepo: HouseRepository,
    private val entryRepo: WorkEntryRepository
) : ViewModel() {

    private val _filterType = MutableStateFlow<WorkType?>(null)
    val filterType: StateFlow<WorkType?> = _filterType

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth

    data class Item(val entry: WorkEntry, val houseName: String)

    private val monthEntries = _currentMonth.flatMapLatest { ym ->
        val from = ym.atDay(1).toString()
        val to = ym.atEndOfMonth().toString()
        entryRepo.observeByDateRange(from, to)
    }

    val items: StateFlow<List<Item>> = combine(
        monthEntries,
        houseRepo.observeAllSorted()
    ) { entries, houses ->
        val houseMap = houses.associateBy({ it.id }, { it.name })
        entries.sortedByDescending { it.dateIso }
            .map { e -> Item(e, houseMap[e.houseId] ?: "?") }
    }.combine(_filterType) { list: List<Item>, type ->
        if (type == null) list else list.filter { it.entry.type == type }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(type: WorkType?) { _filterType.value = type }
    fun nextMonth() { _currentMonth.value = _currentMonth.value.plusMonths(1) }
    fun prevMonth() { _currentMonth.value = _currentMonth.value.minusMonths(1) }
} 