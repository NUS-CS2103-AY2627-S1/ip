package herta.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import herta.exception.HertaException;
import herta.task.Deadline;
import herta.task.Event;
import herta.task.Task;
import herta.task.TaskList;
import herta.task.Todo;

/**
 * Tests conversion between task objects and the line-based storage format.
 */
class TaskStorageConverterTest {
    private static final String RECORD_PREFIX = "Failed to load tasks ";
    private final TaskStorageConverter converter = new TaskStorageConverter();

    @Test
    void convert_completeAndIncompleteTasks_preservesTypesStatusesAndOrder() throws Exception {
        Todo incompleteTodo = new Todo("repeat description");
        Todo completeTodo = new Todo("repeat description");
        completeTodo.markAsDone();
        Deadline deadline = new Deadline("deadline", LocalDateTime.of(2019, 10, 15, 18, 0));
        Event event = new Event("event", LocalDateTime.of(2019, 10, 16, 10, 0),
                LocalDateTime.of(2019, 10, 16, 11, 0));

        List<String> lines = converter.serializeTasks(new TaskList(
                List.of(incompleteTodo, completeTodo, deadline, event)));
        TaskList parsedTasks = converter.parseStorageLines(lines, RECORD_PREFIX);

        assertEquals(List.of("T | 0 | repeat description", "T | 1 | repeat description",
                "D | 0 | deadline | 2019-10-15T18:00:00",
                "E | 0 | event | 2019-10-16T10:00:00 | 2019-10-16T11:00:00"), lines);
        assertEquals(4, parsedTasks.size());
        assertTrue(parsedTasks.get(1).isCompleted());
        assertEquals("repeat description", parsedTasks.get(0).getDescription());
        assertEquals("deadline", parsedTasks.get(2).getDescription());
        assertEquals("event", parsedTasks.get(3).getDescription());
    }

    @Test
    void convert_completedDeadlineAndEvent_preservesCompletionStatus() throws Exception {
        Deadline deadline = new Deadline("deadline", LocalDateTime.of(2019, 10, 15, 18, 0));
        Event event = new Event("event", LocalDateTime.of(2019, 10, 16, 10, 0),
                LocalDateTime.of(2019, 10, 16, 11, 0));
        deadline.markAsDone();
        event.markAsDone();

        List<String> lines = converter.serializeTasks(new TaskList(List.of(deadline, event)));
        TaskList parsedTasks = converter.parseStorageLines(lines, RECORD_PREFIX);

        assertTrue(parsedTasks.get(0).isCompleted());
        assertTrue(parsedTasks.get(1).isCompleted());
    }

    @Test
    void parseStorageLines_bomAndInnerWhitespace_preservesNormalizedFields() throws Exception {
        TaskList tasks = converter.parseStorageLines(List.of(
                "\uFEFFT | 0 | repeated   spaces", "T|1|repeated   spaces"), RECORD_PREFIX);

        assertEquals(2, tasks.size());
        assertEquals("repeated   spaces", tasks.get(0).getDescription());
        assertTrue(tasks.get(1).isCompleted());
    }

    @Test
    void parseStorageLines_innerBom_rejectsDescriptionOnSourceLine() {
        HertaException exception = assertThrows(HertaException.class, () -> converter.parseStorageLines(
                List.of("T | 0 | valid", "T | 0 | bad\uFEFFtext"), RECORD_PREFIX));

        assertTrue(exception.getMessage().contains("at line 2:"));
        assertTrue(exception.getMessage().contains("descriptions cannot contain"));
    }

    @Test
    void parseStorageLines_malformedRecords_reportSpecificOneBasedLine() {
        assertMalformedRecord("", "blank records are not supported");
        assertMalformedRecord("T", "missing task type or status");
        assertMalformedRecord("T | 0", "type T requires 3 fields");
        assertMalformedRecord("X | 0 | task", "unknown task type");
        assertMalformedRecord("T | 2 | task", "completion status must be 0 or 1");
        assertMalformedRecord("T | 0 | ", "task fields cannot be blank");
        assertMalformedRecord("D | 0", "type D requires 4 fields");
        assertMalformedRecord("E | 0", "type E requires 5 fields");
        assertMalformedRecord("D | 2 | deadline | 2019-10-15", "completion status must be 0 or 1");
        assertMalformedRecord("E | 2 | event | 2019-10-15 | 2019-10-16",
                "completion status must be 0 or 1");
        assertMalformedRecord("D | 0 | deadline | ", "task fields cannot be blank");
        assertMalformedRecord("E | 0 | event | 2019-10-15 | ", "task fields cannot be blank");
        assertMalformedRecord("D | 0 | deadline | 2019-02-30", "Invalid saved deadline date/time");
        assertMalformedRecord("E | 0 | event | not-a-date | 2019-10-16",
                "Invalid saved event date/time");
        assertMalformedRecord("E | 0 | event | 2019-10-15 | not-a-date",
                "Invalid saved event date/time");
        assertMalformedRecord("E | 0 | event | 2019-10-16T11:00:00 | 2019-10-16T10:00:00",
                "Invalid saved event");
    }

    @Test
    void parseStorageLines_invalidDescription_reportsTheSourceLine() {
        HertaException exception = assertThrows(HertaException.class, () -> converter.parseStorageLines(
                List.of("T | 0 | valid", "T | 0 | bad\u202Etext"), RECORD_PREFIX));

        assertTrue(exception.getMessage().contains("at line 2:"));
        assertTrue(exception.getMessage().contains("descriptions cannot contain"));
    }

    @Test
    void serializeTasks_invalidStorageResults_rejectThem() {
        assertSerializationFailure(new NullStorageTask("null result"), "task contains invalid data");
        assertSerializationFailure(new LineBreakingStorageTask("line break"), "line breaks");
        assertSerializationFailure(new MalformedStorageTask("malformed"), "type T requires 3 fields");
        assertSerializationFailure(new ThrowingStorageTask("throws"), "task contains invalid data");
    }

    private void assertMalformedRecord(String record, String expectedMessage) {
        HertaException exception = assertThrows(HertaException.class, () -> converter.parseStorageLines(
                List.of(record), RECORD_PREFIX));

        assertTrue(exception.getMessage().contains("at line 1:"));
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    private void assertSerializationFailure(Task task, String expectedMessage) {
        HertaException exception = assertThrows(HertaException.class, () -> converter.serializeTasks(
                new TaskList(List.of(task))));

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    /** Produces null serialized data to test converter validation. */
    private static final class NullStorageTask extends Todo {
        NullStorageTask(String description) {
            super(description);
        }

        @Override
        public String toStorageString() {
            return null;
        }
    }

    /** Produces line-breaking serialized data to test converter validation. */
    private static final class LineBreakingStorageTask extends Todo {
        LineBreakingStorageTask(String description) {
            super(description);
        }

        @Override
        public String toStorageString() {
            return "T | 0 | line\nbreak";
        }
    }

    /** Produces malformed serialized data to test converter validation. */
    private static final class MalformedStorageTask extends Todo {
        MalformedStorageTask(String description) {
            super(description);
        }

        @Override
        public String toStorageString() {
            return "T | 0 | malformed | extra";
        }
    }

    /** Throws during serialization to test converter error handling. */
    private static final class ThrowingStorageTask extends Todo {
        ThrowingStorageTask(String description) {
            super(description);
        }

        @Override
        public String toStorageString() {
            throw new IllegalStateException("unexpected serializer failure");
        }
    }
}
