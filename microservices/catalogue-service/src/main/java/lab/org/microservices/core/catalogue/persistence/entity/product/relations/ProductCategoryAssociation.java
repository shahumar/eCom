package lab.org.microservices.core.catalogue.persistence.entity.product.relations;


import lab.org.microservices.core.catalogue.persistence.entity.category.CategoryEntity;
import lab.org.microservices.core.catalogue.persistence.entity.product.ProductEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document("products_categories")
public class ProductCategoryAssociation {

    @Id
    private String id;

    @DBRef
    ProductEntity product;

    @DBRef
    CategoryEntity category;
}
