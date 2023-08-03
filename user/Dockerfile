FROM openjdk:17
ADD target/user-0.0.1-SNAPSHOT.jar app.jar
#ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
ENTRYPOINT ["java", "-jar", "app.jar"]