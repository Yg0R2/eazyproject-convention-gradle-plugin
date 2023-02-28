plugins.apply("maven-publish")

configure<PublishingExtension> {
    publications {
        create("maven", MavenPublication::class) {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(project.components["java"])
        }

        withType<MavenPublication> {
            versionMapping {
                allVariants {
                    fromResolutionResult()
                }
            }
        }
    }

    repositories {
        if (project.version.toString().endsWith("-SNAPSHOT")) {
            mavenLocal()
        }
        else {
            maven {
                name = "github"

                credentials {
                    password = project.findProperty("githubPassword") as String? ?: System.getenv("REPOSITORY_PASSWORD")
                    username = project.findProperty("githubUsername") as String? ?: System.getenv("REPOSITORY_USERNAME")
                }

                url = uri("${project.findProperty("githubUrl") ?: System.getenv("REPOSITORY_URL")}/${project.rootProject.name}")
            }
        }
    }
}
