# Android System Toggles

A lightweight, minimal utility application built with Jetpack Compose to toggle hardware settings (Peak Refresh Rate and Preferred Network Mode) on modern Android devices.

This application acts as a quick-access dashboard to instantly switch between high-performance modes and battery-saving settings without diging deep into the system Settings app.

## 🚀 Features

*   **Display Refresh Rate Toggle:** Instantly switch between Smooth Display (120Hz) and Standard Display (60Hz) to save battery.
*   **5G / LTE Toggle:** Force LTE-only mode or enable 5G Auto to improve standby battery times when 5G signal is weak or unnecessary.
*   **Material 3 Design:** Sleek modern interface with clean toggle cards, dark mode support, and dynamic status indicators.
*   **Zero Battery Footprint:** Operates strictly on-demand without any running background services or battery-draining polling tasks.

## 🛠️ Technical Implementation Details

Android restricts standard applications from writing to the `System` and `Global` settings tables directly for security reasons. This app solves this limitation by utilizing developer-level permissions:

*   **Refresh Rate:** Reads/writes to the `Settings.Secure.peak_refresh_rate` and `Settings.Secure.min_refresh_rate` properties.
*   **Network Mode:** Configures `Settings.Global.preferred_network_mode` along with `preferred_network_mode1` and `preferred_network_mode2` for robust multi-SIM support.

## 📦 Requirements & Permissions

To allow this app to write system settings, you must grant the `WRITE_SECURE_SETTINGS` permission once via ADB (Android Debug Bridge):

```bash
adb shell pm grant com.example.systemtoggles android.permission.WRITE_SECURE_SETTINGS
```

## 🏗️ Tech Stack

*   **UI Framework:** Jetpack Compose (Declarative UI)
*   **Design Language:** Material Design 3
*   **Programming Language:** Kotlin
*   **Architecture:** Model-View-ViewModel (MVVM) pattern with StateFlow
*   **Build System:** Gradle (Kotlin DSL)
