package carl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import carl.commands.CommandResult;
import carl.task.TaskManager;

public class CarlTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void start_emptyStorage_initializesWelcomeWithoutWarning() {
        Carl carl = new Carl(new TaskManager(temporaryDirectory.resolve("save.txt")));

        carl.start();

        assertEquals("Carl online. What are we getting done?", carl.getWelcomeMessage());
        assertNull(carl.getStartupWarning());
    }

    @Test
    public void getResponse_typicalWorkflow_returnsSuccessErrorAndExitResults() {
        Carl carl = new Carl(new TaskManager(temporaryDirectory.resolve("save.txt")));
        carl.start();

        CommandResult added = carl.getResponse("todo write tests");
        CommandResult listed = carl.getResponse("list");
        CommandResult duplicate = carl.getResponse("todo WRITE TESTS");
        CommandResult unknown = carl.getResponse("launch");
        CommandResult exit = carl.getResponse("bye");

        assertFalse(added.isError());
        assertTrue(listed.message().contains("write tests"));
        assertTrue(duplicate.isError());
        assertTrue(unknown.isError());
        assertTrue(exit.isExited());
    }

    @Test
    public void start_malformedSave_exposesRecoverableStartupWarning() throws Exception {
        Path saveFile = temporaryDirectory.resolve("save.txt");
        Files.writeString(saveFile, "not valid", StandardCharsets.UTF_8);
        Carl carl = new Carl(new TaskManager(saveFile));

        carl.start();

        assertTrue(carl.getStartupWarning().contains("line(s): [1]"));
        assertEquals("Radar clear — no tasks here.", carl.getResponse("list").message());
    }

    @Test
    public void getResponse_beforeStart_returnsSafeUnexpectedError() {
        Carl carl = new Carl(new TaskManager(temporaryDirectory.resolve("save.txt")));

        CommandResult result = carl.getResponse("list");

        assertTrue(result.isError());
        assertEquals("An unexpected error occurred. The command could not be completed safely.", result.message());
    }

    @Test
    public void getResponse_unwritableDestination_returnsStorageErrorAndRollsBack() {
        Carl carl = new Carl(new TaskManager(temporaryDirectory));
        carl.start();

        CommandResult addResult = carl.getResponse("todo write tests");
        CommandResult listResult = carl.getResponse("list");

        assertTrue(addResult.isError());
        assertTrue(addResult.message().contains("could not save"));
        assertEquals("Radar clear — no tasks here.", listResult.message());
    }
}
