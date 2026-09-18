package habpyduck.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * Tests task list operations that change stored task order and status.
 */
public class TaskListTest {
    @Test
    public void addDeleteInsertAndRemoveLast_mixedOperations_updatesListOrder() {
        TaskList tasks = new TaskList();
        Todo firstTask = new Todo("first");
        Todo secondTask = new Todo("second");
        Todo insertedTask = new Todo("inserted");

        tasks.add(firstTask);
        tasks.add(secondTask);
        Task deletedTask = tasks.delete(0);
        tasks.insert(0, insertedTask);
        Task removedTask = tasks.removeLast();

        assertEquals(firstTask, deletedTask);
        assertEquals(secondTask, removedTask);
        assertEquals(1, tasks.size());
        assertEquals(insertedTask, tasks.get(0));
    }

    @Test
    public void markAsDoneAndNotDone_existingTask_updatesStatus() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        tasks.markAsDone(0);
        assertEquals("[T][X] read book", tasks.get(0).toString());

        tasks.markAsNotDone(0);
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void findByKeyword_matchingDescriptions_returnsMatchingTasksInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy milk"));
        tasks.add(new Todo("return Book"));

        ArrayList<Task> matchingTasks = tasks.findByKeyword("book");

        assertEquals(2, matchingTasks.size());
        assertEquals("[T][ ] read book", matchingTasks.get(0).toString());
        assertEquals("[T][ ] return Book", matchingTasks.get(1).toString());
    }

    @Test
    public void findByKeyword_noMatchingDescriptions_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        ArrayList<Task> matchingTasks = tasks.findByKeyword("milk");

        assertEquals(0, matchingTasks.size());
    }

    @Test
    public void findByKeyword_keywordAppearsOnlyOutsideDescription_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return item", LocalDateTime.of(2026, 8, 25, 18, 0)));
        tasks.add(new Event("project meeting", LocalDateTime.of(2026, 8, 25, 14, 0),
                LocalDateTime.of(2026, 8, 25, 16, 0)));
        tasks.addTag(0, "#book");

        ArrayList<Task> matchingTasks = tasks.findByKeyword("book");

        assertEquals(0, matchingTasks.size());
    }

    @Test
    public void addTag_existingTask_addsTagToTaskDisplay() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        int taskIndex = 0;
        boolean hasAddedTag = tasks.addTag(taskIndex, "#fun");

        assertTrue(hasAddedTag);
        assertEquals("[T][ ] read book #fun", tasks.get(taskIndex).toString());
    }

    @Test
    public void addTag_duplicateTag_doesNotAddTagAgain() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        tasks.addTag(0, "#fun");
        boolean hasAddedTag = tasks.addTag(0, "#fun");

        assertFalse(hasAddedTag);
        assertEquals("[T][ ] read book #fun", tasks.get(0).toString());
    }

    @Test
    public void removeTag_existingTag_removesTagFromTaskDisplay() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        tasks.addTag(0, "#fun");
        boolean hasRemovedTag = tasks.removeTag(0, "#fun");

        assertTrue(hasRemovedTag);
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void findByTag_matchingTags_returnsTaggedTasksInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy milk"));
        tasks.add(new Todo("watch movie"));
        tasks.addTag(0, "#fun");
        tasks.addTag(2, "#fun");

        ArrayList<Task> matchingTasks = tasks.findByTag("#fun");

        assertEquals(2, matchingTasks.size());
        assertEquals("[T][ ] read book #fun", matchingTasks.get(0).toString());
        assertEquals("[T][ ] watch movie #fun", matchingTasks.get(1).toString());
    }

    @Test
    public void constructorAndReplaceAll_givenSourceLists_copiesTasksIntoList() {
        ArrayList<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new Todo("initial"));
        TaskList tasks = new TaskList(initialTasks);
        initialTasks.add(new Todo("should not appear"));

        ArrayList<Task> replacementTasks = new ArrayList<>();
        replacementTasks.add(new Todo("replacement"));
        tasks.replaceAll(replacementTasks);
        replacementTasks.add(new Todo("should also not appear"));

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] replacement", tasks.get(0).toString());
    }

    @Test
    public void asList_returnedListIsChanged_originalTaskListIsUnchanged() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        ArrayList<Task> copiedTasks = tasks.asList();
        copiedTasks.add(new Todo("extra"));
        copiedTasks.clear();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }
}
