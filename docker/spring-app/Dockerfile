FROM eclipse-temurin:17-jre
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENV TZ=Asia/Seoul
ENTRYPOINT ["java","-Dspring.profiles.active=docker","-jar","/app/app.jar"]