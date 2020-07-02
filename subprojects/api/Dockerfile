FROM adoptopenjdk:14-jre-openj9
COPY "build/app/api.jar" /opt/api.jar
CMD ["java", "--enable-preview", "-jar", "/opt/api.jar"]
