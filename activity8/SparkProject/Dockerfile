# Use a base image with Java (Spark requires Java)
FROM spark:3.5.5-scala2.12-java11-ubuntu

# Copy the Spark application JAR file to the container
COPY jars/TestUserMinio-assembly-1.0.jar /app.jar

# Set the default command to run the Spark application
CMD ["spark-submit", "/app.jar"]

# admin xrJTAl7mWL