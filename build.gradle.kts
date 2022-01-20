group = "universe.multikit"
version = "1.0.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
}

plugins {
    kotlin("js") version "1.6.10"
}

kotlin {
    js {
        nodejs()
        binaries.executable()
    }
}