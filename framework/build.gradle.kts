plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    compileSdk = 37
    namespace = "com.linxiao.framework"
    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("proguard-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }

    buildFeatures.viewBinding = true

    resourcePrefix = "com.linxiao.framework"
}

dependencies {
    val lifecycleVersion = "2.11.0"
    val archVersion = "2.2.0"
    androidTestApi("androidx.test.espresso:espresso-core:3.1.0") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
    testApi("junit:junit:4.13.2")
    testApi("androidx.arch.core:core-testing:$archVersion")
    testApi("androidx.lifecycle:lifecycle-runtime-testing:$lifecycleVersion")

    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // Kotlin base
    api("org.jetbrains.kotlin:kotlin-reflect:2.4.0")
    // Android基础库
    api("com.google.android.material:material:1.14.0")
    api("androidx.core:core-ktx:1.19.0")
    api("androidx.appcompat:appcompat:1.8.0")
    api("androidx.legacy:legacy-support-v4:1.0.0")
    api("androidx.constraintlayout:constraintlayout:2.2.2")
    api("androidx.recyclerview:recyclerview:1.4.0")
    api("androidx.preference:preference-ktx:1.2.1")
    api("androidx.biometric:biometric:1.1.0")
    api("androidx.multidex:multidex:2.0.1")
    api("androidx.core:core-splashscreen:1.2.0")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-service:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-process:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycleVersion")
    // JSON解析
    api("com.google.code.gson:gson:2.14.0")
    // OKHttp
    api("com.squareup.okhttp3:okhttp:5.5.0")
    api("com.squareup.okio:okio:3.18.2")
    api("com.squareup.okhttp3:okhttp-urlconnection:5.5.0")
    api("com.github.franmontiel:PersistentCookieJar:v1.0.1")
    // Retrofit
    api("com.squareup.retrofit2:retrofit:3.0.0")
    api("com.squareup.retrofit2:adapter-rxjava2:3.0.0")
    api("com.squareup.retrofit2:converter-gson:3.0.0")
    // RxJava
    api("io.reactivex.rxjava2:rxjava:2.2.21")
    api("io.reactivex.rxjava2:rxandroid:2.1.1")
    api("com.trello.rxlifecycle2:rxlifecycle:2.2.2")
    api("com.trello.rxlifecycle2:rxlifecycle-components:2.2.2")
    // Eventbus
    api("org.greenrobot:eventbus:3.3.1")
    // BRVAH
    api("io.github.cymchad:BaseRecyclerViewAdapterHelper4:4.4.1")
    // 加密算法库
    api("org.bouncycastle:bcprov-jdk15on:1.70")

}
