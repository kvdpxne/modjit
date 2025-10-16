plugins {
  id("java")
  id("maven-publish")
}

description = ""
group = "me.kvdpxne"
version = "0.1.0"

val targetJavaVersion = 8
val javaVersion = JavaVersion.toVersion(targetJavaVersion)

dependencies {
  testImplementation(platform("org.junit:junit-bom:5.10.0"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
  if (JavaVersion.current() < javaVersion) {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
  }

  sourceCompatibility = javaVersion
  targetCompatibility = javaVersion
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      artifactId = project.name
      groupId = project.group.toString()
      version = project.version.toString()

      from(components["java"])
    }
  }
}

tasks {

  withType<Test> {
    useJUnitPlatform()
  }
}