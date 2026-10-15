package ev.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ev.EvException;
import ev.task.Deadline;
import ev.task.Event;
import ev.task.TaskList;
import ev.task.Todo;

public class UiTest {

    private TaskList threeTasks() throws EvException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0)));
        tasks.add(new Event("camp",
                LocalDateTime.of(2019, 12, 1, 9, 0),
                LocalDateTime.of(2019, 12, 3, 17, 0)));
        return tasks;
    }

    @Test
    public void takeResponse_nothingSaid_empty() {
        assertEquals("", new Ui().takeResponse());
    }

    @Test
    public void takeResponse_calledTwice_secondCallEmpty() {
        Ui ui = new Ui();
        ui.showWelcome();
        assertTrue(ui.takeResponse().startsWith("Hi Peter."));
        assertEquals("", ui.takeResponse());
    }

    @Test
    public void takeResponse_severalReplies_joinedInOrder() {
        Ui ui = new Ui();
        ui.showError("First.");
        ui.showError("Second.");
        assertEquals("First.\nSecond.", ui.takeResponse());
    }

    @Test
    public void showWelcome_greetsPeterAndOffersHelp() {
        Ui ui = new Ui();
        ui.showWelcome();
        assertEquals("Hi Peter.\nEV online. What do you need?", ui.takeResponse());
    }

    @Test
    public void showFarewell_signsOff() {
        Ui ui = new Ui();
        ui.showFarewell();
        assertEquals("Signing off, Peter.", ui.takeResponse());
    }

    @Test
    public void showAdded_reportsTheTaskAndTheNewSize() throws EvException {
        Ui ui = new Ui();
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        tasks.add(todo);
        ui.showAdded(todo, tasks);
        assertEquals("Added.\n  [T][ ] read book\n1 task.", ui.takeResponse());
    }

    @Test
    public void showRemoved_reportsTheTaskAndTheNewSize() throws EvException {
        Ui ui = new Ui();
        TaskList tasks = threeTasks();
        ui.showRemoved(tasks.removeByNumber(1), tasks);
        assertEquals("Removed.\n  [T][ ] read book\n2 tasks.", ui.takeResponse());
    }

    @Test
    public void showMarked_doneAndNotDone_differentWording() {
        Ui ui = new Ui();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        ui.showMarked(todo, true);
        assertEquals("Done.\n  [T][X] read book", ui.takeResponse());

        todo.markAsNotDone();
        ui.showMarked(todo, false);
        assertEquals("Back to not done.\n  [T][ ] read book", ui.takeResponse());
    }

    @Test
    public void showUpdated_reportsTheTaskInItsNewState() {
        Ui ui = new Ui();
        ui.showUpdated(new Todo("read the whole book"));
        assertEquals("Updated.\n  [T][ ] read the whole book", ui.takeResponse());
    }

    @Test
    public void showSkippedLines_namesTheCountAndTheFile() {
        Ui ui = new Ui();
        ui.showSkippedLines(3, Paths.get("data", "ev.txt"));
        assertTrue(ui.takeResponse().startsWith("Skipped 3 unreadable line(s) in "));
    }

    @Test
    public void showTasks_emptyList_saysSo() {
        Ui ui = new Ui();
        ui.showTasks(new TaskList());
        assertEquals("Nothing on your list.", ui.takeResponse());
    }

    @Test
    public void showTasks_threeTasks_numberedFromOne() throws EvException {
        Ui ui = new Ui();
        ui.showTasks(threeTasks());
        assertEquals("Your list:"
                + "\n1.[T][ ] read book"
                + "\n2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)"
                + "\n3.[E][ ] camp (from: Dec 1 2019, 9:00 AM to: Dec 3 2019, 5:00 PM)",
                ui.takeResponse());
    }

    @Test
    public void showTasksOn_matchingTasks_keepTheirNumbersInTheFullList() throws EvException {
        Ui ui = new Ui();
        ui.showTasksOn(LocalDate.of(2019, 12, 2), threeTasks());
        assertEquals("On Dec 2 2019:"
                + "\n2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)"
                + "\n3.[E][ ] camp (from: Dec 1 2019, 9:00 AM to: Dec 3 2019, 5:00 PM)",
                ui.takeResponse());
    }

    @Test
    public void showTasksOn_noTaskOnThatDate_saysSo() throws EvException {
        Ui ui = new Ui();
        ui.showTasksOn(LocalDate.of(2020, 1, 1), threeTasks());
        assertEquals("Nothing on Jan 1 2020.", ui.takeResponse());
    }

    @Test
    public void showMatchingTasks_matches_keepTheirNumbersInTheFullList() throws EvException {
        Ui ui = new Ui();
        ui.showMatchingTasks("book", threeTasks());
        assertEquals("Matches:"
                + "\n1.[T][ ] read book"
                + "\n2.[D][ ] return book (by: Dec 2 2019, 6:00 PM)",
                ui.takeResponse());
    }

    @Test
    public void showMatchingTasks_noMatch_quotesTheKeyword() throws EvException {
        Ui ui = new Ui();
        ui.showMatchingTasks("plants", threeTasks());
        assertEquals("No match for \"plants\".", ui.takeResponse());
    }
}
