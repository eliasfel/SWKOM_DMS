# Step 1: Use an official OpenJDK runtime as the base image
FROM openjdk:17-jdk-alpine

# Step 2: Install Tesseract OCR
RUN apk add --no-cache tesseract-ocr

# Step 3: Set the working directory in the container
WORKDIR /app

# Step 4: Copy the compiled JAR file (ensure the path matches your build setup)
COPY target/worker-0.0.1-SNAPSHOT.jar /app/worker.jar

# Step 5: Expose the port (if needed)
EXPOSE 8082

# Step 6: Set the command to run your application
ENTRYPOINT ["java", "-jar", "/app/worker.jar"]
