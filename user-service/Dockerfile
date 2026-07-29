FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

COPY pom.xml .
COPY common-enum/pom.xml ./common-enum/
COPY common-api/pom.xml ./common-api/
COPY user-service/pom.xml ./user-service/
COPY calculation-service/pom.xml ./calculation-service/
COPY decision-service/pom.xml ./decision-service/

RUN mvn dependency:go-offline -B

COPY common-enum/src ./common-enum/src
COPY common-api/src ./common-api/src
COPY user-service/src ./user-service/src

RUN mvn -pl common-enum,common-api,user-service clean install -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/user-service/target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]