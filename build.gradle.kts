plugins {
	java
	id("org.springframework.boot") version "3.0.0"
	id("io.spring.dependency-management") version "1.1.0"
}

group = "no.dnb.r2dbc.test"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	implementation("io.r2dbc:r2dbc-mssql:1.0.0.RELEASE")
	implementation("io.projectreactor:reactor-tools")

	// Flyway (flyway needs JDBC driver)
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-sqlserver")
	implementation("com.microsoft.sqlserver:mssql-jdbc")
	implementation("p6spy:p6spy:3.9.1")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
