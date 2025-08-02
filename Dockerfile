# --- Etapa 1: Construcción (Build) ---
# Usamos una imagen de Maven con Java 21 para compilar el proyecto
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos el pom.xml y descargamos las dependencias primero para aprovechar el cache de Docker
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el resto del código fuente
COPY src ./src

# Compilamos la aplicación y creamos el .jar, saltando los tests
RUN mvn package -DskipTests

# --- Etapa 2: Ejecución (Run) ---
# Usamos una imagen JRE (Java Runtime Environment) ligera para ejecutar la app
FROM eclipse-temurin:21-jre-jammy

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos el archivo .jar que se creó en la etapa de 'build'
COPY --from=build /app/target/sistema-facturacion-*.jar app.jar

# Exponemos el puerto en el que corre la aplicación
EXPOSE 8089

# El comando para iniciar la aplicación
ENTRYPOINT ["java","-jar","app.jar"]