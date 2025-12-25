plugins {
    `java-library`
    `maven-publish`
}

group = "ru.logonik"
version = "5.0.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = ("spigotmc-repo")
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = ("sonatype")
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven { url = uri("https://repo.aikar.co/content/groups/aikar/") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    //compileOnly("org.spigotmc:spigot-api:1.13.1-R0.1-SNAPSHOT")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT") // MIT License
    implementation("dev.triumphteam:triumph-gui:3.1.11") // MIT License
    implementation("com.google.code.gson:gson:2.13.1") // Apache License Version 2.0

    compileOnly("com.j256.ormlite:ormlite-jdbc:6.1") // ISC License (https://opensource.org/licenses/ISC)
}

java {
    withJavadocJar()
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility =  JavaVersion.VERSION_11
    toolchain {
        languageVersion = JavaLanguageVersion.of(21);
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
    repositories {
        mavenLocal()
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
    options.isFork = true
    options.forkOptions.executable = System.getProperty("java.home") + "/bin/javac.exe"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

