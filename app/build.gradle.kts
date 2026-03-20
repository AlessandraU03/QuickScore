import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.secrets.gradle)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.jetbrainsKotlinSerialization) // 1. Agregar este plugin
}

secrets {
    propertiesFileName = "local.properties"
    defaultPropertiesFileName = "local.defaults.properties"
    ignoreList.add("sdk.dir")
}

android {
    namespace = "com.ale.quickscore"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ale.quickscore"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }

    flavorDimensions.add("environment")
    productFlavors {
        create("dev") {
            dimension = "environment"
            // URL local para el emulador de Android (10.0.2.2 mapea al localhost de tu PC)
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8090/\"")
            resValue("string", "app_name", "QuickScore (DEV)")
        }

        create("prod") {
            dimension = "environment"
            // Cambiar por la URL real cuando despliegues el backend
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8090/\"")
            resValue("string", "app_name", "QuickScore")
        }
    }
}

ksp {
    arg("hilt.disableModulesHaveInstallInCheck", "true")
}


kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.text.google.fonts)

    implementation(libs.androidx.lifecycle.viewmodel.compose)       // viewModel()
    implementation(libs.com.squareup.retrofit2.retrofit)            // Retrofit
    implementation(libs.com.squareup.retrofit2.converter.json)      // JSON
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.io.coil.kt.coil.compose)                    // Coil
    implementation(libs.androidx.navigation.compose)                // Navigation
    implementation(libs.androidx.compose.material.icons.extended)   // Icons extendend
    implementation(libs.hilt.android)                               // Implementación de Hilt
    implementation(libs.hilt.navigation.compose)                    // Integración con Jetpack Compose
    ksp(libs.hilt.compiler)                                         // KSP

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}