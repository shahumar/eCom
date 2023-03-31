package lab.org.microservices.core.order.domain;


import io.eventuate.tram.events.common.DomainEvent;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "order_service_restaurants")
@Access(AccessType.FIELD)
public class Restaurant {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    @ElementCollection
    @CollectionTable(name = "order_service_restaurant_menu_items")
    private List<MenuItem> menuItems;

    public Restaurant() {
    }

    public Restaurant(long id, List<MenuItem> menuItems) {
        this.id = id;
        this.menuItems = menuItems;
    }

    public List<DomainEvent> reviseMenu(RestaurantMenu revisedMenu) {
        throw new UnsupportedOperationException();
    }

    public void verifyRestaurantDetails(RestaurantOrderDetails restaurantOrderDetails) {
        // TODO - implement me
    }

    public Long getId() {
        return id;
    }

    public Optional<MenuItem> findMenuItem(String menuItemId) {
        return menuItems.stream().filter(mi -> mi.getId().equals(menuItemId)).findFirst();
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

}
