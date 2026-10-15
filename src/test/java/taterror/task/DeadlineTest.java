package taterror.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Deadline}.
 */
public class DeadlineTest {

    @Test
    public void toDisplayDetail_validIsoDate_rendersInReadableFormat() {
        Deadline deadline = new Deadline("submit report", "2019-10-15");
        assertEquals("(by: Oct 15 2019)", deadline.toDisplayDetail());
    }

    @Test
    public void toDisplayDetail_nonIsoDate_fallsBackToRawText() {
        Deadline deadline = new Deadline("submit report", "next Friday");
        assertEquals("(by: next Friday)", deadline.toDisplayDetail());
    }

    @Test
    public void toSaveDetail_prependsPipeSeparator() {
        Deadline deadline = new Deadline("submit report", "2019-10-15");
        assertEquals(" | 2019-10-15", deadline.toSaveDetail());
    }

    @Test
    public void toString_includesTypeCodeStatusAndFormattedDate() {
        Deadline deadline = new Deadline("submit report", "2019-10-15");
        assertEquals("[D][ ] submit report (by: Oct 15 2019)", deadline.toString());
    }
}
