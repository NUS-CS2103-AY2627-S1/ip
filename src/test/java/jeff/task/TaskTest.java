package jeff.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void constructor_nullOrBlankDescription_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Task(null));
        assertThrows(IllegalArgumentException.class, () -> new Task(""));
        assertThrows(IllegalArgumentException.class, () -> new Task("   "));
    }

    @Test
    public void constructor_descriptionContainsPipe_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Task("buy milk | eggs"));
    }

    @Test
    public void getDescription_returnsDescriptionPassedToConstructor() {
        Task task = new Task("read book");
        assertEquals("read book", task.getDescription());
    }

    @Test
    public void constructor_validDescription_startsNotDone() {
        Task task = new Task("read book");
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void markAsDone_changesStatusIconToX() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void unmarkAsDone_afterMarkAsDone_changesStatusIconBackToBlank() {
        Task task = new Task("read book");
        task.markAsDone();
        task.unmarkAsDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void toSaveFormat_notDone_startsWithZero() {
        Task task = new Task("read book");
        assertEquals("0 | read book", task.toSaveFormat());
    }

    @Test
    public void toSaveFormat_done_startsWithOne() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("1 | read book", task.toSaveFormat());
    }

    @Test
    public void getChronologicalDate_plainTask_isEmpty() {
        Task task = new Task("read book");
        assertTrue(task.getChronologicalDate().isEmpty());
    }
}
