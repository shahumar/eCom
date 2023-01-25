package lab.org.api.core.product;

import java.math.BigDecimal;

public class Product {
    private int productId;
    private String name;
    private BigDecimal weight;
    private String serviceAddress;

    public Product() {
        productId = 0;
        name = null;
        weight = BigDecimal.valueOf(0);
        serviceAddress = null;
    }

    public Product(int productId, String name, BigDecimal weight, String serviceAddress) {
        this.productId = productId;
        this.name = name;
        this.weight = weight;
        this.serviceAddress = serviceAddress;
    }

    public Product(int productId, String name, int weight, String serviceAddress) {
        this.productId = productId;
        this.name = name;
        this.weight = BigDecimal.valueOf(weight);
        this.serviceAddress = serviceAddress;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
}
