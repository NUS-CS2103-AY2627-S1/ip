package piplupbot.task;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import piplupbot.PiplupBotException;

/**
 * Tests what {@link Event} adds to {@link Task}: a start and an end.
 *
 * <p>Having two dates rather than one is what makes an event worth its own
 * cases. They are written to the file as two fields rather than as one combined
 * "2-4pm", so that reading the file back does not have to take them apart again
 * -- and they must not be able to swap places, which is what the cases below
 * check by giving the two ends different times. For the same reason, the end
 * must come after the start, and two events are the same event only if both
 * of their times match.</p>
 *
 * <p>The behavior every task shares is checked in {@link TodoTest} rather than
 * repeated here.</p>
 */
public class EventTest {

    // ---------- What the user sees ----------

    @Test
    public void toString_newEvent_showsDescriptionStartAndEnd() throws PiplupBotException {
        assertEquals("[E][ ] project meeting (from: Oct 2 2019 02:00 PM to: Oct 2 2019 04:00 PM)",
                new Event("project meeting", "2019-10-02 1400", "2019-10-02 1600").toString());
    }

    @Test
    public void toString_doneEvent_showsCrossInStatusBox() throws PiplupBotException {
        Event event = new Event("project meeting", "2019-10-02 1400", "2019-10-02 1600");
        event.markAsDone();
        assertEquals("[E][X] project meeting (from: Oct 2 2019 02:00 PM to: Oct 2 2019 04:00 PM)",
                event.toString());
    }

    /** An event may run across midnight into the following day. */
    @Test
    public void toString_eventSpanningTwoDays_showsBothDates() throws PiplupBotException {
        assertEquals("[E][ ] 24/7 shift (from: Oct 12 2019 12:00 AM to: Oct 13 2019 12:00 AM)",
                new Event("24/7 shift", "2019-10-12", "2019-10-13").toString());
    }

    // ---------- What the save file holds ----------

    /**
     * The start and the end are separate fields, in that order. Reversing them
     * would still produce a readable file, and every event in it would come back
     * running backwards.
     */
    @Test
    public void toFileFields_newEvent_returnsStartThenEndInIsoForm() throws PiplupBotException {
        assertArrayEquals(
                new String[] {"E", "0", "project meeting", "2019-10-02T14:00", "2019-10-02T16:00"},
                new Event("project meeting", "2019-10-02 1400", "2019-10-02 1600").toFileFields());
    }

    @Test
    public void toFileFields_doneEvent_returnsDoneFlagOfOne() throws PiplupBotException {
        Event event = new Event("project meeting", "2019-10-02 1400", "2019-10-02 1600");
        event.markAsDone();
        assertArrayEquals(
                new String[] {"E", "1", "project meeting", "2019-10-02T14:00", "2019-10-02T16:00"},
                event.toFileFields());
    }

    // ---------- A time the bot cannot read ----------

    /** Either end being unreadable is enough to refuse the whole event. */
    @Test
    public void constructor_unreadableStartTime_exceptionThrown() {
        assertThrows(PiplupBotException.class, () ->
                new Event("meeting", "sometime", "2019-10-02 1600"));
    }

    @Test
    public void constructor_unreadableEndTime_exceptionThrown() {
        assertThrows(PiplupBotException.class, () ->
                new Event("meeting", "2019-10-02 1400", "sometime"));
    }

    // ---------- The end must come after the start ----------

    @Test
    public void constructor_endBeforeStart_exceptionThrown() {
        assertThrows(PiplupBotException.class, () ->
                new Event("meeting", "2019-10-02 1600", "2019-10-02 1400"));
    }

    /**
     * An event that ends the moment it starts is refused as well. This is the
     * case that tells {@code isAfter} from "not before", which would let it in.
     */
    @Test
    public void constructor_endSameAsStart_exceptionThrown() {
        assertThrows(PiplupBotException.class, () ->
                new Event("meeting", "2019-10-02 1400", "2019-10-02 1400"));
    }

    /** The smallest gap the bot can store is enough, so the rule is not stricter than it says. */
    @Test
    public void constructor_endOneMinuteAfterStart_accepted() throws PiplupBotException {
        assertEquals("[E][ ] meeting (from: Oct 2 2019 02:00 PM to: Oct 2 2019 02:01 PM)",
                new Event("meeting", "2019-10-02 1400", "2019-10-02 1401").toString());
    }

    /**
     * The message shows both times as the bot read them. A date typed without a
     * time means midnight, which is how a one-day event typed as two bare dates
     * ends up refused -- and the message is where the user finds that out.
     */
    @Test
    public void constructor_endSameAsStart_messageShowsBothTimes() {
        PiplupBotException exception = assertThrows(PiplupBotException.class, () ->
                new Event("trip", "2019-10-02", "2019-10-02"));
        assertArrayEquals(new String[] {
            "Pip... An event has to end after it starts.",
            "This one starts Oct 2 2019 12:00 AM and ends Oct 2 2019 12:00 AM.",
        }, exception.getMessageLines());
    }

    // ---------- Telling one event from another ----------

    /**
     * Two events are the same only if they start and end together. Comparing
     * just the start -- the date an event is sorted by -- would treat these two
     * as one.
     */
    @Test
    public void isDuplicateOf_sameStartDifferentEnd_false() throws PiplupBotException {
        Event shortMeeting = new Event("meeting", "2019-10-02 1400", "2019-10-02 1500");
        Event longMeeting = new Event("meeting", "2019-10-02 1400", "2019-10-02 1700");
        assertFalse(shortMeeting.isDuplicateOf(longMeeting));
    }

    @Test
    public void isDuplicateOf_sameStartAndEnd_true() throws PiplupBotException {
        Event meeting = new Event("meeting", "2019-10-02 1400", "2019-10-02 1500");
        Event sameMeeting = new Event("Meeting", "2019-10-02 14:00", "2/10/2019 1500");
        assertTrue(meeting.isDuplicateOf(sameMeeting));
    }
}
