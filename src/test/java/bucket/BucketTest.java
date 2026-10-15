package bucket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * End-to-end tests for the chatbot: a line of text goes in, a reply comes out.
 *
 * These go through getResponse rather than the pieces underneath, so they cover
 * what a user actually experiences, including that a mistyped command produces a
 * message instead of an exception.
 *
 * Each test gets its own save file in a @TempDir, so the suite never reads or
 * writes the tasks belonging to whoever is using the app.
 */
public class BucketTest {

    @TempDir
    private Path tempDir;

    private Bucket bucket;

    @BeforeEach
    public void setUp() {
        bucket = new Bucket(tempDir.resolve("data").resolve("bucket.txt"));
    }

    /** Runs a line and returns just the text, for the many tests that ignore the flag. */
    private String say(String input) {
        return bucket.getResponse(input).text();
    }

    // ---------- adding tasks ----------

    @Test
    public void getResponse_todo_confirmsWithTheTask() {
        assertTrue(say("todo read book").contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_deadline_confirmsWithFormattedDate() {
        assertTrue(say("deadline return book /by 2019-10-15")
                .contains("[D][ ] return book (by: Oct 15 2019)"));
    }

    @Test
    public void getResponse_event_confirmsWithBothDates() {
        assertTrue(say("event meeting /from 2019-10-15 /to 2019-10-16")
                .contains("[E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)"));
    }

    @Test
    public void getResponse_addingThree_reportsRunningCount() {
        say("todo a");
        say("todo b");
        assertTrue(say("todo c").contains("3 tasks"));
    }

    @Test
    public void getResponse_addingTask_isNotAnError() {
        assertFalse(bucket.getResponse("todo read book").isError());
    }

    // ---------- listing ----------

    @Test
    public void getResponse_listWhenEmpty_saysSoRatherThanShowingNothing() {
        assertTrue(say("list").contains("nothing in your list"));
    }

    @Test
    public void getResponse_listWithTasks_numbersThemFromOne() {
        say("todo read book");
        assertTrue(say("list").contains("1.[T][ ] read book"));
    }

    // Listing orders by date, so the numbers shown are the sorted positions
    @Test
    public void getResponse_list_ordersByDateWithTodosLast() {
        say("todo no date");
        say("deadline later /by 2026-12-01");
        say("deadline sooner /by 2026-01-02");

        String listed = say("list");
        int sooner = listed.indexOf("sooner");
        int later = listed.indexOf("later");
        int noDate = listed.indexOf("no date");

        assertTrue(sooner < later, "earlier deadline should come first");
        assertTrue(later < noDate, "undated todo should come last");
    }

    // ---------- marking and unmarking ----------

    @Test
    public void getResponse_mark_showsTaskAsDone() {
        say("todo read book");
        assertTrue(say("mark 1").contains("[T][X] read book"));
    }

    @Test
    public void getResponse_unmark_putsTaskBackToNotDone() {
        say("todo read book");
        say("mark 1");
        assertTrue(say("unmark 1").contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_markThenList_showsTheCrossInTheList() {
        say("todo read book");
        say("mark 1");
        assertTrue(say("list").contains("1.[T][X] read book"));
    }

    // Marking something already done is harmless, not an error
    @Test
    public void getResponse_markTwice_staysDone() {
        say("todo read book");
        say("mark 1");
        assertTrue(say("mark 1").contains("[T][X] read book"));
    }

    @Test
    public void getResponse_unmarkSomethingNeverMarked_staysNotDone() {
        say("todo read book");
        assertTrue(say("unmark 1").contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_markNumberTooBig_reportsAnError() {
        say("todo read book");
        assertTrue(bucket.getResponse("mark 99").isError());
    }

    @Test
    public void getResponse_markZero_reportsAnError() {
        say("todo read book");
        assertTrue(bucket.getResponse("mark 0").isError());
    }

    @Test
    public void getResponse_markNegative_reportsAnError() {
        say("todo read book");
        assertTrue(bucket.getResponse("mark -1").isError());
    }

    @Test
    public void getResponse_markNotANumber_asksForANumber() {
        say("todo read book");
        assertTrue(say("mark abc").contains("task number"));
    }

    @Test
    public void getResponse_markOnEmptyList_reportsAnError() {
        assertTrue(bucket.getResponse("mark 1").isError());
    }

    // ---------- deleting ----------

    @Test
    public void getResponse_delete_confirmsAndReportsRemainingCount() {
        say("todo read book");
        say("todo buy milk");

        String reply = say("delete 1");

        assertTrue(reply.contains("removed"));
        assertTrue(reply.contains("1 tasks"));
    }

    @Test
    public void getResponse_deleteThenList_taskIsGone() {
        say("todo read book");
        say("delete 1");
        assertTrue(say("list").contains("nothing in your list"));
    }

    @Test
    public void getResponse_deleteNumberTooBig_reportsAnError() {
        say("todo read book");
        assertTrue(bucket.getResponse("delete 5").isError());
    }

    @Test
    public void getResponse_deleteFromEmptyList_reportsAnError() {
        assertTrue(bucket.getResponse("delete 1").isError());
    }

    // ---------- finding ----------

    @Test
    public void getResponse_findMatching_listsOnlyTheMatches() {
        say("todo read book");
        say("todo buy milk");

        String found = say("find book");

        assertTrue(found.contains("read book"));
        assertFalse(found.contains("buy milk"));
    }

    @Test
    public void getResponse_findDifferentCase_stillMatches() {
        say("todo read book");
        assertTrue(say("find BOOK").contains("read book"));
    }

    @Test
    public void getResponse_findNoMatch_saysSo() {
        say("todo read book");
        assertTrue(say("find umbrella").contains("No matching tasks"));
    }

    @Test
    public void getResponse_findWithNoKeyword_reportsAnError() {
        assertTrue(bucket.getResponse("find").isError());
    }

    // ---------- malformed dates, per task type ----------

    @Test
    public void getResponse_deadlineWithWordInsteadOfDate_explainsTheDateFormat() {
        assertTrue(say("deadline return book /by tomorrow").contains("2019-10-15"));
    }

    @Test
    public void getResponse_deadlineWithImpossibleDate_reportsAnError() {
        assertTrue(bucket.getResponse("deadline x /by 2019-02-29").isError());
    }

    @Test
    public void getResponse_deadlineMissingByMarker_saysWhatIsMissing() {
        assertTrue(say("deadline return book 2019-10-15").contains("missing a part"));
    }

    @Test
    public void getResponse_eventWithBadStartDate_reportsAnError() {
        assertTrue(bucket.getResponse("event meeting /from someday /to 2019-10-16").isError());
    }

    @Test
    public void getResponse_eventWithBadEndDate_reportsAnError() {
        assertTrue(bucket.getResponse("event meeting /from 2019-10-15 /to someday").isError());
    }

    @Test
    public void getResponse_eventMissingToMarker_reportsAnError() {
        assertTrue(bucket.getResponse("event meeting /from 2019-10-15").isError());
    }

    // A rejected command must not leave a half-built task behind
    @Test
    public void getResponse_rejectedDeadline_doesNotAddAnything() {
        say("deadline broken /by not-a-date");
        assertTrue(say("list").contains("nothing in your list"));
    }

    // ---------- empty and unknown input ----------

    @Test
    public void getResponse_todoWithNoDescription_reportsAnError() {
        assertTrue(bucket.getResponse("todo").isError());
    }

    @Test
    public void getResponse_unknownCommand_saysItDoesNotUnderstand() {
        assertTrue(say("blah").contains("don't know what that means"));
    }

    @Test
    public void getResponse_unknownCommand_isFlaggedAsAnError() {
        assertTrue(bucket.getResponse("blah").isError());
    }

    @Test
    public void getResponse_emptyLine_reportsAnErrorRatherThanThrowing() {
        assertTrue(bucket.getResponse("").isError());
    }

    // Commands are matched in lower case, so a capitalised one is not recognised
    @Test
    public void getResponse_capitalisedCommand_notRecognised() {
        assertTrue(bucket.getResponse("TODO read book").isError());
    }

    // ---------- saying goodbye ----------

    @Test
    public void getResponse_bye_saysGoodbye() {
        assertTrue(say("bye").contains("BYE"));
    }

    @Test
    public void isExitCommand_bye_isTrue() {
        assertTrue(Bucket.isExitCommand("bye"));
    }

    // Leaving is common enough that case and stray spaces should not stop it
    @Test
    public void isExitCommand_mixedCaseAndSpaces_isTrue() {
        assertTrue(Bucket.isExitCommand("  ByE  "));
    }

    @Test
    public void isExitCommand_otherCommand_isFalse() {
        assertFalse(Bucket.isExitCommand("list"));
    }

    // ---------- carrying over between runs ----------

    @Test
    public void getResponse_tasksSurviveANewBucketOnTheSameFile() {
        Path file = tempDir.resolve("data").resolve("bucket.txt");
        Bucket first = new Bucket(file);
        first.getResponse("todo read book");
        first.getResponse("mark 1");

        Bucket second = new Bucket(file);

        assertTrue(second.getResponse("list").text().contains("[T][X] read book"));
    }

    @Test
    public void getWelcomeMessage_noSavedTasks_saysThereAreNone() {
        assertTrue(bucket.getWelcomeMessage().contains("No tasks found"));
    }

    @Test
    public void getWelcomeMessage_withSavedTasks_listsThem() {
        Path file = tempDir.resolve("data").resolve("bucket.txt");
        new Bucket(file).getResponse("todo read book");

        assertTrue(new Bucket(file).getWelcomeMessage().contains("read book"));
    }

    // ---------- non-ASCII input ----------

    @Test
    public void getResponse_chineseDescription_storedAndListedUnchanged() {
        say("todo 读一本书");
        assertEquals(true, say("list").contains("读一本书"));
    }
}
