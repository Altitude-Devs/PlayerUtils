rootProject.name = "PlayerUtils"

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://dev.alttd.com/snapshots") // Altitude - Galaxy
        maven("https://repo.destro.xyz/snapshots") // Altitude - Galaxy
        maven("'https://jitpack.io'") // Vault
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}