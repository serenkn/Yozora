FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw package -DskipTests
CMD ["java", "-jar", "target/yozora-0.0.1-SNAPSHOT.jar"]
