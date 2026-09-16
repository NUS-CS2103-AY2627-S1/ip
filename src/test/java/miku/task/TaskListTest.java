package miku.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list operations that support Miku's commands. */
class TaskListTest {

    @Test
    void findByDescription_mixedCaseKeyword_returnsOnlyMatchingTasksInListOrder() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("buy groceries"),
                new Todo("return BOOK")));

        List<Task> matches = tasks.findByDescription("BoOk");

        assertEquals(2, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
        assertEquals("return BOOK", matches.get(1).getDescription());
    }

    @Test
    void findByDescription_noMatch_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), tasks.findByDescription("meeting"));
    }

    @Test
    void replace_existingIndex_returnsOriginalAndUpdatesTask() {
        Todo originalTask = new Todo("read book");
        Todo replacementTask = new Todo("write notes");
        TaskList tasks = new TaskList(List.of(originalTask));

        Task replacedTask = tasks.replace(0, replacementTask);

        assertSame(originalTask, replacedTask);
        assertSame(replacementTask, tasks.get(0));
    }
}
