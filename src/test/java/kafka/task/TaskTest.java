package kafka.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests display and completion behavior for each concrete task type.
 */
class TaskTest {
    @Test
    void todoStartsUnmarkedAndCanBeMarkedAndUnmarked() {
        Task task = new Todo("read book");

        assertEquals("[T][ ] read book", task.display());
        task.mark();
        assertEquals("[T][X] read book", task.display());
        task.unmark();
        assertEquals("[T][ ] read book", task.display());
    }

    @Test
    void deadlineDisplaysDeadlineText() {
        Task task = new Deadline("return book", "Sunday");

        assertEquals("[D][ ] return book (by: Sunday)", task.display());
    }

    @Test
    void eventDisplaysStartAndEndText() {
        Task task = new Event("project meeting", "Mon 2pm", "4pm");

        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", task.display());
    }

    @ParameterizedTest
    @MethodSource("taskTypes")
    void markAndUnmark_repeatedCalls_keepDisplayAndStorageConsistent(Task task, String type, String details) {
        String unfinishedDisplay = task.display();
        assertEquals(type + " | 0 | " + details, task.toDataString());

        task.mark();
        task.mark();
        assertEquals(unfinishedDisplay.replace("[ ]", "[X]"), task.display());
        assertEquals(type + " | 1 | " + details, task.toDataString());

        task.unmark();
        task.unmark();
        assertEquals(unfinishedDisplay, task.display());
        assertEquals(type + " | 0 | " + details, task.toDataString());
    }

    @ParameterizedTest
    @MethodSource("taskTypes")
    void rename_completedTask_preservesTypeStatusAndSchedule(Task task, String type, String details) {
        task.mark();

        task.rename("new name");

        assertEquals(type + " | 1 | " + details.replace("original", "new name"), task.toDataString());
    }

    private static Stream<Arguments> taskTypes() {
        return Stream.of(
                Arguments.of(new Todo("original"), "T", "original"),
                Arguments.of(new Deadline("original", "Sunday"), "D", "original | Sunday"),
                Arguments.of(new Event("original", "Monday", "Tuesday"), "E", "original | Monday | Tuesday"));
    }
}
