import com.android.SdkConstants
import com.android.build.api.variant.impl.VariantOutputImpl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.text.SimpleDateFormat
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget("21")
        freeCompilerArgs.addAll(listOf(
            "-Xno-param-assertions",
            "-Xno-call-assertions",
            "-Xno-receiver-assertions"
        ))
    }
}

androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            if (output is VariantOutputImpl) {
                val versionName = output.versionName.getOrElse("null")
                val versionCode = output.versionCode.getOrElse(0)
                val newFileName = "${libs.versions.project.name.get()}_${versionName}_${versionCode}_${variant.name}_${System.currentTimeMillis() / 1000}.apk"
                output.outputFileName.set(newFileName)
            }
        }
    }
}

android {
    namespace = libs.versions.project.app.packageName.get()
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = libs.versions.project.app.packageName.get()
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionName = libs.versions.project.app.versionName.get()
        versionCode = libs.versions.project.app.versionCode.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BUILD_TIME", "\"" + SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis()) + "\"")
        ndk {
            abiFilters.add(SdkConstants.ABI_ARM64_V8A)
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    androidResources {
        additionalParameters += listOf("--stable-ids", "stableIds.txt")
        additionalParameters += listOf("--allow-reserved-package-id", "--package-id", "0x60")
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    lint { checkReleaseBuilds = false }
    val properties = Properties()
    runCatching { properties.load(project.rootProject.file("local.properties").inputStream()) }
    val ksPath = properties.getProperty("KEYSTORE_PATH") ?: System.getenv("KEYSTORE_PATH")
    val ksPWD = properties.getProperty("KEYSTORE_PWD") ?: System.getenv("KEYSTORE_PWD")
    val kAlias = properties.getProperty("KEY_ALIAS") ?: System.getenv("KEY_ALIAS")
    val kPWD = properties.getProperty("KEY_PWD") ?: System.getenv("KEY_PWD")
    signingConfigs {
        register("release") {
            storeFile = file(ksPath)
            storePassword = ksPWD
            keyAlias =kAlias
            keyPassword = kPWD
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {
    compileOnly(libs.xposed.api)
    implementation(libs.yukihookapi.api)
    ksp(libs.yukihookapi.ksp.xposed)
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.extension)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.ui)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.dexkit)
    implementation(project(mapOf("path" to ":hyperx-compose")))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.constraintlayout.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso)
}