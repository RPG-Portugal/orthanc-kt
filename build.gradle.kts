plugins {
    kotlin("jvm") version "2.0.21"
}

group = "com.rpgportugal"
version = "1.0.0"

repositories {
    mavenCentral()
}

val ktArrowVersion = properties["kt.arrow.version"]

dependencies {
    implementation("io.arrow-kt:arrow-core:$ktArrowVersion")
    implementation("io.arrow-kt:arrow-fx-coroutines:$ktArrowVersion")
    implementation("io.arrow-kt:arrow-autoclose:$ktArrowVersion")
    implementation("io.arrow-kt:arrow-optics:$ktArrowVersion")
    implementation("io.arrow-kt:arrow-collectors:$ktArrowVersion")
    implementation("io.arrow-kt:arrow-eval:$ktArrowVersion")
    implementation("io.arrow-kt:arrow-core-serialization:$ktArrowVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${properties["courotines.version"]}")
    implementation("ch.qos.logback:logback-classic:${properties["logback.version"]}")
    implementation("io.insert-koin:koin-core-jvm:${properties["koin.version"]}")
    implementation("net.dv8tion:JDA:${properties["jda.version"]}")
    implementation("club.minnced:jda-ktx:${properties["jda.ktx.version"]}")
    implementation("org.ktorm:ktorm-core:${properties["ktorm.version"]}")
    implementation("org.xerial:sqlite-jdbc:${properties["sqlite.driver.version"]}")
    implementation("org.quartz-scheduler:quartz:${properties["quartz.version"]}")
    //Bot Module Dependencies
    implementation("dev.diceroll:dice-parser:0.3.0")

    // Test
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}