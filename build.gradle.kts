import java.util.Locale

plugins {
    id("com.android.application") version "8.0.0" apply false
    id("com.android.library") version "8.0.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.20" apply false
    id("com.diffplug.spotless") version "6.18.0"
    id("com.github.ben-manes.versions") version "0.46.0"
}

spotless {
    kotlinGradle {
        target("**/*.kts")
        targetExclude("$buildDir/**/*.kts")
        ktlint()
    }
}

subprojects {
    apply<com.diffplug.gradle.spotless.SpotlessPlugin>()

    spotless {
        kotlin {
            target("**/*.kt")
            targetExclude("$buildDir/**/*.kt")
            ktlint()
        }

        kotlinGradle {
            target("**/*.kts")
            targetExclude("$buildDir/**/*.kts")
            ktlint()
        }

        format("xml") {
            target("**/*.xml")
            targetExclude("$buildDir/**/*.xml")
            // Look for the first XML tag that isn't the xml declaration (<?xml)
            licenseHeader(
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>",
                "(<[^?])"
            )
            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()
        }
    }
}

tasks.named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates")
    .configure {
        rejectVersionIf {
            isNonStable(candidate.version)
        }
    }

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any {
        version.uppercase(Locale.getDefault()).contains(it)
    }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}
