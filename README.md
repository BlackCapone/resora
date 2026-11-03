# CRMP Launcher

Native Android launcher foundation for a San Andreas Multiplayer-based CRMP client.

## Current interface

- Landscape-only launcher layout.
- Fixed left navigation: Play, Servers, Settings.
- Dark, Majestic-inspired visual direction with a compact sidebar, clear active state, restrained red accent, and a wide content area.
- Play screen with selected-server summary and quick server switching.
- Servers screen for Moscow and Krasnodar.
- Settings screen with local interface toggles and an honest client-component readiness list.
- Launch action explains missing runtime requirements instead of pretending the game is installed.

## Project stack

- Kotlin + Jetpack Compose.
- Android minimum SDK 26, target SDK 35, JDK 17.
- Gradle Kotlin DSL and Android Gradle Plugin 8.7.3.

## Not included yet

This is the launcher UI foundation, not a playable game client. SA-MP/CRMP client binaries, CEF runtime, server connection protocol, account API, patcher, and download manifests still need to be integrated. Those components must come from files and interfaces that you are authorized to distribute.

## Build

Open the repository in Android Studio with JDK 17 and Android SDK 35, sync Gradle, and run the app configuration. GitHub Actions also builds the debug APK on pushes to the main branch.
