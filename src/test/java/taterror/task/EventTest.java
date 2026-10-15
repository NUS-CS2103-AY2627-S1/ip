package taterror.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Event}.
 */
public class EventTest {

    @Test
    public void toDisplayDetail_rendersFromAndToRawText() {
        Event event = new Event("standup", "9am", "10am");
        assertEquals("(from: 9am to: 10am)", event.toDisplayDetail());
    }

    @Test
    public void toSaveDetail_prependsPipeSeparatorsForBothFields() {
        Event event = new Event("standup", "9am", "10am");
        assertEquals(" | 9am | 10am", event.toSaveDetail());
    }

    @Test
    public void toString_includesTypeCodeStatusAndFromToDetail() {
        Event event = new Event("standup", "9am", "10am");
        assertEquals("[E][ ] standup (from: 9am to: 10am)", event.toString());
    }
}
