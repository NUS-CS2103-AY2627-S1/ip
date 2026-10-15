package greenchonk.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void constructor_emptyList_listIsEmpty() {
        TaskList tasks = new TaskList();

        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
    }

    @Test
    void constructor_sourceListChangedAfterConstruction_taskListUnaffected() {
        Task originalTask = new Todo("original");
        List<Task> sourceTasks = new ArrayList<>(List.of(originalTask));
        TaskList tasks = new TaskList(sourceTasks);

        sourceTasks.clear();

        assertEquals(1, tasks.size());
        assertSame(originalTask, tasks.get(0));
    }

    @Test
    void add_appendAndInsert_tasksRemainInRequestedOrder() {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        Task insertedTask = new Todo("inserted");
        TaskList tasks = new TaskList();

        tasks.add(firstTask);
        tasks.add(secondTask);
        tasks.add(1, insertedTask);

        assertFalse(tasks.isEmpty());
        assertEquals(3, tasks.size());
        assertSame(firstTask, tasks.get(0));
        assertSame(insertedTask, tasks.get(1));
        assertSame(secondTask, tasks.get(2));
    }

    @Test
    void delete_existingTask_taskRemovedAndReturned() {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        Task deletedTask = tasks.delete(0);

        assertSame(firstTask, deletedTask);
        assertEquals(1, tasks.size());
        assertSame(secondTask, tasks.get(0));
    }

    @Test
    void replace_existingTask_taskReplacedAndReturned() {
        Task originalTask = new Todo("original");
        Task replacementTask = new Todo("replacement");
        TaskList tasks = new TaskList(List.of(originalTask));

        Task replacedTask = tasks.replace(0, replacementTask);

        assertSame(originalTask, replacedTask);
        assertSame(replacementTask, tasks.get(0));
    }

    @Test
    void containsEquivalent_variedTypesDatesAndStatuses_correctResultReturned() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 28));
        Event event = new Event("conference", LocalDate.of(2026, 8, 29),
                LocalDate.of(2026, 8, 30));
        TaskList tasks = new TaskList(List.of(todo, deadline, event));

        assertTrue(tasks.containsEquivalent(new Todo("read book")));
        assertTrue(tasks.containsEquivalent(
                new Deadline("submit report", LocalDate.of(2026, 8, 28))));
        assertTrue(tasks.containsEquivalent(new Event("conference",
                LocalDate.of(2026, 8, 29), LocalDate.of(2026, 8, 30))));
        assertFalse(tasks.containsEquivalent(new Todo("submit report")));
        assertFalse(tasks.containsEquivalent(
                new Deadline("submit report", LocalDate.of(2026, 8, 29))));
    }
}
