package cheecken;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Checks display and persistence for each task type throughout completion changes.
     * @return task instances and their expected display and storage representations
 */
class TaskTypesTest {
    /**
     * Provides task types and their expected display and storage representations.
     * @return task instances and their expected display and storage representations
     */
    static Stream<Arguments> tasks() {
        return Stream.of(
                Arguments.of(new Todo("read"), "[T][ ] read", "T | 0 | read"),
                Arguments.of(new Deadline("report", "18/9/2026"),
                        "[D][ ] report (by: Sep 18 2026)", "D | 0 | report | 2026-09-18"),
                Arguments.of(new Deadline("report", "2026-09-18T12:00"),
                        "[D][ ] report (by: Sep 18 2026 12:00 PM)", "D | 0 | report | 2026-09-18T12:00"),
                Arguments.of(new Event("meeting", "2026-09-18", "2026-09-19T00:00"),
                        "[E][ ] meeting (from: Sep 18 2026 to: Sep 19 2026 12:00 AM)",
                        "E | 0 | meeting | 2026-09-18 | 2026-09-19T00:00"));
    }

    /**
     * Verifies completion changes with all task types: preserve description and dates.
     * @param task task to process
     * @param display expected unmarked display text
     * @param storage expected unmarked storage text
     */
    @ParameterizedTest
    @MethodSource("tasks")
    void completionChanges_allTaskTypes_preserveDescriptionAndDates(Task task, String display, String storage) {
        assertEquals(display, task.toString());
        assertEquals(storage, task.toStorageString());
        task.mark();
        task.mark();
        assertEquals(display.replace("[ ]", "[X]"), task.toString());
        assertEquals(storage.replace(" | 0 | ", " | 1 | "), task.toStorageString());
        task.unmark();
        task.unmark();
        assertEquals(display, task.toString());
        assertEquals(storage, task.toStorageString());
    }
}
