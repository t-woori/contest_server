FROM amazoncorretto:17-alpine-jdk

ARG JAR_FILE
ARG SPRING_ACTIVE_PROFILE

ENV SPRING_ACTIVE_PROFILE_NAME=${SPRING_ACTIVE_PROFILE}

WORKDIR /server

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_ACTIVE_PROFILE_NAME}","-jar","app.jar" ]

