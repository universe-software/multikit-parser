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
    id("maven-publish")
}

kotlin {
    js {
        nodejs()
        binaries.executable()
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
            groupId = "universe.multikit"
            artifactId = "parser"
            version = "1.0"

            pom {
                withXml {
                    val root = asNode()
                    root.appendNode("name", "MultiKit Processor")
                    root.appendNode("description", "Preproccessor library for MultiKit code processing modules")
                }
            }
        }
    }

    repositories {
        maven {
            url = uri(file("../repo"))
        }
    }
}