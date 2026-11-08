package com.example.packguide.ui.screens

import android.content.ActivityNotFoundException
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.packguide.ai.*
import com.example.packguide.scanning.ScannerViewModel
import com.example.packguide.settings.AppLanguage
import com.example.packguide.ui.components.AsciiButton
import com.example.packguide.ui.components.TechCard
import com.example.packguide.ui.localization.LocalAppLanguage
import com.example.packguide.ui.localization.localizedText
import com.example.packguide.ui.theme.PackGuideColors
import com.example.packguide.ui.theme.Typography

@Composable
private fun previewText(es: String, en: String) = if (LocalAppLanguage.current == AppLanguage.English) en else es

@Composable
private fun PreviewImage(bitmap: Bitmap, description: String) {
    var expanded by remember(bitmap) { mutableStateOf(false) }
    val enlarge = previewText("Ampliar vista organizada", "Enlarge organized preview")
    Image(bitmap.asImageBitmap(), contentDescription = description,
        contentScale = ContentScale.Fit,
        modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp, max = 340.dp)
            .clickable(role = Role.Button, onClickLabel = enlarge) { expanded = true })
    TextButton(onClick = { expanded = true }) { Text(enlarge) }
    if (expanded) Dialog(onDismissRequest = { expanded = false },
        properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.fillMaxWidth().fillMaxHeight(0.95f).padding(12.dp), color = PackGuideColors.surface) {
            Column {
                Image(bitmap.asImageBitmap(), contentDescription = description,
                    modifier = Modifier.fillMaxWidth().weight(1f), contentScale = ContentScale.Fit)
                TextButton(onClick = { expanded = false }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                    Text(previewText("CERRAR", "CLOSE"))
                }
            }
        }
    }
}

@Composable
private fun PreviewStatus(state: PackingPreviewState, onCancel: () -> Unit) {
    state.work?.let { work ->
        CircularProgressIndicator(color = PackGuideColors.primaryText)
        Text(if (work == PreviewWork.Photos) previewText("Abriendo fotos…", "Opening photos…")
            else previewText("Creando vista organizada…", "Creating organized preview…"),
            color = PackGuideColors.primaryText, style = Typography.bodySmall)
        AsciiButton(previewText("CANCELAR", "CANCEL"), onClick = onCancel,
            isPrimary = false, modifier = Modifier.fillMaxWidth())
    }
    state.problem?.let { problem ->
        Text(when (problem) {
            PreviewProblem.Photo -> previewText("No se pudo abrir la foto o la cámara. Intenta elegir otra imagen.", "Could not open the photo or camera. Try another image.")
            PreviewProblem.UnsupportedPhoto -> previewText("Ese archivo no es una foto compatible. Elige una imagen JPG, PNG o WebP.", "This file is not a supported photo. Choose a JPG, PNG or WebP image.")
            PreviewProblem.LargePhoto -> previewText("La foto es demasiado grande. Elige una imagen más pequeña.", "The photo is too large. Choose a smaller image.")
            PreviewProblem.Generation -> previewText("No se pudo crear la vista. Inténtalo de nuevo o elige otra foto.", "Could not create the preview. Try again or choose another photo.")
            PreviewProblem.Connection -> previewText("El servicio no está disponible. Inténtalo de nuevo más tarde.", "The service is unavailable. Please try again later.")
            PreviewProblem.Cancelled -> previewText("Operación cancelada. Puedes volver a intentarlo.", "Operation cancelled. You can try again.")
        }, color = if (problem == PreviewProblem.Cancelled) PackGuideColors.secondaryText else MaterialTheme.colorScheme.error,
            style = Typography.bodySmall)
    }
}

@Composable
fun PackingGuidePreviewPanel(packing: PackingState, scanner: ScannerViewModel, preview: PackingPreviewViewModel,
                             onOpenScanner: () -> Unit, onOpenCatalog: () -> Unit) {
    val type = packing.activeLuggage
    val plan = PackingPlan.from(packing.activeBag)
    val source = scanner.stateFor(type).photo
    val state = preview.stateFor(type)
    val language = LocalAppLanguage.current
    val current = state.guidePlan == plan && state.guideSource === source
    TechCard(headerTag = previewText("VISTA ORGANIZADA DE EMPAQUE", "ORGANIZED PACKING PREVIEW")) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(previewText("Funciona sin conexión. Combina tu foto original con un esquema de los artículos por zonas; no modifica la foto ni reconoce objetos.",
                "Works offline. Combines your original photo with an item layout by zone; it does not edit the photo or recognize objects."),
                style = Typography.bodySmall, color = PackGuideColors.secondaryText)
            when {
                source == null -> {
                    Text(previewText("Añade una foto de esta maleta abierta en Escáner.", "Add a photo of this open bag in Scanner."), color = PackGuideColors.secondaryText)
                    AsciiButton(previewText("AÑADIR FOTO EN ESCÁNER", "ADD PHOTO IN SCANNER"), onClick = onOpenScanner, modifier = Modifier.fillMaxWidth())
                }
                else -> {
                    Text(previewText("FOTO DE REFERENCIA", "REFERENCE PHOTO"), style = Typography.labelMedium, color = PackGuideColors.primaryText)
                    Image(source.asImageBitmap(), contentDescription = previewText("Foto de tu maleta", "Photo of your suitcase"),
                        contentScale = ContentScale.Fit, modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp, max = 180.dp))
                    TextButton(onClick = onOpenScanner) { Text(previewText("CAMBIAR FOTO", "CHANGE PHOTO")) }
                }
            }
            if (plan.contents.isEmpty()) {
                Text(previewText("Selecciona los objetos de esta maleta en Catálogo.", "Select this bag's items in Catalog."), color = PackGuideColors.secondaryText)
                AsciiButton(previewText("IR AL CATÁLOGO", "OPEN CATALOG"), onClick = onOpenCatalog, modifier = Modifier.fillMaxWidth())
            }
            if (current) state.guide?.let { bitmap ->
                val description = previewText("Vista organizada de empaque. ", "Organized packing preview. ") +
                    plan.contents.joinToString("; ") { "${it.quantity} × ${localizedText(it.name, language)}: ${localizedText(it.zone, language)}" }
                PreviewImage(bitmap, description)
            }
            if (!current && state.guide != null) Text(previewText("Cambió la foto o el plan. Genera una nueva vista.", "The photo or plan changed. Generate a new preview."), color = PackGuideColors.secondaryText)
            AsciiButton(previewText("GENERAR IMAGEN ORGANIZADA", "GENERATE PACKING IMAGE"),
                onClick = { if (source != null) preview.generate(type, plan, source, language) },
                enabled = source != null && !scanner.stateFor(type).loading && plan.contents.isNotEmpty() && state.work == null,
                modifier = Modifier.fillMaxWidth())
            PreviewStatus(state) { preview.cancel(type) }
            Text(previewText("Es una vista orientativa, no una imagen generada por IA ni una garantía de que todo quepa. Tócala para ampliarla.",
                "This is an illustrative preview, not an AI generated image or a guarantee that everything fits. Tap to enlarge."),
                color = PackGuideColors.secondaryText, style = Typography.bodySmall)
        }
    }
}

@Composable
fun PackingReviewPreviewPanel(packing: PackingState, preview: PackingPreviewViewModel) {
    val type = packing.activeLuggage
    val plan = PackingPlan.from(packing.activeBag)
    val state = preview.stateFor(type)
    val language = LocalAppLanguage.current
    key(type) {
        val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(4)) { uris -> preview.addPhotos(type, uris) }
        val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { photo -> if (photo != null) preview.addPhoto(type, photo) }
        TechCard(headerTag = previewText("REFERENCIA PARA REVISAR TU MALETA", "PACKING REVIEW REFERENCE")) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(previewText("Añade hasta 4 fotos para comparar tu maleta con el esquema del inventario. La vista usa la primera foto como referencia. Se procesa en el dispositivo y no califica automáticamente las fotos.",
                    "Add up to 4 photos to compare your bag with the inventory layout. The preview uses the first photo as reference. Processing stays on the device and does not automatically score photos."),
                    style = Typography.bodySmall, color = PackGuideColors.secondaryText)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.photos.size) { index ->
                        Column(Modifier.width(180.dp)) {
                            Image(state.photos[index].asImageBitmap(), contentDescription = previewText("Foto para comparar", "Photo to compare"),
                                contentScale = ContentScale.Fit, modifier = Modifier.fillMaxWidth().height(150.dp))
                            TextButton(onClick = { preview.removePhoto(type, index) }, enabled = state.work == null) { Text(previewText("QUITAR FOTO", "REMOVE PHOTO")) }
                        }
                    }
                }
                AsciiButton(previewText("AÑADIR FOTOS", "ADD PHOTOS"), onClick = {
                    try { gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                    catch (_: ActivityNotFoundException) { preview.photoError(type) }
                    catch (_: SecurityException) { preview.photoError(type) }
                }, enabled = state.photos.size < 4 && state.work == null, isPrimary = false, modifier = Modifier.fillMaxWidth())
                AsciiButton(previewText("TOMAR FOTO", "TAKE PHOTO"), onClick = {
                    try { camera.launch(null) }
                    catch (_: ActivityNotFoundException) { preview.photoError(type) }
                    catch (_: SecurityException) { preview.photoError(type) }
                }, enabled = state.photos.size < 4 && state.work == null, isPrimary = false, modifier = Modifier.fillMaxWidth())
                AsciiButton(previewText("GENERAR REFERENCIA VISUAL", "CREATE VISUAL REFERENCE"),
                    onClick = { state.photos.firstOrNull()?.let { preview.generate(type, plan, it, language, review = true) } },
                    enabled = state.photos.isNotEmpty() && state.work == null && plan.contents.isNotEmpty(), modifier = Modifier.fillMaxWidth())
                PreviewStatus(state) { preview.cancel(type) }
                if (state.reviewPlan == plan) state.review?.let {
                    PreviewImage(it, previewText("Referencia de organización creada en el dispositivo", "Packing reference created on the device"))
                } else if (state.review != null) Text(previewText("Cambió el plan. Genera una nueva referencia.", "The plan changed. Create a new reference."), color = PackGuideColors.secondaryText)
            }
        }
    }
}
