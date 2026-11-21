# syntax=docker/dockerfile:1
# ---------- Etapa 1: build (Maven + JDK 17) ----------
# El contexto de build es la RAIZ del repo (ver docker-compose.yml), porque el
# servicio depende del modulo compartido common-contracts.
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /workspace

# 1) Solo los pom.xml -> capa de dependencias cacheable
COPY pom.xml .
COPY common-contracts/pom.xml common-contracts/
COPY transaction-api/pom.xml transaction-api/
COPY transaction-processor/pom.xml transaction-processor/
COPY fraud-detector/pom.xml fraud-detector/
COPY dashboard-aggregator/pom.xml dashboard-aggregator/
COPY notification-router/pom.xml notification-router/
COPY ad-generator/pom.xml ad-generator/
COPY email-worker/pom.xml email-worker/
RUN mvn dependency:go-offline -B -pl dashboard-aggregator -am -Dexclude=com.banco.ledger:common-contracts

# 2) Codigo fuente y empaquetado (sin tests para agilizar CI/CD)
COPY common-contracts/src common-contracts/src
COPY dashboard-aggregator/src dashboard-aggregator/src
RUN mvn clean package -DskipTests -B -pl dashboard-aggregator -am

# ---------- Etapa 2: runtime (JRE Alpine, usuario no privilegiado) ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build --chown=appuser:appgroup /workspace/dashboard-aggregator/target/app.jar app.jar
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"
USER appuser
EXPOSE 8081
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
