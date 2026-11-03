# CRMP Mobile Launcher

Native Android launcher foundation for a San Andreas Multiplayer-based CRMP client.

## Interface direction

- Landscape-first, full-screen launcher.
- Three icon-only glass navigation controls on the left: Play, Servers, Settings.
- Full-screen custom night-city artwork and a transparent PNG wordmark.
- Play action fixed to the lower-right corner; support action in the upper-right.
- Server list with a clear selected state. Online counts are intentionally not fabricated; they require the server API.
- Settings grouped around the categories familiar from BLACK RUSSIA: appearance, personal, sound, graphics, controls, and security.
- Local settings (background visibility/dimming, server memory, click sounds, haptics, and interface animations) are persisted on-device.

## Project stack

- Kotlin + Jetpack Compose.
- Android minimum SDK 26, target SDK 35, JDK 17.
- Gradle Kotlin DSL and Android Gradle Plugin 8.7.3.

## Not included yet

This is the launcher UI foundation, not a playable game client. SA-MP/CRMP client binaries, CEF runtime, server connection protocol, account API, patcher, and download manifests still need to be integrated. Game graphics and control settings will only become functional once the game runtime is connected.

## Build

Open the repository in Android Studio with JDK 17 and Android SDK 35, sync Gradle, and run the app configuration. GitHub Actions also builds the debug APK on pushes to the main branch.
