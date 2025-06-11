# Use Java as the parent image
FROM eclipse-temurin:21
# Set the working directory inside the container
WORKDIR /app
# Use build argument for the JAR version
ARG JAR_VERSION
# Copy the jar file into the container using the JAR version argument
COPY build/libs/protoc-gen-kotlin-grpc-server-${JAR_VERSION}.jar /app/app.jar
# Expose the port your web app runs on
EXPOSE 2349
# Set the entry point for the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
