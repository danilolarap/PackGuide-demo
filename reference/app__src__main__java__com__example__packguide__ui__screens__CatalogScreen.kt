package com.example.packguide.ui.screens


import com.example.packguide.ui.theme.PackGuideColors

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import com.example.packguide.ui.localization.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.packguide.ui.components.HudHeader
import com.example.packguide.ui.components.LedIndicator
import com.example.packguide.ui.components.TechBadge
import com.example.packguide.ui.theme.AccentGreenLed
import com.example.packguide.ui.theme.AccentRedLed
import com.example.packguide.ui.theme.TechMonospaceFontFamily
import com.example.packguide.ui.theme.Typography
import java.util.Locale
import com.example.packguide.ui.localization.LocalAppLanguage
import com.example.packguide.ui.localization.PackingText

@Composable
fun CatalogScreen(
    modifier: Modifier = Modifier,
    packing: PackingState,
    onCreatePlan: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("TODOS") }

    val categories = listOf("TODOS", "ROPA", "CALZADO", "ASEO", "TECH")

    // Items list initialized with exact specification defaults
    val itemsList = packing.catalogItems

    val filteredItems = remember(itemsList, selectedCategory, searchQuery) { itemsList.filter { item ->
        val matchesCategory = (selectedCategory == "TODOS") || (item.category == selectedCategory)
        val matchesQuery = searchQuery.isEmpty() || item.name.contains(searchQuery, ignoreCase = true) || item.category.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesQuery
    } }

    val copy = PackingText(LocalAppLanguage.current)
    val estimate = packing.activeBag.estimate

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PackGuideColors.background)
    ) {
        // Encabezado del catálogo
        HudHeader(
            title = "CATÁLOGO DE ÍTEMS",
            sysCode = "",
            statusText = "",
            ledColor = AccentGreenLed
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {
            // --- 2. SEARCH INPUT FIELD ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PackGuideColors.surface, shape = RoundedCornerShape(16.dp))
                    .border(1.dp, PackGuideColors.border, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Buscar",
                        tint = PackGuideColors.primaryText,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "BUSCAR ÍTEM... [ _ ]",
                                style = Typography.bodyMedium,
                                fontFamily = TechMonospaceFontFamily,
                                color = PackGuideColors.secondaryText
                            )
                        }
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            textStyle = Typography.bodyMedium.copy(
                                color = PackGuideColors.primaryText,
                                fontFamily = TechMonospaceFontFamily
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- 3. CATEGORY FILTER CHIPS ---
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isActive = selectedCategory == category
                    val chipText = "[$category]"

                    Box(
                        modifier = Modifier
                            .background(
                                if (isActive) PackGuideColors.primaryText else PackGuideColors.surface,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                1.dp,
                                if (isActive) PackGuideColors.primaryText else PackGuideColors.border,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isActive) {
                                LedIndicator(color = AccentRedLed, size = 5.dp, isPulsing = false)
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = chipText,
                                style = Typography.labelSmall.copy(fontSize = 11.sp),
                                fontFamily = TechMonospaceFontFamily,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                color = if (isActive) PackGuideColors.surface else PackGuideColors.primaryText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- 4. ITEM INVENTORY LIST ---
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    LuggageSelector(packing)
                    Text("DESTINOS DEL VIAJE", style = Typography.labelMedium, color = PackGuideColors.primaryText)
                    Text("Reparte las cantidades de cada artículo entre las maletas elegidas en Escáner.",
                        style = Typography.bodySmall, color = PackGuideColors.secondaryText)
                    packing.selectedLuggage.forEach { type ->
                        val bag = packing.bag(type)
                        val over = bag.estimate.exceedsCapacity
                        Text("${type.label}: ${copy.volume(bag.estimate)} · ${copy.balance(bag.estimate)}",
                            style = Typography.bodySmall,
                            color = if (over) AccentRedLed else PackGuideColors.primaryText)
                    }
                    Text("Volumen estimado / capacidad interior total", style = Typography.bodySmall, color = PackGuideColors.secondaryText)
                    Text("Medidas de referencia; litros por unidad estimados de forma independiente. No hay un factor de compresión medido.",
                        style = Typography.bodySmall, color = PackGuideColors.secondaryText)
                }
                if (filteredItems.isEmpty()) {
                    item {
                        Text("No hay artículos para esta búsqueda.",
                            style = Typography.bodyMedium,
                            color = PackGuideColors.secondaryText,
                            modifier = Modifier.padding(vertical = 24.dp))
                    }
                }
                items(filteredItems, key = { it.id }) { item ->
                    CatalogInventoryCard(item = item, packing = packing)
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // --- 5. BOTTOM CAPACITY TRACKER CARD ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .shadow(0.dp, shape = RoundedCornerShape(20.dp))
                    .background(PackGuideColors.surface, shape = RoundedCornerShape(20.dp))
                    .border(1.dp, PackGuideColors.border, shape = RoundedCornerShape(20.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(copy.selectedBag(packing.activeLuggage.label), style = Typography.labelMedium, color = PackGuideColors.primaryText)
                    Text(copy.counts(packing.selectedUnits, packing.packedUnits),
                        style = Typography.labelMedium,
                        color = PackGuideColors.primaryText)
                    // Metric header & available badge
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Volumen estimado / capacidad interior total", style = Typography.bodySmall, color = PackGuideColors.secondaryText)
                        Text(
                            text = copy.volume(estimate),
                            style = Typography.titleMedium,
                            fontFamily = TechMonospaceFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = PackGuideColors.primaryText
                        )

                        TechBadge(
                            text = copy.balance(estimate),
                            containerColor = PackGuideColors.primaryText,
                            contentColor = PackGuideColors.surface,
                            ledColor = if (estimate.exceedsCapacity) AccentRedLed else AccentGreenLed
                        )
                    }

                    // Visual Segmented Progress Bar (10 block segments)
                    val filledSegmentsCount = (estimate.occupancy * 10).toInt().coerceIn(0, 10)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (i in 1..10) {
                            val isFilled = i <= filledSegmentsCount
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(5.dp)
                                    .background(
                                        if (isFilled) PackGuideColors.primaryText else PackGuideColors.surfaceVariant,
                                        shape = RoundedCornerShape(1.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isFilled) PackGuideColors.primaryText else PackGuideColors.border,
                                        shape = RoundedCornerShape(1.dp)
                                    )
                            )
                        }
                    }

                    com.example.packguide.ui.components.AsciiButton(
                        text = "CREAR PLAN DE EMPACADO",
                        onClick = onCreatePlan,
                        icon = Icons.AutoMirrored.Outlined.ArrowForward,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
