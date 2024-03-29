import java.text.DateFormat

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("io.sentry.android.gradle") version "3.11.1"
    id("com.google.gms.google-services")
    id("com.apollographql.apollo3") version "3.8.2"
    id("se.patrikerdes.use-latest-versions") version "0.2.18"
    id("com.github.ben-manes.versions") version "0.41.0"
}

apollo {
    service("service") {
        packageName.set("com.troplo.privateuploader")
    }
}

sentry {
    autoUploadProguardMapping
    autoUploadNativeSymbols
    autoUploadSourceContext
}

android {
    namespace = "com.troplo.privateuploader"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.troplo.privateuploader"
        minSdk = 28
        targetSdk = 34
        versionCode = 14
        versionName = "1.0.14"
        multiDexEnabled = true
        buildConfigField("String", "SERVER_URL", "\"https://flowinity.com\"")
        buildConfigField("String", "BUILD_TIME", "\"${DateFormat.getDateTimeInstance().format(System.currentTimeMillis())}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField("String", "SERVER_URL", "\"https://flowinity.com\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            buildConfigField("String", "SERVER_URL", "\"http://192.168.0.12:34582\"")
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("com.github.skydoves:landscapist-glide:2.2.13-SNAPSHOT")
    implementation("com.apollographql.apollo3:apollo-runtime:3.8.2")
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.1.1"))
    implementation("com.google.firebase:firebase-messaging-ktx:23.2.0")
    implementation("com.google.firebase:firebase-analytics-ktx:21.3.0")

    // TPU
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.github.wax911:android-emojify:1.7.1")
    implementation("androidx.activity:activity:1.7.2")
    implementation("com.google.accompanist:accompanist-permissions:0.31.4-beta")
    implementation("io.mhssn:colorpicker:1.0.0")
    implementation("com.google.accompanist:accompanist-insets:0.22.0-rc")
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:ext-strikethrough:4.6.2")
    implementation("io.noties.markwon:ext-tables:4.6.2")
    implementation("io.noties.markwon:html:4.6.2")
    implementation("io.noties.markwon:linkify:4.6.2")
    implementation("io.sentry:sentry-android:6.23.0")
    implementation("io.sentry:sentry-compose-android:6.23.0")
    implementation("androidx.compose.runtime:runtime-tracing:1.0.0-alpha03")
    implementation("com.github.X1nto:OverlappingPanelsCompose:1.2.0")
    implementation("com.github.jeziellago:compose-markdown:0.3.3")
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
    implementation("com.squareup.retrofit2:retrofit:2.10.0-SNAPSHOT")
    implementation("com.squareup.retrofit2:converter-moshi:2.10.0-SNAPSHOT")
    implementation("com.squareup.moshi:moshi:1.12.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    implementation("com.github.bumptech.glide:glide:5.0.0-rc01")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation("io.coil-kt:coil:2.3.0")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("io.coil-kt:coil-gif:2.4.0")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:converter-gson:2.10.0-SNAPSHOT")
    implementation("io.socket:socket.io-client:2.1.0") {
        exclude("org.json", "json")
    }
    // Android

    implementation("androidx.navigation:navigation-compose:2.6.0")

    // Material Design 3
    implementation("androidx.compose.material3:material3:1.2.0-alpha10")
    // For SwipeableState
    implementation("androidx.compose.material:material:1.4.3")
    // or only import the main APIs for the underlying toolkit systems,
    // such as input and measurement/layout
    implementation("androidx.compose.ui:ui")

    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.work.runtime.ktx)
    debugImplementation("androidx.compose.ui:ui-tooling")

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Optional - Add full set of material icons
    implementation("androidx.compose.material:material-icons-extended")
    // Optional - Add window size utils
    implementation("androidx.compose.material3:material3-window-size-class")
    // Optional - Integration with LiveData
    implementation("androidx.compose.runtime:runtime-livedata")
    // Optional - Integration with RxJava
    implementation("androidx.compose.runtime:runtime-rxjava2")
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}