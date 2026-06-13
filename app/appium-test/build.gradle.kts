import java.util.Properties

plugins {
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    testImplementation("io.appium:java-client:9.3.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}
val androidSdkDir = localProperties.getProperty("sdk.dir")

tasks.test {
    useJUnitPlatform()
    systemProperty("appium.server.url", System.getProperty("appium.server.url", "http://127.0.0.1:4723"))
    systemProperty("appium.device.name", System.getProperty("appium.device.name", "Android Emulator"))
    systemProperty(
        "appium.app.path",
        System.getProperty(
            "appium.app.path",
            "${rootProject.projectDir}/app/build/outputs/apk/debug/app-debug.apk"
        )
    )

    if (androidSdkDir != null) {
        environment("ANDROID_HOME", androidSdkDir)
        environment("ANDROID_SDK_ROOT", androidSdkDir)
    }
}
