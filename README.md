# 🌼 Ellof — Gentle Reminder App

Ellof is a **minimal, aesthetic reminder app** built with **Jetpack Compose** and modern Android architecture.  
It focuses on calm UX, reliability, and delightful interactions — inspired by iOS Reminders but crafted natively for Android.

> “Reminders that feel gentle, not noisy.”

---

## ✨ Features

- ⏰ **Time-based reminders**
- ✅ **Mark reminders as completed**
- 🗑 **Clean delete & cancel logic (no ghost notifications)**
- 🔔 **Reliable alarms using AlarmManager**
- 🔁 **Survives device reboot (BOOT_COMPLETED support)**
- 🧩 **Home Screen Widget (Jetpack Glance)**
  - Mark reminders as done directly from the widget
- 🧠 **Room database for offline-first storage**
- 🎨 **Minimal, warm UI**
  - Nude backgrounds
  - Mustard yellow accents
- 🧼 **No clutter. No over-notification.**

---

## 🧱 Tech Stack

### 📱 UI
- **Jetpack Compose**
- **Material 3**
- **DM Sans font**
- Clean, thumb-friendly layouts

### 🗄 Data
- **Room Database**
- **Flow** for reactive data streams

### ⏰ Background & System
- **AlarmManager** (exact alarms)
- **BroadcastReceiver** (`BOOT_COMPLETED`)
- **WorkManager** (widget updates)

### 🧩 Widgets
- **Jetpack Glance**
- Interactive widget actions
- Live sync with Room DB

### 🧠 Architecture
- **MVVM**
- **Single source of truth**
- Clear separation of concerns



---

## 🧩 App Widget

Ellof includes a **home screen widget** built with **Jetpack Glance**.

Widget capabilities:
- Shows active reminders
- Tap ✅ to mark reminder as completed
- Instantly syncs with database
- Cancels alarms automatically

> No app launch required. Calm productivity.

---

## 🔔 Notification Actions

Each notification includes:
- **Mark as Done**
- Auto-cancels alarm
- Updates DB + widget instantly

---

## 🔁 App Restart Safety

- Reminders persist after device reboot
- Alarms are re-scheduled automatically
- No missed reminders

---

## 🎨 Design System

```kotlin
NudeBackground  #F5EFE6
NudeSurface     #EFE4D6
MustardYellow   #F2C94C
TextPrimary     #2B2B2B
TextSecondary   #6B6B6B
