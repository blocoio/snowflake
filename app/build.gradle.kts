import com.android.build.api.variant.FilterConfiguration
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "io.bloco.snowflake"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "io.bloco.snowflake"
        minSdk = 26
        targetSdk = 37
        versionCode = 10 // Increment by 5 to account for ABI split
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        androidResources.localeFilters += listOf("en", "fr", "ja", "pt")
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            splits {
                abi {
                    // Detect app bundle and conditionally disable split abis
                    // This is needed due to a "Sequence contains more than one matching element" error
                    // present since AGP 8.9.0, for more info see:
                    // https://issuetracker.google.com/issues/402800800

                    // AppBundle tasks usually contain "bundle" in their name
                    val isBuildingBundle = gradle.startParameter.taskNames.any { it.lowercase().contains("bundle") }

                    // Disable split abis when building appBundle
                    isEnable = !isBuildingBundle

                    reset()
                    // Specifies a list of ABIs supported by probe-engine.
                    include("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
                    // Specifies that you don't want to also generate a universal APK that includes all ABIs.
                    isUniversalApk = true
                }
            }

            // Map for the version code that gives each ABI a value.
            val abiCodes = mapOf("armeabi-v7a" to 1, "arm64-v8a" to 2, "x86" to 3, "x86_64" to 4)

            androidComponents {
                onVariants { variant ->
                    variant.outputs.forEach { output ->
                        val name = output.filters.find { it.filterType == FilterConfiguration.FilterType.ABI }?.identifier
                        val baseAbiCode = abiCodes[name]
                        if (baseAbiCode != null) {
                            output.versionCode.set(
                                baseAbiCode + (output.versionCode.get() ?: 0),
                            )
                        }
                    }
                }
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
            freeCompilerArgs.addAll(
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
                "-opt-in=androidx.compose.ui.text.ExperimentalTextApi",
            )
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
    lint {
        disable += listOf(
            "MissingTranslation",
        )
        informational += listOf(
            "AndroidGradlePluginVersion",
            "GradleDependency",
            "NewerVersionAvailable",
            "ObsoleteLintCustomCheck",
            "VectorPath",
            "VectorRaster",
        )
        checkTestSources = true
        showAll = true
        warningsAsErrors = true
    }
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.app)
    ksp(libs.bundles.ksp)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
