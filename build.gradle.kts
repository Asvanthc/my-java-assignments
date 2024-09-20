plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


dependencies {
    implementation ("javax.xml.bind:jaxb-api:2.3.1")
    implementation ("org.glassfish.jaxb:jaxb-runtime:2.3.1")
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("org.apache.logging.log4j:log4j-core:3.0.0-beta2")
    implementation("org.apache.logging.log4j:log4j-api:3.0.0-beta2")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.13.0")
}

tasks.test {
    useJUnitPlatform()
}