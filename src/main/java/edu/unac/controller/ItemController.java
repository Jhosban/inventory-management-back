package edu.unac.controller;

import edu.unac.domain.Item;
import edu.unac.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        try {
            Item createdItem = itemService.createItem(item);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable("id") Long id, @RequestBody Item item) {
        try {
            return itemService.updateItem(id, item)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable("id") Long id) {
        try {
            itemService.deleteItem(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
