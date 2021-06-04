dependencies {
    compileOnly(project(":commons"))
    compileOnly(project(":message"))
    compileOnly(project(":platform"))
    compileOnly(project(":config:config-node"))
    compileOnly("com.google.guava:guava:30.1-jre")
    implementation("net.kyori:adventure-api:4.7.0")
}
