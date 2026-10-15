package piplupbot.task;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Comparator;

import org.junit.jupiter.api.Test;

import piplupbot.PiplupBotException;

/**
 * Tests {@link SortDirection}, which reads the word after a sort key and turns
 * an order round.
 *
 * <p>The cases here are about what is refused as much as what is accepted. The
 * bot matches every other word a user types exactly, and a direction that
 * quietly accepted {@code DESC} or {@code descending} would make {@code sort}
 * the one command that does not -- an inconsistency nothing else would
 * report.</p>
 */
public class SortDirectionTest {

    @Test
    public void fromKeyword_bothKeywords_returnThatDirection() throws PiplupBotException {
        assertEquals(SortDirection.ASC, SortDirection.fromKeyword("asc"));
        assertEquals(SortDirection.DESC, SortDirection.fromKeyword("desc"));
    }

    @Test
    public void fromKeyword_capitalisedOrSpeltOut_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> SortDirection.fromKeyword("DESC"));
        assertThrows(PiplupBotException.class, () -> SortDirection.fromKeyword("descending"));
        assertThrows(PiplupBotException.class, () -> SortDirection.fromKeyword("down"));
    }

    /**
     * A line such as {@code sort date desc now} hands the whole of the rest of
     * the line over as the direction, so the refusal quotes it entire. That is
     * how the extra word is reported rather than ignored.
     */
    @Test
    public void fromKeyword_unknownWord_messageQuotesItAndNamesBothDirections() {
        PiplupBotException exception =
                assertThrows(PiplupBotException.class, () -> SortDirection.fromKeyword("desc now"));

        assertArrayEquals(new String[] {
            "Pip... I don't know the sort direction \"desc now\".",
            "Try: asc or desc.",
        }, exception.getMessageLines());
    }

    /** The two directions are named without a comma, which a list of two would add. */
    @Test
    public void getKeywordHint_namesBothDirections() {
        assertEquals("Try: asc or desc.", SortDirection.getKeywordHint());
    }

    // ---------- Turning an order round ----------

    @Test
    public void applyTo_ascending_returnsTheOrderUnchanged() {
        Comparator<Task> byDescription = Comparator.comparing(Task::getDescription);

        assertSame(byDescription, SortDirection.ASC.applyTo(byDescription));
    }

    @Test
    public void applyTo_descending_returnsTheOppositeOrder() {
        Comparator<Task> byDescription = Comparator.comparing(Task::getDescription);
        Todo a = new Todo("a");
        Todo b = new Todo("b");

        assertEquals(-1, Integer.signum(SortDirection.ASC.applyTo(byDescription).compare(a, b)));
        assertEquals(1, Integer.signum(SortDirection.DESC.applyTo(byDescription).compare(a, b)));
    }
}
