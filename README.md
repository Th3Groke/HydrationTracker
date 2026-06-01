# Hydration 💧

Hydration is a clean, modern, and native Android hydration tracking application built entirely with Jetpack Compose. It helps users manage their daily water intake through real-time progress tracking, localized multi-language support, smart background reminders with custom active windows, and a visual historical progress calendar.

---

## 🚀 Features

* **Fully Operational Hydration Tracking:** Easily log your water intake with real-time progress updates against your daily goal.
* **Personalized Controls:** Fully configurable settings to adjust your total daily fluid target (e.g., 2000 mL) and standard cup sizes.
* **Interactive Logs with Swipe-to-Delete:** View your fluid history ledger for the day inside clean UI cards. Accidental entries can be removed instantly with a fluid right-to-left swipe gesture.
* **Smart Reminders:** Custom background notification engine featuring user-adjustable frequencies and strict "active hour" limits (e.g., 08:00 to 20:00) to ensure zero midnight alerts.
* **Achievement Calendar:** An interactive month-by-month grid displaying native circular progress rings for past dates, allowing you to quickly visualize your hydration consistency over time.
* **Localization Ready:** Fully translated and optimized for multiple languages, including English, Polish (`pl`), and German (`de`).

---

## 🛠️ Tech Stack & Architecture

The application is engineered using modern Android development best practices and architecture component guidelines:

* **UI Layer:** 100% Jetpack Compose using **Material Design 3** formatting.
* **Navigation:** Jetpack Navigation Compose using explicit type-safe sealed class routing via a permanent Scaffold bottom navigation framework.
* **Asynchronous Data Flows:** Kotlin Coroutines and asynchronous `Flow`/`StateFlow` architectures for multi-threaded data pipelines.
* **Local Database:** **Room Database** implementing structural DAOs and robust SQL mappings to preserve historical tracking data.
* **Persistent Preferences:** **Jetpack DataStore (Preferences)** for storing small key-value user settings configurations safely on non-blocking background threads.
* **Background Scheduling:** **WorkManager** to guarantee energy-efficient, periodic background executions for user reminder alerts.

---

## 📦 Getting Started

### Prerequisites
* Android Studio (Ladybug or newer recommended)
* Android SDK 34+
* JDK 17

### Installation
1. Clone the repository down to your local machine:
   ```bash
   git clone [https://github.com/your-username/your-repo-name.git](https://github.com/your-username/your-repo-name.git)
