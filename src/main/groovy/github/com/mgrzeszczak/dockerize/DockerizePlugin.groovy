package github.com.mgrzeszczak.dockerize

import org.gradle.api.Plugin
import org.gradle.api.Project

import java.nio.file.Files

class DockerizePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create('dockerBuild', PluginContext.class)
        project.task('dockerize') {
            doFirst {
                PluginContext context = project.dockerBuild
                context.initialize(project)
                File root = project.rootDir
                try {
                    ResourceLoader.cleanFiles(root)
                    dockerizeProject(project, context)
                } finally {
                    ResourceLoader.cleanFiles(root)
                }
            }
        }
    }

    private static void dockerizeProject(Project project, PluginContext context) {
        def root = project.rootDir
        copyJar(project.buildDir, root)
        def dockerfile = ResourceLoader.createDockerfile(root, context)
        def startup = ResourceLoader.createStartupSh(root, context)
        def buildDocker = ResourceLoader.createBuildDockerSh(root, context)
        buildDockerImage(project)
    }

    private static void buildDockerImage(Project project) {
        project.exec {
            commandLine "./build-docker.sh"
            workingDir project.rootDir
        }
    }

    private static void copyJar(File buildDir, File rootDir) {
        File libDir = new File(buildDir, 'libs')
        Arguments.check(libDir.exists(), "$libDir.path does not exist (did you build the project?)")
        def files = libDir.list().findAll { it.matches('.*\\.jar') }
        Arguments.check(files.size() != 0, "no jar files found in $libDir.path (did you build the project?)")
        Arguments.check(files.size() == 1, "more than one jar files found in $libDir.path")

        def jarFile = new File(libDir, files[0])

        def path = new File(rootDir, 'app.jar').toPath()
        Files.copy(jarFile.toPath(), path)
    }

}
