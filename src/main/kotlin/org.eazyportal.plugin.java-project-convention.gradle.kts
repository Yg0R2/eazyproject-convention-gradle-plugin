import org.eazyportal.plugin.convention.extension.getJavaVersion
import org.gradle.internal.impldep.org.jsoup.safety.Safelist

plugins {
    id("jacoco")

    id("java")
}

if (project == project.rootProject) {
    apply {
        plugin("jacoco-report-aggregation")
        plugin("org.eazyportal.plugin.dependency-version-lock-convention")
    }
}

repositories {
    mavenCentral()

    gradlePluginPortal()

    maven {
        name = "github"

        credentials {
            password = project.findProperty("githubPassword") as String? ?: System.getenv("REPOSITORY_PASSWORD")
            username = project.findProperty("githubUsername") as String? ?: System.getenv("REPOSITORY_USERNAME")
        }

        url = uri("${project.findProperty("githubUrl") ?: System.getenv("REPOSITORY_URL")}/*")
    }

    mavenLocal()
}

java {
    toolchain {
        languageVersion.set(project.getJavaVersion())
    }
}

tasks {
    compileJava {
        options.compilerArgs.add("-Xlint:unchecked")
        options.isDeprecation = true
    }

    test {
        useJUnitPlatform()

        finalizedBy(jacocoTestReport)
    }

    jacocoTestCoverageVerification {
        dependsOn(test)

        classDirectories.setFrom(getFilteredFiles(classDirectories.files, project))
    }

    jacocoTestReport {
        dependsOn(test)

        classDirectories.setFrom(getFilteredFiles(classDirectories.files, project))
    }
}

fun getFilteredFiles(files: Set<File>, project: Project): List<File> = files
    .map {
        project.fileTree(it) {
            exclude(
                "**/config/**",
                "**/exception/**",
                "**/model/**",
                "**/*Configuration.*",
                "**/*Configurer.*",
                "**/*ConfigurerAdapter.*",
                "**/*ErrorHandler.*",
                "**/*Exception.*",
                "**/*ExceptionHandler.*"
            )
        }
    }
    .flatMap { it.files }
    .toList()
