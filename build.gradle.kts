plugins {
    kotlin("jvm") version "1.9.25"
    id ("com.gradleup.shadow") version "9.0.0-beta6"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "xyz.reportcards"
version = "0.1"

repositories {
    mavenCentral()
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }

    maven("https://repo.codemc.io/repository/maven-public/") {
        name = "CodeMC"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // NBT library
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.14.1")

    // Command manager
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")

    // Gui library
    implementation ("com.github.stefvanschie.inventoryframework:IF:0.10.19")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        // Ensure the target JVM version is explicitly set
        jvmTarget = "21"
    }
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.shadowJar {

}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.20.6")
    }
}