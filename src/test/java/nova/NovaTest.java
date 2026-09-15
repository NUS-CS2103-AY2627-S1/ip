package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Covers command dispatch and the state changes each command causes. Wording
 * that Nova borrows from Ui is asserted in UiTest instead, so a reworded
 * message breaks one test rather than thirty.
 */
public class NovaTest {

    // Every Nova built here is pointed at a throwaway file, so no test can
    // read or overwrite the real data/nova.txt.
    @TempDir
    Path tempDir;

    private Path dataFile() {
        return tempDir.resolve("nova.txt");
    }

    /** Returns a chatbot whose save file starts out holding the given lines. */
    private Nova novaWith(String... dataLines) throws IOException {
        Files.write(dataFile(), List.of(dataLines));
        return new Nova(dataFile().toString());
    }

    /** Returns a chatbot whose save file does not exist yet. */
    private Nova freshNova() {
        return new Nova(dataFile().toString());
    }

    // ---------------------------------------------------------------- //
    // Dispatch                                                          //
    // ---------------------------------------------------------------- //

    @Test
    public void getResponse_emptyInput_asksForSomeInput() {
        assertEquals("Type something!", freshNova().getResponse(""));
    }

    @Test
    public void getResponse_whitespaceOnly_asksForSomeInput() {
        assertEquals("Type something!", freshNova().getResponse("     "));
    }

    @Test
    public void getResponse_unrecognisedCommand_listsTheValidOnes() {
        String response = freshNova().getResponse("sing");

        assertTrue(response.startsWith("Input valid command"));
        assertTrue(response.contains("todo"));
        assertTrue(response.contains("delete"));
    }

    @Test
    public void getResponse_commandWordInAnyCase_isStillRecognised() {
        Nova nova = freshNova();
        nova.getResponse("ToDo read book");

        assertTrue(nova.getResponse("LIST").contains("read book"));
        assertTrue(nova.getResponse("LiSt").contains("read book"));
    }

    @Test
    public void getResponse_surroundingWhitespace_isIgnored() {
        Nova nova = freshNova();

        assertTrue(nova.getResponse("   todo read book   ").contains("read book"));
        assertTrue(nova.getResponse("list").contains("read book"));
    }

    @Test
    public void getResponse_bye_returnsTheFarewell() {
        assertEquals(new Ui().getFarewellMessage(), freshNova().getResponse("bye"));
    }

    @Test
    public void getGreeting_returnsTheGreetingWithNoBanner() {
        String greeting = freshNova().getGreeting();

        assertTrue(greeting.contains("Nova"));
        assertFalse(greeting.contains("_"));
    }

    // ---------------------------------------------------------------- //
    // list                                                              //
    // ---------------------------------------------------------------- //

    @Test
    public void list_nothingStored_saysTheListIsEmpty() {
        assertEquals("Your list is empty! Add something.", freshNova().getResponse("list"));
    }

    @Test
    public void list_severalTasks_numbersThemFromOne() throws IOException {
        Nova nova = novaWith("T | 0 | read book", "T | 1 | join club");

        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] read book\n"
                + "2.[T][X] join club", nova.getResponse("list"));
    }

    // ---------------------------------------------------------------- //
    // todo                                                              //
    // ---------------------------------------------------------------- //

    @Test
    public void todo_blankDescription_returnsUsageHint() {
        assertTrue(freshNova().getResponse("todo").startsWith("Pls specify"));
        assertTrue(freshNova().getResponse("todo    ").startsWith("Pls specify"));
    }

    @Test
    public void todo_validDescription_appearsInTheList() {
        Nova nova = freshNova();
        nova.getResponse("todo read book");

        assertTrue(nova.getResponse("list").contains("[T][ ] read book"));
    }

    @Test
    public void todo_validDescription_reportsTheNewCount() {
        Nova nova = freshNova();

        assertTrue(nova.getResponse("todo one").contains("1 task in the list"));
        assertTrue(nova.getResponse("todo two").contains("2 tasks in the list"));
    }

    // ---------------------------------------------------------------- //
    // deadline                                                          //
    // ---------------------------------------------------------------- //

    @Test
    public void deadline_missingByMarker_returnsUsageHint() {
        assertEquals("Use: deadline <task name> /by <end>",
                freshNova().getResponse("deadline return book"));
    }

    @Test
    public void deadline_blankDescription_returnsUsageHint() {
        assertEquals("Use: deadline <task name> /by <end>",
                freshNova().getResponse("deadline /by 2019-10-15"));
    }

    @Test
    public void deadline_nothingAfterTheMarker_returnsUsageHint() {
        assertEquals("Use: deadline <task name> /by <end>",
                freshNova().getResponse("deadline return book /by"));
    }

    @Test
    public void deadline_unparseableDate_reportsTheDateProblem() {
        String response = freshNova().getResponse("deadline return book /by next tuesday");

        assertTrue(response.startsWith("I couldn't understand that date."));
        assertTrue(response.contains(Parser.DATE_FORMAT_HINT));
    }

    @Test
    public void deadline_dateOnly_isStoredAndDisplayedWithoutATime() {
        Nova nova = freshNova();
        nova.getResponse("deadline return book /by 2019-10-15");

        assertTrue(nova.getResponse("list").contains("[D][ ] return book (by: Oct 15 2019)"));
    }

    @Test
    public void deadline_dateAndTime_keepsTheTime() {
        Nova nova = freshNova();
        nova.getResponse("deadline return book /by 2019-10-15 1800");

        assertTrue(nova.getResponse("list")
                .contains("[D][ ] return book (by: Oct 15 2019, 6:00PM)"));
    }

    // ---------------------------------------------------------------- //
    // event                                                             //
    // ---------------------------------------------------------------- //

    @Test
    public void event_missingFromMarker_returnsUsageHint() {
        assertEquals("Use: event <task name> /from <start> /to <end>",
                freshNova().getResponse("event meeting /to 2026-01-05"));
    }

    @Test
    public void event_missingToMarker_returnsUsageHint() {
        assertEquals("Use: event <task name> /from <start> /to <end>",
                freshNova().getResponse("event meeting /from 2026-01-05"));
    }

    @Test
    public void event_unparseableDate_reportsTheDateProblem() {
        assertTrue(freshNova().getResponse("event meeting /from sometime /to later")
                .startsWith("I couldn't understand that date."));
    }

    @Test
    public void event_endBeforeStart_isRefused() {
        assertEquals("An event cannot end before it starts.",
                freshNova().getResponse("event oops /from 2026-01-06 /to 2026-01-05"));
    }

    @Test
    public void event_endBeforeStart_isRefusedEvenWhenForced() {
        // A backwards range is invalid rather than merely inconvenient, so
        // there is nothing for /force to override.
        assertEquals("An event cannot end before it starts.",
                freshNova().getResponse("event oops /from 2026-01-06 /to 2026-01-05 /force"));
    }

    @Test
    public void event_notOverlappingAnything_isAdded() {
        Nova nova = freshNova();
        nova.getResponse("event standup /from 2026-01-05 0900 /to 2026-01-05 0930");

        String response = nova.getResponse("event lunch /from 2026-01-05 1200 /to 2026-01-05 1300");

        assertTrue(response.contains("Got it"));
        assertTrue(nova.getResponse("list").contains("lunch"));
    }

    @Test
    public void event_touchingAnExistingEvent_isAdded() {
        Nova nova = freshNova();
        nova.getResponse("event standup /from 2026-01-05 0900 /to 2026-01-05 1000");

        // Back-to-back scheduling is normal, so ending exactly when the next
        // one begins is not a clash.
        String response = nova.getResponse("event review /from 2026-01-05 1000 /to 2026-01-05 1100");

        assertTrue(response.contains("Got it"));
    }

    @Test
    public void event_overlappingAnExistingEvent_isRefusedAndTheClashIsListed() {
        Nova nova = freshNova();
        nova.getResponse("event standup /from 2026-01-05 0900 /to 2026-01-05 0930");

        String response = nova.getResponse("event review /from 2026-01-05 0915 /to 2026-01-05 1000");

        assertTrue(response.startsWith("That clashes with:"));
        assertTrue(response.contains("standup"));
        assertTrue(response.contains(Parser.FORCE_MARKER));
        assertFalse(nova.getResponse("list").contains("review"));
    }

    @Test
    public void event_overlappingButForced_isAdded() {
        Nova nova = freshNova();
        nova.getResponse("event standup /from 2026-01-05 0900 /to 2026-01-05 0930");

        String response =
                nova.getResponse("event review /from 2026-01-05 0915 /to 2026-01-05 1000 /force");

        assertTrue(response.contains("Got it"));
        assertTrue(nova.getResponse("list").contains("review"));
    }

    @Test
    public void event_overlappingAnEventAlreadyDone_isAdded() throws IOException {
        // Finished work no longer occupies the time.
        Nova nova = novaWith("E | 1 | standup | 2026-01-05T09:00 | 2026-01-05T09:30");

        String response = nova.getResponse("event review /from 2026-01-05 0915 /to 2026-01-05 1000");

        assertTrue(response.contains("Got it"));
    }

    @Test
    public void event_overlappingADeadlineOrTodo_isAdded() throws IOException {
        // Only events have a span, so only events can clash.
        Nova nova = novaWith(
                "T | 0 | read book",
                "D | 0 | report | 2026-01-05T09:15");

        assertTrue(nova.getResponse("event review /from 2026-01-05 0900 /to 2026-01-05 1000")
                .contains("Got it"));
    }

    @Test
    public void forceMarker_onATodo_isRejected() {
        String response = freshNova().getResponse("todo buy milk /force");

        assertTrue(response.startsWith(Parser.FORCE_MARKER));
        assertTrue(response.contains("only applies to events"));
    }

    @Test
    public void forceMarker_onADeadline_isRejected() {
        assertTrue(freshNova().getResponse("deadline report /by 2026-01-05 /force")
                .contains("only applies to events"));
    }

    @Test
    public void forceMarker_notAtTheEnd_isTreatedAsOrdinaryText() {
        Nova nova = freshNova();

        // Only a trailing marker counts, so this is just a description.
        assertTrue(nova.getResponse("todo /force the issue").contains("Got it"));
        assertTrue(nova.getResponse("list").contains("/force the issue"));
    }

    // ---------------------------------------------------------------- //
    // mark and unmark                                                   //
    // ---------------------------------------------------------------- //

    @Test
    public void mark_nonNumericArgument_isRejected() {
        assertTrue(freshNova().getResponse("mark two").startsWith("Invalid argument!"));
    }

    @Test
    public void mark_noArgument_isRejected() {
        assertTrue(freshNova().getResponse("mark").startsWith("Invalid argument!"));
    }

    @Test
    public void mark_numberPastTheEndOfTheList_isRejected() throws IOException {
        Nova nova = novaWith("T | 0 | read book");

        assertTrue(nova.getResponse("mark 2").startsWith("The number you have entered"));
    }

    @Test
    public void mark_zeroOrNegative_isRejected() throws IOException {
        Nova nova = novaWith("T | 0 | read book");

        assertTrue(nova.getResponse("mark 0").startsWith("The number you have entered"));
        assertTrue(nova.getResponse("mark -1").startsWith("The number you have entered"));
    }

    @Test
    public void mark_validNumber_setsTheTaskDone() throws IOException {
        Nova nova = novaWith("T | 0 | read book");

        assertTrue(nova.getResponse("mark 1").contains("[T][X] read book"));
        assertTrue(nova.getResponse("list").contains("[T][X] read book"));
    }

    @Test
    public void mark_aTaskAlreadyDone_leavesItDone() throws IOException {
        Nova nova = novaWith("T | 1 | read book");

        assertTrue(nova.getResponse("mark 1").contains("[T][X] read book"));
    }

    @Test
    public void unmark_validNumber_clearsTheDoneStatus() throws IOException {
        Nova nova = novaWith("T | 1 | read book");

        assertTrue(nova.getResponse("unmark 1").contains("[T][ ] read book"));
        assertTrue(nova.getResponse("list").contains("[T][ ] read book"));
    }

    @Test
    public void unmark_numberPastTheEndOfTheList_isRejected() {
        assertTrue(freshNova().getResponse("unmark 1").startsWith("The number you have entered"));
    }

    // ---------------------------------------------------------------- //
    // delete                                                            //
    // ---------------------------------------------------------------- //

    @Test
    public void delete_noArgument_isRejected() {
        assertEquals("Pls specify which task to delete!", freshNova().getResponse("delete"));
    }

    @Test
    public void delete_nonNumericArgument_isRejected() {
        assertEquals("Specify a number after the command delete",
                freshNova().getResponse("delete two"));
    }

    @Test
    public void delete_numberPastTheEndOfTheList_isRejected() throws IOException {
        Nova nova = novaWith("T | 0 | read book");

        assertEquals("Specify a valid task number!", nova.getResponse("delete 2"));
        assertEquals("Specify a valid task number!", nova.getResponse("delete 0"));
    }

    @Test
    public void delete_validNumber_removesThatTaskAndRenumbersTheRest() throws IOException {
        Nova nova = novaWith("T | 0 | first", "T | 0 | second", "T | 0 | third");

        String response = nova.getResponse("delete 2");

        assertTrue(response.contains("second"));
        assertTrue(response.contains("2 tasks in the list"));
        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] first\n"
                + "2.[T][ ] third", nova.getResponse("list"));
    }

    @Test
    public void delete_theLastRemainingTask_leavesAnEmptyList() throws IOException {
        Nova nova = novaWith("T | 0 | only one");

        assertTrue(nova.getResponse("delete 1").contains("0 tasks in the list"));
        assertEquals("Your list is empty! Add something.", nova.getResponse("list"));
    }

    // ---------------------------------------------------------------- //
    // find                                                              //
    // ---------------------------------------------------------------- //

    @Test
    public void find_noKeyword_asksForOne() {
        assertTrue(freshNova().getResponse("find").startsWith("Pls specify a keyword"));
    }

    @Test
    public void find_noMatches_saysSoAndQuotesTheKeyword() throws IOException {
        Nova nova = novaWith("T | 0 | read book");

        assertEquals("No tasks in your list mention \"holiday\".", nova.getResponse("find holiday"));
    }

    @Test
    public void find_matches_areNumberedFromOneNotByListPosition() throws IOException {
        Nova nova = novaWith("T | 0 | join club", "T | 0 | read book", "T | 0 | return book");

        assertEquals("Here are the matching tasks in your list:\n"
                + "1.[T][ ] read book\n"
                + "2.[T][ ] return book", nova.getResponse("find book"));
    }

    @Test
    public void find_differentCase_stillMatches() throws IOException {
        Nova nova = novaWith("T | 0 | read Book");

        assertTrue(nova.getResponse("find book").contains("read Book"));
        assertTrue(nova.getResponse("find BOOK").contains("read Book"));
    }

    @Test
    public void find_matchingTasks_leavesTheRealListAlone() throws IOException {
        Nova nova = novaWith("T | 0 | join club", "T | 0 | read book");
        nova.getResponse("find book");

        assertTrue(nova.getResponse("list").contains("join club"));
    }

    // ---------------------------------------------------------------- //
    // Persistence                                                       //
    // ---------------------------------------------------------------- //

    @Test
    public void addTask_thenReloading_findsTheTaskStillThere() {
        freshNova().getResponse("todo read book");

        assertTrue(new Nova(dataFile().toString()).getResponse("list").contains("read book"));
    }

    @Test
    public void mark_thenReloading_findsTheStatusStillSet() throws IOException {
        Nova nova = novaWith("T | 0 | read book");
        nova.getResponse("mark 1");

        assertTrue(new Nova(dataFile().toString()).getResponse("list").contains("[T][X] read book"));
    }

    @Test
    public void delete_thenReloading_findsTheTaskGone() throws IOException {
        Nova nova = novaWith("T | 0 | first", "T | 0 | second");
        nova.getResponse("delete 1");

        String reloaded = new Nova(dataFile().toString()).getResponse("list");

        assertFalse(reloaded.contains("first"));
        assertTrue(reloaded.contains("second"));
    }

    @Test
    public void everyTaskType_survivesAReload() {
        Nova nova = freshNova();
        nova.getResponse("todo read book");
        nova.getResponse("deadline return book /by 2019-10-15 1800");
        nova.getResponse("event meeting /from 2019-10-15 1400 /to 2019-10-15 1600");

        assertEquals(nova.getResponse("list"), new Nova(dataFile().toString()).getResponse("list"));
    }

    // ---------------------------------------------------------------- //
    // Startup notices                                                   //
    // ---------------------------------------------------------------- //

    @Test
    public void getStartupNotices_noSaveFileYet_hasNothingToReport() {
        assertTrue(freshNova().getStartupNotices().isEmpty());
    }

    @Test
    public void getStartupNotices_cleanSaveFile_hasNothingToReport() throws IOException {
        assertTrue(novaWith("T | 0 | read book").getStartupNotices().isEmpty());
    }

    @Test
    public void getStartupNotices_unreadableLines_reportsHowManyAndWhere() throws IOException {
        Nova nova = novaWith("T | 0 | read book", "garbage", "more garbage");

        List<String> notices = nova.getStartupNotices();

        assertEquals(1, notices.size());
        assertTrue(notices.get(0).contains("Skipped 2 unreadable line(s)"));
        assertTrue(notices.get(0).contains(dataFile().toString()));
    }

    @Test
    public void getStartupNotices_savedEventsThatClash_reportsThePairCount() throws IOException {
        Nova nova = novaWith(
                "E | 0 | orientation | 2026-01-05T14:00 | 2026-01-05T16:00",
                "E | 0 | briefing | 2026-01-05T15:00 | 2026-01-05T17:00");

        List<String> notices = nova.getStartupNotices();

        assertEquals(1, notices.size());
        assertTrue(notices.get(0).contains("1 pair"));
    }

    @Test
    public void getStartupNotices_aBackwardsSavedEvent_isCountedAsCorrupt() throws IOException {
        Nova nova = novaWith("E | 0 | oops | 2026-01-06T00:00 | 2026-01-05T00:00");

        assertTrue(nova.getStartupNotices().get(0).contains("Skipped 1 unreadable line(s)"));
        assertEquals("Your list is empty! Add something.", nova.getResponse("list"));
    }

    @Test
    public void getStartupNotices_unreadableFile_reportsTheLoadFailure() throws IOException {
        Path directory = Files.createDirectory(tempDir.resolve("nova.txt"));
        Nova nova = new Nova(directory.toString());

        List<String> notices = nova.getStartupNotices();

        assertEquals(1, notices.size());
        assertTrue(notices.get(0).startsWith("Could not read your saved tasks:"));
        assertTrue(notices.get(0).contains("saving is off"));
    }

    @Test
    public void unreadableFile_addingATask_worksButDoesNotOverwriteTheFile() throws IOException {
        Path directory = Files.createDirectory(tempDir.resolve("nova.txt"));
        Nova nova = new Nova(directory.toString());

        // Saving is switched off for the session, so the file we failed to
        // read is never replaced by a list that does not contain its contents.
        assertTrue(nova.getResponse("todo read book").contains("Got it"));
        assertTrue(Files.isDirectory(directory));
        assertEquals(0, directory.toFile().list().length);
    }
}
