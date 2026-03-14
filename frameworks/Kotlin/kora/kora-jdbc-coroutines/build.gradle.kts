import gg.jte.ContentType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    application
    kotlin("jvm") version "1.9.25"
    id("com.google.devtools.ksp") version "1.9.25-1.0.20"
    id("gg.jte.gradle") version "3.2.3"
}

val koraBom: Configuration by configurations.creating
configurations {
    ksp.get().extendsFrom(koraBom)
    compileOnly.get().extendsFrom(koraBom)
    implementation.get().extendsFrom(koraBom)
}

repositories {
    mavenCentral()
}

dependencies {
    koraBom(platform("ru.tinkoff.kora:kora-parent:${property("koraVersion")}"))

    ksp("ru.tinkoff.kora:symbol-processors")

    compileOnly("jakarta.annotation:jakarta.annotation-api:2.1.1")

    implementation("gg.jte:jte:3.2.3")
    implementation("org.postgresql:postgresql:42.7.10")
    implementation("org.slf4j:slf4j-nop:2.0.17")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("ru.tinkoff.kora:config-hocon")
    implementation("ru.tinkoff.kora:database-jdbc")
    implementation("ru.tinkoff.kora:http-server-undertow")
    implementation("ru.tinkoff.kora:json-module")
}

kotlin {
    jvmToolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}

sourceSets.main {
    java.srcDir("build/generated/jte")
}

application {
    applicationName = "application"
    mainClass.set("io.koraframework.benchmark.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8")
}

tasks.run.configure {
    environment(
        mapOf(
            "POSTGRES_JDBC_URL" to "jdbc:postgresql://localhost:5432/postgres",
            "POSTGRES_USER" to "postgres",
            "POSTGRES_PASS" to "postgres"
        )
    )
}

tasks.distTar {
    archiveFileName.set("application.tar")
}

jte {
    generate()
    binaryStaticContent.set(true)
    contentType.set(ContentType.Html)
}

tasks.generateJte {
    sourceDirectory.set(projectDir.toPath().resolve("src/main/jte"))
    targetDirectory.set(projectDir.toPath().resolve("build/generated/jte"))
    contentType.set(ContentType.Html)
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.named("compileKotlin") {
    dependsOn(tasks.generateJte)
}
