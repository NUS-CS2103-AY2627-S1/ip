package nerrad.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import nerrad.NerradException;
import nerrad.loan.Loan;
import nerrad.loan.LoanType;
import nerrad.task.Deadline;
import nerrad.task.Event;
import nerrad.task.Todo;

/**
 * Tests command-detail parsing performed by {@link Parser}.
 */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseTaskIndex_validTaskNumbers_returnsZeroBasedIndexes() throws NerradException {
        assertEquals(0, parser.parseTaskIndex("1", 3, "mark"));
        assertEquals(2, parser.parseTaskIndex(" 3 ", 3, "mark"));
    }

    @Test
    void parseTaskIndex_missingTaskNumber_throwsHelpfulException() {
        NerradException exception = assertThrows(NerradException.class,
                () -> parser.parseTaskIndex("  ", 3, "mark"));

        assertEquals("Please provide a task number to mark.", exception.getMessage());
    }

    @Test
    void parseTaskIndex_nonNumericTaskNumber_throwsHelpfulException() {
        NerradException exception = assertThrows(NerradException.class,
                () -> parser.parseTaskIndex("two", 3, "mark"));

        assertEquals("The task number must be a whole number.", exception.getMessage());
    }

    @Test
    void parseTaskIndex_taskNumberBelowRange_throwsHelpfulException() {
        NerradException exception = assertThrows(NerradException.class,
                () -> parser.parseTaskIndex("0", 3, "mark"));

        assertEquals("There is no task with this number.", exception.getMessage());
    }

    @Test
    void parseTaskIndex_taskNumberAboveRange_throwsHelpfulException() {
        NerradException exception = assertThrows(NerradException.class,
                () -> parser.parseTaskIndex("4", 3, "mark"));

        assertEquals("There is no task with this number.", exception.getMessage());
    }

    @Test
    void parseTask_todoCommand_returnsTodoWithTrimmedDescription() throws NerradException {
        Todo todo = assertInstanceOf(Todo.class, parser.parseTask("todo   read book  "));

        assertEquals("read book", todo.getDescription());
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    void parseTask_deadlineCommand_returnsDeadlineWithParsedDate() throws NerradException {
        Deadline deadline = assertInstanceOf(Deadline.class,
                parser.parseTask("deadline return book /by 2019-12-02"));

        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2019, 12, 2), deadline.getBy());
        assertEquals("[D][ ] return book (by: Dec 02 2019)", deadline.toString());
    }

    @Test
    void parseTask_eventCommand_returnsEventWithStartAndEndDetails() throws NerradException {
        Event event = assertInstanceOf(Event.class,
                parser.parseTask("event project meeting /from Mon 2pm /to 4pm"));

        assertEquals("project meeting", event.getDescription());
        assertEquals("Mon 2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
    }

    @Test
    void parseTask_todoWithoutDescription_throwsHelpfulException() {
        assertTaskParseError("todo", "The description of a todo cannot be empty.");
    }

    @Test
    void parseTask_invalidDeadlineDetails_throwHelpfulExceptions() {
        assertTaskParseError("deadline return book", "A deadline needs a /by date or time.");
        assertTaskParseError("deadline /by 2019-12-02",
                "The description of a deadline cannot be empty.");
        assertTaskParseError("deadline return book /by",
                "The /by date or time of a deadline cannot be empty.");
        assertTaskParseError("deadline return book /by tomorrow",
                "Please use the date format yyyy-MM-dd.");
    }

    @Test
    void parseTask_invalidEventDetails_throwHelpfulExceptions() {
        assertTaskParseError("event project meeting /to 4pm",
                "An event needs a /from start date or time.");
        assertTaskParseError("event project meeting /from Mon 2pm",
                "An event needs a /to end date or time.");
        assertTaskParseError("event project meeting /to 4pm /from Mon 2pm",
                "The /to end date or time must come after /from.");
        assertTaskParseError("event /from Mon 2pm /to 4pm",
                "The description of an event cannot be empty.");
        assertTaskParseError("event project meeting /from /to 4pm",
                "The /from start date or time of an event cannot be empty.");
        assertTaskParseError("event project meeting /from Mon 2pm /to",
                "The /to end date or time of an event cannot be empty.");
    }

    @Test
    void parseTask_unknownCommand_throwsHelpfulException() {
        assertTaskParseError("blah", "I'm sorry, but I don't know what that means :-(");
    }

    @Test
    void parseLoan_validCommands_returnsLoansWithAllDetails() throws NerradException {
        Loan lentLoan = parser.parseLoan("loan lend Alex Tan 12.50 /for lunch");
        Loan borrowedLoan = parser.parseLoan("loan borrow Ben 5");

        assertEquals(LoanType.LENT, lentLoan.getType());
        assertEquals("Alex Tan", lentLoan.getPerson());
        assertEquals(new BigDecimal("12.50"), lentLoan.getAmount());
        assertEquals("lunch", lentLoan.getReason());
        assertEquals(LoanType.BORROWED, borrowedLoan.getType());
        assertEquals("", borrowedLoan.getReason());
    }

    @Test
    void parseLoan_invalidDetails_throwsHelpfulExceptions() {
        assertLoanParseError("loan", "A loan needs a person's name and an amount.");
        assertLoanParseError("loan give Alex 10", "Use loan lend or loan borrow.");
        assertLoanParseError("loan lend Alex", "A loan needs a person's name and an amount.");
        String invalidAmountMessage = "The loan amount must be positive with at most two decimal places.";
        assertLoanParseError("loan lend Alex 0", invalidAmountMessage);
        assertLoanParseError("loan borrow Ben 1.999", invalidAmountMessage);
        assertLoanParseError("loan lend Alex 10 /for", "The reason for a loan cannot be empty.");
    }

    @Test
    void parseLoanIndex_validAndInvalidLoanNumbers_returnIndexesOrHelpfulErrors() throws NerradException {
        assertEquals(0, parser.parseLoanIndex("1", 2));
        assertEquals(1, parser.parseLoanIndex(" 2 ", 2));
        assertLoanIndexParseError("", 2, "Please provide a loan number to settle.");
        assertLoanIndexParseError("one", 2, "The loan number must be a whole number.");
        assertLoanIndexParseError("0", 2, "There is no loan with this number.");
        assertLoanIndexParseError("3", 2, "There is no loan with this number.");
    }

    /**
     * Verifies that an invalid task command produces the expected explanation.
     *
     * @param input command to parse
     * @param expectedMessage expected error explanation
     */
    private void assertTaskParseError(String input, String expectedMessage) {
        NerradException exception = assertThrows(NerradException.class, () -> parser.parseTask(input));

        assertEquals(expectedMessage, exception.getMessage());
    }

    /**
     * Verifies that an invalid loan command produces the expected explanation.
     *
     * @param input Command to parse.
     * @param expectedMessage Expected error explanation.
     */
    private void assertLoanParseError(String input, String expectedMessage) {
        NerradException exception = assertThrows(NerradException.class, () -> parser.parseLoan(input));

        assertEquals(expectedMessage, exception.getMessage());
    }

    /**
     * Verifies that an invalid loan number produces the expected explanation.
     *
     * @param loanNumberText Loan number to parse.
     * @param loanCount Number of loans available.
     * @param expectedMessage Expected error explanation.
     */
    private void assertLoanIndexParseError(String loanNumberText, int loanCount, String expectedMessage) {
        NerradException exception = assertThrows(NerradException.class,
                () -> parser.parseLoanIndex(loanNumberText, loanCount));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
