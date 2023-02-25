package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CartController cartController;

    @Test
    public void testRightAddToCart() {
        ModifyCartRequest modifyCartRequest = generateCartRequest();
        User user = generateUser();
        Item item =generateItem();

        when(userRepository.findByUsername(modifyCartRequest.getUsername())).thenReturn(user);
        when(itemRepository.findById(0L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);
        Cart cart = responseEntity.getBody();
        BigDecimal total = item.getPrice().add(item.getPrice()).add(item.getPrice());
        Assert.assertEquals(total, cart.getTotal());
        Assert.assertEquals(user.getUsername(), cart.getUser().getUsername());
        Assert.assertEquals(item, cart.getItems().get(0));
        Assert.assertEquals(item, cart.getItems().get(1));
        Assert.assertEquals(item, cart.getItems().get(2));
    }

    @Test
    public void testRemoveFromCart() {
        ModifyCartRequest modifyCartRequest = generateCartRequest();
        User user = generateUser();
        Item item =generateItem();
        user.getCart().addItem(item);
        user.getCart().addItem(item);
        user.getCart().addItem(item);
        modifyCartRequest.setQuantity(1);

        when(userRepository.findByUsername(modifyCartRequest.getUsername())).thenReturn(user);
        when(itemRepository.findById(0L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);
        Cart cart = responseEntity.getBody();

        BigDecimal total = item.getPrice().add(item.getPrice());
        Assert.assertEquals(user.getUsername(), cart.getUser().getUsername());
        Assert.assertEquals(2, cart.getItems().size());
        Assert.assertEquals(item, cart.getItems().get(0));
        Assert.assertEquals(item, cart.getItems().get(1));
    }




    private ModifyCartRequest generateCartRequest() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(0);
        modifyCartRequest.setQuantity(3);
        modifyCartRequest.setUsername("username");
        return modifyCartRequest;
    }

    private Item generateItem() {
        Item item = new Item();
        item.setDescription("Description for an item.");
        item.setId(1L);
        item.setPrice(new BigDecimal(111.11));
        item.setName("itemName");
        return item;
    }

    private User generateUser() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        Cart cart =new Cart();
        cart.setUser(user);
        user.setCart(cart);
        return user;
    }
}
