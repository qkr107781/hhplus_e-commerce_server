--주문 입력
INSERT INTO `order`( coupon_discount_price, coupon_id, order_date, order_status, total_price, user_id)
VALUES( 0, 0, DATE_ADD(NOW(), INTERVAL -4 DAY), 'complete_payment', 10000, 1);

INSERT INTO `order`( coupon_discount_price, coupon_id, order_date, order_status, total_price, user_id)
VALUES( 0, 0, DATE_ADD(NOW(), INTERVAL -4 DAY), 'complete_payment', 10000, 1);

INSERT INTO `order`( coupon_discount_price, coupon_id, order_date, order_status, total_price, user_id)
VALUES( 0, 0, DATE_ADD(NOW(), INTERVAL -3 DAY), 'complete_payment', 10000, 1);

INSERT INTO `order`( coupon_discount_price, coupon_id, order_date, order_status, total_price, user_id)
VALUES( 0, 0, DATE_ADD(NOW(), INTERVAL -2 DAY), 'complete_payment', 10000, 1);

INSERT INTO `order`( coupon_discount_price, coupon_id, order_date, order_status, total_price, user_id)
VALUES( 0, 0, DATE_ADD(NOW(), INTERVAL -2 DAY), 'complete_payment', 10000, 1);

INSERT INTO `order`( coupon_discount_price, coupon_id, order_date, order_status, total_price, user_id)
VALUES( 0, 0, DATE_ADD(NOW(), INTERVAL -2 DAY), 'complete_payment', 10000, 1);

INSERT INTO `order`( coupon_discount_price, coupon_id, order_date, order_status, total_price, user_id)
VALUES( 0, 0, DATE_ADD(NOW(), INTERVAL -1 DAY), 'complete_payment', 10000, 1);


--주문 상품 입력
INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(3, 1, 7, 20000, 12);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(3, 1, 4, 10000, 40);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(3, 1, 5, 10000, 41);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(4, 1, 7, 10000, 11);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(4, 1, 8, 5000, 21);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(4, 1, 9, 10000, 31);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(5, 1, 7, 5000, 11);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(5, 1, 8, 10000, 21);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(5, 1, 9, 5000, 31);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(6, 1, 4, 10000, 10);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(6, 1, 5, 5000, 20);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(6, 1, 6, 10000, 30);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(7, 1, 4, 5000, 10);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(7, 1, 5, 5000, 20);

INSERT INTO order_product(order_id, product_id, product_option_id, product_price, product_quantity)
VALUES(7, 1, 6, 5000, 30);



--상품 옵션 정보 입력
INSERT INTO product_option(sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES('Y', 10000, 1, now(), '옵션1', 10, 50);

INSERT INTO product_option(sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES('N', 10000, 1, now(), '옵션2', 20, 50);

INSERT INTO product_option(sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES('N', 10000, 1, now(), '옵션3', 30, 50);

INSERT INTO product_option(sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES('N', 10000, 1, now(), '옵션4', 20, 50);

INSERT INTO product_option(sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES('N', 10000, 1, now(), '옵션5', 30, 50);

INSERT INTO product_option(sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES('N', 10000, 1, now(), '옵션6', 20, 50);

INSERT INTO product_option(sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES('N', 10000, 1, now(), '옵션7', 30, 50);

INSERT INTO product_option(sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES('N', 10000, 1, now(), '옵션8', 30, 50);

INSERT INTO product_option(sales_yn, price, product_id, reg_date, option_name, stock_quantity, total_quantity)
VALUES('N', 10000, 1, now(), '옵션9', 30, 50);
