# CRMP Launcher

Android launcher shell for a San Andreas Multiplayer-based CRMP client.

## Current scope

- Native Android application (Kotlin + Jetpack Compose).
- Majestic-inspired dark launcher interface.
- ABI-ready Android packaging configuration (`armeabi-v7a` and `arm64-v8a`).
- Launcher UI and local installation-state foundation.
- Client binaries, CEF runtime, server API, authentication, and downloads are intentionally not bundled yet.

## Important

This repository currently contains the launcher application only. A working SA-MP/CRMP game client requires legally distributable client binaries and their integration contract. The launcher does not pretend to provide a playable client until those files are supplied.

## Build

Open the project in Android Studio (Giraffe or newer) with JDK 17 and Android SDK 35, then sync Gradle and run the `app` configuration.
