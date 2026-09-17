package mouse.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import mouse.MouseException;

/**
 * Tests for {@link Event} range checks and display.
 */
public class EventTest {
    @Test
    public void constructor_freeTextRange_keepsOriginalText() throws MouseException {
        Event event = new Event("meeting", "Mon 2pm", "4pm");
        assertEquals("[E][ ] meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    public void constructor_isoRange_formatsDates() throws MouseException {
        Event event = new Event("camp", "2019-12-02", "2019-12-03");
        assertTrue(event.toString().contains("Dec 02 2019"));
        assertTrue(event.toString().contains("Dec 03 2019"));
    }

    @Test
    public void constructor_startAfterEnd_throwsMouseException() {
        assertThrows(MouseException.class, () -> new Event("camp", "2019-12-03", "2019-12-02"));
        assertThrows(MouseException.class, () -> new Event("camp", "2019-12-02 1800", "2019-12-02 0900"));
    }

    @Test
    public void constructor_sameStartAndEnd_throwsMouseException() {
        assertThrows(MouseException.class, () -> new Event("camp", "2019-12-02", "2019-12-02"));
    }
}
