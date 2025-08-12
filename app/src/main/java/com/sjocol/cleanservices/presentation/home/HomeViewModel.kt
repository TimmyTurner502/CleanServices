package com.sjocol.cleanservices.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjocol.cleanservices.data.repository.HouseRepository
import com.sjocol.cleanservices.domain.model.House
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val houseRepository: HouseRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _isGrid = MutableStateFlow(true)
    val isGrid: StateFlow<Boolean> = _isGrid.asStateFlow()

    val houses: StateFlow<List<House>> = _query
        .flatMapLatest { q ->
            if (q.isBlank()) houseRepository.observeAllSorted() else houseRepository.search(q)
        }
        .stateIn(viewModelScope, started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), initialValue = emptyList())

    fun onToggleView() { _isGrid.value = !_isGrid.value }
    fun onQueryChange(value: String) { _query.value = value }

    fun deleteHouse(id: Long) {
        viewModelScope.launch { houseRepository.delete(id) }
    }
} 