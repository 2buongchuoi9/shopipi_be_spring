FROM maven:3.8.4-openjdk-17-slim as build-stage

WORKDIR /app
# copy file pom.xml và mvnw vào thư mục app
COPY . .
RUN mvn package -f pom.xml -DskipTests


FROM openjdk:17-slim as production-stage
WORKDIR /app 
COPY --from=build-stage /app/target/shopipi_be_spring-0.0.1-SNAPSHOT.jar app.jar

CMD [ "java", "-jar", "app.jar" ]