plugins {
    kotlin("multiplatform") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("com.rickclephas.kmp.nativecoroutines")
    id("maven-publish")
}

group = "com.kalalau"
version = "0.1.0"

repositories {
    google()
    mavenCentral()
}

kotlin {
    android {
        publishAllLibraryVariants()
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "SwapiStore"
        }
    }

    js(IR) {
        browser()
        binaries.library()
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                with(Versions) {
                    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
                    implementation("io.ktor:ktor-client-core:${ktor_version}")
                    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktor_version}")
                    implementation("io.ktor:ktor-client-content-negotiation:${ktor_version}")
                }
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                with(Versions) {
                    implementation("io.ktor:ktor-client-cio:${ktor_version}")
                }
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                with(Versions) {
                    implementation("io.ktor:ktor-client-darwin:$ktor_version")
                }
            }
        }
        val jsMain by getting
        val jsTest by getting
    }
}

android {
    compileSdk = 33
    defaultConfig {
        minSdk = 27
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    val main by sourceSets.getting {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
}
