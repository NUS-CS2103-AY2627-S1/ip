package auto.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests every user-facing exception factory and its contextual message. */
class AutoExceptionTest {
    @Test
    void factoryMethods_allCases_returnExpectedPrefixedMessages() {
        assertAll(
                () -> assertMessage("Paiseh, I don't understand this command lah.",
                        AutoException.unknownCommand()),
                () -> assertMessage("Paiseh, no task 0 leh. Type list to check the numbers.",
                        AutoException.noSuchTask(0)),
                () -> assertMessage("Paiseh, no task 42 leh. Type list to check the numbers.",
                        AutoException.noSuchTask(42)),
                () -> assertMessage("Paiseh, 'abc' is not a task number lah. Use a whole number.",
                        AutoException.notATaskNumber("abc")),
                () -> assertMessage("Paiseh, find what ah? Try find book.",
                        AutoException.findNeedsKeyword()),
                () -> assertMessage("Paiseh, by when ah? Add /by followed by a date in dd/MM/yyyy.",
                        AutoException.deadlineNeedsBy()),
                () -> assertMessage("Paiseh, when start, when end? Add /from and /to dates in dd/MM/yyyy.",
                        AutoException.eventNeedsFromAndTo()),
                () -> assertMessage(
                        "Paiseh, end before start cannot lah. Check your /from and /to dates.",
                        AutoException.eventEndsBeforeItStarts()),
                () -> assertMessage(
                        "Paiseh, '31/02/2026' is not a valid date leh. Use dd/MM/yyyy.",
                        AutoException.invalidDate("31/02/2026")));
    }

    private void assertMessage(String expected, AutoException exception) {
        assertEquals(expected, exception.getMessage());
    }
}
