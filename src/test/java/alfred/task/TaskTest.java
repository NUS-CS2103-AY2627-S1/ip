package alfred.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests save-line parsing, display text, and completion updates on {@link Task}.
 */
public class TaskTest {
    @Test
    public void fromSaveLine_validTodo_parsesPending() {
        Task task = Task.fromSaveLine("T | 0 | read book");
        assertNotNull(task);
        assertEquals("[T][ ] read book", task.getDisplayText());
        assertEquals("T | 0 | read book | UA", task.toSaveFormat());
        assertFalse(task.occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void fromSaveLine_validDeadline_parsesDate() {
        Task task = Task.fromSaveLine("D | 0 | return book | 2019-10-15");
        assertNotNull(task);
        assertEquals("[D][ ] return book (by: Oct 15 2019)", task.getDisplayText());
        assertEquals("D | 0 | return book | 2019-10-15 | UA", task.toSaveFormat());
    }

    @Test
    public void fromSaveLine_validEvent_parsesRange() {
        Task task = Task.fromSaveLine("E | 0 | camp | 2019-10-14 | 2019-10-16");
        assertNotNull(task);
        assertEquals("[E][ ] camp (from: Oct 14 2019 to: Oct 16 2019)", task.getDisplayText());
        assertEquals("E | 0 | camp | 2019-10-14 | 2019-10-16 | UA", task.toSaveFormat());
    }

    @Test
    public void fromSaveLine_doneBit_marksTaskDone() {
        Task task = Task.fromSaveLine("T | 1 | read book");
        assertNotNull(task);
        assertEquals("[T][X] read book", task.getDisplayText());
        assertEquals("T | 1 | read book | UA", task.toSaveFormat());
    }

    @Test
    public void fromSaveLine_nullOrBlank_returnsNull() {
        assertNull(Task.fromSaveLine(null));
        assertNull(Task.fromSaveLine(""));
        assertNull(Task.fromSaveLine("   "));
    }

    @Test
    public void fromSaveLine_unknownType_returnsNull() {
        assertNull(Task.fromSaveLine("X | 0 | mystery"));
    }

    @Test
    public void fromSaveLine_invalidStatus_returnsNull() {
        assertNull(Task.fromSaveLine("T | 2 | read book"));
        assertNull(Task.fromSaveLine("T | yes | read book"));
    }

    @Test
    public void fromSaveLine_tooFewParts_returnsNull() {
        assertNull(Task.fromSaveLine("T | 0"));
        assertNull(Task.fromSaveLine("not-a-save-line"));
        assertNull(Task.fromSaveLine("D | 0 | return book"));
        assertNull(Task.fromSaveLine("E | 0 | camp | 2019-10-14"));
    }

    @Test
    public void fromSaveLine_emptyDescription_returnsNull() {
        assertNull(Task.fromSaveLine("T | 0 | "));
        assertNull(Task.fromSaveLine("D | 0 |  | 2019-10-15"));
        assertNull(Task.fromSaveLine("E | 0 |  | 2019-10-15 | 2019-10-16"));
    }

    @Test
    public void fromSaveLine_legacyDescriptionA_staysUnarchived() {
        Task task = Task.fromSaveLine("T | 0 | A");
        assertNotNull(task);
        assertEquals("A", task.getDescription());
        assertFalse(task.isArchived());
        assertEquals("T | 0 | A | UA", task.toSaveFormat());
    }

    @Test
    public void fromSaveLine_archiveFlag_roundTrips() {
        Task live = Task.fromSaveLine("T | 0 | read book | UA");
        assertNotNull(live);
        assertFalse(live.isArchived());
        assertEquals("T | 0 | read book | UA", live.toSaveFormat());

        Task archived = Task.fromSaveLine("D | 0 | return book | 2019-10-15 | A");
        assertNotNull(archived);
        assertTrue(archived.isArchived());
        assertEquals("D | 0 | return book | 2019-10-15 | A", archived.toSaveFormat());
    }

    @Test
    public void archiveThenUnarchive_updatesSaveFlag() {
        Task task = new ToDo("read book");
        assertFalse(task.isArchived());
        task.archive();
        assertTrue(task.isArchived());
        assertEquals("T | 0 | read book | A", task.toSaveFormat());
        task.unarchive();
        assertFalse(task.isArchived());
        assertEquals("T | 0 | read book | UA", task.toSaveFormat());
    }

    @Test
    public void fromSaveLine_invalidArchiveFlag_returnsNull() {
        assertNull(Task.fromSaveLine("T | 0 | read book | extra"));
        assertNull(Task.fromSaveLine("D | 0 | return book | 2019-10-15 | YES"));
    }

    @Test
    public void fromSaveLine_invalidSavedDate_returnsNull() {
        assertNull(Task.fromSaveLine("D | 0 | return book | Sunday"));
        assertNull(Task.fromSaveLine("E | 0 | camp | 2019-10-14 | not-a-date"));
    }

    @Test
    public void markAsDoneThenNotDone_updatesDisplayAndSaveBit() {
        Task task = new ToDo("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        assertEquals("T | 1 | read book | UA", task.toSaveFormat());
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
        assertEquals("T | 0 | read book | UA", task.toSaveFormat());
    }

    @Test
    public void fromSaveLine_doneDeadlineWithTime_roundTrips() {
        Task task = Task.fromSaveLine("D | 1 | submit report | 2019-10-15T18:00:00");
        assertNotNull(task);
        assertEquals("[D][X] submit report (by: Oct 15 2019, 6:00PM)", task.getDisplayText());
        assertEquals("D | 1 | submit report | 2019-10-15T18:00:00 | UA", task.toSaveFormat());
        assertTrue(task.occursOn(LocalDate.of(2019, 10, 15)));
    }
}
