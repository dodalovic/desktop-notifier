import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar


plugins {
    val kotlinVersion = "1.4.21"
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "5.2.0"
    kotlin("plugin.serialization") version kotlinVersion
}

group = "com.odalovic"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val slf4jVersion = "1.7.25"
    implementation(kotlin("stdlib"))
    implementation("info.picocli:picocli:4.5.2")
    annotationProcessor("info.picocli:picocli-codegen:4.5.2")
    kapt("info.picocli:picocli-codegen:4.5.2")
    implementation("fr.jcgay.send-notification:send-notification:0.16.0")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
}

kapt {
    arguments {
        arg("project", "${project.group}/${project.name}")
    }
}

tasks.withType<ShadowJar>() {
    manifest {
        attributes["Main-Class"] = "com.odalovic.desktopnotifier.DesktopNotifierKt"
    }
}
