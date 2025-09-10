-- Debezium용 권한 (application 유저는 env로 이미 생성됨)
GRANT REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'application'@'%';
FLUSH PRIVILEGES;

ALTER USER 'application'@'%' IDENTIFIED WITH mysql_native_password BY 'application';
FLUSH PRIVILEGES;