package bags.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import bags.exception.BagsException;

/**
 * Tests the {@link Event} class.
 */
class EventTest {

    @Test
    void constructor_validEvent_parsesDatesAndFormatsTask() throws BagsException {
        Event event = new Event("Project meeting", "2026-09-13 10:00", "2026-09-13 11:00");

        assertEquals(LocalDateTime.of(2026, 9, 13, 10, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 9, 13, 11, 0), event.getTo());
        assertEquals("[E][ ] Project meeting (from: 13/09/2026 10:00AM to: 13/09/2026 11:00AM)",
                event.toString());
        assertEquals("E | [ ] | Project meeting | 2026-09-13 10:00 | 2026-09-13 11:00",
                event.parseEvent());
    }

    @Test
    void createTask_validCommand_createsEvent() throws BagsException {
        Event event = Event.createTask(
                "event Project meeting /from 2026-09-13 10:00 /to 2026-09-13 11:00");

        assertEquals("Project meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2026, 9, 13, 10, 0), event.getFrom());
        assertEquals(TaskType.EVENT, event.getType());
        assertEquals(LocalDateTime.of(2026, 9, 13, 11, 0), event.getTo());
    }

    @Test
    void createTask_missingDescription_throwsException() {
        BagsException exception = assertThrows(BagsException.class, () ->
                Event.createTask("event /from 2026-09-13 10:00 /to 2026-09-13 11:00")
        );

        assertTrue(exception.getMessage().contains("Missing task description"));
    }

    @Test
    void createTask_missingOrMisorderedTimeKeywords_throwsException() {
        BagsException missingKeyword = assertThrows(BagsException.class, () ->
                Event.createTask("event Project meeting")
        );
        BagsException misorderedKeywords = assertThrows(BagsException.class, () ->
                Event.createTask("event Project meeting /to 2026-09-13 11:00"
                        + " /from 2026-09-13 10:00")
        );

        assertTrue(missingKeyword.getMessage().contains("Missing /from or /to"));
        assertTrue(misorderedKeywords.getMessage().contains("Missing /from or /to"));
    }

    @Test
    void createTask_missingTimeframe_throwsException() {
        BagsException exception = assertThrows(BagsException.class, () ->
                Event.createTask("event Project meeting /from /to")
        );

        assertTrue(exception.getMessage().contains("Missing timeframe"));
    }

    @Test
    void constructor_invalidDate_throwsException() {
        BagsException exception = assertThrows(BagsException.class, () ->
                new Event("Project meeting", "2026-13-30 10:00", "2026-09-13 11:00")
        );

        assertTrue(exception.getMessage().contains("Invalid date"));
    }

    @Test
    void constructor_startDateAfterEndDate_throwsException() {
        BagsException exception = assertThrows(BagsException.class, () ->
                new Event("Project meeting", "2026-09-13 11:00", "2026-09-13 10:00")
        );

        assertEquals("Hmm, the event can't end before it starts. Check the dates and try again.",
                exception.getMessage());
    }
}
