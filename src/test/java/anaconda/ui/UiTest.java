package anaconda.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import anaconda.task.Deadline;
import anaconda.task.Event;
import anaconda.task.Task;
import anaconda.task.ToDo;
import anaconda.testutil.ConsoleSession;

/**
 * Tests console input and exact UI output without using the real terminal.
 */
@ResourceLock("SYSTEM_STREAMS")
public class UiTest {
    private static final String LINE = "____________________________________________________________\n";

    @Test
    public void constructor_suppliedStreams_isolatesInputAndOutputAndLeavesOutputOpen() {
        ByteArrayInputStream input = new ByteArrayInputStream("custom input\n".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
        try (ConsoleSession session = new ConsoleSession("console input\n");
                PrintStream output = new PrintStream(outputBuffer, true, StandardCharsets.UTF_8)) {
            try (Ui ui = new Ui(input, output)) {
                assertEquals("custom input", ui.readCommand());
                ui.showError("Custom error.");
            }
            output.println("Still open.");
            assertEquals("Hang on. Custom error.\nStill open.\n",
                    outputBuffer.toString(StandardCharsets.UTF_8).replace("\r\n", "\n"));
            assertEquals("", session.output());
            try (Ui consoleUi = new Ui()) {
                assertEquals("console input", consoleUi.readCommand());
            }
        }
    }

    @Test
    public void readCommand_multipleLines_trimsEdgesAndPreservesInternalWhitespace() {
        try (ConsoleSession session = new ConsoleSession("  todo Read  Book \t\n   \nBYE\n");
                Ui ui = new Ui()) {
            assertEquals("todo Read  Book", ui.readCommand());
            assertEquals("", ui.readCommand());
            assertEquals("BYE", ui.readCommand());
            assertEquals("", session.output());
        }
    }

    @Test
    public void showWelcome_startup_printsBannerAndPrompt() {
        try (ConsoleSession session = new ConsoleSession(""); Ui ui = new Ui()) {
            ui.showWelcome();
            String expected = LINE
                    + "    _    _   _    _    ____ ___  _   _ ____    _\n"
                    + "   / \\  | \\ | |  / \\  / ___/ _ \\| \\ | |  _ \\  / \\\n"
                    + "  / _ \\ |  \\| | / _ \\| |  | | | |  \\| | | | |/ _ \\\n"
                    + " / ___ \\| |\\  |/ ___ \\ |__| |_| | |\\  | |_| / ___ \\\n"
                    + "/_/   \\_\\_| \\_/_/   \\_\\____\\___/|_| \\_|____/_/   \\_\\\n"
                    + "\nYo, it's Anaconda.\nWhat do you need?\n";
            assertEquals(expected, session.output());
        }
    }

    @Test
    public void showLineAndGoodbye_printsSeparatorsAndFarewell() {
        try (ConsoleSession session = new ConsoleSession(""); Ui ui = new Ui()) {
            ui.showLine();
            ui.showGoodbye();
            assertEquals(LINE + LINE + "Alright, off you go. Try to get something done.\n" + LINE, session.output());
        }
    }

    @Test
    public void showCommandList_availableCommands_printsCompactGroups() {
        try (ConsoleSession session = new ConsoleSession(""); Ui ui = new Ui()) {
            ui.showCommandList();
            assertEquals("Available commands:\nAdd: todo, deadline, event\n"
                    + "View/search: list, find, /by, /from\nUpdate: mark, unmark, delete, clear\n"
                    + "History: undo, undo undo (redo)\nHelp: help\nExit: bye\n", session.output());
        }
    }

    @Test
    public void showErrorAndLoadingError_printsHelpfulMessages() {
        try (ConsoleSession session = new ConsoleSession(""); Ui ui = new Ui()) {
            ui.showError("Bad command.");
            ui.showLoadingError();
            assertEquals("Hang on. Bad command.\nHang on. Your saved list was compromised or could not be read. "
                    + "Starting with a new empty list.\n", session.output());
        }
    }

    @Test
    public void showTasks_fullAndFilteredLists_printsCorrectHeadersAndNumbering() {
        Task todo = new ToDo("book");
        todo.markAsDone();
        LocalDate date = LocalDate.of(2026, 8, 19);
        List<Task> tasks = List.of(todo, new Deadline("report", date), new Event("meeting", date, date));
        String rows = "1.[T][X] book\n2.[D][ ] report (by: Aug 19 2026)\n"
                + "3.[E][ ] meeting (from: Aug 19 2026 to: Aug 19 2026)\n";
        try (ConsoleSession session = new ConsoleSession(""); Ui ui = new Ui()) {
            ui.showTasks(tasks, false);
            ui.showTasks(tasks, true);
            assertEquals("Here's what you've got:\n" + rows + "Found these:\n" + rows, session.output());
            assertEquals(3, tasks.size());
        }
    }

    @Test
    public void showTasks_emptyLists_printsListHeaderAndNoMatchesMessage() {
        try (ConsoleSession session = new ConsoleSession(""); Ui ui = new Ui()) {
            ui.showTasks(List.of(), false);
            ui.showTasks(List.of(), true);
            assertEquals("Here's what you've got:\nNothing. No matching tasks.\n", session.output());
        }
    }

    @Test
    public void showFindResults_matchingAndEmptyLists_printsRequiredHeaderAndNumbering() {
        Task todo = new ToDo("read book");
        todo.markAsDone();
        List<Task> tasks = List.of(todo, new Deadline("return book", LocalDate.of(2026, 8, 19)));
        try (ConsoleSession session = new ConsoleSession(""); Ui ui = new Ui()) {
            ui.showFindResults(tasks);
            ui.showFindResults(List.of());
            assertEquals("Found these:\n1.[T][X] read book\n"
                    + "2.[D][ ] return book (by: Aug 19 2026)\n"
                    + "Nothing. No matching tasks.\n", session.output());
        }
    }

    @Test
    public void showFindResults_subsetOfTasks_numbersOnlyDisplayedTasks() {
        Task first = new ToDo("first");
        Task middle = new ToDo("middle");
        Task last = new ToDo("last");
        try (ConsoleSession session = new ConsoleSession(""); Ui ui = new Ui()) {
            ui.showTasks(List.of(first, middle, last), false);
            ui.showTasks(List.of(last), true);
            ui.showFindResults(List.of(first, last));
            assertEquals("Here's what you've got:\n1.[T][ ] first\n2.[T][ ] middle\n3.[T][ ] last\n"
                    + "Found these:\n1.[T][ ] last\n"
                    + "Found these:\n1.[T][ ] first\n2.[T][ ] last\n",
                    session.output());
        }
    }

    @Test
    public void showTaskAdded_taskAndSuppliedCount_printsThreeLines() {
        try (ConsoleSession session = new ConsoleSession(""); Ui ui = new Ui()) {
            Task task = new ToDo("book");
            ui.showTaskAdded(task, 12);
            assertEquals("Alright, added it:\n  [T][ ] book\n"
                    + "Now you have 12 tasks in the list.\n", session.output());
        }
    }

    @Test
    public void showDuplicateWarning_addedDuplicate_explainsAdditionAndUndo() {
        try (ConsoleSession session = new ConsoleSession(""); Ui ui = new Ui()) {
            ui.showDuplicateWarning();
            assertEquals("Did you forget? This task already exists.\n"
                    + "We can undo anyways. . .\n", session.output());
        }
    }

    @Test
    public void showTaskRemoved_taskAndSuppliedCount_printsThreeLines() {
        try (ConsoleSession session = new ConsoleSession(""); Ui ui = new Ui()) {
            Task task = new ToDo("book");
            ui.showTaskRemoved(task, 0);
            assertEquals("Gone. Hope you didn't need that:\n  [T][ ] book\n"
                    + "Now you have 0 tasks in the list.\n", session.output());
        }
    }

    @Test
    public void showMarked_bothStatuses_printsMatchingMessage() {
        try (ConsoleSession session = new ConsoleSession(""); Ui ui = new Ui()) {
            Task task = new ToDo("book");
            task.markAsDone();
            ui.showMarked(task, true);
            task.markAsUndone();
            ui.showMarked(task, false);
            assertEquals("Fine, that's done now:\n  [T][X] book\n"
                    + "Really? Unmarked? Alright . . .\n  [T][ ] book\n", session.output());
        }
    }

    @Test
    public void showCleared_success_keepsExistingPersonality() {
        try (ConsoleSession session = new ConsoleSession(""); Ui ui = new Ui()) {
            ui.showCleared();
            assertEquals("Fine. Everything's gone.\n", session.output());
        }
    }

    @Test
    public void showUndo_success_printsConfirmation() {
        try (ConsoleSession session = new ConsoleSession(""); Ui ui = new Ui()) {
            ui.showUndo();
            assertEquals("Undid the previous command.\n", session.output());
        }
    }

    @Test
    public void showRedo_success_printsConfirmation() {
        try (ConsoleSession session = new ConsoleSession(""); Ui ui = new Ui()) {
            ui.showRedo();
            assertEquals("Undid the previous undo.\n", session.output());
        }
    }

    @Test
    public void close_readerClosed_furtherReadsThrowIllegalStateException() {
        try (ConsoleSession session = new ConsoleSession("bye\n")) {
            Ui ui = new Ui();
            ui.close();
            assertThrows(IllegalStateException.class, ui::readCommand);
            assertEquals("", session.output());
        }
    }
}
