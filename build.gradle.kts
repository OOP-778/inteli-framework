
import java.io.FileOutputStream

import java.io.BufferedInputStream
import java.net.URL
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
    publish = true
    version = "0.1"
}

configureProject("hologram") {
    needMc = true
    needNMS = true
    publish = true
    version = "0.1"
}

configureProject("message") {
    needMc = true
    needNMS = false
    publish = true
    version = "0.1"
}

configureProject("scoreboard") {
    needMc = true
    needNMS = true
    publish = true
    version = "0.1"
}

configureProject("menu") {
    needMc = true
    publish = true
    version = "0.1"
}

configureProject("command-bukkit") {
    needMc = true
    publish = true
    version = "0.1"
}

configureProject("item") {
    needMc = true
    publish = true
    version = "0.1"
}

configureProject("adapters") {
    needMc = true
    version = "0.1"
}

configureProject("test-plugin") {
    needMc = true
    needNMS = true
    out = "out"
}

configureProject("commons") {
    needMc = true
    publish = true
    version = "0.1"
}

configureProject("packet-injector") {
    needMc = true
    publish = true
    version = "0.1"
}

configureProject("recipe") {
    needMc = true
}

subprojects {
    repositories {
        jcenter()
        maven { setUrl("https://repo.codemc.org/repository/nms/") }
        maven { setUrl("https://repo.codemc.org/repository/maven-public/") }
        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots") }
        mavenLocal()
    }

    val config = props[name]

    dependencies {
        config?.let {
            if (it.needMc) {
                compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
                implementation("org.apache.commons:commons-lang3:3.11")
            }

            if (it.needNMS)
                compileOnly(fileTree("../lib/"))
        }

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
    var needNMS: Boolean = false
) {
    constructor(project: String, version: Any) : this(
        name = project, version = version
    )
}
