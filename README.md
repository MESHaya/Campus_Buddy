# CampusBuddy - Personal Campus Guide

**Campus_Buddy** is an Android application developed in Kotlin using Android Studio and SQLite Database. The app helps users to stay organized, protected, and when navigating.
---
### 🚩Important Note: Create your own API Key for Maps ###
Steps:
1. Navigate to: https://console.cloud.google.com/
2. Sign in with your Google Account
3. At the top, click the project dropdown → New Project.
4. Name your project: “Campus Buddy”
5. Click create
6. In the left sidebar, go to APIs & Services → Library.
7. Enable Maps SDK for Android and Maps Static API
8. Go to APIs & Services → Credentials.
9. Click + Create Credentials → API Key.
10. A pop-up will appear showing your new API key — copy it somewhere safe.
11. Lastly paste your API Key in res/values/strings and Open app/src/main/AndroidManifest.xml
Find line ~36:
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="AIzaSyAN_WDRNp97kSMTULNQmZEYS09ioHk-FZ0" />

---
## ✨ Innovative features for Part 3

- 📍 **Maps** Allows users to receive directions, displays campus map with Google Maps API showing IIE sister schools with directions. Users can choose between IIE MSA and IIE Varsity.
- ⛶  **Attendance** Users can scan the provided QR code with their camera to mark their attendance.

## 🚀 Features for Part 3

- 🔐 **SSO Login** The user is able to log in to the app using their Google account for quick access. 
- ✈︎ **Offline Mode** Users can perform offline actions with synchronisation capabilities once they reconnect. These features are the calendar, notes and emergency. 
- 🔔 **Real-time Notification** Implemented push notification system for real-time updates and alerts such as notifying tasks added and events that show to the user.
- 🗣️ **Multi-Language**  2 South African Languages are supported that users can choose from. We included the option to support English, Afrikaans, isiZulu, Sesotho.
---

### 🚩Important Note:Quick Setup Guide ###


⚠️ IMPORTANT: This project uses pre-configured Firebase credentials
All API keys and Firebase configuration are already included in this project for testing purposes.

Quick Start (5 minutes)
Step 1: Extract the Project
# Extract the zip file to your desired location
unzip Campus_Buddy.zip
cd Campus_Buddy
Step 2: Open in Android Studio
1.	Open Android Studio
2.	Click File → Open
3.	Navigate to the extracted Campus_Buddy folder
4.	Click OK
5.	Wait for Gradle sync to complete (may take 2-5 minutes)
Step 3: Get Your SHA-1 Fingerprint
The app requires your device's SHA-1 fingerprint for Google Sign-In to work.
Note: The SHA-1 setup is only required because we're testing with debug certificates. In a published app on Google Play Store, the app would be signed once with a release certificate, that SHA-1 would be added to Firebase, and all users would download the identically-signed app. Regular users never configure SHA-1 - they just download from the Play Store and sign in with Google, like any other app (Gmail, YouTube, etc.). This is standard Android development practice.

Method 1: Using Gradle in Android Studio (RECOMMENDED)
On Windows:
1.	Open the Terminal tab at the bottom of Android Studio
2.	Type: gradlew signingReport
3.	Press Enter
4.	Wait for the command to complete
On macOS/Linux:
1.	Open the Terminal tab at the bottom of Android Studio
2.	Type: ./gradlew signingReport
3.	Press Enter
4.	Wait for the command to complete
Find Your SHA-1: Look for output like this:
Variant: debug
Config: debug
Store: C:\Users\YourName\.android\debug.keystore
Alias: AndroidDebugKey
MD5: 5C:01:D7:58:6F:41:29:4B:2D:90:C8:9C:9B:A2:AF:2B
SHA1: 49:3F:E1:88:CB:80:78:8E:67:E9:AB:30:53:C6:2E:3A:CD:13:AB:01  ← COPY THIS!
SHA-256: C0:25:A2:93:EB:01:72:87:06:10:C7:80:E7:E5:47:68:77:1D:BC:A3
Copy the SHA-1 (format: XX:XX:XX:XX:... - exactly 20 pairs of hex values)

Method 2: Using Keytool Command (If Method 1 Fails)
On Windows:
Open Command Prompt (not Android Studio Terminal):
1.	Press Windows Key + R
2.	Type cmd and press Enter
3.	Copy and paste this entire command:
"C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe" -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
If that fails, try this alternative:
"C:\Program Files\Android\Android Studio\jre\bin\keytool.exe" -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
On macOS/Linux:
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
Find Your SHA-1: Look for this in the output:
Certificate fingerprints:
         MD5:  5C:01:D7:58:6F:41:29:4B:2D:90:C8:9C:9B:A2:AF:2B
         SHA1: 49:3F:E1:88:CB:80:78:8E:67:E9:AB:30:53:C6:2E:3A:CD:13:AB:01  ← COPY THIS!
         SHA256: C0:25:A2:93:EB:01:72:87:06:10:C7:80:E7:E5:47:68:77:1D:BC:A3
Copy the SHA1 value (not MD5 or SHA-256!)

Common Issues Getting SHA-1
Problem: "JAVA_HOME is not set" or "java command not found"
Quick Fix (Windows): Use the direct keytool command from Method 2 above instead of gradlew.
Alternative Fix (Windows):
# Set JAVA_HOME temporarily in Terminal
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
set PATH=%JAVA_HOME%\bin;%PATH%
gradlew signingReport
Problem: "keystore not found"
The debug keystore doesn't exist yet. Fix:
1.	In Android Studio, click the ▶️ Run button
2.	Let it build (even if it fails to install)
3.	This creates the debug keystore
4.	Try getting SHA-1 again
Problem: Can't find SHA-1 in output
Filter the output:
•	Windows: gradlew signingReport | findstr SHA1
•	Mac/Linux: ./gradlew signingReport | grep SHA1

Step 4: Add Your SHA-1 to Firebase
Since I've already created the Firebase project, you just need to add your device's fingerprint:
1.	Go to Firebase Console
2.	Click on the project: campus-buddy-bdcf5
3.	Sign in with Google if prompted
4.	Go to ⚙️ Settings → Project Settings
5.	Scroll down to "Your apps" → Find Campus Buddy (Android)
6.	Scroll to "SHA certificate fingerprints"
7.	Click "Add fingerprint"
8.	Paste your SHA-1 from Step 3
9.	Click "Save"
Note: If you don't have access to my Firebase project, see "Alternative Setup" below.

Step 5: Run the App
1.	Connect your Android device via USB (with USB debugging enabled)
OR
Start an Android emulator (API 24+)
2.	In Android Studio, click the ▶️ Run button
3.	Select your device
4.	Wait for the app to install and launch

Step 6: Test Google Sign-In
1.	Open the app
2.	Click "Register" or "Login"
3.	Click "Sign in with Google"
4.	Select your Google account
5.	Sign-in should work successfully ✅

Alternative Setup (If Firebase Access Fails)
If you cannot access my Firebase project or want to use your own:
1. Create Your Own Firebase Project
1.	Go to Firebase Console
2.	Click "Add project"
3.	Enter project name: campus-buddy-test
4.	Disable Google Analytics (optional)
5.	Click "Create project"
2. Add Android App
1.	Click Android icon to add Android app
2.	Android package name: com.example.campus_buddy (MUST BE EXACT)
3.	Debug signing certificate SHA-1: Your SHA-1 from Step 3 above
4.	Click "Register app"
5.	Download the new google-services.json
6.	Replace the existing file: app/google-services.json
3. Enable Google Sign-In
1.	In Firebase Console → Authentication
2.	Click "Get started"
3.	Go to "Sign-in method" tab
4.	Click "Google" → Toggle "Enable"
5.	Set Project support email (your email)
6.	Click "Save"
4. Create OAuth Consent Screen
1.	Go to Google Cloud Console
2.	Select your Firebase project
3.	APIs & Services → OAuth consent screen
4.	Select "External" → Click "Create"
5.	Fill in: 
o	App name: Campus Buddy
o	User support email: Your email
o	Developer contact: Your email
6.	Click "Save and Continue" (skip other steps)
5. Create Web OAuth Client ID
1.	APIs & Services → Credentials
2.	"+ CREATE CREDENTIALS" → "OAuth client ID"
3.	Select "Web application"
4.	Name: Campus Buddy Web Client
5.	Click "Create"
6.	Copy the Client ID
6. Update Web Client ID in Code
1.	Open app/src/main/java/com/example/campus_buddy/auth/GoogleSignInManager.kt
2.	Find line ~27:
private const val WEB_CLIENT_ID = "639197056183-5ri2nqe36mifb4i9c3mps36qte9tqenj.apps.googleusercontent.com"
3.	Replace with your new Web Client ID
7. Sync and Rebuild
1.	Click "Sync Project with Gradle Files"
2.	Build → "Clean Project"
3.	Build → "Rebuild Project"
4.	Uninstall the app from device completely
5.	Click ▶️ Run to reinstall


Optional: Google Maps Setup
If you want to test the Maps feature:
1.	In Google Cloud Console → APIs & Services → Library
2.	Search for "Maps SDK for Android" → Click "Enable"
3.	Go to Credentials → "+ CREATE CREDENTIALS" → "API key"
4.	Copy the API key
5.	Open app/src/main/AndroidManifest.xml
6.	Find line ~36:
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="AIzaSyAN_WDRNp97kSMTULNQmZEYS09ioHk-FZ0" />
and in res/values/strings
7.	Replace with your new API key
Troubleshooting
"Developer Error (Code 10)"
Cause: Your SHA-1 fingerprint not added to Firebase
Fix:
1.	Get your SHA-1: gradlew signingReport (or use keytool method)
2.	Add it to Firebase Console (Step 4 above)
3.	Wait 1-2 minutes for changes to propagate
4.	Uninstall app completely from device
5.	Reinstall and test
"JAVA_HOME is not set" Error
Cause: Java not in system PATH
Fix (Windows): Use the direct keytool command instead:
"C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe" -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
Alternative: Set JAVA_HOME temporarily:
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
set PATH=%JAVA_HOME%\bin;%PATH%
gradlew signingReport
"google-services.json not found"
Cause: File missing or in wrong location
Fix: Verify file exists at Campus_Buddy/app/google-services.json
Google Sign-In Immediately Fails
Cause: Incorrect package name or missing OAuth client
Fix: Follow "Alternative Setup" section completely
Maps Not Loading
Cause: Maps API not enabled or API key expired
Fix: Follow "Optional: Google Maps Setup" section
"keystore not found" Error
Cause: Debug keystore doesn't exist yet
Fix:
1.	In Android Studio, click ▶️ Run button
2.	Let it try to build and install (even if it fails)
3.	This creates the debug keystore automatically
4.	Try getting SHA-1 again

Project Features
✅ Google Sign-In authentication
✅ Traditional email/password registration
✅ SQLite local database
✅ Google Maps integration
✅ QR code scanning
✅ Push notifications
✅ Multi-language support (English, Afrikaans, Zulu)
✅ Dark mode
✅ Settings management

Testing Credentials
For Traditional Login:
After registering through the app, use your created credentials.
For Google Sign-In:
Use any Google account. The app will auto-register new users.

Technical Details
•	Min SDK: 24 (Android 7.0)
•	Target SDK: 36 (Android 14)
•	Language: Kotlin
•	Database: SQLite
•	Authentication: Firebase Auth + Google Sign-In
•	Architecture: MVVM pattern

Important Notes
Security:
•	The included google-services.json and API keys are for testing only
•	In production, these should be secured and not committed to version control
Testing:
•	Requires internet connection for Google Sign-In
•	Test with real Google account (emulator accounts may not work)
•	Maps feature requires Google Play Services
Estimated Setup Time: 5-10 minutes (with included config) or 20-30 minutes (new Firebase project)

Summary Checklist
Quick Setup (Using my Firebase):
•	[ ] Extracted project
•	[ ] Opened in Android Studio
•	[ ] Generated SHA-1 fingerprint
•	[ ] Added SHA-1 to my Firebase project
•	[ ] Synced Gradle
•	[ ] App runs successfully
•	[ ] Google Sign-In works
Alternative Setup (Your own Firebase):
•	[ ] All steps in "Alternative Setup" completed
•	[ ] New google-services.json downloaded and replaced
•	[ ] Web Client ID updated in code
•	[ ] Project rebuilt
•	[ ] App works with your Firebase

Quick Reference: SHA-1 Commands
Windows (Android Studio Terminal):
gradlew signingReport
Windows (Command Prompt - if JAVA_HOME error):
"C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe" -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
Mac/Linux (Android Studio Terminal):
./gradlew signingReport
Mac/Linux (Terminal):
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

Ready to test! 
For any questions or issues, please contact the team – Meshaya Munnhar.



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

# 🎬 YouTube Link: https://www.youtube.com/watch?v=nKmiv5RmVwM

---

# 👥 Group Members:
- Meshaya Munnhar ST10272710
- Panashe Mavhunga ST10393030
- Zoe Heyneke ST10305921
- Kgomotso Mawande ST10264535
