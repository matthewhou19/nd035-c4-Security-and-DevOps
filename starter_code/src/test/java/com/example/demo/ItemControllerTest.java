package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemController itemController;

    @Test
    public void testGetItems(){
        Item item = generateItem();
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        when(itemRepository.findAll()).thenReturn(itemList);
        ResponseEntity<List<Item>> responseEntity = itemController.getItems();
        List<Item>itemListResponse = responseEntity.getBody();
        assertItem(itemList.get(0), itemListResponse.get(0));
    }

    @Test
    public void testRightGetItemById() {
        Item item = generateItem();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        ResponseEntity<Item> responseEntity = itemController.getItemById(1L);
        Item responseItem = responseEntity.getBody();
        assertItem(item, responseItem);
    }

    @Test
    public void testBadGetItemById() {
        ResponseEntity<Item> responseEntity = itemController.getItemById(1L);
        Assert.assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void testRightGetItemByName() {
        Item item = generateItem();
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        when(itemRepository.findByName(item.getName())).thenReturn(itemList);
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName(item.getName());
        List<Item>itemListResponse = responseEntity.getBody();
        assertItem(itemList.get(0), itemListResponse.get(0));
    }

    @Test
    public void testBadGetItemByName() {
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("itemName");
        Assert.assertEquals(404, responseEntity.getStatusCodeValue());
    }

    private Item generateItem() {
        Item item = new Item();
        item.setDescription("Description for an item.");
        item.setId(1L);
        item.setPrice(new BigDecimal(111.11));
        item.setName("itemName");
        return item;
    }

    private void assertItem(Item item1, Item item2) {
        Assert.assertEquals(item1.getName(), item2.getName());
        Assert.assertEquals(item1.getPrice(), item2.getPrice());
        Assert.assertEquals(item1.getDescription(), item2.getDescription());
    }
}
