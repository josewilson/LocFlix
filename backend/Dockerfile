# syntax=docker/dockerfile:1

# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Cache de dependências: copia só o pom primeiro
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Compila e empacota (sem rodar testes para acelerar a imagem)
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# ffmpeg é necessário para a conversão HLS (chamado via ProcessBuilder);
# curl é usado pelo health check do container.
RUN apt-get update \
    && apt-get install -y --no-install-recommends ffmpeg curl \
    && rm -rf /var/lib/apt/lists/*

# Diretório de armazenamento dos vídeos (montado como volume no compose)
RUN mkdir -p /app/storage/videos

COPY --from=build /build/target/*.jar app.jar

ENV SPRING_PROFILE=docker \
    LOCFLIX_VIDEOS_PATH=/app/storage/videos \
    JAVA_OPTS=""

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
