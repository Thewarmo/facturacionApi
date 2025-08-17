# --- Etapa 1: Construcción (Build) ---
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos el pom.xml y descargamos las dependencias primero para aprovechar el cache de Docker
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el resto del código fuente
COPY src ./src

# Compilamos la aplicación optimizada para producción
RUN mvn package -DskipTests \
    -Dspring-boot.build-image.skip=true \
    -Dmaven.javadoc.skip=true \
    -Dmaven.source.skip=true \
    -Dproject.build.sourceEncoding=UTF-8 \
    -Dproject.reporting.outputEncoding=UTF-8

# --- Etapa 2: Extracción de capas (para mejor caching) ---
FROM eclipse-temurin:21-jre-jammy AS layers
WORKDIR /app
COPY --from=build /app/target/sistema-facturacion-*.jar app.jar
# Extraemos las capas del JAR para mejor caching
RUN java -Djarmode=layertools -jar app.jar extract

# --- Etapa 3: Ejecución (Run) ---
FROM eclipse-temurin:21-jre-jammy

# Instalamos curl para health checks
RUN apt-get update && apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/* && apt-get clean

# Creamos un usuario no-root por seguridad
RUN groupadd -r appuser && useradd --no-log-init -r -g appuser appuser

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos las capas extraídas en orden de menos a más probable de cambiar
COPY --from=layers --chown=appuser:appuser /app/dependencies/ ./
COPY --from=layers --chown=appuser:appuser /app/spring-boot-loader/ ./
COPY --from=layers --chown=appuser:appuser /app/snapshot-dependencies/ ./
COPY --from=layers --chown=appuser:appuser /app/application/ ./

# Cambiamos al usuario no-root
USER appuser

# Exponemos el puerto (usa PORT de Render, defaultea a 8080)
EXPOSE ${PORT:-8080}

# Variables de entorno optimizadas para Render
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=70.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -XX:+OptimizeStringConcat \
               -Djava.awt.headless=true \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.profiles.active=prod"

# Comando optimizado para Render
ENTRYPOINT exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher