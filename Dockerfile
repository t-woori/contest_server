FROM amazoncorretto:17-alpine-jdk

ARG JAR_FILE
ARG SPRING_ACTIVE_PROFILE

ENV SPRING_ACTIVE_PROFILE_NAME=${SPRING_ACTIVE_PROFILE}
ENV DB_HOST=${DB_HOST}
ENV DB_PORT=${DB_PORT}
ENV DB_NAME=${DB_NAME}
ENV DB_USER=${DB_USER}
ENV DB_PASSWORD=${DB_PASSWORD}

ENV REDIS_HOST=${REDIS_HOST}
ENV REDIS_PORT=${REDIS_PORT}

WORKDIR /server

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_ACTIVE_PROFILE_NAME}","-jar","app.jar" ]

