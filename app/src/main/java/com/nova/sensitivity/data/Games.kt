package com.nova.sensitivity.data

import androidx.compose.ui.graphics.Color

const val DEFAULT_NOTE =
    "Menu names and slider ranges can change between game versions. Match each value to the closest slider in your game's settings."

/** One adjustable sensitivity control inside a game. */
data class SettingDef(
    val key: String,
    val label: String,
    val hint: String,
    val min: Float,
    val max: Float,
    val step: Float,
    val base: Float,
    val unit: String = "",
)

/** A supported game. To add a game, add a GameDef to [GameCatalog.games]. */
data class GameDef(
    val id: String,
    val name: String,
    val badge: String,
    val tagline: String,
    val accent: Color,
    val settings: List<SettingDef>,
    val note: String = DEFAULT_NOTE,
)

private fun pct(key: String, label: String, hint: String, min: Int, max: Int, base: Int) =
    SettingDef(key, label, hint, min.toFloat(), max.toFloat(), 1f, base.toFloat(), "%")

private fun num(key: String, label: String, hint: String, min: Int, max: Int, base: Int) =
    SettingDef(key, label, hint, min.toFloat(), max.toFloat(), 1f, base.toFloat())

private fun dec(key: String, label: String, hint: String, min: Float, max: Float, base: Float) =
    SettingDef(key, label, hint, min, max, 0.05f, base)

private fun pubgStyleSettings() = listOf(
    pct("camera", "Camera Sensitivity", "Turning speed while not aiming", 50, 300, 115),
    pct("ads", "ADS Sensitivity", "Turning speed while aiming down sights", 30, 300, 80),
    pct("gyro", "Gyroscope", "Tilt-to-look speed while moving", 50, 400, 210),
    pct("ads_gyro", "ADS Gyroscope", "Tilt-to-look speed while aiming", 50, 400, 230),
    pct("free_look", "Free Look", "Looking around without turning your character", 30, 300, 95),
)

private fun freeFireStyleSettings() = listOf(
    num("general", "General", "Base turning speed", 0, 100, 88),
    num("red_dot", "Red Dot", "Red dot and holo sights", 0, 100, 82),
    num("scope_2x", "2X Scope", "2X scopes", 0, 100, 78),
    num("scope_4x", "4X Scope", "4X scopes", 0, 100, 72),
    num("sniper", "Sniper Scope", "AWM and other sniper scopes", 0, 100, 62),
    num("free_look", "Free Look", "Looking around without turning", 0, 100, 70),
)

object GameCatalog {
    val games: List<GameDef> = listOf(
        GameDef(
            id = "pubgm", name = "PUBG Mobile", badge = "PUBG",
            tagline = "Camera, ADS and gyroscope",
            accent = Color(0xFFF2A900),
            settings = pubgStyleSettings(),
        ),
        GameDef(
            id = "bgmi", name = "BGMI", badge = "BGMI",
            tagline = "Camera, ADS and gyroscope",
            accent = Color(0xFFFF6B35),
            settings = pubgStyleSettings(),
        ),
        GameDef(
            id = "freefire", name = "Free Fire", badge = "FF",
            tagline = "General plus every scope",
            accent = Color(0xFFFF9F1C),
            settings = freeFireStyleSettings(),
        ),
        GameDef(
            id = "freefire_max", name = "Free Fire MAX", badge = "FFM",
            tagline = "General plus every scope",
            accent = Color(0xFFFF4D6D),
            settings = freeFireStyleSettings(),
        ),
        GameDef(
            id = "codm", name = "Call of Duty: Mobile", badge = "COD",
            tagline = "Camera, firing, scopes and gyro",
            accent = Color(0xFF8BC34A),
            settings = listOf(
                num("camera", "Camera Sensitivity", "Turning speed outside of fights", 0, 100, 55),
                num("firing", "Firing Sensitivity", "Turning speed while shooting", 0, 100, 48),
                num("ads", "ADS Sensitivity", "Turning speed while aiming down sights", 0, 100, 42),
                num("tactical", "Tactical Scope", "Low-zoom scopes", 0, 100, 38),
                num("sniper", "Sniper Scope", "Sniper scopes", 0, 100, 32),
                num("gyro", "Gyroscope", "Tilt-to-look speed", 0, 300, 120),
            ),
        ),
        GameDef(
            id = "rivals", name = "Roblox Rivals", badge = "RIV",
            tagline = "Camera, aim and scope controls",
            accent = Color(0xFF4FC3F7),
            settings = listOf(
                dec("camera", "Camera Sensitivity", "Roblox settings: overall camera turning speed", 0.3f, 3.0f, 1.10f),
                dec("aim", "Aim Sensitivity", "In-game turning speed while hip-firing", 0.1f, 3.0f, 0.90f),
                dec("ads", "ADS Sensitivity", "Turning speed while aiming", 0.1f, 3.0f, 0.70f),
                dec("scope", "Scope Sensitivity", "Turning speed with scoped weapons", 0.1f, 3.0f, 0.50f),
            ),
        ),
        GameDef(
            id = "standoff2", name = "Standoff 2", badge = "SO2",
            tagline = "Look, aim, scope and gyro",
            accent = Color(0xFFB388FF),
            settings = listOf(
                num("look", "Look Sensitivity", "Turning speed outside of aiming", 10, 100, 52),
                num("aim", "Aim Sensitivity", "Turning speed while aiming down sights", 10, 100, 44),
                num("scope", "Scope Sensitivity", "Sniper scope turning speed", 10, 100, 36),
                num("gyro", "Gyroscope", "Tilt-to-look speed", 0, 100, 40),
            ),
        ),
        GameDef(
            id = "bloodstrike", name = "Blood Strike", badge = "BS",
            tagline = "Camera, ADS, scope and gyro",
            accent = Color(0xFFE53935),
            settings = listOf(
                num("camera", "Camera Sensitivity", "Turning speed outside of aiming", 0, 100, 62),
                num("ads", "ADS Sensitivity", "Turning speed while aiming down sights", 0, 100, 55),
                num("scope", "Scope Sensitivity", "Scoped weapon turning speed", 0, 100, 46),
                num("gyro", "Gyroscope", "Tilt-to-look speed", 0, 100, 50),
            ),
        ),
        GameDef(
            id = "fortnite", name = "Fortnite Mobile", badge = "FN",
            tagline = "Look X/Y, ADS, build and edit",
            accent = Color(0xFF6EA8FF),
            settings = listOf(
                pct("look_x", "Look Sensitivity X", "Horizontal turning speed", 10, 100, 45),
                pct("look_y", "Look Sensitivity Y", "Vertical turning speed", 10, 100, 40),
                pct("ads", "ADS Sensitivity", "Turning speed while aiming", 10, 100, 42),
                pct("build", "Build Sensitivity", "Turning speed in build mode", 10, 100, 50),
                pct("edit", "Edit Sensitivity", "Turning speed in edit mode", 10, 100, 45),
            ),
        ),
        GameDef(
            id = "criticalops", name = "Critical Ops", badge = "CO",
            tagline = "Look, aim and sniper scope",
            accent = Color(0xFF26C6DA),
            settings = listOf(
                num("look", "Look Sensitivity", "Turning speed outside of aiming", 0, 100, 48),
                num("aim", "Aim Sensitivity", "Turning speed while aiming down sights", 0, 100, 42),
                num("sniper", "Sniper Scope", "Turning speed with sniper scopes", 0, 100, 30),
            ),
        ),
    )

    fun byId(id: String): GameDef = games.firstOrNull { it.id == id } ?: games.first()
}
