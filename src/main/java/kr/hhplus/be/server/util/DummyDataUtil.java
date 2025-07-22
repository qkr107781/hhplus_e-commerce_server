package kr.hhplus.be.server.util;

import kr.hhplus.be.server.application.balance.dto.BalanceResponse;
import kr.hhplus.be.server.presentation.coupon.CouponResponse;
import kr.hhplus.be.server.presentation.order.OrderResponse;
import kr.hhplus.be.server.presentation.payment.PaymentResponse;
import kr.hhplus.be.server.presentation.product.ProductResponse;
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
        String issuedAtStr = "2025-07-16 10:30:00";
        String endDateStr = "2025-07-16 11:30:00";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime issuedAt = LocalDateTime.parse(issuedAtStr,formatter);
        LocalDateTime endDate = LocalDateTime.parse(endDateStr,formatter);

        return new CouponResponse.Issue(1L,2L,"신규 가입 쿠폰",1_000L,10_000L,issuedAt,endDate);
    }
    /**
     * 본인 쿠폰 조회 더미 데이터
     * @return CouponResponse.SelectByUserId
     */
    public CouponResponse.SelectByUserId getCouponSelectByUserId() {
        String issuedAtStr = "2025-07-16 10:30:00";
        String endDateStr = "2025-07-16 11:30:00";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime issuedAt = LocalDateTime.parse(issuedAtStr,formatter);
        LocalDateTime endDate = LocalDateTime.parse(endDateStr,formatter);

        return new CouponResponse.SelectByUserId(2L,"신규 가입 쿠폰",1_000L,10_000L,issuedAt,endDate,"N");
    }

    /**
     * 쿠폰 상태별 조회 더미 데이터
     * @param status:쿠폰 상태 값
     * @return CouponResponse.SelectByStatus
     */
    public CouponResponse.SelectByStatus getCouponSelectByStatus(String status) {
        String regDateStr = "2025-07-16 09:00:00";
        String issuanceStartTimeStr = "2025-07-16 10:00:00";
        String issuanceEndTimeStr = "2025-07-16 11:00:00";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime regDate = LocalDateTime.parse(regDateStr,formatter);
        LocalDateTime issuanceStartTime = LocalDateTime.parse(issuanceStartTimeStr,formatter);
        LocalDateTime issuanceEndTime = LocalDateTime.parse(issuanceEndTimeStr,formatter);

        if("pending".equals(status)){
            return new CouponResponse.SelectByStatus(1L,"복귀 환영 쿠폰",3_000L,30L,20L,10_000L,issuanceStartTime,issuanceEndTime,24L,"pending",regDate);
        }else if("issuing".equals(status)){
            return new CouponResponse.SelectByStatus(2L,"신규 가입 쿠폰",1_000L,30L,20L,10_000L,issuanceStartTime,issuanceEndTime,24L,"issuing",regDate);
        }else if("closed".equals(status)){
            return new CouponResponse.SelectByStatus(3L,"여름 특가 쿠폰",4_000L,30L,20L,10_000L,issuanceStartTime,issuanceEndTime,24L,"closed",regDate);
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
        String orderDateStr = "2025-07-16 11:00:00";
        LocalDateTime orderDate = LocalDateTime.parse(orderDateStr,formatter);

        List<OrderResponse.OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(new OrderResponse.OrderProduct(1L,1L,"반팔티",1L,"XL",2,12_000L));
        orderProducts.add(new OrderResponse.OrderProduct(2L,1L,"반팔티",2L,"M",4,12_000L));

        return new OrderResponse.Create(1L,2L,"신규 가입 쿠폰",1_000L,72_000L,"pending_payment",orderDate,orderProducts);
    }

    //결제 더미 데이터
    public PaymentResponse.Create getPaymentCreate(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String orderDateStr = "2025-07-16 11:00:00";
        LocalDateTime orderDate = LocalDateTime.parse(orderDateStr,formatter);

        List<OrderResponse.OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(new OrderResponse.OrderProduct(1L,1L,"반팔티",1L,"XL",2,12_000L));
        orderProducts.add(new OrderResponse.OrderProduct(2L,1L,"반팔티",2L,"M",4,12_000L));

        return new PaymentResponse.Create(1L,new OrderResponse.Create(1L,2L,"신규 가입 쿠폰",1_000L,72_000L,"pending_payment",orderDate,orderProducts));
    }


    //상품 더미 데이터
    public List<ProductResponse.Select> getProductsSelect(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String regDateStr = "2025-07-15 09:00:00";
        LocalDateTime regDate = LocalDateTime.parse(regDateStr,formatter);

        List<ProductResponse.Option> options1 = new ArrayList<>();
        options1.add(new ProductResponse.Option(1L,"XL",12_000L,10L,5L,"Y",regDate));
        options1.add(new ProductResponse.Option(2L,"M",12_000L,5L,2L,"Y",regDate));

        List<ProductResponse.Option> options2 = new ArrayList<>();
        options2.add(new ProductResponse.Option(3L,"240",32_000L,10L,5L,"Y",regDate));
        options2.add(new ProductResponse.Option(4L,"270",32_000L,5L,2L,"Y",regDate));

        List<ProductResponse.Select> products = new ArrayList<>();
        products.add(new ProductResponse.Select(1L,"반팔 티셔츠","반팔 티셔츠 설명", options1));
        products.add(new ProductResponse.Select(2L,"신발","신발 설명", options2));

        return products;
    }

    public List<ProductResponse.Statistics> getProductsStatistics(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String selectionDateStr1 = "2025-07-14 03:00:00";
        LocalDateTime selectionDate1 = LocalDateTime.parse(selectionDateStr1,formatter);
        String selectionDateStr2 = "2025-07-15 03:00:00";
        LocalDateTime selectionDate2 = LocalDateTime.parse(selectionDateStr2,formatter);
        String selectionDateStr3 = "2025-07-16 03:00:00";
        LocalDateTime selectionDate3 = LocalDateTime.parse(selectionDateStr3,formatter);

        List<ProductResponse.Statistics> statisticsList = new ArrayList<>();
        statisticsList.add(new ProductResponse.Statistics(1L,1L,"반팔 티셔츠","XL",12_000L,5L,1L,selectionDate1));
        statisticsList.add(new ProductResponse.Statistics(1L,2L,"반팔 티셔츠","M",12_000L,2L,2L,selectionDate2));
        statisticsList.add(new ProductResponse.Statistics(2L,3L,"반팔 티셔츠","240",32_000L,1L,3L,selectionDate3));

        return statisticsList;
    }

    //유저 더미 테이터
    public BalanceResponse getUserBalanceCharge(){
        return new BalanceResponse(1L,50_000L,LocalDateTime.now());
    }

    public BalanceResponse getUserSelectBalanceByUserId(){
        return new BalanceResponse(1L,10_000L,LocalDateTime.now());
    }
}