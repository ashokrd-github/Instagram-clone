import com.android.build.gradle.internal.packaging.defaultExcludes
import org.gradle.internal.impldep.bsh.commands.dir
import org.jetbrains.kotlin.backend.wasm.lower.excludeDeclarationsFromCodegen
import kotlin.script.experimental.jvm.defaultJvmScriptingHostConfiguration

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.yaduvanshi_ashok_rd.likee"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.yaduvanshi_ashok_rd.likee"
        minSdk = 24
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

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.ui.graphics.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.google.firebase:firebase-messaging")


    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation (platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation ("com.google.firebase:firebase-analytics-ktx")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.squareup.picasso:picasso:2.8")

    implementation ("com.github.3llomi:CircularStatusView:V1.0.3")
    implementation ("com.github.OMARIHAMZA:StoryView:1.0.2-alpha")
    implementation ("com.makeramen:roundedimageview:2.3.0")
    implementation ("com.soundcloud.android:android-crop:1.0.1@aar")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation ("com.google.android.material:material:1.4.0")
    implementation ("me.leolin:ShortcutBadger:1.1.22@aar")

    implementation("androidx.emoji2:emoji2-emojipicker:1.5.0")
    implementation("androidx.emoji:emoji:1.1.0")
    implementation("org.kodein.emoji:emoji-kt:2.0.1")
    implementation("org.kodein.emoji:emoji-compose-m2:2.0.1") // With compose.material
    implementation("org.kodein.emoji:emoji-compose-m3:2.0.1") // With compose.material3
    implementation("com.vanniktech:emoji-facebook:0.21.0")

}
