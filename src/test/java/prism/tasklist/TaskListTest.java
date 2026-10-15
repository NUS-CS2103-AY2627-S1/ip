package prism.tasklist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import prism.PrismException;
import prism.task.Deadline;
import prism.task.Event;
import prism.task.Task;
import prism.task.Todo;

public class TaskListTest {
    private TaskList taskList;

    @BeforeEach
    public void setUp() {
        this.taskList = new TaskList();
    }

    @Test
    public void addTask_singleTask_sizeIncreasesAndTaskReturned() {
        Task todo = new Todo("read book");
        Task added = this.taskList.addTask(todo);

        assertEquals(1, this.taskList.getSize());
        assertEquals(todo, added);
    }

    @Test
    public void deleteTask_validIndex_taskRemovedAndReturned() throws PrismException {
        Task todo1 = new Todo("read book");
        Task todo2 = new Todo("buy groceries");
        this.taskList.addTask(todo1);
        this.taskList.addTask(todo2);

        Task removed = this.taskList.deleteTask(0);

        assertEquals(1, this.taskList.getSize());
        assertEquals(todo1, removed);
        assertEquals(todo2, this.taskList.getTask(0));
    }

    @Test
    public void deleteTask_negativeIndex_throwsException() {
        this.taskList.addTask(new Todo("read book"));
        assertThrows(PrismException.class, () -> this.taskList.deleteTask(-1));
    }

    @Test
    public void deleteTask_outOfBoundsIndex_throwsException() {
        this.taskList.addTask(new Todo("read book"));
        assertThrows(PrismException.class, () -> this.taskList.deleteTask(5));
    }

    @Test
    public void findTasks_keywordMatchesDescription_returnsMatchingTasksIgnoringCase() {
        Task matchingTodo = new Todo("read book");
        Task nonMatchingTodo = new Todo("buy groceries");
        Task matchingDeadline = new Todo("return library book");
        this.taskList.addTask(matchingTodo);
        this.taskList.addTask(nonMatchingTodo);
        this.taskList.addTask(matchingDeadline);

        List<Task> matchingTasks = this.taskList.findTasks("BOOK");

        assertEquals(List.of(matchingTodo, matchingDeadline), matchingTasks);
    }

    @Test
    public void markAndUnmarkTask_validIndex_updatesStatus() throws PrismException {
        this.taskList.addTask(new Todo("read book"));
        assertTrue(this.taskList.markTask(0).isDone());
        assertTrue(this.taskList.getTask(0).isDone());
        this.taskList.unmarkTask(0);
        assertTrue(!this.taskList.getTask(0).isDone());
    }

    @Test
    public void getTasksOnDate_deadlineAndEvent_returnsMatchingTasks() throws PrismException {
        Task deadline = new Deadline("submit", "2025-12-02 1800");
        Task event = new Event("meeting", "2025-12-01 0900", "2025-12-03 1000");
        this.taskList.addTask(deadline);
        this.taskList.addTask(event);

        assertEquals(List.of(deadline, event), this.taskList.getTasksOnDate(LocalDate.of(2025, 12, 2)));
        assertEquals(List.of(), this.taskList.getTasksOnDate(LocalDate.of(2025, 12, 4)));
    }

    @Test
    public void snoozeTodo_throwsException() {
        this.taskList.addTask(new Todo("read book"));
        PrismException exception = assertThrows(PrismException.class, () ->
                this.taskList.snoozeTask(0, "2025-12-02 1800"));
        assertEquals("!!! Only deadlines and events can be snoozed.", exception.getMessage());
    }
}
