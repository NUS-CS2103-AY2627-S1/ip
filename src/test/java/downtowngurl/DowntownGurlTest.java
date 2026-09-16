package downtowngurl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the chatbot response flow used by the JavaFX GUI.
 */
public class DowntownGurlTest {
    @TempDir
    private Path temporaryFolder;

    /**
     * Checks that GUI responses use the same command execution flow as the CLI.
     */
    @Test
    public void getResponse_addAndListTask_returnsRealTaskMessages() {
        DowntownGurl downtownGurl = new DowntownGurl(temporaryFolder.resolve("tasks.txt"));

        String addResponse = downtownGurl.getResponse("todo read book");
        String listResponse = downtownGurl.getResponse("list");

        assertTrue(addResponse.contains("Gotcha. Noted it downz:"));
        assertTrue(addResponse.contains("[T][ ] read book"));
        assertTrue(listResponse.contains("Here's your tasks:"));
        assertTrue(listResponse.contains("1. [T][ ] read book"));
    }

    /**
     * Checks that the GUI response flow preserves the CLI exit command behavior.
     */
    @Test
    public void getResponse_byeCommand_setsExitFlag() {
        DowntownGurl downtownGurl = new DowntownGurl(temporaryFolder.resolve("tasks.txt"));

        assertFalse(downtownGurl.isExit());
        String response = downtownGurl.getResponse("bye");

        assertTrue(response.contains("That's bombz. Byes!"));
        assertTrue(downtownGurl.isExit());
    }
}
