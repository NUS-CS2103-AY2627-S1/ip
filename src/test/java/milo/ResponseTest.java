package milo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import milo.task.Deadline;
import milo.task.TaskList;
import milo.task.ToDo;

/** Checks error classification and recovery guidance without writing task data. */
public class ResponseTest {
    @Test
    public void executeResponse_invalidCommands_includesRelevantExamples() {
        Logic logic = new Logic(new TaskList());
        for (String command : List.of("todo", "deadline", "event", "find", "unknown")) {
            Response response = logic.executeResponse(command);
            assertTrue(response.isError(), command);
            assertTrue(response.displayText().contains("Try: "), command);
            assertEquals(logic.execute(command), response.message());
        }
        assertTrue(logic.executeResponse("deadline book /by invalid").suggestion().contains("/by"));
        assertTrue(logic.executeResponse("event study").suggestion().contains("/from"));
    }

    @Test
    public void executeResponse_invalidNumberThenSuccess_doesNotRetainErrorStatus() {
        Logic logic = new Logic(new TaskList(List.of(new ToDo("read book"))));
        Response error = logic.executeResponse("mark abc");
        assertTrue(error.isError());
        assertTrue(error.suggestion().contains("list"));
        assertTrue(logic.executeResponse("mark 9").isError());

        Response success = logic.executeResponse("list");
        assertFalse(success.isError());
        assertEquals(success.message(), success.displayText());
        assertFalse(logic.executeResponse("find missing").isError());
    }

    @Test
    public void executeResponse_badReschedule_suggestsMatchingTaskFormat() {
        Logic logic = new Logic(new TaskList(List.of(new Deadline("book", "2026-10-01"))));
        Response response = logic.executeResponse("reschedule 1 /from invalid");
        assertTrue(response.isError());
        assertEquals("Try: reschedule 1 /by 2026-10-15 1800", response.suggestion());
    }
}
