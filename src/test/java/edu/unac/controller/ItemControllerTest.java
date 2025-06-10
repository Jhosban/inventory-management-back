package edu.unac.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.unac.domain.Item;
import edu.unac.domain.Loan;
import edu.unac.repository.ItemRepository;
import edu.unac.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private  LoanRepository loanRepository;

    @BeforeEach
    void setup() {
        itemRepository.deleteAll();
        loanRepository.deleteAll();
    }

    @Test
    void createItemTest() throws Exception {
        Item item = new Item(null, "Laptop", "Electronics",10, true);

        mockMvc.perform(
                        post("/api/items")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(item))
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void createItemInvalidNameTest() throws Exception {
        Item item = new Item(null, null, "Electronics", 10, true);

        mockMvc.perform(
                post("/api/items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(item))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void getAllItemsTest() throws Exception {
        Item item1 = new Item(null, "Laptop", "Electronics", 10, true);
        Item item2 = new Item(null, "Projector", "Electronics", 5, true);
        itemRepository.save(item1);
        itemRepository.save(item2);

        mockMvc.perform(
                        get("/api/items")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void updateItemTest() throws Exception {
        Item item = new Item(null, "Laptop", "Electronics", 10, true);
        Item savedItem = itemRepository.save(item);

        savedItem.setName("Laptop Pro");

        mockMvc.perform(
                        put("/api/items/" + savedItem.getId())
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(savedItem))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop Pro"));
    }

    @Test
    void updateItemInvalidNameTest() throws Exception {
        Item item = new Item(null, "Laptop", "Electronics", 10, true);
        Item savedItem = itemRepository.save(item);

        savedItem.setName(null);

        mockMvc.perform(
                put("/api/items/" + savedItem.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(savedItem))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void deleteItemTest() throws Exception {
        Item item = new Item(null, "Laptop", "Electronics", 10, true);
        Item savedItem = itemRepository.save(item);

        mockMvc.perform(
                delete("/api/items/" + savedItem.getId())
        ).andExpect(status().isNoContent());

        assertFalse(itemRepository.findById(savedItem.getId()).isPresent());
    }

    @Test
    void deleteItemActiveLoanTest() throws Exception {
        Item item = new Item(null, "Laptop", "Electronics", 10, true);
        Item savedItem = itemRepository.save(item);

        Loan loan = new Loan(null, savedItem.getId(), 1, System.currentTimeMillis(), System.currentTimeMillis() + 86400000, "Jose Pedro", false);
        loanRepository.save(loan);

        mockMvc.perform(
                delete("/api/items/" + savedItem.getId())
        ).andExpect(status().isBadRequest());
    }
}