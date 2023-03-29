plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-android")
    id(Plugins.Detekt.plugin)
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.nafanya.words"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles.addAll(
                mutableListOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    File("proguard-rules.pro")
                )
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

detekt {
    autoCorrect = true
    buildUponDefaultConfig = true
    allRules = false
    config = files("$rootDir/staticAnalysis/detektConfig.yml")
}

dependencies {

    implementation(Dependencies.Androidx.androidxCore)
    implementation(Dependencies.Androidx.appCompat)
    implementation(Dependencies.Androidx.constraintLayout)
    implementation(Dependencies.Androidx.liveData)
    implementation(Dependencies.Androidx.viewModel)
    implementation(Dependencies.Androidx.navigationFragment)
    implementation(Dependencies.Androidx.navigationUi)

    implementation(Dependencies.Material.material)

    // Room
    implementation(Dependencies.Room.runtime)
    implementation(Dependencies.Room.ktx)
    annotationProcessor(Dependencies.Room.annotationProcessor)
    kapt(Dependencies.Room.kapt)

    // Dagger
    implementation(Dependencies.Dagger.dagger)
    kapt(Dependencies.Dagger.kapt)

    // Coroutines
    implementation(Dependencies.Coroutines.core)
    implementation(Dependencies.Coroutines.android)

    testImplementation(Dependencies.Test.junit)
    androidTestImplementation(Dependencies.Test.junitExt)
    androidTestImplementation(Dependencies.Test.espresso)
}
apply(from = "../ktlint.gradle")
