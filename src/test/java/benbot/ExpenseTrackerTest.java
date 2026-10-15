package benbot;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests expense collection operations and total calculation. */
class ExpenseTrackerTest {
    @Test
    void emptyTracker_returnsEmptyViewsAndZeroTotal() {
        ExpenseTracker tracker = new ExpenseTracker();

        assertEquals(0, tracker.size());
        assertArrayEquals(new String[0], tracker.toNumberedDisplayLines());
        assertEquals(List.of(), tracker.toStorageCommands());
        assertEquals(new BigDecimal("0.00"), tracker.getTotalAmount());
    }

    @Test
    void addAndRemove_expenses_updatesTotal() throws InvalidCommandException {
        ExpenseTracker tracker = new ExpenseTracker();
        tracker.add(Expense.createFromCommand(
                new String[] {"expense", "lunch", "/amount", "12.50"}));
        tracker.add(Expense.createFromCommand(
                new String[] {"expense", "bus", "/amount", "2.40"}));

        assertEquals(2, tracker.size());
        assertArrayEquals(new String[] {"1.[$] lunch ($12.50)", "2.[$] bus ($2.40)"},
                tracker.toNumberedDisplayLines());
        assertEquals(List.of(
                "expense lunch /amount 12.50",
                "expense bus /amount 2.40"), tracker.toStorageCommands());
        assertEquals(new BigDecimal("14.90"), tracker.getTotalAmount());

        tracker.remove(0);

        assertEquals(1, tracker.size());
        assertEquals(new BigDecimal("2.40"), tracker.getTotalAmount());
        assertEquals("1.[$] bus ($2.40)", tracker.toNumberedDisplayLines()[0]);
    }
}
