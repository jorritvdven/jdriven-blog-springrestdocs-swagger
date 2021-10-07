import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.hidetake.gradle.swagger.generator.GenerateSwaggerUI
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	id("org.springframework.boot") version "2.5.5"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("org.hidetake.swagger.generator") version "2.18.2"
	id("com.epages.restdocs-api-spec") version "0.12.0"
	kotlin("jvm") version "1.5.31"
	kotlin("plugin.spring") version "1.5.31"
}

group = "com.jdriven.blog"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

val snippetsDir = file("${project.buildDir}/generated-snippets")

dependencies {
	swaggerUI("org.webjars:swagger-ui:3.51.2")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testImplementation("com.epages:restdocs-api-spec-mockmvc:0.12.0")
}

tasks {
	withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs = listOf("-Xjsr305=strict")
			jvmTarget = "11"
		}
	}

	withType<Test> {
		useJUnitPlatform()
	}

	test {
		outputs.dir(snippetsDir)
	}

	withType<GenerateSwaggerUI> {
		dependsOn("openapi3")
	}

	register<Copy>("copySwaggerUI") {
		dependsOn("generateSwaggerUIHelloService")

		val generateSwaggerTask = named<GenerateSwaggerUI>("generateSwaggerUIHelloService").get()
		from("${generateSwaggerTask.outputDir}")
		into("${project.buildDir}/resources/main/static")
	}

	withType<BootJar> {
		dependsOn("copySwaggerUI")
	}
}

openapi3 {
	setServer("https://hello-service.example.com")

	title = "Hello Service API"
	description = "An API providing greetings to users."
	version = "1.0.0"
	format = "yaml"
}

swaggerSources {
	register("helloService").configure {
		setInputFile(file("${project.buildDir}/api-spec/openapi3.yaml"))
	}
}
