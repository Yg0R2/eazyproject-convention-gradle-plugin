plugins.apply("maven-publish")

configure<PublishingExtension> {
    publications {
        if (isKotlinDslPluginPublishEnabled().not()) {
            create("maven", MavenPublication::class) {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()

                from(project.components["java"])
            }
        }

        withType<MavenPublication> {
            suppressAllPomMetadataWarnings()

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
        } else {
            maven {
                name = "github"

                credentials {
                    password = "${project.properties["githubPassword"]!!}"
                    username = "${project.properties["githubUsername"]!!}"
                }

                url = uri("${project.properties["githubUrl"]!!}/${project.rootProject.name}")
            }
        }
    }
}

fun isKotlinDslPluginPublishEnabled(): Boolean =
    extensions.findByType<GradlePluginDevelopmentExtension>()
        ?.isAutomatedPublishing
        ?: false
