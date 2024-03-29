package lab.org.microservices.core.catalogue.persistence.entity.product;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Document(collection = "products")
//@CompoundIndex(def = "{'merchantId': 1, 'sku': 1}", unique = true)
public class ProductEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    @Indexed(unique = true)
    private int productId;

//    private long merchantId;

    private String name;

//    private LocalDateTime dateAvailable = LocalDateTime.now();
//
//    private Boolean available = true;
//
//    private Boolean preOrder = false;
//
//    private Boolean productShipeable = false;
//
//    private Boolean productVirtual = false;
//
//    private Boolean productIsFree = false;

    private BigDecimal weight;

//    private BigDecimal length;
//
//    private BigDecimal width;
//
//    private BigDecimal height;
//
//    private BigDecimal reviewAvg;
//
//    private Integer reviewCount;
//
//    private Integer productOrdered;
//
//    private Integer sortOrder = Integer.valueOf(0);

//    @NotNull
//    @Pattern(regexp = "^[a-zA-Z0-9_]*$")
//    private String sku;

//    private String refSku;
//
//    private Integer rentalDuration;
//
//    private Integer rentalPeriod;

//    @CreatedDate
//    private LocalDateTime dateCreated;
//
//    @LastModifiedDate
//    private LocalDateTime dateModified;
//
//    private String modifiedBy;
//
//    @DocumentReference
//    private Set<ProductDescription> descriptions = new HashSet<>();
//
//    @DocumentReference
//    private Set<ProductAvailability> availabilities = new HashSet<>();

    public ProductEntity(int productId, String name, int weight) {
        this.productId = productId;
        this.name = name;
        this.weight = BigDecimal.valueOf(weight);
    }
//    public ProductEntity(int productId, String name, int weight, long merchantId) {
//        this.productId = productId;
//        this.name = name;
//        this.merchantId = merchantId;
//        this.weight = BigDecimal.valueOf(weight);
//    }


}
