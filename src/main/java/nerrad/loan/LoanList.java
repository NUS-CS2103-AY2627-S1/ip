package nerrad.loan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Maintains the loan records managed by Nerrad.
 */
public class LoanList {
    private final List<Loan> loans;

    /** Creates an empty loan list. */
    public LoanList() {
        this.loans = new ArrayList<>();
    }

    /**
     * Creates a loan list containing copies of the supplied references.
     *
     * @param loans Loans to add initially.
     */
    public LoanList(List<Loan> loans) {
        this.loans = new ArrayList<>(loans);
    }

    /**
     * Adds a loan to the end of the list.
     *
     * @param loan Loan to add.
     */
    public void add(Loan loan) {
        assert loan != null : "loan must not be null";
        loans.add(loan);
    }

    /**
     * Removes and returns the loan at an index.
     *
     * @param index Zero-based loan index.
     * @return Removed loan.
     */
    public Loan remove(int index) {
        return loans.remove(index);
    }

    /**
     * Returns the loan at an index.
     *
     * @param index Zero-based loan index.
     * @return Requested loan.
     */
    public Loan get(int index) {
        return loans.get(index);
    }

    /**
     * Returns the number of loan records.
     *
     * @return Number of loans.
     */
    public int size() {
        return loans.size();
    }

    /**
     * Returns an unmodifiable view of the loan records.
     *
     * @return Loans in insertion order.
     */
    public List<Loan> getLoans() {
        return Collections.unmodifiableList(loans);
    }
}
