# Stage 1: Build
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Instalar Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Cachear dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:25-jre

# Seguridad: usuario non-root
RUN groupadd -r app && useradd -r -g app app

WORKDIR /app

# Copiar JAR
COPY --from=builder /app/target/notification-library-*.jar app.jar

# JVM container settings
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+ExitOnOutOfMemoryError \
    -XX:+UseG1GC"

USER app

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
