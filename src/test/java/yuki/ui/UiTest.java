package yuki.ui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import yuki.task.Deadline;
import yuki.task.Event;
import yuki.task.Task;
import yuki.task.ToDo;
import yuki.time.TaskDateTime;

/** Tests command-line input handling and response formatting. */
class UiTest {
    @Test
    void readCommand_multipleLines_linesReturnedUntilInputEnds() {
        InputStream originalInput = System.in;
        try {
            System.setIn(new ByteArrayInputStream(
                    "list\nbye\n".getBytes(StandardCharsets.UTF_8)));
            Ui ui = new Ui();

            assertTrue(ui.hasNextCommand());
            assertEquals("list", ui.readCommand());
            assertTrue(ui.hasNextCommand());
            assertEquals("bye", ui.readCommand());
            assertFalse(ui.hasNextCommand());
        } finally {
            System.setIn(originalInput);
        }
    }

    @Test
    void showResponses_silentUi_exactTextRecorded() {
        Ui ui = Ui.createSilentUi();
        Task first = new ToDo("read book");
        Task second = new ToDo("buy food");
        List<Task> tasks = List.of(first, second);

        ui.showWelcome();
        String welcome = ui.getLastResponse();
        ui.showTaskList(tasks);
        String taskList = ui.getLastResponse();
        ui.showMatchingTasks(List.of(second));
        String matches = ui.getLastResponse();
        ui.showUpcomingTasks(tasks, List.of(2));
        String upcoming = ui.getLastResponse();
        ui.showNoUpcomingTasks();
        String noUpcoming = ui.getLastResponse();
        ui.showTaskStatusChanged("Changed:", first);
        String statusChanged = ui.getLastResponse();
        ui.showTaskAdded(first, 2);
        String added = ui.getLastResponse();
        ui.showTaskDeleted(second, 1);
        String deleted = ui.getLastResponse();
        ui.showError("Bad command.");
        String error = ui.getLastResponse();
        ui.showLoadingError("Broken file.");
        String loadingError = ui.getLastResponse();
        ui.showGoodbye();

        String newline = System.lineSeparator();
        assertAll(() -> assertTrue(welcome.startsWith("__   __")), () ->
                assertTrue(welcome.endsWith("...Hello. This is Yuki. What do you need?")), () ->
                assertEquals("Here... These are the tasks you have:" + newline
                        + "1.[T][ ] read book" + newline + "2.[T][ ] buy food", taskList), () ->
                assertEquals("Here... These are the matching tasks in your list:" + newline
                        + "1.[T][ ] buy food", matches), () ->
                assertEquals("Here... These tasks are due in the next 24 hours:" + newline
                        + "2.[T][ ] buy food", upcoming), () ->
                assertEquals("You have no tasks due in the next 24 hours.", noUpcoming), () ->
                assertEquals("Changed:" + newline + "[T][ ] read book", statusChanged), () ->
                assertEquals("Alright... I've added it." + newline + "  [T][ ] read book"
                        + newline + "There are 2 tasks now.", added), () ->
                assertEquals("Alright... I've removed it." + newline + "  [T][ ] buy food"
                        + newline + "There are 1 tasks now.", deleted), () ->
                assertEquals("I couldn't process that. Bad command.", error), () ->
                assertEquals("I couldn't load the saved tasks. Broken file.", loadingError), () ->
                assertEquals("...Goodbye.", ui.getLastResponse()));
    }

    @Test
    void showResponses_outputEnabled_responsesPrinted() {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
            Ui ui = new Ui();

            ui.showGoodbye();
            ui.showLoadingError("Broken file.");
        } finally {
            System.setOut(originalOutput);
        }

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        assertAll(() -> assertTrue(output.contains("---")), () ->
                assertTrue(output.contains("...Goodbye.")), () ->
                assertTrue(output.contains("I couldn't load the saved tasks. Broken file.")));
    }

    @Test
    void showTaskLists_emptyLists_noTasksMessagesRecorded() {
        Ui ui = Ui.createSilentUi();

        ui.showTaskList(List.of());
        String taskList = ui.getLastResponse();
        ui.showMatchingTasks(List.of());
        String matches = ui.getLastResponse();

        assertAll(() -> assertEquals("...There aren't any tasks in your list.", taskList), () ->
                assertEquals("...There aren't any matching tasks.", matches));
    }

    @Test
    void showTaskAdded_unrecognizedReminderDates_warningIncluded() {
        Ui ui = Ui.createSilentUi();
        String warning = "...I couldn't recognize that date, so this task won't appear in reminders.";

        ui.showTaskAdded(new Deadline("submit report", TaskDateTime.of("tomorrow night")), 1);
        String deadlineResponse = ui.getLastResponse();
        ui.showTaskAdded(new Event("meeting", TaskDateTime.of("next Friday"),
                TaskDateTime.of("later")), 2);
        String eventResponse = ui.getLastResponse();
        ui.showTaskAdded(new Event("workshop", TaskDateTime.of(LocalDate.of(2026, 9, 18)),
                TaskDateTime.of("until finished")), 3);
        String eventWithRecognizedStartResponse = ui.getLastResponse();

        assertAll(() -> assertTrue(deadlineResponse.endsWith(warning)), () ->
                assertTrue(eventResponse.endsWith(warning)), () ->
                assertFalse(eventWithRecognizedStartResponse.contains(warning)));
    }

    @Test
    void showUpcomingTasks_numberOutsideTaskList_assertionErrorThrown() {
        Ui ui = Ui.createSilentUi();

        assertThrows(AssertionError.class, () ->
                ui.showUpcomingTasks(List.of(new ToDo("read book")), List.of(0)));
    }
}
