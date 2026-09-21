package janet.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import janet.exception.InvalidTaskException;

class TaskListTest {
    @Test
    void constructor_sourceListChanges_doesNotChangeTaskList() {
        ArrayList<Task> sourceTasks = new ArrayList<>();
        sourceTasks.add(new Todo("first"));
        TaskList tasks = new TaskList(sourceTasks);

        sourceTasks.add(new Todo("second"));

        assertEquals(1, tasks.size());
        assertEquals("first", tasks.get(1).getDescription());
    }

    @Test
    void delete_existingTask_removesAndReturnsSelectedTask() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));

        Task deletedTask = tasks.delete(1);

        assertEquals("first", deletedTask.getDescription());
        assertEquals(1, tasks.size());
        assertEquals("second", tasks.get(1).getDescription());
    }

    @Test
    void delete_multipleExistingTasks_removesAtomicallyAndReturnsRequestedOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.add(new Todo("third"));
        tasks.add(new Todo("fourth"));

        List<Task> deletedTasks = tasks.delete(List.of(4, 2));

        assertEquals(List.of("fourth", "second"), deletedTasks.stream()
                .map(Task::getDescription)
                .toList());
        assertEquals(List.of("first", "third"), tasks.getTasks().stream()
                .map(Task::getDescription)
                .toList());
    }

    @Test
    void delete_duplicateOrInvalidTaskNumbers_throwsExceptionWithoutRemovingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.add(new Todo("third"));

        assertThrows(InvalidTaskException.class, () -> tasks.delete(List.of()));
        assertThrows(InvalidTaskException.class, () -> tasks.delete(List.of(1, 1)));
        assertThrows(InvalidTaskException.class, () -> tasks.delete(List.of(2, 4)));
        assertEquals(List.of("first", "second", "third"), tasks.getTasks().stream()
                .map(Task::getDescription)
                .toList());
    }

    @Test
    void add_exactDuplicateTasks_throwsExceptionWithoutAddingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("same description"));
        tasks.add(new Deadline("same description", LocalDate.of(2099, 1, 1)));
        tasks.add(new Event("meeting", "10:00", "11:00"));

        assertThrows(InvalidTaskException.class, () -> tasks.add(new Todo("same description")));
        assertThrows(InvalidTaskException.class, () ->
                tasks.add(new Deadline("same description", LocalDate.of(2099, 1, 1))));
        assertThrows(InvalidTaskException.class, () -> tasks.add(new Event("meeting", "10:00", "11:00")));
        assertEquals(3, tasks.size());
    }

    @Test
    void add_tasksWithDifferentTypeOrDetails_addsEveryTask() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("same description"));
        tasks.add(new Deadline("same description", LocalDate.of(2099, 1, 1)));
        tasks.add(new Deadline("same description", LocalDate.of(2099, 1, 2)));
        tasks.add(new Event("meeting", "10:00", "11:00"));
        tasks.add(new Event("meeting", "10:00", "12:00"));

        assertEquals(5, tasks.size());
    }

    @Test
    void get_invalidTaskNumber_throwsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only task"));

        assertThrows(InvalidTaskException.class, () -> tasks.get(0));
        assertThrows(InvalidTaskException.class, () -> tasks.get(2));
    }

    @Test
    void find_matchingKeyword_returnsMatchingTasksInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("call friend"));
        tasks.add(new Todo("borrow book"));

        List<Task> matchingTasks = tasks.find("book");

        assertEquals(2, matchingTasks.size());
        assertEquals("read book", matchingTasks.get(0).getDescription());
        assertEquals("borrow book", matchingTasks.get(1).getDescription());
    }

    @Test
    void find_missingOrDifferentlyCasedKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertTrue(tasks.find("missing").isEmpty());
        assertTrue(tasks.find("Book").isEmpty());
    }

    @Test
    void getTasks_returnedListCannotChangeTaskList() {
        TaskList tasks = new TaskList(List.of(new Todo("first")));
        List<Task> returnedTasks = tasks.getTasks();

        assertThrows(UnsupportedOperationException.class, () -> returnedTasks.add(new Todo("second")));
        assertEquals(1, tasks.size());
    }

    @Test
    void restore_previousSnapshot_replacesCurrentOrder() {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));
        List<Task> previousTasks = tasks.getTasks();
        tasks.delete(1);

        tasks.restore(previousTasks);

        assertEquals(List.of("first", "second"), tasks.getTasks().stream()
                .map(Task::getDescription)
                .toList());
    }
}
