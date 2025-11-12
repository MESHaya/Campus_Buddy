plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.campus_buddy"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.campus_buddy"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    // ✅ Fix duplicate META-INF entries
    packaging {
        resources {
            excludes += listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt"
            )
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.0")

    // Retrofit & Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0") // or converter-gson
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Maps
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // ------------------------API SECTION --------------------------

        implementation("androidx.core:core-ktx:1.12.0")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.9.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")

        // ✅ Google Sign-In
        implementation("com.google.android.gms:play-services-auth:20.7.0")

        // ✅ Google Calendar API client libs
        implementation("com.google.api-client:google-api-client-android:1.34.0")
        implementation("com.google.http-client:google-http-client-android:1.43.3")
        implementation("com.google.http-client:google-http-client-gson:1.43.3")
        implementation("com.google.apis:google-api-services-calendar:v3-rev411-1.25.0")
        //added now testing
        implementation("com.google.http-client:google-http-client-jackson2:1.41.4")
        //implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")

        implementation ("com.google.api-client:google-api-client-android:1.33.0")
        implementation ("com.google.api-client:google-api-client-gson:1.33.0")

// WorkManager for background tasks (ADD THIS)
    implementation ("androidx.work:work-runtime-ktx:2.9.0")

    // Core AndroidX libraries
    implementation ("androidx.core:core-ktx:1.12.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")

    // Material Design
    implementation ("com.google.android.material:material:1.11.0")

    // ConstraintLayout
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")

    // Fragment KTX
    implementation ("androidx.fragment:fragment-ktx:1.6.2")

    // Lifecycle components
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // Testing
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
}







