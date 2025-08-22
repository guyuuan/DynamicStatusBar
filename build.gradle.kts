plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.nexus.publish) apply false
}
val sonatypeUsername: String? by project
val sonatypePassword: String? by project

//group = "io.github.guyuuan"
//
//nexusPublishing {
//    packageGroup = provider { project.group.toString() }
//    repositories {
//        // see https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/#configuration
//        sonatype {
//            useStaging = true
//            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
//            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
//            username = sonatypeUsername ?: System.getenv("SONATYPE_USERNAME")
//            password = sonatypePassword ?: System.getenv("SONATYPE_PASSWORD")
//        }
//    }
//}
//
//mavenPublishing {
//    publishToMavenCentral(automaticRelease = false)
//    signAllPublications()
//}