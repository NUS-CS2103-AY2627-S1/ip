package dog.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void toSaveFormat_validDescription_returnsSaveFormat() {
        // Test done event
        Event doneEvent = new Event("cry to sleep",
                java.time.LocalDate.of(2026, 8, 8),
                java.time.LocalDate.of(2026, 8, 10),
                true);
        assertEquals("E | X | 2026-08-08 | 2026-08-10 | cry to sleep", doneEvent.toSaveFormat());

        // Test pending event
        Event pendingEvent = new Event("survive 2103t",
                java.time.LocalDate.of(2026, 8, 9),
                java.time.LocalDate.of(2026, 8, 11),
                false);
        assertEquals("E |   | 2026-08-09 | 2026-08-11 | survive 2103t", pendingEvent.toSaveFormat());

        // Test description with spaces
        Event spacesEvent = new Event("  a  b   c ", java.time.LocalDate.of(2026,
                8, 10),
                java.time.LocalDate.of(2026, 8, 12),
                false);
        assertEquals("E |   | 2026-08-10 | 2026-08-12 |   a  b   c ", spacesEvent.toSaveFormat());

        // Test description with pipes
        Event pipesEvent = new Event(" || d | e |   | f ",
                java.time.LocalDate.of(2026, 8, 11),
                java.time.LocalDate.of(2026, 8, 13),
                true);
        assertEquals("E | X | 2026-08-11 | 2026-08-13 |  || d | e |   | f ", pipesEvent.toSaveFormat());
    }

    @Test
    public void fromSaveFormat_validFormat_returnsEvent() {
        // Test done event
        Event doneEvent = Event.fromSaveFormat("E | X | 2026-08-08 | 2026-08-10 | cry to sleep");
        assertEquals("E | X | 2026-08-08 | 2026-08-10 | cry to sleep", doneEvent.toSaveFormat());

        // Test pending event
        Event pendingEvent = Event.fromSaveFormat("E |   | 2026-08-09 | 2026-08-11 | survive 2103t");
        assertEquals("E |   | 2026-08-09 | 2026-08-11 | survive 2103t", pendingEvent.toSaveFormat());
    }

    @Test
    public void fromSaveFormat_specialCharacters_preservesDescription() {
        // Test description with special characters
        Event specialCharEvent = Event.fromSaveFormat("E | X | 2026-08-08 | 2026-08-10 | "
                + "cry @home #urgent!#%&^(*^_)_){}/.,./';}{|");
        assertEquals("E | X | 2026-08-08 | 2026-08-10 | cry @home #urgent!#%&^(*^_)_){}/.,./';}{|",
                specialCharEvent.toSaveFormat());

        // Test description with pipe characters
        Event pipeCharPendingEvent = Event.fromSaveFormat("E |   | 2026-08-08 | 2026-08-10 | X | cry");
        assertEquals("E |   | 2026-08-08 | 2026-08-10 | X | cry", pipeCharPendingEvent.toSaveFormat());

        // Test description with pipe characters
        Event pipeCharDoneEvent = Event.fromSaveFormat("E | X | 2026-08-08 | 2026-08-10 |   | fard");
        assertEquals("E | X | 2026-08-08 | 2026-08-10 |   | fard", pipeCharDoneEvent.toSaveFormat());
    }

    @Test
    public void fromSaveFormat_invalidFormat_returnsNull() {
        // Test missing parts
        assertNull(Event.fromSaveFormat("E | 2026-08-08 | 2026-08-10 | broken"));

        // Test missing spaces
        assertNull(Event.fromSaveFormat("E| X | 2026-08-08 | 2026-08-10 | i need space"));
        assertNull(Event.fromSaveFormat("E| | 2026-08-08 | 2026-08-10 | give me space"));
        assertNull(Event.fromSaveFormat("E | X | 2026-08-08 | 2026-08-10 |give me more space"));

        // Test invalid/missing status icon
        assertNull(Event.fromSaveFormat("E |  | give me more space"));
        assertNull(Event.fromSaveFormat("E | F | what the F"));

        // Test invalid/missing task type icon
        assertNull(Event.fromSaveFormat("T | X | 2026-08-08 | 2026-08-10 | im dead :skull:"));
        assertNull(Event.fromSaveFormat("D |   | 2026-08-08 | 2026-08-10 | d"));
        assertNull(Event.fromSaveFormat(" |   | 2026-08-08 | 2026-08-10 | where is the e"));

        // Test invalid/missing dates
        assertNull(Event.fromSaveFormat("E | X | 08-08-2026 | 2026-08-10 | invalid date"));
        assertNull(Event.fromSaveFormat("E | X | 2026-08-08 | 08-08-2026 | another invalid date"));
        assertNull(Event.fromSaveFormat("E | X | 2026-30-08 | 08-08-2026 | another invalid date"));
        assertNull(Event.fromSaveFormat("E | X | 2026-08-08 | cant find a date"));

        // Test completely wrong format
        assertNull(Event.fromSaveFormat("E | X | bro what | 2026-08-10"));
        assertNull(Event.fromSaveFormat("E | X | huh | ??????"));

        // Test empty string
        assertNull(Event.fromSaveFormat(""));

        // Test null
        assertNull(Event.fromSaveFormat(null));
    }

    @Test
    public void fromSaveFormat_roundTrip_producesSameString() {
        // Test round-trip consistency
        String original = "E | X | 2026-08-08 | 2026-08-10 | Test task";
        Event event = Event.fromSaveFormat(original);
        assertEquals(original, event.toSaveFormat());
    }
}
