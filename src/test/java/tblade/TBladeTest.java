package tblade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests TBlade's command dispatch end-to-end (getResponse()), including persistence across
 * instances, error handling, and the isRunning()/bye lifecycle. GUI wiring (Main, MainWindow,
 * DialogBox) is excluded per the course's guidance to test that manually instead.
 */
public class TBladeTest {
    @TempDir
    private Path tempDir;

    private String dataFilePath() {
        return tempDir.resolve("duke.txt").toString();
    }

    @Test
    public void constructor_noExistingFile_startsWithEmptyList() {
        TBlade tblade = new TBlade(dataFilePath());

        assertEquals("Here's your kill list:", tblade.getResponse("list"));
    }

    @Test
    public void constructor_dataFileUnreadable_showsErrorAndStartsWithEmptyList() throws IOException {
        Path unreadableAsAFile = tempDir.resolve("not-a-file");
        Files.createDirectory(unreadableAsAFile);
        PrintStream originalOut = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput));

        TBlade tblade;
        try {
            tblade = new TBlade(unreadableAsAFile.toString());
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(capturedOutput.toString().contains("Chat, is this real?"));
        assertEquals("Here's your kill list:", tblade.getResponse("list"));
    }

    @Test
    public void getResponse_blankCommand_returnsErrorAskingForACommand() {
        TBlade tblade = new TBlade(dataFilePath());

        assertEquals("Chat, is this real? Please enter a command. "
                + "Use: todo, deadline, event, list, find, mark, unmark, delete, or bye.",
                tblade.getResponse("   "));
    }

    @Test
    public void getResponse_unknownCommand_returnsError() {
        TBlade tblade = new TBlade(dataFilePath());

        assertEquals("Chat, is this real? I don't know that command. "
                + "Use: todo, deadline, event, list, find, mark, unmark, delete, or bye.",
                tblade.getResponse("frobnicate"));
    }

    @Test
    public void getResponse_bye_returnsGoodbyeAndStopsRunning() {
        TBlade tblade = new TBlade(dataFilePath());
        assertTrue(tblade.isRunning());

        String response = tblade.getResponse("bye");

        assertEquals("gg. Technoblade never dies — I'll be right here when you're back.", response);
        assertFalse(tblade.isRunning());
    }

    @Test
    public void getResponse_byeWithExtraText_throwsAndKeepsRunning() {
        TBlade tblade = new TBlade(dataFilePath());

        String response = tblade.getResponse("bye now");

        assertEquals("Chat, is this real? The bye command does not take extra text. Use: bye", response);
        assertTrue(tblade.isRunning());
    }

    @Test
    public void getResponse_listWithExtraText_returnsError() {
        TBlade tblade = new TBlade(dataFilePath());

        assertEquals("Chat, is this real? The list command does not take extra text. Use: list",
                tblade.getResponse("list please"));
    }

    @Test
    public void getResponse_commandWithLeadingOrTrailingWhitespace_isStillRecognized() {
        TBlade tblade = new TBlade(dataFilePath());

        String response = tblade.getResponse("  todo read book  ");

        assertEquals("Added to the kill list:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 tasks on the kill list.", response);
    }

    @Test
    public void getResponse_byeWithOnlyTrailingWhitespace_isTreatedAsPlainBye() {
        TBlade tblade = new TBlade(dataFilePath());

        String response = tblade.getResponse("bye   ");

        assertEquals("gg. Technoblade never dies — I'll be right here when you're back.", response);
        assertFalse(tblade.isRunning());
    }

    @Test
    public void getResponse_addTodo_returnsConfirmationAndPersistsAcrossInstances() {
        TBlade tblade = new TBlade(dataFilePath());

        String response = tblade.getResponse("todo read book");

        assertEquals("Added to the kill list:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 tasks on the kill list.", response);
        TBlade reloaded = new TBlade(dataFilePath());
        assertEquals("Here's your kill list:\n1.[T][ ] read book", reloaded.getResponse("list"));
    }

    @Test
    public void getResponse_addDeadline_returnsConfirmationWithFormattedDate() {
        TBlade tblade = new TBlade(dataFilePath());

        String response = tblade.getResponse("deadline return book /by 2026-03-15");

        assertEquals("Added to the kill list:\n"
                + "  [D][ ] return book (by: Mar 15 2026)\n"
                + "Now you have 1 tasks on the kill list.", response);
    }

    @Test
    public void getResponse_addEvent_returnsConfirmation() {
        TBlade tblade = new TBlade(dataFilePath());

        String response = tblade.getResponse("event meeting /from Mon 2pm /to 4pm");

        assertEquals("Added to the kill list:\n"
                + "  [E][ ] meeting (from: Mon 2pm to: 4pm)\n"
                + "Now you have 1 tasks on the kill list.", response);
    }

    @Test
    public void getResponse_addDuplicateTask_returnsErrorAndDoesNotDuplicate() {
        TBlade tblade = new TBlade(dataFilePath());
        tblade.getResponse("todo read book");

        String response = tblade.getResponse("todo read book");

        assertTrue(response.startsWith("Chat, is this real? This task is already in your list"));
        assertEquals("Here's your kill list:\n1.[T][ ] read book", tblade.getResponse("list"));
    }

    @Test
    public void getResponse_mark_marksTaskAndPersistsAcrossInstances() {
        TBlade tblade = new TBlade(dataFilePath());
        tblade.getResponse("todo read book");

        String response = tblade.getResponse("mark 1");

        assertEquals("GG! Task conquered:\n  [X] read book", response);
        TBlade reloaded = new TBlade(dataFilePath());
        assertEquals("Here's your kill list:\n1.[T][X] read book", reloaded.getResponse("list"));
    }

    @Test
    public void getResponse_unmark_unmarksTask() {
        TBlade tblade = new TBlade(dataFilePath());
        tblade.getResponse("todo read book");
        tblade.getResponse("mark 1");

        String response = tblade.getResponse("unmark 1");

        assertEquals("Back in the fight — not done yet:\n  [ ] read book", response);
    }

    @Test
    public void getResponse_delete_removesTaskAndPersistsAcrossInstances() {
        TBlade tblade = new TBlade(dataFilePath());
        tblade.getResponse("todo read book");
        tblade.getResponse("todo return laptop");

        String response = tblade.getResponse("delete 1");

        assertEquals("Wiped from the kill list:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 tasks on the kill list.", response);
        TBlade reloaded = new TBlade(dataFilePath());
        assertEquals("Here's your kill list:\n1.[T][ ] return laptop", reloaded.getResponse("list"));
    }

    @Test
    public void getResponse_find_returnsOnlyMatchingTasks() {
        TBlade tblade = new TBlade(dataFilePath());
        tblade.getResponse("todo read book");
        tblade.getResponse("todo return laptop");

        String response = tblade.getResponse("find book");

        assertEquals("Matches found on the kill list:\n1.[T][ ] read book", response);
    }

    @Test
    public void getResponse_taskIndexCommandOnEmptyList_returnsError() {
        TBlade tblade = new TBlade(dataFilePath());

        assertEquals("Chat, is this real? There are no tasks to mark. Add a task first.",
                tblade.getResponse("mark 1"));
    }

    @Test
    public void run_readsCommandsFromStandardInputUntilBye() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        System.setIn(new ByteArrayInputStream("todo read book\nbye\n".getBytes()));
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput));

        TBlade tblade;
        try {
            tblade = new TBlade(dataFilePath());
            tblade.run();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String output = capturedOutput.toString();
        assertTrue(output.contains("What's the move?"));
        assertTrue(output.contains("Added to the kill list:"));
        assertTrue(output.contains("gg. Technoblade never dies"));
        assertFalse(tblade.isRunning());
    }
}
