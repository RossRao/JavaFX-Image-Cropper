plugins {
    java
    `java-library`
    id("org.openjfx.javafxplugin") version "0.1.0"
}

version = "26.1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.openjfx:javafx-controls:17.0.10")
    compileOnly("org.openjfx:javafx-graphics:17.0.10")
}

javafx {
    version = "17.0.10"
    modules = listOf("javafx.controls", "javafx.graphics")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
