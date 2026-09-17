package megatron.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import megatron.exception.MegatronException;
import megatron.storage.TaskStorage;
import megatron.task.TaskList;
import megatron.ui.Ui;

/**
 * Tests a parser-to-command workflow using real task storage and console output.
 */
class CommandWorkflowIntegrationTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void parsedCommands_addMarkAndFindTaskAcrossWorkflow() throws MegatronException {
        Parser parser = new Parser();
        TaskList tasks = new TaskList();
        TaskStorage storage = new TaskStorage(tempDirectory.resolve("tasks.csv").toString());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new Scanner(""), new PrintStream(output, true, StandardCharsets.UTF_8));

        parser.parse("todo read book").execute(tasks, ui, storage);
        parser.parse("mark 1").execute(tasks, ui, storage);
        parser.parse("find bok").execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertTrue(tasks.getTask(1).isDone());
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("[T][X] read book"));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains(
                "Target acquired! Here are the matching tasks:"));
    }
}
