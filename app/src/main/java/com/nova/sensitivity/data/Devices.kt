package com.nova.sensitivity.data

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import kotlin.math.sqrt

object DeviceCatalog {
    private class Entry(val info: DeviceInfo, val codes: List<String>)

    private fun e(name: String, brand: String, tier: Tier, inches: Float, vararg codes: String) =
        Entry(DeviceInfo(name, brand, tier, inches), codes.toList())

    private val entries = listOf(
        // Samsung
        e("Galaxy A05", "Samsung", Tier.BUDGET, 6.7f, "SM-A055"),
        e("Galaxy A06", "Samsung", Tier.BUDGET, 6.7f, "SM-A065", "SM-A066"),
        e("Galaxy A15", "Samsung", Tier.BUDGET, 6.5f, "SM-A155", "SM-A156"),
        e("Galaxy A16", "Samsung", Tier.BUDGET, 6.7f, "SM-A165", "SM-A166"),
        e("Galaxy A25", "Samsung", Tier.MID, 6.5f, "SM-A256"),
        e("Galaxy A35", "Samsung", Tier.MID, 6.6f, "SM-A356"),
        e("Galaxy A55", "Samsung", Tier.MID, 6.6f, "SM-A556"),
        e("Galaxy M14", "Samsung", Tier.BUDGET, 6.6f, "SM-M146"),
        e("Galaxy S23", "Samsung", Tier.HIGH, 6.1f, "SM-S911"),
        e("Galaxy S23 Ultra", "Samsung", Tier.HIGH, 6.8f, "SM-S918"),
        e("Galaxy S24", "Samsung", Tier.HIGH, 6.2f, "SM-S921"),
        e("Galaxy S24 Ultra", "Samsung", Tier.HIGH, 6.8f, "SM-S928"),
        e("Galaxy S25 Ultra", "Samsung", Tier.HIGH, 6.9f, "SM-S938"),
        // Xiaomi / Redmi
        e("Redmi 12", "Xiaomi", Tier.BUDGET, 6.79f),
        e("Redmi 13C", "Xiaomi", Tier.BUDGET, 6.74f),
        e("Redmi Note 12", "Xiaomi", Tier.MID, 6.67f),
        e("Redmi Note 13 Pro", "Xiaomi", Tier.MID, 6.67f),
        e("Redmi Note 14 Pro", "Xiaomi", Tier.MID, 6.67f),
        e("Xiaomi 13T", "Xiaomi", Tier.HIGH, 6.67f),
        e("Xiaomi 14", "Xiaomi", Tier.HIGH, 6.36f),
        // POCO
        e("POCO C65", "POCO", Tier.BUDGET, 6.74f),
        e("POCO M6 Pro", "POCO", Tier.MID, 6.79f),
        e("POCO X6 Pro", "POCO", Tier.MID, 6.67f),
        e("POCO F6", "POCO", Tier.HIGH, 6.67f),
        // Realme
        e("Realme C55", "Realme", Tier.BUDGET, 6.72f),
        e("Realme C67", "Realme", Tier.BUDGET, 6.72f),
        e("Realme Narzo 60", "Realme", Tier.MID, 6.43f),
        e("Realme 11 Pro", "Realme", Tier.MID, 6.7f),
        e("Realme GT 6", "Realme", Tier.HIGH, 6.78f),
        // OnePlus
        e("OnePlus Nord CE 3 Lite", "OnePlus", Tier.MID, 6.72f),
        e("OnePlus Nord 4", "OnePlus", Tier.MID, 6.74f),
        e("OnePlus 11", "OnePlus", Tier.HIGH, 6.7f),
        e("OnePlus 12", "OnePlus", Tier.HIGH, 6.82f),
        // Infinix
        e("Infinix Hot 40", "Infinix", Tier.BUDGET, 6.78f),
        e("Infinix Hot 50", "Infinix", Tier.BUDGET, 6.78f),
        e("Infinix Note 40", "Infinix", Tier.MID, 6.78f),
        e("Infinix Zero 30", "Infinix", Tier.MID, 6.78f),
        // Tecno
        e("Tecno Spark 20", "Tecno", Tier.BUDGET, 6.56f),
        e("Tecno Camon 30", "Tecno", Tier.MID, 6.78f),
        e("Tecno Pova 6", "Tecno", Tier.MID, 6.78f),
        // Google Pixel
        e("Pixel 6a", "Google", Tier.MID, 6.1f),
        e("Pixel 7", "Google", Tier.HIGH, 6.3f),
        e("Pixel 8", "Google", Tier.HIGH, 6.2f),
        e("Pixel 8 Pro", "Google", Tier.HIGH, 6.7f),
        e("Pixel 9", "Google", Tier.HIGH, 6.3f),
        // Motorola
        e("Moto G Power", "Motorola", Tier.BUDGET, 6.5f),
        e("Moto G54", "Motorola", Tier.MID, 6.5f, "moto g54"),
        e("Motorola Edge 40", "Motorola", Tier.MID, 6.55f),
    )

    val all: List<DeviceInfo> = entries.map { it.info }

    fun search(query: String): List<DeviceInfo> {
        val parts = query.trim().lowercase().split(" ").filter { it.isNotBlank() }
        if (parts.isEmpty()) return all
        return all.filter { d ->
            val hay = "${d.brand} ${d.name}".lowercase()
            parts.all { hay.contains(it) }
        }
    }

    /** Matches the model/brand Android reports against the catalog. */
    fun match(model: String, brand: String): DeviceInfo? {
        val m = model.trim().uppercase()
        if (m.isEmpty()) return null
        entries.firstOrNull { en -> en.codes.any { m.startsWith(it.uppercase()) } }?.let { return it.info }
        return entries.firstOrNull { en ->
            en.info.name.equals(model.trim(), ignoreCase = true) ||
                "${en.info.brand} ${en.info.name}".equals("${brand.trim()} ${model.trim()}", ignoreCase = true)
        }?.info
    }
}

/**
 * Reads only what Android exposes without any permission: build model/manufacturer,
 * total RAM and screen size. It does not (and cannot) read the marketing name of every phone,
 * so unknown models fall back to "Brand MODEL" and an estimated performance tier.
 */
object DeviceDetector {
    fun detect(context: Context): DeviceInfo {
        val model = Build.MODEL.orEmpty().trim()
        val brand = Build.MANUFACTURER.orEmpty().trim()

        DeviceCatalog.match(model, brand)?.let { return it.copy(detected = true) }

        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        val ramGb = mi.totalMem / (1024.0 * 1024.0 * 1024.0)
        val tier = when {
            ramGb < 4.5 -> Tier.BUDGET
            ramGb < 7.2 -> Tier.MID
            else -> Tier.HIGH
        }

        val dm = context.resources.displayMetrics
        val w = dm.widthPixels / dm.xdpi
        val h = dm.heightPixels / dm.ydpi
        val inches = sqrt(w * w + h * h).takeIf { it in 5f..8.5f } ?: 6.5f

        val pretty = when {
            model.isEmpty() -> "Android device"
            brand.isEmpty() || model.startsWith(brand, ignoreCase = true) -> model
            else -> brand.replaceFirstChar { it.uppercase() } + " " + model
        }
        return DeviceInfo(
            name = pretty,
            brand = brand.replaceFirstChar { it.uppercase() },
            tier = tier,
            screenInches = inches,
            detected = true,
        )
    }
}
