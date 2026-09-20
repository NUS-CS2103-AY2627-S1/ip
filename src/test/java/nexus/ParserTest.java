package nexus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests task-command parsing and validation. */
class ParserTest {
    @Test
    void createTask_extraCommandWhitespace_normalizesDescription() throws Exception {
        Task task = Parser.createTask("todo     read   book");

        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void createTask_deadlineWithRepeatedBy_throwsReadableError() {
        String command = "deadline submit /by 2026-09-18 /by 2026-09-19";
        NexusException exception = assertThrows(NexusException.class, () -> Parser.createTask(command));

        assertEquals("Use exactly one /by followed by a date in YYYY-MM-DD format.",
                exception.getMessage());
    }

    @Test
    void createTask_eventWhoseEndIsNotAfterStart_throwsReadableError() {
        String command = "event workshop /from 2026-09-18 /to 2026-09-18";
        NexusException exception = assertThrows(NexusException.class, () -> Parser.createTask(command));

        assertEquals("An event's /to date must be after its /from date.",
                exception.getMessage());
    }

    @Test
    void createTask_eventWithReversedMarkers_throwsReadableError() {
        String command = "event workshop /to 2026-09-19 /from 2026-09-18";
        NexusException exception = assertThrows(NexusException.class, () -> Parser.createTask(command));

        assertEquals("Use /from before /to, with one date after each marker.",
                exception.getMessage());
    }

    @Test
    void createTask_taskWithMissingDescription_throwsReadableError() {
        String command = "deadline /by 2026-09-18";
        NexusException exception = assertThrows(NexusException.class, () -> Parser.createTask(command));

        assertEquals("A deadline needs a description before /by.",
                exception.getMessage());
    }

    @Test
    void createTask_descriptionContainsStorageDelimiter_throwsReadableError() {
        String command = "todo compare alpha | beta";
        NexusException exception = assertThrows(NexusException.class, () -> Parser.createTask(command));

        assertEquals("Descriptions cannot contain | because Nexus uses it to save tasks.",
                exception.getMessage());
    }

    @Test
    void createTask_deadlineWithMissingDate_throwsReadableError() {
        String command = "deadline submit report /by";
        NexusException exception = assertThrows(NexusException.class, () -> Parser.createTask(command));

        assertEquals("A deadline needs a date after /by.", exception.getMessage());
    }

    @Test
    void createTask_eventWithRepeatedFrom_throwsReadableError() {
        String command = "event trip /from 2026-09-18 /from 2026-09-19 /to 2026-09-20";
        NexusException exception = assertThrows(NexusException.class, () -> Parser.createTask(command));

        assertEquals("Use exactly one /from and one /to, each followed by a date.",
                exception.getMessage());
    }

    @Test
    void createTask_eventWithRepeatedTo_throwsReadableError() {
        String command = "event trip /from 2026-09-18 /to 2026-09-19 /to 2026-09-20";
        NexusException exception = assertThrows(NexusException.class, () -> Parser.createTask(command));

        assertEquals("Use exactly one /from and one /to, each followed by a date.",
                exception.getMessage());
    }

    @Test
    void createTask_eventWithMissingFromDate_throwsReadableError() {
        String command = "event trip /from /to 2026-09-20";
        NexusException exception = assertThrows(NexusException.class, () -> Parser.createTask(command));

        assertEquals("An event needs a date after /from.", exception.getMessage());
    }

    @Test
    void createTask_eventWithMissingToDate_throwsReadableError() {
        String command = "event trip /from 2026-09-18 /to";
        NexusException exception = assertThrows(NexusException.class, () -> Parser.createTask(command));

        assertEquals("An event needs a date after /to.", exception.getMessage());
    }

    @Test
    void createTask_bareDeadline_throwsReadableError() {
        String command = "deadline";
        NexusException exception = assertThrows(NexusException.class, () -> Parser.createTask(command));

        assertEquals("A deadline needs a description and /by date.",
                exception.getMessage());
    }

    @Test
    void createTask_deadlineWithoutBy_throwsReadableError() {
        String command = "deadline submit report 2026-09-18";
        NexusException exception = assertThrows(NexusException.class, () -> Parser.createTask(command));

        assertEquals("Use exactly one /by followed by a date in YYYY-MM-DD format.",
                exception.getMessage());
    }

    @Test
    void createTask_bareEvent_throwsReadableError() {
        String command = "event";
        NexusException exception = assertThrows(NexusException.class, () -> Parser.createTask(command));

        assertEquals("An event needs a description, /from date, and /to date.",
                exception.getMessage());
    }

    @Test
    void createTask_eventWithoutMarkers_throwsReadableError() {
        String command = "event project meeting 2026-09-18 2026-09-19";
        NexusException exception = assertThrows(NexusException.class, () -> Parser.createTask(command));

        assertEquals("Use exactly one /from and one /to, each followed by a date.",
                exception.getMessage());
    }

    @Test
    void createTask_eventWithoutDescription_throwsReadableError() {
        String command = "event /from 2026-09-18 /to 2026-09-19";
        NexusException exception = assertThrows(NexusException.class, () -> Parser.createTask(command));

        assertEquals("An event needs a description before /from.",
                exception.getMessage());
    }
}
