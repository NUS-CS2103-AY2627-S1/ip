package serangooner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SerangoonerTest {
    // Serangooner builds its own Ui over the standard streams, so a test has to
    // stand in for them. Only input that the chatbot accepts is fed to it: an
    // error would reach the sound player, which a test run must not set off.

    private static final String DIVIDER = "━━━ . °‧ 𓆝 𓆟 𓆞 ·｡";

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @AfterEach
    public void restoreStandardStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /** Runs the chatbot on the given save file, feeding it the given lines. */
    private String runWith(Path saveFile, String... lines) {
        String input = String.join(System.lineSeparator(), lines);
        if (lines.length > 0) {
            input += System.lineSeparator();
        }
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        new Serangooner(saveFile.toString()).run();

        return output.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void run_noInput_greetsAndStopsWithoutWaiting(@TempDir Path directory) {
        String printed = runWith(directory.resolve("tasks.txt"));
        assertTrue(printed.contains("serangooner at your service"));
    }

    @Test
    public void run_missingSaveFile_saysNothingAboutLoading(@TempDir Path directory) {
        String printed = runWith(directory.resolve("tasks.txt"), "bye");
        assertFalse(printed.contains("loaded"));
        assertFalse(printed.contains("couldn't read"));
    }

    @Test
    public void run_byeCommand_saysGoodbye(@TempDir Path directory) {
        String printed = runWith(directory.resolve("tasks.txt"), "bye");
        assertTrue(printed.contains("bye~"));
    }

    @Test
    public void run_linesAfterBye_areNotRun(@TempDir Path directory) throws IOException {
        Path saveFile = directory.resolve("tasks.txt");
        Files.write(saveFile, List.of("T | 0 | read book"));

        String printed = runWith(saveFile, "bye", "list");

        assertTrue(printed.contains("bye~"));
        assertFalse(printed.contains("[T][ ] read book"));
    }

    @Test
    public void run_existingSaveFile_loadsItAndReportsWhatItRead(@TempDir Path directory)
            throws IOException {
        Path saveFile = directory.resolve("tasks.txt");
        Files.write(saveFile, List.of("T | 0 | read book", "T | 1 | write essay"));

        String printed = runWith(saveFile, "list", "bye");

        assertTrue(printed.contains("loaded 2 task(s) from your last visit"));
        assertTrue(printed.contains("[T][ ] read book"));
        assertTrue(printed.contains("[T][✓] write essay"));
    }

    @Test
    public void run_unreadableSaveFile_startsEmptyAndSaysWhy(@TempDir Path directory)
            throws IOException {
        Path saveFile = directory.resolve("tasks.txt");
        Files.createDirectory(saveFile);

        String printed = runWith(saveFile, "bye");

        assertTrue(printed.contains("couldn't read"));
        assertTrue(printed.contains("bye~"));
    }

    @Test
    public void run_addCommand_writesTheTaskToTheSaveFile(@TempDir Path directory)
            throws IOException {
        Path saveFile = directory.resolve("tasks.txt");

        runWith(saveFile, "todo read book", "bye");

        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(saveFile));
    }

    @Test
    public void run_severalCommands_carriesTheListBetweenThem(@TempDir Path directory)
            throws IOException {
        Path saveFile = directory.resolve("tasks.txt");

        runWith(saveFile, "todo read book", "todo write essay", "mark 1", "delete 2", "bye");

        assertEquals(List.of("T | 1 | read book"), Files.readAllLines(saveFile));
    }

    @Test
    public void run_undoAfterAnEdit_reachesTheSaveFile(@TempDir Path directory)
            throws IOException {
        Path saveFile = directory.resolve("tasks.txt");

        runWith(saveFile, "todo read book", "todo write essay", "undo", "bye");

        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(saveFile));
    }

    @Test
    public void run_everyCommand_isFollowedByADivider(@TempDir Path directory) {
        String printed = runWith(directory.resolve("tasks.txt"), "list", "help", "bye");

        // The greeting sits between two dividers, and one follows each of the
        // three commands.
        assertEquals(5, printed.split(DIVIDER, -1).length - 1);
    }

    @Test
    public void constructor_missingSaveFile_doesNotFail(@TempDir Path directory) {
        assertDoesNotThrow(() -> new Serangooner(directory.resolve("tasks.txt").toString()));
    }

    @Test
    public void getResponse_addCommand_addsTheTaskAndSaysSo(@TempDir Path directory) {
        Serangooner serangooner = new Serangooner(directory.resolve("tasks.txt").toString());

        Serangooner.Response response = serangooner.getResponse("todo read book");

        assertTrue(response.text().startsWith("added todo: [T][ ] read book"));
        assertFalse(response.isExit());
        assertFalse(response.isError());
    }

    @Test
    public void getResponse_severalLines_carriesTheListBetweenThem(@TempDir Path directory) {
        Serangooner serangooner = new Serangooner(directory.resolve("tasks.txt").toString());

        serangooner.getResponse("todo read book");
        serangooner.getResponse("todo write essay");

        assertTrue(serangooner.getResponse("list").text().contains(" 2. [T][ ] write essay"));
    }

    @Test
    public void getResponse_unforeseenFailure_reportsAnErrorWithoutEndingTheConversation(
            @TempDir Path directory) {
        Serangooner serangooner = new Serangooner(directory.resolve("tasks.txt").toString());

        // Neither front end passes a missing line, so the failure it causes is one no check foresaw.
        Serangooner.Response response = serangooner.getResponse(null);

        assertTrue(response.isError());
        assertFalse(response.isExit());
        assertTrue(response.text().contains("NullPointerException"));
        assertTrue(serangooner.getResponse("todo read book").text().startsWith("added todo"));
    }

    @Test
    public void getResponse_unknownCommand_reportsAnErrorWithoutEndingTheConversation(
            @TempDir Path directory) {
        Serangooner serangooner = new Serangooner(directory.resolve("tasks.txt").toString());

        Serangooner.Response response = serangooner.getResponse("bake a cake");

        assertTrue(response.isError());
        assertFalse(response.isExit());
        assertFalse(response.text().isBlank());
    }

    @Test
    public void getResponse_byeCommand_saysGoodbyeAndAsksToEnd(@TempDir Path directory) {
        Serangooner serangooner = new Serangooner(directory.resolve("tasks.txt").toString());

        Serangooner.Response response = serangooner.getResponse("bye");

        assertEquals("bye~", response.text());
        assertTrue(response.isExit());
    }

    @Test
    public void getResponse_addCommand_reachesTheSaveFile(@TempDir Path directory)
            throws IOException {
        Path saveFile = directory.resolve("tasks.txt");

        new Serangooner(saveFile.toString()).getResponse("todo read book");

        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(saveFile));
    }

    @Test
    public void getGreeting_always_pointsToHelpAndBye(@TempDir Path directory) {
        String greeting = new Serangooner(directory.resolve("tasks.txt").toString()).getGreeting();

        assertTrue(greeting.contains("to see commands, type 'help'"));
        assertTrue(greeting.contains("done? type 'bye' to exit :("));
    }

    @Test
    public void getLoadReport_existingSaveFile_reportsWhatItRead(@TempDir Path directory)
            throws IOException {
        Path saveFile = directory.resolve("tasks.txt");
        Files.write(saveFile, List.of("T | 0 | read book", "T | 1 | write essay"));

        String report = new Serangooner(saveFile.toString()).getLoadReport();

        assertEquals("loaded 2 task(s) from your last visit", report);
    }

    @Test
    public void getLoadReport_missingSaveFile_saysNothing(@TempDir Path directory) {
        assertEquals("",
                new Serangooner(directory.resolve("tasks.txt").toString()).getLoadReport());
    }
}
