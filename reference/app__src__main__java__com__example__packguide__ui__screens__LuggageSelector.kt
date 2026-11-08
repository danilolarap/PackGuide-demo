package com.example.packguide.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.packguide.ui.localization.Text
import com.example.packguide.ui.theme.PackGuideColors
import com.example.packguide.ui.theme.Typography

@Composable
fun LuggageSelector(packing: PackingState) {
    Column {
        Text("MALETA A ORGANIZAR", style = Typography.labelMedium, color = PackGuideColors.secondaryText)
        Row(Modifier.fillMaxWidth().selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            packing.selectedLuggage.forEach { type ->
                FilterChip(
                    selected = packing.activeLuggage == type,
                    onClick = { packing.selectLuggage(type) },
                    label = { Text(type.label, style = Typography.labelSmall) }
                )
            }
        }
    }
}

@Composable
fun TripLuggageSelection(packing: PackingState, onToggle: (LuggagePreset) -> Unit) {
    Column {
        Text("EQUIPAJE DEL VIAJE", style = Typography.titleMedium, color = PackGuideColors.primaryText)
        Text("Selecciona todas las maletas que llevarás. Debe quedar al menos una.",
            style = Typography.bodySmall, color = PackGuideColors.secondaryText)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            LuggagePreset.entries.forEach { type ->
                val selected = type in packing.selectedLuggage
                Column(Modifier.weight(1f).selectable(
                    selected = selected,
                    enabled = !selected || packing.selectedLuggage.size > 1,
                    role = Role.Checkbox,
                    onClick = { onToggle(type) }
                ).padding(vertical = 8.dp)) {
                    Checkbox(checked = selected, onCheckedChange = null)
                    Text(type.label, style = Typography.labelMedium, color = PackGuideColors.primaryText)
                }
            }
        }
    }
}
