package luna.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import luna.task.Task;
import luna.task.TaskList;
import luna.task.Todo;

/**
 * Tests for task-display methods in {@link Ui}.
 */
public class UiTest {
    @Test
    public void showTaskList_tasksProvided_formatsNumberedTaskList() {
        Ui ui = new Ui(false);
        TaskList tasks = new TaskList(new Todo("read book"));

        ui.showTaskList(tasks);

        assertEquals("Here's what's on your radar:\n1. [T][ ] read book", ui.consumeLatestResponse());
    }

    @Test
    public void showTaskList_emptyList_formatsHelpfulEmptyState() {
        Ui ui = new Ui(false);

        ui.showTaskList(new TaskList());

        assertEquals("Your radar is clear - there are no active tasks yet.\n"
                + "Try todo <description> to add one.", ui.consumeLatestResponse());
    }

    @Test
    public void showMatchingTasks_nonConsecutiveMatches_preservesOriginalTaskNumbers() {
        Ui ui = new Ui(false);
        TaskList tasks = new TaskList(
                new Todo("read book"),
                new Todo("submit quiz"),
                new Todo("return book"));

        ui.showMatchingTasks(tasks, List.of(0, 2));

        assertEquals("These tasks came into view:\n"
                        + "1. [T][ ] read book\n"
                        + "3. [T][ ] return book",
                ui.consumeLatestResponse());
    }

    @Test
    public void showArchivedTasks_tasksProvided_formatsNumberedArchivedTasks() {
        Ui ui = new Ui(false);
        List<Task> archivedTasks = List.of(new Todo("read book"));

        ui.showArchivedTasks(archivedTasks);

        assertEquals("These tasks are resting in your archive:\n1. [T][ ] read book",
                ui.consumeLatestResponse());
    }

    @Test
    public void showArchivedTasks_emptyList_formatsEmptyArchiveMessage() {
        Ui ui = new Ui(false);

        ui.showArchivedTasks(List.of());

        assertEquals("Your archive is quiet - nothing is resting there yet.", ui.consumeLatestResponse());
    }

    @Test
    public void showAddSuccess_taskProvided_formatsTaskCountConfirmation() {
        Ui ui = new Ui(false);
        Task task = new Todo("read book");

        ui.showAddSuccess(task, 1);

        assertEquals("It's on your radar:\n"
                + "[T][ ] read book\n"
                + "You now have 1 task on your radar.", ui.consumeLatestResponse());
    }

    @Test
    public void showDeleteSuccess_taskProvided_formatsTaskCountConfirmation() {
        Ui ui = new Ui(false);
        Task task = new Todo("read book");

        ui.showDeleteSuccess(task, 0);

        assertEquals("Cleared from your path:\n"
                + "[T][ ] read book\n"
                + "You now have 0 tasks on your radar.", ui.consumeLatestResponse());
    }

    @Test
    public void showMarkSuccess_taskProvided_formatsCompletionConfirmation() {
        Ui ui = new Ui(false);
        Todo task = new Todo("read book");
        task.markAsDone();

        ui.showMarkSuccess(task);

        assertEquals("Nicely done - this task is complete:\n[T][X] read book", ui.consumeLatestResponse());
    }

    @Test
    public void showUnmarkSuccess_taskProvided_formatsActiveConfirmation() {
        Ui ui = new Ui(false);
        Todo task = new Todo("read book");

        ui.showUnmarkSuccess(task);

        assertEquals("No rush. This task is back on your active path:\n[T][ ] read book",
                ui.consumeLatestResponse());
    }

    @Test
    public void showArchiveSuccess_oneTask_formatsArchiveConfirmation() {
        Ui ui = new Ui(false);

        ui.showArchiveSuccess(1, 1);

        assertEquals("Tucked away 1 task to data/archive.txt.\n"
                + "You have 1 task still on your radar.", ui.consumeLatestResponse());
    }

    @Test
    public void showArchiveSuccess_multipleTasks_formatsPluralNouns() {
        Ui ui = new Ui(false);

        ui.showArchiveSuccess(2, 3);

        assertEquals("Tucked away 2 tasks to data/archive.txt.\n"
                + "You have 3 tasks still on your radar.", ui.consumeLatestResponse());
    }

    @Test
    public void showNoTasksToArchive_formatsNoOpMessage() {
        Ui ui = new Ui(false);

        ui.showNoTasksToArchive();

        assertEquals("Your active list is already clear - there is nothing to archive.",
                ui.consumeLatestResponse());
    }

    @Test
    public void showHelp_formatsCompleteCommandReference() {
        Ui ui = new Ui(false);

        ui.showHelp();

        String response = ui.consumeLatestResponse();
        assertTrue(response.startsWith("Available commands:"));
        assertTrue(response.contains("> deadline <desc> /by <yyyy-MM-dd>:"));
        assertTrue(response.contains("> help: Displays this command guide."));
    }

    @Test
    public void showError_messageProvided_formatsBrandedError() {
        Ui ui = new Ui(false);

        ui.showError("Please enter a command.");

        assertEquals("I lost that signal. Please enter a command.", ui.consumeLatestResponse());
    }

    @Test
    public void showLoadingError_formatsProtectedSessionMessage() {
        Ui ui = new Ui(false);

        ui.showLoadingError();

        assertTrue(ui.consumeLatestResponse().contains("changes are disabled to protect the existing file"));
    }

    @Test
    public void showExit_formatsFarewell() {
        Ui ui = new Ui(false);

        ui.showExit();

        assertEquals("The moon is setting. Rest well - I'll keep your tasks safe.", ui.consumeLatestResponse());
    }

    @Test
    public void consumeLatestResponse_calledTwice_clearsResponseAfterFirstCall() {
        Ui ui = new Ui(false);
        ui.showError("test");

        String firstResponse = ui.consumeLatestResponse();
        String secondResponse = ui.consumeLatestResponse();

        assertEquals("I lost that signal. test", firstResponse);
        assertEquals("", secondResponse);
    }

    @Test
    public void getWelcomeMessage_guiMode_returnsCompactWelcome() {
        Ui ui = new Ui(false);

        String welcomeMessage = ui.getWelcomeMessage();

        assertTrue(welcomeMessage.startsWith("Plan your night"));
        assertTrue(welcomeMessage.contains("Type help to view all commands."));
    }
}
