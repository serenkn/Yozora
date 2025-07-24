FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src
RUN ./mvnw clean package -DskipTests
CMD ["java", "-jar", "target/yozora-0.0.1-SNAPSHOT.jar"]
