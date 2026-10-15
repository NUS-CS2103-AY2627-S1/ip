package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Exercises successive undo, intervening commands, and session boundaries through the public API. */
class UndoSequenceTest {
    @TempDir
    private Path temporaryDirectory;

    /** Mixed changes must be reversed in order and saved after every undo. */
    @Test
    void undoReversesMixedChangesAndPersistsEachState() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        SevenSix chatbot = new SevenSix(file);
        chatbot.getResponse("todo first");
        chatbot.getResponse("todo second");
        chatbot.getResponse("mark 1");
        chatbot.getResponse("unmark 1");
        chatbot.getResponse("delete 2");

        assertEquals("Undid deletion: [T][ ] second", chatbot.getResponse("undo"));
        assertListPersisted(chatbot, file, "1.[T][ ] first", "2.[T][ ] second");
        assertEquals("Undid mark as not done: [T][X] first", chatbot.getResponse("undo"));
        assertListPersisted(chatbot, file, "1.[T][X] first", "2.[T][ ] second");
        assertEquals("Undid mark as done: [T][ ] first", chatbot.getResponse("undo"));
        assertListPersisted(chatbot, file, "1.[T][ ] first", "2.[T][ ] second");
        assertEquals("Undid addition: [T][ ] second", chatbot.getResponse("undo"));
        assertListPersisted(chatbot, file, "1.[T][ ] first");
        assertEquals("Undid addition: [T][ ] first", chatbot.getResponse("undo"));
        assertEquals(Ui.getEmptyListMessage(), new SevenSix(file).getResponse("list"));
        assertEquals(Ui.formatError(Ui.EMPTY_UNDO_HISTORY), chatbot.getResponse("undo"));
    }

    /** A new change after undo must retain earlier history without resurrecting the undone change. */
    @Test
    void newChangeAfterUndoKeepsEarlierHistoryAcrossReadOnlyAndInvalidCommands() {
        SevenSix chatbot = new SevenSix(temporaryDirectory.resolve("tasks.txt"));
        chatbot.getResponse("todo first");
        chatbot.getResponse("todo discarded");
        chatbot.getResponse("undo");
        chatbot.getResponse("todo replacement");
        chatbot.getResponse("help");
        chatbot.getResponse("list");
        chatbot.getResponse("find first");
        chatbot.getResponse("delete 99");
        chatbot.getResponse("todo first");

        assertEquals("Undid addition: [T][ ] replacement", chatbot.getResponse("undo"));
        assertEquals("Undid addition: [T][ ] first", chatbot.getResponse("undo"));
        assertEquals(Ui.formatError(Ui.EMPTY_UNDO_HISTORY), chatbot.getResponse("undo"));
    }

    /** Repeating a status command remains an undoable step, but restarting clears all history. */
    @Test
    void repeatedMarkRemainsUndoableAndRestartClearsHistory() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        SevenSix chatbot = new SevenSix(file);
        chatbot.getResponse("todo first");
        chatbot.getResponse("mark 1");
        chatbot.getResponse("mark 1");

        assertEquals("Undid mark as done: [T][X] first", chatbot.getResponse("undo"));
        assertListPersisted(chatbot, file, "1.[T][X] first");
        assertEquals("Undid mark as done: [T][ ] first", chatbot.getResponse("undo"));
        assertEquals(Ui.formatError(Ui.EMPTY_UNDO_HISTORY), new SevenSix(file).getResponse("undo"));
        assertEquals("Undid addition: [T][ ] first", chatbot.getResponse("undo"));
    }

    /** Compares the live and reloaded list to the same independent expectation.
     *
     * @param chatbot the active session.
     * @param file the persisted task file.
     * @param lines the expected numbered tasks.
     */
    private void assertListPersisted(SevenSix chatbot, Path file, String... lines) {
        String expected = "Here is your pipeline:" + System.lineSeparator()
                + String.join(System.lineSeparator(), lines);
        assertEquals(expected, chatbot.getResponse("list"));
        assertEquals(expected, new SevenSix(file).getResponse("list"));
    }
}
