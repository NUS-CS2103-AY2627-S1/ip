package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/** Tests expense parsing, validation, and formatting. */
class ExpenseTest {
    @Test
    void createFromCommand_validAmount_createsExpense() throws InvalidCommandException {
        Expense expense = Expense.createFromCommand(
                new String[] {"expense", "bus", "fare", "/amount", "2.5"});

        assertEquals(new BigDecimal("2.50"), expense.getAmount());
        assertEquals("[$] bus fare ($2.50)", expense.toString());
        assertEquals("expense bus fare /amount 2.50", expense.toStorageString());
    }

    @Test
    void createFromCommand_integerAmount_addsTwoDecimalPlaces() throws InvalidCommandException {
        Expense expense = Expense.createFromCommand(
                new String[] {"expense", "ticket", "/amount", "2"});

        assertEquals(new BigDecimal("2.00"), expense.getAmount());
        assertEquals("expense ticket /amount 2.00", expense.toStorageString());
    }

    @Test
    void createFromCommand_nonNumericOrOverPreciseAmount_throwsHelpfulException() {
        String[] invalidAmounts = {"abc", "1.234", "1.230"};

        for (String amount : invalidAmounts) {
            InvalidCommandException exception = assertThrows(InvalidCommandException.class, () ->
                    Expense.createFromCommand(
                            new String[] {"expense", "lunch", "/amount", amount}));
            assertEquals("The expense amount must be a number with at most two decimal places.",
                    exception.getMessage());
        }
    }

    @Test
    void createFromCommand_nonPositiveAmount_throwsHelpfulException() {
        String[] invalidAmounts = {"0", "-1", "-1.20"};

        for (String amount : invalidAmounts) {
            InvalidCommandException exception = assertThrows(InvalidCommandException.class, () ->
                    Expense.createFromCommand(
                            new String[] {"expense", "lunch", "/amount", amount}));
            assertEquals("The expense amount must be greater than zero.", exception.getMessage());
        }
    }

    @Test
    void createFromCommand_nonstandardAmountSyntax_throwsHelpfulException() {
        String[] invalidAmounts = {"1e2", "1E2", "+2", ".50", "2.", "1_000", "$2"};

        for (String amount : invalidAmounts) {
            InvalidCommandException exception = assertThrows(InvalidCommandException.class, () ->
                    Expense.createFromCommand(
                            new String[] {"expense", "lunch", "/amount", amount}));
            assertEquals("The expense amount must be a number with at most two decimal places.",
                    exception.getMessage());
        }
    }

    @Test
    void createFromCommand_missingDescription_throwsException() {
        InvalidCommandException exception = assertThrows(InvalidCommandException.class, () ->
                Expense.createFromCommand(new String[] {"expense", "/amount", "4.50"}));

        assertEquals("Use: " + Expense.USAGE, exception.getMessage());
    }

    @Test
    void createFromCommand_invalidStructure_throwsUsageException() {
        String[][] invalidCommands = {
            {"expense", "lunch", "4.50"},
            {"expense", "lunch", "/amount"},
            {"expense", "lunch", "/amount", "4.50", "/amount", "5.00"},
            {"expense", "lunch", "/amount", "4.50", "extra"}
        };

        for (String[] words : invalidCommands) {
            InvalidCommandException exception = assertThrows(InvalidCommandException.class, () ->
                    Expense.createFromCommand(words));
            assertEquals("Use: " + Expense.USAGE, exception.getMessage());
        }
    }

    @Test
    void expense_invalidConstructorArguments_throwAssertionError() {
        assertThrows(AssertionError.class, () ->
                new Expense(null, new BigDecimal("1.00")));
        assertThrows(AssertionError.class, () ->
                new Expense(" ", new BigDecimal("1.00")));
        assertThrows(AssertionError.class, () -> new Expense("lunch", null));
        assertThrows(AssertionError.class, () ->
                new Expense("lunch", new BigDecimal("0.00")));
        assertThrows(AssertionError.class, () ->
                new Expense("lunch", new BigDecimal("1.0")));
    }
}
