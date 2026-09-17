package megatron.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import megatron.exception.MegatronException;
import megatron.storage.TaskStorage;
import megatron.task.TaskList;
import megatron.task.Todo;
import megatron.ui.Ui;

/**
 * Tests exit state and display behavior by {@link ExitCommand}.
 */
class ExitCommandTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void execute_taskList_displaysGoodbyeWithoutChangingState() throws MegatronException {
        TaskList tasks = new TaskList(List.of(new Todo("task")));
        Path storageFile = tempDirectory.resolve("tasks.csv");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new ExitCommand().execute(tasks, createUi(output), new TaskStorage(storageFile.toString()));

        assertEquals(expectedGoodbyeOutput(), output.toString(StandardCharsets.UTF_8));
        assertEquals(1, tasks.size());
        assertEquals("task", tasks.getTask(1).getDescription());
        assertFalse(Files.exists(storageFile));
    }

    @Test
    void isExit_exitCommand_returnsTrue() {
        assertTrue(new ExitCommand().isExit());
    }

    private static Ui createUi(ByteArrayOutputStream output) {
        return new Ui(new Scanner(""), new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    private static String expectedGoodbyeOutput() {
        return "Retreat accepted. Try not to create more tasks while I'm gone!"
                + System.lineSeparator()
                + "____________________________________________________________" + System.lineSeparator();
    }
}
