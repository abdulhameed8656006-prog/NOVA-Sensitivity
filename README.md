# NOVA Sensitivity

> Your device. Your game. Your sensitivity.

NOVA Sensitivity is an offline Android app that gives you **recommended starting sensitivity values** for popular mobile shooters, based on your phone and the game you pick.

**Flow:** Choose Device → Choose Game → Generate Sensitivity → Fine-tune → Save

These values are a starting point, not a "perfect" setting. Your personal aim style may need some adjustment.

## Games
PUBG Mobile · BGMI · Free Fire · Free Fire MAX · Call of Duty: Mobile · Roblox Rivals · Standoff 2 · Blood Strike · Fortnite Mobile · Critical Ops

## Features
- Device detection from what Android actually exposes (model name, RAM, screen size), plus manual search and custom devices
- Game-specific settings (not one generic slider set)
- Fine-tune: Too slow / Too fast, per-setting sliders and +/- buttons, Reset to Recommended
- Copy Settings to clipboard
- Local saved profiles (no account, no network)
- Dark, AMOLED and Light themes
- **Zero permissions**, no analytics, no data collection

## Download
Every push to `main` builds a signed release APK with GitHub Actions. Grab it from the **Releases** page, or from the **Actions** tab under the `NOVA-Sensitivity-APK` artifact.

## Build locally
Requires JDK 17 and the Android SDK.

```
gradle :app:assembleRelease
```

The APK lands in `app/build/outputs/apk/release/`.

> The release build is signed with the auto-generated debug key so it installs directly. Use your own keystore if you publish to the Play Store.

## Add a game
Open `app/src/main/java/com/nova/sensitivity/data/Games.kt` and add a `GameDef` to `GameCatalog.games` with its own list of `SettingDef` entries (key, label, hint, min, max, step, base value). The UI picks it up automatically.

## Add a device
Add an entry in `data/Devices.kt`. Optional model-code prefixes (for example `SM-A065`) let auto-detection match it.

## How recommendations work
Each setting has a base value. A small, bounded adjustment (about ±12%) is applied from the device's performance tier and screen size, then snapped to the game's slider step. It is a heuristic, which is why fine-tuning is built in.
