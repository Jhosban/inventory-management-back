package edu.unac.service;

import edu.unac.repository.ItemRepository;
import edu.unac.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

class LoanServiceTest {

    ItemRepository itemRepository;
    LoanRepository loanRepository;

    @BeforeEach
    void setUp(){
        itemRepository = mock(ItemRepository.class);
        loanRepository = mock(LoanRepository.class);
    }

    @Test
    void registerLoanSavedTest() {}

}