package bingbong.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    public void toString_validDateFormat_formattedCorrectly() {
        Deadline deadline = new Deadline("buy eggs", "24/08/2026");
        assertEquals("[D] [ ] buy eggs (by: 24/08/2026)", deadline.toString());
    }

    @Test
    public void toString_markedAsCompleted_formattedCorrectly() {
        Deadline deadline = new Deadline("buy eggs", "24/08/2026");
        deadline.mark();
        assertEquals("[D] [X] buy eggs (by: 24/08/2026)", deadline.toString());
    }

    @Test
    public void formatToFile_normalInput_formattedCorrectly() {
        Deadline deadline = new Deadline("buy eggs", "24/08/2026");
        assertEquals("D | 0 | buy eggs | 2026-08-24", deadline.formatToFile());
    }

    @Test
    public void formatToFile_completedInput_formattedCorrectly() {
        Deadline deadline = new Deadline("buy eggs", "24/08/2026");
        deadline.mark();
        assertEquals("D | 1 | buy eggs | 2026-08-24", deadline.formatToFile());
    }

    @Test
    public void constructor_invalidDateString_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> {
            new Deadline("buy eggs", "invalid-date-format");
        });
    }
}
