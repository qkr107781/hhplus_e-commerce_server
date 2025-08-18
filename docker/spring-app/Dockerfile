# Dockerfile 예시
# OpenJDK 17 기반의 JRE 이미지를 사용
FROM openjdk:17-jdk-slim

# 애플리케이션의 JAR 파일을 컨테이너 내부에 복사
COPY build/libs/*.jar app.jar

# 환경 변수 설정
ENV TZ=Asia/Seoul

# 컨테이너 실행 시 app.jar 파일을 실행하도록 설정
ENTRYPOINT ["java", "-jar", "/app.jar"]