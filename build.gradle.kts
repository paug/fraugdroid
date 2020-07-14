plugins {
    id("com.github.johnrengelman.shadow").version("2.0.2")
    id("org.jetbrains.kotlin.jvm").version("1.3.71")
    id("com.squareup.sqldelight").version("1.4.0")
    id("org.jetbrains.kotlin.plugin.serialization").version("1.3.71")
}

repositories {
    jcenter()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/")}
    maven { url = uri("https://oss.jfrog.org/artifactory/libs-release")}
}

sqldelight {
    database("FishDatabase") {
        packageName = "fraug.droid.db"
        schemaOutputDirectory = file("src/main/sqldelight/databases")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.github.twitch4j:twitch4j:1.0.0-alpha.5")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.0")
    implementation("ch.qos.logback:logback-classic:1.1.8")
    implementation("com.jcabi:jcabi-log:0.17.2")
    implementation("com.squareup.sqldelight:sqlite-driver:1.4.0")
    implementation("com.squareup.sqldelight:coroutines-extensions:1.4.0")
    implementation("com.github.ajalt:clikt:2.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")

    testImplementation("junit:junit:4.13")
}

tasks.withType(Jar::class.java) {
    manifest {
        attributes("Main-Class" to "fraug.droid.LauncherKt")
    }
}