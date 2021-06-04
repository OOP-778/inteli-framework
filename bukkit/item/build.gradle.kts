dependencies {
    implementation("org.jetbrains:annotations:20.1.0")
    implementation(project(":commons"))
    implementation(project(":bukkit:bukkit-nbt"))
    compileOnly(project(":adapters"))
}
