# Step 1: Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy the jar file produced by the Maven build
# Assuming your built jar file will be located in /target folder (after Maven build)
COPY target/SWKOM_DMS-0.0.1-SNAPSHOT.jar /app/app.jar

# Step 4: Expose the port on which the Spring Boot application will run
EXPOSE 8081

# Step 5: Define the command to run your application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]