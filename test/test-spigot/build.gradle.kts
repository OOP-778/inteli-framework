dependencies {
    implementation(project(":platform"))
    implementation(project(":commons"))
    implementation(project(":config"))
    implementation(project(":config:config-node"))
    implementation(project(":command:command-bukkit"))
    implementation(project(":command"))
    implementation(project(":item"))
    implementation(project(":task"))
    implementation(project(":dependency"))
    implementation(project(":dependency:dependency-common"))

    implementation(project(":hologram"))
    implementation(project(":hologram:hologram-animation"))
    implementation(project(":animation"))

    implementation(project(":menu"))
    implementation(project(":menu:menu-config"))

    implementation(project(":message"))
    implementation("net.kyori:adventure-api:4.7.0")
    implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")
    implementation("net.kyori:adventure-platform-bukkit:4.0.0-SNAPSHOT")
}
