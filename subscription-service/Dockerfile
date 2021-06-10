FROM maven AS BuilderImage
WORKDIR /subscription-service-build
COPY pom.xml /subscription-service-build
COPY src /subscription-service-build/src
RUN mvn clean compile package

FROM openjdk:11-slim
WORKDIR /subscription-service
COPY --from=BuilderImage /subscription-service-build/target/subscription-service-0.0.1-SNAPSHOT.jar /subscription-service/app.jar
COPY --from=BuilderImage /subscription-service-build/src/main/resources/access_tokens.json /subscription-service/access_tokens.json
ENV ACCESS_TOKENS_PATH=/subscription-service/access_tokens.json

EXPOSE 8081

CMD ["java","-jar","./app.jar"]