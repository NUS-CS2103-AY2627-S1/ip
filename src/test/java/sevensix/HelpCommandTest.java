package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/** Verifies that help is available without modifying tasks, storage, or undo history. */
class HelpCommandTest {
    /** Keeps persisted data isolated from the user's task list. */
    @TempDir
    private Path temporaryDirectory;

    /** Help accepts surrounding whitespace without creating a saved task file or undo entry.
     *
     * @param command a valid help command with optional whitespace.
     */
    @ParameterizedTest
    @ValueSource(strings = {"help", "help ", "help     ", " help", "  help  ", "help\t", "\thelp\t",
        " \t help \t ", "\u00a0help\u00a0", "\u2003help\u2003", "\u202fhelp\u202f", "\u3000help\u3000"})
    void getResponseHelpLeavesNewSessionEmpty(String command) {
        Path file = temporaryDirectory.resolve("tasks.txt");
        SevenSix chatbot = new SevenSix(file);

        assertEquals(Ui.getHelpMessage(), chatbot.getResponse(command));
        assertEquals(Ui.getEmptyListMessage(), chatbot.getResponse("list"));
        assertEquals(Ui.formatError(Ui.EMPTY_UNDO_HISTORY), chatbot.getResponse("undo"));
        assertFalse(Files.exists(file));
    }

    /** Repeated help requests must preserve saved tasks and the previous undo.
     *
     * @param command the help request to check.
     * @throws IOException if the isolated task file cannot be read.
     */
    @ParameterizedTest
    @ValueSource(strings = {"help", "help  ", "\t help \t"})
    void getResponseHelpPreservesTasksFileAndUndo(String command) throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        SevenSix chatbot = new SevenSix(file);
        chatbot.getResponse("todo read book");
        chatbot.getResponse("mark 1");
        String savedTasks = Files.readString(file);

        assertEquals(Ui.getHelpMessage(), chatbot.getResponse(command));
        assertEquals(Ui.getHelpMessage(), chatbot.getResponse(command));
        assertEquals("Here is your pipeline:" + System.lineSeparator() + "1.[T][X] read book",
                chatbot.getResponse("list"));
        assertEquals(savedTasks, Files.readString(file));
        assertEquals("Undid mark as done: [T][ ] read book", chatbot.getResponse("undo"));
        assertEquals("Here is your pipeline:" + System.lineSeparator() + "1.[T][ ] read book",
                chatbot.getResponse("list"));
        assertEquals(Ui.getHelpMessage(), chatbot.getResponse(command));
        assertEquals("Undid addition: [T][ ] read book", chatbot.getResponse("undo"));
        assertEquals(Ui.formatError(Ui.EMPTY_UNDO_HISTORY), chatbot.getResponse("undo"));
    }

    /** Extra arguments are rejected after whitespace normalization without replacing undo history.
     *
     * @param command a help command containing unsupported arguments.
     */
    @ParameterizedTest
    @ValueSource(strings = {"help todo", "help  todo  ", " help\ttodo\t", "help\u00a0todo",
        "help\u2003todo", "help /by", "help 1", "help --all", "help bye", "help help"})
    void getResponseHelpWithArgumentsPreservesUndo(String command) {
        SevenSix chatbot = new SevenSix(temporaryDirectory.resolve("tasks.txt"));
        chatbot.getResponse("todo read book");

        assertEquals(Ui.formatError(Ui.UNEXPECTED_ARGUMENTS), chatbot.getResponse(command));
        assertEquals("Here is your pipeline:" + System.lineSeparator() + "1.[T][ ] read book",
                chatbot.getResponse("list"));
        assertEquals("Undid addition: [T][ ] read book", chatbot.getResponse("undo"));
        assertEquals(Ui.getEmptyListMessage(), chatbot.getResponse("list"));
    }

    /** Null, empty, and whitespace-only input produces a recoverable error before a valid help request.
     *
     * @param command missing or blank command input.
     */
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", " \t ", "\u00a0", "\u2003", "\u202f", "\u3000"})
    void getResponseBlankInputStillAllowsHelp(String command) {
        SevenSix chatbot = new SevenSix(temporaryDirectory.resolve("tasks.txt"));

        assertEquals(Ui.formatError(Ui.EMPTY_COMMAND), chatbot.getResponse(command));
        assertEquals(Ui.getHelpMessage(), chatbot.getResponse("help"));
    }

    /** Control characters and Unicode line breaks must not become an accepted help command.
     *
     * @param command input containing a line break or another disallowed control character.
     */
    @ParameterizedTest
    @ValueSource(strings = {"help\n", "help\r", "help\r\n", "help\nbye", "help\0", "help\f",
        "he\blp", "help\u001b", "help\u007f", "help\u2028", "\u2029help", "help\u2028bye"})
    void getResponseControlCharactersAreRejected(String command) {
        Path file = temporaryDirectory.resolve("tasks.txt");
        SevenSix chatbot = new SevenSix(file);

        assertEquals(Ui.formatError(Ui.INVALID_CHARACTERS), chatbot.getResponse(command));
        assertEquals(Ui.getHelpMessage(), chatbot.getResponse("help"));
        assertFalse(Files.exists(file));
    }

    /** Storage problems must not prevent the user from reading command instructions. */
    @Test
    void getResponseHelpWorksWhenStorageIsUnavailable() {
        SevenSix chatbot = new SevenSix(temporaryDirectory);

        assertEquals(Ui.LOAD_FAILURE, chatbot.getStartupWarning());
        assertEquals(Ui.getHelpMessage(), chatbot.getResponse("help"));
    }

    /** Help remains an exact, lowercase keyword rather than matching prefixes or case variants.
     *
     * @param command an unsupported spelling of help.
     */
    @ParameterizedTest
    @ValueSource(strings = {"helper", "helpful", "HELP", "Help", "/help", "help!", "he lp", "he\tlp",
        "help\u200b", "help\ufeff"})
    void getResponseSimilarKeywordsRemainUnknown(String command) {
        SevenSix chatbot = new SevenSix(temporaryDirectory.resolve("tasks.txt"));

        assertEquals(Ui.formatError(Ui.getUnknownCommandMessage()), chatbot.getResponse(command));
    }
}
