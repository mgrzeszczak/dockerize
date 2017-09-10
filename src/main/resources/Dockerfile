FROM java:%JAVA_VERSION%
COPY %JAR_PATH% /app.jar
COPY startup.sh /startup.sh
CMD ["/bin/sh", "startup.sh"]