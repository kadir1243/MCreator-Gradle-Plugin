plugins {
    java
    idea
    `java-gradle-plugin`
    `maven-publish`
}

group = "io.github.kadir1243"
version = "${project.property("version")!!}"

base {
    archivesName.set("mcreatorPlugin")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())

    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    compileOnly("org.jetbrains:annotations:23.0.0")
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("mcreatorPlugin") {
            id = "mcreatorPlugin"
            implementationClass = "io.github.kadir1243.mcreatorPlugin.MainPlugin"
        }
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "io.github.kadir1243"
            artifactId = base.archivesName.get()
            version = project.property("version").toString()

            from(components["java"])
        }
    }
    repositories {
        if (System.getProperty("publishURL", null) != null) {
            maven(System.getProperty("publishURL")) {
                credentials {
                    name = System.getProperty("mavenName")
                    password = System.getProperty("mavenPassword")
                }
            }
        }
    }
}
