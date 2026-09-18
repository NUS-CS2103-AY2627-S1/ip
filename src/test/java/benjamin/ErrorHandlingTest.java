package benjamin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import benjamin.parser.CommandType;
import benjamin.parser.Parser;
import benjamin.task.Deadline;
import benjamin.task.Event;
import benjamin.task.TaskDateTime;
import benjamin.task.Todo;

/**
 * Regression tests for the error cases fixed in the A-MoreErrorHandling increment.
 *
 * <p>Each test here corresponds to something the app previously accepted and
 * either mishandled silently or reported with a misleading message.
 */
public class ErrorHandlingTest {
    @TempDir
    Path root;

    private Benjamin chatbot() {
        return new Benjamin(root.toString(), "data", "benjamin.txt");
    }

    // --- dates that do not exist were silently moved to a nearby real date ---

    @Test
    public void parse_februaryThirtieth_rejectedInsteadOfMovedToTheTwentyEighth() {
        assertThrows(BenjaminException.class, () -> TaskDateTime.parse("2019-02-30"));
    }

    @Test
    public void parse_februaryTwentyNinthInNonLeapYear_rejected() {
        assertThrows(BenjaminException.class, () -> TaskDateTime.parse("2019-02-29"));
    }

    @Test
    public void parse_februaryTwentyNinthInLeapYear_accepted() throws BenjaminException {
        assertEquals("Feb 29 2020", TaskDateTime.parse("2020-02-29").toString());
    }

    @Test
    public void parse_thirtyFirstOfApril_rejected() {
        assertThrows(BenjaminException.class, () -> TaskDateTime.parse("31/4/2019"));
        assertThrows(BenjaminException.class, () -> TaskDateTime.parse("2019-04-31"));
    }

    @Test
    public void parse_monthThirteen_rejected() {
        assertThrows(BenjaminException.class, () -> TaskDateTime.parse("2019-13-01"));
    }

    // --- an event that finishes before, or when, it starts ---

    @Test
    public void newEvent_endsBeforeItStarts_exceptionThrown() throws BenjaminException {
        TaskDateTime from = TaskDateTime.parse("2019-08-08");
        TaskDateTime to = TaskDateTime.parse("2019-08-06");

        BenjaminException exception = assertThrows(BenjaminException.class, () ->
                new Event("backwards", from, to));
        assertTrue(exception.getMessage().contains("finish after it starts"));
    }

    @Test
    public void newEvent_startsAndEndsAtTheSameMoment_exceptionThrown() throws BenjaminException {
        TaskDateTime moment = TaskDateTime.parse("2019-08-06 1400");

        assertThrows(BenjaminException.class, () -> new Event("instant", moment, moment));
    }

    @Test
    public void newEvent_endsAfterItStarts_accepted() throws BenjaminException {
        Event event = new Event("camp",
                TaskDateTime.parse("2019-08-06"),
                TaskDateTime.parse("2019-08-08"));

        assertTrue(event.toString().contains("camp"));
    }

    // --- the same task added twice ---

    @Test
    public void isDuplicateOf_sameDescription_returnsTrue() {
        assertTrue(new Todo("read book").isDuplicateOf(new Todo("read book")));
    }

    @Test
    public void isDuplicateOf_differentCaseOrSpacing_stillDuplicate() {
        assertTrue(new Todo("read book").isDuplicateOf(new Todo("  READ BOOK  ")));
    }

    @Test
    public void isDuplicateOf_completionAndTagsIgnored() {
        Todo first = new Todo("read book");
        first.markAsDone();
        first.addTag("fun");

        assertTrue(first.isDuplicateOf(new Todo("read book")));
    }

    @Test
    public void isDuplicateOf_differentType_returnsFalse() throws BenjaminException {
        Deadline deadline = new Deadline("read book", TaskDateTime.parse("2019-10-15"));

        assertFalse(new Todo("read book").isDuplicateOf(deadline));
    }

    @Test
    public void isDuplicateOf_sameDescriptionDifferentDate_returnsFalse() throws BenjaminException {
        Deadline first = new Deadline("essay", TaskDateTime.parse("2019-10-15"));
        Deadline second = new Deadline("essay", TaskDateTime.parse("2019-10-16"));

        assertFalse(first.isDuplicateOf(second));
    }

    @Test
    public void getResponse_duplicateTask_refusedWithAnExplanation() {
        Benjamin benjamin = chatbot();
        benjamin.getResponse("todo read book");

        String reply = benjamin.getResponse("todo read book");

        assertTrue(reply.contains("already have that exact task"));
        assertFalse(benjamin.getResponse("list").contains("2."));
    }

    // --- a description holding the save file separator corrupted the file ---

    @Test
    public void parseTask_pipeInTodoDescription_rejected() {
        BenjaminException exception = assertThrows(BenjaminException.class, () ->
                Parser.parseTask("todo x | y", CommandType.TODO));
        assertTrue(exception.getMessage().contains("| character"));
    }

    @Test
    public void parseTask_pipeInDeadlineDescription_rejected() {
        assertThrows(BenjaminException.class, () ->
                Parser.parseTask("deadline a | b /by 2019-10-15", CommandType.DEADLINE));
    }

    @Test
    public void getResponse_pipeInDescription_neverReachesTheSaveFile() {
        Benjamin benjamin = chatbot();
        benjamin.getResponse("todo x | y");

        // Before this was rejected, the task saved as "T | 0 | x | y" and came
        // back as the task "x" carrying a tag "y".
        assertFalse(chatbot().getResponse("list").contains("x"));
    }

    // --- a marker given more than once ---

    @Test
    public void parseTask_byGivenTwice_saysSoInsteadOfBlamingTheDate() {
        BenjaminException exception = assertThrows(BenjaminException.class, () ->
                Parser.parseTask("deadline f /by 2019-10-15 /by 2019-10-16",
                        CommandType.DEADLINE));
        assertTrue(exception.getMessage().contains("/by more than once"));
    }

    @Test
    public void parseTask_fromGivenTwice_rejected() {
        BenjaminException exception = assertThrows(BenjaminException.class, () ->
                Parser.parseTask("event g /from 2019-01-01 /from 2019-01-02 /to 2019-01-03",
                        CommandType.EVENT));
        assertTrue(exception.getMessage().contains("/from more than once"));
    }

    @Test
    public void parseTask_toGivenTwice_rejected() {
        assertThrows(BenjaminException.class, () ->
                Parser.parseTask("event g /from 2019-01-01 /to 2019-01-02 /to 2019-01-03",
                        CommandType.EVENT));
    }

    // --- task numbers ---

    @Test
    public void parseTaskNumber_twoNumbersGiven_asksForJustOne() {
        BenjaminException exception = assertThrows(BenjaminException.class, () ->
                Parser.parseTaskNumber("delete 1 2", "delete"));
        assertTrue(exception.getMessage().contains("just one task number"));
    }

    @Test
    public void parseTaskNumber_numberTooLargeForAnInt_saysItIsTooLarge() {
        BenjaminException exception = assertThrows(BenjaminException.class, () ->
                Parser.parseTaskNumber("mark 99999999999", "mark"));
        assertTrue(exception.getMessage().contains("far too large"));
    }

    @Test
    public void parseTaskNumber_surroundingSpaces_stillAccepted() throws BenjaminException {
        assertEquals(1, Parser.parseTaskNumber("mark    1   ", "mark"));
    }
}
