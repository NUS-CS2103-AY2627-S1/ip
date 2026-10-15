package gooble;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the application facade without requiring interactive standard input. */
class GoobleTest {
    @Test
    void executeCommand_returnsExitStateAndReportsErrors(@TempDir Path tempDir) {
        Gooble gooble = new Gooble(tempDir.resolve("tasks.txt").toString());
        StringBuilder output = new StringBuilder();

        assertFalse(gooble.executeCommand("todo read book", output::append));
        assertTrue(gooble.executeCommand("bye", output::append));
        assertFalse(gooble.executeCommand("mark", output::append));
        assertTrue(output.toString().contains("[T][ ] read book"));
        assertTrue(output.toString().contains("Please provide a valid task number."));
    }

    @Test
    void executeCommand_persistsTasksAcrossApplicationInstances(@TempDir Path tempDir) {
        String filePath = tempDir.resolve("tasks.txt").toString();
        Gooble first = new Gooble(filePath);
        first.executeCommand("todo read book", ignored -> { });

        Gooble second = new Gooble(filePath);
        StringBuilder output = new StringBuilder();
        second.executeCommand("list", output::append);

        assertTrue(output.toString().contains("read book"));
    }
}
