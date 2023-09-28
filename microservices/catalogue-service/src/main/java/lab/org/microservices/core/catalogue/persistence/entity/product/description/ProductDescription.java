package lab.org.microservices.core.catalogue.persistence.entity.product.description;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document
public class ProductDescription {

    @Id
    private String id;

    private String productHighlight;

    private String productExternalDl;

    private String seUrl;

    private String metaTagTitle;

    private String metaTagKeywords;

    private String metaTagDescription;
}
