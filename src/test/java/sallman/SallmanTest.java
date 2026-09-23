package sallman;

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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests both ways of driving {@link Sallman}: one reply per input, as a GUI
 * does, and the console loop that reads commands until the user says bye.
 */
public class SallmanTest {

    private static final InputStream ORIGINAL_IN = System.in;
    private static final PrintStream ORIGINAL_OUT = System.out;

    @TempDir
    private Path folder;

    private Sallman sallman;
    private String dataPath;

    @BeforeEach
    public void setUp() {
        dataPath = folder.resolve("tasks.txt").toString();
        sallman = new Sallman(dataPath, false);
    }

    @AfterEach
    public void restoreConsole() {
        System.setIn(ORIGINAL_IN);
        System.setOut(ORIGINAL_OUT);
    }

    /**
     * Feeds lines to the console and returns what was printed while running.
     * The input is swapped in before the chatbot is created, since that is when
     * it starts reading from the console.
     *
     * @param action what to run once the console is swapped
     * @param lines  what the user types, one command per line
     * @return everything printed to the console
     */
    private String runOnConsole(Runnable action, String... lines) {
        String input = String.join("\n", lines) + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream console = new ByteArrayOutputStream();
        System.setOut(new PrintStream(console, true, StandardCharsets.UTF_8));
        action.run();
        return console.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void getResponse_validCommand_notAnError() {
        String reply = sallman.getResponse("todo read book");

        assertTrue(reply.contains("read book"));
        assertFalse(sallman.isLastResponseError());
        assertEquals("AddCommand", sallman.getLastCommandType());
    }

    @Test
    public void getResponse_invalidCommand_reportedAsAnError() {
        sallman.getResponse("blah");

        assertTrue(sallman.isLastResponseError());
        assertEquals("", sallman.getLastCommandType());
    }

    @Test
    public void getResponse_validCommandAfterAnError_errorFlagCleared() {
        // The flag describes the latest reply only, so an earlier error must not
        // make the next, successful reply look like a problem too.
        sallman.getResponse("blah");
        sallman.getResponse("list");

        assertFalse(sallman.isLastResponseError());
    }

    @Test
    public void getResponse_blankInput_emptyReplyAndNoExit() {
        assertEquals("", sallman.getResponse("   "));
        assertFalse(sallman.isExitRequested());
    }

    @Test
    public void getResponse_bye_asksToExit() {
        sallman.getResponse("bye");

        assertTrue(sallman.isExitRequested());
    }

    @Test
    public void getGreeting_cleanStart_greetsWithoutWarnings() {
        String greeting = sallman.getGreeting();

        assertTrue(greeting.startsWith("Hello! I'm saLLMan"));
        assertFalse(greeting.contains("unreadable"));
    }

    @Test
    public void getGreeting_damagedDataFile_warnsAboutTheSkippedLine() throws Exception {
        Path file = folder.resolve("damaged.txt");
        Files.write(file, List.of("T | 0 | read book", "GARBAGE"));

        String greeting = new Sallman(file.toString(), false).getGreeting();

        assertTrue(greeting.contains("I noticed 1 unreadable line in your saved data"));
    }

    @Test
    public void constructor_dataFileCannotBeRead_startsWithAnEmptyListAndSaysWhy() throws Exception {
        // A folder where the data file should be cannot be read as one.
        Path notAFile = Files.createDirectory(folder.resolve("folder.txt"));

        Sallman started = new Sallman(notAFile.toString(), false);

        assertTrue(started.getGreeting().contains("I couldn't read your saved tasks"));
        assertTrue(started.getResponse("list").contains("Your list is currently empty."));
    }

    @Test
    public void getResponse_afterALineCouldNotBeDecoded_otherTasksSurviveTheNextSave() throws Exception {
        // One undecodable byte used to make the whole file unreadable, and the
        // next change then overwrote every task in it.
        Path file = folder.resolve("latin1.txt");
        Files.write(file, StorageTest.bytesWithLatin1Line());

        new Sallman(file.toString(), false).getResponse("todo replacement");

        List<String> saved = new Storage(file.toString()).load().stream().map(Object::toString).toList();
        assertEquals(List.of("[T][ ] read book", "[T][ ] buy milk", "[T][ ] replacement"), saved);
    }

    @Test
    public void run_mixedCommands_carriesOnPastBlankLinesAndErrorsUntilBye() throws Exception {
        String printed = runOnConsole(() -> new Sallman(dataPath).run(),
                "todo read book", "", "blah", "list", "bye", "todo never reached");

        assertTrue(printed.contains("Certainly! What a wonderful task."));
        assertTrue(printed.contains("I'm sorry, but I don't know what \"blah\" means."));
        assertTrue(printed.contains("1.[T][ ] read book"));
        assertTrue(printed.contains("Goodbye!"));
        // Nothing after bye is carried out.
        assertEquals(1, new Storage(dataPath).load().size());
    }

    @Test
    public void run_inputEndsWithoutBye_stopsAndKeepsWhatWasSaved() throws Exception {
        runOnConsole(() -> new Sallman(dataPath).run(), "todo read book");

        assertEquals(1, new Storage(dataPath).load().size());
    }

    @Test
    public void dataPathFrom_noArguments_defaultFile() {
        assertEquals("data/sallman.txt", Sallman.dataPathFrom(List.of()));
    }

    @Test
    public void dataPathFrom_fileNamed_thatFileWhicheverWindowStartsIt() {
        // The console and the GUI both read the data file from here, so a file
        // named when starting either one is the file actually used.
        assertEquals("elsewhere/tasks.txt", Sallman.dataPathFrom(List.of("elsewhere/tasks.txt", "ignored")));
    }

    @Test
    public void main_dataPathGiven_usesThatFile() throws Exception {
        runOnConsole(() -> Sallman.main(new String[] {dataPath}), "todo read book", "bye");

        assertEquals(1, new Storage(dataPath).load().size());
    }
}
