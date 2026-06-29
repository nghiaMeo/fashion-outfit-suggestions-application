FROM gradle:8.10-jdk21 AS build
WORKDIR /app

COPY build.gradle settings.gradle ./
COPY src src

RUN gradle --no-daemon clean bootJar

FROM eclipse-temurin:21-jre
WORKDIR /app

ENV JAVA_OPTS=""
ENV PORT=8080

COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
