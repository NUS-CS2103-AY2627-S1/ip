package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import duke.ui.Response;

/**
 * Tests command outcomes and recovery through the same chatbot API used by the GUI.
 */
public class DukeTest {
    @TempDir
    public Path tempDir;

    @Test
    public void getReply_invalidCommand_reportsErrorAndAllowsRecovery() {
        Duke duke = new Duke(tempDir.resolve("tasks.txt").toString());
        Response error = duke.getReply("todo");
        assertTrue(error.isError());
        assertFalse(error.isExit());
        assertTrue(error.displayText().contains("A todo needs a description"));
        assertFalse(duke.getReply("todo read book").isError());
        assertTrue(duke.getResponse("list").contains("1.[T][ ] read book"));
    }

    @Test
    public void getReply_whitespacePaddedCommands_normalizesInputAndExit() {
        Duke duke = new Duke(tempDir.resolve("tasks.txt").toString());
        assertFalse(duke.getReply("  todo read book  ").isError());
        assertTrue(duke.getReply("  bye  ").isExit());
        assertFalse(duke.getReply("bye now").isExit());
    }

    @Test
    public void getReply_blankOrNullInput_reportsHelpfulError() {
        Duke duke = new Duke(tempDir.resolve("tasks.txt").toString());
        assertTrue(duke.getReply(" \t ").isError());
        assertEquals(duke.getReply("").text(), duke.getReply(null).text());
    }

    @Test
    public void getReply_reversedEvent_doesNotChangeSavedTasks() throws Exception {
        Path dataFile = tempDir.resolve("tasks.txt");
        Duke duke = new Duke(dataFile.toString());
        duke.getReply("todo read book");
        String savedTasks = Files.readString(dataFile);
        assertTrue(duke.getReply("event backwards /from 2019-10-16 /to 2019-10-15").isError());
        assertEquals(savedTasks, Files.readString(dataFile));
        assertFalse(duke.getResponse("list").contains("backwards"));
    }

    @Test
    public void getStartupWarning_corruptFile_reportsErrorSeparately() throws Exception {
        Path dataFile = tempDir.resolve("tasks.txt");
        Files.writeString(dataFile, "broken task\n");
        Duke duke = new Duke(dataFile.toString());
        assertTrue(duke.getStartupWarning().isError());
        assertTrue(duke.getGreeting().contains("Hello! I'm geen."));
        assertFalse(duke.getReply("list").isError());
    }

    @Test
    public void getReply_saveFailure_reportsErrorInsteadOfSuccess() throws Exception {
        Path folder = Files.createDirectory(tempDir.resolve("folder"));
        Duke duke = new Duke(folder.toString());
        Response reply = duke.getReply("todo read book");
        assertTrue(reply.isError());
        assertTrue(reply.displayText().contains("I could not save the tasks."));
        assertFalse(reply.displayText().contains("I've added this task"));
    }

    @Test
    public void displayText_separatorInDescription_isPreserved() {
        String separator = "____________________________________________________________";
        Response reply = new Response(separator + "\n" + separator + "\n" + separator, false, false);
        assertEquals(separator, reply.displayText());
        assertEquals("plain text", new Response("plain text", false, false).displayText());
    }
}
