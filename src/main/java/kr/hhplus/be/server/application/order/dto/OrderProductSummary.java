package kr.hhplus.be.server.application.order.dto;

/**
 * @param totalOrderedQuantity 해당 옵션의 총 주문 수량
 */
public record OrderProductSummary(Long productOptionId, long totalOrderedQuantity) {

}
