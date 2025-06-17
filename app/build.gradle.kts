import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {

    namespace = "com.linxiao.quickdevframework"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.linxiao.quickdevframework"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures.viewBinding = true
    resourcePrefix = "com.linxiao.quickdevframework"

    android.applicationVariants.configureEach {
        this.outputs.configureEach {
            val outputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            val appName = "QuickDevFramework"
            val packagingTime = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
            val fileName = "${appName}_${name}_v${defaultConfig.versionName}_${packagingTime}.apk"
            outputImpl.outputFileName = fileName
        }
    }
}

dependencies {
    implementation(project(":framework"))

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.9.1")
    releaseImplementation("com.squareup.leakcanary:leakcanary-android-no-op:2.9.1")
}
