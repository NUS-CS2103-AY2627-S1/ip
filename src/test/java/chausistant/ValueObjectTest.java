package chausistant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests immutable values exchanged between the chatbot and its graphical interface. */
class ValueObjectTest {

    @Test
    void chatResponse_errorType_reportsError() {
        ChatResponse response = new ChatResponse("Could not save tasks.", ChatResponse.Type.ERROR);

        assertEquals("Could not save tasks.", response.text());
        assertTrue(response.isError());
    }

    @Test
    void chatResponse_standardType_isNotAnError() {
        ChatResponse response = new ChatResponse("Tasks listed.", ChatResponse.Type.STANDARD);

        assertFalse(response.isError());
    }

    @Test
    void valueObjects_rejectNullTextAndResponseType() {
        assertThrows(NullPointerException.class, () -> new ChatResponse(null, ChatResponse.Type.STANDARD));
        assertThrows(NullPointerException.class, () -> new ChatResponse("message", null));
        assertThrows(NullPointerException.class, () -> new TaskSummary(null, false));
    }

    @Test
    void taskSummary_preservesDashboardFields() {
        TaskSummary taskSummary = new TaskSummary("[T][X] read book", true);

        assertEquals("[T][X] read book", taskSummary.text());
        assertTrue(taskSummary.isCompleted());
    }
}
