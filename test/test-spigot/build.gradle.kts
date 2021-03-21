dependencies {
    compileOnly("io.netty:netty-all:5.0.0.Alpha2")
    implementation(project(":menu"))
    implementation(project(":commons"))
    implementation(project(":config"))
    implementation(project(":command-bukkit"))
    implementation(project(":command"))
    implementation(project(":item"))
    implementation(project(":task"))
}
