# Etapa 1: Compila el JAR
FROM eclipse-temurin:22-jdk-alpine AS build

ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8

WORKDIR /app

COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests -Dfile.encoding=UTF-8

# Etapa 2: Ejecuta el JAR
FROM eclipse-temurin:22-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]