package minweeder.task;

import java.util.Objects;

/**
 * A record of money lent to, or borrowed from, another person.
 * The task's description (inherited from {@link Task}) holds the other person's name.
 */
public class Loan extends Task {
    private final double amount;
    private final LoanType type;

    /**
     * Creates a loan record.
     *
     * @param person the name of the other person involved in the loan.
     * @param amount the amount of money involved, in dollars.
     * @param type whether the money was lent to, or borrowed from, {@code person}.
     */
    public Loan(String person, double amount, LoanType type) {
        super(person);
        this.amount = amount;
        this.type = type;
    }

    @Override
    public String toFileString() {
        return "L | " + super.toFileFields() + " | " + this.type + " | " + this.amount;
    }

    @Override
    public String toString() {
        String verb = this.type == LoanType.LENT ? "Lent" : "Borrowed";
        String preposition = this.type == LoanType.LENT ? "to" : "from";
        return "[L][" + super.getStatusIcon() + "] " + verb + " $" + String.format("%.2f", this.amount)
                + " " + preposition + " " + super.getDescription();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Loan)) {
            return false;
        }
        Loan otherLoan = (Loan) other;
        return this.getDescription().equals(otherLoan.getDescription())
                && this.amount == otherLoan.amount
                && this.type == otherLoan.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Loan.class, this.getDescription(), this.amount, this.type);
    }
}
