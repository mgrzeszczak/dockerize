# dockerize
Gradle plugin creating a docker image for your java application.

# [Installation](https://plugins.gradle.org/plugin/github.com.mgrzeszczak.dockerize)

# How to use

In projects __build.gradle__ add configuration (every parameter is optional):

```
dockerBuild {
     jarNamePattern = '.*\\.jar' // only this jar will be copied to docker image and run on start
     imageName = ''  // defaults to project.name
     imageVersion ='' // defaults to project.version
     vmMem = '-Xmx1024m -Xms256m'
     vmArgs = ''
     appArgs = ''
     debug = false // debug suspends application
     debugPort = 5005
     javaDockerVersion = '8-jre-alpine'
}
```
Now run 'dockerize' task `gradle dockerize` to create your docker image.

# License
```
Copyright (c) 2017 Maciej Grzeszczak

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
