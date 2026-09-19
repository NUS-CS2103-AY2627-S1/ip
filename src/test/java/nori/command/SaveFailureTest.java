package nori.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import nori.Nori;
import nori.testutil.IsolatedSessionTest;
import nori.ui.GuiUi;

/**
 * Tests that a command undoes its change to the task list when the save fails.
 *
 * Every command that changes a task saves immediately and puts the list back as
 * it was if the save does not succeed. Otherwise the list on screen and the file
 * on disk disagree, and the user is told a task was added, completed or deleted
 * when the next launch will show otherwise.
 *
 * Saving is made to fail by replacing the task file with a directory that a
 * finished temporary file cannot be moved onto. That is the one sabotage that
 * behaves the same on every platform: file permissions do not, and neither does
 * making a directory read-only.
 */
public class SaveFailureTest extends IsolatedSessionTest {
    private static final String STORAGE_FILE = "nori.txt";
    private static final String SAVE_FAILURE_MESSAGE =
            "SAD NOOT! I couldn't write your tasks onto the ice.";

    @Test
    public void addTask_saveFails_reportsItAndKeepsTheTaskOut() throws IOException {
        GuiUi guiUi = new GuiUi();
        Nori nori = startSessionHolding(guiUi, "todo read book");
        blockSaving();

        nori.executeCommand("todo write essay");

        assertEquals(SAVE_FAILURE_MESSAGE, guiUi.consumeResponse());
        assertTrue(guiUi.isErrorResponse());
        assertEquals("Noot noot! Tasks currently chilling on the iceberg:\n1.[T][ ] read book",
                listTasks(nori, guiUi));
    }

    @Test
    public void mark_saveFails_reportsItAndLeavesTheTaskOpen() throws IOException {
        GuiUi guiUi = new GuiUi();
        Nori nori = startSessionHolding(guiUi, "todo read book");
        blockSaving();

        nori.executeCommand("mark 1");

        assertEquals(SAVE_FAILURE_MESSAGE, guiUi.consumeResponse());
        assertEquals("Noot noot! Tasks currently chilling on the iceberg:\n1.[T][ ] read book",
                listTasks(nori, guiUi));
    }

    @Test
    public void unmark_saveFails_reportsItAndLeavesTheTaskDone() throws IOException {
        GuiUi guiUi = new GuiUi();
        Nori nori = startSessionHolding(guiUi, "todo read book", "mark 1");
        blockSaving();

        nori.executeCommand("unmark 1");

        assertEquals(SAVE_FAILURE_MESSAGE, guiUi.consumeResponse());
        assertEquals("Noot noot! Tasks currently chilling on the iceberg:\n1.[T][X] read book",
                listTasks(nori, guiUi));
    }

    @Test
    public void delete_saveFails_reportsItAndPutsTheTaskBackWhereItWas() throws IOException {
        GuiUi guiUi = new GuiUi();
        Nori nori = startSessionHolding(guiUi, "todo first", "todo second", "todo third");
        blockSaving();

        nori.executeCommand("delete 2");

        assertEquals(SAVE_FAILURE_MESSAGE, guiUi.consumeResponse());
        assertEquals("Noot noot! Tasks currently chilling on the iceberg:\n"
                + "1.[T][ ] first\n"
                + "2.[T][ ] second\n"
                + "3.[T][ ] third", listTasks(nori, guiUi));
    }

    @Test
    public void addTask_saveFails_leavesTheNextSessionUnchanged() throws IOException {
        GuiUi guiUi = new GuiUi();
        Nori nori = startSessionHolding(guiUi, "todo read book");
        blockSaving();
        nori.executeCommand("todo write essay");

        allowSaving();

        assertEquals("Noot noot! Tasks currently chilling on the iceberg:\n1.[T][ ] read book",
                runCommands("list"));
    }

    /**
     * Starts a session and runs commands in it while saving still works.
     *
     * @param guiUi the interface the session answers through.
     * @param inputs the commands to run first.
     * @return the running session.
     */
    private static Nori startSessionHolding(GuiUi guiUi, String... inputs) {
        Nori nori = new Nori(guiUi);
        for (String input : inputs) {
            nori.executeCommand(input);
            guiUi.consumeResponse();
        }
        return nori;
    }

    /**
     * Lists the tasks the running session holds, without starting a new one.
     *
     * @param nori the running session.
     * @param guiUi the interface the session answers through.
     * @return the listing response.
     */
    private static String listTasks(Nori nori, GuiUi guiUi) {
        nori.executeCommand("list");
        return guiUi.consumeResponse();
    }

    /**
     * Makes every later save fail, by putting a directory where the task file goes.
     *
     * @throws IOException if the sabotage cannot be set up.
     */
    private void blockSaving() throws IOException {
        Path taskFile = storageDirectory.resolve(STORAGE_FILE);
        Files.deleteIfExists(taskFile);
        Files.createDirectory(taskFile);
        Files.writeString(taskFile.resolve("blocker"), "x", StandardCharsets.UTF_8);
    }

    /**
     * Undoes the sabotage, restoring the task file from the backup written before it.
     *
     * @throws IOException if the task file cannot be restored.
     */
    private void allowSaving() throws IOException {
        Path taskFile = storageDirectory.resolve(STORAGE_FILE);
        Files.deleteIfExists(taskFile.resolve("blocker"));
        Files.deleteIfExists(taskFile);
        Files.copy(storageDirectory.resolve(STORAGE_FILE + ".bak"), taskFile);
    }
}
