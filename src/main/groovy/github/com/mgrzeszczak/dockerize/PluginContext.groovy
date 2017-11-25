package github.com.mgrzeszczak.dockerize

import org.gradle.api.Project


class PluginContext {

    String jarNamePattern = ".*\\.jar"
    String imageName
    String imageVersion
    String vmMem = '-Xmx1024m -Xms256m'
    String vmArgs = ''
    String appArgs = ''
    boolean debug = false
    int debugPort = 5005
    String javaDockerVersion = '8-jre-alpine'

    void initialize(Project project) {
        if (imageName == null) imageName = project.name
        if (imageVersion == null) imageVersion = project.version
    }


}
