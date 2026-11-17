package com.nova.sensitivity.data

import org.json.JSONArray
import org.json.JSONObject

enum class ThemeMode(val label: String) {
    DARK("Dark"),
    AMOLED("AMOLED black"),
    LIGHT("Light"),
}

enum class Tier(val label: String) {
    BUDGET("Budget"),
    MID("Mid-range"),
    HIGH("High-end"),
}

data class DeviceInfo(
    val name: String,
    val brand: String,
    val tier: Tier,
    val screenInches: Float,
    val detected: Boolean = false,
    val custom: Boolean = false,
) {
    fun toJson(): JSONObject = JSONObject()
        .put("name", name)
        .put("brand", brand)
        .put("tier", tier.name)
        .put("inches", screenInches.toDouble())
        .put("detected", detected)
        .put("custom", custom)

    companion object {
        fun fromJson(o: JSONObject): DeviceInfo = DeviceInfo(
            name = o.getString("name"),
            brand = o.optString("brand", ""),
            tier = runCatching { Tier.valueOf(o.getString("tier")) }.getOrDefault(Tier.MID),
            screenInches = o.optDouble("inches", 6.5).toFloat(),
            detected = o.optBoolean("detected", false),
            custom = o.optBoolean("custom", false),
        )
    }
}

data class SavedProfile(
    val id: String,
    val gameId: String,
    val device: DeviceInfo,
    val values: Map<String, Float>,
    val savedAt: Long,
) {
    fun toJson(): JSONObject {
        val vals = JSONObject()
        values.forEach { (k, v) -> vals.put(k, v.toDouble()) }
        return JSONObject()
            .put("id", id)
            .put("gameId", gameId)
            .put("device", device.toJson())
            .put("values", vals)
            .put("savedAt", savedAt)
    }

    companion object {
        fun fromJson(o: JSONObject): SavedProfile {
            val vals = mutableMapOf<String, Float>()
            val obj = o.getJSONObject("values")
            val keys = obj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                vals[k] = obj.getDouble(k).toFloat()
            }
            return SavedProfile(
                id = o.getString("id"),
                gameId = o.getString("gameId"),
                device = DeviceInfo.fromJson(o.getJSONObject("device")),
                values = vals,
                savedAt = o.optLong("savedAt", 0L),
            )
        }

        fun listToJson(list: List<SavedProfile>): String {
            val arr = JSONArray()
            list.forEach { arr.put(it.toJson()) }
            return arr.toString()
        }

        fun listFromJson(json: String?): List<SavedProfile> {
            if (json.isNullOrBlank()) return emptyList()
            return runCatching {
                val arr = JSONArray(json)
                (0 until arr.length()).map { fromJson(arr.getJSONObject(it)) }
            }.getOrDefault(emptyList())
        }
    }
}
