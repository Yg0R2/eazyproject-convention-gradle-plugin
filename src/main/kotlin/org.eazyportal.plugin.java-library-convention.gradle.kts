plugins {
    id("org.eazyportal.plugin.java-project-convention")
    id("org.eazyportal.plugin.publish-convention")

    id("java-library")
    id("org.cyclonedx.bom")
}

tasks {
    check {
        finalizedBy(cyclonedxBom)
    }

    jar {
        manifest {
            attributes["Implementation-Version"] = project.version
        }
    }
}