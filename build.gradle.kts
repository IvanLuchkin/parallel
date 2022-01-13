plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "me.ivan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.typesafe.akka:akka-actor_3:2.6.17")
}
