package niulai.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import niulai.model.Deadline;
import niulai.model.Event;
import niulai.model.Task;
import niulai.model.TaskList;
import niulai.model.Todo;

/** Tests console input handling and every user-facing text branch. */
class UiTest {
    /** Captures each logical line while retaining the real UI formatting behavior. */
    private static class RecordingUi extends Ui {
        /** Lines written by the UI in call order. */
        private final ArrayList<String> lines = new ArrayList<>();

        /** Records one output line instead of writing it to standard output. */
        @Override
        protected void writeLine(String text) {
            lines.add(text);
        }

        /** Returns every recorded line and clears the recorder. */
        List<String> consumeLines() {
            List<String> capturedLines = List.copyOf(lines);
            lines.clear();
            return capturedLines;
        }
    }

    /** Verifies the console scanner detects input and trims the command it reads. */
    @Test
    @ResourceLock("SYSTEM_IN")
    void readCommand_paddedInput_returnsTrimmedLineAndReachesEnd() {
        InputStream originalInput = System.in;
        try {
            System.setIn(new ByteArrayInputStream("  list  \n".getBytes(StandardCharsets.UTF_8)));
            Ui ui = new Ui();

            assertTrue(ui.hasNextLine());
            assertEquals("list", ui.readCommand());
            assertFalse(ui.hasNextLine());
        } finally {
            System.setIn(originalInput);
        }
    }

    /** Verifies the concrete console writer sends separators to standard output. */
    @Test
    @ResourceLock(Resources.SYSTEM_OUT)
    void showSeparator_consoleUi_printsSeparator() {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));

            new Ui().showSeparator();

            assertEquals("____________________________________________________________",
                    capturedOutput.toString(StandardCharsets.UTF_8).strip());
        } finally {
            System.setOut(originalOutput);
        }
    }

    /** Verifies startup and exit text contains the banner, greeting, and farewell. */
    @Test
    void showWelcomeAndBye_recordsCompleteSessionFraming() {
        RecordingUi ui = new RecordingUi();

        ui.showWelcome();
        ui.showBye();
        List<String> lines = ui.consumeLines();

        assertEquals("|\\ | | |  | |     /\\  |\n| \\| | \\__/ |___ /~~\\ |\n", lines.get(0));
        assertTrue(lines.contains("     Hello! I'm NiuLai!"));
        assertTrue(lines.contains("     What can I do for you?"));
        assertTrue(lines.contains("     Bye. Hope not to see you again."));
    }

    /** Verifies list output numbers every task in display order. */
    @Test
    void showList_mixedTasks_numbersTasksInOrder() {
        RecordingUi ui = new RecordingUi();
        TaskList tasks = new TaskList(
                new Todo("first"),
                new Deadline("second", "tomorrow"));

        ui.showList(tasks);

        assertEquals(List.of(
                "     Here are the tasks in your list:",
                "     1.[T][ ] first",
                "     2.[D][ ] second (by: tomorrow)",
                "    ____________________________________________________________\n"
        ), ui.consumeLines());
    }

    /** Verifies date search includes dated tasks, excludes todos, and retains original indexes. */
    @Test
    void showTasksOnDate_matchingTasks_showsOnlyDeadlinesAndEvents() {
        RecordingUi ui = new RecordingUi();
        TaskList tasks = new TaskList(
                new Todo("undated"),
                new Deadline("due", "2026-09-10"),
                new Event("conference", "2026-09-09", "2026-09-11"),
                new Deadline("later", "2026-09-12"));

        ui.showTasksOnDate(tasks, LocalDate.of(2026, 9, 10));
        List<String> lines = ui.consumeLines();

        assertTrue(lines.contains("     2.[D][ ] due (by: Sep 10 2026)"));
        assertTrue(lines.contains(
                "     3.[E][ ] conference (from: 2026-09-09 to: 2026-09-11)"));
        assertFalse(lines.stream().anyMatch(line -> line.contains("undated")));
        assertFalse(lines.stream().anyMatch(line -> line.contains("later")));
    }

    /** Verifies empty date and keyword searches explain that no task matched. */
    @Test
    void showSearchResults_noMatches_showsEmptyResultMessages() {
        RecordingUi ui = new RecordingUi();
        TaskList tasks = new TaskList(new Todo("read book"));

        ui.showTasksOnDate(tasks, LocalDate.of(2026, 9, 10));
        assertTrue(ui.consumeLines().contains("     No deadlines or events found."));

        ui.showTasksContaining(tasks, "holiday");
        assertTrue(ui.consumeLines().contains("     No matching tasks found."));
    }

    /** Verifies keyword search retains original task indexes rather than renumbering matches. */
    @Test
    void showTasksContaining_matchingTasks_retainsOriginalIndexes() {
        RecordingUi ui = new RecordingUi();
        TaskList tasks = new TaskList(
                new Todo("read notes"),
                new Todo("buy food"),
                new Todo("read book"));

        ui.showTasksContaining(tasks, "READ");
        List<String> lines = ui.consumeLines();

        assertTrue(lines.contains("     1.[T][ ] read notes"));
        assertTrue(lines.contains("     3.[T][ ] read book"));
        assertFalse(lines.stream().anyMatch(line -> line.contains("buy food")));
    }

    /** Verifies all task-changing confirmations show their task and resulting count or state. */
    @Test
    void showTaskConfirmations_validTask_recordsExpectedMessages() {
        RecordingUi ui = new RecordingUi();
        Task task = new Todo("read book");

        ui.showTaskAdded(task, 1);
        assertTrue(String.join("\n", ui.consumeLines()).contains("Now you have 1 tasks"));

        task.markAsDone();
        ui.showTaskMarked(task);
        assertTrue(String.join("\n", ui.consumeLines()).contains("[T][X] read book"));

        task.markAsNotDone();
        ui.showTaskUnmarked(task);
        assertTrue(String.join("\n", ui.consumeLines()).contains("not done yet"));

        ui.showTaskDeleted(task, 0);
        assertTrue(String.join("\n", ui.consumeLines()).contains("Now you have 0 tasks"));
    }

    /** Verifies undo, error, recovery, and loading diagnostics use their distinct messages. */
    @Test
    void showDiagnostics_variedConditions_recordsExpectedMessages() {
        RecordingUi ui = new RecordingUi();

        ui.showUndo();
        assertTrue(String.join("\n", ui.consumeLines()).contains("undone the last command"));

        ui.showNoUndo();
        assertTrue(String.join("\n", ui.consumeLines()).contains("nothing to undo"));

        ui.showError("NOOO!!! custom error");
        assertTrue(String.join("\n", ui.consumeLines()).contains("custom error"));

        ui.showLoadingError("cannot read tasks");
        assertTrue(String.join("\n", ui.consumeLines()).contains("NOOO!!! cannot read tasks"));

        ui.showRecoveryWarning(List.of(2));
        assertTrue(String.join("\n", ui.consumeLines()).contains("on line 2"));

        ui.showRecoveryWarning(List.of(3, 7));
        assertTrue(String.join("\n", ui.consumeLines()).contains("on lines 3, 7"));

        ui.showRecoveryWarning(List.of());
        assertEquals(List.of(), ui.consumeLines());
    }
}
