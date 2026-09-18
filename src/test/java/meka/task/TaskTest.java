package meka.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the status changes and text representations provided by {@link Task}.
 */
public class TaskTest {

    @Test
    public void markAsDone_newTask_statusChangesToDone() {
        Task task = new Task("read a book");

        task.markAsDone();

        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsDone_alreadyMarkedTask_statusRemainsDone() {
        Task task = new Task("read a book");
        task.markAsDone();

        task.markAsDone();

        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void unmark_markedTask_statusChangesToNotDone() {
        Task task = new Task("read a book");
        task.markAsDone();

        task.unmark();

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void unmark_newTask_statusRemainsNotDone() {
        Task task = new Task("read a book");

        task.unmark();

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void getStatusIcon_newTask_returnsBlankIcon() {
        Task task = new Task("read a book");

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void getStatusIcon_markedTask_returnsDoneIcon() {
        Task task = new Task("read a book");
        task.markAsDone();

        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void containsKeyword_matchingKeywordWithDifferentCase_returnsTrue() {
        Task task = new Task("Read a Book");

        assertTrue(task.containsKeyword("book"));
    }

    @Test
    public void containsKeyword_keywordNotInDescription_returnsFalse() {
        Task task = new Task("read a book");

        assertFalse(task.containsKeyword("return"));
    }

    @Test
    public void toDataString_newTask_returnsIncompleteTaskData() {
        Task task = new Task("read a book");

        assertEquals("T | 0 | read a book", task.toDataString());
    }

    @Test
    public void toDataString_markedTask_returnsCompletedTaskData() {
        Task task = new Task("read a book");
        task.markAsDone();

        assertEquals("T | 1 | read a book", task.toDataString());
    }

    @Test
    public void formatDataString_newTaskAndCustomType_returnsIncompleteTaskData() {
        Task task = new Task("read a book");

        assertEquals("D | 0 | read a book", task.formatDataString("D"));
    }

    @Test
    public void formatDataString_markedTaskAndCustomType_returnsCompletedTaskData() {
        Task task = new Task("read a book");
        task.markAsDone();

        assertEquals("D | 1 | read a book", task.formatDataString("D"));
    }

    @Test
    public void toString_newTask_returnsDescriptionWithBlankIcon() {
        Task task = new Task("read a book");

        assertEquals("[ ] read a book", task.toString());
    }

    @Test
    public void toString_markedTask_returnsDescriptionWithDoneIcon() {
        Task task = new Task("read a book");
        task.markAsDone();

        assertEquals("[X] read a book", task.toString());
    }
}
