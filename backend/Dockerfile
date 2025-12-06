FROM maven:3-alpine AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src .
RUN mvn clean package -DskipTest


FROM openjdk:17-jre-slim AS runtime
WORKDIR /app   
COPY --from=build /build/target/dechet.1.0.0.jar /app/dechet.1.0.0.jar 
EXPOSE 8082
CMD ["java","-jar","/app/dechet.1.0.0.jar"]