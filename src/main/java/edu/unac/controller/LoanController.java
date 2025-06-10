package edu.unac.controller;

import edu.unac.domain.Loan;
import edu.unac.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "*")
public class LoanController {

private final LoanService loanService;

    public LoanController(LoanService loanService) {
            this.loanService = loanService;
    }

    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @PostMapping
    public ResponseEntity<Loan> registerLoan(@RequestBody Loan loan) {
        try {
            Loan created = loanService.registerLoan(loan);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.print("Error registering loan: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Loan> cancelLoan(@RequestBody Long loanId) {
        try {
            Loan cancelled = loanService.cancelLoan(loanId);
            return ResponseEntity.ok(cancelled);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            // Loan cannot be cancelled after it has started
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    @GetMapping("/id/{itemId}")
    public ResponseEntity<List<Loan>> getLoansByItemId(@PathVariable("itemId") Long itemId) {
        List<Loan> loans = loanService.getLoansByItemId(itemId);
        if (loans.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(loans);
    }

}
