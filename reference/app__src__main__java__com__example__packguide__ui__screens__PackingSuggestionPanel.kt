package com.example.packguide.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.packguide.scanning.PackingRules
import com.example.packguide.ui.components.TechCard
import com.example.packguide.ui.localization.Text
import com.example.packguide.ui.theme.AccentRedLed
import com.example.packguide.ui.theme.PackGuideColors
import com.example.packguide.ui.theme.Typography
import java.util.Locale
import com.example.packguide.ui.localization.LocalAppLanguage
import com.example.packguide.ui.localization.PackingText

@Composable
fun PackingSuggestionPanel(packing: PackingState) {
    val estimate = packing.activeBag.estimate
    val copy = PackingText(LocalAppLanguage.current)
    var expanded by rememberSaveable { mutableStateOf(false) }
    TechCard(headerTag = "SUGERENCIA ORIENTATIVA") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Volumen estimado / capacidad interior total", color = PackGuideColors.secondaryText,
                style = Typography.bodySmall)
            androidx.compose.material3.Text(
                copy.volume(estimate),
                color = if (estimate.exceedsCapacity) AccentRedLed else PackGuideColors.primaryText,
                style = Typography.displaySmall)
            if (!packing.dimensionsConfirmed.value) Text(
                "Confirma las medidas en Escáner antes de usar esta estimación.",
                color = PackGuideColors.secondaryText, style = Typography.bodySmall)
            Text(copy.recommendation(estimate),
                color = PackGuideColors.primaryText, style = Typography.bodySmall)
            TextButton(onClick = { expanded = !expanded }) {
                Text(if (expanded) "OCULTAR DETALLES" else "VER CRITERIOS Y CONSEJOS",
                    color = PackGuideColors.primaryText, style = Typography.labelMedium)
            }
            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Capacidad interior = alto × ancho × fondo / 1000. Sin descuento adicional. Volumen estimado = suma de litros por unidad × cantidad seleccionada.",
                        color = PackGuideColors.secondaryText, style = Typography.bodySmall)
                    Text("Medidas de referencia; litros por unidad estimados de forma independiente. No hay un factor de compresión medido.",
                        color = PackGuideColors.secondaryText, style = Typography.bodySmall)
                    Text("Los litros por artículo son supuestos del catálogo, no medidas de la foto. No es una simulación física ni garantiza que todo quepa.",
                        color = PackGuideColors.secondaryText, style = Typography.bodySmall)
                    Text("Calzado en bolsas junto a las ruedas; pantalones y chaquetas en la base. Enrolla camisas resistentes en el centro; vestidos delicados arriba. Accesorios en un bolsillo accesible.",
                        color = PackGuideColors.primaryText, style = Typography.bodySmall)
                }
            }
        }
    }
}
