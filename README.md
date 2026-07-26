# TimeBoundary

**TimeBoundary** is a digital wellbeing Android app that helps you control how much time you spend on distracting apps. Pick the apps you want to limit, set a duration for each, and TimeBoundary reminds you the moment your time is up — every single time you open that app.

Unlike most screen-time apps that track cumulative daily usage, TimeBoundary treats **every app open as a fresh session**. Open Instagram at 5:00 PM with a 10-minute limit? You'll be reminded at 5:10 PM. Close it, reopen it at 5:45 PM? A brand new 10-minute timer starts, and you'll be reminded at 5:55 PM — no matter how many times you open it in a day.

---

## ✨ Features

- **App Picker** — searchable list of installed apps, pick any number to monitor
- **Per-app time limits** — set a different duration for each monitored app
- **Fresh session on every open** — the timer restarts each time you reopen a monitored app
- **Background monitoring** — a lightweight foreground service tracks app switches in real time
- **Reliable reminders** — uses `AlarmManager` for exact-time notifications, even in Doze mode
- **Gentle or Strict mode** — choose between a simple notification or a full-screen blocking reminder
- **Limited snooze** — optional "+5 min" snooze, capped to avoid defeating the purpose
- **Daily stats** — see how many times each app was opened and how often you went over
- **Fully offline** — no account, no cloud, no ads; everything stays on your device

---

## 🛠 Tech Stack

- **Language:** Kotlin
- **Architecture:** MVVM + Repository pattern
- **Persistence:** SharedPreferences / Room
- **Scheduling:** `AlarmManager` (`setExactAndAllowWhileIdle`)
- **App usage detection:** `UsageStatsManager`
- **Min SDK:** 26 (Android 8.0) · **Target SDK:** latest stable

---

## 📋 Requirements & Permissions

| Permission | Purpose |
|---|---|
| `PACKAGE_USAGE_STATS` | Detect which app is currently in the foreground (special access, granted manually in Settings) |
| `SCHEDULE_EXACT_ALARM` | Fire reminders at the exact expected time (Android 12+) |
| `POST_NOTIFICATIONS` | Show reminder notifications (Android 13+) |
| `RECEIVE_BOOT_COMPLETED` | Resume monitoring automatically after a device restart |
| `FOREGROUND_SERVICE` | Keep the monitoring service alive in the background |

> **Note:** `PACKAGE_USAGE_STATS` cannot be requested via a normal runtime dialog. On first launch, the app will guide you to **Settings → Apps → Special Access → Usage Access** to enable it manually.

---

## 🚀 Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/timeboundary.git
   ```
2. Open the project in **Android Studio** (Giraffe or newer recommended).
3. Let Gradle sync and download dependencies.
4. Run on a device or emulator with **API 26+**.
5. On first launch, grant **Usage Access** when prompted.

---

## 📱 How to Use

1. Open TimeBoundary and grant the Usage Access permission when prompted.
2. Go to **Add Apps** and select the apps you want to limit (e.g. Instagram, YouTube).
3. Set a time limit for each selected app.
4. Save your selections — monitoring starts automatically in the background.
5. Use your phone normally. Whenever you open a monitored app, TimeBoundary starts a countdown from that moment.
6. When time's up, you'll get a reminder — a notification (Gentle mode) or a full-screen alert (Strict mode).

---

## 🗂 Project Structure

```
app/
 ├── ui/
 │   ├── picker/          # App picker screen (search, select, set duration)
 │   ├── dashboard/        # Home screen with monitored apps & stats
 │   ├── reminder/         # Full-screen reminder activity
 │   └── settings/         # Global settings (mode, snooze, permissions)
 ├── service/
 │   ├── AppMonitorService.kt   # Foreground service polling foreground app
 │   └── ReminderReceiver.kt    # BroadcastReceiver that fires notifications
 ├── data/
 │   ├── MonitoredAppsPrefs.kt  # Persistence for selected apps & durations
 │   └── AppListProvider.kt     # Fetches installed launchable apps
 └── model/
     └── MonitoredApp.kt        # Data class for a monitored app
```

---

## ⚠️ Known Limitations (v1)

- Android only — no iOS version yet
- No cloud sync — data is local to the device only
- Polling-based detection (every ~2 seconds) rather than instant event callbacks
- Exact alarm delivery may be delayed on some OEM devices with aggressive battery optimization; whitelisting the app is recommended

---

## 🗺 Roadmap Ideas

- Cross-app daily usage totals and weekly summaries
- Streaks and gentle motivational stats
- Custom reminder messages
- Home-screen widget with live countdown
- iOS version

---

## 🤝 Contributing

Issues and pull requests are welcome. Please open an issue first to discuss any major changes before submitting a PR.

## 📄 License

This project is licensed under the MIT License — see the `LICENSE` file for details.
