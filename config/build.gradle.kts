dependencies {
    implementation(project(":commons"))
    implementation("org.yaml:snakeyaml:1.27")
    implementation("com.google.code.gson:gson:2.8.6")
    compileOnly("org.apache.commons:commons-lang3:3.12.0")
    implementation(project(":config:config-node"))
}
