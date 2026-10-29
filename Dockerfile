# Builds frontend and backend into one jar. Gradle downloads the Node it
# needs, so the build stage only needs a JDK.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/embeddr-*.jar embeddr.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "embeddr.jar"]
