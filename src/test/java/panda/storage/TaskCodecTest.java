package panda.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import panda.exception.storage.FileCorruptedException;
import panda.task.Deadline;
import panda.task.Event;
import panda.task.Task;
import panda.task.Todo;

/**
 * Contains unit tests for {@link TaskCodec} using round-trip serialization and corrupted line handling.
 */
public class TaskCodecTest {

    @ParameterizedTest
    @MethodSource("validTaskRecords")
    public void roundTrip_validRecord_preservesRecord(
            String record, Class<? extends Task> expectedType) throws Exception {
        Task task = TaskCodec.decode(record);

        assertInstanceOf(expectedType, task);
        assertEquals(record, TaskCodec.encode(task));
    }

    @Test
    public void decode_corruptedRecord_throwsFileCorruptedException() {
        assertThrows(FileCorruptedException.class, () -> TaskCodec.decode("corrupted task record"));
    }

    @Test
    public void decode_illegalStoredDate_throwsFileCorruptedException() {
        assertThrows(FileCorruptedException.class, () ->
                TaskCodec.decode("D | 0 | return book | invalid-date"));
    }

    @Test
    public void decode_eventEndNotAfterStart_throwsFileCorruptedException() {
        assertThrows(FileCorruptedException.class, () ->
                TaskCodec.decode("E | 0 | meeting | 2026-09-10 15:00 | 2026-09-10 14:00"));
    }

    private static Stream<Arguments> validTaskRecords() {
        return Stream.of(
                Arguments.of("T | 0 | read book", Todo.class),
                Arguments.of("T | 1 | read book", Todo.class),
                Arguments.of("D | 0 | return book | 2019-06-06 18:00", Deadline.class),
                Arguments.of("D | 1 | return book | 2019-06-06", Deadline.class),
                Arguments.of("E | 1 | project meeting | 2019-08-06 14:00 | 2019-08-06 16:00", Event.class),
                Arguments.of("E | 0 | orientation camp | 2019-08-06 | 2019-08-08", Event.class));
    }
}
