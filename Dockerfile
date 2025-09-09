    FROM maven:3.9.6-eclipse-temurin-21-alpine

    WORKDIR /app

    COPY pom.xml .
    RUN mvn dependency:go-offline

    COPY src ./src

    RUN mvn clean package -DskipTests

    CMD ["java", "-jar", "target/masterit-0.0.1-SNAPSHOT.jar"]

