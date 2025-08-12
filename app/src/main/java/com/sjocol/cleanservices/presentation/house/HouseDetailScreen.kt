package com.sjocol.cleanservices.presentation.house

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sjocol.cleanservices.domain.model.WorkEntry
import com.sjocol.cleanservices.domain.model.WorkType
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseDetailScreen(
    onBack: () -> Unit,
    onAddEntry: () -> Unit,
    viewModel: HouseDetailViewModel = hiltViewModel()
) {
    val house by viewModel.house.collectAsState()
    val entries by viewModel.entries.collectAsState()
    val filter by viewModel.filterType.collectAsState()
    val month by viewModel.currentMonth.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(house?.name ?: "Detalle") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } },
                actions = {
                    IconButton(onClick = { viewModel.prevMonth() }) { Icon(Icons.Default.ArrowLeft, contentDescription = "Mes anterior") }
                    IconButton(onClick = { viewModel.nextMonth() }) { Icon(Icons.Default.ArrowRight, contentDescription = "Mes siguiente") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEntry) { Icon(Icons.Default.Add, contentDescription = "Agregar evento") }
        }
    ) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {
            FilterChipsRow(current = filter, onChange = viewModel::setFilter)

            Text(
                text = "${month.month.name.lowercase().replaceFirstChar { it.titlecase() }} ${month.year}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (entries.isEmpty()) {
                Text("Sin eventos en este mes", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(entries.filter { filter == null || it.type == filter }) { e ->
                        WorkEntryRow(e)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipsRow(current: WorkType?, onChange: (WorkType?) -> Unit) {
    androidx.compose.foundation.layout.Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        val colors = AssistChipDefaults.assistChipColors()
        AssistChip(
            onClick = { onChange(null) },
            label = { Text("Todos") },
            colors = colors,
            leadingIcon = null,
            enabled = current != null
        )
        AssistChip(
            onClick = { onChange(WorkType.LIMPIEZA) },
            label = { Text("Limpiezas") },
            colors = colors,
            enabled = current != WorkType.LIMPIEZA
        )
        AssistChip(
            onClick = { onChange(WorkType.SERVICIO) },
            label = { Text("Servicios") },
            colors = colors,
            enabled = current != WorkType.SERVICIO
        )
    }
}

@Composable
private fun WorkEntryRow(entry: WorkEntry) {
    val date = entry.dateIso
    val type = if (entry.type == WorkType.LIMPIEZA) "Limpieza" else "Servicio"
    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("$type — $date", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
        val details = buildString {
            if (!entry.startTime.isNullOrBlank()) append("Inicio: ${entry.startTime}  ")
            if (!entry.endTime.isNullOrBlank()) append("Fin: ${entry.endTime}  ")
            append("Personas: ${entry.peopleCount}")
            if (!entry.peopleNamesCsv.isNullOrBlank()) append(" (${entry.peopleNamesCsv})")
        }
        Text(details, style = MaterialTheme.typography.bodySmall)
        if (!entry.notes.isNullOrBlank()) Text(entry.notes!!, style = MaterialTheme.typography.bodySmall)
    }
} 