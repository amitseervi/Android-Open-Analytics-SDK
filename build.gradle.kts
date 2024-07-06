/*
 * Copyright (c) [2024] Amitkumar Chaudhary
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import nl.littlerobots.vcu.plugin.versionCatalogUpdate

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.baselineprofile) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.google.service) apply false
    alias(libs.plugins.gradle.versions)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.spotless)
    alias(libs.plugins.ktlint)
}

versionCatalogUpdate {
    sortByKey = true
    keep {
        keepUnusedVersions = true
        keepUnusedPlugins = true
        keepUnusedLibraries = true
    }
}
val stableVersionRegex = "/^[0-9,.v-]+(-r)?\$/"

fun isNonStable(version: String): Boolean = !version.matches(stableVersionRegex.toRegex())

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    resolutionStrategy {
        componentSelection {
            all {
                if (isNonStable(candidate.version) && !isNonStable(currentVersion)) {
                    reject("Release candidate")
                }
            }
        }
    }
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**/*.kt")
        ktlint {
            android = true
        }
        licenseHeaderFile(rootProject.file("spotless/copyright.txt"))
    }
    format("kts") {
        target("**/*.kts")
        targetExclude("**/build/**/*.kts")
        // Look for the first line that doesn't have a block comment (assumed to be the license)
        licenseHeaderFile(rootProject.file("spotless/copyright.txt"), "(^(?![\\/ ]\\*).*$)")
    }
}
