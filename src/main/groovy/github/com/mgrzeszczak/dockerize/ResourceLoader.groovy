package github.com.mgrzeszczak.dockerize

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


    private static File load(String resource, File destination, Map<String, String> arguments) {
        arguments.forEach({ key, value -> resource = resource.replaceAll("%$key%", value) })
        destination.write(resource)
        return destination
    }

    static File createDockerfile(File rootDir, PluginContext context) {
        def arguments = [
                JAVA_VERSION: context.javaDockerVersion,
                JAR_PATH    : 'app.jar'
        ]
        return load(DOCKERFILE, new File(rootDir, 'Dockerfile'), arguments)
    }

    static File createStartupSh(File rootDir, PluginContext context) {
        def arguments = [
                VM_MEM  : context.vmMem,
                DEBUG   : context.debug ? getDebugArgument(context.debugPort) : '',
                VM_ARGS : context.vmArgs,
                APP_ARGS: context.appArgs
        ]
        def file = load(STARTUP, new File(rootDir, 'startup.sh'), arguments)
        file.setExecutable(true)
        return file
    }

    static File createBuildDockerSh(File rootDir, PluginContext context) {
        def arguments = [
                IMAGE_NAME   : context.imageName,
                IMAGE_VERSION: context.imageVersion
        ]
        def file = load(BUILD_DOCKER, new File(rootDir, 'build-docker.sh'), arguments)
        file.setExecutable(true)
        return file
    }

    private static String getDebugArgument(int port) {
        return "-agentlib:jdwp=transport=dt_socket,address=$port,server=y,suspend=y"
    }

    static void cleanFiles(File rootDir) {
        ['Dockerfile', 'startup.sh', 'build-docker.sh', 'app.jar']
                .collect { new File(rootDir, it) }
                .findAll { it.exists() }
                .forEach({ it.delete() })
    }

}
