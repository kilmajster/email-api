FROM amd64/openjdk:14-buster
COPY "build/app/email-api-1.0.0-SNAPSHOT.jar" /opt/email-api.jar
CMD ["java", "--enable-preview", "-jar", "/opt/email-api.jar"]
