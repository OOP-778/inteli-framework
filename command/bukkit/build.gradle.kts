repositories {
    maven{setUrl("https://libraries.minecraft.net")}
}

dependencies {
    implementation(project(":packet-injector"))
    implementation(project(":commons"))
    implementation(project(":command:command-api"))
    compileOnly("com.mojang:brigadier:1.0.17")
}
