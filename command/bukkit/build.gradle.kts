repositories {
    maven{setUrl("https://libraries.minecraft.net")}
}

dependencies {
    implementation(project(":packet-injector"))
    implementation(project(":commons"))
    compileOnly(project(":command"))
    compileOnly(project(":platform"))
    compileOnly("com.mojang:brigadier:1.0.17")
}
