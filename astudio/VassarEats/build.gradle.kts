// Top-level build file where you can add configuration options common to all sub-projects/modules.
<<<<<<< HEAD
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.2")
        classpath("com.google.gms:google-services:4.3.15")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
=======
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
>>>>>>> parent of c4e9599 (Added Home page icon, made Gordon Commons the default dining restaurant, and implemented the swiping feature)
}