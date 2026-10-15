package zeus;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command responses exposed to the graphical interface. */
public class ZeusTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void getResponse_addAndListCommands_returnsFormattedReplies() {
        Zeus zeus = new Zeus(temporaryDirectory.resolve("zeus.txt").toString());

        String addResponse = zeus.getResponse("todo read book");
        String listResponse = zeus.getResponse("list");

        assertEquals("It is decreed. Your task has been added.\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 tasks in the list.", addResponse);
        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] read book", listResponse);
        assertFalse(zeus.isExitRequested());
        assertFalse(zeus.wasLastResponseError());
    }

    @Test
    public void getResponse_invalidAndExitCommands_returnsRepliesAndTracksExit() {
        Zeus zeus = new Zeus(temporaryDirectory.resolve("zeus.txt").toString());

        assertEquals("OOPS!!! Even Zeus cannot understand that command. "
                        + "Type 'help' to see available commands.",
                zeus.getResponse("unknown"));
        assertFalse(zeus.isExitRequested());
        assertTrue(zeus.wasLastResponseError());

        assertEquals("Farewell, mortal.", zeus.getResponse("bye"));
        assertTrue(zeus.isExitRequested());
        assertFalse(zeus.wasLastResponseError());
    }

    @Test
    public void getResponse_helpCommand_returnsGuidanceWithoutSaving() throws IOException {
        Path dataFile = temporaryDirectory.resolve("zeus.txt");
        Zeus zeus = new Zeus(dataFile.toString());
        zeus.getResponse("todo read book");
        byte[] dataBeforeHelp = Files.readAllBytes(dataFile);

        String response = zeus.getResponse("help");

        assertEquals("Here are the commands you can use:\n"
                + "  todo DESCRIPTION - Add a task without a date.\n"
                + "  deadline DESCRIPTION /by yyyy-MM-dd - Add a task with a due date.\n"
                + "  event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd - Add a task with start and end dates.\n"
                + "  list - Show all tasks.\n"
                + "  find KEYWORD - Find tasks whose descriptions contain the keyword.\n"
                + "  mark NUMBER - Mark the numbered task as done.\n"
                + "  unmark NUMBER - Mark the numbered task as not done.\n"
                + "  delete NUMBER - Delete the numbered task.\n"
                + "  help - Show this help page.\n"
                + "  bye - Exit Zeus.\n"
                + "\n"
                + "Dates must use yyyy-MM-dd, for example 2026-09-06.", response);
        assertArrayEquals(dataBeforeHelp, Files.readAllBytes(dataFile));
        assertFalse(zeus.isExitRequested());
    }
}
