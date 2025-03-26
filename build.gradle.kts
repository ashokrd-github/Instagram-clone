
buildscript {
    repositories {
//      const.kotlin_version = "1.5.0"
        google()
        mavenCentral()

    }
    dependencies {
        classpath(libs.google.services)
        classpath("com.android.tools.build:gradle:4.1.3")
//        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("com.google.gms:google-services:4.3.2")
    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}
