package edu.unac.repository;

import edu.unac.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository  extends JpaRepository<Loan, Long> {
    List<Loan> findByItemId(Long ItemId);
    List<Loan> findByItemIdAndStartDateBetween(Long itemId, Long startTimestamp, Long endTimestamp);

}
