FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/life4cut-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "life4cut-0.0.1-SNAPSHOT.jar"]