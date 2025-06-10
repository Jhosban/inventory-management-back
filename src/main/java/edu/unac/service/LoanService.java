package edu.unac.service;

import edu.unac.domain.Item;
import edu.unac.domain.Loan;
import edu.unac.repository.ItemRepository;
import edu.unac.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanService {
  @Autowired
    private LoanRepository loanRepository;
  @Autowired
  private ItemRepository itemRepository;

  public LoanService(LoanRepository loanRepository, ItemRepository itemRepository) {
        this.loanRepository = loanRepository;
        this.itemRepository = itemRepository;
  }

  public  Loan registerLoan(Loan loan) {
    Item item = itemRepository.findById(loan.getItemId()).orElseThrow(() ->
            new IllegalArgumentException("Item not found"));

    if(!item.getIsAvailable()) {
      throw new IllegalStateException("Item is not available for loan");
    }

    if (loan.getQuantity() < 1) {
      throw new IllegalArgumentException("Quantity must be at least 1");
    }

    if (loan.getStartDate() <= System.currentTimeMillis()) {
      throw new IllegalArgumentException("Start date must be in the future");
    }

    if (loan.getEndDate() <= loan.getStartDate()) {
      throw new IllegalArgumentException("End date must be after start date");
    }

    int availableItems = calculateAvalibleItems(loan.getItemId(), loan.getStartDate(), loan.getEndDate());
    if (availableItems < loan.getQuantity()) {
      throw new IllegalStateException("Not enough available items for the requested quantity");
    }

    item.setIsAvailable(false);
    itemRepository.save(item);
    return loanRepository.save(loan);
  }

  public Loan cancelLoan (Long loanId){
    Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

    Item item = itemRepository.findById(loan.getItemId())
            .orElseThrow(() -> new IllegalArgumentException("Item not found"));

    if (loan.getStartDate() <= System.currentTimeMillis()){
        throw new IllegalStateException("Loan cannot be cancelled after it has started");
    }

    if(loan.isCancelled()) {
      throw new IllegalStateException("Loan is already cancelled");
    }

    item.setIsAvailable(true);
    itemRepository.save(item);
    loan.setCancelled(true);
    return loanRepository.save(loan);
  }

  public List<Loan> getAllLoans() {
            return loanRepository.findAll();
  }

  public int calculateAvalibleItems(Long itemId, Long startDate, Long endDate) {
    Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item not found"));

    int availableItems = item.getTotalQuantity();

    List<Loan> loans = loanRepository.findByItemIdAndStartDateBetween(itemId, startDate, endDate);

    int reservedItems = loans.stream()
            .mapToInt(Loan::getQuantity)
            .sum();

    return availableItems - reservedItems;
  }
}
