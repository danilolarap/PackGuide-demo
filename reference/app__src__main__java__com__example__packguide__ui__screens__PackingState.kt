package com.example.packguide.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.DryCleaning
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.packguide.scanning.PackingRules

/** Catalog reference envelope, not a measurement of a particular user's belongings. */
data class ItemDimensions(val lengthCm: Int, val widthCm: Int, val heightCm: Int) {
    val rectangularVolumeL: Double get() = PackingRules.rectangularVolumeL(lengthCm, widthCm, heightCm)
    val label: String get() = "$lengthCm × $widthCm × $heightCm cm"
}

data class CatalogItemData(
    val id: String,
    val name: String,
    val category: String,
    val dimensionsCm: ItemDimensions,
    // Legacy packing estimate per unit, independent of the reference envelope above.
    // No calibrated compression factors or measured object volumes exist in the catalog.
    val unitVolumeL: Double,
    val icon: ImageVector,
    val quantity: Int,
    val packedQuantity: Int = 0,
    val zone: PackingZone = PackingZone.Base
) {
    val dimensions: String get() = dimensionsCm.label
    val totalVolumeL: Double get() = PackingRules.totalVolumeL(listOf(quantity to unitVolumeL))
    init {
        require(quantity >= 0 && packedQuantity in 0..quantity)
        require(unitVolumeL.isFinite() && unitVolumeL >= 0)
        require(listOf(dimensionsCm.lengthCm, dimensionsCm.widthCm, dimensionsCm.heightCm).all { it in 1..300 })
    }
}

enum class PackingZone(val label: String) {
    Base("BASE"), Center("CENTRO"), Top("ACCESO RÁPIDO");
    fun next(): PackingZone = entries[(ordinal + 1) % entries.size]
}

enum class LuggagePreset(val label: String, val heightCm: Int, val widthCm: Int, val depthCm: Int) {
    Personal("MANO", 45, 35, 20),
    Cabin("CABINA", 55, 35, 25),
    Checked("BODEGA", 75, 50, 30);

    val capacityL: Double get() = PackingRules.rectangularVolumeL(heightCm, widthCm, depthCm)
}

private fun defaultCatalogItems() = listOf(
        CatalogItemData("ITEM-01", "CAMISETAS", "ROPA", ItemDimensions(28, 20, 4), 1.2, Icons.Outlined.Checkroom, 3),
        CatalogItemData("ITEM-02", "PANTALONES", "ROPA", ItemDimensions(38, 26, 6), 2.8, Icons.Outlined.DryCleaning, 2),
        CatalogItemData("ITEM-03", "CALZADO", "CALZADO", ItemDimensions(32, 22, 12), 4.5, Icons.AutoMirrored.Outlined.DirectionsWalk, 1),
        CatalogItemData("ITEM-04", "CHAQUETA", "ROPA", ItemDimensions(40, 28, 8), 3.2, Icons.Outlined.Checkroom, 1),
        CatalogItemData("ITEM-05", "NECESER", "ASEO", ItemDimensions(22, 14, 8), 1.5, Icons.Outlined.MedicalServices, 1),
        CatalogItemData("ITEM-06", "CARGADORES & TECH", "TECH", ItemDimensions(18, 12, 4), 0.8, Icons.Outlined.Devices, 1),
        CatalogItemData("ITEM-07", "ROPA INTERIOR", "ROPA", ItemDimensions(18, 12, 3), 0.3, Icons.Outlined.Checkroom, 0),
        CatalogItemData("ITEM-08", "MEDIAS", "ROPA", ItemDimensions(15, 10, 3), 0.2, Icons.Outlined.Checkroom, 0),
        CatalogItemData("ITEM-09", "PIJAMA", "ROPA", ItemDimensions(30, 22, 5), 1.4, Icons.Outlined.Checkroom, 0),
        CatalogItemData("ITEM-10", "VESTIDO", "ROPA", ItemDimensions(35, 25, 5), 1.8, Icons.Outlined.Checkroom, 0),
        CatalogItemData("ITEM-11", "FALDA", "ROPA", ItemDimensions(28, 20, 4), 1.0, Icons.Outlined.Checkroom, 0),
        CatalogItemData("ITEM-12", "SHORTS", "ROPA", ItemDimensions(28, 20, 4), 1.0, Icons.Outlined.Checkroom, 0),
        CatalogItemData("ITEM-13", "SUÉTER", "ROPA", ItemDimensions(35, 28, 7), 2.3, Icons.Outlined.Checkroom, 0),
        CatalogItemData("ITEM-14", "CAMISA", "ROPA", ItemDimensions(30, 23, 4), 1.2, Icons.Outlined.Checkroom, 0),
        CatalogItemData("ITEM-15", "TRAJE DE BAÑO", "ROPA", ItemDimensions(20, 15, 2), 0.3, Icons.Outlined.Checkroom, 0),
        CatalogItemData("ITEM-16", "ZAPATOS FORMALES", "CALZADO", ItemDimensions(34, 23, 12), 5.0, Icons.AutoMirrored.Outlined.DirectionsWalk, 0),
        CatalogItemData("ITEM-17", "SANDALIAS", "CALZADO", ItemDimensions(28, 20, 6), 2.0, Icons.AutoMirrored.Outlined.DirectionsWalk, 0),
        CatalogItemData("ITEM-18", "PANTUFLAS", "CALZADO", ItemDimensions(28, 18, 7), 2.1, Icons.AutoMirrored.Outlined.DirectionsWalk, 0),
        CatalogItemData("ITEM-19", "CEPILLO DE DIENTES", "ASEO", ItemDimensions(20, 3, 3), 0.1, Icons.Outlined.MedicalServices, 0),
        CatalogItemData("ITEM-20", "PASTA DENTAL", "ASEO", ItemDimensions(16, 5, 4), 0.2, Icons.Outlined.MedicalServices, 0),
        CatalogItemData("ITEM-21", "DESODORANTE", "ASEO", ItemDimensions(14, 5, 5), 0.3, Icons.Outlined.MedicalServices, 0),
        CatalogItemData("ITEM-22", "CHAMPÚ", "ASEO", ItemDimensions(16, 6, 6), 0.6, Icons.Outlined.MedicalServices, 0),
        CatalogItemData("ITEM-23", "JABÓN", "ASEO", ItemDimensions(10, 7, 4), 0.3, Icons.Outlined.MedicalServices, 0),
        CatalogItemData("ITEM-24", "PROTECTOR SOLAR", "ASEO", ItemDimensions(15, 6, 5), 0.5, Icons.Outlined.MedicalServices, 0),
        CatalogItemData("ITEM-25", "MEDICAMENTOS", "ASEO", ItemDimensions(18, 12, 5), 0.8, Icons.Outlined.MedicalServices, 0),
        CatalogItemData("ITEM-26", "LENTES DE CONTACTO", "ASEO", ItemDimensions(12, 8, 3), 0.2, Icons.Outlined.MedicalServices, 0),
        CatalogItemData("ITEM-27", "TELÉFONO", "TECH", ItemDimensions(16, 8, 2), 0.3, Icons.Outlined.Devices, 0),
        CatalogItemData("ITEM-28", "TABLETA", "TECH", ItemDimensions(26, 18, 2), 0.7, Icons.Outlined.Devices, 0),
        CatalogItemData("ITEM-29", "PORTÁTIL", "TECH", ItemDimensions(35, 25, 3), 2.6, Icons.Outlined.Devices, 0),
        CatalogItemData("ITEM-30", "AURICULARES", "TECH", ItemDimensions(18, 15, 8), 1.0, Icons.Outlined.Devices, 0),
        CatalogItemData("ITEM-31", "BATERÍA EXTERNA", "TECH", ItemDimensions(15, 8, 3), 0.4, Icons.Outlined.Devices, 0),
        CatalogItemData("ITEM-32", "ADAPTADOR DE ENCHUFE", "TECH", ItemDimensions(8, 7, 5), 0.3, Icons.Outlined.Devices, 0),
        CatalogItemData("ITEM-33", "CÁMARA", "TECH", ItemDimensions(18, 13, 10), 1.7, Icons.Outlined.Devices, 0),
        CatalogItemData("ITEM-34", "TARJETAS DE MEMORIA", "TECH", ItemDimensions(8, 6, 2), 0.1, Icons.Outlined.Devices, 0),
        CatalogItemData("ITEM-35", "CABLE USB", "TECH", ItemDimensions(12, 9, 3), 0.2, Icons.Outlined.Devices, 0),
        CatalogItemData("ITEM-36", "RELOJ", "TECH", ItemDimensions(10, 8, 5), 0.4, Icons.Outlined.Devices, 0),
    )

class LuggagePacking(val type: LuggagePreset, initialItems: List<CatalogItemData>) {
    init { require(initialItems.map { it.id }.distinct().size == initialItems.size) }
    val dimensionsConfirmed = mutableStateOf(false)
    val heightCm = mutableStateOf(type.heightCm)
    val widthCm = mutableStateOf(type.widthCm)
    val depthCm = mutableStateOf(type.depthCm)
    val items = mutableStateListOf<CatalogItemData>().apply { addAll(initialItems) }

    val estimate get() = PackingRules.estimate(heightCm.value, widthCm.value, depthCm.value,
        items.map { it.quantity to it.unitVolumeL })
    val capacityL: Double get() = estimate.capacityL
    val usedL: Double get() = estimate.usedL
    val packedUnits: Int get() = items.sumOf { it.packedQuantity }
    val selectedUnits: Int get() = items.sumOf { it.quantity }
    val progressPercent: Int get() = PackingRules.progressPercent(packedUnits, selectedUnits)

    fun updateItem(id: String, transform: (CatalogItemData) -> CatalogItemData) {
        val index = items.indexOfFirst { it.id == id }
        if (index >= 0) {
            val updated = transform(items[index])
            require(updated.id == id) { "Updating an item must preserve its identity" }
            items[index] = updated
        }
    }
}

class PackingState {
    val catalogItems: List<CatalogItemData> = defaultCatalogItems()
    private val luggage = mutableStateMapOf(
        LuggagePreset.Cabin to LuggagePacking(LuggagePreset.Cabin, catalogItems)
    )
    private val activeType = mutableStateOf(LuggagePreset.Cabin)
    val selectedLuggage: List<LuggagePreset> get() = LuggagePreset.entries.filter { it in luggage }
    val activeLuggage: LuggagePreset get() = activeType.value
    val activeBag: LuggagePacking get() = bag(activeLuggage)
    fun bag(type: LuggagePreset): LuggagePacking = requireNotNull(luggage[type])

    // Removing a bag removes its assignments; they are never silently moved to another bag.
    fun toggleLuggage(type: LuggagePreset) {
        if (type in luggage) {
            if (luggage.size == 1) return
            luggage.remove(type)
            if (activeLuggage == type) activeType.value = selectedLuggage.first()
        } else {
            luggage[type] = LuggagePacking(type, catalogItems.map { it.copy(quantity = 0, packedQuantity = 0) })
        }
    }

    fun selectLuggage(type: LuggagePreset) {
        if (type in luggage) activeType.value = type
    }

    val allDimensionsConfirmed: Boolean get() = selectedLuggage.all { bag(it).dimensionsConfirmed.value }
    val totalCapacityL: Double get() = selectedLuggage.sumOf { bag(it).capacityL }
    val totalUsedL: Double get() = selectedLuggage.sumOf { bag(it).usedL }
    val totalSelectedUnits: Int get() = selectedLuggage.sumOf { bag(it).selectedUnits }
    val totalPackedUnits: Int get() = selectedLuggage.sumOf { bag(it).packedUnits }

    val items get() = activeBag.items
    val heightCm get() = activeBag.heightCm
    val widthCm get() = activeBag.widthCm
    val depthCm get() = activeBag.depthCm
    val dimensionsConfirmed get() = activeBag.dimensionsConfirmed
    val capacityL get() = activeBag.capacityL
    val usedL get() = activeBag.usedL
    val selectedUnits get() = activeBag.selectedUnits
    val packedUnits get() = activeBag.packedUnits

    fun updateItem(id: String, type: LuggagePreset = activeLuggage, transform: (CatalogItemData) -> CatalogItemData) {
        luggage[type]?.updateItem(id, transform)
    }
}
