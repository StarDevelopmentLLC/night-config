import org.gradle.api.tasks.testing.logging.TestLogEvent

// Common setup shared by all nightconfig libraries (core, toml, ...)

plugins {
    `java-library`
    `maven-publish`
    signing
	jacoco
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

// When building the JAR, also build some additional jars.
java {
    withJavadocJar()
    withSourcesJar()
}

// Add Automatic-Module-Name for JPMS support, and some other attributes for OSGI
tasks.jar {
	manifest {
		attributes["Automatic-Module-Name"] = "com.stardevllc.nightconfig.${project.name}"
		attributes["Bundle-SymbolicName"] = "com.stardevllc.nightconfig.${project.name}"
		attributes["Bundle-Name"] = "night-config:${project.name}"
		attributes["Bundle-Version"] = "${project.version}"
	}
}

// Set project metadata for publishing
group = "com.stardevllc.night-config"
version = "1.0.0"

// Publish the library as a Maven artifact.
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            // automatically set:
            // groupId = project.group, artifactId = project.name, version = project.version
            pom {
                name = "NightConfig ${project.name}"
                description = "A multi-format configuration library, ${project.name} module."
                url = "https://github.com/TheElectronWill/night-config"

                licenses {
                    license {
                        name = "GNU Lesser General Public License v3.0"
                        url = "https://www.gnu.org/licenses/lgpl-3.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "TheElectronWill"
                        url = "https://github.com/TheElectronWill"
                    }
                }
            }
            from(components["java"])
        }
    }
    // Publish to Sonatype OSSRH Staging API Service (OSSRH has been shut down)
    repositories {
        maven {
			name = "stardev"
            url = uri("https://repo.stardevllc.com/releases")
            credentials(PasswordCredentials::class)
        }
    }
}