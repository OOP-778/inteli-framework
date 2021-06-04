dependencies {
    implementation(project(":command"))
    implementation(project(":bukkit:bukkit-command"))
    implementation(project(":commons"))
    implementation(project(":platform"))
    implementation(project(":dependency"))
    implementation(project(":dependency:dependency-common"))
    implementation(project(":task"))
    implementation(project(":bukkit:bukkit-task"))
    implementation(project(":event"))
    implementation(project(":bukkit:bukkit-event"))
    implementation(project(":adapters"))

    implementation(project(":bukkit:bukkit-entity:bukkit-entity-hologram"))
    implementation(project(":bukkit:bukkit-entity:bukkit-entity-tracker"))

    implementation("net.kyori:adventure-api:4.7.0")
    implementation("net.kyori:adventure-platform-bukkit:4.0.0-SNAPSHOT")
}
