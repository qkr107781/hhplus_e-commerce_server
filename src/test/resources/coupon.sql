
--쿠폰 메타 데이터 생성
INSERT INTO coupon
(coupon_id, discount_price, issuance_end_time, issuance_start_time, reg_date, remaining_coupon_amount, total_coupon_amount, coupon_status, coupon_name)
VALUES(1, 1000, '2025-07-01 12:00:00', '2025-07-03 14:00:00', '2025-06-30 13:00:00', 20, 30, 'closed', '여름 쿠폰');

INSERT INTO coupon
(coupon_id, discount_price, issuance_end_time, issuance_start_time, reg_date, remaining_coupon_amount, total_coupon_amount, coupon_status, coupon_name)
VALUES(2, 1000, '2025-07-05 12:00:00', '2025-07-08 14:00:00', '2025-06-30 13:00:00', 10, 30, 'pending', '신규 쿠폰');

INSERT INTO coupon
(coupon_id, discount_price, issuance_end_time, issuance_start_time, reg_date, remaining_coupon_amount, total_coupon_amount, coupon_status, coupon_name)
VALUES(3, 1000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL -1 DAY), '2025-06-30 13:00:00', 100, 100, 'issuing', '복귀 쿠폰');

INSERT INTO coupon
(coupon_id, discount_price, issuance_end_time, issuance_start_time, reg_date, remaining_coupon_amount, total_coupon_amount, coupon_status, coupon_name)
VALUES(4, 1000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL -1 DAY), '2025-06-30 13:00:00', 100, 100, 'issuing', '테스트 쿠폰');

INSERT INTO coupon
(coupon_id, discount_price, issuance_end_time, issuance_start_time, reg_date, remaining_coupon_amount, total_coupon_amount, coupon_status, coupon_name)
VALUES(5, 1000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL -1 DAY), '2025-06-30 13:00:00', 100, 100, 'issuing', '동시성 쿠폰');