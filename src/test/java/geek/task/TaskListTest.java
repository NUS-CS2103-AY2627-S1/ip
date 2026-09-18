package geek.task;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import geek.exception.GeekException;

/**
 * Tests one-based task operations and task filtering in a task list.
 */
class TaskListTest {
    @Test
    void constructor_sourceListChanges_doNotChangeTaskList() {
        List<Task> sourceTasks = new ArrayList<>();
        sourceTasks.add(Task.newTodo("first"));
        TaskList taskList = new TaskList(sourceTasks);

        sourceTasks.add(Task.newTodo("second"));

        assertEquals(1, taskList.size());
    }

    @Test
    void getTasks_attemptedModification_throwsException() {
        TaskList taskList = new TaskList();
        taskList.add(Task.newTodo("read book"));

        List<Task> returnedTasks = taskList.getTasks();

        assertThrows(
                UnsupportedOperationException.class, () -> returnedTasks.add(Task.newTodo("write notes"))
        );
    }

    @Test
    void delete_validOneBasedNumber_removesAndReturnsTask() {
        Task first = Task.newTodo("first");
        Task second = Task.newTodo("second");
        TaskList taskList = new TaskList(List.of(first, second));

        Task deleted = taskList.delete(1);

        assertAll(() -> assertSame(first, deleted), () -> assertEquals(1, taskList.size()), () -> assertEquals(
                        List.of(second),
                        taskList.getTasks()
                )
        );
    }

    @Test
    void markAndUnmark_validTaskNumber_updatesAndReturnsTask() {
        Task task = Task.newTodo("read book");
        TaskList taskList = new TaskList(List.of(task));

        Task marked = taskList.mark(1);
        String markedStatus = task.getStatus();
        Task unmarked = taskList.unmark(1);

        assertAll(() -> assertSame(task, marked), () ->
                assertEquals("X", markedStatus), () ->
                assertSame(task, unmarked), () ->
                assertEquals(" ", task.getStatus()));
    }

    @Test
    void taskOperations_outOfRangeNumbers_throwGeekException() {
        TaskList taskList = new TaskList(
                List.of(Task.newTodo("only task"))
        );

        assertAll(() -> assertThrows(
                        GeekException.class, () -> taskList.mark(0)
                ), () -> assertThrows(
                        GeekException.class, () -> taskList.unmark(2)
                ), () -> assertThrows(
                        GeekException.class, () -> taskList.delete(-1)
                )
        );
    }

    @Test
    void findOn_targetDate_returnsMatchingDatedTasksInOrder() {
        Task todo = Task.newTodo("undated task");
        Task event = Task.newEvent(
                "conference",
                "2/12/2019 2300",
                "4/12/2019 0100"
        );
        Task deadline = Task.newDeadline(
                "submit report",
                "3/12/2019"
        );
        TaskList taskList = new TaskList(
                List.of(todo, event, deadline)
        );

        List<Task> matchingTasks = taskList.findOn(
                LocalDate.of(2019, 12, 3)
        );

        assertEquals(List.of(event, deadline), matchingTasks);
    }

    @Test
    void find_mixedCaseKeyword_returnsDescriptionMatchesInOrder() {
        Task first = Task.newTodo("read BOOK");
        Task second = Task.newTodo("write notes");
        Task third = Task.newDeadline(
                "return book",
                "3/12/2019"
        );
        TaskList taskList = new TaskList(
                List.of(first, second, third)
        );

        List<Task> matchingTasks = taskList.find("BoOk");

        assertEquals(List.of(first, third), matchingTasks);
    }

    @Test
    void find_absentKeyword_returnsEmptyList() {
        TaskList taskList = new TaskList(
                List.of(Task.newTodo("read book"))
        );

        assertEquals(List.of(), taskList.find("notes"));
    }

    @Test
    void sortChronologically_mixedTasks_sortsDatedTasksAndKeepsTodosLast() {
        Task firstTodo = Task.newTodo("read book");
        Task laterDeadline = Task.newDeadline(
                "submit report",
                "3/12/2019"
        );
        Task event = Task.newEvent(
                "morning meeting",
                "2/12/2019 0900",
                "2/12/2019 1000"
        );
        Task earlierDeadline = Task.newDeadline(
                "return book",
                "2/12/2019"
        );
        Task secondTodo = Task.newTodo("write notes");
        TaskList taskList = new TaskList(
                List.of(
                        firstTodo,
                        laterDeadline,
                        event,
                        earlierDeadline,
                        secondTodo
                )
        );

        taskList.sortChronologically();

        assertEquals(
                List.of(
                        earlierDeadline,
                        event,
                        laterDeadline,
                        firstTodo,
                        secondTodo
                ),
                taskList.getTasks()
        );
    }

    @Test
    void sortChronologically_equalDateTimes_preservesOriginalOrder() {
        Task first = Task.newDeadline("first", "2/12/2019");
        Task second = Task.newEvent(
                "second",
                "2/12/2019 0000",
                "2/12/2019 0100"
        );
        TaskList taskList = new TaskList(List.of(first, second));

        taskList.sortChronologically();

        assertEquals(List.of(first, second), taskList.getTasks());
    }
}
