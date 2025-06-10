package edu.unac.service;

import edu.unac.domain.Item;
import edu.unac.domain.Loan;
import edu.unac.repository.ItemRepository;
import edu.unac.repository.LoanRepository;
import io.cucumber.java.nl.Stel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoanServiceTest {

    ItemRepository itemRepository;
    LoanRepository loanRepository;

    @BeforeEach
    void setUp(){
        itemRepository = mock(ItemRepository.class);
        loanRepository = mock(LoanRepository.class);
    }

    @Test
    void registerLoanSavedTest() {
        Item item = new Item (1L, "Sillas", "Eventos", 20, true);
        Loan loan = new Loan(null, item.getId(), 5, System.currentTimeMillis() + 60480000L, System.currentTimeMillis() + 604800000L, "Juan Perez", false);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(new Item(item.getId(), item.getName(), item.getDescription(), item.getTotalQuantity(), false));
        when(loanRepository.save(loan)).thenReturn(new Loan(1L, item.getId(), loan.getQuantity(), loan.getStartDate(), loan.getEndDate(), loan.getRequestedBy(), loan.isCancelled()));

        LoanService loanService = new LoanService(loanRepository, itemRepository);
        Loan registeredLoan = loanService.registerLoan(loan);

        assertEquals("Juan Perez", registeredLoan.getRequestedBy());
    }

    @Test
    void registerLoanItemNotFoundTest() {
        Loan loan = new Loan(null, 1L, 5, System.currentTimeMillis() + 60480000L, System.currentTimeMillis() + 604800000L, "Juan Perez", false);

        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        LoanService loanService = new LoanService(loanRepository, itemRepository);

        assertThrows(IllegalArgumentException.class,
                () -> loanService.registerLoan(loan));
    }

    @Test
    void registerLoanItemNotAvailableTest() {
        Item item = new Item(1L, "Sillas", "Eventos", 20, false);
        Loan loan = new Loan(null, item.getId(), 5, System.currentTimeMillis() + 60480000L, System.currentTimeMillis() + 604800000L, "Juan Perez", false);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        LoanService loanService = new LoanService(loanRepository, itemRepository);

        assertThrows(IllegalStateException.class,
                () -> loanService.registerLoan(loan));
    }

    @Test
    void quantityLessThanOneTest() {
        Item item = new Item(1L, "Sillas", "Eventos", 20, true);
        Loan loan = new Loan(null, item.getId(), 0, System.currentTimeMillis() + 60480000L, System.currentTimeMillis() + 604800000L, "Juan Perez", false);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        LoanService loanService = new LoanService(loanRepository, itemRepository);

        assertThrows(IllegalArgumentException.class,
                () -> loanService.registerLoan(loan));
    }

    @Test
    void endDateBeforeStartDateTest() {
        Item item = new Item(1L, "Sillas", "Eventos", 20, true);
        Loan loan = new Loan(null, item.getId(), 5, System.currentTimeMillis() + 604800000L, System.currentTimeMillis() + 60480000L, "Juan Perez", false);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        LoanService loanService = new LoanService(loanRepository, itemRepository);

        assertThrows(IllegalArgumentException.class,
                () -> loanService.registerLoan(loan));
    }

    @Test
    void startDateInThePastTest() {
        Item item = new Item(1L, "Sillas", "Eventos", 20, true);
        Loan loan = new Loan(null, item.getId(), 5, System.currentTimeMillis() - 1000L, System.currentTimeMillis() + 604800000L, "Juan Perez", false);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        LoanService loanService = new LoanService(loanRepository, itemRepository);

        assertThrows(IllegalArgumentException.class,
                () -> loanService.registerLoan(loan));
    }

    @Test
    void availableItemsLessThanRequestedTest() {
        Item item = new Item(1L, "Sillas", "Eventos", 20, true);
        Loan loan = new Loan(null, item.getId(), 25, System.currentTimeMillis() + 60480000L, System.currentTimeMillis() + 604800000L, "Juan Perez", false);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        LoanService loanService = new LoanService(loanRepository, itemRepository);

        assertThrows(IllegalStateException.class,
                () -> loanService.registerLoan(loan));
    }

    @Test
    void cancelLoanTest() {
        Item item = new Item(1L, "Sillas", "Eventos", 20, false);
        Loan loan = new Loan(1L, item.getId(), 5, System.currentTimeMillis() + 60480000L, System.currentTimeMillis() + 604800000L, "Juan Perez", false);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(new Item(item.getId(), item.getName(), item.getDescription(), item.getTotalQuantity(), true));
        when(loanRepository.save(loan)).thenReturn(new Loan(1L, item.getId(), loan.getQuantity(), loan.getStartDate(), loan.getEndDate(), loan.getRequestedBy(), true));

        LoanService loanService = new LoanService(loanRepository, itemRepository);
        Loan cancelledLoan = loanService.cancelLoan(1L);

        assertEquals(1L, cancelledLoan.getId());
        assertEquals(true, item.getIsAvailable());
    }

    @Test
    void cancelLoanNotFoundTest() {
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        LoanService loanService = new LoanService(loanRepository, itemRepository);

        assertThrows(IllegalArgumentException.class,
                () -> loanService.cancelLoan(1L));
    }

    @Test
    void cancelLoanAlreadyStartedTest() {
        Item item = new Item(1L, "Sillas", "Eventos", 20, false);
        Loan loan = new Loan(1L, item.getId(), 5, System.currentTimeMillis() - 1000L, System.currentTimeMillis() + 604800000L, "Juan Perez", false);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        LoanService loanService = new LoanService(loanRepository, itemRepository);

        assertThrows(IllegalStateException.class,
                () -> loanService.cancelLoan(1L));
    }

    @Test
    void cancelLoanAlreadyCancelledTest() {
        Item item = new Item(1L, "Sillas", "Eventos", 20, false);
        Loan loan = new Loan(1L, item.getId(), 5, System.currentTimeMillis() + 60480000L, System.currentTimeMillis() + 604800000L, "Juan Perez", true);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        LoanService loanService = new LoanService(loanRepository, itemRepository);

        assertThrows(IllegalArgumentException.class,
                () -> loanService.cancelLoan(1L));
    }

    @Test
    void getAllLoans(){
        Loan loan1 = new Loan(1L, 1L, 5, System.currentTimeMillis() + 60480000L, System.currentTimeMillis() + 604800000L, "Juan Perez", false);
        Loan loan2 = new Loan(2L, 2L, 3, System.currentTimeMillis() + 60480000L, System.currentTimeMillis() + 604800000L, "Maria Lopez", false);

        when(loanRepository.findAll()).thenReturn(Arrays.asList(loan1, loan2));

        LoanService loanService = new LoanService(loanRepository, itemRepository);
        List<Loan> loans = loanService.getAllLoans();

        assertEquals(2, loans.size());
        assertEquals("Juan Perez", loans.get(0).getRequestedBy());
        assertEquals("Maria Lopez", loans.get(1).getRequestedBy());
    }



}