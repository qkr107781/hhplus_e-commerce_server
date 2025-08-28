-- 1. FK 제약조건 해제
SET FOREIGN_KEY_CHECKS = 0;

-- 2. 모든 테이블 데이터 삭제
TRUNCATE TABLE balance;
TRUNCATE TABLE coupon;
TRUNCATE TABLE coupon_issued_info;
TRUNCATE TABLE `order`;
TRUNCATE TABLE order_product;
TRUNCATE TABLE payment;
TRUNCATE TABLE product;
TRUNCATE TABLE product_option;

-- 3. FK 제약조건 원복
SET FOREIGN_KEY_CHECKS = 1;