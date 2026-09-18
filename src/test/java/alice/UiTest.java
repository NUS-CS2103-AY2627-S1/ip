package alice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Guards the rule that Alice's personality is only ever framing: the
 * functional content of a message - counts, task text, and above all the
 * reason a command failed - must survive intact however the wording around
 * it changes.
 */
public class UiTest {
    private final Ui ui = new Ui();

    @Test
    public void showError_singleLineCause_isReproducedVerbatim() {
        String cause = "mark needs a task number, e.g. mark 2.";

        assertTrue(ui.showError(cause).contains(cause));
    }

    @Test
    public void showError_multiLineCause_keepsEveryLine() {
        String shown = ui.showError("I don't know the command 'lsit'.\nTry one of: todo, deadline, bye.");

        assertTrue(shown.contains("I don't know the command 'lsit'."));
        assertTrue(shown.contains("Try one of: todo, deadline, bye."));
    }

    @Test
    public void showError_multiLineCause_keepsTheExplanationOnItsOwnLine() {
        String shown = ui.showError("First line.\nSecond line.");

        assertEquals(2, shown.split("\n").length);
        assertTrue(shown.split("\n")[1].startsWith("Second line."));
    }

    @Test
    public void showTaskAdded_statesTheTaskAndTheNewCount() {
        String shown = ui.showTaskAdded(new Todo("read book"), 3);

        assertTrue(shown.contains("[T][ ] read book"));
        assertTrue(shown.contains("3"));
    }

    @Test
    public void showDeleted_statesTheTaskAndHowManyRemain() {
        String shown = ui.showDeleted(new Todo("buy milk"), 2);

        assertTrue(shown.contains("[T][ ] buy milk"));
        assertTrue(shown.contains("2 left."));
    }

    @Test
    public void showMarked_showsTheTaskWithItsTickedBox() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertTrue(ui.showMarked(todo).contains("[T][X] read book"));
    }

    @Test
    public void showSnoozed_showsTheNewDate() {
        Deadline deadline = new Deadline("essay", LocalDateTime.of(2019, 12, 2, 18, 0));
        deadline.snooze(3);

        assertTrue(ui.showSnoozed(deadline).contains("Dec 5 2019"));
    }

    @Test
    public void showTaskList_numbersEveryTaskFromOne() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy milk"));
        String shown = ui.showTaskList(tasks);

        assertTrue(shown.contains("1.[T][ ] read book"));
        assertTrue(shown.contains("2.[T][ ] buy milk"));
    }

    @Test
    public void showTaskList_emptyList_saysSoInsteadOfShowingAnEmptyHeading() {
        String shown = ui.showTaskList(new TaskList());

        assertEquals(1, shown.split("\n").length);
        assertTrue(shown.toLowerCase().contains("nothing"));
    }

    @Test
    public void showFoundTasks_emptyResult_saysNothingMatched() {
        assertTrue(ui.showFoundTasks(new ArrayList<>()).toLowerCase().contains("nothing matched"));
    }

    @Test
    public void showFoundTasks_numbersTheMatches() {
        ArrayList<Task> matches = new ArrayList<>();
        matches.add(new Todo("read book"));

        assertTrue(ui.showFoundTasks(matches).contains("1.[T][ ] read book"));
    }

    @Test
    public void showLoadWarnings_reproducesEveryWarning() {
        String shown = ui.showLoadWarnings(List.of("first problem", "second problem"));

        assertTrue(shown.contains("first problem"));
        assertTrue(shown.contains("second problem"));
    }
}
