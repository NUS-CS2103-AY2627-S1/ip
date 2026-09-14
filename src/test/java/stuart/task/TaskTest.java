package stuart.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void getStatusIcon_newTask_returnsSpace() {
        ToDos task = new ToDos("eat malatang");
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void getStatusIcon_afterMarkAsDone_returnsX() {
        ToDos task = new ToDos("eat malatang");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void getStatusIcon_markAsDoneThenMarkAsNotDone_returnsSpace() {
        ToDos task = new ToDos("eat malatang");
        task.markAsDone();
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void toString_notDone_returnsFormattedLineWithSpaceIcon() {
        ToDos task = new ToDos("eat malatang");
        assertEquals("[T][ ] eat malatang", task.toString());
    }

    @Test
    public void toString_done_returnsFormattedLineWithXIcon() {
        ToDos task = new ToDos("eat malatang");
        task.markAsDone();
        assertEquals("[T][X] eat malatang", task.toString());
    }

    @Test
    public void toSaveFormat_notDone_returnsPipeDelimitedLineWithZero() {
        ToDos task = new ToDos("eat malatang");
        assertEquals("T | 0 | eat malatang", task.toSaveFormat());
    }

    @Test
    public void toSaveFormat_done_returnsPipeDelimitedLineWithOne() {
        ToDos task = new ToDos("eat malatang");
        task.markAsDone();
        assertEquals("T | 1 | eat malatang", task.toSaveFormat());
    }

    @Test
    public void containsKeyword_descriptionContainsKeyword_returnsTrue() {
        ToDos task = new ToDos("eat malatang");
        assertTrue(task.containsKeyword("malatang"));
    }

    @Test
    public void containsKeyword_descriptionDoesNotContainKeyword_returnsFalse() {
        ToDos task = new ToDos("eat malatang");
        assertFalse(task.containsKeyword("sushi"));
    }

    @Test
    public void containsKeyword_exactMatch_returnsTrue() {
        ToDos task = new ToDos("eat malatang");
        assertTrue(task.containsKeyword("eat malatang"));
    }

    @Test
    public void containsKeyword_caseSensitiveMismatch_returnsFalse() {
        ToDos task = new ToDos("eat malatang");
        assertFalse(task.containsKeyword("Malatang"));
    }

    @Test
    public void containsKeyword_emptyKeyword_returnsTrue() {
        ToDos task = new ToDos("eat malatang");
        assertTrue(task.containsKeyword(""));
    }

    @Test
    public void hasSameDetails_sameDescriptionSameClass_returnsTrue() {
        ToDos task = new ToDos("eat malatang");
        ToDos other = new ToDos("eat malatang");
        assertTrue(task.hasSameDetails(other));
    }

    @Test
    public void hasSameDetails_differentDescription_returnsFalse() {
        ToDos task = new ToDos("eat malatang");
        ToDos other = new ToDos("eat hotpot");
        assertFalse(task.hasSameDetails(other));
    }

    @Test
    public void hasSameDetails_differentConcreteClass_returnsFalse() {
        ToDos task = new ToDos("eat malatang");
        Deadlines other = new Deadlines("eat malatang", LocalDate.of(2019, 10, 15));
        assertFalse(task.hasSameDetails(other));
    }
}
