package dude.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list collection and search behavior.
 */
public class TaskListTest {
    @Test
    public void find_isCaseInsensitive() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("buy milk")));

        assertEquals(List.of("read book"), tasks.find("BOOK").stream()
                .map(Task::getDescription)
                .toList());
    }

    @Test
    public void find_preservesOriginalOrder() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("return book"),
                new Todo("buy milk")));

        assertEquals(List.of("read book", "return book"), tasks.find("book").stream()
                .map(Task::getDescription)
                .toList());
    }

    @Test
    public void find_returnsEmptyListWhenThereAreNoMatches() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), tasks.find("train"));
    }

    @Test
    public void findMatches_preservesOriginalIndexes() {
        TaskList tasks = new TaskList(List.of(
                new Todo("buy milk"),
                new Todo("read book"),
                new Todo("return book")));

        assertEquals(List.of(1, 2), tasks.findMatches("book").stream()
                .map(TaskMatch::index)
                .toList());
    }

    @Test
    public void findDuplicateIndexes_normalizesCaseAndWhitespace() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("buy milk"),
                new Todo(" READ   BOOK ")));

        assertEquals(List.of(0, 2), tasks.findDuplicateIndexes("Read book"));
    }

    @Test
    public void replace_returnsPreviousTaskAndPreservesListOrder() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first, second));
        Todo replacement = new Todo("replacement");

        assertEquals(second, tasks.replace(1, replacement));
        assertEquals(List.of(first, replacement), tasks.asList());
    }
}
