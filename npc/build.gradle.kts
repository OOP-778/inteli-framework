dependencies {
    compileOnly(project(":platform"))
    compileOnly(project(":commons"))
    compileOnly(project(":event"))
    compileOnly(fileTree("util"))
    compileOnly(project(":task"))
    implementation(project(":packet-injector"))
}
