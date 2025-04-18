
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Копирование файлов pom.xml
COPY pom.xml .

# Загрузка зависимостей
RUN mvn dependency:go-offline -B

# Копирование исходного кода
COPY src ./src

# Сборка приложения
RUN mvn package -DskipTests

# Финальный образ
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Копирование собранного JAR из предыдущего этапа
COPY --from=build /app/target/*.jar app.jar

# Создание директории для .env файла
RUN mkdir -p /app/config

# Определение переменной окружения для Spring профиля
ENV SPRING_PROFILES_ACTIVE=prod

# Используем PORT переменную от Render
ENV PORT=1634
EXPOSE ${PORT}

# Запуск приложения с динамическим портом
ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar --server.port=${PORT}"]