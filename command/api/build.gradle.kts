repositories {
    maven {
        setUrl("https://mvnrepository.com/artifact/org.apache.commons/commons-lang3")
    }
}

dependencies {
    compileOnly(project(":commons"))
    compileOnly("com.google.guava:guava:30.1-jre")
    implementation("org.apache.commons:commons-lang3:3.11")
}
