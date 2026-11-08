package com.example.packguide.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.packguide.ui.components.TechBadge
import com.example.packguide.ui.components.TechCard
import com.example.packguide.ui.localization.LocalAppLanguage
import com.example.packguide.ui.localization.Text
import com.example.packguide.ui.localization.localizedText
import com.example.packguide.ui.theme.PackGuideColors
import com.example.packguide.ui.theme.Typography
import java.util.Locale
import com.example.packguide.ui.localization.PackingText
import com.example.packguide.ui.localization.formatLiters

@Composable
fun CatalogInventoryCard(item: CatalogItemData, packing: PackingState) {
    val language = LocalAppLanguage.current
    val copy = PackingText(language)
    val totalQuantity = packing.selectedLuggage.sumOf { type -> packing.bag(type).items.first { it.id == item.id }.quantity }
    val totalVolume = packing.selectedLuggage.sumOf { type -> packing.bag(type).items.first { it.id == item.id }.totalVolumeL }
    TechCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(44.dp).background(PackGuideColors.surfaceVariant, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center) {
                Icon(item.icon, contentDescription = null, tint = PackGuideColors.primaryText, modifier = Modifier.size(22.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(item.name, style = Typography.titleMedium, color = PackGuideColors.primaryText)
                Text("${item.dimensions} · ${copy.itemEstimate(item.unitVolumeL)}", style = Typography.bodySmall,
                    color = PackGuideColors.secondaryText)
            }
        }
        Text(copy.itemGeometry(item.dimensionsCm.rectangularVolumeL), style = Typography.bodySmall,
            color = PackGuideColors.secondaryText)
        Spacer(Modifier.height(12.dp))
        TechBadge(item.category, containerColor = PackGuideColors.surfaceVariant, contentColor = PackGuideColors.secondaryText)
        Spacer(Modifier.height(12.dp))
        Text("DESTINO DEL ARTÍCULO", style = Typography.labelSmall, color = PackGuideColors.secondaryText)
        Spacer(Modifier.height(12.dp))
        packing.selectedLuggage.forEach { type ->
            val assigned = packing.bag(type).items.first { it.id == item.id }
            Row(Modifier.fillMaxWidth()
                .background(PackGuideColors.surfaceVariant, RoundedCornerShape(16.dp))
                .padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(type.label, style = Typography.labelMedium, color = PackGuideColors.primaryText)
                Text("${formatLiters(assigned.totalVolumeL)} L",
                    style = Typography.titleLarge, color = PackGuideColors.primaryText)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    packing.updateItem(item.id, type) { current ->
                        val quantity = (current.quantity - 1).coerceAtLeast(0)
                        current.copy(quantity = quantity, packedQuantity = current.packedQuantity.coerceAtMost(quantity))
                    }
                }, enabled = assigned.quantity > 0,
                    modifier = Modifier.size(48.dp)
                        .background(PackGuideColors.surface, RoundedCornerShape(12.dp))
                        .border(1.dp, PackGuideColors.border, RoundedCornerShape(12.dp))) {
                    Icon(Icons.Outlined.Remove, "${localizedText("Disminuir cantidad", language)}: ${type.label}",
                        tint = if (assigned.quantity > 0) PackGuideColors.primaryText else PackGuideColors.secondaryText.copy(alpha = 0.45f),
                        modifier = Modifier.size(18.dp))
                }
                Text(assigned.quantity.toString(), style = Typography.titleMedium, color = PackGuideColors.primaryText,
                    modifier = Modifier.widthIn(min = 24.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                IconButton(onClick = { packing.updateItem(item.id, type) { it.copy(quantity = it.quantity + 1) } },
                    modifier = Modifier.size(48.dp)
                        .background(PackGuideColors.surface, RoundedCornerShape(12.dp))
                        .border(1.dp, PackGuideColors.border, RoundedCornerShape(12.dp))) {
                    Icon(Icons.Outlined.Add, "${localizedText("Aumentar cantidad", language)}: ${type.label}",
                        tint = PackGuideColors.primaryText, modifier = Modifier.size(18.dp))
                }
            }
        }
            Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(copy.itemTotal(totalQuantity, totalVolume),
            style = Typography.bodySmall, color = PackGuideColors.secondaryText)
    }
}
