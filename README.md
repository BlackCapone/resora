# CRMP Launcher

Native Android launcher foundation for a San Andreas Multiplayer-based CRMP client.

## Included

- Kotlin + Jetpack Compose Android application.
- Dark, Majestic-inspired launcher home screen.
- Selectable Moscow and Krasnodar server cards.
- Home, game-files, and profile tabs.
- Explicit client-readiness state; the launch action explains missing runtime requirements instead of pretending the game is installed.
- Android minimum SDK 26, target SDK 35, JDK 17.

## Not included yet

The repository was empty when development began. This first implementation is the launcher shell, not a playable game client. SA-MP/CRMP client binaries, CEF runtime, server connection protocol, account API, patcher, and download manifests still need to be integrated. Those components must come from files and interfaces that you are authorized to distribute.

## Build

Open the repository in Android Studio with JDK 17 and Android SDK 35, sync Gradle, and run the `app` configuration. The project uses Gradle Kotlin DSL and Android Gradle Plugin 8.7.3.
