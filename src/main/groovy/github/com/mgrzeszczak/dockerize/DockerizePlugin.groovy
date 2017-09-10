package github.com.mgrzeszczak.dockerize

import org.gradle.api.Plugin
import org.gradle.api.Project

class DockerizePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create('dockerBuild', DockerizePluginExtension.class)
        project.task('dockerize') {
            doFirst {
                DockerizePluginExtension context = project.dockerBuild
                checkArgument(context.imageName != null, "missing dockerBuild.imageName")
                checkArgument(context.imageVersion != null, "missing dockerBuild.imageVersion")

                def jarPath = findJar(project.buildDir)
                cleanFiles()
                createFiles(jarPath, context)
                buildDockerImage(project, context)
                cleanFiles()
            }
        }
    }

    private static void cleanFiles() {
        ['Dockerfile', 'startup.sh','build-docker.sh']
                .collect {new File(it)}
                .findAll {it.exists()}
                .forEach({it.delete()})
    }

    private static String getDockerfileContent() {
        return "FROM java:%JAVA_VERSION%\n" +
                "COPY %JAR_PATH% /app.jar\n" +
                "COPY startup.sh /startup.sh\n" +
                "CMD [\"/bin/sh\", \"startup.sh\"]"
    }

    private static String getStartupContent() {
        return "#!/bin/sh\n" +
                "java %VM_MEM% %DEBUG% -jar %VM_ARGS% /app.jar";
    }

    private static void createFiles(String jarPath, DockerizePluginExtension context) {
        def dockerfileContent = getDockerfileContent()
                .replaceAll("%JAVA_VERSION%", context.javaDockerVersion)
                .replaceAll("%JAR_PATH%", jarPath)

        def startupContent = getStartupContent()
                .replaceAll("%VM_ARGS%", context.vmArgs)
                .replaceAll("%VM_MEM%", context.vmMem)
                .replaceAll("%DEBUG%", context.debug ? getDebugCommand(context.debugPort) : '')

        def dockerfile = new File('Dockerfile')
        dockerfile.write(dockerfileContent)
        def startup = new File('startup.sh')
        startup.write(startupContent)
    }

    private static void buildDockerImage(Project project, DockerizePluginExtension context) {
        def command = "docker build . -t ${context.imageName}:${context.imageVersion}"
        def f = new File('build-docker.sh')
        f.write(command)
        f.setExecutable(true)

        project.exec {
            commandLine "./build-docker.sh"
        }

        println "Created docker image $context.imageName:$context.imageVersion\n"
        println "Example run command:\ndocker run $context.imageName:$context.imageVersion"

    }

    private static void checkArgument(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException(message)
        }
    }

    private static String findJar(File buildDir) {
        File libDir = new File(buildDir, 'libs')
        checkArgument(libDir.exists(), "$libDir.path does not exit (did you build the project?)")
        def files = libDir.list().findAll {it.matches('.*\\.jar')}
        checkArgument(files.size() != 0, "no jar files found in $libDir.path")
        checkArgument(files.size() == 1, "more than one jar files found in $libDir.path")
        return "build/libs/${files[0]}"
    }

    private static String getDebugCommand(int port) {
        return "-agentlib:jdwp=transport=dt_socket,address=$port,server=y,suspend=y"
    }

}
