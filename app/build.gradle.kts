plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)

}

android {
    namespace = "com.kay.gene"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kay.gene"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("com.google.firebase:firebase-auth:23.1.0")//had to addd manually but authentication on firebase working maybe?
    implementation ("com.google.firebase:firebase-firestore:25.1.1")//new
    implementation ("com.google.firebase:firebase-storage:21.0.1")//new
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.2")
    implementation ("com.firebaseui:firebase-ui-storage:8.0.2")
    implementation ("com.google.firebase:firebase-appcheck-safetynet:16.0.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.2")
    implementation ("com.firebaseui:firebase-ui-storage:8.0.2")

}

apply(plugin = "com.google.gms.google-services")


