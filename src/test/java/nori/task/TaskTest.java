package nori.task;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void constructor_blankDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Todo(" "));
    }

    @Test
    void deadlineConstructor_nullTime_throwsAssertionError() {
        assertThrows(AssertionError.class, () ->
                new Deadline("submit report", LocalDate.of(2026, 9, 10), null));
    }

    @Test
    void eventConstructor_blankStart_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Event("meeting", " ", "5pm"));
    }
}
