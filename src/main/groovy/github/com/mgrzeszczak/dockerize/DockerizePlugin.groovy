package github.com.mgrzeszczak.dockerize

import org.gradle.api.Plugin
import org.gradle.api.Project

import java.nio.file.Path

class DockerizePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create('dockerBuild', PluginContext.class)
        project.task('dockerize') {
            doFirst {
                PluginContext context = project.dockerBuild
                context.initialize(project)
                try {
                    ResourceLoader.cleanFiles()
                    dockerizeProject(project, context)
                } finally {
                    ResourceLoader.cleanFiles()
                }
            }
        }
    }

    private static void dockerizeProject(Project project, PluginContext context) {
        def jarPath = findJarPath(project.buildDir)
        def dockerfile = ResourceLoader.createDockerfile(context, jarPath)
        def startup = ResourceLoader.createStartupSh(context)
        def buildDocker = ResourceLoader.createBuildDockerSh(context)
        buildDockerImage(project, buildDocker)
    }

    private static void buildDockerImage(Project project, File buildDocker) {
        project.exec {
            commandLine "./${buildDocker.name}"
        }
    }

    private static Path findJarPath(File buildDir) {
        File libDir = new File(buildDir, 'libs')
        Arguments.check(libDir.exists(), "$libDir.path does not exist (did you build the project?)")
        def files = libDir.list().findAll { it.matches('.*\\.jar') }
        Arguments.check(files.size() != 0, "no jar files found in $libDir.path (did you build the project?)")
        Arguments.check(files.size() == 1, "more than one jar files found in $libDir.path")

        def jarFile = new File(libDir, files[0])
        return new File('.').toPath().relativize(jarFile.toPath())
    }

}
