package lab.org.api.core.order.events;


import lab.org.api.common.Money;

import javax.persistence.*;

@Embeddable
public class OrderLineItem {

    public OrderLineItem() {
    }

    private int quantity;
    private String menuItemId;
    private String name;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name="amount", column = @Column(name = "price")))
    private Money price;

    public OrderLineItem(String menuItemId, String name, Money price, int quantity) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Money deltaForChangedQuantity(int newQuantity) {
        return price.multiply(newQuantity - quantity);
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setMenuItemId(String menuItemId) {
        this.menuItemId = menuItemId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getMenuItemId() {
        return menuItemId;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }


    public Money getTotal() {
        return price.multiply(quantity);
    }
}
