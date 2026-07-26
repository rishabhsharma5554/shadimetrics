# ---- Build stage ----
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Cache dependencies separately from source so `docker build` reuses this layer
# whenever only application code changes.
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew --no-daemon --version

COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test

# ---- Runtime stage ----
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

RUN useradd --create-home --shell /bin/bash appuser \
    && mkdir -p /app/data /app/uploads \
    && chown -R appuser:appuser /app

COPY --from=build /app/build/libs/*.jar app.jar

USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
