package com.nova.sensitivity.data

import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Produces *starting* values. This is a heuristic, not a measurement: each setting has a base
 * value and a small bounded adjustment from the device tier and screen size is applied.
 */
object Recommender {
    fun factor(d: DeviceInfo): Float {
        val tierAdj = when (d.tier) {
            Tier.BUDGET -> 0.05f
            Tier.MID -> 0f
            Tier.HIGH -> -0.03f
        }
        val sizeAdj = (6.4f - d.screenInches.coerceIn(5f, 8f)) * 0.04f
        return (1f + tierAdj + sizeAdj).coerceIn(0.88f, 1.12f)
    }

    fun snap(def: SettingDef, v: Float): Float {
        val c = v.coerceIn(def.min, def.max)
        val steps = ((c - def.min) / def.step).roundToInt()
        return (def.min + steps * def.step).coerceIn(def.min, def.max)
    }

    fun recommend(game: GameDef, device: DeviceInfo): Map<String, Float> {
        val f = factor(device)
        return game.settings.associate { it.key to snap(it, it.base * f) }
    }

    /** Size of one fine-tune nudge, as a fraction of the slider range (never less than one step). */
    fun amount(def: SettingDef, fraction: Float): Float = max(def.step, (def.max - def.min) * abs(fraction))

    fun format(def: SettingDef, v: Float): String {
        val n = if (def.step >= 1f) v.roundToInt().toString() else String.format(Locale.US, "%.2f", v)
        return n + def.unit
    }

    fun exportText(game: GameDef, device: DeviceInfo, values: Map<String, Float>): String = buildString {
        appendLine("NOVA Sensitivity - ${game.name}")
        appendLine("Device: ${device.name}")
        appendLine()
        game.settings.forEach { s -> appendLine("${s.label}: ${format(s, values[s.key] ?: s.base)}") }
        appendLine()
        append("Starting values only. Adjust to your aim style.")
    }
}
