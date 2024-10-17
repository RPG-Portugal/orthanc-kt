plugins {
    kotlin("multiplatform") version "2.0.21"
}

group = "com.rpgportugal"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        binaries.executable()
        useCommonJs()
        nodejs()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                dependencies {
                    implementation(npm("discord.js","14.16.3"))
                    implementation(npm("discord-api-types","0.37.102"))
                }
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrLink> {
    compilerOptions.moduleKind.set(org.jetbrains.kotlin.gradle.dsl.JsModuleKind.MODULE_COMMONJS)
}

tasks.withType(Copy::class.java) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}