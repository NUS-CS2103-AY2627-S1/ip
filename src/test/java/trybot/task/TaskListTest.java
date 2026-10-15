package trybot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests TaskList's ordering, mutation, and snapshot guarantees.
 */
class TaskListTest {

    @Test
    void taskList_addGetRemove_maintainsOrderAndSize() {
        TaskList tasks = new TaskList();
        Task first = new Todo("first");
        Task second = new Todo("second");

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertEquals(first, tasks.get(0));
        assertEquals(second, tasks.get(1));
        assertEquals(first, tasks.remove(0));
        assertEquals(1, tasks.size());
        assertEquals(second, tasks.get(0));
    }

    @Test
    void taskList_nullTasks_rejected() {
        TaskList tasks = new TaskList();
        List<Task> nullTaskList = new ArrayList<>();
        nullTaskList.add(null);

        assertThrows(IllegalArgumentException.class, () -> new TaskList(null));
        assertThrows(IllegalArgumentException.class, () -> new TaskList(nullTaskList));
        assertThrows(IllegalArgumentException.class, () -> tasks.add(null));
    }

    @Test
    void taskList_toList_returnsImmutableCopy() {
        Task task = new Todo("read book");
        TaskList tasks = new TaskList(List.of(task));
        List<Task> snapshot = tasks.toList();

        assertEquals(List.of(task), snapshot);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("write report")));

        tasks.add(new Todo("write report"));
        assertEquals(1, snapshot.size());
        assertEquals(2, tasks.size());
    }

    @Test
    void taskList_constructor_copiesInputList() {
        Task task = new Todo("read book");
        java.util.ArrayList<Task> source = new java.util.ArrayList<>(List.of(task));
        TaskList tasks = new TaskList(source);

        source.clear();

        assertEquals(1, tasks.size());
        assertInstanceOf(Todo.class, tasks.get(0));
    }

    @Test
    void taskList_findByDescription_matchesCaseInsensitiveSubstringInOrder() {
        Task first = new Todo("Read a book");
        Task second = new Todo("return BOOK to the library");
        Task unrelated = new Todo("write report");
        TaskList tasks = new TaskList(List.of(first, second, unrelated));

        assertEquals(List.of(first, second), tasks.findByDescription("book"));
        assertEquals(List.of(first, second), tasks.findByDescription("  BOOK  "));
    }

    @Test
    void taskList_findByDescription_rejectsBlankKeyword() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(IllegalArgumentException.class, () -> tasks.findByDescription(null));
        assertThrows(IllegalArgumentException.class, () -> tasks.findByDescription("   "));
        assertEquals(List.of(), tasks.findByDescription("missing"));
    }

    @Test
    void task_addTag_formatsTagsAndAvoidsDuplicates() {
        Task task = new Todo("read book");

        task.addTag("  urgent ");
        task.addTag("urgent");
        task.addTag("school");

        assertEquals(List.of("urgent", "school"), task.getTags());
        assertEquals("[T][ ] read book #urgent #school", task.toString());
        assertThrows(IllegalArgumentException.class, () -> task.addTag("   "));

        task.clearTags();
        assertEquals(List.of(), task.getTags());
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void taskList_addDuplicateDetails_rejectsDuplicateRegardlessOfStatus() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        Todo duplicate = new Todo("read book");
        duplicate.markAsDone();

        assertThrows(IllegalArgumentException.class, () -> tasks.add(duplicate));
        assertEquals(1, tasks.size());
    }

    @Test
    void taskConstructors_rejectMissingRequiredFields() {
        assertThrows(IllegalArgumentException.class, () -> new Todo(null));
        assertThrows(IllegalArgumentException.class, () -> new Deadline("report", null));
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", "Monday", null));
        assertThrows(IllegalArgumentException.class,
                () -> new Event("meeting", "2024-01-01 0900", "2024-01-01 0900"));
    }
}
