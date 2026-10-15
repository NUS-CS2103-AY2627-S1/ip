package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Verifies failed commands preserve task state, saved records, and the previous undo history. */
class SevenSixErrorTest {
    /** Keeps all data and blocked-save fixtures in the test directory. */
    @TempDir
    private Path temporaryDirectory;

    /** Duplicate rejection must preserve both completion status and the last valid undo. */
    @Test
    void getResponseDuplicateTaskPreservesStateAndUndo() {
        SevenSix chatbot = new SevenSix(temporaryDirectory.resolve("tasks.txt"));
        chatbot.getResponse("todo read book");
        chatbot.getResponse("mark 1");

        assertEquals(Ui.formatError(Ui.DUPLICATE_TASK), chatbot.getResponse("todo  read\tbook"));
        assertEquals("Here is your pipeline:" + System.lineSeparator() + "1.[T][X] read book",
                chatbot.getResponse("list"));
        assertEquals("Undid mark as done: [T][ ] read book", chatbot.getResponse("undo"));
        assertEquals("Here is your pipeline:" + System.lineSeparator() + "1.[T][ ] read book",
                chatbot.getResponse("list"));
    }

    /** A failed add, delete, mark, unmark, or undo must retain all previous state. */
    @Test
    void getResponseSaveFailureRollsBackEveryTaskChangingCommand() throws IOException {
        for (String command : List.of("todo second", "deadline second /by 2024-02-29",
                "event second /from 2024-01-01 /to 2024-01-02", "mark 1", "unmark 1", "delete 1", "undo")) {
            verifyFailedChange(command);
        }
    }

    /** Blocks a save deterministically, checks rollback, then checks the previous undo still works.
     *
     * @param command the task-changing command to attempt while saving is blocked.
     * @throws IOException if the isolated test fixture cannot be created or restored.
     */
    private void verifyFailedChange(String command) throws IOException {
        Path directory = Files.createTempDirectory(temporaryDirectory, "rollback-");
        Path file = directory.resolve("tasks.txt");
        Path backup = directory.resolve("original.txt");
        SevenSix chatbot = new SevenSix(file);
        chatbot.getResponse("todo first");
        chatbot.getResponse("mark 1");
        String original = Files.readString(file);
        Files.move(file, backup);
        Files.createDirectory(file);

        assertEquals(Ui.formatError(Ui.SAVE_FAILURE), chatbot.getResponse(command), command);
        assertEquals("Here is your pipeline:" + System.lineSeparator() + "1.[T][X] first",
                chatbot.getResponse("list"), command);
        assertEquals(original, Files.readString(backup));

        Files.delete(file);
        Files.move(backup, file);
        assertEquals("Undid mark as done: [T][ ] first", chatbot.getResponse("undo"), command);
        assertEquals("Here is your pipeline:" + System.lineSeparator() + "1.[T][ ] first",
                new SevenSix(file).getResponse("list"), command);
        assertEquals("Undid addition: [T][ ] first", chatbot.getResponse("undo"), command);
        assertEquals(Ui.getEmptyListMessage(), new SevenSix(file).getResponse("list"), command);
        assertEquals(Ui.formatError(Ui.EMPTY_UNDO_HISTORY), chatbot.getResponse("undo"), command);
    }

    /** Startup failures should be exposed to both interfaces and prevent apparent successful adds. */
    @Test
    void getResponseUnreadableStorageBlocksChanges() {
        SevenSix chatbot = new SevenSix(temporaryDirectory);

        assertEquals(Ui.LOAD_FAILURE, chatbot.getStartupWarning());
        assertEquals(Ui.formatError(Ui.SAVE_FAILURE), chatbot.getResponse("todo new"));
        assertEquals(Ui.getEmptyListMessage(), chatbot.getResponse("list"));
        assertTrue(Ui.isErrorResponse(chatbot.getResponse("undo")));
    }

    /** Bad input must not write the file or replace the previous undo opportunity. */
    @Test
    void getResponseInvalidInputPreservesFileAndUndo() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        SevenSix chatbot = new SevenSix(file);
        chatbot.getResponse("todo first");
        String original = Files.readString(file);

        for (String command : List.of("mark 0", "delete 99", "unmark -1", "list extra", "bye now", "",
                "event meeting /from 2024-01-02 /to 2024-01-01")) {
            assertTrue(Ui.isErrorResponse(chatbot.getResponse(command)), command);
            assertEquals(original, Files.readString(file), command);
        }
        assertEquals("Undid addition: [T][ ] first", chatbot.getResponse("undo"));
        assertEquals(Ui.getEmptyListMessage(), chatbot.getResponse("list"));
    }

    /** Escaped punctuation and Unicode descriptions should survive saving and loading. */
    @Test
    void getResponseSpecialDescriptionCharactersRoundTrip() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        SevenSix chatbot = new SevenSix(file);
        chatbot.getResponse("  todo\tread | book\\ 日本語  ");

        assertEquals("Here is your pipeline:" + System.lineSeparator() + "1.[T][ ] read | book\\ 日本語",
                new SevenSix(file).getResponse("list"));
    }
}
