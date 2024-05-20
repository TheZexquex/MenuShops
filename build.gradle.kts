import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import java.net.URL

plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.7"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "2.3.0"
}

group = "dev.thezexquex"
version = "1.0-SNAPSHOT"

val mainClass = "${group}.${rootProject.name.lowercase()}.MenuShopsPlugin"
val shadeBasePath = "${group}.${rootProject.name.lowercase()}.libs."

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.xenondevs.xyz/releases")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven {
        url = uri("https://repo.unknowncity.de/private")
        credentials (PasswordCredentials::class) {
            username = System.getenv("MVN_REPO_USERNAME")
            password = System.getenv("MVN_REPO_PASSWORD")
        }
    }
}

dependencies {
    implementation("org.spongepowered", "configurate-yaml", "4.2.0-SNAPSHOT")
    implementation("org.incendo", "cloud-paper", "2.0.0-beta.7")
    implementation("org.incendo", "cloud-minecraft-extras", "2.0.0-beta.7")
    implementation("xyz.xenondevs.invui", "invui", "1.30")

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")
    compileOnly("su.nightexpress.coinsengine", "CoinsEngine", "2.3.3")
    compileOnly("io.papermc.paper", "paper-api", "1.20.6-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")
    compileOnly("me.clip", "placeholderapi", "2.11.5")
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

    softDepend = listOf("Vault, PlaceholderAPI")

    defaultPermission = BukkitPluginDescription.Permission.Default.OP
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    getByName<Test>("test") {
        useJUnitPlatform()
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    runServer {
        minecraftVersion("1.20.6")

        downloadPlugins {
            url("https://github.com/MilkBowl/Vault/releases/download/1.7.3/Vault.jar")
            url("https://github.com/EssentialsX/Essentials/releases/download/2.20.1/EssentialsX-2.20.1.jar")
            url("https://cdn.thezexquex.dev/s/3s4T45pnTiYg4fe/download")
            url("https://cdn.thezexquex.dev/s/jMzSo2LGSyZCXoB/download")
            //hangar("PlaceholderAPI", "2.11.5")
            jvmArgs("-Dcom.mojang.eula.agree=true")
        }
    }

    shadowJar {
        fun relocateDependency(from : String) = relocate(from, "$shadeBasePath$from")

        relocateDependency("org.spongepowered")
        relocateDependency("org.incendo")
        relocateDependency("xyz.xenondevs.invui")
        relocateDependency("de.eldoria.jacksonbukkit")
        relocateDependency("com.fasterxml.jackson.dataformat")
    }

    register<Copy>("copyToServer") {
        val path = System.getenv("SERVER_DIR")
        if (path.toString().isEmpty()) {
            println("No SERVER_DIR env variable set")
            return@register
        }
        from(shadowJar)
        destinationDir = File(path.toString())
    }
}