FROM openjdk:8-jdk-alpine
ADD target/kinesisConsumer.jar kinesisConsumer.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/kinesisConsumer.jar"]