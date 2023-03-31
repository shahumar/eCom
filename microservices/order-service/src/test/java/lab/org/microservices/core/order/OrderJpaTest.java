package lab.org.microservices.core.order;


import lab.org.api.common.Money;
import lab.org.api.core.order.events.OrderLineItem;
import lab.org.microservices.core.order.domain.Order;
import lab.org.microservices.core.order.domain.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;

@DataJpaTest(properties = {"spring.jpa.hibernate.ddl-auto=update"})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderJpaTest extends MySqlTestBase {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    public void shouldDoSomething() {
        Long orderId = transactionTemplate.execute(ts -> {
            Assertions.assertNotNull(orderRepository);
            long consumerId = -1;
            long restaurantId = -2;
            Money orderTotal = Money.ZERO;
            Order order = new Order(consumerId, restaurantId, Collections.singletonList(new OrderLineItem("samosas", "Samosas", new Money("2.50"), 3)));
            orderRepository.save(order);
            return order.getId();
        });

        transactionTemplate.execute( ts -> {
            Order loadedOrder = orderRepository.findById(orderId).get();
            Assertions.assertNotNull(loadedOrder);
            Assertions.assertEquals(1, loadedOrder.getLineItems().size());
            return null;
        });
    }
}
