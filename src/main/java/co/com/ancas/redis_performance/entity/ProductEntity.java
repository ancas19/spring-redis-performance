package co.com.ancas.redis_performance.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString
@Table("products")
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {
    @Id
    private Long id;
    private String description;
    private Double price;
}
