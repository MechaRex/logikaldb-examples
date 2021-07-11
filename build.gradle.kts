import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinxVersion: String by project

plugins {
    kotlin("jvm") version "1.5.20"
}

group = "com.logikaldb"
version = "1.0"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("com.logikaldb:logikaldb:0.4.0")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}