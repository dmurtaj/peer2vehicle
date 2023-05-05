FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y curl \
    && curl -sL https://deb.nodesource.com/setup_18.x | bash - \
    && apt-get install -y nodejs \
    && curl -L https://www.npmjs.com/install.sh | npm_install="8.19.2" | sh

WORKDIR /usr/src/app

COPY . .

RUN cd frontend && npm install
RUN cd frontend && npm run build
RUN rm -r frontend

RUN sed -i 's/\r$//' mvnw
RUN chmod +x mvnw
RUN ./mvnw package

EXPOSE 8080
CMD ["java", "-jar", "/usr/src/app/target/peer2vehicle-0.0.1-SNAPSHOT.jar"]