package com.nova.sensitivity

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.nova.sensitivity.data.DeviceDetector
import com.nova.sensitivity.data.DeviceInfo
import com.nova.sensitivity.data.GameCatalog
import com.nova.sensitivity.data.GameDef
import com.nova.sensitivity.data.Recommender
import com.nova.sensitivity.data.SavedProfile
import com.nova.sensitivity.data.SettingDef
import com.nova.sensitivity.data.Store
import com.nova.sensitivity.data.ThemeMode
import java.util.UUID

enum class NavTab(val label: String) {
    Home("Home"),
    Games("Games"),
    Profiles("My Profiles"),
    Settings("Settings"),
}

enum class Overlay { None, Picker, Result }

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val store = Store(app)

    /** What Android tells us about this phone. */
    val detected: DeviceInfo = DeviceDetector.detect(app)

    var device by mutableStateOf(store.loadDevice() ?: detected)
        private set
    var selectedGameId by mutableStateOf(GameCatalog.games.first().id)
        private set
    var theme by mutableStateOf(store.loadTheme())
        private set
    var profiles by mutableStateOf(store.loadProfiles())
        private set

    var tab by mutableStateOf(NavTab.Home)
    var overlay by mutableStateOf(Overlay.None)
        private set

    var resultGame by mutableStateOf(GameCatalog.games.first())
        private set
    var resultDevice by mutableStateOf(detected)
        private set
    var recommended by mutableStateOf<Map<String, Float>>(emptyMap())
        private set
    val values = mutableStateMapOf<String, Float>()

    fun selectGame(id: String) { selectedGameId = id }

    fun openPicker() { overlay = Overlay.Picker }
    fun closeOverlay() { overlay = Overlay.None }

    fun chooseDevice(d: DeviceInfo) {
        device = d
        store.saveDevice(d)
        overlay = Overlay.None
    }

    fun generate() {
        showResult(GameCatalog.byId(selectedGameId), device, null)
    }

    fun openProfile(p: SavedProfile) {
        showResult(GameCatalog.byId(p.gameId), p.device, p.values)
    }

    private fun showResult(game: GameDef, dev: DeviceInfo, saved: Map<String, Float>?) {
        resultGame = game
        resultDevice = dev
        recommended = Recommender.recommend(game, dev)
        values.clear()
        values.putAll(recommended)
        if (saved != null) {
            values.putAll(saved.filterKeys { k -> game.settings.any { it.key == k } })
        }
        overlay = Overlay.Result
    }

    fun setValue(def: SettingDef, v: Float) {
        values[def.key] = Recommender.snap(def, v)
    }

    /** direction: +1 or -1 for a single setting (about 1% of its range). */
    fun bump(def: SettingDef, direction: Int) {
        val cur = values[def.key] ?: def.base
        values[def.key] = Recommender.snap(def, cur + direction * Recommender.amount(def, 0.01f))
    }

    /** direction: +1 = "too slow, increase", -1 = "too fast, decrease" for every setting (about 4%). */
    fun nudgeAll(direction: Int) {
        resultGame.settings.forEach { def ->
            val cur = values[def.key] ?: def.base
            values[def.key] = Recommender.snap(def, cur + direction * Recommender.amount(def, 0.04f))
        }
    }

    fun resetToRecommended() {
        values.clear()
        values.putAll(recommended)
    }

    fun saveProfile() {
        val existing = profiles.firstOrNull { it.gameId == resultGame.id && it.device.name == resultDevice.name }
        val p = SavedProfile(
            id = existing?.id ?: UUID.randomUUID().toString(),
            gameId = resultGame.id,
            device = resultDevice,
            values = values.toMap(),
            savedAt = System.currentTimeMillis(),
        )
        profiles = listOf(p) + profiles.filter { it.id != p.id }
        store.saveProfiles(profiles)
    }

    fun deleteProfile(id: String) {
        profiles = profiles.filter { it.id != id }
        store.saveProfiles(profiles)
    }

    fun clearProfiles() {
        profiles = emptyList()
        store.saveProfiles(profiles)
    }

    fun updateTheme(mode: ThemeMode) {
        theme = mode
        store.saveTheme(mode)
    }
}
