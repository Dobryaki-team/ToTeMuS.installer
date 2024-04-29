plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("edu.sc.seis.launch4j") version "3.0.5"
    id("groovy")
}

group = "totemus.space"
version = ""

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.repository.redhat.com/ga/")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.9")
    implementation("com.google.code.gson:gson:2.9.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.codehaus.groovy:groovy-all:3.0.9")
    implementation("org.python:jython-standalone:2.7.2")
    implementation("org.apache.logging.log4j:log4j-core:2.19.0.redhat-00001")
    implementation(kotlin("script-runtime"))
}

tasks.jar {
    File("build").delete()
    File("releases").delete()
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes["Main-Class"] = "totemus.space.MainKt"
    }
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({ configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) } })
    getReleases()
}

fun getReleases() {
    val pathReleases = File("releases")
    if (!pathReleases.exists()) {
        pathReleases.mkdirs()
    }
    File("build/launch4j/installer.blin_totemusa.exe")
        .renameTo(File("releases/ToTeMuS_installer._.exe"));
    File("build/libs/installer.blin_totemusa.jar")
        .renameTo(File("releases/ToTeMuS_installer._.jar"));
}

launch4j {
    File("build").delete()
    File("releases").delete()
    mainClassName = "totemus.space.MainKt"
    icon = "${projectDir}/src/main/resources/icons/temp_icon.ico"
    getReleases()
}
