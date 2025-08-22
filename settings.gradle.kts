pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.9.0")
}
dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "Central Portal Snapshots"
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        }
    }
//
//    versionCatalogs {
//        create("libs") {
//            from(files("./gradle/libs.versions.toml"))
//        }
//    }
}


rootProject.name = "DynamicStatuBar"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":library")
