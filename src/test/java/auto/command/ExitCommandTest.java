package auto.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import auto.task.TaskList;

class ExitCommandTest extends CommandTestSupport {
    @TempDir
    Path tempDirectory;

    @Test
    void execute_always_displaysGoodbyeWithoutMutationOrSave() {
        TaskList tasks = new TaskList();
        ExitCommand command = new ExitCommand();

        CommandResult result = command.execute(tasks, failingStorage(tempDirectory));

        assertEquals(0, tasks.size());
        assertTrue(result.message().contains("Okay, bye lah! Go take a kopi break."));
    }
}
