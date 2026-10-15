package nerrad.loan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/**
 * Tests the display and settlement state of {@link Loan} records.
 */
class LoanTest {
    @Test
    void toString_andSettlementState_displayLoanDetails() {
        Loan loan = new Loan(LoanType.LENT, "Alex Tan", new BigDecimal("12.5"), "lunch");

        assertFalse(loan.isSettled());
        assertEquals("[LENT][OUTSTANDING] Alex Tan: S$12.50 (lunch)", loan.toString());
        loan.markAsSettled();
        assertTrue(loan.isSettled());
        assertEquals("[LENT][SETTLED] Alex Tan: S$12.50 (lunch)", loan.toString());
    }
}
