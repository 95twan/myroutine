FROM eclipse-temurin:17-jre
WORKDIR /app
COPY build/libs/*.jar app.jar
ARG PROFILE=prod
ENV SPRING_PROFILES_ACTIVE=${PROFILE}
ENTRYPOINT ["java","-jar","/app/app.jar"]
