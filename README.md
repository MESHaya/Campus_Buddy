# CampusBuddy - Personal Campus Guide

**Campus_Buddy** is an Android application developed in Kotlin using Android Studio and SQLite Database. The app helps users to stay organized, protected, and when navigating.

---

## ✨ Innovative features for Part 2

- 📅  **Calendar** Allow users view all classes, exams, and deadlines in one interactive timeline, making it easy to plan ahead and avoid scheduling conflicts.
- 📞  **Emergency Contact** Provides quick access to important contacts, alerts, or resources in urgent situations, helping users respond faster when unexpected events occur. Users can select a phone number, and the system will dial the number, requiring the user to initiate the call button.
- 📝  **To-Do List** Allows users to create, organize, and prioritize assignments with due dates and reminders, ensuring nothing is forgotten.

## 🚀 Features for Part 2

- 🔐 **Register and Login**
  The user is able to log in to the app using their username and password. This is achieved by following the functionality of connecting the button and setting up SQLite to save the user’s details when they sign in, therefore aiding in the log in process. The creation    of the database was achieved by using an SQL Helper as well as creating all the necessary tables.
- ⚙️ **Change settings in app**
  In the Settings page:
    •	Users can select a language option from the dropdown to choose English, Zulu, or Afrikaans.
    •	Users can select to receive notifications by using a toggle button. This is achieved with the help of permissions.
    •	Users can select their desired theme from dark mode or light mode by using a toggle button.
  In the Tasks page: 
    •	Users can select the option to display tasks that are due, done, or overdue through radio buttons.
- 📂 **REST API**
  Using Google Calendar API which is a RESTful API provided by Google so that users can get events, add events, update, and delete. Integrate Google Calendar API by using “import com.google.api.services.calendar.Calendar” and “import com.google.api.services.calendar.CalendarScopes” and accessing through HTTP calls.
- 👤 **User Profile** support (with login/registration capability).
- 🔄 **Live Data with Flow** – Real-time updates using Kotlin coroutines and Flow.
- 📱 **Bottom Navigation** – Seamless navigation between Home, Calendar, To-Do, Emergency, and Settings.

---

## 🛠️ Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel), Fragments
- **Persistence**: SQLite Database
- **Async Handling**: Kotlin Coroutines + Flow
- **UI**: Material Design Components
- **Tools**: Android Studio, Jetpack Libraries

---
# ▶️ How to run the app from a zipped folder:


To run the **Campus_Buddy** Android app from a zipped project folder, first extract the contents of the `.zip` file to a location on your computer.
Open **Android Studio**, click **"Open"**, and navigate to the extracted folder — make sure to select the main project directory (where the `build.gradle` or `settings.gradle` file is located)
. Once opened, Android Studio will begin syncing the Gradle files; if it doesn’t happen automatically, you can trigger it manually via **File > Sync Project with Gradle Files**.
After syncing completes, connect your Android device or start an emulator, then click the green **Run** ▶ button at the top of Android Studio to build and launch the app.

---

# 🎬 YouTube Link: 

---

# 👥 Group Members:
- Meshaya Munnhar ST10272710
- Panashe Mavhunga ST10393030
- Zoe Heyneke ST10305921
- Kgomotso Mawande ST10264535
