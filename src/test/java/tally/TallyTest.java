package tally;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the replies Tally hands back for a front end to display. */
public class TallyTest {
    @TempDir
    private Path folder;

    /** Runs a console Tally over the given input, and returns everything it printed. */
    private String runOnConsole(String typed) {
        InputStream realInput = System.in;
        PrintStream realOutput = System.out;
        try {
            System.setIn(new ByteArrayInputStream(typed.getBytes(StandardCharsets.UTF_8)));
            ByteArrayOutputStream printed = new ByteArrayOutputStream();
            System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
            new Tally(folder.resolve("tally.txt")).run();
            return printed.toString(StandardCharsets.UTF_8);
        } finally {
            System.setIn(realInput);
            System.setOut(realOutput);
        }
    }

    private Tally makeTally() {
        return new Tally(folder.resolve("tally.txt"), false);
    }

    @Test
    public void getGreeting_freshTally_greetsWithoutComplaining() {
        String greeting = makeTally().getGreeting();
        assertTrue(greeting.contains("Tally."));
        assertTrue(greeting.contains("I keep the count. You keep the promises."));
    }

    @Test
    public void getResponse_addThenList_reportsBoth() {
        Tally tally = makeTally();
        assertTrue(tally.getResponse("todo read book").contains("[T][ ] read book"));
        assertTrue(tally.getResponse("list").contains("1.[T][ ] read book"));
    }

    @Test
    public void getResponse_badCommand_returnsTheComplaintRatherThanThrowing() {
        assertTrue(makeTally().getResponse("blah").startsWith("Unknown command."));
    }

    @Test
    public void getResponse_reply_carriesNoHorizontalRules() {
        // The rules separate messages in a terminal; a chat window separates them itself.
        assertFalse(makeTally().getResponse("todo read book").contains("____"));
    }

    @Test
    public void getResponse_everyReply_isNeverEmpty() {
        Tally tally = makeTally();
        List<String> commands = List.of("list", "todo read book", "mark 1", "find book",
                "delete 1", "blah", "mark 99");
        for (String command : commands) {
            assertFalse(tally.getResponse(command).isBlank(), "empty reply for: " + command);
        }
    }

    @Test
    public void isExiting_beforeAndAfterGoodbye_flipsOnlyOnGoodbye() {
        Tally tally = makeTally();
        tally.getResponse("todo read book");
        assertFalse(tally.isExiting());
        assertTrue(tally.getResponse("bye").contains("Session ended."));
        assertTrue(tally.isExiting());
    }

    @Test
    public void isExiting_commandAfterGoodbye_staysTrue() {
        Tally tally = makeTally();
        assertTrue(tally.getResponse("bye").contains("Session ended."));
        assertTrue(tally.isExiting());
        // A front end that keeps taking input must not be told the conversation
        // resumed, nor be given the farewell a second time.
        String afterwards = tally.getResponse("list");
        assertTrue(tally.isExiting(), "the goodbye was forgotten by the next command");
        assertFalse(afterwards.contains("Session ended"), afterwards);
    }

    @Test
    public void getResponse_theSameTaskTwice_isRefusedAndNamesWhereItAlreadyIs() {
        Tally tally = makeTally();
        tally.getResponse("todo read book");
        String reply = tally.getResponse("todo read    book");
        assertTrue(reply.contains("Already on record as task 1"), reply);
        // Refusing it is only useful if the tally is left as it was.
        assertEquals(1, tally.getResponse("list").lines().count() - 1);
    }

    @Test
    public void getResponse_goodbyeWithTextAfterIt_doesNotEndTheConversation() {
        Tally tally = makeTally();
        // "bye now" used to be read as bye, ending the conversation and, on the console,
        // swallowing every command that followed it.
        assertTrue(tally.getResponse("bye now").contains("takes nothing after it"));
        assertFalse(tally.isExiting(), "a mistyped goodbye ended the conversation");
    }

    @Test
    public void getResponse_goodbyeTwice_isAnsweredBothTimes() {
        Tally tally = makeTally();
        assertTrue(tally.getResponse("bye").contains("Session ended."));
        // A command that asks to leave is answered, however many times it is asked.
        // The window stops taking input at the first one, so this is for any other
        // front end, and for the reader wondering whether the second is swallowed.
        assertTrue(tally.getResponse("bye").contains("Session ended."));
        assertTrue(tally.isExiting());
    }

    @Test
    public void getResponse_freeFromTheLastWritableDate_namesOnlyTheDaysItSearched() {
        // Only one day can be written down at all from there, so a reply naming a year
        // would be describing days the search never looked at and could not offer.
        String reply = makeTally().getResponse("free /for 2 /from 9999-12-31");
        assertTrue(reply.contains("from Dec 31 9999 to Dec 31 9999"), reply);
    }

    @Test
    public void getResponse_saveFails_doesNotAnnounceTheChangeFirst() throws IOException {
        Path file = folder.resolve("tally.txt");
        Files.writeString(file, "T | 0 | read book\n");
        assumeTrue(Files.getFileStore(file).supportsFileAttributeView(PosixFileAttributeView.class),
                "this file system does not carry POSIX permissions");
        Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("r--------"));
        assumeTrue(!Files.isWritable(file), "these tests are running as a user nothing stops");

        Tally tally = new Tally(file, false);
        String reply = tally.getResponse("todo write essay");

        // Saying "Got it" and then taking it back leaves the user unsure which happened.
        assertFalse(reply.contains("Recorded:"), "a failed save was announced as a success: " + reply);
        assertTrue(reply.contains("could not be saved"), reply);
        assertFalse(tally.getResponse("list").contains("write essay"),
                "the tally kept a change that never reached the file");
    }

    @Test
    public void getResponse_changes_areSavedForTheNextRun() {
        Path file = folder.resolve("tally.txt");
        new Tally(file, false).getResponse("todo read book");
        assertEquals("1.[T][ ] read book",
                new Tally(file, false).getResponse("list").lines().skip(1).findFirst().orElse(""));
    }

    @Test
    public void run_commandsTypedAtAConsole_areAnsweredUntilTheGoodbye() {
        String printed = runOnConsole("todo read book\nlist\nbye\n");
        assertTrue(printed.contains("Tally."), printed);
        assertTrue(printed.contains("Recorded:"), printed);
        assertTrue(printed.contains("On record:"), printed);
        assertTrue(printed.contains("1.[T][ ] read book"), printed);
        assertTrue(printed.contains("Session ended. Your tasks did not."), printed);
    }

    @Test
    public void run_inputEndingWithoutAGoodbye_stillSaysOne() {
        // Piping a file into Tally closes the input rather than typing bye.
        String printed = runOnConsole("todo read book\n");
        assertTrue(printed.contains("Session ended."), printed);
    }

    @Test
    public void run_aCommandItRefuses_reportsItAndCarriesOn() {
        String printed = runOnConsole("blah\nlist\nbye\n");
        assertTrue(printed.contains("Unknown command."), printed);
        assertTrue(printed.contains("Nothing on record."), "it stopped at the refusal: " + printed);
    }

    @Test
    public void getResponse_eachKindOfTask_isRecordedAndShown() {
        Tally tally = makeTally();
        assertTrue(tally.getResponse("todo read book").contains("[T][ ] read book"));
        assertTrue(tally.getResponse("deadline return book /by 2019-10-15")
                .contains("[D][ ] return book (by: Oct 15 2019)"));
        assertTrue(tally.getResponse("event project meeting /from 2019-08-06 /to 2019-08-07")
                .contains("[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)"));
        String fourth = tally.getResponse("window submit form /between 2026-09-08 /and 2026-09-12");
        assertTrue(fourth.contains("[W][ ] submit form (window: Sep 08 2026 to Sep 12 2026)"));
        assertTrue(fourth.contains("4 tasks on record."), fourth);
        assertEquals(5, tally.getResponse("list").lines().count(), "a heading and four tasks");
    }

    @Test
    public void getResponse_markThenUnmark_putsTheTaskBackAsItWas() {
        Tally tally = makeTally();
        tally.getResponse("todo read book");
        assertTrue(tally.getResponse("mark 1").contains("[T][X] read book"));
        String undone = tally.getResponse("unmark 1");
        assertTrue(undone.contains("Marked not done. As you were:"), undone);
        assertTrue(undone.contains("[T][ ] read book"), undone);
    }

    @Test
    public void getResponse_findingNothing_saysSoRatherThanShowingAnEmptyList() {
        Tally tally = makeTally();
        tally.getResponse("todo read book");
        assertTrue(tally.getResponse("find quidditch").startsWith("No match."));
    }

    @Test
    public void getResponse_free_readsForOneDayAndForARunOfThem() {
        Tally tally = makeTally();
        assertEquals("Next free day: Sep 09 2026.", tally.getResponse("free /from 2026-09-09"));
        assertEquals("Next 3 free days in a row begin Sep 09 2026.",
                tally.getResponse("free /for 3 /from 2026-09-09"));
    }

    @Test
    public void getResponse_everyDayTakenUp_saysThereIsNoneRatherThanSearchingOn() {
        Tally tally = makeTally();
        tally.getResponse("window busy /between 2026-09-09 /and 2027-09-09");
        String reply = tally.getResponse("free /from 2026-09-09");
        assertTrue(reply.startsWith("No free day from Sep 09 2026 to Sep 09 2027."), reply);
    }

    @Test
    public void getGreeting_dataFileThatCannotBeRead_complainsOnceAndStartsEmpty()
            throws IOException {
        Path file = folder.resolve("tally.txt");
        Files.writeString(file, "T | 0 | read book\n");
        assumeTrue(Files.getFileStore(file).supportsFileAttributeView(PosixFileAttributeView.class),
                "this file system does not carry POSIX permissions");
        Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("---------"));
        assumeTrue(!Files.isReadable(file), "these tests are running as a user nothing stops");

        Tally tally = new Tally(file, false);
        String greeting = tally.getGreeting();
        assertTrue(greeting.contains("could not be read"), greeting);
        assertTrue(tally.getResponse("list").contains("Nothing on record."));

        // Saving is refused, and reading it back to find out what is really there fails
        // for the same reason, so the change stays here and is said to be going nowhere.
        String reply = tally.getResponse("todo write essay");
        assertTrue(reply.contains("will not be written over"), reply);
        assertTrue(reply.contains("as you left it"), reply);
    }

    @Test
    public void main_givenADataFile_runsTheWholeProgramOverStandardInput() throws IOException {
        Path file = folder.resolve("tally.txt");
        InputStream realInput = System.in;
        PrintStream realOutput = System.out;
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(
                    "todo read book\nbye\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
            Tally.main(new String[] {file.toString()});
        } finally {
            System.setIn(realInput);
            System.setOut(realOutput);
        }
        assertTrue(printed.toString(StandardCharsets.UTF_8).contains("Recorded:"));
        assertEquals("T | 0 | read book", Files.readString(file).strip());
    }

    @Test
    public void getResponse_askedOfAConsoleTally_isRefusedRatherThanAnsweredEmpty() {
        // A console Tally prints as it goes, so there is nothing left to hand back. A
        // front end asking for one has been wired to the wrong kind.
        Tally console = new Tally(folder.resolve("tally.txt"), true);
        assertThrows(AssertionError.class, () -> console.getResponse("list"));
        assertThrows(AssertionError.class, console::getGreeting);
    }

    @Test
    public void getResponse_freeWithAnEventOnTheDay_looksPastIt() {
        // Every event names the days it runs across, so the free-day search sees the
        // whole tally and has nothing to disclaim. An event whose ends were free text
        // used to be stepped over, and the answer had to say it was drawn from less
        // than everything on record.
        Tally tally = makeTally();
        tally.getResponse("event standup /from 2026-09-09 /to 2026-09-09");
        String reply = tally.getResponse("free /from 2026-09-09");
        assertTrue(reply.contains("Next free day: Sep 10 2026."), reply);
        assertEquals(1, reply.lines().count(), reply);
    }

    @Test
    public void getResponse_saveFailsAndTheFileIsDamaged_reportsBothOnPuttingItBack()
            throws IOException {
        Path file = folder.resolve("tally.txt");
        Files.writeString(file, "T | 0 | read book\nBAD LINE\n");
        Tally tally = new Tally(file, false);
        String greeting = tally.getGreeting();
        assertTrue(greeting.contains("could not be read"), greeting);

        assumeTrue(Files.getFileStore(file).supportsFileAttributeView(PosixFileAttributeView.class),
                "this file system does not carry POSIX permissions");
        Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("r--------"));
        assumeTrue(!Files.isWritable(file), "these tests are running as a user nothing stops");

        // The save fails, so the file is read again to find out what is really there --
        // and that read finds the damage, which the user is told about along with it.
        String reply = tally.getResponse("todo write essay");
        assertTrue(reply.contains("could not be saved"), reply);
        assertTrue(reply.contains("put back"), reply);
        assertTrue(reply.contains("could not be read"), reply);
    }
}
