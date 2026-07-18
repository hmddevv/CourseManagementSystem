# ============================================================
# Multi-stage build: build bang Maven, chay bang JRE gon nhe.
# ============================================================

# ---- Stage 1: Build ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependency: copy pom truoc de tan dung layer cache
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copy ma nguon va dong goi (bo qua test de build nhanh trong image)
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- Stage 2: Runtime ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Chay bang user khong phai root cho an toan
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /app/target/course-management-system.jar app.jar
USER app

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod

# Healthcheck qua Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
