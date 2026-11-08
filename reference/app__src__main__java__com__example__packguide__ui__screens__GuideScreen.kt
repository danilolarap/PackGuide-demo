package com.example.packguide.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.packguide.ui.components.AsciiButton
import com.example.packguide.ui.components.TechCard
import com.example.packguide.ui.localization.Text
import com.example.packguide.ui.navigation.NavTab
import com.example.packguide.ui.theme.AccentGreenLed
import com.example.packguide.ui.theme.AccentRedLed
import com.example.packguide.ui.theme.PackGuideColors
import com.example.packguide.ui.theme.Typography

@Composable
fun GuideScreen(
    packing: PackingState,
    modifier: Modifier = Modifier,
    onNavigateToTab: ((NavTab) -> Unit)? = null,
    scanner: com.example.packguide.scanning.ScannerViewModel? = null,
    preview: com.example.packguide.ai.PackingPreviewViewModel? = null
) {
    val selectedItems = packing.items.filter { it.quantity > 0 }
    val progress = packing.activeBag.progressPercent
    val copy = com.example.packguide.ui.localization.PackingText(com.example.packguide.ui.localization.LocalAppLanguage.current)

    Column(modifier.fillMaxSize().background(PackGuideColors.background)) {
        com.example.packguide.ui.components.HudHeader(title = "GUÍA DE EMPACADO", sysCode = "", statusText = "")
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { LuggageSelector(packing) }
            item { PackingSuggestionPanel(packing) }
            item {
                TechCard(headerTag = "PROGRESO REAL", borderColor = PackGuideColors.primaryText) {
                    Text(copy.progress(packing.packedUnits, packing.selectedUnits, progress),
                        style = Typography.titleMedium, color = PackGuideColors.primaryText)
                    Spacer(Modifier.height(8.dp))
                    androidx.compose.material3.LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = PackGuideColors.primaryText,
                        trackColor = PackGuideColors.surfaceVariant,
                        drawStopIndicator = {}
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Marca cada objeto cuando esté dentro de la maleta. Toca la zona para cambiar su ubicación.",
                        style = Typography.bodySmall, color = PackGuideColors.secondaryText)
                }
            }
            if (scanner != null && preview != null) item {
                PackingGuidePreviewPanel(packing, scanner, preview,
                    onOpenScanner = { onNavigateToTab?.invoke(NavTab.Scanner) },
                    onOpenCatalog = { onNavigateToTab?.invoke(NavTab.Catalog) })
            }
            item {
                TechCard(headerTag = "CONSEJO DE DISTRIBUCIÓN", borderColor = PackGuideColors.border) {
                    Text("Coloca el calzado y los objetos pesados en la base; la ropa en el centro y lo que necesitarás durante el viaje arriba.",
                        style = Typography.bodyMedium, color = PackGuideColors.primaryText)
                }
            }
            item {
                Text("ASIGNACIÓN EN VALIJA", style = Typography.titleLarge,
                    color = PackGuideColors.primaryText)
                Text("Cada artículo tiene una zona editable y un contador de unidades empacadas.",
                    style = Typography.bodySmall, color = PackGuideColors.secondaryText)
            }
            if (selectedItems.isEmpty()) {
                item {
                    Text("Aún no hay artículos seleccionados. Añádelos en el catálogo.",
                        color = PackGuideColors.secondaryText)
                    AsciiButton("IR AL CATÁLOGO", onClick = { onNavigateToTab?.invoke(NavTab.Catalog) },
                        modifier = Modifier.fillMaxWidth())
                }
            }
            items(selectedItems, key = { it.id }) { item ->
                TechCard(borderColor = PackGuideColors.border) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(item.icon, contentDescription = null, tint = PackGuideColors.primaryText)
                        Text("  ${item.name}", style = Typography.titleMedium,
                            color = PackGuideColors.primaryText, modifier = Modifier.weight(1f))
                        Text("${item.packedQuantity}/${item.quantity}", color = PackGuideColors.primaryText)
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsciiButton(
                            text = "ZONA: ${item.zone.label} ↻",
                            onClick = { packing.updateItem(item.id) { it.copy(zone = it.zone.next()) } },
                            modifier = Modifier.weight(1f), isPrimary = false
                        )
                        AsciiButton(
                            text = if (item.packedQuantity == item.quantity) "DESMARCAR" else "MARCAR EMPACADO",
                            onClick = {
                                packing.updateItem(item.id) {
                                    it.copy(packedQuantity = if (it.packedQuantity == it.quantity) 0 else it.packedQuantity + 1)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            isPrimary = item.packedQuantity != item.quantity
                        )
                    }
                }
            }
            item {
                AsciiButton("VER RESUMEN", onClick = { onNavigateToTab?.invoke(NavTab.Summary) },
                    modifier = Modifier.fillMaxWidth(), isPrimary = true)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
