package chatty;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests commands that require coordination between parsing, tasks, responses, and storage.
 */
public class ChattyTest {
    /** Temporary directory used for isolated command tests. */
    @TempDir
    private Path tempDirectory;

    /**
     * Verifies missing storage is accepted and corrupt storage produces a visible warning.
     *
     * @throws IOException if corrupt test data cannot be created.
     */
    @Test
    public void startupStorage_missingAndCorruptFiles_handledGracefully() throws IOException {
        Chatty missingFileChatty = new Chatty(tempDirectory.resolve("missing.txt"));
        Path corruptFile = tempDirectory.resolve("corrupt.txt");
        Files.writeString(corruptFile, "invalid record", StandardCharsets.UTF_8);
        Chatty corruptFileChatty = new Chatty(corruptFile);

        assertNull(missingFileChatty.getStartupWarning());
        assertTrue(missingFileChatty.getResponse("list").equals(" Flight plan status:"));
        assertTrue(corruptFileChatty.getStartupWarning().contains("corrupted at line 1"));
        assertTrue(corruptFileChatty.getResponse("list").equals(" Flight plan status:"));
    }

    /** Verifies command normalization, exit detection, and unknown-command handling. */
    @Test
    public void basicCommands_whitespaceAndUnknownInput_handledCorrectly() {
        Chatty chatty = new Chatty(tempDirectory.resolve("chatty.txt"));

        assertTrue(chatty.shouldExit("  bye  "));
        assertTrue(!chatty.shouldExit("bye now"));
        assertTrue(chatty.getResponse("unknown command").contains("don't recognise"));
        assertTrue(chatty.getResponse("   list   ").equals(" Flight plan status:"));
    }

    /**
     * Verifies add, find, mark, unmark, delete, and reload behavior across commands.
     */
    @Test
    public void getResponse_standardTaskCommands_mutationsPersistAcrossReload() {
        Path filePath = tempDirectory.resolve("chatty.txt");
        Chatty chatty = new Chatty(filePath);

        assertTrue(chatty.getResponse("todo read book").contains("[T][ ] read book"));
        assertTrue(chatty.getResponse("deadline submit report /by 2026-10-15")
                .contains("[D][ ] submit report"));
        assertTrue(chatty.getResponse("event meeting /from 2pm /to 4pm")
                .contains("[E][ ] meeting"));
        assertTrue(chatty.getResponse("find report").contains("submit report"));
        assertTrue(chatty.getResponse("mark 1").contains("[T][X] read book"));
        assertTrue(chatty.getResponse("unmark 1").contains("[T][ ] read book"));
        assertTrue(chatty.getResponse("delete 2").contains("submit report"));

        Chatty reloadedChatty = new Chatty(filePath);
        String reloadedList = reloadedChatty.getResponse("list");
        assertTrue(reloadedList.contains("read book"));
        assertTrue(reloadedList.contains("meeting"));
        assertTrue(!reloadedList.contains("submit report"));
    }

    /**
     * Verifies that a failed save restores the last task list held in storage.
     *
     * @throws IOException if the blocking parent file cannot be created.
     */
    @Test
    public void getResponse_saveFails_unsavedTaskRemovedFromMemory() throws IOException {
        Path blockingParent = tempDirectory.resolve("not-a-directory");
        Files.writeString(blockingParent, "blocking file", StandardCharsets.UTF_8);
        Chatty chatty = new Chatty(blockingParent.resolve("chatty.txt"));

        String addResponse = chatty.getResponse("todo read book");
        String listResponse = chatty.getResponse("list");

        assertTrue(addResponse.contains("couldn't save your tasks"));
        assertTrue(listResponse.equals(" Flight plan status:"));
    }

    /**
     * Verifies corrupt storage blocks every mutation until repaired storage is reopened.
     *
     * @throws IOException if test storage cannot be written or read.
     */
    @Test
    public void getResponse_corruptStorage_mutationsBlockedUntilRestart() throws IOException {
        Path filePath = tempDirectory.resolve("corrupt-mutations.txt");
        byte[] corruptBytes = "invalid record\n".getBytes(StandardCharsets.UTF_8);
        Files.write(filePath, corruptBytes);
        Chatty chatty = new Chatty(filePath);
        String[] commands = {
            "todo read book",
            "deadline submit report /by 2026-10-15",
            "event meeting /from 2pm /to 4pm",
            "recurring project meeting /on 2026-09-14 /every 1 week",
            "mark 1",
            "unmark 1",
            "delete 1"
        };

        for (String command : commands) {
            assertTrue(chatty.getResponse(command).contains("OOPS!!!"), command);
            assertArrayEquals(corruptBytes, Files.readAllBytes(filePath), command);
            assertEquals(" Flight plan status:", chatty.getResponse("list"), command);
        }

        Files.writeString(filePath, "", StandardCharsets.UTF_8);
        assertTrue(chatty.getResponse("todo still blocked").contains("OOPS!!!"));
        assertEquals("", Files.readString(filePath, StandardCharsets.UTF_8));
        assertEquals(" Flight plan status:", chatty.getResponse("list"));

        Chatty restartedChatty = new Chatty(filePath);
        assertNull(restartedChatty.getStartupWarning());
        assertTrue(restartedChatty.getResponse("todo recovered").contains("[T][ ] recovered"));
        assertEquals(" Flight plan status:\n 1.[T][ ] recovered",
                new Chatty(filePath).getResponse("list"));
    }

    /** Verifies find retains full-list indices for separated, identically described tasks. */
    @Test
    public void getResponse_findOriginalIndices_mutationsTargetCorrectTasksAcrossReload() {
        Path filePath = tempDirectory.resolve("find-indices.txt");
        Chatty chatty = new Chatty(filePath);
        chatty.getResponse("todo unrelated first");
        chatty.getResponse("todo matching task");
        chatty.getResponse("todo unrelated middle");
        chatty.getResponse("todo matching task");

        assertEquals(" Radar found these matching tasks:\n 2.[T][ ] matching task"
                + "\n 4.[T][ ] matching task", chatty.getResponse("find matching"));
        assertTrue(chatty.getResponse("mark 4").contains("[T][X] matching task"));
        assertEquals(" Radar found these matching tasks:\n 2.[T][ ] matching task"
                + "\n 4.[T][X] matching task", new Chatty(filePath).getResponse("find matching"));

        assertTrue(chatty.getResponse("unmark 4").contains("[T][ ] matching task"));
        assertEquals(" Radar found these matching tasks:\n 2.[T][ ] matching task"
                + "\n 4.[T][ ] matching task", new Chatty(filePath).getResponse("find matching"));
        assertTrue(chatty.getResponse("delete 2").contains("[T][ ] matching task"));

        String expectedList = " Flight plan status:\n 1.[T][ ] unrelated first"
                + "\n 2.[T][ ] unrelated middle\n 3.[T][ ] matching task";
        assertEquals(expectedList, chatty.getResponse("list"));
        Chatty reloadedChatty = new Chatty(filePath);
        assertEquals(expectedList, reloadedChatty.getResponse("list"));
        assertEquals(" Radar found these matching tasks:\n 3.[T][ ] matching task",
                reloadedChatty.getResponse("find matching"));
    }

    /** Verifies the recurring-task add, mark, list, and unmark command flow. */
    @Test
    public void getResponse_recurringTaskCommands_scheduleManagedAcrossCommands() {
        Chatty chatty = new Chatty(tempDirectory.resolve("chatty.txt"));

        String addedResponse = chatty.getResponse(
                "recurring project meeting /on 2026-09-14 /every 1 week");
        String markedResponse = chatty.getResponse("mark 1");
        String listResponse = chatty.getResponse("list");
        String unmarkResponse = chatty.getResponse("unmark 1");

        assertTrue(addedResponse.contains(
                "[R][ ] project meeting (on: Sep 14 2026, every: 1 week)"));
        assertTrue(markedResponse.contains("Recurring milestone cleared"));
        assertTrue(markedResponse.contains("on: Sep 21 2026"));
        assertTrue(listResponse.contains("on: Sep 21 2026"));
        assertTrue(unmarkResponse.contains("cannot be unmarked"));
    }
}
