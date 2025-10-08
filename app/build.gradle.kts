plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("app.cash.sqldelight") version "2.1.0"
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.example.pocdb"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pocdb"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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

    flavorDimensions += listOf("db", "di")

    productFlavors {
        create("room") {
            dimension = "db"
            applicationIdSuffix = ".room"
        }
        create("sqldelight") {
            dimension = "db"
            applicationIdSuffix = ".sqldelight"
        }

        create("hilt") {
            dimension = "di"
            applicationIdSuffix = ".hilt"
        }
        create("koin") {
            dimension = "di"
            applicationIdSuffix = ".koin"
        }
    }
}

var sqlDelightConfigured = false

androidComponents {
    beforeVariants { variant ->
        val flavors = variant.productFlavors.toMap()

        if (flavors["db"] == "room") {
            dependencies.add("ksp", libs.room.compiler)
        }

        if (flavors["di"] == "hilt") {
            dependencies.add("ksp", "com.google.dagger:hilt-android-compiler:2.57.1")
        }
    }

    onVariants { variant ->
        val flavors = variant.productFlavors.toMap()
        if (!sqlDelightConfigured && flavors["db"] == "sqldelight") {
            sqlDelightConfigured = true
            extensions.configure<app.cash.sqldelight.gradle.SqlDelightExtension>("sqldelight") {
                databases {
                    create("AppDatabase") {
                        packageName.set("com.example.pocdb")
                        generateAsync.set(true) // enables suspend APIs
                    }
                }
            }
        }
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
    implementation(libs.androidx.profileinstaller)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    "baselineProfile"(project(":benchmarks"))
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    "hiltImplementation"("com.google.dagger:hilt-android:2.57.1")

    "roomImplementation"(libs.room.coroutines)

    "sqldelightImplementation"("app.cash.sqldelight:android-driver:2.1.0")
    "sqldelightImplementation"("app.cash.sqldelight:coroutines-extensions:2.1.0")

    "koinImplementation"("io.insert-koin:koin-android:4.1.0")
}