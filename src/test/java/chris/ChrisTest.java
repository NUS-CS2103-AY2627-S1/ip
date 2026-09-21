package chris;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the command-response interface shared with the GUI. */
public class ChrisTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void getResponse_addAndListTask_returnsExpectedResponses() {
        Chris chris = new Chris(temporaryDirectory.resolve("data/tasks.txt").toString());

        String addResponse = chris.getResponse("todo read book");
        String listResponse = chris.getResponse("list");

        assertTrue(addResponse.contains("I've added this task"));
        assertTrue(addResponse.contains("[T][ ] read book"));
        assertTrue(listResponse.contains("1.[T][ ] read book"));
    }

    @Test
    public void getResponse_bye_setsExitRequested() {
        Chris chris = new Chris(temporaryDirectory.resolve("data/tasks.txt").toString());

        String response = chris.getResponse("bye");

        assertEquals("Bye. Hope to see you again soon!", response.strip());
        assertTrue(chris.isExitRequested());
    }

    @Test
    public void getResponse_help_listsAvailableCommands() {
        Chris chris = new Chris(temporaryDirectory.resolve("data/tasks.txt").toString());

        String response = chris.getResponse("help");

        assertTrue(response.contains("todo DESCRIPTION"));
        assertTrue(response.contains("deadline DESCRIPTION /by yyyy-MM-dd HHmm"));
        assertTrue(response.contains("list | find KEYWORD"));
    }

    @Test
    public void getResponse_invalidInput_keepsTasksUnchanged() {
        Chris chris = new Chris(temporaryDirectory.resolve("data/tasks.txt").toString());
        chris.getResponse("todo read book");

        assertTrue(chris.getResponse("deadline impossible /by 2026-02-30 1800").contains("OOPS!!!"));
        assertTrue(chris.getResponse("event backwards /from 2026-09-20 1800 /to 2026-09-20 1400")
                .contains("must end after it starts"));
        assertTrue(chris.getResponse("todo bad | separator").contains("cannot contain '|'"));
        assertTrue(chris.getResponse("deadline duplicate /by 2026-09-20 1800 /by 2026-09-21 1800")
                .contains("exactly one '/by'"));
        assertTrue(chris.getResponse("list all").contains("does not take extra words"));
        assertTrue(chris.getResponse("list").contains("1.[T][ ] read book"));
        assertTrue(!chris.getResponse("list").contains("2."));
    }

    @Test
    public void getResponse_extraSpacesAndInvalidBye_areHandled() {
        Chris chris = new Chris(temporaryDirectory.resolve("data/tasks.txt").toString());

        assertTrue(chris.getResponse("   todo read book   ").contains("[T][ ] read book"));
        assertTrue(chris.getResponse("bye now").contains("does not take extra words"));
        assertTrue(!chris.isExitRequested());
        assertTrue(chris.getResponse("  list  ").contains("1.[T][ ] read book"));
    }

    @Test
    public void getResponse_corruptSavedData_doesNotOverwriteIt() throws java.io.IOException {
        Path dataFile = temporaryDirectory.resolve("data/tasks.txt");
        java.nio.file.Files.createDirectories(dataFile.getParent());
        java.nio.file.Files.writeString(dataFile, "T | 0 | read book\ncorrupted line\n");
        Chris chris = new Chris(dataFile.toString());

        assertTrue(chris.startGui().contains("read-only"));
        assertTrue(chris.getResponse("todo new task").contains("Repair or back up"));
        assertEquals("T | 0 | read book\ncorrupted line\n", java.nio.file.Files.readString(dataFile));
    }

    @Test
    public void getResponse_saveFailure_doesNotClaimTaskWasAdded() throws java.io.IOException {
        Path blockedDirectory = temporaryDirectory.resolve("not-a-directory");
        java.nio.file.Files.writeString(blockedDirectory, "leave this file alone");
        Chris chris = new Chris(blockedDirectory.resolve("tasks.txt").toString());

        assertTrue(chris.getResponse("todo do not lose this").contains("could not save"));
        assertTrue(!chris.getResponse("list").contains("do not lose this"));
        assertEquals("leave this file alone", java.nio.file.Files.readString(blockedDirectory));
    }
}
