package botzilla.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import botzilla.BotzillaException;

/**
 * Tests that tags on an EventTask are appended after the "from"/"to"
 * fields/label, so they always land as the last field regardless of
 * task type.
 */
public class EventTaskTagTest {

    @Test
    void toString_withTag_appendsHashtagAfterFromToLabel() throws BotzillaException {
        EventTask task = new EventTask("party", "later", "even later");
        task.addTag("fun");

        String result = task.toString();

        assertEquals("[E][ ] party (from: later to: even later) #fun", result);
    }

    @Test
    void toFileString_withTag_appendsTagsFieldAfterFromToFields() throws BotzillaException {
        EventTask task = new EventTask("party", "later", "even later");
        task.addTag("fun");

        String result = task.toFileString();

        assertEquals("E | 0 | party | later | even later | fun", result);
    }

    @Test
    void toFileString_withMultipleTags_joinsTagsWithComma() throws BotzillaException {
        EventTask task = new EventTask("party", "later", "even later");
        task.addTag("fun");
        task.addTag("social");

        String result = task.toFileString();

        assertEquals("E | 0 | party | later | even later | fun,social", result);
    }

    @Test
    void toFileString_noTags_omitsTagsField() throws BotzillaException {
        EventTask task = new EventTask("party", "later", "even later");

        assertEquals("E | 0 | party | later | even later", task.toFileString());
    }
}
