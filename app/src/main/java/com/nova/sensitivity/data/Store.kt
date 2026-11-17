package com.nova.sensitivity.data

import android.content.Context
import org.json.JSONObject

/** Tiny local store. Everything stays on the device in app-private storage. */
class Store(context: Context) {
    private val sp = context.getSharedPreferences("nova_prefs", Context.MODE_PRIVATE)

    fun loadTheme(): ThemeMode =
        runCatching { ThemeMode.valueOf(sp.getString("theme", ThemeMode.DARK.name)!!) }
            .getOrDefault(ThemeMode.DARK)

    fun saveTheme(mode: ThemeMode) = sp.edit().putString("theme", mode.name).apply()

    fun loadDevice(): DeviceInfo? =
        sp.getString("device", null)?.let { runCatching { DeviceInfo.fromJson(JSONObject(it)) }.getOrNull() }

    fun saveDevice(device: DeviceInfo) = sp.edit().putString("device", device.toJson().toString()).apply()

    fun loadProfiles(): List<SavedProfile> = SavedProfile.listFromJson(sp.getString("profiles", null))

    fun saveProfiles(list: List<SavedProfile>) =
        sp.edit().putString("profiles", SavedProfile.listToJson(list)).apply()
}
