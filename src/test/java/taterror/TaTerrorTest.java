package taterror;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TaTerror#getResponse(String)}.
 *
 * <p>Deliberately limited to inputs that do NOT trigger {@code Storage.save()}
 * (i.e. no successful todo/deadline/event/mark/unmark/delete), so running these
 * tests never overwrites the real {@code data/tasks.txt} save file. Each test
 * targets a distinct validation/parsing branch inside {@code getResponse}, not
 * just the happy path.
 */
public class TaTerrorTest {

    @Test
    public void getResponse_byeCommand_returnsOneOfTheKnownFarewellMessages() {
        TaTerror taTerror = new TaTerror();
        assertTrue(TaTerror.BYE_RESPONSES.contains(taTerror.getResponse("bye")));
    }

    @Test
    public void getResponse_unrecognizedCommand_returnsOneOfTheKnownErrorMessages() {
        TaTerror taTerror = new TaTerror();
        assertTrue(TaTerror.UNKNOWN_COMMAND_RESPONSES.contains(taTerror.getResponse("blahblahblah")));
    }

    @Test
    public void getResponse_markWithNonNumericIndex_returnsNumberFormatError() {
        TaTerror taTerror = new TaTerror();
        // "abc" isn't a number, so Integer.parseInt throws before any task is touched.
        assertEquals("OOPS!!! That's not even a number. Are you okay?", taTerror.getResponse("mark abc"));
    }

    @Test
    public void getResponse_markWithOutOfRangeIndex_returnsRangeError() {
        TaTerror taTerror = new TaTerror();
        // Index is numeric but absurdly out of range, so no task is actually marked/saved.
        assertEquals("OOPS!!! That task number doesn't even exist. Try again.", taTerror.getResponse("mark 99999"));
    }

    @Test
    public void getResponse_todoWithoutDescription_returnsValidationErrorAndDoesNotAddTask() {
        TaTerror taTerror = new TaTerror();
        assertEquals(
                "OOPS!!! A todo needs an actual description. Use your words.", taTerror.getResponse("todo"));
    }

    @Test
    public void getResponse_deadlineWithoutByClause_returnsValidationError() {
        TaTerror taTerror = new TaTerror();
        assertEquals(
                "OOPS!!! A deadline needs a description AND a '/by' date (e.g. 2019-10-15).",
                taTerror.getResponse("deadline return book"));
    }

    @Test
    public void getResponse_eventWithoutFromOrTo_returnsValidationError() {
        TaTerror taTerror = new TaTerror();
        assertEquals(
                "OOPS!!! An event needs '/from' and '/to' details. Don't skip steps.",
                taTerror.getResponse("event project meeting"));
    }

    @Test
    public void getResponse_listCommand_startsWithExpectedHeader() {
        TaTerror taTerror = new TaTerror();
        // Loose/structural assertion on purpose: the exact task contents depend on
        // whatever is currently saved in data/tasks.txt, which this test must not
        // assume or depend on.
        String response = taTerror.getResponse("list");
        assertTrue(response.startsWith("Here are the tasks in your list:"));
    }

    @Test
    public void getResponse_findWithoutKeyword_returnsValidationError() {
        TaTerror taTerror = new TaTerror();
        assertEquals("OOPS!!! Find what, exactly? Give me a keyword.", taTerror.getResponse("find"));
    }

    @Test
    public void getResponse_priorityWithoutLevel_returnsUsageError() {
        TaTerror taTerror = new TaTerror();
        assertEquals(
                "OOPS!!! Usage: priority <task number> <none|low|medium|high>.",
                taTerror.getResponse("priority 1"));
    }

    @Test
    public void getResponse_priorityWithOutOfRangeIndex_returnsRangeError() {
        TaTerror taTerror = new TaTerror();
        // Index is numeric but absurdly out of range, so no task is actually touched/saved.
        assertEquals(
                "OOPS!!! That task number doesn't even exist. Try again.",
                taTerror.getResponse("priority 99999 high"));
    }

    @Test
    public void getResponse_todoWithInvalidPriorityFlag_returnsValidationErrorAndDoesNotAddTask() {
        TaTerror taTerror = new TaTerror();
        assertEquals(
                "OOPS!!! That's not a real priority. Try none, low, medium, or high.",
                taTerror.getResponse("todo read book /priority urgent"));
    }

    @Test
    public void getResponse_markWithTrailingSpace_treatedSameAsWithout() {
        TaTerror taTerror = new TaTerror();
        // Out-of-range on purpose so nothing is marked/saved; the point is that a
        // trailing space no longer produces the "not even a number" error.
        assertEquals(
                "OOPS!!! That task number doesn't even exist. Try again.",
                taTerror.getResponse("mark 99999 "));
    }

    @Test
    public void getResponse_byeWithSurroundingWhitespace_stillRecognizedAsBye() {
        TaTerror taTerror = new TaTerror();
        assertTrue(TaTerror.BYE_RESPONSES.contains(taTerror.getResponse("  bye  ")));
    }

    @Test
    public void isExitCommand_byeWithWhitespace_returnsTrue() {
        TaTerror taTerror = new TaTerror();
        assertTrue(taTerror.isExitCommand("bye"));
        assertTrue(taTerror.isExitCommand(" bye "));
    }

    @Test
    public void isExitCommand_otherInput_returnsFalse() {
        TaTerror taTerror = new TaTerror();
        assertTrue(!taTerror.isExitCommand("list"));
        assertTrue(!taTerror.isExitCommand("goodbye"));
    }
}
