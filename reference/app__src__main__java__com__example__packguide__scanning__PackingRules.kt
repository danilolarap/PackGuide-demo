package com.example.packguide.scanning

import java.math.BigDecimal

data class PackingEstimate(val capacityL: Double, val usedL: Double) {
    val exceedsCapacity: Boolean get() = usedL > capacityL
    val freeL: Double get() = (capacityL - usedL).coerceAtLeast(0.0)
    val excessL: Double get() = (usedL - capacityL).coerceAtLeast(0.0)
    val occupancy: Double get() = if (capacityL > 0) usedL / capacityL else 0.0
    val occupancyPercent: Int get() = (occupancy * 100).toInt()
}

object PackingRules {
    /** Interior rectangular capacity; no hidden allowance or compression factor. */
    fun rectangularVolumeL(height: Int, width: Int, depth: Int): Double {
        require(listOf(height, width, depth).all { it in 1..300 })
        return height.toDouble() * width * depth / 1000.0
    }

    fun totalVolumeL(volumes: List<Pair<Int, Double>>): Double {
        require(volumes.all { (count, volume) -> count >= 0 && volume.isFinite() && volume >= 0 })
        // Sum decimal catalog values exactly; binary floating point must not turn an exact fit
        // into an excess. No display rounding is applied to these calculations.
        return volumes.fold(BigDecimal.ZERO) { total, (count, volume) ->
            total + BigDecimal.valueOf(volume).multiply(BigDecimal.valueOf(count.toLong()))
        }.toDouble()
    }

    fun progressPercent(packed: Int, selected: Int): Int {
        require(selected >= 0 && packed in 0..selected)
        return if (selected == 0) 0 else (packed.toLong() * 100 / selected).toInt()
    }

    fun estimate(height: Int, width: Int, depth: Int, volumes: List<Pair<Int, Double>>): PackingEstimate {
        return PackingEstimate(rectangularVolumeL(height, width, depth), totalVolumeL(volumes))
    }
}
