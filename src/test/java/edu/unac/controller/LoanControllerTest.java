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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        loanRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void registerLoanTest() throws Exception {
        Item itemSaved = itemRepository.save(new Item(null, "Sillas", "Para oficina", 20, true));
        Loan loan = new Loan(null, itemSaved.getId(),5, System.currentTimeMillis() + 100000, System.currentTimeMillis() + 200000, "Juan Perez", false);

        mockMvc.perform(
                        post("/api/loans")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loan))
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.requestedBy").value("Juan Perez"))
                .andExpect(jsonPath("$.itemId").value(itemSaved.getId().intValue()))
                .andExpect(jsonPath("$.quantity").value(5));
    }

    @Test
    void registerLoanInvalidItemTest() throws Exception {
        Loan loan = new Loan(null, 1L, 5, System.currentTimeMillis() + 100000, System.currentTimeMillis() + 200000, "Juan Perez", false);

        mockMvc.perform(
                post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loan))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void getAllLoansTest() throws Exception {
        Item itemSaved1 = itemRepository.save(new Item(null, "Sillas", "Para oficina", 20, true));
        loanRepository.save(new Loan(null, itemSaved1.getId(), 5, System.currentTimeMillis() + 100000, System.currentTimeMillis() + 200000, "Juan Perez", false));

        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].requestedBy").value("Juan Perez"))
                .andExpect(jsonPath("$[0].itemId").value(itemSaved1.getId().intValue()))
                .andExpect(jsonPath("$[0].quantity").value(5));
    }

    @Test
    void cancelLoanTest() throws Exception {
        Item itemSaved = itemRepository.save(new Item(null, "Sillas", "Para oficina", 20, true));
        Loan loan = loanRepository.save(new Loan(null, itemSaved.getId(), 5, System.currentTimeMillis() + 100000, System.currentTimeMillis() + 200000, "Juan Perez", false));

        mockMvc.perform(
                        put("/api/loans/" + loan.getId() + "/cancel")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loan.getId()))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.requestedBy").value("Juan Perez"))
                .andExpect(jsonPath("$.itemId").value(itemSaved.getId().intValue()))
                .andExpect(jsonPath("$.quantity").value(5));
    }

    @Test
    void cancelLoanNotFoundTest() throws Exception {
        mockMvc.perform(
                put("/api/loans/999/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(999L))
        ).andExpect(status().isNotFound());
    }

    @Test
    void cancelLoanConflictTest1() throws Exception {
        Item itemSaved = itemRepository.save(new Item(null, "Sillas", "Para oficina", 20, true));
        Loan loan = loanRepository.save(new Loan(null, itemSaved.getId(), 5, System.currentTimeMillis() - 100000, System.currentTimeMillis() + 200000, "Juan Perez", false));

        mockMvc.perform(
                put("/api/loans/" + loan.getId() + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loan.getId()))
        ).andExpect(status().isConflict());
    }
}