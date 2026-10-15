package piplupbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link PiplupBot#respondTo}, the step that both the console and the
 * window take for every line the user types, along with what comes before and
 * around it: {@link PiplupBot#greet}, and the console's own loop,
 * {@link PiplupBot#run}.
 *
 * <p>The console is tested end to end by the text-UI test plan, but the window
 * has no such plan, so these cases are what check that a line typed there is
 * carried out, answered and saved just as the console would do it -- and that
 * the window, which greets the user through the same method, opens with the
 * same greeting and warnings.</p>
 *
 * <p>The replies are caught by a {@link RecordingUi} rather than by the
 * window's {@link GuiUi}, which can only be made while JavaFX is running and
 * which opens the real save file. Every case keeps its tasks in a folder JUnit
 * creates and deletes for it, so no test can read or damage the real save
 * file.</p>
 */
public class PiplupBotTest {

    /**
     * A folder JUnit creates for each test and deletes afterwards. It is left
     * package-private because {@code @TempDir} cannot fill in a private field.
     */
    @TempDir
    Path tempDir;

    /** Catches what the bot says. */
    private final RecordingUi ui = new RecordingUi();

    /**
     * Returns the save file this test should use, inside the temporary folder.
     *
     * @return The path to a save file of this test's own.
     */
    private Path getSaveFile() {
        return tempDir.resolve("piplupbot.txt");
    }

    /**
     * Returns everything the given bot prints in a console conversation, as
     * though the user had typed the given text.
     *
     * <p>The console reads through {@code System.in} and prints through
     * {@code System.out}. Both are swapped for streams this test controls while
     * the bot runs, and put back afterwards even if the run fails: every test
     * shares them, so leaving either one swapped would quietly change the tests
     * that come after.</p>
     *
     * @param bot   The bot to talk to.
     * @param typed Everything the user types, with a line break after each line.
     * @return Everything the bot printed.
     */
    private static String runInConsole(PiplupBot bot, String typed) {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(typed.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
        try {
            bot.run();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        return printed.toString(StandardCharsets.UTF_8);
    }

    // ---------- Greeting the user ----------

    /** A save file that reads cleanly is not worth a word, so the greeting is all the user sees. */
    @Test
    public void greet_readableSaveFile_greetsOnly() throws Exception {
        Files.writeString(getSaveFile(), "T | 0 | read book\n");

        new PiplupBot(getSaveFile()).greet(ui);

        assertEquals(List.of("Hello! I'm PiplupBot. Pip-pip!\nWhat can I do for you?"), ui.getReplies());
    }

    /**
     * A save file that could not be read in full is reported as a reply of its
     * own, after the greeting, so that the bot introduces itself before it
     * complains.
     */
    @Test
    public void greet_damagedSaveFile_greetsThenWarns() throws Exception {
        Files.writeString(getSaveFile(), "T | 0 | read book\nnonsense\n");

        new PiplupBot(getSaveFile()).greet(ui);

        List<String> replies = ui.getReplies();
        assertEquals(2, replies.size());
        assertEquals("Hello! I'm PiplupBot. Pip-pip!\nWhat can I do for you?", replies.get(0));
        assertTrue(replies.get(1).startsWith("I could not understand 1 line in "),
                "Expected the warning second, but was: " + replies.get(1));
    }

    // ---------- Answering one line ----------

    @Test
    public void respondTo_taskCommand_answersAndSavesTheTask() throws Exception {
        PiplupBot bot = new PiplupBot(getSaveFile());

        bot.respondTo("todo read book", ui);

        assertFalse(bot.isExit());
        assertEquals(List.of("Piplup! I've tucked this task under my wing:\n"
                        + "  [T][ ] read book\n"
                        + "Now you have 1 task in the list."),
                ui.getReplies());
        assertEquals("T | 0 | read book\n", Files.readString(getSaveFile()));
    }

    /** Lines typed one after another act on the same list, as they do in the console. */
    @Test
    public void respondTo_severalLines_actOnTheSameList() {
        PiplupBot bot = new PiplupBot(getSaveFile());
        bot.respondTo("todo read book", ui);
        bot.respondTo("mark 1", ui);
        ui.clearReplies();

        bot.respondTo("list", ui);

        assertEquals(List.of("Here are the tasks in your list:\n1.[T][X] read book"), ui.getReplies());
    }

    /**
     * The window has no test plan of its own, so this is what checks that
     * {@code sort} works there too: the same reply the console gives, and a
     * save file holding the tasks in the new order rather than the order they
     * were added in. It is also the only place the sorted order is checked
     * against the file at all -- each text-UI case starts a fresh program, so
     * none of them can see what a sort left behind.
     *
     * <p>The saved line format is unchanged: the same fields, the same
     * separator and the same ISO date as before this command existed. Only
     * which line comes first is different, which is why a file written by an
     * earlier version still loads.</p>
     */
    @Test
    public void respondTo_sortCommand_answersWithTheSortedListAndSavesTheNewOrder()
            throws Exception {
        PiplupBot bot = new PiplupBot(getSaveFile());
        bot.respondTo("todo read book", ui);
        bot.respondTo("deadline return book /by 2019-10-15 1800", ui);
        ui.clearReplies();

        bot.respondTo("sort date", ui);

        assertFalse(bot.isExit());

        assertEquals(List.of("Here are your tasks, sorted by date:\n"
                        + "1.[D][ ] return book (by: Oct 15 2019 06:00 PM)\n"
                        + "2.[T][ ] read book"),
                ui.getReplies());
        assertEquals("D | 0 | return book | 2019-10-15T18:00\nT | 0 | read book\n",
                Files.readString(getSaveFile()));
    }

    /** {@code bye} ends the conversation, and still says goodbye first. */
    @Test
    public void respondTo_bye_saysGoodbyeAndEndsConversation() {
        PiplupBot bot = new PiplupBot(getSaveFile());
        bot.respondTo("bye", ui);

        assertTrue(bot.isExit());
        assertEquals(List.of("Pip-pip! Off for a swim. Hope to see you again soon!"), ui.getReplies());
    }

    /**
     * A line the bot cannot understand is answered rather than thrown: an
     * exception escaping here would end the console conversation, and leave the
     * window with no reply at all.
     */
    @Test
    public void respondTo_unknownCommand_explainsAndCarriesOn() {
        PiplupBot bot = new PiplupBot(getSaveFile());
        bot.respondTo("blah", ui);

        assertFalse(bot.isExit());
        assertEquals(1, ui.getReplies().size());
        assertTrue(ui.getReplies().get(0).startsWith("Pip... I don't know what \"blah\" means."));
    }

    /** A blank line names no command, so it gets no reply, not even an error. */
    @Test
    public void respondTo_blankLine_saysNothingAndCarriesOn() {
        PiplupBot bot = new PiplupBot(getSaveFile());
        bot.respondTo("   ", ui);

        assertFalse(bot.isExit());
        assertEquals(List.of(), ui.getReplies());
    }

    /**
     * Guards the trim in {@code respondTo}. The console's reader trims every
     * line before the bot sees it, but the window's text box does not, so
     * without it a stray space typed in the window would turn {@code bye} into
     * an unknown command.
     */
    @Test
    public void respondTo_surroundingSpaces_areIgnored() {
        PiplupBot bot = new PiplupBot(getSaveFile());
        bot.respondTo("  bye  ", ui);

        assertTrue(bot.isExit());
    }

    /**
     * Guards the choice to leave {@code isExit} alone on a line that is not
     * carried out. Once {@code bye} has ended the conversation, a stray line
     * after it -- a blank one, say -- must not quietly reopen it, which is what
     * would happen if every line reset the answer to {@code false}.
     */
    @Test
    public void isExit_blankLineAfterBye_staysTrue() {
        PiplupBot bot = new PiplupBot(getSaveFile());
        bot.respondTo("bye", ui);

        bot.respondTo("   ", ui);

        assertTrue(bot.isExit());
    }

    // ---------- The console's conversation ----------

    /**
     * {@code bye} ends the conversation there and then: a line typed after it
     * is never read, so the task it names is never stored. The save file is
     * checked rather than what was printed, because how the console lays out a
     * reply is the text-UI test plan's business.
     */
    @Test
    public void run_lineAfterBye_isNeverRead() throws Exception {
        String printed = runInConsole(new PiplupBot(getSaveFile()),
                "todo read book\nbye\ntodo write notes\n");

        assertTrue(printed.contains("Pip-pip! Off for a swim."),
                "Expected a goodbye, but the console printed:\n" + printed);
        assertEquals("T | 0 | read book\n", Files.readString(getSaveFile()));
    }

    /**
     * Input that runs out without a {@code bye} -- the end of a piped file, or
     * Ctrl-D -- ends the conversation quietly. Reading on regardless would fail
     * on a line that is not there.
     */
    @Test
    public void run_inputEndsWithoutBye_stopsWithoutGoodbye() throws Exception {
        String printed = runInConsole(new PiplupBot(getSaveFile()), "todo read book\n");

        assertFalse(printed.contains("Pip-pip! Off for a swim."),
                "Expected no goodbye, but the console printed:\n" + printed);
        assertEquals("T | 0 | read book\n", Files.readString(getSaveFile()));
    }
}
