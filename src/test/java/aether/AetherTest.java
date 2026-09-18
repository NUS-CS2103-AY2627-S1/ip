package aether;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AetherTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponseProcessesCommandsAndKeepsWorkingAfterInvalidInput() {
        Aether aether = new Aether(temporaryDirectory.resolve("aether.txt"));

        assertTrue(aether.getWelcomeMessage().contains("I'm Aether, your calm guide"));
        assertTrue(aether.getResponse("todo read book").contains("[T][ ] read book"));
        assertTrue(aether.getResponse("todo").contains("description of a todo cannot be empty"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book", aether.getResponse("list"));
        assertEquals("Until next time. May your day stay clear and focused.", aether.getResponse("bye"));
    }

    @Test
    void failedLoadPreventsChangesFromOverwritingSavedData() throws IOException {
        Path dataFile = temporaryDirectory.resolve("aether.txt");
        String originalData = "T | 0 | cmVhZCBib29r\ncorrupted record\n";
        Files.writeString(dataFile, originalData, StandardCharsets.UTF_8);
        Aether aether = new Aether(dataFile);

        assertTrue(aether.getResponse("todo new task").contains(
                "cannot save changes because the saved tasks could not be loaded"));
        assertEquals("Here are the tasks in your list:", aether.getResponse("list"));
        assertEquals(originalData, Files.readString(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void duplicateTaskIsRejectedWithoutChangingSavedTasks() {
        Path dataFile = temporaryDirectory.resolve("aether.txt");
        Aether aether = new Aether(dataFile);

        aether.getResponse("deadline Submit report /by 2026-09-20");

        assertTrue(aether.getResponse("deadline submit report /by 2026-09-20").contains("already in your list"));
        assertEquals("Here are the tasks in your list:\n"
                + "1.[D][ ] Submit report (by: Sep 20 2026)", aether.getResponse("list"));
    }

    @Test
    void commandResultDistinguishesSuccessfulAndInvalidCommands() {
        Aether aether = new Aether(temporaryDirectory.resolve("aether.txt"));

        Aether.CommandResult success = aether.getCommandResult("list");
        Aether.CommandResult error = aether.getCommandResult("unknown");

        assertEquals("Here are the tasks in your list:", success.getMessage());
        assertFalse(success.isError());
        assertTrue(error.getMessage().contains("don't recognise"));
        assertTrue(error.isError());
    }

    @Test
    void taskCommandsAddUpdateFindAndDeleteTasks() {
        Aether aether = new Aether(temporaryDirectory.resolve("aether.txt"));

        assertTrue(aether.getResponse("todo read book").contains("[T][ ] read book"));
        assertTrue(aether.getResponse("deadline submit /by 2026-09-20").contains("[D][ ] submit"));
        assertTrue(aether.getResponse("event camp /from 2026-09-21 /to 2026-09-22").contains("[E][ ] camp"));
        assertTrue(aether.getResponse("mark 2").contains("[D][X] submit"));
        assertTrue(aether.getResponse("unmark 2").contains("[D][ ] submit"));
        assertTrue(aether.getResponse("find CAMP").contains("3.[E][ ] camp"));
        assertTrue(aether.getResponse("delete 1").contains("[T][ ] read book"));
        assertTrue(aether.getResponse("list").contains("1.[D][ ] submit"));
    }

    @Test
    void failedSaveRollsBackAddMarkDeleteAndSort() throws IOException {
        assertAddRollsBackWhenSaveFails(temporaryDirectory.resolve("add.txt"));
        assertMarkRollsBackWhenSaveFails(temporaryDirectory.resolve("mark.txt"));
        assertDeleteRollsBackWhenSaveFails(temporaryDirectory.resolve("delete.txt"));
        assertSortRollsBackWhenSaveFails(temporaryDirectory.resolve("sort.txt"));
    }

    @Test
    void runProcessesCommandsReportsErrorsAndStopsAtBye() {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("todo\ntodo read book\nbye\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
            Aether aether = new Aether(temporaryDirectory.resolve("aether.txt"));

            aether.run();

            String output = capturedOutput.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("description of a todo cannot be empty"));
            assertTrue(output.contains("[T][ ] read book"));
            assertTrue(output.contains("Until next time"));
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }

    @Test
    void sortOrdersTasksByDateAndSavesTheNewOrder() {
        Path dataFile = temporaryDirectory.resolve("aether.txt");
        Aether aether = new Aether(dataFile);
        aether.getResponse("todo buy milk");
        aether.getResponse("deadline submit report /by 2026-09-12");
        aether.getResponse("event workshop /from 2026-09-05 /to 2026-09-06");
        aether.getResponse("deadline renew pass /by 2026-09-05");

        assertEquals("Your schedule is aligned by date.\n"
                + "Here are the tasks in your list:\n"
                + "1.[E][ ] workshop (from: Sep 05 2026 to: Sep 06 2026)\n"
                + "2.[D][ ] renew pass (by: Sep 05 2026)\n"
                + "3.[D][ ] submit report (by: Sep 12 2026)\n"
                + "4.[T][ ] buy milk", aether.getResponse("sort"));

        Aether reloadedAether = new Aether(dataFile);
        assertEquals("Here are the tasks in your list:\n"
                + "1.[E][ ] workshop (from: Sep 05 2026 to: Sep 06 2026)\n"
                + "2.[D][ ] renew pass (by: Sep 05 2026)\n"
                + "3.[D][ ] submit report (by: Sep 12 2026)\n"
                + "4.[T][ ] buy milk", reloadedAether.getResponse("list"));
    }

    /** Confirms an added task is removed again when its save fails. */
    private void assertAddRollsBackWhenSaveFails(Path dataFile) throws IOException {
        Aether aether = new Aether(dataFile);
        Files.createDirectory(dataFile);

        assertTrue(aether.getResponse("todo read book").contains("could not save"));
        assertEquals("Here are the tasks in your list:", aether.getResponse("list"));
    }

    /** Confirms a status change is undone when its save fails. */
    private void assertMarkRollsBackWhenSaveFails(Path dataFile) throws IOException {
        Aether aether = new Aether(dataFile);
        aether.getResponse("todo read book");
        replaceFileWithDirectory(dataFile);

        assertTrue(aether.getResponse("mark 1").contains("could not save"));
        assertTrue(aether.getResponse("list").contains("1.[T][ ] read book"));
    }

    /** Confirms a deletion is undone when its save fails. */
    private void assertDeleteRollsBackWhenSaveFails(Path dataFile) throws IOException {
        Aether aether = new Aether(dataFile);
        aether.getResponse("todo read book");
        replaceFileWithDirectory(dataFile);

        assertTrue(aether.getResponse("delete 1").contains("could not save"));
        assertTrue(aether.getResponse("list").contains("1.[T][ ] read book"));
    }

    /** Confirms sorting is undone when its save fails. */
    private void assertSortRollsBackWhenSaveFails(Path dataFile) throws IOException {
        Aether aether = new Aether(dataFile);
        aether.getResponse("todo read book");
        aether.getResponse("deadline submit /by 2026-09-20");
        replaceFileWithDirectory(dataFile);

        assertTrue(aether.getResponse("sort").contains("could not save"));
        assertTrue(aether.getResponse("list").contains("1.[T][ ] read book\n2.[D][ ] submit"));
    }

    /** Replaces a temporary data file with a directory so subsequent saves fail safely. */
    private void replaceFileWithDirectory(Path dataFile) throws IOException {
        Files.delete(dataFile);
        Files.createDirectory(dataFile);
    }
}
