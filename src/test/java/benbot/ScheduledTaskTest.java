package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests scheduled deadline and event task behavior. */
class ScheduledTaskTest {
    @Test
    void deadline_dateAndTime_formatsForDisplayAndStorage() throws InvalidCommandException {
        Deadline deadline = new Deadline(
                "deadline", "return", "book", "/by", "2/12/2019", "1800");

        assertEquals("[D][ ] return book (by: Dec 02 2019 6:00 pm)", deadline.toString());
        assertEquals("deadline return book /by 2/12/2019 1800", deadline.toStorageString());
    }

    @Test
    void deadline_dateOnly_preservesDateOnlyStorageFormat() throws InvalidCommandException {
        Deadline deadline = new Deadline(
                "deadline", "return", "book", "/by", "1/12/2029");

        assertEquals("[D][ ] return book (by: Dec 01 2029)", deadline.toString());
        assertEquals("deadline return book /by 1/12/2029", deadline.toStorageString());
    }

    @Test
    void deadline_invalidDate_throwsException() {
        assertThrows(InvalidCommandException.class, () ->
                new Deadline("deadline", "return", "book", "/by", "not-a-date"));
    }

    @Test
    void event_validDates_formatsForDisplayAndStorage() throws InvalidCommandException {
        Event event = new Event(
                "event", "study", "/from", "2/12/2019", "1800", "/to", "2/12/2019", "2000");

        assertEquals("[E][ ] study (from: Dec 02 2019 6:00 pm to: Dec 02 2019 8:00 pm)",
                event.toString());
        assertEquals("event study /from 2/12/2019 1800 /to 2/12/2019 2000",
                event.toStorageString());
    }

    @Test
    void event_mixedDateAndTime_formatsEachEndpoint() throws InvalidCommandException {
        Event dateToTimeEvent = new Event(
                "event", "conference", "/from", "1/12/2029", "/to", "2/12/2029", "1800");
        Event timeToDateEvent = new Event(
                "event", "holiday", "/from", "1/12/2029", "1800", "/to", "2/12/2029");

        assertEquals("[E][ ] conference (from: Dec 01 2029 to: Dec 02 2029 6:00 pm)",
                dateToTimeEvent.toString());
        assertEquals("event conference /from 1/12/2029 /to 2/12/2029 1800",
                dateToTimeEvent.toStorageString());
        assertEquals("[E][ ] holiday (from: Dec 01 2029 6:00 pm to: Dec 02 2029)",
                timeToDateEvent.toString());
        assertEquals("event holiday /from 1/12/2029 1800 /to 2/12/2029",
                timeToDateEvent.toStorageString());
    }

    @Test
    void event_endBeforeStart_throwsException() {
        InvalidCommandException exception = assertThrows(InvalidCommandException.class, () ->
                new Event(
                        "event", "study", "/from", "2/12/2019", "2000",
                        "/to", "2/12/2019", "1800"));

        assertEquals("An event's end must be after its start.", exception.getMessage());
    }

    @Test
    void event_endAtStart_throwsException() {
        InvalidCommandException dateTimeException = assertThrows(InvalidCommandException.class, () ->
                new Event(
                        "event", "study", "/from", "2/12/2019", "1800",
                        "/to", "2/12/2019", "1800"));
        InvalidCommandException dateOnlyException = assertThrows(InvalidCommandException.class, () ->
                new Event(
                        "event", "holiday", "/from", "2/12/2019",
                        "/to", "2/12/2019"));

        assertEquals("An event's end must be after its start.", dateTimeException.getMessage());
        assertEquals("An event's end must be after its start.", dateOnlyException.getMessage());
    }
}
