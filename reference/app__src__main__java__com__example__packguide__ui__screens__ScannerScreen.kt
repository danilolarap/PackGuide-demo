package com.example.packguide.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.packguide.scanning.ScannerViewModel
import com.example.packguide.ui.components.AsciiButton
import com.example.packguide.ui.components.TechCard
import com.example.packguide.ui.localization.Text
import com.example.packguide.ui.theme.PackGuideColors
import com.example.packguide.ui.theme.Typography
import java.util.Locale

@Composable
fun ScannerScreen(
    modifier: Modifier = Modifier,
    packing: PackingState,
    scanner: ScannerViewModel,
    onOpenCatalog: () -> Unit
) {
    var pendingRemoval by remember { mutableStateOf<LuggagePreset?>(null) }
    val remove = pendingRemoval
    if (remove != null) {
        AlertDialog(
            onDismissRequest = { pendingRemoval = null },
            title = { Text("QUITAR MALETA: ${remove.label}") },
            text = { Text("Se quitarán del plan los artículos asignados a esta maleta. Las demás maletas conservarán sus artículos.") },
            confirmButton = { TextButton(onClick = {
                packing.toggleLuggage(remove)
                scanner.clearPhoto(remove)
                pendingRemoval = null
            }) { Text("QUITAR MALETA") } },
            dismissButton = { TextButton(onClick = { pendingRemoval = null }) { Text("CANCELAR") } }
        )
    }
    Column(modifier.fillMaxSize().background(PackGuideColors.background)) {
        com.example.packguide.ui.components.HudHeader(title = "EQUIPAJE DEL VIAJE", statusText = "")
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                TripLuggageSelection(packing) { type ->
                    if (type in packing.selectedLuggage && packing.bag(type).selectedUnits > 0) {
                        pendingRemoval = type
                    } else {
                        packing.toggleLuggage(type)
                        if (type !in packing.selectedLuggage) scanner.clearPhoto(type)
                    }
                }
            }
            packing.selectedLuggage.forEach { type ->
                item(key = type.name) {
                    val bag = packing.bag(type)
                    TechCard(headerTag = "MALETA: ${type.label}") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            PhotoScanPanel(scanner, type)
                            Text("MEDIDAS INTERIORES ÚTILES", style = Typography.labelMedium,
                                color = PackGuideColors.primaryText)
                            DimensionStepperRow("ALTO", bag.heightCm.value) {
                                bag.heightCm.value = it; bag.dimensionsConfirmed.value = false
                            }
                            DimensionStepperRow("ANCHO", bag.widthCm.value) {
                                bag.widthCm.value = it; bag.dimensionsConfirmed.value = false
                            }
                            DimensionStepperRow("PROF.", bag.depthCm.value) {
                                bag.depthCm.value = it; bag.dimensionsConfirmed.value = false
                            }
                            Text("${com.example.packguide.ui.localization.localizedText("CAPACIDAD INTERIOR TOTAL", com.example.packguide.ui.localization.LocalAppLanguage.current)}: ${com.example.packguide.ui.localization.formatLiters(bag.capacityL)} L",
                                style = Typography.titleMedium, color = PackGuideColors.primaryText)
                            Text("Una foto común no mide la maleta. Ajusta las medidas interiores útiles y confírmalas.",
                                style = Typography.bodySmall, color = PackGuideColors.secondaryText)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = bag.dimensionsConfirmed.value,
                                    onCheckedChange = { bag.dimensionsConfirmed.value = it })
                                Text("CONFIRMO LAS MEDIDAS: ${type.label}", style = Typography.labelSmall,
                                    color = PackGuideColors.primaryText)
                            }
                        }
                    }
                }
            }
            item {
                Text("En Catálogo podrás repartir los artículos entre las maletas seleccionadas.",
                    style = Typography.bodySmall, color = PackGuideColors.secondaryText)
                Spacer(Modifier.height(12.dp))
                AsciiButton("CONTINUAR AL CATÁLOGO", onClick = onOpenCatalog,
                    enabled = packing.allDimensionsConfirmed, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun DimensionStepperRow(label: String, valueCm: Int, onValueChange: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("$label:", modifier = Modifier.weight(1f), style = Typography.titleMedium,
            color = PackGuideColors.primaryText)
        androidx.compose.material3.IconButton(onClick = { onValueChange(valueCm - 1) }, enabled = valueCm > 1) {
            Text("−", color = PackGuideColors.primaryText)
        }
        Text("$valueCm CM", style = Typography.titleMedium, color = PackGuideColors.primaryText)
        androidx.compose.material3.IconButton(onClick = { onValueChange(valueCm + 1) }, enabled = valueCm < 300) {
            Text("+", color = PackGuideColors.primaryText)
        }
    }
}
