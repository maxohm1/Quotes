---
description: Build and Run the QuoteApp
---

1. Open the project in Android Studio.
2. Sync Project with Gradle Files (File -> Sync Project).
3. Select an emulator or connected device.
4. Click the 'Run' button (Usage of Shift+F10).

# Troubleshooting
If you see "resource not found" errors, ensure you have run the Supabase setup SQL scripts.
If you see "SDK" errors, verify `compileSdk` is at least 36 in `app/build.gradle.kts`.
