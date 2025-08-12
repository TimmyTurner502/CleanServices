package com.sjocol.cleanservices.presentation.events

import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class WorkEntryFormViewModel @Inject constructor(
    private val houseRepo: HouseRepository,
    private val entryRepo: WorkEntryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val houses = houseRepo.observeAllSorted().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _houseId = MutableStateFlow<Long?>(savedStateHandle.get<Long>("houseId")?.takeIf { it > 0 })
    val houseId: StateFlow<Long?> = _houseId.asStateFlow()

    private val _type = MutableStateFlow(WorkType.LIMPIEZA)
    val type: StateFlow<WorkType> = _type.asStateFlow()

    private val _dateIso = MutableStateFlow(LocalDate.now().toString())
    val dateIso: StateFlow<String> = _dateIso.asStateFlow()

    // Por defecto, hora actual (minutos, sin segundos)
    private val _startTime = MutableStateFlow<String?>(LocalTime.now().withSecond(0).withNano(0).toString())
    val startTime: StateFlow<String?> = _startTime.asStateFlow()
    private val _endTime = MutableStateFlow<String?>(null)
    val endTime: StateFlow<String?> = _endTime.asStateFlow()
    private val _startChanged = MutableStateFlow(false)
    private val _endChanged = MutableStateFlow(false)

    private val _peopleCount = MutableStateFlow(1)
    val peopleCount: StateFlow<Int> = _peopleCount.asStateFlow()

    // Lista de nombres para UI; se guardará como CSV
    private val _peopleNamesList = MutableStateFlow(listOf(""))
    val peopleNamesList: StateFlow<List<String>> = _peopleNamesList.asStateFlow()

    fun setHouseId(id: Long) { _houseId.value = id }
    fun setType(t: WorkType) { _type.value = t }
    fun setDate(iso: String) { _dateIso.value = iso }
    fun setStart(time: String?) { _startTime.value = time; _startChanged.value = true }
    fun setEnd(time: String?) { _endTime.value = time; _endChanged.value = true }
    fun setPeopleCount(c: Int) { _peopleCount.value = c }

    fun setPersonAt(index: Int, value: String) {
        val list = _peopleNamesList.value.toMutableList()
        if (index in list.indices) {
            list[index] = value
            _peopleNamesList.value = list
        }
    }

    fun addPersonField() { _peopleNamesList.value = _peopleNamesList.value + "" }
    fun removePersonAt(index: Int) {
        val list = _peopleNamesList.value.toMutableList()
        if (list.size > 1 && index in list.indices) {
            list.removeAt(index)
            _peopleNamesList.value = list
        }
    }

    fun startStopTimer() {
        if (_startTime.value.isNullOrBlank()) {
            _startTime.value = LocalTime.now().withSecond(0).withNano(0).toString(); _startChanged.value = true
        } else if (_endTime.value.isNullOrBlank()) {
            _endTime.value = LocalTime.now().withSecond(0).withNano(0).toString(); _endChanged.value = true
        } else {
            _startTime.value = null
            _endTime.value = null
            _startChanged.value = false
            _endChanged.value = false
        }
    }

    fun save(onDone: () -> Unit) {
        val hid = _houseId.value ?: return
        viewModelScope.launch {
            val csv = _peopleNamesList.value.map { it.trim() }.filter { it.isNotEmpty() }.joinToString(",")
            val entry = WorkEntry(
                houseId = hid,
                type = _type.value,
                dateIso = _dateIso.value,
                startTime = if (_startChanged.value) _startTime.value else null,
                endTime = if (_endChanged.value) _endTime.value else null,
                peopleCount = _peopleCount.value,
                peopleNamesCsv = csv.ifBlank { null },
                notes = null
            )
            entryRepo.upsert(entry)
            onDone()
        }
    }
} 