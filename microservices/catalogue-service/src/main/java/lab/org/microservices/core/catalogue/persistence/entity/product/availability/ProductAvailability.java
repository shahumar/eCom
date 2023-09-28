package lab.org.microservices.core.catalogue.persistence.entity.product.availability;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document
public class ProductAvailability {

    @Id
    private String id;

    private LocalDateTime dateAvailable;

    private String region;

    private String regionVariant;

    private String owner;

    private Boolean status = true;

    private Boolean isAlwaysFreeShipping;

    private Boolean available;

}
