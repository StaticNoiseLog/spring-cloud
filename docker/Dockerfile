FROM openjdk:8-jre-alpine

COPY ./build/libs/spring-cloud-0.0.1-SNAPSHOT.jar /spring-cloud.jar

ENV APP_TITLE='No APP_TITLE set on deployment'

CMD ["/usr/bin/java", "-jar", "/spring-cloud.jar"]