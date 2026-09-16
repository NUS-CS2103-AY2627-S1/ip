package chattingheads.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import chattingheads.exception.InvalidCommandException;
import chattingheads.task.TaskList;
import chattingheads.task.Todo;

public class UiTest {

    private static final String SEPARATOR =
            "--------------------------------------------------------------------------------";

    private final Ui ui = new Ui();

    @Test
    public void getStartupMessage_returnsExpectedMessage() {
        assertEquals(
                "You may find yourself\nLiving in a shotgun shack\n" + SEPARATOR,
                ui.getStartupMessage());
    }

    @Test
    public void getShutdownMessage_returnsExpectedMessage() {
        assertEquals(
                "Letting the days go \"bye!\"\nLet the water shut me down\n" + SEPARATOR,
                ui.getShutdownMessage());
    }

    @Test
    public void getListStatus_emptyList_reportsNoTasks() {
        assertEquals(
                "Now you have no tasks in the list\n" + SEPARATOR,
                ui.getListStatus(new TaskList()));
    }

    @Test
    public void getListStatus_oneTask_usesSingularTask() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("test"));

        assertEquals(
                "Now you have 1 task in the list\n" + SEPARATOR,
                ui.getListStatus(taskList));
    }

    @Test
    public void getListStatus_multipleTasks_usesPluralTasks() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("first"));
        taskList.add(new Todo("second"));

        assertEquals(
                "Now you have 2 tasks in the list\n" + SEPARATOR,
                ui.getListStatus(taskList));
    }

    @Test
    public void getAddStatus_addedTask_includesTaskAndUpdatedCount() {
        TaskList taskList = new TaskList();
        Todo task = new Todo("read book");
        taskList.add(task);

        String message = ui.getAddStatus(task, taskList);

        assertTrue(message.contains("Put it right there on your list:"));
        assertTrue(message.contains("[T][ ] read book"));
        assertTrue(message.contains("Now you have 1 task in the list"));
    }

    @Test
    public void getDeleteStatus_deletedTask_includesTaskAndUpdatedCount() {
        TaskList taskList = new TaskList();
        Todo deletedTask = new Todo("read book");

        String message = ui.getDeleteStatus(deletedTask, taskList);

        assertTrue(message.contains("After this task is gone:"));
        assertTrue(message.contains("[T][ ] read book"));
        assertTrue(message.contains("Now you have no tasks in the list"));
    }

    @Test
    public void getErrorMessage_exception_appendsSeparator() {
        String message = ui.getErrorMessage(new InvalidCommandException());

        assertEquals(
                "And you may ask yourself\n\"How do I work this?\"\n" + SEPARATOR,
                message);
    }

    @Test
    public void getMarkStatus_markedTask_includesCompletedTask() {
        Todo task = new Todo("read book", true);

        String message = ui.getMarkStatus(task);

        assertTrue(message.contains("Marked this task as complete:"));
        assertTrue(message.contains("[T][X] read book"));
        assertTrue(message.endsWith(SEPARATOR));
    }

    @Test
    public void getUnmarkStatus_unmarkedTask_includesIncompleteTask() {
        Todo task = new Todo("read book");

        String message = ui.getUnmarkStatus(task);

        assertTrue(message.contains("Marked this task as incomplete:"));
        assertTrue(message.contains("[T][ ] read book"));
        assertTrue(message.endsWith(SEPARATOR));
    }

    @Test
    public void getTaskListMessage_emptyList_containsNoNumberedTasks() {
        String message = ui.getTaskListMessage(new TaskList());

        assertEquals("Take a look at these tasks:\n" + SEPARATOR, message);
    }

    @Test
    public void getTaskListMessage_multipleTasks_numbersTasksInOrder() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("first"));
        taskList.add(new Todo("second", true));

        String message = ui.getTaskListMessage(taskList);

        assertTrue(message.contains("1. [T][ ] first"));
        assertTrue(message.contains("2. [T][X] second"));
        assertTrue(message.indexOf("1. [T][ ] first") < message.indexOf("2. [T][X] second"));
    }

    @Test
    public void getFoundTasksMessage_matchingTasks_keepsOriginalTaskNumbers() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("buy milk"));
        taskList.add(new Todo("read notes"));

        String message = ui.getFoundTasksMessage(taskList, "read");

        assertTrue(message.contains("1. [T][ ] read book"));
        assertTrue(message.contains("3. [T][ ] read notes"));
        assertFalse(message.contains("2. [T][ ] buy milk"));
    }

    @Test
    public void getFoundTasksMessage_noMatches_containsNoNumberedTasks() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        String message = ui.getFoundTasksMessage(taskList, "sleep");

        assertEquals("Take a look at these tasks:\n" + SEPARATOR, message);
    }

    @Test
    public void getRescheduleStatus_rescheduledTask_includesTaskTypeAndTask() {
        Todo task = new Todo("test");

        String message = ui.getRescheduleStatus(task, "task");

        assertTrue(message.contains("Rescheduled this task:"));
        assertTrue(message.contains("[T][ ] test"));
        assertTrue(message.endsWith(SEPARATOR));
    }

    @Test
    public void readCommand_inputAvailable_returnsNextLine() {
        InputStream originalInput = System.in;
        try {
            System.setIn(new ByteArrayInputStream("list\n".getBytes(StandardCharsets.UTF_8)));
            Ui inputUi = new Ui();
            assertEquals("list", inputUi.readCommand());
        } finally {
            System.setIn(originalInput);
        }
    }
}
