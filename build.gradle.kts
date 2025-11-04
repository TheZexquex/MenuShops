import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta15"
    id("de.eldoria.plugin-yml.bukkit") version "0.7.1"
    id("xyz.jpenilla.run-paper") version "2.3.1"

    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
}

group = "dev.thezexquex"
version = "0.2.1"

val mainClass = "${group}.${rootProject.name.lowercase()}.MenuShopsPlugin"
val shadeBasePath = "${group}.${rootProject.name.lowercase()}.libs."

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.xenondevs.xyz/releases")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.nightexpressdev.com/releases")

    maven("https://repo.unknowncity.de/public")
}

dependencies {
    implementation("org.spongepowered", "configurate-yaml", "4.2.0-SNAPSHOT")
    implementation("org.incendo", "cloud-paper", "2.0.0-beta.13")
    implementation("org.incendo", "cloud-minecraft-extras", "2.0.0-beta.13")
    implementation("org.incendo", "cloud-brigadier", "2.0.0-beta.13")
    implementation("xyz.xenondevs.invui", "invui", "2.0.0-alpha.20")

    compileOnly("su.nightexpress.coinsengine", "CoinsEngine", "2.5.3")
    compileOnly("io.papermc.paper", "paper-api", "1.21.10-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7.1")
    compileOnly("me.clip", "placeholderapi", "2.11.5")

    paperweight.paperDevBundle("1.21.10-R0.1-SNAPSHOT")
}

paperweight {
    paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
}

bukkit {
    name = "MenuShops"
    version = "${rootProject.version}"
    description = "The only menu shop plugin you need"

    author = "TheZexquex"

    main = mainClass

    foliaSupported = false

    apiVersion = "1.21"

    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD

    softDepend = listOf("Vault, PlaceholderAPI")

    defaultPermission = BukkitPluginDescription.Permission.Default.OP
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

runPaper {
    disablePluginJarDetection()
}

tasks {

    runServer {
        dependsOn(reobfJar)
        minecraftVersion("1.21.10")

        pluginJars.from(
            file("build/libs/${project.name}-${project.version}-reobf.jar")
        )

        downloadPlugins {

            url("https://github.com/MilkBowl/Vault/releases/download/1.7.3/Vault.jar")
            // url("https://github.com/EssentialsX/Essentials/releases/download/2.20.1/EssentialsX-2.20.1.jar")
            // CoinsEngine
            url("https://api.spiget.org/v2/resources/84121/download")
            // NightCore
            url("https://github.com/nulli0n/nightcore-spigot/releases/download/v2.9.4/nightcore-2.9.4.jar")
            //hangar("PlaceholderAPI", "2.11.5")
            jvmArgs("-Dcom.mojang.eula.agree=true")
        }
    }

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