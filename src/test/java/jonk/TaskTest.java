package jonk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.format.DateTimeParseException;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests status transitions, date validation, and display and storage formats for every task type.
 */
public class TaskTest {

    @ParameterizedTest
    @MethodSource("taskExamples")
    public void taskLifecycle_eachTaskType_preservesDetailsAndFormatsStatus(
            Task task, String display, String saved) {
        assertEquals("read | notes \\ draft", task.getDescription());
        assertFalse(task.isDone());
        assertEquals(display, task.toString());
        assertEquals(saved, task.toFileString());

        task.markAsDone();
        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals(display.replace("[ ]", "[X]"), task.toString());
        assertEquals(saved.replace("0 |", "1 |"), task.toFileString());

        task.markAsUndone();
        task.markAsUndone();

        assertFalse(task.isDone());
        assertEquals(display, task.toString());
        assertEquals(saved, task.toFileString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2019-02-29", "2020-02-30", "2026-13-01", "2026-00-01", "2026/01/01", ""})
    public void constructor_invalidDate_rejectsDeadlineAndBothEventDates(String date) {
        assertThrows(DateTimeParseException.class, () -> new Deadline("deadline", date));
        assertThrows(DateTimeParseException.class, () -> new Event("event", date, "2026-01-01"));
        assertThrows(DateTimeParseException.class, () -> new Event("event", "2026-01-01", date));
    }

    private static Stream<Arguments> taskExamples() {
        String description = "read | notes \\ draft";
        String escapedDescription = "read \\| notes \\\\ draft";
        return Stream.of(
                Arguments.of(new Task(description), "[ ] " + description, "0 | " + escapedDescription),
                Arguments.of(new Todo(description), "[T][ ] " + description, "T | 0 | " + escapedDescription),
                Arguments.of(new Deadline(description, "2024-02-29"),
                        "[D][ ] " + description + " (by: Feb 29 2024)",
                        "D | 0 | " + escapedDescription + " | 2024-02-29"),
                Arguments.of(new Event(description, "2023-12-31", "2024-01-01"),
                        "[E][ ] " + description + " (from: Dec 31 2023 to: Jan 1 2024)",
                        "E | 0 | " + escapedDescription + " | 2023-12-31 | 2024-01-01"));
    }
}
