FROM eclipse-temurin:21-jdk
COPY target/learn_topic-0.0.1-SNAPSHOT.war app.war
ENTRYPOINT ["java", "-jar", "/app.war"]
