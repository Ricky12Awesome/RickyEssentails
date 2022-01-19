import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val serializationVersion: String by project
val coroutinesVersion: String by project
val exposedVersion: String by project
val paperVersion: String by project
val commandAPIVersion: String by project
val vaultVersion: String by project

plugins {
  kotlin("jvm") version "1.6.10"
  kotlin("plugin.serialization") version "1.6.10"
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
  mavenCentral()
  maven("https://papermc.io/repo/repository/maven-public/")
  maven("https://repo.codemc.org/repository/maven-public/")
  maven("https://jitpack.io")
}

dependencies {
  shadow(kotlin("stdlib"))

  // Kotlinx Serialization https://github.com/Kotlin/kotlinx.serialization
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
  shadow("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")

  // Kotlinx Coroutines https://github.com/Kotlin/kotlinx.coroutines
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
  shadow("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

  // Exposed https://github.com/JetBrains/Exposed
  implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
  shadow("org.jetbrains.exposed:exposed-core:$exposedVersion")

  implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
  shadow("org.jetbrains.exposed:exposed-dao:$exposedVersion")

  implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
  shadow("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

  // PaperMC https://papermc.io/
  compileOnly("io.papermc.paper:paper-api:$paperVersion")

  // CommandAPI https://commandapi.jorel.dev/
  compileOnly("dev.jorel.CommandAPI:commandapi-core:$commandAPIVersion")

  // Vault https://github.com/MilkBowl/VaultAPI
  compileOnly("com.github.MilkBowl:VaultAPI:$vaultVersion")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "17"
  }

  shadowJar {
    destinationDirectory.set(file("./server/plugins"))
  }
}