plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "internal"
version = "2.0.2.2"

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins.register("internal") {
        id = "internal"
        implementationClass = "internal.InternalPlugin"
    }
}
