package johnny.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import johnny.task.Task;
import johnny.task.TaskList;
import johnny.task.Todo;

public class UiTest {

    @Test
    public void showTaskAdded_zeroTaskCount_throwsAssertionError() {
        Ui ui = new Ui(message -> { });

        assertThrows(AssertionError.class, () -> ui.showTaskAdded(new Todo("read book"), 0));
    }

    @Test
    public void showTaskDeleted_negativeTaskCount_throwsAssertionError() {
        Ui ui = new Ui(message -> { });

        assertThrows(AssertionError.class, () -> ui.showTaskDeleted(new Todo("read book"), -1));
    }

    @Test
    public void showGreeting_validCall_displaysBannerAndGreeting() {
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.showGreeting();

        assertEquals(5, messages.size());
        assertEquals("    ____________________________________________________________", messages.get(0));
        assertEquals("     Good day. Johnny at your service.", messages.get(2));
        assertEquals("     How may I keep your day in order?", messages.get(3));
        assertEquals(messages.get(0), messages.get(4));
    }

    @Test
    public void showStatusMessages_validCalls_displaysExpectedText() {
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.showFarewell();
        ui.showLoadingError();
        ui.showSaveError();
        ui.showError("Invalid command.");
        ui.showCommandUndone();

        assertEquals(List.of(
                "     Until next time. I'll keep things in order.",
                "     A note, if I may: I couldn't load your agenda, so we'll start afresh.",
                "     A note, if I may: I couldn't save your agenda.",
                "     I'm afraid something is amiss: Invalid command.",
                "     As you wish. The last change has been undone."), messages);
    }

    @Test
    public void showTaskChanges_validCalls_displaysTasksAndPluralCounts() {
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);
        Todo task = new Todo("read book");

        ui.showTaskAdded(task, 2);
        task.markAsDone();
        ui.showTaskMarked(task);
        task.markAsNotDone();
        ui.showTaskUnmarked(task);
        ui.showTaskDeleted(task, 0);

        assertEquals(List.of(
                "     Consider it noted:",
                "       [T][ ] read book",
                "     Your agenda now has 2 tasks.",
                "     Excellent. One task completed:",
                "       [T][X] read book",
                "     Very well. This task is pending again:",
                "       [T][ ] read book",
                "     Removed from the agenda:",
                "       [T][ ] read book",
                "     Your agenda now has 0 tasks."), messages);
    }

    @Test
    public void showTaskList_multipleTasks_displaysNumberedTasks() {
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));

        ui.showTaskList(tasks);

        assertEquals(List.of(
                "     Here is your current agenda:",
                "     1.[T][ ] first",
                "     2.[T][ ] second"), messages);
    }

    @Test
    public void showFindResults_multipleMatches_displaysNumberedTasks() {
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);
        ArrayList<Task> matches = new ArrayList<>();
        matches.add(new Todo("first"));
        matches.add(new Todo("second"));

        ui.showFindResults(matches);

        assertEquals(List.of(
                "     These entries match your request:",
                "     1.[T][ ] first",
                "     2.[T][ ] second"), messages);
    }

    @Test
    public void hasNextLine_consumerBackedUi_returnsFalse() {
        Ui ui = new Ui(message -> { });

        assertFalse(ui.hasNextLine());
        ui.close();
    }
}
