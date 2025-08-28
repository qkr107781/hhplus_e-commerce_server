# Spring App용 Dockerfile
FROM openjdk:17-jdk-slim
# 컨테이너 내 작업 디렉토리 설정
WORKDIR /app
# 빌드된 JAR 파일을 컨테이너로 복사
COPY build/libs/*.jar ./app.jar
# application.yml 파일을 resources 디렉토리로 복사
COPY src/main/resources/application.yml ./application.yml
# 애플리케이션이 사용하는 포트 노출
EXPOSE 8080
# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]