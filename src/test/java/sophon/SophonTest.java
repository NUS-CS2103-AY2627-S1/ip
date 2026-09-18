package sophon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SophonTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_addCommands_returnsAddResponses() {
        Sophon sophon = new Sophon(temporaryDirectory.toString(), "tasks.txt");

        assertTrue(sophon.getResponse("todo read book")
                .startsWith("Recorded. A new task has entered observation:"));
        assertTrue(sophon.getResponse("deadline return book /by 2019-10-15")
                .startsWith("Recorded. A new deadline has entered observation:"));
        assertTrue(sophon.getResponse("event meeting /from 2019-10-15 /to 2019-10-16")
                .startsWith("Recorded. A new event has entered observation:"));
    }

    @Test
    public void getResponse_stateChangingCommands_updatesListAndFile() throws IOException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Sophon sophon = new Sophon(saveFile.toString());

        sophon.getResponse("todo read book");
        assertTrue(sophon.getResponse("mark 1").contains("[T][X] read book"));
        assertTrue(sophon.getResponse("unmark 1").contains("[T][ ] read book"));
        assertTrue(sophon.getResponse("find BOOK").contains("[T][ ] read book"));
        assertTrue(sophon.getResponse("list").contains("1.[T][ ] read book"));
        assertTrue(sophon.getResponse("delete 1").startsWith("Removed."));

        assertEquals(0, Files.readAllLines(saveFile, StandardCharsets.UTF_8).size());
        assertEquals("Current tasks under observation:\n", sophon.getResponse("list"));
    }

    @Test
    public void getResponse_invalidAndUnknownCommands_returnsHelpfulMessages() {
        Sophon sophon = new Sophon(temporaryDirectory.toString(), "tasks.txt");

        assertEquals("No task exists at that number.", sophon.getResponse("mark 1"));
        assertEquals("No task exists at that number.", sophon.getResponse("unmark 1"));
        assertEquals("No task exists at that number.", sophon.getResponse("delete 1"));
        assertEquals("Your message has been observed.\nIts meaning, however, remains unknown.",
                sophon.getResponse("hello"));
        assertEquals("Our conversation ends here.\nUntil we meet again.", sophon.getResponse("bye"));
    }

    @Test
    public void constructor_invalidSaveFile_exposesWarningAndStartsEmpty() throws IOException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(saveFile, "X | 0 | mystery", StandardCharsets.UTF_8);

        Sophon sophon = new Sophon(saveFile.toString());

        assertTrue(sophon.getGreeting().endsWith("The save file contains an unknown task type."));
        assertEquals("Current tasks under observation:\n", sophon.getResponse("list"));
    }

    @Test
    public void getResponse_unwritableSaveLocation_returnsSaveFailure() throws IOException {
        Path fileUsedAsDirectory = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(fileUsedAsDirectory, "blocking file", StandardCharsets.UTF_8);
        Sophon sophon = new Sophon(fileUsedAsDirectory.toString(), "tasks.txt");

        assertEquals("I could not save the task list.", sophon.getResponse("todo read book"));
    }
}
