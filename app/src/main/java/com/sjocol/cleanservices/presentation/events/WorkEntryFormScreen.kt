package com.sjocol.cleanservices.presentation.events

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sjocol.cleanservices.domain.model.WorkType
import java.time.LocalDate
import androidx.compose.material3.DropdownMenu
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.text.style.TextAlign
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkEntryFormScreen(
    onBack: () -> Unit,
    viewModel: WorkEntryFormViewModel = hiltViewModel()
) {
    val houses by viewModel.houses.collectAsState()
    val houseId by viewModel.houseId.collectAsState()
    val type by viewModel.type.collectAsState()
    val dateIso by viewModel.dateIso.collectAsState()
    val start by viewModel.startTime.collectAsState()
    val end by viewModel.endTime.collectAsState()
    // Campos de personas removidos por la nueva UX

    Scaffold(
        topBar = { TopAppBar(title = { Text("Registrar evento") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.save(onBack) }) { Text("Guardar") }
        }
    ) { inner ->
        Column(Modifier.fillMaxSize().padding(inner).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Selector de casa (DropdownMenu simple)
            val expanded = remember { mutableStateOf(false) }
            Box {
                OutlinedTextField(
                    value = houses.firstOrNull { it.id == houseId }?.name ?: "Selecciona una casa",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Casa") },
                    modifier = Modifier.clickable { expanded.value = true }
                )
                DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
                    houses.forEach { h ->
                        DropdownMenuItem(text = { Text(h.name) }, onClick = { viewModel.setHouseId(h.id); expanded.value = false })
                    }
                }
            }

            // Tipo con chips destacadas
            Text(if (type == WorkType.LIMPIEZA) "Seleccionado: Limpieza" else "Seleccionado: Servicio")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val colors = AssistChipDefaults.assistChipColors()
                AssistChip(
                    onClick = { viewModel.setType(WorkType.LIMPIEZA) },
                    label = { Text("Limpieza") },
                    colors = colors,
                    leadingIcon = {},
                    enabled = type != WorkType.LIMPIEZA
                )
                AssistChip(
                    onClick = { viewModel.setType(WorkType.SERVICIO) },
                    label = { Text("Servicio") },
                    colors = colors,
                    enabled = type != WorkType.SERVICIO
                )
            }

            // Fecha: oculto por defecto; se abre un dialog al cambiar
            val millis = LocalDate.parse(dateIso).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            val showDatePicker = remember { mutableStateOf(false) }
            Text("Fecha: $dateIso")
            Row { TextButton(onClick = { showDatePicker.value = true }) { Text("Cambiar") } }
            if (showDatePicker.value) {
                val dateState: DatePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = millis,
                    initialDisplayMode = DisplayMode.Picker
                )
                DatePickerDialog(
                    onDismissRequest = { showDatePicker.value = false },
                    confirmButton = {
                        TextButton(onClick = {
                            dateState.selectedDateMillis?.let { sel ->
                                val local = Instant.ofEpochMilli(sel).atZone(ZoneOffset.UTC).toLocalDate()
                                viewModel.setDate(local.toString())
                            }
                            showDatePicker.value = false
                        }) { Text("Aceptar") }
                    },
                    dismissButton = { TextButton(onClick = { showDatePicker.value = false }) { Text("Cancelar") } }
                ) {
                    DatePicker(state = dateState, showModeToggle = false)
                }
            }

            // Horas y cronómetro: TimeInput sólo HH:mm (50% ancho)
            Text("Hora inicio (HH:mm)")
            val startState = rememberTimePickerState()
            Box(Modifier.fillMaxWidth(0.5f)) { TimeInput(state = startState) }
            Row {}

            Text("Hora fin (HH:mm)")
            val endState = rememberTimePickerState()
            Box(Modifier.fillMaxWidth(0.5f)) { TimeInput(state = endState) }
            Row {}
        }
    }
} 