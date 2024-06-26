plugins {
    id("java")
    id("maven-publish")
    id("com.gorylenko.gradle-git-properties") version "2.3.1"
}

group = "com.alttd"
version = System.getenv("BUILD_NUMBER") ?: "1.0-SNAPSHOT"
description = "Altitude's Transfer Items plugin"

apply<JavaLibraryPlugin>()

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

gitProperties {
    keys = listOf("git.commit.id", "git.commit.time")
}

tasks {
    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }

    withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }

    jar {
        archiveFileName.set("${rootProject.name}.jar")
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        filesMatching("plugin.yml") {
            expand(Pair("version", project.version))
        }
    }
}

dependencies {
    compileOnly("com.alttd:Galaxy-API:1.20.4-R0.1-SNAPSHOT") {
        isChanging = true
    }
}