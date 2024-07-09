import java.util.Properties

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
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
}

android {
    namespace = "com.rignis.analyticssdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    val keystoreProperties = Properties()
    keystoreProperties.load(
        File(
            project.rootProject.projectDir,
            "keystore/keystore.properties"
        ).inputStream()
    )

    signingConfigs {
        create("release") {
            keyAlias =
                keystoreProperties.getProperty("rignis-analytics-release-key-alias") as String
            storeFile = File(project.rootProject.projectDir, "keystore/analytics-sdk-keystore.jks")
            keyPassword =
                keystoreProperties.getProperty("rignis-analytics-release-key-password") as String
            storePassword =
                keystoreProperties.getProperty("rignis-analytics-release-store-password") as String
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false // Disable minifying for sdk
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["debug"])

                groupId = "com.rignis"
                artifactId = "analyticssdk"
                version = "1.0.0"

                pom {
                    name.set("Sample Analytics SDK library")
                    description.set("Open analytics sdk library for triggering analytics event to server using post api request")
                    url.set("https://github.com/amitseervi/analyticssdk")

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            distribution.set("repo")
                        }
                    }

                    developers {
                        developer {
                            id.set("amitseervi")
                            name.set("Amitkumar Chaudhary")
                            email.set("amitchaudhary.adc@gmail.com")
                        }
                    }

                    scm { url = "https://svn.apache.org/viewvc/maven" }
                }
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.androidx.room.testing)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.retrofit)

    implementation(libs.androidx.startup.runtime)

    implementation(libs.timber)

    implementation(libs.androidx.work.runtime.ktx)
    testImplementation(libs.androidx.work.testing)

    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.okhttp.converter.gson)
    androidTestImplementation(libs.okhttp.mockwebserver)

    implementation(libs.koin.android)
    androidTestImplementation(libs.koin.test)
    androidTestImplementation(libs.koin.android.test)
    androidTestImplementation(libs.truth)
}