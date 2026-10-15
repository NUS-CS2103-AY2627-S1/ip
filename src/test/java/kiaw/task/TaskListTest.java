package kiaw.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void add_task_taskAdded() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("read book"));

        assertEquals(1, tasks.size());
        assertEquals(
                "read book",
                tasks.get(0).getDescription()
        );
    }

    @Test
    public void add_multipleTasks_allTasksAdded() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy bread"));

        assertEquals(2, tasks.size());
        assertEquals(
                "read book",
                tasks.get(0).getDescription()
        );
        assertEquals(
                "buy bread",
                tasks.get(1).getDescription()
        );
    }

    @Test
    public void delete_existingTask_correctTaskRemoved() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy bread"));

        Task deletedTask = tasks.delete(0);

        assertEquals(
                "read book",
                deletedTask.getDescription()
        );
        assertEquals(1, tasks.size());
        assertEquals(
                "buy bread",
                tasks.get(0).getDescription()
        );
    }

    @Test
    public void delete_middleTask_remainingTasksShiftCorrectly() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.add(new Todo("third"));

        Task deletedTask = tasks.delete(1);

        assertEquals(
                "second",
                deletedTask.getDescription()
        );
        assertEquals(2, tasks.size());
        assertEquals(
                "first",
                tasks.get(0).getDescription()
        );
        assertEquals(
                "third",
                tasks.get(1).getDescription()
        );
    }

    @Test
    public void mark_existingTask_taskMarkedDone() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("read book"));

        tasks.mark(0);

        assertEquals(
                "X",
                tasks.get(0).getStatusIcon()
        );
    }

    @Test
    public void unmark_doneTask_taskMarkedNotDone() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("read book"));
        tasks.mark(0);

        tasks.unmark(0);

        assertEquals(
                " ",
                tasks.get(0).getStatusIcon()
        );
    }

    @Test
    public void isEmpty_newTaskList_returnsTrue() {
        TaskList tasks = new TaskList();

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void isEmpty_taskAdded_returnsFalse() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("read book"));

        assertFalse(tasks.isEmpty());
    }

    @Test
    public void sortByDate_datedTasks_tasksSortedChronologically() {
        TaskList tasks = new TaskList();

        tasks.add(new Deadline(
                "late deadline",
                LocalDate.of(2026, 9, 20)
        ));
        tasks.add(new Event(
                "early event",
                LocalDate.of(2026, 9, 10),
                LocalDate.of(2026, 9, 11)
        ));
        tasks.add(new Deadline(
                "middle deadline",
                LocalDate.of(2026, 9, 15)
        ));

        tasks.sortByDate();

        assertEquals(
                "early event",
                tasks.get(0).getDescription()
        );
        assertEquals(
                "middle deadline",
                tasks.get(1).getDescription()
        );
        assertEquals(
                "late deadline",
                tasks.get(2).getDescription()
        );
    }

    @Test
    public void sortByDate_todosAndDatedTasks_todosPlacedLast() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("first todo"));
        tasks.add(new Deadline(
                "deadline",
                LocalDate.of(2026, 9, 15)
        ));
        tasks.add(new Todo("second todo"));
        tasks.add(new Event(
                "event",
                LocalDate.of(2026, 9, 10),
                LocalDate.of(2026, 9, 11)
        ));

        tasks.sortByDate();

        assertEquals(
                "event",
                tasks.get(0).getDescription()
        );
        assertEquals(
                "deadline",
                tasks.get(1).getDescription()
        );
        assertEquals(
                "first todo",
                tasks.get(2).getDescription()
        );
        assertEquals(
                "second todo",
                tasks.get(3).getDescription()
        );
    }

    @Test
    public void sortByDate_sameDate_relativeOrderPreserved() {
        TaskList tasks = new TaskList();

        LocalDate sameDate = LocalDate.of(2026, 9, 15);

        tasks.add(new Deadline(
                "first",
                sameDate
        ));
        tasks.add(new Event(
                "second",
                sameDate,
                LocalDate.of(2026, 9, 16)
        ));
        tasks.add(new Deadline(
                "third",
                sameDate
        ));

        tasks.sortByDate();

        assertEquals(
                "first",
                tasks.get(0).getDescription()
        );
        assertEquals(
                "second",
                tasks.get(1).getDescription()
        );
        assertEquals(
                "third",
                tasks.get(2).getDescription()
        );
    }

    @Test
    public void sortByDate_onlyTodos_relativeOrderPreserved() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.add(new Todo("third"));

        tasks.sortByDate();

        assertEquals(
                "first",
                tasks.get(0).getDescription()
        );
        assertEquals(
                "second",
                tasks.get(1).getDescription()
        );
        assertEquals(
                "third",
                tasks.get(2).getDescription()
        );
    }
}
