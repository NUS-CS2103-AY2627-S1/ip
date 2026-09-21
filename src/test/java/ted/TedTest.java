package ted;

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
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link Ted} from the outside, the way the user meets it: one message
 * at a time as the GUI sends them, and as a whole conversation in the terminal.
 * <p>
 * Every test keeps its save file inside a {@link TempDir}, so running the tests
 * can never disturb the user's own tasks.
 */
public class TedTest {
    /** The rule Ted prints above and below each reply in the terminal. */
    private static final String DIVIDER = "____________________________________________________________";

    /**
     * Verifies that a command with leading spaces is recognized.
     *
     * @param tempDir temporary folder for the save file.
     */
    @Test
    public void getResponse_leadingSpaces_commandRecognized(@TempDir Path tempDir) {
        // The GUI passes its text field on untrimmed, so Ted must cope with padding.
        Ted ted = new Ted(tempDir.resolve("ted.txt").toString());
        assertTrue(ted.getResponse("   list").contains("Your list is empty."));
    }

    /**
     * Verifies that a warning about unreadable save data follows the welcome message.
     *
     * @param tempDir temporary folder for the save file.
     * @throws IOException if the test save file cannot be written.
     */
    @Test
    public void getGreeting_unreadableSaveLine_warningFollowsWelcome(@TempDir Path tempDir) throws IOException {
        Path dataFile = tempDir.resolve("ted.txt");
        Files.write(dataFile, List.of("not a task"));

        String greeting = new Ted(dataFile.toString()).getGreeting();
        int welcomeIndex = greeting.indexOf("I'm Ted");
        int warningIndex = greeting.indexOf("so I skipped 1 line");
        assertTrue(welcomeIndex >= 0 && warningIndex > welcomeIndex);
    }

    /**
     * Verifies that the greeting tells the user where the unreadable save
     * file was copied.
     *
     * @param tempDir temporary folder for the save file.
     * @throws IOException if the test save file cannot be written.
     */
    @Test
    public void getGreeting_unreadableSaveLine_mentionsTheCopy(@TempDir Path tempDir) throws IOException {
        Path dataFile = tempDir.resolve("ted.txt");
        Files.write(dataFile, List.of("not a task"));

        String greeting = new Ted(dataFile.toString()).getGreeting();
        assertTrue(greeting.contains("kept a copy of the original file at " + tempDir.resolve("ted.txt.bak")));
    }

    /**
     * Verifies that tasks added, marked and tagged in one session are all
     * there, unchanged, when Ted is started again on the same file.
     *
     * @param tempDir temporary folder for the save file.
     */
    @Test
    public void getResponse_conversationThenRestart_tasksRemembered(@TempDir Path tempDir) {
        String filePath = tempDir.resolve("ted.txt").toString();
        Ted ted = new Ted(filePath);
        ted.getResponse("todo borrow book");
        ted.getResponse("deadline return book /by 2/12/2019 1800");
        ted.getResponse("event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600");
        ted.getResponse("mark 1");
        ted.getResponse("tag 2 #school");

        String list = new Ted(filePath).getResponse("list");

        assertTrue(list.contains("1.[T][X] borrow book"));
        assertTrue(list.contains("2.[D][ ] return book #school (by: 2 Dec 2019, 6:00 PM)"));
        assertTrue(list.contains("3.[E][ ] project meeting (from: 2 Dec 2019, 2:00 PM to: 2 Dec 2019, 4:00 PM)"));
    }

    /**
     * Verifies that blank input gets no reply at all.
     *
     * @param tempDir temporary folder for the save file.
     */
    @Test
    public void getResponse_blankInput_noReply(@TempDir Path tempDir) {
        Ted ted = new Ted(tempDir.resolve("ted.txt").toString());
        assertEquals("", ted.getResponse("   "));
    }

    /**
     * Verifies that a mistake is explained and the conversation carries on.
     *
     * @param tempDir temporary folder for the save file.
     */
    @Test
    public void getResponse_unknownCommand_explainedAndConversationContinues(@TempDir Path tempDir) {
        Ted ted = new Ted(tempDir.resolve("ted.txt").toString());

        assertTrue(ted.getResponse("sing").contains("\"sing\"? Never heard of it."));
        assertFalse(ted.isExit());
        assertTrue(ted.getResponse("todo read book").contains("I've added:"));
    }

    /**
     * Verifies that a command that fails while running, such as deleting a
     * task that does not exist, is explained without ending the conversation.
     *
     * @param tempDir temporary folder for the save file.
     */
    @Test
    public void getResponse_deleteMissingTask_explainedAndConversationContinues(@TempDir Path tempDir) {
        Ted ted = new Ted(tempDir.resolve("ted.txt").toString());

        assertTrue(ted.getResponse("delete 3").contains("Your list is empty, so there's no task 3."));
        assertFalse(ted.isExit());
    }

    /**
     * Verifies that only bye ends the conversation, and that it gets a goodbye.
     *
     * @param tempDir temporary folder for the save file.
     */
    @Test
    public void getResponse_bye_goodbyeAndExit(@TempDir Path tempDir) {
        Ted ted = new Ted(tempDir.resolve("ted.txt").toString());
        ted.getResponse("list");
        assertFalse(ted.isExit());

        assertEquals("Leaving already? Fine. I'll be here on the shelf. Bye!", ted.getResponse("bye"));
        assertTrue(ted.isExit());
    }

    /**
     * Verifies that only a reply explaining a problem is flagged as an error,
     * so that the GUI highlights mistakes and nothing else.
     *
     * @param tempDir temporary folder for the save file.
     */
    @Test
    public void isError_mistakeThenValidCommand_onlyMistakeFlagged(@TempDir Path tempDir) {
        Ted ted = new Ted(tempDir.resolve("ted.txt").toString());
        assertFalse(ted.isError());

        ted.getResponse("mark 1");
        assertTrue(ted.isError());

        ted.getResponse("todo read book");
        assertFalse(ted.isError());
    }

    /**
     * Verifies that a greeting warning about the save file is flagged, while
     * an ordinary greeting is not.
     *
     * @param tempDir temporary folder for the save file.
     * @throws IOException if the test save file cannot be written.
     */
    @Test
    public void isError_greetingWithAndWithoutWarning_flaggedOnlyWithWarning(@TempDir Path tempDir)
            throws IOException {
        Ted ted = new Ted(tempDir.resolve("clean.txt").toString());
        ted.getGreeting();
        assertFalse(ted.isError());

        Path corruptedFile = tempDir.resolve("corrupted.txt");
        Files.write(corruptedFile, List.of("not a task"));
        Ted warnedTed = new Ted(corruptedFile.toString());
        warnedTed.getGreeting();
        assertTrue(warnedTed.isError());
    }

    /**
     * Verifies that a folder where the save file should be is reported in the
     * greeting, and Ted starts with an empty list instead of failing.
     *
     * @param tempDir temporary folder for the save file.
     * @throws IOException if the test folder cannot be created.
     */
    @Test
    public void getGreeting_saveFileIsAFolder_startsWithEmptyList(@TempDir Path tempDir) throws IOException {
        Path dataFile = Files.createDirectory(tempDir.resolve("ted.txt"));
        Ted ted = new Ted(dataFile.toString());

        String greeting = ted.getGreeting();
        assertTrue(greeting.contains("is a folder, not a file"));
        assertTrue(greeting.contains("I'll start you on an empty list for now."));
        assertTrue(ted.getResponse("list").contains("Your list is empty."));
    }

    /**
     * Verifies that the terminal conversation answers each typed line in
     * turn, skips blank lines, and stops at bye.
     *
     * @param tempDir temporary folder for the save file.
     */
    @Test
    public void run_typedLines_eachAnsweredUntilBye(@TempDir Path tempDir) {
        String output = runInTerminal(tempDir, "todo read book\n\nlist\nbye\nlist\n");

        assertTrue(output.contains("I've added:"));
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("Leaving already?"));
        // The greeting, todo, list and bye each get one framed reply; the blank
        // line gets none, and the list after bye is never read.
        assertEquals(8, countDividers(output));
    }

    /**
     * Verifies that the user still gets a goodbye when the input runs out
     * before they say bye, e.g. at the end of a piped file.
     *
     * @param tempDir temporary folder for the save file.
     */
    @Test
    public void run_inputEndsWithoutBye_stillSaysGoodbye(@TempDir Path tempDir) {
        String output = runInTerminal(tempDir, "list\n");
        assertTrue(output.contains("Leaving already?"));
    }

    /**
     * Runs a whole terminal conversation with the given typed input.
     *
     * @param tempDir the folder holding the save file.
     * @param typed   everything the user types, one command per line.
     * @return everything Ted printed.
     */
    private static String runInTerminal(Path tempDir, String typed) {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(typed.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
            // Ted's Ui reads from whatever System.in is when Ted is created.
            new Ted(tempDir.resolve("ted.txt").toString()).run();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        return printed.toString(StandardCharsets.UTF_8);
    }

    /**
     * Counts the divider lines in the terminal output.
     *
     * @param output everything Ted printed.
     * @return how many lines are dividers.
     */
    private static long countDividers(String output) {
        return output.lines().filter(DIVIDER::equals).count();
    }
}
