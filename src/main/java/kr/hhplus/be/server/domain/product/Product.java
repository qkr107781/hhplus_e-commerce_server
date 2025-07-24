package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="product_option_id", nullable = false)
    private List<ProductOption> productOptions;

    @Builder
    public Product(Long productId, String name, String description, List<ProductOption> productOptions) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.productOptions = productOptions;
    }
}