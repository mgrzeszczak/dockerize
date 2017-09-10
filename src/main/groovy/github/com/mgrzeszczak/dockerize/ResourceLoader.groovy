package github.com.mgrzeszczak.dockerize

import java.nio.file.Path


class ResourceLoader {

    private static File load(String resourceName, String destination, Map<String, String> arguments) {
        def resourceContent = ClassLoader.getResourceAsStream(resourceName).text
        arguments.forEach({ key, value -> resourceContent = resourceContent.replaceAll("%$key%", value) })
        def file = new File(destination)
        file.write(resourceContent)
        return file
    }

    static File createDockerfile(PluginContext context, Path jarPath) {
        def arguments = [
                JAVA_VERSION: context.javaDockerVersion,
                JAR_PATH    : jarPath.text
        ]
        return load('Dockerfile', 'Dockerfile', arguments);
    }

    static File createStartupSh(PluginContext context) {
        def arguments = [
                VM_MEM  : context.vmMem,
                DEBUG   : context.debug ? getDebugArgument(context.debugPort) : '',
                VM_ARGS : context.vmArgs,
                APP_ARGS: context.appArgs
        ]
        def file = load('startup.sh', 'startup.sh', arguments);
        file.setExecutable(true)
        return file
    }

    static File createBuildDockerSh(PluginContext context) {
        def arguments = [
                IMAGE_NAME   : context.imageName,
                IMAGE_VERSION: context.imageVersion
        ]
        def file = load('build-docker.sh', 'build-docker.sh', arguments)
        file.setExecutable(true)
        return file
    }

    private static String getDebugArgument(int port) {
        return "-agentlib:jdwp=transport=dt_socket,address=$port,server=y,suspend=y"
    }

    static void cleanFiles() {
        ['Dockerfile', 'startup.sh', 'build-docker.sh']
                .collect { new File(it) }
                .findAll { it.exists() }
                .forEach({ it.delete() })
    }

}
