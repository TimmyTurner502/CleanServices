package com.sjocol.cleanservices.presentation.house

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjocol.cleanservices.data.repository.HouseRepository
import com.sjocol.cleanservices.domain.model.House
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HouseFormViewModel @Inject constructor(
    private val houseRepository: HouseRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _id = MutableStateFlow<Long?>(null)
    val id: StateFlow<Long?> = _id.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _address = MutableStateFlow<String?>(null)
    val address: StateFlow<String?> = _address.asStateFlow()

    private val _photoUri = MutableStateFlow<String?>(null)
    val photoUri: StateFlow<String?> = _photoUri.asStateFlow()

    init {
        val argId = savedStateHandle.get<Long>("houseId") ?: -1L
        if (argId > 0) {
            viewModelScope.launch {
                houseRepository.getById(argId)?.let { existing ->
                    _id.value = existing.id
                    _name.value = existing.name
                    _address.value = existing.address
                    _photoUri.value = existing.photoUri
                }
            }
        }
    }

    fun setName(value: String) { _name.value = value }
    fun setAddress(value: String) { _address.value = value.ifBlank { null } }
    fun setPhotoUri(value: String?) { _photoUri.value = value }

    fun save(onDone: () -> Unit) {
        val currentName = _name.value.trim()
        if (currentName.isEmpty()) return
        viewModelScope.launch {
            val house = House(
                id = _id.value ?: 0L,
                name = currentName,
                address = _address.value,
                photoUri = _photoUri.value
            )
            houseRepository.upsert(house)
            onDone()
        }
    }
} 