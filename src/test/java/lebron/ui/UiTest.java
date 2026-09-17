package lebron.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import lebron.task.Deadline;
import lebron.task.Task;
import lebron.task.TaskList;
import lebron.task.TaskListException;
import lebron.task.Todo;

public class UiTest {
    private final Ui ui = new Ui();

    @Test
    public void showBanner_returnsGreeting() {
        assertEquals("What's up, I am LeBron.\nCan the king assist you in any way?", ui.showBanner());
    }

    @Test
    public void showMessage_returnsMessageUnchanged() {
        assertEquals("some message", ui.showMessage("some message"));
    }

    @Test
    public void showTaskList_returnsTaskListToString() throws TaskListException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTask(new Todo("read book"));

        assertEquals(taskList.toString(), ui.showTaskList(taskList));
    }

    @Test
    public void showTodoAdded_includesHeaderTaskAndCount() {
        Task task = new Todo("read book");
        assertEquals("More todo!\n[T][ ] read book\n2 tasks left to grind now!", ui.showTodoAdded(task, 2));
    }

    @Test
    public void showDeadlineAdded_includesHeaderTaskAndCount() {
        Task task = new Deadline("return book", LocalDateTime.of(2019, 10, 15, 18, 0));
        String expected = "Don't put these off!\n[D][ ] return book (by: Oct 15 2019, 6:00 PM)\n"
                + "1 tasks left to grind now!";
        assertEquals(expected, ui.showDeadlineAdded(task, 1));
    }

    @Test
    public void showEventAdded_includesHeaderTaskAndCount() {
        Task task = new Todo("project meeting");
        assertEquals("Go have some fun young blood!\n[T][ ] project meeting\n3 tasks left to grind now!",
                ui.showEventAdded(task, 3));
    }

    @Test
    public void showRecurringAdded_includesHeaderTaskAndCount() {
        Task task = new Todo("weekly sync");
        assertEquals("Got you, this one's on repeat!\n[T][ ] weekly sync\n1 tasks left to grind now!",
                ui.showRecurringAdded(task, 1));
    }

    @Test
    public void showTaskDeleted_includesHeaderTaskAndCount() {
        Task task = new Todo("read book");
        assertEquals("Task been taken care of!\n[T][ ] read book\n0 tasks left to grind now!",
                ui.showTaskDeleted(task, 0));
    }

    @Test
    public void showTaskMarked_includesHeaderAndTaskOnly() {
        Task task = new Todo("read book");
        task.setStatus(true);
        assertEquals("Oh yea we're striving for greatness!\n[T][X] read book", ui.showTaskMarked(task));
    }

    @Test
    public void showTaskUnmarked_includesHeaderAndTaskOnly() {
        Task task = new Todo("read book");
        assertEquals("Oh nah we undoing stuff now?\n[T][ ] read book", ui.showTaskUnmarked(task));
    }

    @Test
    public void showMatchingTasks_multipleMatches_numberedFromOne() {
        List<Task> matches = List.of(new Todo("read book"), new Todo("return book"));

        String response = ui.showMatchingTasks(matches);

        assertEquals("Here's what's matching your search, chief!\n1. [T][ ] read book\n2. [T][ ] return book",
                response);
    }

    @Test
    public void showMatchingTasks_noMatches_headerOnly() {
        assertEquals("Here's what's matching your search, chief!", ui.showMatchingTasks(List.of()));
    }
}
