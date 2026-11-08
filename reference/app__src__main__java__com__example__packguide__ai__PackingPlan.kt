package com.example.packguide.ai

import com.example.packguide.ui.screens.LuggagePacking

/** Immutable request input. Packing checkmarks do not change the desired layout. */
data class PackingPlan(val dimensions: List<Int>, val contents: List<PlanItem>) {
    val description: String get() = "Medidas interiores: ${dimensions.joinToString(" × ")} cm.\n" +
        "Capacidad interior total: ${com.example.packguide.scanning.PackingRules.rectangularVolumeL(dimensions[0], dimensions[1], dimensions[2])} L. Sin reserva adicional.\n" +
        "Las medidas de artículos son referencias; los litros son estimaciones independientes, sin compresión calibrada.\n" +
        contents.joinToString("\n") { "${it.quantity} × ${it.name}; referencia ${it.dimensions}; estimación por unidad ${it.unitVolumeL} L; zona ${it.zone}" }

    companion object {
        fun from(bag: LuggagePacking) = PackingPlan(
            listOf(bag.heightCm.value, bag.widthCm.value, bag.depthCm.value),
            bag.items.filter { it.quantity > 0 }.map { PlanItem(it.name, it.quantity, it.dimensions, it.zone.label, it.unitVolumeL, it.category) }
        )
    }
}

data class PlanItem(val name: String, val quantity: Int, val dimensions: String, val zone: String,
                    val unitVolumeL: Double, val category: String)
