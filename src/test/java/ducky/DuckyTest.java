package ducky;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests command execution and validation in {@link Ducky}.
 */
class DuckyTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_invalidDelete_returnsErrorResponse() {
        Ducky ducky = createDucky();

        String response = ducky.getResponse("delete 1");

        assertTrue(response.contains("That task number does not exist"));
        assertTrue(ducky.lastResponseWasError());
    }

    @Test
    void getResponse_duplicateTask_returnsErrorResponse() {
        Ducky ducky = createDucky();

        ducky.getResponse("todo read book");
        String response = ducky.getResponse("todo read book");

        assertTrue(response.contains("already in your pond"));
        assertTrue(ducky.lastResponseWasError());
    }

    @Test
    void getResponse_successfulCommand_clearsErrorState() {
        Ducky ducky = createDucky();

        ducky.getResponse("delete 1");
        ducky.getResponse("todo read book");

        assertFalse(ducky.lastResponseWasError());
    }

    @Test
    void getResponse_taskCommands_updateTaskList() {
        Ducky ducky = createDucky();

        ducky.getResponse("todo read book");
        ducky.getResponse("mark 1");
        assertTrue(ducky.getResponse("list").contains("[X] read book"));

        ducky.getResponse("unmark 1");
        assertTrue(ducky.getResponse("list").contains("[ ] read book"));

        ducky.getResponse("delete 1");
        assertTrue(ducky.getResponse("list").contains("pond"));
    }

    @Test
    void getResponse_bye_setsExitState() {
        Ducky ducky = createDucky();

        ducky.getResponse("bye");

        assertTrue(ducky.isExit());
    }

    private Ducky createDucky() {
        return new Ducky(temporaryDirectory.resolve("tasks.txt").toString());
    }
}
