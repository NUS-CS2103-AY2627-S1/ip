package goat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests complete command handling without starting either user interface.
 */
public class GoatTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void getResponse_addMarkFindDelete_updatesAndPersistsTasks() {
        Path dataFile = tempDirectory.resolve("goat.txt");
        Goat goat = new Goat(dataFile);

        assertTrue(goat.getResponse("todo read book").contains("Hoof-tastic!"));
        assertTrue(goat.getResponse("mark 1").contains("Baa-rilliant!"));
        assertTrue(goat.getResponse("find book").contains("[T][X] read book"));
        assertTrue(goat.getResponse("delete 1").contains("One less climb!"));
        assertEquals("Here are the tasks in your list:", goat.getResponse("list"));
        assertTrue(Files.exists(dataFile));
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorAndKeepsRunning() {
        Goat goat = new Goat(tempDirectory.resolve("goat.txt"));

        String response = goat.getResponse("dance");

        assertTrue(response.startsWith("OOPS!!!"));
        assertFalse(goat.isExitRequested());
    }

    @Test
    public void getResponse_bye_requestsExit() {
        Goat goat = new Goat(tempDirectory.resolve("goat.txt"));

        assertEquals("Time to hoof it. See you on the next climb!", goat.getResponse("bye"));
        assertTrue(goat.isExitRequested());
    }

    @Test
    public void getResponse_sort_ordersTasksAndPersistsOrder() {
        Path dataFile = tempDirectory.resolve("goat.txt");
        Goat goat = new Goat(dataFile);
        goat.getResponse("todo undated task");
        goat.getResponse("deadline later /by 2026-12-20");
        goat.getResponse("event earlier /from 2026-10-01 /to 2026-10-02");

        String response = goat.getResponse("sort");
        Goat reloadedGoat = new Goat(dataFile);

        assertTrue(response.indexOf("[E][ ] earlier") < response.indexOf("[D][ ] later"));
        assertTrue(response.indexOf("[D][ ] later") < response.indexOf("[T][ ] undated task"));
        String reloadedList = reloadedGoat.getResponse("list");
        assertTrue(reloadedList.indexOf("[E][ ] earlier")
                < reloadedList.indexOf("[D][ ] later"));
        assertTrue(reloadedList.indexOf("[D][ ] later")
                < reloadedList.indexOf("[T][ ] undated task"));
    }

    @Test
    public void getResponse_saveFailure_rollsBackAddedTask() throws Exception {
        Path blocker = tempDirectory.resolve("not-a-directory");
        Files.writeString(blocker, "blocks directory creation");
        Goat goat = new Goat(blocker.resolve("goat.txt"));

        String response = goat.getResponse("todo temporary task");

        assertTrue(response.startsWith("OOPS!!! I couldn't save your tasks:"));
        assertEquals("Here are the tasks in your list:", goat.getResponse("list"));
    }
}
