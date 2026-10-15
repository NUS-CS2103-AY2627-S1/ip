package bot.task;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void constructor_blankDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Todo(" "));
    }

    @Test
    void deadlineConstructor_nullDate_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Deadline("return book", null));
    }

    @Test
    void eventConstructor_endNotAfterStart_throwsAssertionError() {
        LocalDateTime start = LocalDateTime.of(2019, 12, 2, 14, 0);
        LocalDateTime end = LocalDateTime.of(2019, 12, 2, 13, 0);

        assertThrows(AssertionError.class, () -> new Event("meeting", start, end));
    }
}
