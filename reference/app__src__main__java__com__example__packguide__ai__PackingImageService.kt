package com.example.packguide.ai

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.example.packguide.scanning.PackingRules
import com.example.packguide.settings.AppLanguage
import com.example.packguide.ui.localization.PackingText
import com.example.packguide.ui.localization.localizedText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

/** UI-independent boundary for a future authenticated server implementation. No provider secrets. */
fun interface PackingImageService {
    suspend fun generate(plan: PackingPlan, photo: Bitmap, language: AppLanguage): Bitmap
}

/** A local composition, not photo editing, object recognition, or a physical packing simulation. */
class OfflinePackingImageService : PackingImageService {
    override suspend fun generate(plan: PackingPlan, photo: Bitmap, language: AppLanguage): Bitmap =
        withContext(Dispatchers.Default) {
            require(!photo.isRecycled && photo.width > 0 && photo.height > 0)
            val layout = PackingPreviewLayout.from(plan)
            val english = language == AppLanguage.English
            fun text(es: String, en: String) = if (english) en else es
            val result = Bitmap.createBitmap(1200, layout.height, Bitmap.Config.ARGB_8888)
            try {
                val canvas = Canvas(result)
                val ink = Color.rgb(15, 15, 17)
                val muted = Color.rgb(94, 100, 110)
                val red = Color.rgb(210, 32, 32)
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                fun label(value: String, x: Float, y: Float, size: Float = 28f, color: Int = ink) {
                    paint.style = Paint.Style.FILL
                    paint.color = color
                    paint.textSize = size
                    paint.typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL)
                    canvas.drawText(value, x, y, paint)
                }
                fun box(rect: RectF, fill: Int, stroke: Int = Color.rgb(209, 213, 219), radius: Float = 12f) {
                    paint.style = Paint.Style.FILL; paint.color = fill
                    canvas.drawRoundRect(rect, radius, radius, paint)
                    paint.style = Paint.Style.STROKE; paint.strokeWidth = 2f; paint.color = stroke
                    canvas.drawRoundRect(rect, radius, radius, paint)
                    paint.style = Paint.Style.FILL
                }
                fun fitLabel(value: String, x: Float, y: Float, width: Float, size: Float, color: Int = ink) {
                    paint.textSize = size
                    val count = paint.breakText(value, true, width, null)
                    label(if (count < value.length) value.take((count - 1).coerceAtLeast(0)) + "…" else value,
                        x, y, size, color)
                }
                canvas.drawColor(Color.rgb(242, 244, 247))
                label(text("VISTA ORGANIZADA DE EMPAQUE", "ORGANIZED PACKING PREVIEW"), 48f, 65f, 36f)
                label(text("Composición local · foto original + inventario", "Local composition · original photo + inventory"), 48f, 105f, 26f, muted)
                box(RectF(48f, 140f, 600f, 470f), Color.WHITE)
                val scale = minOf(528f / photo.width, 306f / photo.height)
                val photoWidth = photo.width * scale; val photoHeight = photo.height * scale
                val photoRect = RectF(324f - photoWidth / 2, 305f - photoHeight / 2,
                    324f + photoWidth / 2, 305f + photoHeight / 2)
                canvas.drawBitmap(photo, null, photoRect, paint)
                val estimate = PackingRules.estimate(plan.dimensions[0], plan.dimensions[1], plan.dimensions[2],
                    plan.contents.map { it.quantity to it.unitVolumeL })
                val copy = PackingText(language)
                label(text("FOTO DE REFERENCIA", "REFERENCE PHOTO"), 48f, 505f, 24f, muted)
                label(text("INVENTARIO SELECCIONADO", "SELECTED INVENTORY"), 640f, 180f, 26f)
                label("${plan.contents.sumOf { it.quantity }} " + text("unidades", "units"), 640f, 230f, 34f)
                label(copy.volume(estimate), 640f, 285f, 30f)
                label(text("Estimación / capacidad interior", "Estimate / interior capacity"), 640f, 325f, 24f, muted)
                label(copy.balance(estimate), 640f, 380f, 27f, if (estimate.exceedsCapacity) red else ink)
                label(plan.dimensions.joinToString(" × ") + " cm", 640f, 430f, 27f, muted)
                // A suitcase-shaped diagram below the unmodified reference photo.
                box(RectF(480f, 535f, 720f, 575f), Color.WHITE, ink)
                box(RectF(48f, 565f, 1152f, layout.height - 100f), Color.WHITE, ink, 28f)
                var y = 610f
                layout.zones.forEach { zone ->
                    currentCoroutineContext().ensureActive()
                    label(localizedText(zone.label, language), 80f, y, 30f)
                    y += 22f
                    if (zone.items.isEmpty()) {
                        label(text("Sin artículos asignados", "No assigned items"), 90f, y + 30f, 25f, muted)
                        y += 62f
                    } else {
                        zone.items.forEachIndexed { index, item ->
                            val x = if (index % 2 == 0) 80f else 610f
                            val rowY = y + (index / 2) * 112f
                            box(RectF(x, rowY, x + 510f, rowY + 98f), Color.rgb(245, 247, 249))
                            // Simple category illustrations are schematic, never detected objects.
                            paint.color = ink; paint.style = Paint.Style.STROKE; paint.strokeWidth = 3f
                            when (item.category) {
                                "CALZADO" -> {
                                    canvas.drawRoundRect(RectF(x + 16f, rowY + 48f, x + 70f, rowY + 72f), 8f, 8f, paint)
                                    canvas.drawLine(x + 16f, rowY + 48f, x + 24f, rowY + 28f, paint)
                                    canvas.drawLine(x + 24f, rowY + 28f, x + 42f, rowY + 48f, paint)
                                }
                                "ASEO" -> {
                                    canvas.drawRoundRect(RectF(x + 25f, rowY + 32f, x + 60f, rowY + 76f), 5f, 5f, paint)
                                    canvas.drawRect(x + 33f, rowY + 20f, x + 52f, rowY + 32f, paint)
                                }
                                "TECH" -> {
                                    canvas.drawRoundRect(RectF(x + 24f, rowY + 22f, x + 62f, rowY + 76f), 5f, 5f, paint)
                                    canvas.drawLine(x + 32f, rowY + 65f, x + 54f, rowY + 65f, paint)
                                }
                                else -> {
                                    canvas.drawRoundRect(RectF(x + 18f, rowY + 30f, x + 69f, rowY + 70f), 8f, 8f, paint)
                                    canvas.drawLine(x + 18f, rowY + 49f, x + 69f, rowY + 49f, paint)
                                }
                            }
                            paint.style = Paint.Style.FILL
                            fitLabel("${item.quantity} × ${localizedText(item.name, language)}", x + 88f, rowY + 38f, 405f, 26f)
                            fitLabel(item.dimensions, x + 88f, rowY + 73f, 405f, 23f, muted)
                        }
                        y += ((zone.items.size + 1) / 2) * 112f + 8f
                    }
                    y += 34f
                }
                label(text("Esquema orientativo; no mide ni garantiza el ajuste físico.",
                    "Illustrative layout; it does not measure or guarantee physical fit."), 48f, layout.height - 50f, 26f, muted)
                currentCoroutineContext().ensureActive()
                result
            } catch (error: Throwable) {
                result.recycle()
                throw error
            }
        }
}

data class PreviewZone(val label: String, val items: List<PlanItem>)
data class PackingPreviewLayout(val zones: List<PreviewZone>, val height: Int) {
    companion object {
        fun from(plan: PackingPlan): PackingPreviewLayout {
            require(plan.dimensions.size == 3 && plan.dimensions.all { it in 1..300 })
            require(plan.contents.isNotEmpty() && plan.contents.size <= 64)
            val order = listOf("ACCESO RÁPIDO", "CENTRO", "BASE")
            require(plan.contents.all { it.quantity > 0 && it.zone in order && it.unitVolumeL.isFinite() && it.unitVolumeL >= 0 })
            val zones = order.map { label -> PreviewZone(label, plan.contents.filter { it.zone == label }) }
            val height = 750 + zones.sumOf { 56 + if (it.items.isEmpty()) 62 else ((it.items.size + 1) / 2) * 112 + 8 }
            require(height <= 4096)
            return PackingPreviewLayout(zones, height)
        }
    }
}
