# Etapa de construcción
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copia el JAR compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Copia la wallet Oracle (desde src/main/resources en tu proyecto)
COPY src/main/resources/Wallet_WV80UNLYMHJZX3G3 /app/wallet

# Define la variable de entorno TNS_ADMIN para que Oracle la use
ENV TNS_ADMIN=/app/wallet

# Ejecuta la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
