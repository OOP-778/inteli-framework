dependencies {
    implementation(project(":commons"))
    compileOnly(project(":config:config-node"))
    compileOnly(project(":config"))
    compileOnly(project(":platform"))
    compileOnly("org.apache.commons:commons-lang3:3.12.0")
    compileOnly("com.google.guava:guava:30.1-jre")
}
