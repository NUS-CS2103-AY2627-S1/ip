package tangent.tasklist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import tangent.exception.ErrorMessages;
import tangent.exception.TangentException;
import tangent.task.Task;
import tangent.task.TaskList;
import tangent.task.ToDo;

public class TaskListTest {
    @Test
    public void find_matchingTasks_returnsTasksInOriginalOrder() {
        TaskList tasks = new TaskList();
        Task first = new ToDo("read a book");
        Task second = new ToDo("complete assignment");
        Task third = new ToDo("return the book");

        tasks.add(first);
        tasks.add(second);
        tasks.add(third);

        TaskList matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertEquals(first, matches.get(0));
        assertEquals(third, matches.get(1));
    }

    @Test
    public void find_keywordMatchingIsCaseInsensitive_returnsMatchingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("Read a Book"));

        TaskList matches = tasks.find("book");

        assertEquals(1, matches.size());
        assertEquals("Read a Book", matches.get(0).getDescription());
    }

    @Test
    public void find_noMatchingTasks_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read a book"));

        TaskList matches = tasks.find("movie");

        assertEquals(0, matches.size());
    }

    @Test
    public void removeAtIndexes_usesOriginalIndexesAndReturnsOriginalOrder() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        Task third = new ToDo("third");
        Task fourth = new ToDo("fourth");
        TaskList tasks = new TaskList(first, second, third, fourth);

        List<Task> removedTasks = tasks.removeAtIndexes(List.of(3, 1));

        assertEquals(List.of(second, fourth), removedTasks);
        assertEquals(List.of(first, third), tasks.toList());
    }

    @Test
    public void restoreAtIndexes_restoresTasksToOriginalPositions() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        Task third = new ToDo("third");
        Task fourth = new ToDo("fourth");
        TaskList tasks = new TaskList(first, second, third, fourth);
        List<Integer> indexes = List.of(3, 1);
        List<Task> removedTasks = tasks.removeAtIndexes(indexes);

        tasks.restoreAtIndexes(indexes, removedTasks);

        assertEquals(List.of(first, second, third, fourth), tasks.toList());
    }

    @Test
    public void constructorsAndBasicOperations_manageTasks() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        Task inserted = new ToDo("inserted");
        TaskList tasks = new TaskList(first);

        tasks.add(second);
        tasks.add(1, inserted);

        assertEquals(3, tasks.size());
        assertFalse(tasks.isEmpty());
        assertEquals(first, tasks.get(0));
        assertEquals("inserted", tasks.get(1).getDescription());
        assertEquals("second", tasks.removeLast().getDescription());
        assertEquals(List.of(first, inserted), tasks.toList());
    }

    @Test
    public void listConstructorAndToList_copyCollections() {
        List<Task> original = new ArrayList<>(List.of(new ToDo("first")));
        TaskList tasks = new TaskList(original);
        List<Task> copy = tasks.toList();

        original.clear();
        copy.clear();

        assertEquals(1, tasks.size());
        assertEquals("first", tasks.get(0).getDescription());
    }

    @Test
    public void getTasksAtIndexes_returnsSelectedTasksInOriginalOrder() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        Task third = new ToDo("third");
        TaskList tasks = new TaskList(first, second, third);

        assertEquals(List.of(first, third), tasks.getTasksAtIndexes(List.of(2, 0)));
    }

    @Test
    public void validateIndexes_acceptsValidIndexesAndRejectsInvalidIndexes() throws TangentException {
        TaskList tasks = new TaskList(new ToDo("first"), new ToDo("second"));

        tasks.validateIndexes(List.of(0, 1));
        TangentException negative = assertThrows(TangentException.class, () -> tasks.validateIndexes(List.of(-1)));
        TangentException tooLarge = assertThrows(TangentException.class, () -> tasks.validateIndexes(List.of(2)));

        assertEquals(ErrorMessages.INVALID_TASK_INDEX_MESSAGE, negative.getMessage());
        assertEquals(ErrorMessages.INVALID_TASK_INDEX_MESSAGE, tooLarge.getMessage());
    }

    @Test
    public void find_emptyKeyword_returnsAllTasks() {
        TaskList tasks = new TaskList(new ToDo("first"), new ToDo("second"));

        assertEquals(tasks.toList(), tasks.find("").toList());
    }

    @Test
    public void emptyTaskList_reportsEmpty() {
        assertTrue(new TaskList().isEmpty());
    }
}
