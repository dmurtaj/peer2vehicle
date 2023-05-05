FROM openjdk:17-jdk-slim

WORKDIR /usr/src/app

COPY . .

RUN sed -i 's/\r$//' mvnw
RUN chmod +x mvnw
RUN ./mvnw package

EXPOSE 8080
CMD ["java", "-jar", "/usr/src/app/target/peer2vehicle-0.0.1-SNAPSHOT.jar"]