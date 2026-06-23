plugins {
    id("fabric-loom") version "1.17.12"
    kotlin("jvm") version "2.3.20"
}

val modVersion: String by project
val mavenGroup: String by project
val archivesBaseName: String by project

version = modVersion
group = mavenGroup

base { archivesName.set(archivesBaseName) }

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.architectury.dev/")
    maven("https://maven.impactdev.net/repository/development/") // Cobblemon
    maven("https://maven.shedaniel.me/")                          // REI
    maven("https://maven.terraformersmc.com/")                    // ModMenu / EMI compat
    maven("https://api.modrinth.com/maven")                       // CobbleDollars
}

val minecraftVersion: String by project
val yarnMappings: String by project
val loaderVersion: String by project
val fabricVersion: String by project
val kotlinLoaderVersion: String by project
val cobblemonVersion: String by project
val reiVersion: String by project

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$yarnMappings:v2")
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
    modImplementation("net.fabricmc:fabric-language-kotlin:$kotlinLoaderVersion")

    modImplementation("com.cobblemon:fabric:$cobblemonVersion")
    modImplementation("me.shedaniel:RoughlyEnoughItems-fabric:$reiVersion")
}

tasks.processResources {
    inputs.property("version", modVersion)
    filesMatching("fabric.mod.json") {
        expand("version" to modVersion)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
}

kotlin {
    jvmToolchain(21)
}

tasks.jar {
    from("LICENSE") { rename { "${it}_${archivesBaseName}" } }
}
