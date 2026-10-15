package sine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests command responses exposed to the graphical user interface.
 */
public class SineTest {
    @TempDir
    private Path tempDir;

    /** Tests that GUI commands share state and save changes. */
    @Test
    public void getResponse_addThenList_returnsSavedTask() throws IOException {
        Path dataFile = tempDir.resolve("data/sine.txt");
        Sine sine = new Sine(dataFile.toString());

        String addResponse = sine.getResponse("todo borrow book");
        String listResponse = sine.getResponse("list");

        assertTrue(addResponse.contains("[T][ ] borrow book"));
        assertTrue(listResponse.contains("1.[T][ ] borrow book"));
        assertEquals("T | 0 | borrow book", Files.readString(dataFile).strip());
    }

    /** Tests that validation failures become GUI response text. */
    @Test
    public void getResponse_invalidCommand_returnsErrorMessage() {
        Sine sine = new Sine(tempDir.resolve("data/sine.txt").toString());

        String response = sine.getResponse("todo");

        assertEquals("Error :( The description of a todo cannot be empty.", response);
    }

    /** Tests that failed loading still initializes usable state for subsequent commands. */
    @Test
    public void getResponse_malformedSavedData_recoversAndAcceptsCommands() throws IOException {
        Path dataFile = tempDir.resolve("sine.txt");
        Files.writeString(dataFile, "D | 0 | return book | 2026-02-30");
        Sine sine = new Sine(dataFile.toString());

        String firstResponse = sine.getResponse("list");
        String addResponse = sine.getResponse("todo borrow book");

        assertTrue(firstResponse.contains("Starting with an empty list."));
        assertTrue(addResponse.contains("[T][ ] borrow book"));
        assertEquals("T | 0 | borrow book", Files.readString(dataFile).strip());
    }

    /** Tests reverse-order restoration, including a completed deleted task. */
    @Test
    public void getResponse_addMarkDeleteUndo_restoresEachState() {
        Sine sine = new Sine(tempDir.resolve("tasks.txt").toString());
        sine.getResponse("deadline book /by 2026-08-30");
        sine.getResponse("mark 1");
        sine.getResponse("delete 1");

        assertEquals("Understood. I undid the last task change.\nI added back this task:"
                + "\n  [D][X] book (by: Aug 30 2026)", sine.getResponse("undo"));
        assertEquals("Understood. I undid the last task change.\nI unmarked this task:"
                + "\n  [D][ ] book (by: Aug 30 2026)", sine.getResponse("UNDO"));
        assertEquals("Understood. I undid the last task change.\nI removed this task:"
                + "\n  [D][ ] book (by: Aug 30 2026)", sine.getResponse("undo"));
        assertEquals("Error :( There is nothing to undo.", sine.getResponse("undo"));
    }

    /** Tests that irrelevant commands do not hide the last actual change. */
    @Test
    public void getResponse_noOpsAndInvalidCommands_preserveHistory() {
        Sine sine = new Sine(tempDir.resolve("tasks.txt").toString());
        sine.getResponse("todo book");
        sine.getResponse("mark 1");
        sine.getResponse("mark 1");
        sine.getResponse("list");
        sine.getResponse("find book");
        sine.getResponse("todo");
        sine.getResponse("unknown");
        sine.getResponse("undo 2");

        assertTrue(sine.getResponse("undo").contains("I unmarked this task:"));
        sine.getResponse("unmark 1");
        assertTrue(sine.getResponse("undo").contains("I removed this task:"));
    }

    /** Tests a new change after undo and persistence without history across restarts. */
    @Test
    public void getResponse_newChangeAfterUndo_preservesOlderHistory() throws IOException {
        Path dataFile = tempDir.resolve("tasks.txt");
        Sine sine = new Sine(dataFile.toString());
        sine.getResponse("todo first");
        sine.getResponse("todo second");
        sine.getResponse("undo");
        sine.getResponse("event meeting /from Mon /to Tue");
        assertTrue(sine.getResponse("undo").contains("[E][ ] meeting (from: Mon to Tue)"));
        assertEquals("T | 0 | first", Files.readString(dataFile).strip());

        Sine restarted = new Sine(dataFile.toString());
        assertEquals("Error :( There is nothing to undo.", restarted.getResponse("undo"));
        assertTrue(restarted.getResponse("list").contains("[T][ ] first"));
        assertTrue(sine.getResponse("undo").contains("[T][ ] first"));
    }

    /** Tests that save failures retain memory changes and consume undo history once. */
    @Test
    public void getResponse_savingFails_undoStillReversesMemoryChange() throws IOException {
        Path dataFile = tempDir.resolve("tasks.txt");
        Sine sine = new Sine(dataFile.toString());
        sine.getResponse("list");
        // A directory at the file path makes saving fail without relying on OS permissions.
        Files.createDirectory(dataFile);
        assertTrue(sine.getResponse("todo book").contains("I couldn't save that change."));

        assertEquals("Understood. I undid the last task change.\nI removed this task:"
                + "\n  [T][ ] book\n Error :( I couldn't save that change."
                + " It is available only for this session.", sine.getResponse("undo"));
        assertEquals("Error :( There is nothing to undo.", sine.getResponse("undo"));
        assertEquals("TODO list:", sine.getResponse("list"));
    }
    /** Tests that rejected parameters leave saved tasks and undo history unchanged. */
    @Test
    public void getResponse_repeatedParameter_preservesStateAndHistory() throws IOException {
        Path dataFile = tempDir.resolve("tasks.txt");
        Sine sine = new Sine(dataFile.toString());
        sine.getResponse("todo book");
        String savedData = Files.readString(dataFile);

        assertEquals("Error :( Specify /to only once.",
                sine.getResponse("event meeting /from 2pm /to 4pm /to 5pm"));
        assertEquals(savedData, Files.readString(dataFile));
        assertEquals("TODO list:\n 1.[T][ ] book", sine.getResponse("list"));
        assertTrue(sine.getResponse("undo").contains("I removed this task:"));
    }

}
