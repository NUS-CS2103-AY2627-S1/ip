package nerrad.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import nerrad.loan.Loan;
import nerrad.loan.LoanType;
import nerrad.task.Todo;

/**
 * Tests the user-facing messages assembled by {@link Ui}.
 */
class UiTest {
    private final Ui ui = new Ui();

    @Test
    void getWelcomeAndGoodbyeMessage_displayForestCompanionVoice() {
        assertEquals("Welcome, wanderer. I'm Nerrad - Darren from the other side.\n"
                        + "Let's clear the path ahead, one task at a time.",
                ui.getWelcomeMessage());
        assertEquals("  Safe travels. May your path stay clear.", ui.getGoodbyeMessage());
    }

    @Test
    void getTaskAndLoanListMessages_displayItemsWithOneBasedNumbers() {
        Todo todo = new Todo("read book");
        Loan loan = new Loan(LoanType.BORROWED, "Ben", new BigDecimal("5"), "bus");

        assertEquals("  Here is the path ahead:\n  1.[T][ ] read book",
                ui.getTaskListMessage(List.of(todo)));
        assertEquals("  Here is your loan ledger:\n  1.[BORROWED][OUTSTANDING] Ben: S$5.00 (bus)",
                ui.getLoanListMessage(List.of(loan)));
    }

    @Test
    void getErrorMessage_prefixesUserFacingErrors() {
        assertEquals("  OOPS!!! An example error.", ui.getErrorMessage("An example error."));
    }
}
