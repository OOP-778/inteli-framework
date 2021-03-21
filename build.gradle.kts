plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
    `maven-publish`
}

version = "1.0"

subprojects {
    apply {
        plugin("java")
        plugin("com.github.johnrengelman.shadow")
        plugin("maven-publish")
    }
}

var props: MutableMap<String, ProjectConfig> = hashMapOf()
loadProjects()

configureProject("config") {
    needMc = true
}

configureProject("hologram") {
    needMc = true
    needNMS = true
}

configureProject("menu") {
    needMc = true
    publish = true
    version = "1.0"
}

configureProject("scoreboard") {
    needMc = true
    needNMS = true
}

configureProject("adapters") {
    needMc = true
}

configureProject("test-plugin") {
    needMc = true
    needNbtApi = true
    needNMS = true
    out = "/run/media/oop-778/BRABARAR/Serrvers/OOP/1.8.8/plugins/"
}

configureProject("commons") {
    needMc = true
}

configureProject("recipe") {
    needMc = true
}

configureProject("item") {
    needMc = true
    needNbtApi = true
    mcVersion = MCVersion.V1_16
    version = 1.0
    publish = true
}

configureProject("task") {
    needMc = true
    needUnitTesting = true
    needPlatform = true
}

configureProject("plugin") {
    needMc = true
    needUnitTesting = true
}

configureProject("command") {
    needMc = true
}

configureProject("command-bukkit") {
    needMc = true
}

configureProject("message") {
    needMc = true
}

configureProject("packet-injector") {
    needMc = true
}

subprojects {
    repositories {
        jcenter()
        mavenCentral()
        maven { setUrl("https://repo.codemc.org/repository/nms/") }
        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots") }
        maven { setUrl("https://repo.codemc.org/repository/maven-public/") }
    }

    val config = props[name]

    dependencies {
        config?.let {
            if (it.needMc) {
                compileOnly("org.spigotmc:spigot-api:${it.mcVersion.versionName}")
                implementation("org.apache.commons:commons-lang3:3.11")
            }

            if (it.needNbtApi)
                implementation("de.tr7zw:item-nbt-api:2.7.1")

            if (it.needUnitTesting)
                testImplementation("junit:junit:4.13")

            if (it.needNMS)
                compileOnly(fileTree("../lib/"))

            if (it.needPlatform)
                compileOnly(project(":platform"))
        }

        implementation("org.jetbrains:annotations:20.1.0")
        compileOnly("org.projectlombok:lombok:1.18.8")
        annotationProcessor("org.projectlombok:lombok:1.18.8")
    }

    tasks {
        register("cleanOut") {
            val directory = File("$projectDir/out/")
            if (directory.exists())
                directory.delete()
        }

        config?.let {
            shadowJar {
                archiveFileName.set("${it.name}.jar")
                destinationDirectory.set(file(it.out))
            }
        }

        build {
            dependsOn(findByName("cleanOut"))
            config?.let {
                if (it.publish) {
                    dependsOn(shadowJar)
                    dependsOn(publish)
                }
            }
        }

        config?.let {
            if (it.publish) {
                shadowJar {
                    finalizedBy(publish)
                }
            }
        }
    }

    props[name]?.let { pc ->
        if (pc.publish) {
            publishing {
                repositories {
                    mavenLocal()
                    if (project.hasProperty("mavenUsername")) {
                        maven {
                            credentials {
                                username = project.property("mavenUsername") as String
                                password = project.property("mavenPassword") as String
                            }

                            setUrl("https://repo.codemc.org/repository/maven-releases/")
                        }
                    }
                }

                publications {
                    register("mavenJava", MavenPublication::class) {
                        artifact(file("out/${pc.name}.jar"))

                        groupId = pc.group
                        artifactId = pc.artifact.replace("-module", "")
                        version = pc.version.toString()
                    }
                }
            }
        }
    }
}

tasks {
    register("generate-javadocs", Javadoc::class) {
        setDestinationDir(file("$buildDir/docs"))
        title = "$project.name $version API"

        subprojects.forEach { proj ->
            proj.tasks.withType<Javadoc>().forEach { javadocTask ->
                source += javadocTask.source
                classpath += javadocTask.classpath
                excludes += javadocTask.excludes
                includes += javadocTask.includes
            }
        }
    }
}

// << UTILS START >>
fun loadProjects() {
    for (children in childProjects.values)
        props[children.name.toLowerCase()] = ProjectConfig(children.name, version)
}

fun configureProject(name: String, apply: (ProjectConfig).() -> Unit) {
    props[name.toLowerCase()]?.let(apply)
}

data class ProjectConfig(
        val name: String,
        var outName: String = name,
        var publish: Boolean = false,
        var group: String = "com.oop.inteliframework",
        var artifact: String = name,
        var out: String = "out",
        var version: Any,
        var needMc: Boolean = false,
        var mcVersion: MCVersion = MCVersion.V1_8,
        var needNMS: Boolean = false,
        var needNbtApi: Boolean = false,
        var needUnitTesting: Boolean = false,
        var needPlatform: Boolean = false
) {
    constructor(project: String, version: Any) : this(
            name = project, version = version
    )
}

enum class MCVersion(val versionName: String) {
    V1_8("1.8.8-R0.1-SNAPSHOT"),
    V1_12("1.12.2-R0.1-SNAPSHOT"),
    V1_13("1.13.2-R0.1-SNAPSHOT"),
    V1_14("1.14.4-R0.1-SNAPSHOT"),
    V1_15("1.15.2-R0.1-SNAPSHOT"),
    V1_16("1.16.5-R0.1-SNAPSHOT")
}
