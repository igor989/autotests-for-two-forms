plugins {
    id("java")
}

group = "protei_qa_test_webpage"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.seleniumhq.selenium:selenium-java:4.25.0")
    implementation("io.github.bonigarcia:webdrivermanager:5.9.2")
    testImplementation("org.testng:testng:7.10.2")
}

tasks.test {
    useTestNG()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}