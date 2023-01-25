package lab.org.microservices.core.product;

import lab.org.api.core.product.Product;
import lab.org.microservices.core.product.persistence.entity.product.ProductEntity;
import lab.org.microservices.core.product.services.ProductMapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;


public class MapperTests {

    private ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    void mapperTest() {
        assertNotNull(mapper);
        Product api = new Product(1, "n", 1, "sa");

        ProductEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getName(), entity.getName());
        assertEquals(api.getProductId(), entity.getProductId());
        assertEquals(api.getWeight(), entity.getWeight());

        Product api2 = mapper.entityToApi(entity);
        assertEquals(api.getWeight(), api2.getWeight());
        assertEquals(api.getName(), api2.getName());
        assertEquals(api.getProductId(), api2.getProductId());
        assertNull(api2.getServiceAddress());
    }
}
