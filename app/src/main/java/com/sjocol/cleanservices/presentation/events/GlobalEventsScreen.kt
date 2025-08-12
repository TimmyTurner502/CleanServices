package com.sjocol.cleanservices.presentation.events

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextButton
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sjocol.cleanservices.domain.model.WorkType
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.Instant
import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.compose.ui.viewinterop.AndroidView
import android.content.Intent
import java.io.File
import java.io.FileOutputStream
import android.provider.MediaStore
import android.content.ContentValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalEventsScreen(
    onBack: () -> Unit,
    viewModel: GlobalEventsViewModel = hiltViewModel()
) {
    val items by viewModel.items.collectAsState()
    val filter by viewModel.filterType.collectAsState()
    val month by viewModel.currentMonth.collectAsState()
    val context = LocalContext.current
    val exportMenuExpanded = remember { mutableStateOf(false) }
    val shareMenuExpanded = remember { mutableStateOf(false) }

    // Restaurado a navegación por mes con flechas

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial global") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } },
                actions = {
                    IconButton(onClick = { viewModel.prevMonth() }) { Icon(Icons.Default.ArrowLeft, contentDescription = "Mes anterior") }
                    IconButton(onClick = { viewModel.nextMonth() }) { Icon(Icons.Default.ArrowRight, contentDescription = "Mes siguiente") }
                }
            )
        }
    ) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {
            androidx.compose.foundation.layout.Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val colors = AssistChipDefaults.assistChipColors()
                AssistChip(onClick = { viewModel.setFilter(null) }, label = { Text("Todos") }, colors = colors, enabled = filter != null)
                AssistChip(onClick = { viewModel.setFilter(WorkType.LIMPIEZA) }, label = { Text("Limpiezas") }, colors = colors, enabled = filter != WorkType.LIMPIEZA)
                AssistChip(onClick = { viewModel.setFilter(WorkType.SERVICIO) }, label = { Text("Servicios") }, colors = colors, enabled = filter != WorkType.SERVICIO)

                // Exportar (estilo chip)
                AssistChip(onClick = { exportMenuExpanded.value = true }, label = { Text("Exportar") }, colors = colors)
                DropdownMenu(expanded = exportMenuExpanded.value, onDismissRequest = { exportMenuExpanded.value = false }) {
                    DropdownMenuItem(text = { Text("PDF") }, onClick = {
                        exportMenuExpanded.value = false
                        // Crear PDF simple con el listado
                        val pdf = PdfDocument()
                        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
                        val page = pdf.startPage(pageInfo)
                        val canvas = page.canvas
                        val paint = Paint().apply {
                            textSize = 12f
                            typeface = Typeface.MONOSPACE
                        }
                        var y = 40f
                        canvas.drawText("Historial ${month.month} ${month.year}", 40f, y, paint)
                        y += 20f
                        items.forEach { item ->
                            val e = item.entry
                            val typeStr = if (e.type == WorkType.LIMPIEZA) "Limpieza" else "Servicio"
                            val line = "${e.dateIso} — $typeStr — ${item.houseName}"
                            if (y > 800f) {
                                pdf.finishPage(page)
                                val next = pdf.startPage(PdfDocument.PageInfo.Builder(595, 842, pdf.pages.size + 1).create())
                                y = 40f
                                next.canvas.drawText("(continúa)", 40f, y, paint)
                                y += 20f
                                page.canvas.setBitmap(null)
                            }
                            page.canvas.drawText(line, 40f, y, paint)
                            y += 18f
                        }
                        pdf.finishPage(page)
                        val file = File(context.cacheDir, "historial_${System.currentTimeMillis()}.pdf")
                        pdf.writeTo(FileOutputStream(file))
                        pdf.close()
                        // Guardar en Descargas
                        val values = ContentValues().apply {
                            put(MediaStore.Downloads.DISPLAY_NAME, file.name)
                            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                        }
                        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                        uri?.let { u -> context.contentResolver.openOutputStream(u)?.use { it.write(file.readBytes()) } }
                    })
                    DropdownMenuItem(text = { Text("Imagen (JPG)") }, onClick = {
                        exportMenuExpanded.value = false
                        // Capturar pantalla como imagen simple
                        val rootView = (context as? android.app.Activity)?.window?.decorView?.rootView
                        val bitmap = rootView?.drawToBitmap()
                        if (bitmap != null) {
                            val file = File(context.cacheDir, "historial_${System.currentTimeMillis()}.jpg")
                            FileOutputStream(file).use { out -> bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out) }
                            val values = ContentValues().apply {
                                put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
                                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            }
                            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                            uri?.let { u -> context.contentResolver.openOutputStream(u)?.use { it.write(file.readBytes()) } }
                        }
                    })
                }

                // Compartir (estilo chip)
                AssistChip(onClick = { shareMenuExpanded.value = true }, label = { Text("Compartir") }, colors = colors)
                DropdownMenu(expanded = shareMenuExpanded.value, onDismissRequest = { shareMenuExpanded.value = false }) {
                    DropdownMenuItem(text = { Text("PDF") }, onClick = {
                        shareMenuExpanded.value = false
                        val pdf = PdfDocument()
                        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                        val page = pdf.startPage(pageInfo)
                        val canvas = page.canvas
                        val paint = Paint().apply { textSize = 12f; typeface = Typeface.MONOSPACE }
                        var y = 40f
                        canvas.drawText("Historial ${month.month} ${month.year}", 40f, y, paint)
                        y += 20f
                        items.forEach { item ->
                            val e = item.entry
                            val typeStr = if (e.type == WorkType.LIMPIEZA) "Limpieza" else "Servicio"
                            page.canvas.drawText("${e.dateIso} — $typeStr — ${item.houseName}", 40f, y, paint)
                            y += 18f
                        }
                        pdf.finishPage(page)
                        val file = File(context.cacheDir, "historial_share_${System.currentTimeMillis()}.pdf")
                        pdf.writeTo(FileOutputStream(file))
                        pdf.close()
                        val uri: Uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(intent, "Compartir PDF"))
                    })
                    DropdownMenuItem(text = { Text("Imagen (JPG)") }, onClick = {
                        shareMenuExpanded.value = false
                        val rootView = (context as? android.app.Activity)?.window?.decorView?.rootView
                        val bitmap = rootView?.drawToBitmap()
                        if (bitmap != null) {
                            val file = File(context.cacheDir, "historial_share_${System.currentTimeMillis()}.jpg")
                            FileOutputStream(file).use { out -> bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out) }
                            val uri: Uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "image/jpeg"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(intent, "Compartir imagen"))
                        }
                    })
                }
            }
            Text("${month.month.name.lowercase().replaceFirstChar { it.titlecase() }} ${month.year}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 16.dp))
            if (items.isEmpty()) {
                Text("Sin eventos", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { item ->
                        val e = item.entry
                        val type = if (e.type == WorkType.LIMPIEZA) "Limpieza" else "Servicio"
                        Text("${e.dateIso} — $type — ${item.houseName}", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                    }
                }
            }
        }
    }
} 