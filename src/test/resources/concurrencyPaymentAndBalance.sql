--유저 잔액 생성
--충전 + 결제 테스트용
INSERT INTO balance (balance, last_charge_date, user_id) VALUES(400000,  now(), 1);
--반복 결제 테스트 용
INSERT INTO balance (balance, last_charge_date, user_id) VALUES(100000,  now(), 2);
--반복 충전 테스트용
INSERT INTO balance (balance, last_charge_date, user_id) VALUES(0,  now(), 3);

--상품 옵션 정보 입력
INSERT INTO product( name)
VALUES( '티셔츠1L');

INSERT INTO product_option(sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES('Y', 10000, 1, now(), '옵션1', 30, 50);

INSERT INTO product_option(sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES('Y', 20000, 1, now(), '옵션2', 30, 50);

INSERT INTO product_option(sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES('Y', 30000, 1, now(), '옵션3', 30, 50);

INSERT INTO product_option(sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES('Y', 30000, 1, now(), '옵션4', 30, 50);

INSERT INTO product_option(sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES('Y', 30000, 1, now(), '옵션5', 30, 50);

--사용자 소유 쿠폰 정보 입력
INSERT INTO coupon
(coupon_id, discount_price, issuance_end_time, issuance_start_time, reg_date, remaining_coupon_amount, total_coupon_amount, coupon_status, coupon_name)
VALUES(3, 1000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL -1 DAY), '2025-06-30 13:00:00', 30, 30, 'issuing', '복귀 쿠폰');

INSERT INTO coupon_issued_info(use_yn, coupon_id,  end_date, issued_at, user_id)
VALUES('Y', 3, DATE_ADD(NOW(), INTERVAL 1 DAY), now(), 1);

--주문 정보 입력
INSERT INTO `order` (order_id,coupon_discount_price, coupon_id, order_date, total_price, user_id, order_status)
VALUES(3, 1000, 3, now(), 320000, 1, 'pending_payment');

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(3, 1, 2, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(3, 1, 3, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(3, 1, 4, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(3, 1, 5, 20000, 1);