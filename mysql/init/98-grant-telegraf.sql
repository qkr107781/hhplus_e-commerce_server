GRANT PROCESS, REPLICATION CLIENT ON *.* TO 'application'@'%';
GRANT SELECT ON performance_schema.* TO 'application'@'%';
FLUSH PRIVILEGES;