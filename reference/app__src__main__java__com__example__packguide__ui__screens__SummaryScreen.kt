package com.example.packguide.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.packguide.ui.components.AsciiButton
import com.example.packguide.ui.components.TechCard
import com.example.packguide.ui.localization.Text
import com.example.packguide.ui.navigation.NavTab
import com.example.packguide.ui.theme.AccentGreenLed
import com.example.packguide.ui.theme.AccentRedLed
import com.example.packguide.ui.theme.PackGuideColors
import com.example.packguide.ui.theme.Typography
import java.util.Locale
import com.example.packguide.ui.localization.LocalAppLanguage
import com.example.packguide.ui.localization.PackingText
import com.example.packguide.ui.localization.formatLiters

@Composable
fun SummaryScreen(
    packing: PackingState,
    modifier: Modifier = Modifier,
    onNavigateToTab: ((NavTab) -> Unit)? = null,
    preview: com.example.packguide.ai.PackingPreviewViewModel? = null
) {
    val selected = packing.items.filter { it.quantity > 0 }
    val estimate = packing.activeBag.estimate
    val copy = PackingText(LocalAppLanguage.current)

    Column(modifier.fillMaxSize().background(PackGuideColors.background)) {
        com.example.packguide.ui.components.HudHeader(title = "RESUMEN DEL EQUIPAJE", sysCode = "", statusText = "")
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                TechCard(headerTag = "TODAS LAS MALETAS") {
                    packing.selectedLuggage.forEach { type ->
                        val bag = packing.bag(type)
                        Text(copy.bagSummary(type.label, bag.estimate, bag.packedUnits, bag.selectedUnits),
                            style = Typography.bodyMedium,
                            color = if (bag.estimate.exceedsCapacity) AccentRedLed else PackGuideColors.primaryText)
                        Spacer(Modifier.height(6.dp))
                    }
                    Text("TOTAL: ${packing.totalSelectedUnits} unidades · ${packing.totalPackedUnits} empacadas",
                        style = Typography.labelMedium, color = PackGuideColors.primaryText)
                }
            }
            item { LuggageSelector(packing) }
            if (preview != null) item { PackingReviewPreviewPanel(packing, preview) }
            item {
                TechCard(headerTag = "MALETA SELECCIONADA", borderColor = PackGuideColors.primaryText) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Luggage, contentDescription = null, tint = AccentRedLed)
                        Text("  ${packing.activeLuggage.label} · ${packing.heightCm.value}×${packing.widthCm.value}×${packing.depthCm.value} cm",
                            color = PackGuideColors.primaryText, style = Typography.titleMedium)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Capacidad calculada con medidas interiores. Verifica las medidas exteriores y las normas de la aerolínea por separado.",
                        color = PackGuideColors.secondaryText, style = Typography.bodySmall)
                }
            }
            item {
                TechCard(headerTag = "CAPACIDAD ESTIMADA", borderColor = PackGuideColors.border) {
                    Text("Volumen estimado / capacidad interior total", color = PackGuideColors.secondaryText, style = Typography.bodySmall)
                    Text(copy.volume(estimate),
                        color = PackGuideColors.primaryText, style = Typography.headlineMedium)
                    Text(copy.occupancy(estimate.occupancyPercent), color = PackGuideColors.secondaryText, style = Typography.bodySmall)
                    Spacer(Modifier.height(6.dp))
                    androidx.compose.material3.LinearProgressIndicator(
                        progress = { estimate.occupancy.toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = if (estimate.exceedsCapacity) AccentRedLed else PackGuideColors.primaryText,
                        trackColor = PackGuideColors.surfaceVariant,
                        drawStopIndicator = {}
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(copy.balance(estimate), color = if (estimate.exceedsCapacity) AccentRedLed else PackGuideColors.primaryText)
                }
            }
            item {
                TechCard(headerTag = "ESTADO DE EMPACADO", borderColor = PackGuideColors.border) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = AccentGreenLed)
                        Text("  ${packing.packedUnits} empacados", color = PackGuideColors.primaryText)
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.RadioButtonUnchecked, contentDescription = null,
                            tint = PackGuideColors.secondaryText)
                        Text("  ${packing.selectedUnits - packing.packedUnits} pendientes", color = PackGuideColors.primaryText)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("${selected.size} tipos de artículos · ${packing.selectedUnits} unidades seleccionadas",
                        color = PackGuideColors.secondaryText, style = Typography.bodySmall)
                }
            }
            item {
                Text("DETALLE POR ARTÍCULO", style = Typography.titleLarge,
                    color = PackGuideColors.primaryText)
            }
            item {
                TechCard(headerTag = "DISTRIBUCIÓN POR ZONA", borderColor = PackGuideColors.border) {
                    PackingZone.entries.forEach { zone ->
                        val zoneItems = selected.filter { it.zone == zone }
                        val units = zoneItems.sumOf { it.quantity }
                        val volume = zoneItems.sumOf { it.totalVolumeL }
                        Text(copy.zone(zone.label, units, volume),
                            color = PackGuideColors.primaryText, style = Typography.bodyMedium)
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
            if (selected.isEmpty()) {
                item { Text("No hay artículos seleccionados.", color = PackGuideColors.secondaryText) }
            }
            items(selected, key = { it.id }) { item ->
                TechCard(borderColor = PackGuideColors.border) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(item.icon, contentDescription = null, tint = PackGuideColors.primaryText)
                        Text("  ${item.name}", modifier = Modifier.weight(1f),
                            color = PackGuideColors.primaryText, style = Typography.titleMedium)
                        Text("${item.packedQuantity}/${item.quantity}", color = PackGuideColors.primaryText)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("${item.zone.label} · ${formatLiters(item.totalVolumeL)} L",
                        color = PackGuideColors.secondaryText, style = Typography.bodySmall)
                }
            }
            item {
                AsciiButton("EDITAR CATÁLOGO", onClick = { onNavigateToTab?.invoke(NavTab.Catalog) },
                    modifier = Modifier.fillMaxWidth(), isPrimary = false,
                    icon = Icons.Outlined.Inventory2)
                Spacer(Modifier.height(8.dp))
                AsciiButton("CONTINUAR EMPACANDO", onClick = { onNavigateToTab?.invoke(NavTab.Guide) },
                    modifier = Modifier.fillMaxWidth(), isPrimary = true)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
