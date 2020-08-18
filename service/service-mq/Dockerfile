FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY ./target/service-mq.jar service-mq.jar
ENTRYPOINT ["java","-jar","/service-mq", "&"]