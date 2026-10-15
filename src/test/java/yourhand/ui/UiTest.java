package yourhand.ui;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Scanner;

import yourhand.tasks.Deadline;
import yourhand.tasks.TaskDateTime;
import yourhand.tasks.TaskList;
import yourhand.tasks.Todo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests console output and input behavior without starting the GUI. */
class UiTest {

    @Test
    void outputMethods_emptyAndNonEmptyLists_showAppropriateMessages() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new PrintStream(output));
        TaskList taskList = new TaskList();

        ui.showTaskList(taskList);
        assertTrue(output.toString().contains("As empty as your wallet"));

        taskList.add(new Todo("read book"));
        ui.showTaskList(taskList);
        assertTrue(output.toString().contains("read book"));
    }

    @Test
    void searchAndScheduleMethods_showMatchesAndNoMatchMessages() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new PrintStream(output));
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Deadline("submit report", new TaskDateTime(LocalDate.of(2026, 9, 10))));

        ui.showSearchResults(taskList, "book");
        ui.showSearchResults(taskList, "missing");
        ui.showSchedule(taskList, LocalDate.of(2026, 9, 10));
        ui.showSchedule(taskList, LocalDate.of(2026, 9, 11));

        String text = output.toString();
        assertTrue(text.contains("read book"));
        assertTrue(text.contains("No tasks found"));
        assertTrue(text.contains("submit report"));
        assertTrue(text.contains("No scheduled tasks"));
    }

    @Test
    void statusAndPersistenceWarnings_showRelevantDetails() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new PrintStream(output));
        Todo task = new Todo("read book");

        ui.showDuplicateTaskWarning(1);
        ui.showTaskAdded(task, 1);
        ui.showTaskStatus(task, true, true);
        ui.showTaskStatus(task, true, false);
        ui.showTaskStatus(task, false, true);
        ui.showTaskStatus(task, false, false);
        ui.showTaskDeleted(task, 0);
        ui.showCorruptFileWarning();
        ui.showLoadingError();

        String text = output.toString();
        assertTrue(text.contains("already has that description"));
        assertTrue(text.contains("I'll note this down for you haiz"));
        assertTrue(text.contains("already done"));
        assertTrue(text.contains("Stop scamming me >:("));
        assertTrue(text.contains("Thanks for reminding me of your laziness..."));
        assertTrue(text.contains("Man got hacked ggwp"));
        assertTrue(text.contains("Hands can't save you from load failure zzz"));
    }

    @Test
    void helpAndWelcomeMessages_containUsableCommands() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new PrintStream(output));

        ui.showWelcomeMessage();
        ui.showHelp();

        String text = output.toString();
        assertTrue(text.contains("YourHand"));
        assertTrue(text.contains("todo DESCRIPTION"));
        assertTrue(text.contains("event DESCRIPTION"));
        assertTrue(text.contains("view schedule DATE"));
        assertTrue(text.contains("bye"));
    }

    @Test
    void readCommand_preservesWhitespaceForValidation() {
        Ui ui = new Ui(new Scanner(" todo read book\n"), new PrintStream(new ByteArrayOutputStream()));

        assertEquals(" todo read book", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }
}
