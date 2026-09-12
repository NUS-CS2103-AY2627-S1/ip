package minweeder.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class LoanTest {

    @Test
    public void toFileString_lentLoan_returnsCorrectFormat() {
        Loan loan = new Loan("Alice", 50.0, LoanType.LENT);

        assertEquals("L | 0 | Alice | LENT | 50.0", loan.toFileString());
    }

    @Test
    public void toFileString_borrowedLoanMarkedDone_returnsCorrectFormat() {
        Loan loan = new Loan("Bob", 12.5, LoanType.BORROWED);
        loan.mark();

        assertEquals("L | 1 | Bob | BORROWED | 12.5", loan.toFileString());
    }

    @Test
    public void toString_lentLoan_returnsDisplayFormat() {
        Loan loan = new Loan("Alice", 50.0, LoanType.LENT);

        assertEquals("[L][ ] Lent $50.00 to Alice", loan.toString());
    }

    @Test
    public void toString_borrowedLoan_returnsDisplayFormat() {
        Loan loan = new Loan("Bob", 12.5, LoanType.BORROWED);

        assertEquals("[L][ ] Borrowed $12.50 from Bob", loan.toString());
    }

    @Test
    public void equals_sameFields_returnsTrue() {
        Loan first = new Loan("Alice", 50.0, LoanType.LENT);
        Loan second = new Loan("Alice", 50.0, LoanType.LENT);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void equals_differentType_returnsFalse() {
        Loan first = new Loan("Alice", 50.0, LoanType.LENT);
        Loan second = new Loan("Alice", 50.0, LoanType.BORROWED);

        assertNotEquals(first, second);
    }

    @Test
    public void equals_differentAmount_returnsFalse() {
        Loan first = new Loan("Alice", 50.0, LoanType.LENT);
        Loan second = new Loan("Alice", 60.0, LoanType.LENT);

        assertNotEquals(first, second);
    }

    @Test
    public void equals_differentTaskType_returnsFalse() {
        Loan loan = new Loan("Alice", 50.0, LoanType.LENT);

        assertNotEquals(loan, new Todo("Alice"));
    }
}
