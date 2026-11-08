package com.example.packguide.ui.screens

import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.packguide.scanning.ScannerViewModel
import com.example.packguide.ui.components.AsciiButton
import com.example.packguide.ui.components.TechCard
import com.example.packguide.ui.localization.LocalAppLanguage
import com.example.packguide.ui.localization.Text
import com.example.packguide.ui.localization.localizedText
import com.example.packguide.ui.theme.PackGuideColors
import com.example.packguide.ui.theme.Typography

@Composable
fun PhotoScanPanel(scanner: ScannerViewModel, luggage: LuggagePreset) {
    val state = scanner.stateFor(luggage)
    val language = LocalAppLanguage.current
    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) scanner.selectPhoto(uri, luggage)
    }
    // The system camera supplies an in-memory thumbnail without creating app photo files.
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) scanner.selectPhoto(bitmap, luggage)
    }
    TechCard(headerTag = "EQUIPAJE", borderColor = PackGuideColors.primaryText) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Toma o elige una foto de tu maleta.",
                style = Typography.bodyMedium, color = PackGuideColors.primaryText)
            state.photo?.let { photo ->
                Image(photo.asImageBitmap(),
                    contentDescription = localizedText("Foto de tu maleta", language),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 140.dp, max = 260.dp)
                        .clip(RoundedCornerShape(12.dp)).background(PackGuideColors.surfaceVariant))
            }
            AsciiButton("TOMAR FOTO", onClick = {
                try { camera.launch(null) }
                catch (_: ActivityNotFoundException) {
                    scanner.message("Cámara no disponible. Usa Elegir foto.", luggage)
                } catch (_: SecurityException) {
                    scanner.message("Acceso a la cámara denegado. Usa Elegir foto.", luggage)
                }
            }, enabled = !state.loading, icon = Icons.Outlined.PhotoCamera, modifier = Modifier.fillMaxWidth())
            AsciiButton("ELEGIR FOTO", onClick = {
                try {
                    gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } catch (_: ActivityNotFoundException) {
                    scanner.message("Selector de fotos no disponible. Usa Tomar foto.", luggage)
                } catch (_: SecurityException) {
                    scanner.message("Acceso a la foto denegado. Intenta elegirla de nuevo.", luggage)
                }
            }, enabled = !state.loading, isPrimary = false, icon = Icons.Outlined.AddPhotoAlternate, modifier = Modifier.fillMaxWidth())
            if (state.loading) CircularProgressIndicator(color = PackGuideColors.primaryText)
            state.message?.let { Text(it, color = PackGuideColors.secondaryText, style = Typography.bodySmall) }
            Text("La foto es una referencia. El espacio se calcula con las medidas de la maleta y los volúmenes estándar del catálogo.",
                style = Typography.bodySmall, color = PackGuideColors.secondaryText)
        }
    }
}
