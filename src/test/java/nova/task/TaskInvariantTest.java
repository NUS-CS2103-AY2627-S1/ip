package nova.task;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests internal assumptions enforced by task-model assertions.
 */
public class TaskInvariantTest {
    @Test
    public void constructor_blankDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Todo("   "));
    }

    @Test
    public void deadlineConstructor_dateOnlyWithNonMidnightTime_throwsAssertionError() {
        LocalDateTime dueDateTime = LocalDateTime.of(2019, 12, 2, 18, 0);

        assertThrows(AssertionError.class, () -> new Deadline("return book", dueDateTime, false));
    }

    @Test
    public void eventConstructor_endBeforeStart_throwsAssertionError() {
        LocalDateTime startDateTime = LocalDateTime.of(2019, 12, 3, 18, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2019, 12, 2, 18, 0);

        assertThrows(AssertionError.class, () ->
                new Event("meeting", startDateTime, true, endDateTime, true));
    }

    @Test
    public void numberedTask_nonPositiveNumber_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                new TaskList.NumberedTask(0, new Todo("read book")));
    }
}
