
FROM openjdk:8-jre-alpine
ADD /target/*.jar /app.jar
EXPOSE 8876
ENV JAVA_OPTS=""
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]