package sandrone;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SandroneTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_addCommand_returnsCommandOutput() {
        Sandrone sandrone = new Sandrone(temporaryDirectory.resolve("tasks.txt").toString());

        String response = sandrone.getResponse("todo read JavaFX guide");

        assertTrue(response.contains("Recorded. Do try not to make me repeat the process."));
        assertTrue(response.contains("todo read JavaFX guide"));
        assertTrue(response.contains("You now have 1 task in the list"));

        String secondResponse = sandrone.getResponse("todo write tests");

        assertTrue(secondResponse.contains("You now have 2 tasks in the list"));
    }

    @Test
    void getResponse_byeCommand_marksApplicationForExit() {
        Sandrone sandrone = new Sandrone(temporaryDirectory.resolve("tasks.txt").toString());

        String response = sandrone.getResponse("bye");

        assertTrue(response.contains("Your records have been preserved. Do not lose them."));
        assertTrue(sandrone.isExitRequested());
    }

    @Test
    void getResponse_statsCommand_returnsFormattedStatistics() {
        Sandrone sandrone = new Sandrone(temporaryDirectory.resolve("tasks.txt").toString());

        String response = sandrone.getResponse("stats");

        assertTrue(response.contains("Task statistics"));
        assertFalse(response.contains("Task statistics:"));
        assertTrue(response.contains("By type"));
        assertFalse(response.contains("By type:"));
        assertTrue(response.contains("Total tasks: 0 / 10"));
        assertTrue(response.contains("Completion rate: 0.0%"));
        assertTrue(response.contains("Remaining task slots: 10"));
    }

    @Test
    void getResponse_invalidCommand_marksResponseAsError() {
        Sandrone sandrone = new Sandrone(temporaryDirectory.resolve("tasks.txt").toString());

        String errorResponse = sandrone.getResponse("not a command");

        assertTrue(errorResponse.contains("That input does not form a valid instruction. Correct it: Invalid command"));
        assertTrue(sandrone.wasLastResponseError());

        sandrone.getResponse("stats");

        assertFalse(sandrone.wasLastResponseError());
    }
}
