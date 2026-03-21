plugins {
    kotlin("jvm") version "2.2.21"
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.0"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }

}

group = "com.kuraky"
version = "v1.1"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("com.zaxxer:HikariCP:5.1.0")
        implementation("org.postgresql:postgresql:42.7.2")
        implementation("org.mongodb:mongodb-driver-sync:4.11.1")
        implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    }
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}
tasks.build {
    dependsOn("shadowJar")
}