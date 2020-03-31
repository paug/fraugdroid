rootProject.name = "fraugdroid"

pluginManagement {
    repositories {
        maven { url = uri("https://plugins.gradle.org/m2")}
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/")}
        jcenter()
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.squareup.sqldelight") {
                useModule("com.squareup.sqldelight:gradle-plugin:${requested.version}")
            }
        }
    }
}
