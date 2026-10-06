package ducky;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Task}.
 */
class TaskTest {

    // Tests that construction preserves a task's description and incomplete status.
    @Test
    void constructor_descriptionAndIncompleteStatusSet() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
    }


    // Tests retrieval of a task's description.
    @Test
    void getDescription_taskDescriptionReturned() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
    }


    // Tests that a newly created task is incomplete.
    @Test
    void isDone_newTask_falseReturned() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
    }


    // Tests the status icon for an incomplete task.
    @Test
    void getStatusIcon_newTask_spaceReturned() {
        Task task = new Task("read book");

        assertEquals(" ", task.getStatusIcon());
    }


    // Tests the status icon for a completed task.
    @Test
    void getStatusIcon_completedTask_xReturned() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("X", task.getStatusIcon());
    }


    // Tests marking an incomplete task as complete.
    @Test
    void markAsDone_incompleteTask_taskBecomesDone() {
        Task task = new Task("read book");

        task.markAsDone();

        assertTrue(task.isDone());
    }


    // Tests that marking a completed task again leaves it completed.
    @Test
    void markAsDone_completedTask_remainsDone() {
        Task task = new Task("read book");

        task.markAsDone();
        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
    }


    // Tests unmarking a completed task.
    @Test
    void unmark_completedTask_taskBecomesIncomplete() {
        Task task = new Task("read book");

        task.markAsDone();
        task.unmark();

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }


    // Tests that unmarking an incomplete task leaves it incomplete.
    @Test
    void unmark_incompleteTask_remainsIncomplete() {
        Task task = new Task("read book");

        task.unmark();

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }


    // Tests the display string for an incomplete task.
    @Test
    void toString_incompleteTask_formattedDescriptionReturned() {
        Task task = new Task("read book");

        assertEquals("[ ] read book", task.toString());
    }


    // Tests the display string for a completed task.
    @Test
    void toString_completedTask_formattedDescriptionReturned() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("[X] read book", task.toString());
    }


    // Tests tag display and removal of duplicate tags.
    @Test
    void constructor_duplicateTags_uniqueTagsDisplayed() {
        Task task = new Task("watch movie", List.of("fun", "weekend", "fun"));

        assertEquals(List.of("fun", "weekend"), task.getTags());
        assertEquals("[ ] watch movie #fun #weekend", task.toString());
    }


    // Tests that searching is case-insensitive and returns matching tasks in their original order.
    @Test
    void find_keywordMatchingDescriptions_matchingTasksReturned() {
        TaskList tasks = new TaskList(List.of(
                new ToDo("Read a book"),
                new ToDo("Buy groceries"),
                new ToDo("Return the BOOK")));

        List<Task> matchingTasks = tasks.find("book");

        assertEquals(2, matchingTasks.size());
        assertEquals("Read a book", matchingTasks.get(0).getDescription());
        assertEquals("Return the BOOK", matchingTasks.get(1).getDescription());
    }


    // Tests searching for a task by tag.
    @Test
    void find_keywordMatchingTag_matchingTaskReturned() {
        Task taggedTask = new ToDo("Watch movie", List.of("fun"));
        TaskList tasks = new TaskList(List.of(
                taggedTask,
                new ToDo("Buy groceries", List.of("errands"))));

        assertEquals(List.of(taggedTask), tasks.find("#FUN"));
        assertEquals(List.of(taggedTask), tasks.find("fun"));
    }
}
