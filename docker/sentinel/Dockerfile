# Redis Sentinel을 위한 Dockerfile
FROM redis:latest

EXPOSE 26379

# sentinel.conf 파일을 컨테이너 내부로 복사합니다.
COPY sentinel.conf /etc/redis/sentinel.conf

# 실제 파일명인 'entrypoint.sh'를 사용하여 COPY 명령을 수정합니다.
# 이 파일을 컨테이너의 entrypoint 디렉터리로 복사합니다.
COPY entrypoint.sh /usr/local/bin/entrypoint.sh

# entrypoint 스크립트에 실행 권한을 부여합니다.
RUN chmod +x /usr/local/bin/entrypoint.sh
# 개행 문자를 Unix 형식으로 변환합니다.
RUN sed -i 's/\r$//' /usr/local/bin/entrypoint.sh
# 컨테이너의 entrypoint를 설정합니다.
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]