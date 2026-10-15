package botzilla.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.jupiter.api.Test;

import botzilla.BotzillaException;

public class DeadlineTaskTest {

    // ---- constructor / getBy(): parseable date with time ----
    @Test
    void getBy_parseableDateWithTime_returnsMatchingDateTime() throws BotzillaException {
        DeadlineTask task = new DeadlineTask("return book", "2/12/2019 1800");

        LocalDateTime by = task.getBy();

        assertNotNull(by);
        assertEquals(2019, by.getYear());
        assertEquals(Month.DECEMBER, by.getMonth());
        assertEquals(2, by.getDayOfMonth());
        assertEquals(18, by.getHour());
        assertEquals(0, by.getMinute());
    }

    // ---- constructor / getBy(): parseable date without time ----
    @Test
    void getBy_parseableDateWithoutTime_returnsNonNullDate() throws BotzillaException {
        DeadlineTask task = new DeadlineTask("return book", "2/12/2019");

        LocalDateTime by = task.getBy();

        assertNotNull(by);
        assertEquals(2019, by.getYear());
        assertEquals(Month.DECEMBER, by.getMonth());
        assertEquals(2, by.getDayOfMonth());
    }

    // ---- constructor / getBy(): unparseable text falls back to raw string ----
    @Test
    void getBy_unparseableText_returnsNull() throws BotzillaException {
        DeadlineTask task = new DeadlineTask("return book", "whenever I feel like it");

        assertNull(task.getBy());
    }

    @Test
    void getBy_emptyByText_returnsNull() throws BotzillaException {
        DeadlineTask task = new DeadlineTask("return book", "");

        assertNull(task.getBy());
    }

    // ---- toFileString() ----
    @Test
    void toFileString_parseableDate_includesNameAndByField() throws BotzillaException {
        DeadlineTask task = new DeadlineTask("return book", "2/12/2019 1800");

        String result = task.toFileString();

        assertTrue(result.startsWith("D | 0 | return book | "));
    }

    @Test
    void toFileString_unparseableDate_fallsBackToRawText() throws BotzillaException {
        DeadlineTask task = new DeadlineTask("return book", "whenever I feel like it");

        String result = task.toFileString();

        assertEquals("D | 0 | return book | whenever I feel like it", result);
    }

    @Test
    void toFileString_doneTask_showsOneDoneFlag() throws BotzillaException {
        DeadlineTask task = new DeadlineTask("return book", "whenever I feel like it");
        task.mark();

        String result = task.toFileString();

        assertEquals("D | 1 | return book | whenever I feel like it", result);
    }

    // ---- toString() ----
    @Test
    void toString_parseableDate_containsIconNameAndByLabel() throws BotzillaException {
        DeadlineTask task = new DeadlineTask("return book", "2/12/2019 1800");

        String result = task.toString();

        assertTrue(result.contains("[D]"));
        assertTrue(result.contains("return book"));
        assertTrue(result.contains("(by: "));
    }

    @Test
    void toString_unparseableDate_showsRawTextInByLabel() throws BotzillaException {
        DeadlineTask task = new DeadlineTask("return book", "whenever I feel like it");

        String result = task.toString();

        assertEquals("[D][ ] return book (by: whenever I feel like it)", result);
    }

    @Test
    void toString_doneTask_showsXStatus() throws BotzillaException {
        DeadlineTask task = new DeadlineTask("return book", "whenever I feel like it");
        task.mark();

        String result = task.toString();

        assertEquals("[D][X] return book (by: whenever I feel like it)", result);
    }

    // ---- inherited mark()/unmark() behavior still works on subclass ----
    @Test
    void mark_thenUnmark_statusTogglesCorrectly() throws BotzillaException {
        DeadlineTask task = new DeadlineTask("return book", "2/12/2019 1800");

        task.mark();
        assertTrue(task.toFileString().startsWith("D | 1 | "));

        task.unmark();
        assertTrue(task.toFileString().startsWith("D | 0 | "));
    }

    // ---- tags: appended after the "by" field/label, not before ----
    @Test
    void toString_withTag_appendsHashtagAfterByLabel() throws BotzillaException {
        DeadlineTask task = new DeadlineTask("return book", "whenever I feel like it");
        task.addTag("urgent");

        String result = task.toString();

        assertEquals("[D][ ] return book (by: whenever I feel like it) #urgent", result);
    }

    @Test
    void toFileString_withTag_appendsTagsFieldAfterByField() throws BotzillaException {
        DeadlineTask task = new DeadlineTask("return book", "whenever I feel like it");
        task.addTag("urgent");

        String result = task.toFileString();

        assertEquals("D | 0 | return book | whenever I feel like it | urgent", result);
    }

    @Test
    void toFileString_withMultipleTags_joinsTagsWithComma() throws BotzillaException {
        DeadlineTask task = new DeadlineTask("return book", "whenever I feel like it");
        task.addTag("urgent");
        task.addTag("work");

        String result = task.toFileString();

        assertEquals("D | 0 | return book | whenever I feel like it | urgent,work", result);
    }
}
