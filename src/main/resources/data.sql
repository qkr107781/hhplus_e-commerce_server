INSERT INTO coupon
(coupon_id, discount_price, issuance_end_time, issuance_start_time, reg_date, remaining_coupon_amount, total_coupon_amount, coupon_status, coupon_name)
VALUES(1, 1000, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL -1 DAY), '2025-06-30 13:00:00', 100000, 100000, 'issuing', '복귀 쿠폰');