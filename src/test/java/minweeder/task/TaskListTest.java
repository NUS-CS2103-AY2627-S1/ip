package minweeder.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void add_singleTask_increasesSizeAndIsRetrievable() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");

        tasks.add(todo);

        assertEquals(1, tasks.size());
        assertSame(todo, tasks.get(0));
    }

    @Test
    public void size_emptyList_returnsZero() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.size());
    }

    @Test
    public void delete_existingIndex_removesAndReturnsTask() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("read book");
        Todo second = new Todo("return book");
        tasks.add(first);
        tasks.add(second);

        Task deleted = tasks.delete(0);

        assertSame(first, deleted);
        assertEquals(1, tasks.size());
        assertSame(second, tasks.get(0));
    }

    @Test
    public void findIndices_keywordMatchesSomeTasks_returnsIndicesOfMatchingTasks() {
        TaskList tasks = new TaskList();
        Todo joinClub = new Todo("join sports club");
        Todo readBook = new Todo("read book");
        Todo returnBook = new Todo("return book");
        tasks.add(joinClub);
        tasks.add(readBook);
        tasks.add(returnBook);

        List<Integer> matchingIndices = tasks.findIndices("book");

        // The indices are positions in the full list, not positions among the matches.
        assertEquals(List.of(1, 2), matchingIndices);
    }

    @Test
    public void findIndices_keywordMatchesNoTasks_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        List<Integer> matchingIndices = tasks.findIndices("homework");

        assertTrue(matchingIndices.isEmpty());
    }

    @Test
    public void findIndices_keywordDifferentCase_matchesCaseInsensitively() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        List<Integer> matchingIndices = tasks.findIndices("BOOK");

        assertEquals(List.of(0), matchingIndices);
    }

    @Test
    public void findIndices_emptyList_returnsEmptyList() {
        TaskList tasks = new TaskList();

        List<Integer> matchingIndices = tasks.findIndices("book");

        assertTrue(matchingIndices.isEmpty());
    }

    @Test
    public void stream_multipleTasks_streamsInInsertionOrder() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("read book");
        Todo second = new Todo("return book");
        tasks.add(first);
        tasks.add(second);

        List<Task> streamed = tasks.stream().collect(Collectors.toList());

        assertEquals(List.of(first, second), streamed);
    }
}
