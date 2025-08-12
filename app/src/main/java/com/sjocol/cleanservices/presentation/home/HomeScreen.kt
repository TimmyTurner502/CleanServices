package com.sjocol.cleanservices.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.sjocol.cleanservices.domain.model.House

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddHouse: () -> Unit,
    onOpenSettings: () -> Unit,
    onEditHouse: (Long) -> Unit,
    onDeleteHouse: (Long) -> Unit,
    onOpenHouse: (Long) -> Unit,
    onOpenGlobal: () -> Unit
) {
    val isGrid by viewModel.isGrid.collectAsState()
    val houses by viewModel.houses.collectAsState()
    val query by viewModel.query.collectAsState()

    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inicio — Casas") },
                actions = {
                    IconButton(onClick = { viewModel.onToggleView() }) {
                        Icon(imageVector = if (isGrid) Icons.Default.List else Icons.Default.GridView, contentDescription = "Alternar vista")
                    }
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Opciones")
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(text = { Text("Historial global") }, onClick = { menuExpanded = false; onOpenGlobal() })
                        DropdownMenuItem(text = { Text("Configuración") }, onClick = { menuExpanded = false; onOpenSettings() })
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddHouse) { Icon(Icons.Default.Add, contentDescription = "Agregar casa") }
        }
    ) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {
            SearchBar(
                query = query,
                onQueryChange = viewModel::onQueryChange,
                onSearch = { },
                active = false,
                onActiveChange = { },
                placeholder = { Text("Buscar casas…") },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {}

            if (houses.isEmpty()) {
                EmptyState()
            } else {
                if (isGrid) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(180.dp),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(houses) { house ->
                            HouseCard(house, onEdit = onEditHouse, onDelete = { pendingDeleteId = house.id }, onOpen = onOpenHouse)
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(houses) { house ->
                            HouseRow(house, onEdit = onEditHouse, onDelete = { pendingDeleteId = house.id }, onOpen = onOpenHouse)
                        }
                    }
                }
            }
        }
    }

    if (pendingDeleteId != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text("Eliminar casa") },
            text = { Text("Esta acción no se puede deshacer. ¿Deseas eliminar esta casa?") },
            confirmButton = {
                FilledTonalButton(onClick = {
                    pendingDeleteId?.let(onDeleteHouse)
                    pendingDeleteId = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun EmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No hay casas. Toca + para agregar.", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun HouseCard(house: House, onEdit: (Long) -> Unit, onDelete: () -> Unit, onOpen: (Long) -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }
    Column(Modifier.background(MaterialTheme.colorScheme.surface).padding(12.dp).clickable { onOpen(house.id) }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(house.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            IconButton(onClick = { menuExpanded = true }) { Icon(Icons.Default.MoreVert, contentDescription = null) }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                DropdownMenuItem(text = { Text("Historial") }, leadingIcon = { Icon(Icons.Default.Edit, null) }, onClick = { menuExpanded = false; onEdit(house.id) })
                DropdownMenuItem(text = { Text("Nuevo evento") }, onClick = { menuExpanded = false; onOpen(house.id) })
                DropdownMenuItem(text = { Text("Eliminar") }, leadingIcon = { Icon(Icons.Default.Delete, null) }, onClick = { menuExpanded = false; onDelete() })
            }
        }
        if (!house.address.isNullOrBlank()) {
            Text(house.address ?: "", style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        if (!house.photoUri.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(house.photoUri),
                contentDescription = house.name,
                modifier = Modifier.height(120.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun HouseRow(house: House, onEdit: (Long) -> Unit, onDelete: () -> Unit, onOpen: (Long) -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }
    Column(Modifier.background(MaterialTheme.colorScheme.surface).padding(12.dp).clickable { onOpen(house.id) }.background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(house.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            IconButton(onClick = { menuExpanded = true }) { Icon(Icons.Default.MoreVert, contentDescription = null) }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                DropdownMenuItem(text = { Text("Historial") }, leadingIcon = { Icon(Icons.Default.Edit, null) }, onClick = { menuExpanded = false; onEdit(house.id) })
                DropdownMenuItem(text = { Text("Nuevo evento") }, onClick = { menuExpanded = false; onOpen(house.id) })
                DropdownMenuItem(text = { Text("Eliminar") }, leadingIcon = { Icon(Icons.Default.Delete, null) }, onClick = { menuExpanded = false; onDelete() })
            }
        }
        if (!house.address.isNullOrBlank()) {
            Text(house.address ?: "", style = MaterialTheme.typography.bodySmall)
        }
    }
} 