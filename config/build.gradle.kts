dependencies {
    implementation(project(":commons"))
    compileOnly(project(":platform"))
    compile("org.yaml:snakeyaml:1.27")
    compileOnly("org.jetbrains:annotations:20.0.0")
    implementation("com.google.code.gson:gson:2.8.6")
}
