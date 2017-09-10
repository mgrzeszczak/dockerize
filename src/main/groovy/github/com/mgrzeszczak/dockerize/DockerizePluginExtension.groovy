package github.com.mgrzeszczak.dockerize

import org.gradle.api.Project


class DockerizePluginExtension {

    // required
    def String imageName
    def String imageVersion

    // optional
    def String vmMem = '-Xmx1024m -Xms256m'
    def String vmArgs = ''
    def String appArgs = ''
    def boolean debug = false
    def int debugPort = 5005
    def String javaDockerVersion = '8-jre-alpine'

    void initialize(Project project) {
        if (imageName == null) imageName = project.name
        if (imageVersion == null) imageVersion = project.version
    }


}
