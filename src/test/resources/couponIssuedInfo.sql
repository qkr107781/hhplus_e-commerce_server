
--쿠폰 발급 정보 데이터 생성
INSERT INTO coupon_issued_info(use_yn, coupon_id,  end_date, issued_at, user_id)
VALUES('N', 3, DATE_ADD(NOW(), INTERVAL 1 DAY), now(), 1);

INSERT INTO coupon_issued_info(use_yn, coupon_id,  end_date, issued_at, user_id)
VALUES('Y',4, DATE_ADD(NOW(), INTERVAL 1 DAY), now(), 1);