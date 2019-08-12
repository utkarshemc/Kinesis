FROM openjdk:8-jdk-alpine
ADD target/KinesisConsumer.jar KinesisConsumer.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/KinesisConsumer.jar"]