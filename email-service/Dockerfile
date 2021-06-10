FROM maven AS BuilderImage
WORKDIR /email-service-build
COPY pom.xml /email-service-build
COPY src /email-service-build/src
RUN mvn clean compile package

FROM openjdk:11-slim
WORKDIR /email-service
COPY --from=BuilderImage /email-service-build/target/email-service-0.0.1-SNAPSHOT.jar /email-service/app.jar

EXPOSE 8082

CMD ["java","-jar","./app.jar"]