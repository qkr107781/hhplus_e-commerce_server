plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.asciidoctor.jvm.convert") version "3.3.2"
}

fun getGitHash(): String {
	return providers.exec {
		commandLine("git", "rev-parse", "--short", "HEAD")
	}.standardOutput.asText.get().trim()
}

group = "kr.hhplus.be"
version = getGitHash()

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
	}
}

dependencies {
	// Spring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-aop")

	// DB
	runtimeOnly("com.mysql:mysql-connector-j")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mysql")
	testImplementation("org.redisson:redisson:3.50.0")
	testImplementation("org.testcontainers:kafka")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Swagger (SpringDoc)
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")


	// Lombok
	compileOnly("org.projectlombok:lombok:1.18.38")
	annotationProcessor("org.projectlombok:lombok:1.18.38")

	testCompileOnly("org.projectlombok:lombok:1.18.38")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.38")

	// LocalDateTime 직렬화를 위함
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")

	// QueryDSL Implementation
	implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
	annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jakarta")
	annotationProcessor("jakarta.annotation:jakarta.annotation-api")
	annotationProcessor("jakarta.persistence:jakarta.persistence-api")

	// Redis
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	// Redisson
	implementation("org.redisson:redisson-spring-boot-starter:3.50.0")
	//Kafka
	implementation("org.springframework.kafka:spring-kafka")

	// Actuator (헬스체크, 메트릭 등)
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// Micrometer Prometheus 레지스트리
	runtimeOnly("io.micrometer:micrometer-registry-prometheus")
}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("user.timezone", "UTC")
}

/**
 * QueryDSL Build Options
 */
val querydslDir = "src/main/generated/querydsl"

sourceSets {
	getByName("main").java.srcDirs(querydslDir)
}

tasks.withType<JavaCompile> {
	options.generatedSourceOutputDirectory = file(querydslDir)

	// 위의 설정이 안되면 아래 설정 사용
	// options.generatedSourceOutputDirectory.set(file(querydslDir))
}

tasks.named("clean") {
	doLast {
		file(querydslDir).deleteRecursively()
	}
}

tasks.test {
	// 논리 코어 수의 절반을 계산하여 maxParallelForks에 할당
	maxParallelForks = Runtime.getRuntime().availableProcessors() / 2

	// JVM 당 메모리 설정 (테스트의 복잡성에 따라 조정)
	jvmArgs("-Xmx1g")
}