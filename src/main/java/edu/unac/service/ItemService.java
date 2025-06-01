package edu.unac.service;

import edu.unac.domain.Item;
import edu.unac.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item createItem(Item item) {
        if (item.getName() == null || item.getName().length() < 3) throw new IllegalArgumentException("Item name must be at least 3 characters long");

        if (item.getTotalQuantity() < 1) throw new IllegalArgumentException("Total quantity must be greater than zero");

        return itemRepository.save(item);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> updateItem(Long id, Item item) {
        if (item.getName() == null || item.getName().length() < 3) throw new IllegalArgumentException("Item name must be at least 3 characters long");
        if (item.getTotalQuantity() < 1) throw new IllegalArgumentException("Total quantity must be greater than zero");

        return itemRepository.findById(id).map(existingItem -> {
            existingItem.setName(item.getName());
            existingItem.setDescription(item.getDescription());
            existingItem.setTotalQuantity(item.getTotalQuantity());

            return itemRepository.save(existingItem);
        });
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}
