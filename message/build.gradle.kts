dependencies {
    compileOnly(project(":commons"))
    compileOnly(project(":platform"))
    compileOnly(project(":task"))

    implementation("net.kyori:adventure-api:4.7.0")
    implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")
    implementation("net.kyori:adventure-platform-bungeecord:4.0.0-SNAPSHOT")
    implementation("net.kyori:adventure-platform-bukkit:4.0.0-SNAPSHOT")
}
