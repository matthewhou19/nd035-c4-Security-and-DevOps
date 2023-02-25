package com.example.demo;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderController orderController;

    @Test
    public void testSubmitOrder() {
        User user = generateUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        ResponseEntity<UserOrder> responseEntity = orderController.submit(user.getUsername());
        UserOrder order = responseEntity.getBody();
        Assert.assertEquals(order.getUser().getUsername(), user.getUsername());
        Assert.assertEquals(order.getTotal(), user.getCart().getTotal());
        Assert.assertEquals(order.getItems().get(0),user.getCart().getItems().get(0));
    }

    @Test
    public void testGetOrdersForUser() {
        User user = generateUser();
        List<UserOrder> userOrders = generateOrderList();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(userOrders);

        ResponseEntity<List<UserOrder>> responseEntity =orderController.getOrdersForUser(user.getUsername());
        List<UserOrder> responseOrderList = responseEntity.getBody();
        Assert.assertEquals(responseOrderList.get(0).getUser().getUsername(), user.getUsername());
        Assert.assertEquals(responseOrderList.get(0).getTotal(), userOrders.get(0).getTotal());
    }

    private User generateUser() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        Cart cart =new Cart();
        cart.setUser(user);
        cart.addItem(generateItem());
        cart.addItem(generateItem());
        user.setCart(cart);
        return user;
    }

    private Item generateItem() {
        Item item = new Item();
        item.setDescription("Description for an item.");
        item.setId(1L);
        item.setPrice(new BigDecimal(111.11));
        item.setName("itemName");
        return item;
    }

    private List<UserOrder> generateOrderList() {
        List<UserOrder> userOrders = new ArrayList<>();
        userOrders.add(generateOrder());
        return userOrders;
    }

    private UserOrder generateOrder() {
        UserOrder order = new UserOrder();
        order.setTotal(new BigDecimal(111.11));
        order.setUser(generateUser());
        return order;
    }

}
