package wodan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

/**
 * Unit tests for {@link Wodan} replies and the console command loop.
 */
public class WodanTest {
    @TempDir
    Path tempDir;

    @Test
    public void getResponse_todo_includesAcceptedQuest() {
        Wodan wodan = new Wodan(tempDir.resolve("wodan.txt").toString());
        String reply = wodan.getResponse("todo borrow book");
        assertTrue(reply.contains("borrow book"));
        assertTrue(reply.contains("accepted the following quest"));
        assertFalse(wodan.isExit());
        assertFalse(wodan.wasError());
    }

    @Test
    public void getResponse_bye_setsExit() {
        Wodan wodan = new Wodan(tempDir.resolve("wodan.txt").toString());
        String reply = wodan.getResponse("bye");
        assertEquals("So it is written. Farewell, wanderer.", reply);
        assertTrue(wodan.isExit());
        assertFalse(wodan.wasError());
    }

    @Test
    public void getResponse_duplicateTodo_setsError() {
        Wodan wodan = new Wodan(tempDir.resolve("wodan.txt").toString());
        wodan.getResponse("todo borrow book");
        String reply = wodan.getResponse("todo borrow book");
        assertTrue(wodan.wasError());
        assertTrue(reply.contains("already on the list"));
        assertFalse(wodan.isExit());
    }

    @Test
    public void getGreeting_noSaveFile_welcomeOnly() {
        Wodan wodan = new Wodan(tempDir.resolve("wodan.txt").toString());
        assertEquals(
                "Hail, wanderer. Wodan is listening.\nWhat is your command?",
                wodan.getGreeting());
    }

    @Test
    public void getLoadMessage_noSaveFile_null() {
        Wodan wodan = new Wodan(tempDir.resolve("wodan.txt").toString());
        assertNull(wodan.getLoadMessage());
    }

    @Test
    public void getResponse_unknownCommand_setsError() {
        Wodan wodan = new Wodan(tempDir.resolve("wodan.txt").toString());
        String reply = wodan.getResponse("blah");
        assertTrue(wodan.wasError());
        assertTrue(reply.contains("That rune is unknown"));
        assertFalse(wodan.isExit());
    }

    @Test
    public void getResponse_validThenInvalid_errorFlagFollowsLastReply() {
        Wodan wodan = new Wodan(tempDir.resolve("wodan.txt").toString());
        wodan.getResponse("todo borrow book");
        assertFalse(wodan.wasError());
        wodan.getResponse("not-a-command");
        assertTrue(wodan.wasError());
        wodan.getResponse("list");
        assertFalse(wodan.wasError());
    }

    @Test
    public void getResponse_listFindOnMarkDeleteTag_expectedPhrases() {
        Wodan wodan = new Wodan(tempDir.resolve("wodan.txt").toString());
        wodan.getResponse("todo borrow book");
        wodan.getResponse("deadline return book /by 2019-12-02");
        assertTrue(wodan.getResponse("list").contains("borrow book"));
        assertTrue(wodan.getResponse("find book").contains("matching tasks"));
        assertTrue(wodan.getResponse("find missing").contains("no matching quests"));
        assertTrue(wodan.getResponse("on 2019-12-02").contains("return book"));
        assertTrue(wodan.getResponse("on 2019-01-01").contains("no quests on"));
        assertTrue(wodan.getResponse("mark 1").contains("[T][X] borrow book"));
        assertTrue(wodan.getResponse("unmark 1").contains("[T][ ] borrow book"));
        assertTrue(wodan.getResponse("tag 1 school").contains("branded this quest school"));
        assertTrue(wodan.getResponse("delete 2").contains("removed this task"));
        assertFalse(wodan.wasError());
    }

    @Test
    public void getLoadMessage_corruptFile_warningThenListLoadsValidLine() throws Exception {
        Path file = tempDir.resolve("wodan.txt");
        Files.writeString(file, "garbage\nT | 0 | keep | general\n");
        Wodan wodan = new Wodan(file.toString());
        assertTrue(wodan.getLoadMessage().contains("skipped 1 corrupted quest"));
        assertTrue(wodan.getResponse("list").contains("keep"));
    }

    @Test
    public void getLoadMessage_pathIsDirectory_errorText() throws Exception {
        Path dir = tempDir.resolve("not-a-file");
        Files.createDirectory(dir);
        Wodan wodan = new Wodan(dir.toString());
        assertTrue(wodan.getLoadMessage().contains("not a file"));
        assertTrue(wodan.getResponse("list").contains("The ravens have given these quests."));
    }

    @Test
    public void getResponse_emptyAndUnknown_setsError() {
        Wodan wodan = new Wodan(tempDir.resolve("wodan.txt").toString());
        assertTrue(wodan.getResponse("   ").contains("Silence is not a command"));
        assertTrue(wodan.wasError());
    }

    @Test
    public void constructor_existingSaveFile_loadsTasks() {
        Path file = tempDir.resolve("wodan.txt");
        Wodan first = new Wodan(file.toString());
        first.getResponse("todo borrow book");
        Wodan second = new Wodan(file.toString());
        assertNull(second.getLoadMessage());
        assertTrue(second.getResponse("list").contains("borrow book"));
    }

    @Test
    @ResourceLock(Resources.SYSTEM_OUT)
    @ResourceLock("system.in")
    public void run_todoThenBye_printsGreetingAcceptedAndFarewell() {
        String output = runWithInput("todo borrow book\nbye\n", tempDir.resolve("wodan.txt"));
        assertTrue(output.contains("Hail, wanderer. Wodan is listening."));
        assertTrue(output.contains("accepted the following quest"));
        assertTrue(output.contains("[T][ ] borrow book"));
        assertTrue(output.contains("So it is written. Farewell, wanderer."));
    }

    @Test
    @ResourceLock(Resources.SYSTEM_OUT)
    @ResourceLock("system.in")
    public void run_unknownThenBye_printsError() {
        String output = runWithInput("blah\nbye\n", tempDir.resolve("wodan.txt"));
        assertTrue(output.contains("That rune is unknown"));
        assertTrue(output.contains("So it is written. Farewell, wanderer."));
    }

    @Test
    @ResourceLock(Resources.SYSTEM_OUT)
    @ResourceLock("system.in")
    public void run_emptyInput_printsWelcomeAndStops() {
        String output = runWithInput("", tempDir.resolve("wodan.txt"));
        assertTrue(output.contains("Hail, wanderer. Wodan is listening."));
        assertFalse(output.contains("Farewell"));
    }

    @Test
    @ResourceLock(Resources.SYSTEM_OUT)
    @ResourceLock("system.in")
    public void run_corruptSaveFile_showsLoadWarning() throws Exception {
        Path file = tempDir.resolve("wodan.txt");
        Files.writeString(file, "garbage\nT | 0 | keep | general\n", StandardCharsets.UTF_8);
        String output = runWithInput("list\nbye\n", file);
        assertTrue(output.contains("skipped 1 corrupted quest"));
        assertTrue(output.contains("keep"));
    }

    private String runWithInput(String input, Path saveFile) {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream captured = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        System.setIn(in);
        System.setOut(captured);
        try {
            new Wodan(saveFile.toString()).run();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
            captured.close();
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }
}
