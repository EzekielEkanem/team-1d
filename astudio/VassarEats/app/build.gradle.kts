import android.databinding.tool.writer.ViewBinding

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "edu.vassar.cmpu203.vassareats"
    compileSdk = 34

    defaultConfig {
        applicationId = "edu.vassar.cmpu203.vassareats"
        minSdk = 30
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true;
    }


}

android {
    testOptions {
        animationsDisabled = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Add Mockito Core for mocking
    androidTestImplementation(libs.mockito.core)
    implementation(libs.json)

    // Espresso core library for UI tests
    androidTestImplementation (libs.espresso.core.v361)

    // JUnit 4 for Android tests
    androidTestImplementation (libs.junit.v121)

    // AndroidX Test Rules
    androidTestImplementation (libs.rules.v150)

    // AndroidX Test Core for ActivityScenario
    androidTestImplementation (libs.core)

    // Basic unit test dependency
    testImplementation (libs.junit)

    // Hamcrest for combining matchers
//    androidTestImplementation (libs.hamcrest.library)
}