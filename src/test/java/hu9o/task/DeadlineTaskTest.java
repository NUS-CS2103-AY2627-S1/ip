package hu9o.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link DeadlineTask}'s two non-trivial methods, {@code compressionString}
 * (save-file record) and {@code specificationSuffix} (the "(by: ...)" text),
 * plus the date parsing done in its constructor.
 */
public class DeadlineTaskTest {

    @Test
    public void compressionString_onTheHourTime_appendsDeadlineInStorageFormat() {
        DeadlineTask deadline = new DeadlineTask("submit assignment", "12/08/26 3 PM");

        assertEquals("D| |submit assignment|12/08/26 3 PM|", deadline.compressionString());
    }

    @Test
    public void compressionString_afterMark_recordsDoneFlag() {
        DeadlineTask deadline = new DeadlineTask("submit assignment", "12/08/26 3 PM");
        deadline.mark();

        assertEquals("D|X|submit assignment|12/08/26 3 PM|", deadline.compressionString());
    }

    @Test
    public void specificationSuffix_onTheHourTime_rendersByClauseInDisplayFormat() {
        DeadlineTask deadline = new DeadlineTask("submit assignment", "12/08/26 3 PM");

        assertEquals(" (by: 12 Aug 2026, 3 PM)", deadline.specificationSuffix());
    }

    @Test
    public void constructor_unparseableTime_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () ->
                new DeadlineTask("submit assignment", "sunday"));
    }
}
