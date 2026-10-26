plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.mdl.tclremote"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mdl.tclremote"
        minSdk = 31
        targetSdk = 35
        // En CI, BUILD = número de ejecución + 100 (ver .github/workflows/apk.yml).
        versionCode = System.getenv("BUILD")?.toIntOrNull() ?: 1
        versionName = "1.0.$versionCode"
    }

    // Clave fija para que cada APK nuevo se instale encima del anterior sin desinstalar.
    // No está en el repositorio: en CI se crea a partir de los secretos SIGNING_KEYSTORE_BASE64
    // y SIGNING_PASSWORD (ver .github/workflows/apk.yml).
    signingConfigs {
        create("personal") {
            storeFile = file("personal.keystore")
            storePassword = System.getenv("SIGNING_PASSWORD")
            keyAlias = "mitcl"
            keyPassword = System.getenv("SIGNING_PASSWORD")
        }
    }

    buildTypes {
        release {
            // R8 elimina el código que no se usa: la app ocupa menos y arranca antes.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("personal")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    lint {
        textReport = true
        textOutput = file("stdout")
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20240303")
}
