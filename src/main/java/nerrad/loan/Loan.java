package nerrad.loan;

import java.math.BigDecimal;

/**
 * Represents money lent to or borrowed from a person.
 */
public class Loan {
    private final LoanType type;
    private final String person;
    private final BigDecimal amount;
    private final String reason;
    private boolean isSettled;

    /**
     * Creates an outstanding loan record.
     *
     * @param type Direction of the loan.
     * @param person Person involved in the loan.
     * @param amount Amount of money involved.
     * @param reason Optional reason for the loan.
     */
    public Loan(LoanType type, String person, BigDecimal amount, String reason) {
        this.type = type;
        this.person = person;
        this.amount = amount;
        this.reason = reason;
        this.isSettled = false;
    }

    /**
     * Returns the direction of the loan.
     *
     * @return Whether money was lent or borrowed.
     */
    public LoanType getType() {
        return type;
    }

    /**
     * Returns the person involved in the loan.
     *
     * @return Name of the person.
     */
    public String getPerson() {
        return person;
    }

    /**
     * Returns the amount recorded for the loan.
     *
     * @return Monetary amount.
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Returns the optional reason recorded for the loan.
     *
     * @return Loan reason, or an empty string.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Returns whether the loan has been settled.
     *
     * @return Whether no money remains outstanding.
     */
    public boolean isSettled() {
        return isSettled;
    }

    /** Marks this loan as settled. */
    public void markAsSettled() {
        isSettled = true;
    }

    /** Marks this loan as outstanding. */
    public void markAsOutstanding() {
        isSettled = false;
    }

    /**
     * Returns the loan in its user-facing display format.
     *
     * @return Loan type, settlement status, person, amount, and optional reason.
     */
    @Override
    public String toString() {
        String typeIcon = type == LoanType.LENT ? "LENT" : "BORROWED";
        String statusIcon = isSettled ? "SETTLED" : "OUTSTANDING";
        String reasonText = reason.isEmpty() ? "" : " (" + reason + ")";
        return "[" + typeIcon + "][" + statusIcon + "] " + person
                + ": S$" + amount.setScale(2) + reasonText;
    }
}
