plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.signing)
}

android {
    namespace = "io.github.guyuuan.dynamicstatusbar"

    compileSdk = 36

    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    externalNativeBuild {
        cmake {
            version = "4.1.0"
            path = file("./CMakeLists.txt")
        }
    }

    buildFeatures {
        buildConfig = true
    }
}
kotlin {
    jvmToolchain(21)
}
dependencies {

//    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

}

//apply from : '../publish.gradle'