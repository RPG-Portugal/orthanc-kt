plugins {
    kotlin("jvm") version "2.0.21"
}

group = "com.rpgportugal"
version = "2.0.3"

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
    implementation("jakarta.persistence:jakarta.persistence-api:${properties["jakarta.persistence.version"]}")
    implementation("org.hibernate.orm:hibernate-core:${properties["hibernate.version"]}")
    implementation("org.hibernate.orm:hibernate-hikaricp:${properties["hibernate.version"]}")
    implementation("org.hibernate.orm:hibernate-community-dialects:${properties["hibernate.dialects.version"]}")
    implementation("org.reflections:reflections:${properties["reflections.version"]}")
    implementation("org.postgresql:postgresql:${properties["postgresql.driver.version"]}")
    implementation("org.quartz-scheduler:quartz:${properties["quartz.version"]}")
    //Bot Module Dependencies
    implementation("dev.diceroll:dice-parser:${properties["diceparser.version"]}")

    // Test
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["Implementation-Title"] = "RPG PT Orthanc"
        attributes["Implementation-Version"] = version
        attributes["Main-Class"] = "com.rpgportugal.orthanc.kt.Main"
    }
}

tasks {
    register("fatJar", Jar::class.java) {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes("Main-Class" to "com.rpgportugal.orthanc.kt.Main")
        }
        from(configurations.runtimeClasspath.get()
            .onEach { println("add from dependencies: ${it.name}") }
            .map { if (it.isDirectory) it else zipTree(it) })
        val sourcesMain = sourceSets.main.get()
        sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
        from(sourcesMain.output)
    }
}
