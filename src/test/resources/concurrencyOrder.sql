--상품 옵션 정보 입력
INSERT INTO product( name)
VALUES( '티셔츠1L');

INSERT INTO product_option(product_option_id,sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES(1,'Y', 10000, 1, now(), '옵션1', 30, 50);

INSERT INTO product_option(product_option_id,sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES(2,'Y', 20000, 1, now(), '옵션2', 30, 50);

INSERT INTO product_option(product_option_id,sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES(3,'Y', 30000, 1, now(), '옵션3', 30, 50);

INSERT INTO product_option(product_option_id,sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES(4,'Y', 30000, 1, now(), '옵션4', 30, 50);

INSERT INTO product_option(product_option_id,sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES(5,'Y', 30000, 1, now(), '옵션5', 30, 50);

INSERT INTO product_option(product_option_id,sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES(6,'Y', 10000, 1, now(), '옵션6', 30, 50);

INSERT INTO product_option(product_option_id,sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES(7,'Y', 20000, 1, now(), '옵션7', 30, 50);

INSERT INTO product_option(product_option_id,sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES(8,'Y', 30000, 1, now(), '옵션8', 30, 50);

INSERT INTO product_option(product_option_id,sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES(9,'Y', 30000, 1, now(), '옵션9', 30, 50);

INSERT INTO product_option(product_option_id,sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES(10,'Y', 30000, 1, now(), '옵션10', 30, 50);

INSERT INTO product_option(product_option_id,sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES(11,'Y', 30000, 1, now(), '옵션10', 30, 50);

INSERT INTO product_option(product_option_id,sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES(12,'Y', 30000, 1, now(), '옵션10', 30, 50);

INSERT INTO product_option(product_option_id,sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES(13,'Y', 30000, 1, now(), '옵션10', 30, 50);

INSERT INTO product_option(product_option_id,sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES(14,'Y', 30000, 1, now(), '옵션10', 30, 50);

INSERT INTO product_option(product_option_id,sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES(15,'Y', 30000, 1, now(), '옵션10', 30, 50);

--사용자 소유 쿠폰 정보 입력
INSERT INTO coupon
(coupon_id, discount_price, issuance_end_time, issuance_start_time, reg_date, remaining_coupon_amount, total_coupon_amount, coupon_status, coupon_name)
VALUES(3, 1000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL -1 DAY), '2025-06-30 13:00:00', 30, 30, 'issuing', '복귀 쿠폰');

INSERT INTO coupon
(coupon_id, discount_price, issuance_end_time, issuance_start_time, reg_date, remaining_coupon_amount, total_coupon_amount, coupon_status, coupon_name)
VALUES(4, 1000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL -1 DAY), '2025-06-30 13:00:00', 30, 30, 'issuing', '복귀 쿠폰');

INSERT INTO coupon
(coupon_id, discount_price, issuance_end_time, issuance_start_time, reg_date, remaining_coupon_amount, total_coupon_amount, coupon_status, coupon_name)
VALUES(5, 1000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL -1 DAY), '2025-06-30 13:00:00', 30, 30, 'issuing', '복귀 쿠폰');

INSERT INTO coupon
(coupon_id, discount_price, issuance_end_time, issuance_start_time, reg_date, remaining_coupon_amount, total_coupon_amount, coupon_status, coupon_name)
VALUES(6, 1000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL -1 DAY), '2025-06-30 13:00:00', 30, 30, 'issuing', '복귀 쿠폰');

INSERT INTO coupon
(coupon_id, discount_price, issuance_end_time, issuance_start_time, reg_date, remaining_coupon_amount, total_coupon_amount, coupon_status, coupon_name)
VALUES(7, 1000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL -1 DAY), '2025-06-30 13:00:00', 30, 30, 'issuing', '복귀 쿠폰');

INSERT INTO coupon
(coupon_id, discount_price, issuance_end_time, issuance_start_time, reg_date, remaining_coupon_amount, total_coupon_amount, coupon_status, coupon_name)
VALUES(8, 1000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL -1 DAY), '2025-06-30 13:00:00', 30, 30, 'issuing', '복귀 쿠폰');

INSERT INTO coupon_issued_info(use_yn, coupon_id,  end_date, issued_at, user_id)
VALUES('N', 3, DATE_ADD(NOW(), INTERVAL 1 DAY), now(), 1);

INSERT INTO coupon_issued_info(use_yn, coupon_id,  end_date, issued_at, user_id)
VALUES('N', 4, DATE_ADD(NOW(), INTERVAL 1 DAY), now(), 1);

INSERT INTO coupon_issued_info(use_yn, coupon_id,  end_date, issued_at, user_id)
VALUES('N', 5, DATE_ADD(NOW(), INTERVAL 1 DAY), now(), 1);

INSERT INTO coupon_issued_info(use_yn, coupon_id,  end_date, issued_at, user_id)
VALUES('Y', 3, DATE_ADD(NOW(), INTERVAL 1 DAY), now(), 2);

INSERT INTO coupon_issued_info(use_yn, coupon_id,  end_date, issued_at, user_id)
VALUES('Y', 4, DATE_ADD(NOW(), INTERVAL 1 DAY), now(), 2);

INSERT INTO coupon_issued_info(use_yn, coupon_id,  end_date, issued_at, user_id)
VALUES('Y', 5, DATE_ADD(NOW(), INTERVAL 1 DAY), now(), 2);

INSERT INTO coupon_issued_info(use_yn, coupon_id,  end_date, issued_at, user_id)
VALUES('Y', 6, DATE_ADD(NOW(), INTERVAL 1 DAY), now(), 2);

INSERT INTO coupon_issued_info(use_yn, coupon_id,  end_date, issued_at, user_id)
VALUES('Y', 7, DATE_ADD(NOW(), INTERVAL 1 DAY), now(), 2);

INSERT INTO coupon_issued_info(use_yn, coupon_id,  end_date, issued_at, user_id)
VALUES('Y', 8, DATE_ADD(NOW(), INTERVAL 1 DAY), now(), 2);

--주문 정보 입력
INSERT INTO `order` (order_id,coupon_discount_price, coupon_id, order_date, total_price, user_id, order_status)
VALUES(3, 1000, 3, now(), 50000, 2, 'pending_payment');

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(3, 1, 12, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(3, 1, 13, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(3, 1, 14, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(3, 1, 15, 20000, 1);



--주문 취소 정보 입력
INSERT INTO `order` (order_id,coupon_discount_price, coupon_id, order_date, total_price, user_id, order_status)
VALUES(4, 1000, 4, now(), 50000, 2, 'pending_payment');

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(4, 1, 6, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(4, 1, 7, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(4, 1, 8, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(4, 1, 9, 20000, 1);

--주문 취소 정보 입력
INSERT INTO `order` (order_id,coupon_discount_price, coupon_id, order_date, total_price, user_id, order_status)
VALUES(5, 1000, 5, now(), 50000, 2, 'pending_payment');

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(5, 1, 6, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(5, 1, 7, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(5, 1, 8, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(5, 1, 9, 20000, 1);

--주문 취소 정보 입력
INSERT INTO `order` (order_id,coupon_discount_price, coupon_id, order_date, total_price, user_id, order_status)
VALUES(6, 1000, 6, now(), 50000, 2, 'pending_payment');

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(6, 1, 6, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(6, 1, 7, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(6, 1, 8, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(6, 1, 9, 20000, 1);

--주문 취소 정보 입력
INSERT INTO `order` (order_id,coupon_discount_price, coupon_id, order_date, total_price, user_id, order_status)
VALUES(7, 1000, 7, now(), 50000, 2, 'pending_payment');

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(7, 1, 6, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(7, 1, 7, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(7, 1, 8, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(7, 1, 9, 20000, 1);

--주문 취소 정보 입력
INSERT INTO `order` (order_id,coupon_discount_price, coupon_id, order_date, total_price, user_id, order_status)
VALUES(8, 1000, 8, now(), 50000, 2, 'pending_payment');

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(8, 1, 6, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(8, 1, 7, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(8, 1, 8, 20000, 5);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(8, 1, 9, 20000, 1);