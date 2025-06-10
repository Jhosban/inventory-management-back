package edu.unac.service;

import edu.unac.domain.Item;
import edu.unac.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ItemServiceTest {
    ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
    }

    @Test
    void createItemValidTest() {
        Item item = new Item(null, "Laptop", "Electronics", 10, true);

        when(itemRepository.findByName("Laptop")).thenReturn(Optional.empty());
        when(itemRepository.save(item)).thenReturn(new Item(1L, "Laptop", "Electronics", 10, true));

        ItemService itemService = new ItemService(itemRepository);
        Item createdItem = itemService.createItem(item);

        assertEquals("Laptop", createdItem.getName());
        assertEquals(10, createdItem.getTotalQuantity());
    }

    @Test
    void createItemInvalidNameTest() {
        Item item1 = new Item(null, "La", "Electronics", 10, true);
        Item item2 = new Item(null, null, "Electronics", 10, true);

        ItemService itemService = new ItemService(itemRepository);

        assertThrows(IllegalArgumentException.class,
                () -> itemService.createItem(item1)
        );

        assertThrows(IllegalArgumentException.class,
                () -> itemService.createItem(item2)
        );
    }

    @Test
    void createItemQuantityZeroTest() {
        Item item = new Item(null, "Laptop", "Electronics", 0, true);

        ItemService itemService = new ItemService(itemRepository);

        assertThrows(IllegalArgumentException.class,
                () -> itemService.createItem(item));

    }

    @Test
    void createItemDuplicateNameTest() {
        Item item = new Item(null, "Laptop", "Electronics", 10, true);

        when(itemRepository.findByName("Laptop")).thenReturn(Optional.of(item));

        ItemService itemService = new ItemService(itemRepository);

        assertThrows(IllegalArgumentException.class,
                () -> itemService.createItem(item));
    }

    @Test
    void getAllItemsTest() {
        Item item1 = new Item(1L, "Laptop", "Electronics", 10, true);
        Item item2 = new Item(2L, "Tablet", "Electronics", 5, true);

        when(itemRepository.findAll()).thenReturn(List.of(item1, item2));

        ItemService itemService = new ItemService(itemRepository);
        List<Item> items = itemService.getAllItems();

        assertEquals(2, items.size());
        assertTrue(items.contains(item1));
        assertTrue(items.contains(item2));
    }

    @Test
    void updateItemValidTest() {
        Item existingItem = new Item(1L, "Laptop", "Electronics", 10, true);
        Item updatedItem = new Item(null, "Gaming Laptop", "Electronics", 15, true);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(new Item(1L, "Gaming Laptop", "Electronics", 15, true));

        ItemService itemService = new ItemService(itemRepository);
        Optional<Item> result = itemService.updateItem(1L, updatedItem);

        assertTrue(result.isPresent());
        assertEquals("Gaming Laptop", result.get().getName());
        assertEquals(15, result.get().getTotalQuantity());
    }
}