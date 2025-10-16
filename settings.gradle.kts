pluginManagement {

  repositories {
    gradlePluginPortal()

    mavenCentral()
    mavenLocal()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {

  repositories {
    mavenCentral()
    mavenLocal()
  }

  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  rulesMode.set(RulesMode.PREFER_SETTINGS)
}

rootProject.name = "modjit"