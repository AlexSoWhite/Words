object Dependencies {

    object Androidx {
        object Versions {
            const val coreCtx = "1.7.0"
            const val appcompat = "1.5.1"
            const val constraintLayout = "2.1.4"
            const val liveData = "2.5.1"
            const val viewModel = "2.5.1"
            const val navigationFragment = "2.3.5"
            const val navigationUi = "2.3.5"
            const val viewPager2 = "1.0.0"
        }

        const val androidxCore = "androidx.core:core-ktx:${Versions.coreCtx}"
        const val appCompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
        const val liveData = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.liveData}"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.viewModel}"
        const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigationFragment}"
        const val navigationUi = "androidx.navigation:navigation-ui-ktx:${Versions.navigationUi}"
        const val viewPager2 = "androidx.viewpager2:viewpager2:${Versions.viewPager2}"
    }

    object Material {
        object Versions {
            const val materialVersion = "1.7.0"
        }

        const val material = "com.google.android.material:material:${Versions.materialVersion}"
    }

    object Room {
        object Versions {
            const val roomVersion = "2.5.0"
        }

        const val runtime = "androidx.room:room-runtime:${Versions.roomVersion}"
        const val ktx = "androidx.room:room-ktx:${Versions.roomVersion}"
        const val annotationProcessor = "androidx.room:room-compiler:${Versions.roomVersion}"
        const val kapt = "androidx.room:room-compiler:${Versions.roomVersion}"
    }

    object Dagger {
        object Versions {
            const val daggerVersion = "2.41"
        }

        const val dagger = "com.google.dagger:dagger:${Versions.daggerVersion}"
        const val kapt = "com.google.dagger:dagger-compiler:${Versions.daggerVersion}"
    }

    object Coroutines {
        object Versions {
            const val coroutinesVersion = "1.6.1"
        }

        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesVersion}"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesVersion}"
    }

    object Gson {
        object Versions {
            const val gsonVersion = "2.10.1"
        }

        const val gson = "com.google.code.gson:gson:${Versions.gsonVersion}"
    }

    object Test {
        object Versions {
            const val junit = "4.13.2"
            const val ext = "1.1.4"
            const val espresso = "3.5.0"
        }

        const val junit = "junit:junit:${Versions.junit}"
        const val junitExt = "androidx.test.ext:junit:${Versions.ext}"
        const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    }
}
