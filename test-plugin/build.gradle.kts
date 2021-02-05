dependencies {
    compileOnly("io.netty:netty-all:5.0.0.Alpha2")
    implementation(project(":menu"))
    implementation(project(":commons"))
    implementation(project(":config"))

    implementation("com.oop.orangeengine:item:5.3")
    implementation("com.oop.orangeengine:engine:5.3")
}