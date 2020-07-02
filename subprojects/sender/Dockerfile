FROM adoptopenjdk:14-jre-openj9
COPY "build/app/sender.jar" /opt/sender.jar
CMD ["java", "--enable-preview", "-jar", "/opt/sender.jar"]
