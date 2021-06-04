dependencies {
    compileOnly(project(":commons"))
    compileOnly(project(":platform"))
    implementation(project(":bukkit:bukkit-entity:bukkit-entity-commons"))
    implementation(project(":bukkit:bukkit-entity:bukkit-entity-armorstand"))
    implementation(project(":bukkit:bukkit-entity:bukkit-entity-tracker"))
    compileOnly(project(":adapters"))
    compileOnly(project(":task"))
}
