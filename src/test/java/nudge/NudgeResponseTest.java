package nudge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests responses created by {@link NudgeResponse}.
 */
class NudgeResponseTest {
    @Test
    void withDetails_nullDetail_assertionFails() {
        List<String> detailsWithNull = new ArrayList<>();
        detailsWithNull.add(null);

        assertThrows(AssertionError.class, () -> {
            NudgeResponse.withDetails("Tasks found:", detailsWithNull);
        });
    }

    @Test
    void withDetails_multipleDetailArguments_preservesDetailsInOrder() {
        NudgeResponse response = NudgeResponse.withDetails(
                "Tasks found:", "1.[T][ ] Read book", "2.[T][ ] Return book");

        assertEquals("Tasks found:", response.getHeader());
        assertEquals(List.of("1.[T][ ] Read book", "2.[T][ ] Return book"),
                response.getDetails());
    }

    @Test
    void error_errorMessage_marksResponseAsError() {
        NudgeResponse response = NudgeResponse.error("Invalid command");

        assertEquals("Invalid command", response.getDisplayText());
        assertTrue(response.isError());
        assertFalse(response.shouldExit());
    }

    @Test
    void message_normalMessage_doesNotMarkResponseAsError() {
        NudgeResponse response = NudgeResponse.message("Task added");

        assertFalse(response.isError());
    }

    @Test
    void exitMessage_exitMessage_marksResponseForExit() {
        NudgeResponse response = NudgeResponse.exitMessage("Goodbye");

        assertEquals("Goodbye", response.getHeader());
        assertTrue(response.shouldExit());
        assertFalse(response.isError());
    }

    @Test
    void withDetails_footerProvided_formatsCompleteDisplayText() {
        NudgeResponse response = NudgeResponse.withDetails(
                "Tasks found:", List.of("1.[T][ ] Read book"), "One task on your radar.");
        String expectedText = String.join(System.lineSeparator(),
                "Tasks found:", "1.[T][ ] Read book", "One task on your radar.");

        assertTrue(response.hasFooter());
        assertEquals("One task on your radar.", response.getFooter());
        assertEquals(expectedText, response.getDisplayText());
    }
}
