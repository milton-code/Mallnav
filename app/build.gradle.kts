plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.proyecto.mallnav"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.proyecto.mallnav"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
}

dependencies {
    //navigine SDK
    implementation(files("libs/libnavigine.aar"))
    //test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //lifecycle
    implementation ("androidx.lifecycle:lifecycle-livedata:2.8.1")
    implementation("androidx.lifecycle:lifecycle-process:2.8.1")
    //support
    implementation("androidx.appcompat:appcompat:1.7.0")
    //ui
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation ("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    //firebase
    implementation("com.google.firebase:firebase-firestore:25.0.0")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-analytics")
    //fragment
    implementation ("androidx.fragment:fragment:1.8.0")
    //navigation
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation ("androidx.navigation:navigation-ui:2.7.7")
    //glide
    implementation ("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.0")
    //lottie
    implementation ("com.airbnb.android:lottie:5.0.3")
    //shimmer
    implementation ("com.facebook.shimmer:shimmer:0.5.0")
    //volley
    implementation("com.android.volley:volley:1.2.1")
    //worker
    implementation("androidx.work:work-runtime:2.9.0")
}