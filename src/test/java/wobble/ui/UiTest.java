package wobble.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wobble.parser.DateTimeParser;
import wobble.tasks.Deadline;
import wobble.tasks.TaskList;
import wobble.tasks.Todo;

/** Tests the UI's input and output behavior. */
class UiTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOutput;

    @BeforeEach
    void redirectOutput() {
        originalOutput = System.out;
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOutput);
    }

    @Test
    void readCommand_inputAvailable_returnsNextLine() {
        Ui ui = new Ui();
        Scanner scanner = new Scanner("todo read book\nbye\n");
        assertEquals("todo read book", ui.readCommand(scanner));
        assertEquals("bye", ui.readCommand(scanner));
        scanner.close();
    }

    @Test
    void readCommand_inputExhausted_returnsNull() {
        Ui ui = new Ui();
        Scanner scanner = new Scanner("");
        assertNull(ui.readCommand(scanner));
        scanner.close();
    }

    @Test
    void showWelcome_containsWobbleGreeting() {
        new Ui().showWelcome();
        assertTrue(output.toString().contains("Hello! I'm Wobble."));
        assertTrue(output.toString().contains("Systems Online"));
        assertTrue(output.toString().contains("Beep boop!"));
        assertTrue(output.toString().contains("Memory tray calibrated."));
    }

    @Test
    void showHelp_containsCommandFormats() {
        new Ui().showHelp();
        assertTrue(output.toString().contains("deadline <description> /by <date/time>"));
        assertTrue(output.toString().contains("reminders [number of days]"));
        assertTrue(output.toString().contains("Time: <date> HHmm (e.g., 2026-09-15 1800)"));
        assertTrue(output.toString().contains("Time: <date> HH:mm (e.g., 2026-09-15 18:00)"));
        assertTrue(output.toString().contains("Valid time range: 00:00 to 23:59 (24-hour clock)"));
    }

    @Test
    void showTasks_containsTaskAndEmptyTrayMessage() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        new Ui().showTasks(taskList);
        assertTrue(output.toString().contains("1. [T][ ] read book"));
    }

    @Test
    void showTasks_emptyList_reportsTidyTray() {
        new Ui().showTasks(new TaskList());

        assertTrue(output.toString().contains("Nothing is wobbling on the tray yet."));
    }

    @Test
    void showMatchingTasks_matchingKeyword_showsMatchingTaskNumbers() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("buy bread"));

        new Ui().showMatchingTasks(taskList, "book");

        assertTrue(output.toString().contains("matching tasks in your list"));
        assertTrue(output.toString().contains("1. [T][ ] read book"));
        assertFalse(output.toString().contains("2. [T][ ] buy bread"));
    }

    @Test
    void showMatchingTasks_noMatches_reportsClearSignal() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        new Ui().showMatchingTasks(taskList, "holiday");

        assertTrue(output.toString().contains("No tasks match that keyword."));
    }

    @Test
    void showTaskAdded_containsTaskAndCount() {
        new Ui().showTaskAdded(new Todo("read book"), 1);
        assertTrue(output.toString().contains("[T][ ] read book"));
        assertTrue(output.toString().contains("Task docked in my memory tray"));
        assertTrue(output.toString().contains("Now you have 1 tasks"));
    }

    @Test
    void showReminders_emptyResults_reportsClearRadar() {
        new Ui().showReminders(new TaskList(), List.of(),
                LocalDateTime.of(2026, 9, 16, 12, 0), 7);
        assertTrue(output.toString().contains("Radar clear."));
        assertTrue(output.toString().contains("next 7 days"));
    }

    @Test
    void showReminders_withResults_showsTasksAndWindowStart() {
        LocalDateTime now = LocalDateTime.now().plusDays(2);
        TaskList taskList = new TaskList();
        taskList.add(new Deadline("submit report", now.plusDays(1)));

        new Ui().showReminders(taskList, List.of(1), now, 7);

        assertTrue(output.toString().contains("upcoming reminders"));
        assertTrue(output.toString().contains("1. [D][ ] submit report"));
        assertTrue(output.toString().contains("Reminder window starts " + DateTimeParser.format(now) + "."));
    }

    @Test
    void showDiagnostic_containsMessage() {
        new Ui().showDiagnostic("test error");
        assertEquals("Wobble diagnostic: test error" + System.lineSeparator(), output.toString());
    }

    @Test
    void showGoodbye_containsGoodbyeMessage() {
        new Ui().showGoodbye();
        assertTrue(output.toString().contains("Bye, human! Wobble is signing off."));
        assertTrue(output.toString().contains("SHUTDOWN_SEQUENCE :: START"));
        assertTrue(output.toString().contains("> save memory .......... OK"));
        assertTrue(output.toString().contains("> lock task tray .......... OK"));
        assertTrue(output.toString().contains("> disconnect .......... OK"));
        assertTrue(output.toString().contains("STATUS :: OFFLINE"));
        assertTrue(output.toString().contains("See you on the next boot, human."));
    }
}
