package nexus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the command parsing and validation performed by {@link Parser}. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseTask_todoWithDescription_returnsTodo() throws NexusException {
        Task task = parser.parseTask("todo buy milk");
        assertInstanceOf(Todo.class, task);
        assertEquals("buy milk", task.getDescription());
    }

    @Test
    void parseTask_todoWithoutDescription_throwsException() {
        assertThrows(NexusException.class, () -> parser.parseTask("todo"));
        assertThrows(NexusException.class, () -> parser.parseTask("todo   "));
    }

    @Test
    void parseTask_deadlineWithValidDate_returnsDeadline() throws NexusException {
        Task task = parser.parseTask("deadline submit report /by 2026-08-26");
        assertInstanceOf(Deadline.class, task);
        assertEquals("submit report", task.getDescription());
        assertEquals("[D][ ] submit report (by: Aug 26 2026)", task.toString());
    }

    @Test
    void parseTask_deadlineWithMissingOrInvalidDate_throwsException() {
        assertThrows(NexusException.class, () -> parser.parseTask("deadline submit report"));
        assertThrows(NexusException.class, () -> parser.parseTask("deadline submit report /by"));
        assertThrows(NexusException.class, () -> parser.parseTask("deadline submit report /by 26-08-2026"));
    }

    @Test
    void parseTask_eventWithFromAndTo_returnsEvent() throws NexusException {
        Task task = parser.parseTask("event meeting /from 10:00 /to 11:00");
        assertInstanceOf(Event.class, task);
        assertEquals("meeting", task.getDescription());
        assertEquals(TaskType.EVENT, task.getTaskType());
        assertEquals("meeting", task.getDescription());
        assertTrue(task.toString().contains("from: 10:00"));
        assertTrue(task.toString().contains("to: 11:00"));
    }

    @Test
    void parseTask_eventWithMissingParts_throwsException() {
        assertThrows(NexusException.class, () -> parser.parseTask("event meeting"));
        assertThrows(NexusException.class, () -> parser.parseTask("event meeting /from 10:00"));
        assertThrows(NexusException.class, () -> parser.parseTask("event /from 10:00 /to 11:00"));
    }

    @Test
    void parseTask_unknownCommand_throwsException() {
        assertThrows(NexusException.class, () -> parser.parseTask("list"));
    }

    @Test
    void parseTask_malformedSpacingOrDuplicateMarker_throwsException() {
        assertThrows(NexusException.class, () -> parser.parseTask(" todo task"));
        assertThrows(NexusException.class, () -> parser.parseTask("todo  task"));
        assertThrows(NexusException.class, () -> parser.parseTask("deadline task /by 2026-01-01 /by 2026-01-02"));
    }

    @Test
    void parseTask_eventThatDoesNotEndAfterStart_throwsException() {
        assertThrows(NexusException.class, () -> parser.parseTask("event meeting /from 10:00 /to 10:00"));
        assertThrows(NexusException.class,
                () -> parser.parseTask("event meeting /from 2026-02-30T10:00 /to 2026-03-01T10:00"));
    }
}
