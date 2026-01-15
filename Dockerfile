FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:25-jre-alpine AS run

WORKDIR /app

# Step 3: Copy the Spring Boot JAR file into the container
COPY --from=build /app/target/*.jar app.jar

# Step 4: Expose the port your application runs on
EXPOSE 8080

# Step 5: Define the command to run your Spring Boot application
CMD ["java", "-jar", "/app/app.jar"]