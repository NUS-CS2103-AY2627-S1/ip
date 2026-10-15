package duncan;

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
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DuncanTest {
    private static final String NEWLINE = System.lineSeparator();

    @TempDir
    Path tempDir;

    /** Returns a Duncan saving to this test's temporary directory, so tests do not share a save file. */
    private Duncan createDuncan() {
        return new Duncan(tempDir.resolve("tasks.txt").toString());
    }

    @Test
    public void getResponse_addTodo_taskAddedMessageReturned() {
        assertEquals("Got it. I've added this task:" + NEWLINE
                + "  [T][ ] read book" + NEWLINE
                + "Now you have 1 tasks in the list." + NEWLINE,
                createDuncan().getResponse("todo read book"));
    }

    @Test
    public void getResponse_unknownCommand_errorMessageReturned() {
        assertEquals("HEY! idk what's that supposed to be" + NEWLINE,
                createDuncan().getResponse("blah"));
    }

    @Test
    public void getResponse_secondCommand_onlyThatCommandsReplyReturned() {
        Duncan duncan = createDuncan();
        duncan.getResponse("todo read book");

        assertEquals("Here are the tasks in your list:" + NEWLINE
                + "1.[T][ ] read book" + NEWLINE,
                duncan.getResponse("list"));
    }

    @Test
    public void getResponse_findAfterNonMatchingTask_resultsUseListNumbers() {
        Duncan duncan = createDuncan();
        duncan.getResponse("todo buy milk");
        duncan.getResponse("todo read book");

        assertEquals("Here are the matching tasks in your list:" + NEWLINE
                + "2.[T][ ] read book" + NEWLINE,
                duncan.getResponse("find book"));
    }

    @Test
    public void isExit_byeCommand_trueOnlyAfterBye() {
        Duncan duncan = createDuncan();
        assertFalse(duncan.isExit());

        duncan.getResponse("list");
        assertFalse(duncan.isExit());

        duncan.getResponse("bye");
        assertTrue(duncan.isExit());
    }

    @Test
    public void getWelcome_freshSession_greetingWithoutBannerReturned() {
        assertEquals("Hello! I'm Duncan." + NEWLINE
                + "What can I do for you?" + NEWLINE,
                createDuncan().getWelcome());
    }

    @Test
    public void getResponse_rescheduleDeadlineWithTextBeforeBy_missingByMessageReturned() {
        Duncan duncan = createDuncan();
        duncan.getResponse("deadline return book /by 2019-12-02");

        assertEquals("HEY! deadlines must have /by <date/time>" + NEWLINE,
                duncan.getResponse("reschedule 1 soon /by 2019-12-09"));
    }

    @Test
    public void getResponse_rescheduleEventWithToBeforeFrom_fromBeforeToMessageReturned() {
        Duncan duncan = createDuncan();
        duncan.getResponse("event project fair /from 2019-12-01 /to 2019-12-02");

        assertEquals("HEY! /from must come before /to" + NEWLINE,
                duncan.getResponse("reschedule 1 /to 2019-12-06 /from 2019-12-05"));
    }

    @Test
    public void getResponse_eventWithToBeforeFrom_errorMessageReturnedInsteadOfCrash() {
        assertEquals("HEY! /from must come before /to" + NEWLINE,
                createDuncan().getResponse("event x /to 2019-12-02 /from 2019-12-01"));
    }

    @Test
    public void getResponse_noDataFileAtStart_todoSavedWithoutWarnings() throws IOException {
        Path savePath = tempDir.resolve("data/tasks.txt");
        Duncan duncan = new Duncan(savePath.toString());

        assertEquals("Hello! I'm Duncan." + NEWLINE + "What can I do for you?" + NEWLINE, duncan.getWelcome());
        assertFalse(duncan.getResponse("todo read book").contains("WARNING"));
        assertEquals("T\t0\tread book" + NEWLINE, Files.readString(savePath));
    }

    @Test
    public void getResponse_tabInDescription_savedAsSpaceAndSurvivesRestart() {
        createDuncan().getResponse("todo read\tbook");

        assertEquals("Here are the tasks in your list:" + NEWLINE
                + "1.[T][ ] read book" + NEWLINE,
                createDuncan().getResponse("list"));
    }

    @Test
    public void getResponse_saveFails_changeKeptAndNotSavedWarningShownEachTime() throws IOException {
        Duncan duncan = createDuncan();
        Files.createDirectory(tempDir.resolve("tasks.txt"));
        String notSavedWarning = "WARNING: that change was made but NOT saved: couldn't write to";

        String firstReply = duncan.getResponse("todo read book");
        String secondReply = duncan.getResponse("todo return book");

        assertTrue(firstReply.startsWith("Got it. I've added this task:"));
        assertTrue(firstReply.contains(notSavedWarning));
        assertTrue(secondReply.contains(notSavedWarning));
        assertEquals("Here are the tasks in your list:" + NEWLINE
                + "1.[T][ ] read book" + NEWLINE
                + "2.[T][ ] return book" + NEWLINE,
                duncan.getResponse("list"));
    }

    @Test
    public void getWelcome_saveFileHasBadLine_warningShownBeforeGreeting() throws IOException {
        Path savePath = tempDir.resolve("tasks.txt");
        Files.writeString(savePath, "garbage\n");

        String welcome = new Duncan(savePath.toString()).getWelcome();

        assertTrue(welcome.startsWith("WARNING: skipped bad lines in " + savePath + ": 1" + NEWLINE));
        assertTrue(welcome.endsWith("Hello! I'm Duncan." + NEWLINE + "What can I do for you?" + NEWLINE));
    }

    @Test
    public void getResponseLines_unknownCommand_singleErrorLine() {
        assertEquals(List.of(new Ui.MessageLine("HEY! idk what's that supposed to be", Ui.Severity.ERROR)),
                createDuncan().getResponseLines("blah"));
    }

    @Test
    public void getResponseLines_validCommand_allLinesNormal() {
        List<Ui.MessageLine> lines = createDuncan().getResponseLines("todo read book");

        assertEquals(Ui.Severity.NORMAL, Ui.getHighestSeverity(lines));
        assertEquals(3, lines.size());
    }

    @Test
    public void getResponseLines_sameCommand_sameTextAsGetResponse() {
        Duncan linesDuncan = new Duncan(tempDir.resolve("lines.txt").toString());
        Duncan stringDuncan = new Duncan(tempDir.resolve("string.txt").toString());

        String joinedLines = linesDuncan.getResponseLines("todo read book").stream()
                .map(line -> line.text() + NEWLINE)
                .collect(Collectors.joining());

        assertEquals(stringDuncan.getResponse("todo read book"), joinedLines);
    }

    @Test
    public void getResponseLines_saveFails_onlyTheWarningLineIsWarning() throws IOException {
        Duncan duncan = createDuncan();
        Files.createDirectory(tempDir.resolve("tasks.txt"));

        List<Ui.MessageLine> lines = duncan.getResponseLines("todo read book");

        assertEquals(4, lines.size());
        assertEquals(Ui.Severity.NORMAL, lines.get(2).severity());
        assertEquals(Ui.Severity.WARNING, lines.get(3).severity());
        assertTrue(lines.get(3).text().startsWith("WARNING: that change was made but NOT saved:"));
        assertEquals(Ui.Severity.WARNING, Ui.getHighestSeverity(lines));
    }

    @Test
    public void getWelcomeLines_saveFileHasBadLine_everyLoadWarningLineIsWarning() throws IOException {
        Path savePath = tempDir.resolve("tasks.txt");
        Files.writeString(savePath, "garbage\n");

        List<Ui.MessageLine> lines = new Duncan(savePath.toString()).getWelcomeLines();

        // The second warning is the backup notice, which carries no "WARNING:" prefix.
        assertEquals(4, lines.size());
        assertEquals(Ui.Severity.WARNING, lines.get(0).severity());
        assertEquals(Ui.Severity.WARNING, lines.get(1).severity());
        assertEquals(new Ui.MessageLine("Hello! I'm Duncan.", Ui.Severity.NORMAL), lines.get(2));
        assertEquals(new Ui.MessageLine("What can I do for you?", Ui.Severity.NORMAL), lines.get(3));
    }

    @Test
    public void getBanner_anyDuncan_sameBannerAsUi() {
        assertEquals(new Ui().getBanner(), createDuncan().getBanner());
    }

    @Test
    public void run_consoleInputEndsWithoutBye_returnsWithoutException() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        try {
            System.setIn(new ByteArrayInputStream("list\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8));
            Duncan duncan = createDuncan();

            assertDoesNotThrow(duncan::run);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}
