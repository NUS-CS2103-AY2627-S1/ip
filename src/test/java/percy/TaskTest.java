package percy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the two non-trivial behaviours of the {@link Task} hierarchy:
 * <ul>
 *   <li>{@code toFileFormat()} &ndash; the on-disk encoding that {@link Storage} depends on</li>
 *   <li>{@code getDescription()} &ndash; the human-friendly rendering shown to the user</li>
 * </ul>
 * Both are overridden down the hierarchy, so each subclass is checked.
 */
public class TaskTest {

    // ---- getStatusIcon() / mark / unmark ---------------------------------

    @Test
    public void statusIcon_tracksDoneState() {
        Task t = new Todo("read book");
        assertEquals("[ ]", t.getStatusIcon());

        t.setDone(true);
        assertEquals("[X]", t.getStatusIcon());

        t.setDone(false);
        assertEquals("[ ]", t.getStatusIcon());
    }

    // ---- toFileFormat() -------------------------------------------------------

    @Test
    public void toFileFormat_todo_hasTypeDoneFlagAndDescription() {
        Todo t = new Todo("read book");
        assertEquals("T | 0 | read book", t.toFileFormat());

        t.setDone(true);
        assertEquals("T | 1 | read book", t.toFileFormat());
    }

    @Test
    public void toFileFormat_deadline_appendsIsoDate() {
        Deadline d = new Deadline("return book", LocalDate.of(2019, 10, 15));
        assertEquals("D | 0 | return book | 2019-10-15", d.toFileFormat());

        d.setDone(true);
        assertEquals("D | 1 | return book | 2019-10-15", d.toFileFormat());
    }

    @Test
    public void toFileFormat_event_appendsIsoDateRange() {
        Event e = new Event("project", LocalDate.of(2019, 10, 10), LocalDate.of(2019, 10, 12));
        assertEquals("E | 0 | project | 2019-10-10 | 2019-10-12", e.toFileFormat());
    }

    // ---- getDescription() --------------------------------------------------

    @Test
    public void getDescription_todo_isJustTheDescription() {
        assertEquals("read book", new Todo("read book").getDescription());
    }

    @Test
    public void getDescription_deadline_appendsFriendlyByDate() {
        Deadline d = new Deadline("return book", LocalDate.of(2019, 10, 15));
        assertEquals("return book (by: Oct 15 2019)", d.getDescription());
    }

    @Test
    public void getDescription_event_appendsFriendlyFromToDates() {
        Event e = new Event("project", LocalDate.of(2019, 10, 10), LocalDate.of(2019, 10, 12));
        assertEquals("project (from: Oct 10 2019 to: Oct 12 2019)", e.getDescription());
    }

    @Test
    public void getRawDescription_deadline_omitsTheDateSuffix() {
        Deadline d = new Deadline("return book", LocalDate.of(2019, 10, 15));
        assertEquals("return book", d.getRawDescription());
    }

    // ---- in-place edits (C-Update) ---------------------------------------

    @Test
    public void setDescription_changesTheRenderedDescription() {
        Deadline d = new Deadline("return book", LocalDate.of(2019, 10, 15));
        d.setDescription("return the library book");
        assertEquals("return the library book (by: Oct 15 2019)", d.getDescription());
    }

    @Test
    public void setBy_changesOnlyTheDueDate() {
        Deadline d = new Deadline("return book", LocalDate.of(2019, 10, 15));
        d.setBy(LocalDate.of(2019, 12, 1));
        assertEquals(LocalDate.of(2019, 12, 1), d.getBy());
        assertEquals("D | 0 | return book | 2019-12-01", d.toFileFormat());
    }

    @Test
    public void setTo_changesOnlyTheEndDate() {
        Event e = new Event("project", LocalDate.of(2019, 10, 10), LocalDate.of(2019, 10, 12));
        e.setTo(LocalDate.of(2019, 10, 20));
        assertEquals(LocalDate.of(2019, 10, 10), e.getFrom());
        assertEquals("project (from: Oct 10 2019 to: Oct 20 2019)", e.getDescription());
    }
}
