repositories {
    maven { setUrl("https://libraries.minecraft.net") }
}

dependencies {
    implementation(project(":bukkit:bukkit-packet"))
    implementation(project(":commons"))
    compileOnly(project(":command"))
    compileOnly(project(":platform"))
    compileOnly(project(":event"))
    compileOnly("com.mojang:brigadier:1.0.17")
}
