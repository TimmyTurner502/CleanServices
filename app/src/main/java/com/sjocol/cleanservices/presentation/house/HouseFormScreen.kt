package com.sjocol.cleanservices.presentation.house

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.sjocol.cleanservices.util.ImageUtils
import android.content.Intent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseFormScreen(
    onBack: () -> Unit,
    viewModel: HouseFormViewModel = hiltViewModel()
) {
    val name by viewModel.name.collectAsState()
    val address by viewModel.address.collectAsState()
    val photoUri by viewModel.photoUri.collectAsState()

    val context = LocalContext.current
    val tempCameraUri = remember { mutableStateOf<Uri?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            // Persist permission for gallery uri
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: SecurityException) { }
            viewModel.setPhotoUri(uri.toString())
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.setPhotoUri(tempCameraUri.value?.toString())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear/Editar Casa") },
                actions = {
                    IconButton(onClick = {
                        val uri = ImageUtils.createTempImageUri(context)
                        tempCameraUri.value = uri
                        takePictureLauncher.launch(uri)
                    }) { Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Cámara") }
                    IconButton(onClick = {
                        pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) { Icon(imageVector = Icons.Default.Image, contentDescription = "Galería") }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier.fillMaxSize().padding(inner).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!photoUri.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(model = photoUri),
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.height(180.dp)
                )
                Spacer(Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = name,
                onValueChange = viewModel::setName,
                label = { Text("Nombre") },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge
            )
            OutlinedTextField(
                value = address ?: "",
                onValueChange = viewModel::setAddress,
                label = { Text("Dirección (opcional)") },
                textStyle = MaterialTheme.typography.bodyLarge
            )

            Button(onClick = { viewModel.save(onBack) }, enabled = name.isNotBlank()) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.height(0.dp))
                Text("Guardar")
            }
        }
    }
} 