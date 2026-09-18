package kenbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import kenbot.KenbotException;

/**
 * Tests {@link Event}, which is the only task type with two dates and so the
 * only one that can be given them the wrong way round.
 *
 * <p>Test names read as {@code method_input_expectedResult}.</p>
 */
public class EventTest {

    @Test
    public void of_wellFormed_keepsDescriptionAndBothDates() throws KenbotException {
        Event event = Event.of("project meeting /from 2019-10-15 1400 /to 2019-10-15 1600");
        assertEquals("[E][ ] project meeting (from: Oct 15 2019 1400 to: Oct 15 2019 1600)",
                event.toString());
    }

    @Test
    public void of_endBeforeStart_throws() {
        KenbotException thrown = assertThrows(KenbotException.class, () ->
                Event.of("backwards /from 2019-10-20 /to 2019-10-15"));
        assertTrue(thrown.getMessage().contains("can't end before it starts"));
    }

    @Test
    public void of_sameDayButEndTimeEarlier_throws() {
        // Same date, so the times decide it.
        assertThrows(KenbotException.class, () ->
                Event.of("backwards /from 2019-10-15 1600 /to 2019-10-15 1400"));
    }

    @Test
    public void of_sameDayNoTimes_isAllowedAsAWholeDayEvent() throws KenbotException {
        // Nothing separates the two ends, but a whole-day event is a real thing
        // and refusing it would be worse than allowing it.
        Event event = Event.of("conference /from 2019-10-15 /to 2019-10-15");
        assertTrue(event.toString().contains("conference"));
    }

    @Test
    public void of_sameDayUncomparableTimes_isAllowed() throws KenbotException {
        // "morning" and "evening" cannot be ordered, so the event is let
        // through rather than refused on a guess.
        Event event = Event.of("retreat /from 2019-10-15 morning /to 2019-10-15 evening");
        assertTrue(event.toString().contains("retreat"));
    }

    @Test
    public void of_repeatedFrom_throws() {
        assertThrows(KenbotException.class, () ->
                Event.of("x /from 2019-10-15 /from 2019-10-16 /to 2019-10-17"));
    }

    @Test
    public void of_repeatedTo_throws() {
        assertThrows(KenbotException.class, () ->
                Event.of("x /from 2019-10-15 /to 2019-10-16 /to 2019-10-17"));
    }

    @Test
    public void of_missingTo_throws() {
        assertThrows(KenbotException.class, () -> Event.of("x /from 2019-10-15"));
    }

    @Test
    public void of_missingDescription_throws() {
        assertThrows(KenbotException.class, () ->
                Event.of(" /from 2019-10-15 /to 2019-10-16"));
    }

    @Test
    public void of_trailingTags_areKeptOffTheDescription() throws KenbotException {
        Event event = Event.of("talk /from 2019-10-15 /to 2019-10-16 #work");
        assertEquals("talk", event.getDescription());
        assertEquals(1, event.getTags().size());
    }

    @Test
    public void toStorable_withTags_putsTagsAfterBothDates() throws KenbotException {
        Event event = Event.of("talk /from 2019-10-15 /to 2019-10-16 #work");
        assertEquals("E | 0 | talk | 2019-10-15 | 2019-10-16 | work", event.toStorable());
    }
}
