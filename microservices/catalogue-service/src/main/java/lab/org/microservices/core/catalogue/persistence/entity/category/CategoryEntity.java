package lab.org.microservices.core.catalogue.persistence.entity.category;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "categories")
public class CategoryEntity {

    @Id
    private String id;

    @Indexed(name = "category_id", unique = true)
    private int categoryId;

    @Version
    private Integer version;

    private String name;

    private String parentId;

    private String slug;

    public CategoryEntity(int categoryId, String name, String parentId, String slug) {
        this.categoryId = categoryId;
        this.name = name;
        this.parentId = parentId;
        this.slug = slug;
    }


}
