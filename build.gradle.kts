plugins {
    `java-library`
    `maven-publish`
}

group = "ru.logonik"
version = "1.0-SNAPSHOT"

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
    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")
    api("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    api("dev.triumphteam:triumph-gui:3.1.11")
}

java {
    withJavadocJar()
    withSourcesJar()
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

