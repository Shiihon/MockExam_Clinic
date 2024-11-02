# Start with Amazon Corretto 17 Alpine base image
FROM amazoncorretto:17-alpine

# Install curl and PostgreSQL client on Alpine
RUN apk update && apk add --no-cache curl postgresql-client

# Copy the jar file into the image
COPY target/app.jar /app.jar

# Expose the port your app runs on
EXPOSE 7000

# Command to run your app
CMD ["java", "-jar", "/app.jar"]
