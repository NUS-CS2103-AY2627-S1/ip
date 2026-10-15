package nerrad.loan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests loan-list operations used by the chatbot's loan commands.
 */
class LoanListTest {
    @Test
    void addAndRemove_loans_keepExpectedOrderAndExposeReadOnlyView() {
        Loan firstLoan = new Loan(LoanType.LENT, "Alex", new BigDecimal("10"), "lunch");
        Loan secondLoan = new Loan(LoanType.BORROWED, "Ben", new BigDecimal("5"), "bus");
        LoanList loanList = new LoanList();

        loanList.add(firstLoan);
        loanList.add(secondLoan);

        assertEquals(List.of(firstLoan, secondLoan), loanList.getLoans());
        assertEquals(firstLoan, loanList.remove(0));
        assertEquals(List.of(secondLoan), loanList.getLoans());
        assertThrows(UnsupportedOperationException.class, loanList.getLoans()::clear);
    }

    @Test
    void constructorAndAdd_nullLoan_assertionErrorAndInputListIsCopied() {
        Loan loan = new Loan(LoanType.LENT, "Alex", new BigDecimal("10"), "lunch");
        List<Loan> originalLoans = new ArrayList<>(List.of(loan));
        LoanList loanList = new LoanList(originalLoans);
        originalLoans.clear();

        assertEquals(1, loanList.size());
        assertThrows(AssertionError.class, () -> loanList.add(null));
    }
}
