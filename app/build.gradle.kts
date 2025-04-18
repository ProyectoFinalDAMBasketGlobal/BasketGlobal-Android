plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrainsKotlinSerialization)
    alias(libs.plugins.kotlin.parcelize)

    //Dagger HIlt
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "dam.intermodular.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "dam.intermodular.app"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    //DESIGN
    implementation(libs.androidx.material.icons.extended)

    //COROUTINES
    implementation(libs.kotlinx.coroutines.core)

    //LIVEDATA
    implementation(libs.androidx.runtime.livedata)

    //VIEW MODEL
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    //NAVIGATION
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    //HILT
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    //RETROFIT
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    //DATASTORE
    implementation(libs.androidx.datastore.preferences)

    //TINK CRYPTOGRAPHY
    implementation (libs.tink.android)

    //CARGA DE IMAGENES
    implementation(libs.androidx.activity.compose)
    implementation(libs.coil.compose)

    //MULTIPART SEND
    implementation(libs.okhttp)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}
