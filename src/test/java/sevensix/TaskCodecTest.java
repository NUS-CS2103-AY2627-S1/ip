package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/** Tests the storage wire format independently of filesystem behavior. */
class TaskCodecTest {
    /** Each supported type and status must encode predictably and decode without losing details.
     *
     * @param task the task to serialize.
     * @param record its exact expected storage representation.
     */
    @ParameterizedTest
    @MethodSource("validRecords")
    void formatAndParsePreserveExactRecords(Task task, String record) {
        TaskCodec codec = new TaskCodec();

        assertEquals(record, codec.formatTask(task));
        Task loaded = codec.parseTask(record);
        assertNotNull(loaded);
        assertEquals(task.getClass(), loaded.getClass());
        assertEquals(task.getDescription(), loaded.getDescription());
        assertEquals(task.isDone(), loaded.isDone());
        assertEquals(record, codec.formatTask(loaded));
    }

    /** Supplies date-only and timed tasks, including characters with storage meaning.
     *
     * @return task and serialized-record pairs.
     */
    private static Stream<Arguments> validRecords() {
        Todo done = new Todo("read | notes\\");
        done.markAsDone();
        LocalDate day = LocalDate.of(2024, 2, 29);
        return Stream.of(
                Arguments.of(new Todo("读书"), "T | 0 | 读书"),
                Arguments.of(done, "T | 1 | read \\| notes\\\\"),
                Arguments.of(new Deadline("report", day), "D | 0 | report | 2024-02-29"),
                Arguments.of(new Deadline("report", day, LocalTime.NOON), "D | 0 | report | 2024-02-29T12:00"),
                Arguments.of(new Event("trip", day, day.plusDays(1)),
                        "E | 0 | trip | 2024-02-29 | 2024-03-01"),
                Arguments.of(new Event("call", day, null, day, LocalTime.NOON),
                        "E | 0 | call | 2024-02-29 | 2024-02-29T12:00"),
                Arguments.of(new Event("shift", day, LocalTime.NOON, day.plusDays(1), null),
                        "E | 0 | shift | 2024-02-29T12:00 | 2024-03-01"));
    }

    /** Malformed field counts, values, escape sequences, and ranges must be rejected.
     *
     * @param record the malformed storage record.
     */
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "T", "T | 0", "T | yes | task", "T | | task", "? | 0 | task",
        "T | 0 |", "T | 0 | task | extra", "D | 0 | task", "D | 0 | | 2024-01-01", "D | 0 | task |",
        "D | 0 | task | 2024-01-01 | extra", "D | 0 | task | 2023-02-29", "E | 0 | task",
        "E | 0 | | 2024-01-01 | 2024-01-02", "E | 0 | task | | 2024-01-02", "E | 0 | task | 2024-01-01 |",
        "E | 0 | task | 2024-01-01 | 2024-01-02 | extra", "E | 0 | task | invalid | 2024-01-02",
        "E | 0 | task | 2024-01-01 | invalid", "E | 0 | task | 2024-01-02 | 2024-01-01",
        "T | 0 | bad\\q", "T | 0 | bad\\", "T | 0 | embedded\tcontrol"})
    void parseTaskRejectsMalformedRecords(String record) {
        assertNull(new TaskCodec().parseTask(record));
    }

    /** Escapes must survive consecutive pipes and backslashes as well as Unicode text.
     *
     * @param description the description containing storage-sensitive characters.
     */
    @ParameterizedTest
    @ValueSource(strings = {"a|b", "a\\b", "a\\|b", "a|\\b", "||", "\\\\", "终点\\", "读书 📚"})
    void roundTripPreservesEscapedDescriptions(String description) {
        TaskCodec codec = new TaskCodec();
        Task loaded = codec.parseTask(codec.formatTask(new Todo(description)));

        assertNotNull(loaded);
        assertEquals(description, loaded.getDescription());
    }
}
