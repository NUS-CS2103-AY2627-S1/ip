package benbot;

import java.math.BigDecimal;
import java.util.List;

/** Manages expenses and calculates the total amount spent. */
final class ExpenseTracker {
    /** A zero monetary amount represented with two decimal places. */
    private static final BigDecimal ZERO_AMOUNT = new BigDecimal("0.00");

    /** The expenses in the order in which the user added them. */
    private final EntityList<Expense> expenses = new EntityList<>();

    /** Adds an expense to this tracker. */
    void add(Expense expense) {
        expenses.add(expense);
    }

    /** Removes and returns the expense at the supplied zero-based index. */
    Expense remove(int index) {
        return expenses.remove(index);
    }

    /** Returns the number of expenses in this tracker. */
    int size() {
        return expenses.size();
    }

    /** Returns display lines numbered from one in expense order. */
    String[] toNumberedDisplayLines() {
        return expenses.toNumberedDisplayLines();
    }

    /** Returns commands that recreate every expense in order. */
    List<String> toStorageCommands() {
        return expenses.toStorageCommands();
    }

    /** Returns the sum of all tracked expense amounts. */
    BigDecimal getTotalAmount() {
        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(ZERO_AMOUNT, BigDecimal::add);
    }
}
