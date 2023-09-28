package lab.org.microservices.core.catalogue.services;


import lab.org.api.core.product.Category;
import lab.org.microservices.core.catalogue.persistence.entity.category.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category entityToApi(CategoryEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    CategoryEntity apiToEntity(Category category);
}
