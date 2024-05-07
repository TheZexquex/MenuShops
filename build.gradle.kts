import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

group = "dev.thezexquex"
version = "1.0-SNAPSHOT"

val mainClass = "${group}.${rootProject.name.lowercase()}.MenuShopsPlugin"
val shadeBasePath = "${group}.${rootProject.name.lowercase()}.libs."

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://eldonexus.de/repository/maven-public")
    maven("https://repo.xenondevs.xyz/releases")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation("org.spongepowered", "configurate-hocon", "4.1.2")
    implementation("org.spongepowered", "configurate-yaml", "4.1.2")
    implementation("org.incendo", "cloud-paper", "2.0.0-beta.2")
    implementation("org.incendo", "cloud-minecraft-extras", "2.0.0-beta.2")
    implementation("xyz.xenondevs.invui", "invui", "1.27")
    implementation("de.eldoria.jacksonbukkit", "paper", "1.2.0")
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml", "2.14.2")

    compileOnly("io.papermc.paper", "paper-api", "1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")
    compileOnly("me.clip", "placeholderapi", "2.11.5")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

bukkit {

    name = "MenuShops"
    version = "0.1.0"
    description = "The only menu shop plugin you need"

    author = "TheZexquex"

    main = mainClass

    foliaSupported = false

    apiVersion = "1.20"

    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD

    softDepend = listOf("Vault")

    defaultPermission = BukkitPluginDescription.Permission.Default.OP
}



tasks {
    getByName<Test>("test") {
        useJUnitPlatform()
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        fun relocateDependency(from : String) = relocate(from, "$shadeBasePath$from")

        relocateDependency("org.spongepowered")
        relocateDependency("org.incendo")
        relocateDependency("xyz.xenondevs.invui")
        relocateDependency("de.eldoria.jacksonbukkit")
        relocateDependency("com.fasterxml.jackson.dataformat")
    }
}

