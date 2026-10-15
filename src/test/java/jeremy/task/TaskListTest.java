package jeremy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jeremy.exception.JeremyException;

public class TaskListTest {

    private TaskList taskList;

    @BeforeEach
    public void setUp() {
        taskList = new TaskList();
    }

    @Test
    public void add_singleTask_sizeIncreasesAndTaskRetrievable() throws JeremyException {
        Todo todo = new Todo("read book");
        taskList.add(todo);

        assertEquals(1, taskList.size());
        assertEquals(todo, taskList.get(1));
    }

    @Test
    public void containsEquivalent_sameTodo_returnsTrue() {
        taskList.add(new Todo("read book"));

        assertTrue(taskList.containsEquivalent(new Todo("read book")));
    }

    @Test
    public void containsEquivalent_differentTask_returnsFalse() {
        taskList.add(new Todo("read book"));

        assertFalse(taskList.containsEquivalent(new Todo("return book")));
    }

    @Test
    public void constructor_fromExistingList_copiesRatherThanAliases() {
        List<Task> seed = new java.util.ArrayList<>();
        seed.add(new Todo("borrowed task"));

        TaskList copy = new TaskList(seed);
        seed.add(new Todo("added after copy"));

        // Mutating the original list after construction must not affect the copy.
        assertEquals(1, copy.size());
    }

    @Test
    public void delete_validIndex_removesAndReturnsTask() throws JeremyException {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        taskList.add(first);
        taskList.add(second);

        Task removed = taskList.delete(1);

        assertEquals(first, removed);
        assertEquals(1, taskList.size());
        assertEquals(second, taskList.get(1));
    }

    @Test
    public void delete_emptyList_throwsWithNothingToDeleteMessage() {
        JeremyException ex = assertThrows(JeremyException.class, () -> taskList.delete(1));
        assertTrue(ex.getMessage().contains("nothing to delete"));
    }

    @Test
    public void delete_indexOutOfRange_throwsJeremyException() throws JeremyException {
        taskList.add(new Todo("only task"));

        assertThrows(JeremyException.class, () -> taskList.delete(5));
        assertThrows(JeremyException.class, () -> taskList.delete(0));
    }

    @Test
    public void markDone_validIndex_setsDoneAndReturnsSameTask() throws JeremyException {
        Todo todo = new Todo("submit form");
        taskList.add(todo);

        Task marked = taskList.markDone(1);

        assertTrue(marked.isDone());
        assertEquals(todo, marked);
    }

    @Test
    public void markNotDone_previouslyMarkedTask_setsNotDone() throws JeremyException {
        Todo todo = new Todo("submit form");
        taskList.add(todo);
        taskList.markDone(1);

        Task unmarked = taskList.markNotDone(1);

        assertFalse(unmarked.isDone());
    }

    @Test
    public void markDone_emptyList_throwsWithNothingToMarkMessage() {
        JeremyException ex = assertThrows(JeremyException.class, () -> taskList.markDone(1));
        assertTrue(ex.getMessage().contains("nothing to mark"));
    }

    @Test
    public void find_keyword_returnsMatchingTasks() {
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("return book"));
        taskList.add(new Todo("go jogging"));

        List<Task> matches = taskList.find("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
        assertEquals("return book", matches.get(1).getDescription());
    }

    @Test
    public void find_isCaseInsensitive() {
        taskList.add(new Todo("Read BOOK"));

        assertEquals(1, taskList.find("book").size());
    }

    @Test
    public void find_noMatches_returnsEmptyList() {
        taskList.add(new Todo("read book"));

        assertTrue(taskList.find("movie").isEmpty());
    }

    @Test
    public void isEmpty_reflectsCurrentState() throws JeremyException {
        assertTrue(taskList.isEmpty());
        taskList.add(new Todo("something"));
        assertFalse(taskList.isEmpty());
    }
}
