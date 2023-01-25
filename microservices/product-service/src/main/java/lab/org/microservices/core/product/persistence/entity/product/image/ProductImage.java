package lab.org.microservices.core.product.persistence.entity.product.image;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.InputStream;

@Data
@NoArgsConstructor
@Document
public class ProductImage {

    @Id
    private String id;

    private String productImage;

    private boolean defaultImage = true;

    private ImageType imageType;

    private String imageUrl;

    private boolean imageCrop;

    @Transient
    private InputStream image;
}
