FROM openjdk:8-jdk-alpine
ADD target/KinesisProducer.jar KinesisProducer.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/KinesisProducer.jar"]