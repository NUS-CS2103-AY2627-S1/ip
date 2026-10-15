package clover.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import clover.exception.CloverException;
import clover.storage.Storage;
import clover.task.Deadline;
import clover.task.TaskList;
import clover.task.ToDo;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

/** Tests searching tasks by a keyword in their descriptions. */
class FindCommandTest {
    @TempDir
    Path tempDir;

    @Test
    void execute_caseInsensitiveMatchingTasks_displaysResultsInTaskOrder() throws CloverException {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("read book"));
        taskList.add(new Deadline("return BOOK", LocalDate.of(2026, 6, 6)));
        taskList.add(new ToDo("buy groceries"));

        String output = captureExecutionOutput(new FindCommand("BoOk"), taskList);

        assertEquals("The grove found these matching quests:" + System.lineSeparator()
                + "1.[T] [ ] read book" + System.lineSeparator()
                + "2.[D] [ ] return BOOK (by: Jun 6 2026)" + System.lineSeparator(), output);
    }

    @Test
    void constructor_blankKeyword_exceptionThrown() {
        CloverException exception = assertThrows(CloverException.class, () -> new FindCommand("   "));

        assertEquals("Give the grove a keyword to search for.", exception.getMessage());
    }

    @Test
    void execute_noMatchingTasks_displaysClearEmptyResultMessage() throws CloverException {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("read book"));

        String output = captureExecutionOutput(new FindCommand("groceries"), taskList);

        assertEquals("No matching tasks found." + System.lineSeparator(), output);
    }

    @Test
    void execute_turkishDefaultLocale_matchesCaseInsensitiveDescriptions() throws CloverException {
        Locale originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));
        try {
            TaskList taskList = new TaskList();
            taskList.add(new ToDo("FIX parser"));

            String output = captureExecutionOutput(new FindCommand("fix"), taskList);

            assertEquals("The grove found these matching quests:" + System.lineSeparator()
                    + "1.[T] [ ] FIX parser" + System.lineSeparator(), output);
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    /** Executes a command while capturing the console output it sends through the UI. */
    private String captureExecutionOutput(FindCommand command, TaskList taskList) throws CloverException {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            command.execute(taskList, new TutoreeList(), new Ui(), new Storage(tempDir.resolve("clover.txt")));
        } finally {
            System.setOut(originalOut);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
