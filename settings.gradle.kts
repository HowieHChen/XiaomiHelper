@file:Suppress("UnstableApiUsage")


pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://api.xposed.info/")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io/")
        maven("https://api.xposed.info/")
        maven("https://maven.aliyun.com/repository/public")
    }
}

rootProject.name = "XiaomiHelper"
include(":app", ":hyperx-compose")