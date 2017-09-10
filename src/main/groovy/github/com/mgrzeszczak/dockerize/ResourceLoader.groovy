package github.com.mgrzeszczak.dockerize

import java.nio.file.Path


class ResourceLoader {

    private final static String DOCKERFILE =
            'FROM java:%JAVA_VERSION%\n' +
            'COPY %JAR_PATH% /app.jar\n' +
            'COPY startup.sh /startup.sh\n' +
            'CMD ["/bin/sh", "startup.sh"]\n'

    private final static String BUILD_DOCKER =
            '#!/bin/sh\n'+
            'docker build . -t %IMAGE_NAME%:%IMAGE_VERSION%\n'

    private final static String STARTUP =
            '#!/bin/sh\n'+
            'java %VM_MEM% %DEBUG% -jar %VM_ARGS% /app.jar %APP_ARGS%\n'


    private static File load(String resource, String destination, Map<String, String> arguments) {
        arguments.forEach({ key, value -> resource = resource.replaceAll("%$key%", value) })
        def file = new File(destination)
        file.write(resource)
        return file
    }

    static File createDockerfile(PluginContext context, Path jarPath) {
        def arguments = [
                JAVA_VERSION: context.javaDockerVersion,
                JAR_PATH    : jarPath.toString()
        ]
        return load(DOCKERFILE, 'Dockerfile', arguments);
    }

    static File createStartupSh(PluginContext context) {
        def arguments = [
                VM_MEM  : context.vmMem,
                DEBUG   : context.debug ? getDebugArgument(context.debugPort) : '',
                VM_ARGS : context.vmArgs,
                APP_ARGS: context.appArgs
        ]
        def file = load(STARTUP, 'startup.sh', arguments);
        file.setExecutable(true)
        return file
    }

    static File createBuildDockerSh(PluginContext context) {
        def arguments = [
                IMAGE_NAME   : context.imageName,
                IMAGE_VERSION: context.imageVersion
        ]
        def file = load(BUILD_DOCKER, 'build-docker.sh', arguments)
        file.setExecutable(true)
        return file
    }

    private static String getDebugArgument(int port) {
        return "-agentlib:jdwp=transport=dt_socket,address=$port,server=y,suspend=y"
    }

    static void cleanFiles() {
        ['Dockerfile', 'startup.sh', 'build-docker.sh', 'app.jar']
                .collect { new File(it) }
                .findAll { it.exists() }
                .forEach({ it.delete() })
    }

}
