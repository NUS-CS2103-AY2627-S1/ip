package aider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import aider.model.Deadline;
import aider.model.Event;
import aider.model.Task;

/** Tests conversion of user commands into task objects. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseDeadlineCommand_createsDeadline() throws AiderException {
        Task task = parser.parseTask("deadline submit report /by 2026-09-01 0900");

        assertEquals("submit report", task.getDescription());
        Deadline deadline = (Deadline) task;
        assertEquals(LocalDateTime.of(2026, 9, 1, 9, 0), deadline.getBy());
    }

    @Test
    void parseEventCommand_rejectsEndBeforeStart() {
        String command = "event lecture /from 2026-09-01 1200 /to 2026-09-01 1000";
        assertThrows(AiderException.class, () -> parser.parseTask(command));
    }

    @Test
    void parseEventCommand_createsEvent() throws AiderException {
        Task task = parser.parseTask("event lecture /from 2026-09-01 1000"
                + " /to 2026-09-01 1200");

        assertEquals("lecture", task.getDescription());
        Event event = (Event) task;
        assertEquals(LocalDateTime.of(2026, 9, 1, 10, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 9, 1, 12, 0), event.getTo());
    }

    @Test
    void parseTask_normalizesHarmlessWhitespace() throws AiderException {
        Task task = parser.parseTask("  todo   read   book  ");

        assertEquals("read book", task.getDescription());
    }

    @Test
    void parseDeadlineCommand_rejectsRepeatedByMarker() {
        String command = "deadline submit report /by 2026-09-01 /by 2026-09-02";

        assertThrows(AiderException.class, () -> parser.parseTask(command));
    }

    @Test
    void parseEventCommand_rejectsEqualStartAndEnd() {
        String command = "event lecture /from 2026-09-01 1000 /to 2026-09-01 1000";

        assertThrows(AiderException.class, () -> parser.parseTask(command));
    }

    @Test
    void parseTask_rejectsBlankCommand() {
        assertThrows(AiderException.class, () -> parser.parseTask("   "));
    }

    @Test
    void parseTask_rejectsUnknownCommand() {
        assertThrows(AiderException.class, () -> parser.parseTask("archive read book"));
    }

    @Test
    void parseDate_rejectsInvalidDate() {
        assertThrows(AiderException.class, () -> parser.parseDate("on 2026-02-30"));
    }
}
