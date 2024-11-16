plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.diegoalvis.travelapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.diegoalvis.travelapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.coil.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.moshi)

    // Coroutines
    implementation(platform(libs.coroutinesBom))
    implementation(libs.coroutinesRx)
    implementation(libs.coroutinesAndroid)

    // gRPC
    implementation(libs.grpcAndroid)
    implementation(libs.grpcKotlinStub)
    implementation(libs.grpcOkhttp)
    implementation(libs.grpcStub)
    implementation(libs.grpcProtobufLite)
    implementation(libs.protobufKotlinLite)

    testImplementation(libs.junit)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

protobuf {
    protoc {
        // Setup protobuf
        artifact = libs.protobufProtoc.get().toString()
    }
    plugins {
        // Generates logic to connect to server such as channels and stubs
        create("grpc") {
            artifact = libs.grpcJava.get().toString()
        }
        // With this we add support for Kotlin and coroutines-based stubs
        create("grpckt") {
            artifact = "${libs.grpcKotlin.get()}:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            // This creates and sets the option for the autogenerated code in Java and Kotlin
            // gRPC doesn't work with Kotlin out of the box it relies on the Java code
            it.builtins {
                create("java") {
                    option("lite")
                }
                create("kotlin") {
                    option("lite")
                }
            }
            it.plugins {
                create("grpc") {
                    option("lite")
                }
                create("grpckt") {
                    option("lite")
                }
            }
        }
    }

}
