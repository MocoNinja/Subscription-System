FROM maven AS BuilderImage
WORKDIR /backend-service-build
COPY pom.xml /backend-service-build
COPY src /backend-service-build/src
RUN mvn clean compile package

FROM openjdk:11-slim
WORKDIR /backend-service
COPY --from=BuilderImage /backend-service-build/target/backend-service-0.0.1-SNAPSHOT.jar /backend-service/app.jar
COPY --from=BuilderImage /backend-service-build/src/main/resources/access_tokens.json /backend-service/access_tokens.json
ENV ACCESS_TOKENS_PATH=/backend-service/access_tokens.json

EXPOSE 8080

CMD ["java","-jar","./app.jar"]