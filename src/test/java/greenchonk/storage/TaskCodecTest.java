package greenchonk.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import greenchonk.exception.StorageException;
import greenchonk.task.Deadline;
import greenchonk.task.Event;
import greenchonk.task.Task;
import greenchonk.task.Todo;

class TaskCodecTest {
    private final TaskCodec taskCodec = new TaskCodec();

    @Test
    void encode_allTaskTypes_canonicalLinesReturned() throws StorageException {
        Todo todo = new Todo("pipe | and slash \\");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 28));
        deadline.markAsDone();
        Event event = new Event("conference", LocalDate.of(2026, 8, 29),
                LocalDate.of(2026, 8, 30));

        assertEquals("T | 0 | pipe \\| and slash \\\\", taskCodec.encode(todo));
        assertEquals("D | 1 | submit report | 2026-08-28", taskCodec.encode(deadline));
        assertEquals("E | 0 | conference | 2026-08-29 | 2026-08-30",
                taskCodec.encode(event));
    }

    @Test
    void decode_escapedDoneDeadline_valuesRestored() throws StorageException {
        Task task = taskCodec.decode("D | 1 | submit \\| report | 2026-08-28", 4);

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("submit | report", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 8, 28), deadline.getDueDate());
        assertTrue(deadline.isDone());
    }

    @Test
    void decode_invalidLine_storageExceptionIdentifiesLine() {
        StorageException exception = assertThrows(StorageException.class, () ->
                taskCodec.decode("E | 0 | backwards | 2026-08-30 | 2026-08-29", 7));

        assertEquals("The data file has an invalid task on line 7.", exception.getMessage());
    }

    @Test
    void encode_unsupportedTask_storageExceptionThrown() {
        StorageException exception = assertThrows(StorageException.class, () ->
                taskCodec.encode(new UnsupportedTask("custom task")));

        assertEquals("I couldn't save an unsupported task type.", exception.getMessage());
    }

    private static class UnsupportedTask extends Task {
        UnsupportedTask(String description) {
            super(description);
        }

        @Override
        public Task withDescription(String description) {
            return new UnsupportedTask(description);
        }

        @Override
        public String getTypeIcon() {
            return "X";
        }
    }
}
