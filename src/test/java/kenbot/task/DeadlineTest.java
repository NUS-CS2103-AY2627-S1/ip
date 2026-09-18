package kenbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import kenbot.KenbotException;

/**
 * Tests {@link Deadline}, which splits its argument at {@code /by} and turns the
 * second half into a real date.
 *
 * <p>Test names read as {@code method_input_expectedResult}.</p>
 */
public class DeadlineTest {

    @Test
    public void of_wellFormed_keepsDescriptionAndDate() throws KenbotException {
        assertEquals("[D][ ] return book (by: Oct 15 2019)",
                Deadline.of("return book /by 2019-10-15").toString());
    }

    @Test
    public void of_dateWithTime_keepsTheTime() throws KenbotException {
        assertEquals("[D][ ] return book (by: Oct 15 2019 1800)",
                Deadline.of("return book /by 2019-10-15 1800").toString());
    }

    @Test
    public void of_noByPart_throws() {
        assertThrows(KenbotException.class, () -> Deadline.of("return book"));
    }

    @Test
    public void of_noDescription_throws() {
        assertThrows(KenbotException.class, () -> Deadline.of(" /by 2019-10-15"));
    }

    @Test
    public void of_blankDate_throws() {
        assertThrows(KenbotException.class, () -> Deadline.of("return book /by  "));
    }

    @Test
    public void of_dateThatIsNotADate_throws() {
        assertThrows(KenbotException.class, () -> Deadline.of("return book /by tomorrow"));
    }

    @Test
    public void of_impossibleDate_throws() {
        // Feb 30 does not exist, and LocalDate refuses it rather than rolling over.
        assertThrows(KenbotException.class, () -> Deadline.of("return book /by 2019-02-30"));
    }

    @Test
    public void of_repeatedBy_throws() {
        assertThrows(KenbotException.class, () ->
                Deadline.of("return book /by 2019-10-15 /by 2019-10-20"));
    }

    @Test
    public void of_trailingTags_areTakenOffBeforeTheDateIsRead() throws KenbotException {
        Deadline deadline = Deadline.of("return book /by 2019-10-15 #urgent");
        assertEquals("return book", deadline.getDescription());
        assertEquals("[#urgent]", deadline.getTags().toString());
    }

    @Test
    public void toStorable_roundTripsThroughTheSaveFormat() throws KenbotException {
        assertEquals("D | 0 | return book | 2019-10-15",
                Deadline.of("return book /by 2019-10-15").toStorable());
    }

    @Test
    public void toStorable_withTags_putsTagsAfterTheDate() throws KenbotException {
        assertEquals("D | 0 | return book | 2019-10-15 | urgent",
                Deadline.of("return book /by 2019-10-15 #urgent").toStorable());
    }
}
