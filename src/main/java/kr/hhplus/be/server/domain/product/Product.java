package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.order.OrderProduct;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "description", length = 200)
    private String description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> productOptions;

    @Builder
    public Product(Long productId, String name, String description, List<ProductOption> productOptions) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.productOptions = Objects.requireNonNullElseGet(productOptions, ArrayList::new);
    }

    public void addProductOptionList(List<ProductOption> productOptionList) {
        this.productOptions = productOptionList;
    }
}