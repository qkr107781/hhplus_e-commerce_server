package kr.hhplus.be.server.util;

import kr.hhplus.be.server.presentation.coupon.CouponResponse;
import kr.hhplus.be.server.presentation.order.OrderResponse;
import kr.hhplus.be.server.presentation.payment.PaymentResponse;
import kr.hhplus.be.server.presentation.product.ProductResponse;
import kr.hhplus.be.server.presentation.balance.BalanceResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class DummyDataUtil {
    /**
     * 쿠폰 발급 더미 데이터
     *
     * @return CouponResponse.Issue
     */
    public CouponResponse.Issue getCouponIssue() {
        String issued_at_str = "2025-07-16 10:30:00";
        String end_date_str = "2025-07-16 11:30:00";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime issued_at = LocalDateTime.parse(issued_at_str,formatter);
        LocalDateTime end_date = LocalDateTime.parse(end_date_str,formatter);

        return new CouponResponse.Issue(1L,2L,"신규 가입 쿠폰",1_000L,10_000L,issued_at,end_date);
    }
    /**
     * 본인 쿠폰 조회 더미 데이터
     * @return CouponResponse.SelectByUserId
     */
    public CouponResponse.SelectByUserId getCouponSelectByUserId() {
        String issued_at_str = "2025-07-16 10:30:00";
        String end_date_str = "2025-07-16 11:30:00";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime issued_at = LocalDateTime.parse(issued_at_str,formatter);
        LocalDateTime end_date = LocalDateTime.parse(end_date_str,formatter);

        return new CouponResponse.SelectByUserId(2L,"신규 가입 쿠폰",1_000L,10_000L,issued_at,end_date,"N");
    }

    /**
     * 쿠폰 상태별 조회 더미 데이터
     * @param status:쿠폰 상태 값
     * @return CouponResponse.SelectByStatus
     */
    public CouponResponse.SelectByStatus getCouponSelectByStatus(String status) {
        String reg_date_str = "2025-07-16 09:00:00";
        String issuance_start_time_str = "2025-07-16 10:00:00";
        String issuance_end_time_str = "2025-07-16 11:00:00";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime reg_date = LocalDateTime.parse(reg_date_str,formatter);
        LocalDateTime issuance_start_time = LocalDateTime.parse(issuance_start_time_str,formatter);
        LocalDateTime issuance_end_time = LocalDateTime.parse(issuance_end_time_str,formatter);

        if("pending".equals(status)){
            return new CouponResponse.SelectByStatus(1L,"복귀 환영 쿠폰",3_000L,30L,20L,10_000L,issuance_start_time,issuance_end_time,24L,"pending",reg_date);
        }else if("issuing".equals(status)){
            return new CouponResponse.SelectByStatus(2L,"신규 가입 쿠폰",1_000L,30L,20L,10_000L,issuance_start_time,issuance_end_time,24L,"issuing",reg_date);
        }else if("closed".equals(status)){
            return new CouponResponse.SelectByStatus(3L,"여름 특가 쿠폰",4_000L,30L,20L,10_000L,issuance_start_time,issuance_end_time,24L,"closed",reg_date);
        }else{
            return  new CouponResponse.SelectByStatus(0L,"",0L,0L,0L,0L,null,null,0L,"",null);
        }
    }

    //주문 더미 데이터

    /**
     * 주문 요청 더미 데이터
     * @return OrderResponse.Create
     */
    public OrderResponse.Create getOrderCreate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String order_date_str = "2025-07-16 11:00:00";
        LocalDateTime order_date = LocalDateTime.parse(order_date_str,formatter);

        List<OrderResponse.OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(new OrderResponse.OrderProduct(1L,1L,"반팔티",1L,"XL",2,12_000L));
        orderProducts.add(new OrderResponse.OrderProduct(2L,1L,"반팔티",2L,"M",4,12_000L));

        return new OrderResponse.Create(1L,2L,"신규 가입 쿠폰",1_000L,72_000L,"pending_payment",order_date,orderProducts);
    }

    //결제 더미 데이터
    public PaymentResponse.Create getPaymentCreate(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String order_date_str = "2025-07-16 11:00:00";
        LocalDateTime order_date = LocalDateTime.parse(order_date_str,formatter);

        List<OrderResponse.OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(new OrderResponse.OrderProduct(1L,1L,"반팔티",1L,"XL",2,12_000L));
        orderProducts.add(new OrderResponse.OrderProduct(2L,1L,"반팔티",2L,"M",4,12_000L));

        return new PaymentResponse.Create(1L,new OrderResponse.Create(1L,2L,"신규 가입 쿠폰",1_000L,72_000L,"pending_payment",order_date,orderProducts));
    }


    //상품 더미 데이터
    public List<ProductResponse.Select> getProductsSelect(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String reg_date_str = "2025-07-15 09:00:00";
        LocalDateTime reg_date = LocalDateTime.parse(reg_date_str,formatter);

        List<ProductResponse.Option> options1 = new ArrayList<>();
        options1.add(new ProductResponse.Option(1L,"XL",12_000L,10L,5L,"Y",reg_date));
        options1.add(new ProductResponse.Option(2L,"M",12_000L,5L,2L,"Y",reg_date));

        List<ProductResponse.Option> options2 = new ArrayList<>();
        options2.add(new ProductResponse.Option(3L,"240",32_000L,10L,5L,"Y",reg_date));
        options2.add(new ProductResponse.Option(4L,"270",32_000L,5L,2L,"Y",reg_date));

        List<ProductResponse.Select> products = new ArrayList<>();
        products.add(new ProductResponse.Select(1L,"반팔 티셔츠","반팔 티셔츠 설명", options1));
        products.add(new ProductResponse.Select(2L,"신발","신발 설명", options2));

        return products;
    }

    public List<ProductResponse.Statistics> getProductsStatistics(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String selection_date_str1 = "2025-07-14 03:00:00";
        LocalDateTime selection_date1 = LocalDateTime.parse(selection_date_str1,formatter);
        String selection_date_str2 = "2025-07-15 03:00:00";
        LocalDateTime selection_date2 = LocalDateTime.parse(selection_date_str2,formatter);
        String selection_date_str3 = "2025-07-16 03:00:00";
        LocalDateTime selection_date3 = LocalDateTime.parse(selection_date_str3,formatter);

        List<ProductResponse.Statistics> statisticsList = new ArrayList<>();
        statisticsList.add(new ProductResponse.Statistics(1L,1L,"반팔 티셔츠","XL",12_000L,5L,1L,selection_date1));
        statisticsList.add(new ProductResponse.Statistics(1L,2L,"반팔 티셔츠","M",12_000L,2L,2L,selection_date2));
        statisticsList.add(new ProductResponse.Statistics(2L,3L,"반팔 티셔츠","240",32_000L,1L,3L,selection_date3));

        return statisticsList;
    }

    //유저 더미 테이터
    public BalanceResponse.Charge getUserBalanceCharge(){
        return new BalanceResponse.Charge(1L,50_000L,60_000L);
    }

    public BalanceResponse.SelectBalanceByUserId getUserSelectBalanceByUserId(){
        return new BalanceResponse.SelectBalanceByUserId(1L,10_000L);
    }
}
