FROM eclipse-temurin:21-jre
WORKDIR /application
COPY Boot/build/libs/mesh-service.jar /application/mesh-service.jar
USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/application/mesh-service.jar"]
