package wobble;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import wobble.parser.Parser;
import wobble.storage.Storage;
import wobble.tasks.TaskList;
import wobble.ui.Ui;

/** Tests console command routing without using Wobble's real data file. */
class WobbleTest {
    @TempDir
    Path temporaryDirectory;

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private InputStream originalInput;
    private PrintStream originalOutput;

    @BeforeEach
    void redirectConsole() {
        originalInput = System.in;
        originalOutput = System.out;
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void restoreConsole() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    void run_supportedCommands_updatesAndDisplaysTasks() throws Exception {
        Path file = temporaryDirectory.resolve("wobble.txt");
        String commands = String.join(System.lineSeparator(),
                "todo read book",
                "deadline submit report /by 2099-08-27 1800",
                "event meeting /from 2099-08-27 1900 /to 2099-08-27 2000",
                "list",
                "find book",
                "due on 2099-08-27",
                "mark 1",
                "unmark 1",
                "delete 1",
                "remove 1",
                "help",
                "reminders",
                "not-a-command",
                "bye") + System.lineSeparator();
        System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));

        new Wobble(new Ui(), new Storage(file), new Parser()).run();

        String result = output.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("Whirr... memory tray scan complete."));
        assertTrue(result.contains("Here are the matching tasks in your list:"));
        assertTrue(result.contains("Time scanner locked onto 2099-08-27:"));
        assertTrue(result.contains("Task marked as done"));
        assertTrue(result.contains("Task marked as not done"));
        assertTrue(result.contains("WOBBLE COMMAND DECK"));
        assertTrue(result.contains("Radar clear."));
        assertTrue(result.contains("I do not recognize that command."));
        assertTrue(result.contains("Wobble is signing off."));

        TaskList savedTasks = new Storage(file).load();
        assertEquals(1, savedTasks.size());
        assertEquals("meeting", savedTasks.get(1).getDescription());
    }

    @Test
    void run_missingStorageDirectory_reportsLoadingError() throws Exception {
        Path directory = temporaryDirectory.resolve("saved-tasks");
        Files.createDirectory(directory);
        System.setIn(new ByteArrayInputStream(new byte[0]));

        new Wobble(new Ui(), new Storage(directory), new Parser()).run();

        assertTrue(output.toString(StandardCharsets.UTF_8)
                .contains("saved tasks could not be loaded; starting with an empty tray."));
    }
}
