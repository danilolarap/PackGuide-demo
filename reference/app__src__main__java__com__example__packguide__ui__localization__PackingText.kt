package com.example.packguide.ui.localization

import com.example.packguide.scanning.PackingEstimate
import com.example.packguide.settings.AppLanguage
import java.util.Locale

/** One decimal separator/precision across every packing screen. Round only for display. */
fun formatLiters(value: Double): String =
    if (value > 0 && value < 0.05) "<0.1" else String.format(Locale.US, "%.1f", value)

class PackingText(private val language: AppLanguage) {
    private fun choose(es: String, en: String) = if (language == AppLanguage.English) en else es
    fun volume(estimate: PackingEstimate) = "${formatLiters(estimate.usedL)} L / ${formatLiters(estimate.capacityL)} L"
    fun balance(estimate: PackingEstimate): String = if (estimate.exceedsCapacity)
        choose("EXCESO EST.: ${formatLiters(estimate.excessL)} L", "EST. EXCESS: ${formatLiters(estimate.excessL)} L")
    else choose("ESPACIO EST.: ${formatLiters(estimate.freeL)} L", "EST. FREE SPACE: ${formatLiters(estimate.freeL)} L")
    fun recommendation(estimate: PackingEstimate) = balance(estimate) + "; " + if (estimate.exceedsCapacity)
        choose("reduce artículos o elige una maleta mayor.", "reduce items or choose a larger bag.")
    else choose("comprueba el ajuste al empacar.", "check the fit while packing.")
    fun itemTotal(units: Int, volume: Double) = choose("TOTAL: $units unidades · ${formatLiters(volume)} L",
        "TOTAL: $units units · ${formatLiters(volume)} L")
    fun counts(selected: Int, packed: Int) = choose("$selected unidades seleccionadas · $packed empacadas",
        "$selected units selected · $packed packed")
    fun progress(packed: Int, selected: Int, percent: Int) = choose("$packed de $selected unidades empacadas · $percent%",
        "$packed of $selected units packed · $percent%")
    fun selectedBag(label: String) = choose("MALETA SELECCIONADA: ", "SELECTED BAG: ") + localizedText(label, language)
    fun itemEstimate(volume: Double) = choose("ESTIMACIÓN POR UNIDAD: ", "ESTIMATE PER UNIT: ") + "${formatLiters(volume)} L"
    fun itemGeometry(volume: Double) = choose("VOLUMEN RECTANGULAR DE REFERENCIA: ", "REFERENCE RECTANGULAR VOLUME: ") + "${formatLiters(volume)} L"
    fun bagSummary(label: String, estimate: PackingEstimate, packed: Int, selected: Int) =
        "${localizedText(label, language)} · ${volume(estimate)} · $packed/$selected " + choose("empacados", "packed")
    fun occupancy(percent: Int) = choose("OCUPACIÓN EST.: $percent%", "EST. OCCUPANCY: $percent%")
    fun zone(label: String, units: Int, volume: Double) = "${localizedText(label, language)}: $units " +
        choose("unidades", "units") + " · ${formatLiters(volume)} L"
}
