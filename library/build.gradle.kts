import com.vanniktech.maven.publish.AndroidSingleVariantLibrary

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.signing)
    alias(libs.plugins.nexus.publish)
}

fun getVersionNameFromGit(): String {
    return try {
        val process = ProcessBuilder("git", "describe", "--tags", "--abbrev=0").start()
        process.inputStream.bufferedReader().readText().trim()
    } catch (e: Exception) {
        "dev"
    }
}

val isSnapshot =
    (findProperty("isSnapshot")?.toString() ?: System.getenv("IS_SNAPSHOT"))?.toBoolean() ?: true
version =
    if (isSnapshot) "dev-SNAPSHOT" else System.getenv(" VERSION_NAME ") ?: getVersionNameFromGit()
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
//    publishing {
//        singleVariant("release")
//    }
}
kotlin {
    jvmToolchain(21)
}
dependencies {

//    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
    configure(
        AndroidSingleVariantLibrary(
            // whether to publish a sources jar
            sourcesJar = true,
            // whether to publish a javadoc jar
            publishJavadocJar = true,
            // the published variant
            variant = "release"
        )
    )
    coordinates(
        groupId = "io.github.guyuuan", artifactId = "dynamicstatusbar", version = version.toString()
    )
    pom {
        name.set("DynamicStatusBar")
        description.set("Automatically modify the color of the status bar icon.")
        url.set("https://github.com/guyuuan/DynamicStatusBar")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                name.set("guyuuan")
                email.set("chunjinchen19998@gmail.com")
                url.set("https://github.com/guyuuan")
            }
        }
        scm {
            connection.set("scm:git@github.com:guyuuan/DynamicStatusBar.git")
            developerConnection.set("scm:git:ssh://github.com/guyuuan/DynamicStatusBar.git")
            url.set("https://github.com/guyuuan/DynamicStatusBar")
        }
    }
}