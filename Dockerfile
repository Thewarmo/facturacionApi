# Usa una imagen base de Java 21 (Eclipse Temurin es una opción recomendada y ligera)
FROM eclipse-temurin:21-jre-jammy

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR compilado de tu proyecto al contenedor
# La ruta del JAR puede variar dependiendo de tu configuración de build.
# Típicamente es target/<artifact-id>-<version>.jar
# Usamos un comodín (*) para evitar problemas con el número de versión.
COPY target/sistema-facturacion-*.jar app.jar

# Expone el puerto en el que la aplicación se ejecuta dentro del contenedor
EXPOSE 8089

# Comando para ejecutar la aplicación cuando el contenedor se inicie
ENTRYPOINT ["java", "-jar", "app.jar"]
