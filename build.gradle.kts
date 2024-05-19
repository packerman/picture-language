plugins {
    kotlin("multiplatform") version "1.9.22"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val kotlinWrappersVersion = "1.0.0-pre.700"

kotlin {
    jvmToolchain(21)
    js {
        browser {
        }
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project.dependencies.platform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:1.0.0-pre.700"))
                implementation("org.jetbrains.kotlin-wrappers:kotlin-browser")
            }
        }
    }
}
