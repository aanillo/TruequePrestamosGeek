import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.compose") version "1.5.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)

    // Usar la versión correcta de Compose para Desktop
    implementation("org.jetbrains.compose.runtime:runtime:1.5.10")
    implementation("org.jetbrains.compose.material:material:1.5.10")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.h2database:h2:2.1.210")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("com.google.code.gson:gson:2.8.8")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "TruequesPrestamosGeek"
            packageVersion = "1.0.0"
        }
    }
}
