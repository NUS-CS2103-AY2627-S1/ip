package thomas.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import thomas.ThomasException;
import thomas.command.AddCommand;
import thomas.command.DeleteCommand;
import thomas.command.ExitCommand;
import thomas.command.FindCommand;
import thomas.command.ListCommand;
import thomas.command.MarkCommand;
import thomas.command.OnCommand;
import thomas.command.UnmarkCommand;

/**
 * Tests the parse method.
 * It is tested ahead of any other method because it is a pure function of one
 * string: it reads no file, prints nothing and keeps no state between calls, so
 * a test can hand it a line and check what comes back without any setting up or
 * tearing down. It is also where every rule about the shape of the input lives,
 * which makes it the one place a typo in a separator or a message is caught.
 * Two things are checked of each line. A line that is understood must produce
 * the right kind of command, and a line that is not must fail with the message
 * the user is meant to read, so these tests pin the wording as well as the fact
 * of the failure. The commands keep their parsed arguments private, so the tests
 * assert the command's type and leave what it does with those arguments to the
 * text-UI tests.
 */
public class ParserTest {

    // ---- commands taking no argument ----

    @Test
    public void parse_byeCommand_returnsExitCommand() throws ThomasException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    public void parse_listCommand_returnsListCommand() throws ThomasException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    @Test
    public void parse_listWithTrailingText_exceptionThrown() {
        // A keyword taking no argument refuses one rather than ignoring it: "list 2019-12-02" was asked with a date
        // in mind, and listing everything would answer a question the user did not ask.
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("list everything"));
        assertEquals("Bust my buffers! 'list' is a signal on its own -- I don't know what to do with 'everything'.",
                e.getMessage());
    }

    @Test
    public void parse_byeWithTrailingText_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("bye now"));
        assertEquals("Bust my buffers! 'bye' is a signal on its own -- I don't know what to do with 'now'.",
                e.getMessage());
    }

    @Test
    public void parse_undoWithTrailingText_exceptionThrown() {
        // "undo 3" reads as a request to undo three changes, which is not a thing, so it is refused rather than
        // undoing one.
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("undo 3"));
        assertEquals("Bust my buffers! 'undo' is a signal on its own -- I don't know what to do with '3'.",
                e.getMessage());
    }

    @Test
    public void parse_keywordWithTrailingSpaces_returnsCommand() throws ThomasException {
        // Trailing spaces are not an argument.
        assertInstanceOf(ListCommand.class, Parser.parse("list   "));
    }

    // ---- stray whitespace ----

    @Test
    public void parse_leadingSpaces_keywordStillFound() throws ThomasException {
        assertInstanceOf(ListCommand.class, Parser.parse("   list"));
    }

    @Test
    public void parse_leadingTab_keywordStillFound() throws ThomasException {
        // Any whitespace is tidied, not only the space character.
        assertInstanceOf(AddCommand.class, Parser.parse("\ttodo read book"));
    }

    @Test
    public void parse_tabBetweenKeywordAndArgument_argumentStillRead() throws ThomasException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo\tread book"));
    }

    @Test
    public void parse_doubleSpacesInsideDate_dateStillRead() throws ThomasException {
        // Runs of spaces inside the argument are squeezed to one, so a doubled space between the day and the time
        // does not turn a good date into an unreadable one.
        assertInstanceOf(AddCommand.class, Parser.parse("deadline return book  /by  2019-12-02   1800"));
    }

    @Test
    public void parse_doubleSpacesAroundMarkers_markersStillFound() throws ThomasException {
        assertInstanceOf(AddCommand.class,
                Parser.parse("event meeting   /from   2019-12-02 1400   /to   2019-12-02 1600"));
    }

    // ---- unrecognized input ----

    @Test
    public void parse_unknownKeyword_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("blah"));
        assertEquals("Cinders and ashes! I don't know that signal. What does it mean?", e.getMessage());
    }

    @Test
    public void parse_emptyInput_exceptionThrown() {
        // An empty line is told it is empty, rather than being reported as an unknown command it did not name.
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse(""));
        assertEquals("Peep? I didn't catch a signal. Give me a command, such as list or todo.", e.getMessage());
    }

    @Test
    public void parse_onlySpaces_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("   "));
        assertEquals("Peep? I didn't catch a signal. Give me a command, such as list or todo.", e.getMessage());
    }

    @Test
    public void parse_wrongCaseKeyword_exceptionThrown() {
        // Matching is case sensitive, so "List" is not the list command.
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("List"));
        assertEquals("Cinders and ashes! I don't know that signal. What does it mean?", e.getMessage());
    }

    // ---- to-do ----

    @Test
    public void parse_todoWithDescription_returnsAddCommand() throws ThomasException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
    }

    @Test
    public void parse_todoWithoutDescription_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("todo"));
        assertEquals("Bust my buffers! A todo needs a description before I can couple it up.", e.getMessage());
    }

    @Test
    public void parse_todoBlankDescription_exceptionThrown() {
        // A keyword followed by spaces only splits into two parts, so the blank check rather than the length check is
        // what has to catch this.
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("todo    "));
        assertEquals("Bust my buffers! A todo needs a description before I can couple it up.", e.getMessage());
    }

    // ---- deadline ----

    @Test
    public void parse_deadlineWithDescriptionAndDate_returnsAddCommand() throws ThomasException {
        assertInstanceOf(AddCommand.class, Parser.parse("deadline return book /by 2019-12-02 1800"));
    }

    @Test
    public void parse_deadlineWithoutArguments_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("deadline"));
        assertEquals("Bust my buffers! A deadline needs a description before I can couple it up.", e.getMessage());
    }

    @Test
    public void parse_deadlineWithoutByMarker_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("deadline return book 2019-12-02 1800"));
        assertEquals("When is it due? A deadline needs a /by before I can pull it.", e.getMessage());
    }

    @Test
    public void parse_deadlineDescriptionEndingInBy_exceptionThrown() {
        // The separator carries a leading space so that a word merely ending in "by" is not mistaken for the marker.
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("deadline put it on standby 2019-12-02 1800"));
        assertEquals("When is it due? A deadline needs a /by before I can pull it.", e.getMessage());
    }

    @Test
    public void parse_deadlineMarkerOnly_exceptionThrown() {
        // A line that is nothing but the marker has no space in front of it once trimmed, so the split fails even
        // though the marker is there: the missing part is the description.
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("deadline /by 2019-12-02 1800"));
        assertEquals("Bust my buffers! A deadline needs a description before I can couple it up.", e.getMessage());
    }

    @Test
    public void parse_deadlineBlankDescription_exceptionThrown() {
        // Here the split does succeed, and the empty description is caught afterwards.
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("deadline    /by 2019-12-02 1800"));
        assertEquals("Bust my buffers! A deadline needs a description before I can couple it up.", e.getMessage());
    }

    @Test
    public void parse_deadlineBlankDate_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("deadline return book /by   "));
        assertEquals("When is it due? A deadline needs a /by before I can pull it.", e.getMessage());
    }

    @Test
    public void parse_deadlineUnreadableDate_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("deadline return book /by tomorrow"));
        assertEquals("I can't read 'tomorrow' as a deadline date! "
                + "My timetable wants a date and a 24-hour time, like 2019-12-02 1800.", e.getMessage());
    }

    @Test
    public void parse_deadlineDayNotOnCalendar_exceptionThrown() {
        // The right shape, naming a day that does not exist. The message says so rather than repeating the format
        // back, which the user has already matched.
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("deadline return book /by 2019-02-30 1800"));
        assertEquals("There's no such moment as '2019-02-30 1800' for a deadline date! "
                + "Check the day is on the calendar and the time is on the 24-hour clock, 0000 to 2359.",
                e.getMessage());
    }

    @Test
    public void parse_deadlineDateWithoutTime_exceptionThrown() {
        // A date is not enough on its own: the time is required too.
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("deadline return book /by 2019-12-02"));
        assertEquals("I can't read '2019-12-02' as a deadline date! "
                + "My timetable wants a date and a 24-hour time, like 2019-12-02 1800.", e.getMessage());
    }

    // ---- event ----

    @Test
    public void parse_eventWithBothDates_returnsAddCommand() throws ThomasException {
        assertInstanceOf(AddCommand.class, Parser.parse(
                "event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600"));
    }

    @Test
    public void parse_eventWithoutArguments_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("event"));
        assertEquals("Bust my buffers! An event needs a description before I can couple it up.", e.getMessage());
    }

    @Test
    public void parse_eventWithoutFromMarker_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("event project meeting /to 2019-12-02 1600"));
        assertEquals("When does it set off? An event needs a /from.", e.getMessage());
    }

    @Test
    public void parse_eventWithoutToMarker_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("event project meeting /from 2019-12-02 1400"));
        assertEquals("When does it arrive? An event needs a /to after its /from.", e.getMessage());
    }

    @Test
    public void parse_eventMarkersInWrongOrder_exceptionThrown() {
        // The markers are split off one at a time, so writing them the wrong way round is rejected rather than being
        // silently swapped -- and the message names the order, not a missing /to the user can see they typed.
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("event project meeting /to 2019-12-02 1600 /from 2019-12-02 1400"));
        assertEquals("Bust my buffers! Your event's /to came before its /from. Set off first, then arrive.",
                e.getMessage());
    }

    @Test
    public void parse_eventDayNotOnCalendar_exceptionThrown() {
        // The strict resolver refuses an event's dates exactly as a deadline's.
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse(
                "event meeting /from 2019-02-30 1400 /to 2019-03-01 1600"));
        assertEquals("There's no such moment as '2019-02-30 1400' for a start date! "
                + "Check the day is on the calendar and the time is on the 24-hour clock, 0000 to 2359.",
                e.getMessage());
    }

    // ---- markers given twice, or to the wrong command ----

    @Test
    public void parse_deadlineByTwice_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("deadline return book /by 2019-12-02 1800 /by 2019-12-03 1800"));
        assertEquals("Bust my buffers! You've given /by more than once. Once is all I need.", e.getMessage());
    }

    @Test
    public void parse_deadlineByTwiceInARow_exceptionThrown() {
        // "/by /by 2019..." puts the second marker at the very start of the date text, which the whole-word match
        // must still see.
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("deadline return book /by /by 2019-12-02 1800"));
        assertEquals("Bust my buffers! You've given /by more than once. Once is all I need.", e.getMessage());
    }

    @Test
    public void parse_deadlineWithFromMarker_exceptionThrown() {
        // Named as the wrong marker, not as a missing /by: the two commands have been mixed up.
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("deadline return book /from 2019-12-02 1800"));
        assertEquals("Bust my buffers! A deadline takes just a /by -- there's no /from or /to on it.", e.getMessage());
    }

    @Test
    public void parse_deadlineWithByAndToMarkers_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("deadline return book /by 2019-12-02 1800 /to 2019-12-03 1800"));
        assertEquals("Bust my buffers! A deadline takes just a /by -- there's no /from or /to on it.", e.getMessage());
    }

    @Test
    public void parse_eventWithByMarker_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("event project meeting /by 2019-12-02 1800"));
        assertEquals("Bust my buffers! An event takes a /from and a /to -- there's no /by on it.", e.getMessage());
    }

    @Test
    public void parse_eventFromTwiceBeforeTo_exceptionThrown() {
        // The second /from lands in the start date.
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("event meeting /from 2019-12-02 1400 /from 2019-12-02 1500 /to 2019-12-02 1600"));
        assertEquals("Bust my buffers! You've given /from more than once. Once is all I need.", e.getMessage());
    }

    @Test
    public void parse_eventFromTwiceAfterTo_exceptionThrown() {
        // The second /from lands in the end date.
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("event meeting /from 2019-12-02 1400 /to 2019-12-02 1600 /from 2019-12-02 1500"));
        assertEquals("Bust my buffers! You've given /from more than once. Once is all I need.", e.getMessage());
    }

    @Test
    public void parse_eventToTwice_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("event meeting /from 2019-12-02 1400 /to 2019-12-02 1600 /to 2019-12-02 1700"));
        assertEquals("Bust my buffers! You've given /to more than once. Once is all I need.", e.getMessage());
    }

    @Test
    public void parse_markerInsideAWord_notTakenForAMarker() throws ThomasException {
        // "/byte" and "standby" contain a marker's letters without being one, so neither is a repeat or a stray.
        assertInstanceOf(AddCommand.class,
                Parser.parse("deadline read /byte standby /by 2019-12-02 1800"));
    }

    @Test
    public void parse_eventMarkerOnly_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("event /from 2019-12-02 1400 /to 2019-12-02 1600"));
        assertEquals("Bust my buffers! An event needs a description before I can couple it up.", e.getMessage());
    }

    @Test
    public void parse_eventBlankStartDate_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("event project meeting /from   /to 2019-12-02 1600"));
        assertEquals("When does it set off? An event needs a /from.", e.getMessage());
    }

    @Test
    public void parse_eventBlankEndDate_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("event project meeting /from 2019-12-02 1400 /to   "));
        assertEquals("When does it arrive? An event needs a /to after its /from.", e.getMessage());
    }

    @Test
    public void parse_eventUnreadableStartDate_exceptionThrown() {
        // Each date names itself in its own message, so the user knows which one to fix.
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("event project meeting /from Mon 2pm /to 2019-12-02 1600"));
        assertEquals("I can't read 'Mon 2pm' as a start date! "
                + "My timetable wants a date and a 24-hour time, like 2019-12-02 1800.", e.getMessage());
    }

    @Test
    public void parse_eventUnreadableEndDate_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("event project meeting /from 2019-12-02 1400 /to 4pm"));
        assertEquals("I can't read '4pm' as an end date! "
                + "My timetable wants a date and a 24-hour time, like 2019-12-02 1800.", e.getMessage());
    }

    @Test
    public void parse_eventEndingBeforeItStarts_exceptionThrown() {
        // An event running backwards is refused, though not by the parser. The check is the event constructor, so that
        // loading the save file is held to it too, and the parser lets the exception through.
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("event project meeting /from 2019-12-02 1600 /to 2019-12-02 1400"));
        assertEquals("Bust my buffers! Your event arrives before it sets off! Check its /from and /to.",
                e.getMessage());
    }

    @Test
    public void parse_eventStartingWhenItEnds_returnsAddCommand() throws ThomasException {
        // An event lasting no time is odd but says nothing false, so it is allowed.
        assertInstanceOf(AddCommand.class, Parser.parse(
                "event project meeting /from 2019-12-02 1400 /to 2019-12-02 1400"));
    }

    // ---- descriptions holding the save file's field separator ----

    @Test
    public void parse_todoDescriptionWithSeparator_exceptionThrown() {
        // A description containing " | " is refused as it is typed, because it could not survive being saved: the task
        // would be accepted and listed, then lost on the next run. All three add commands are checked, since the rule
        // is one helper called from three places rather than one inherited check.
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("todo read book | and return it"));
        assertEquals("Bust my buffers! A description can't contain ' | ' -- "
                + "that's how I keep my wagons apart in the save file.", e.getMessage());
    }

    @Test
    public void parse_deadlineDescriptionWithSeparator_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("deadline read book | and return it /by 2019-12-02 1800"));
        assertEquals("Bust my buffers! A description can't contain ' | ' -- "
                + "that's how I keep my wagons apart in the save file.", e.getMessage());
    }

    @Test
    public void parse_eventDescriptionWithSeparator_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () ->
                Parser.parse("event talk | and lunch /from 2019-12-02 1400 /to 2019-12-02 1600"));
        assertEquals("Bust my buffers! A description can't contain ' | ' -- "
                + "that's how I keep my wagons apart in the save file.", e.getMessage());
    }

    @Test
    public void parse_todoDescriptionWithBarePipe_returnsAddCommand() throws ThomasException {
        // Only the separator as the save file writes it is refused. A bare pipe saves and loads back correctly, so
        // refusing it too would take away a character the format has no trouble with.
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book|and return it"));
    }

    @Test
    public void parse_todoDescriptionWithHalfSpacedPipe_returnsAddCommand() throws ThomasException {
        // A pipe with a space on one side only is likewise not the separator.
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book |and return it"));
    }

    // ---- mark, unmark and delete ----

    @Test
    public void parse_markWithNumber_returnsMarkCommand() throws ThomasException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 2"));
    }

    @Test
    public void parse_unmarkWithNumber_returnsUnmarkCommand() throws ThomasException {
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 2"));
    }

    @Test
    public void parse_deleteWithNumber_returnsDeleteCommand() throws ThomasException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 2"));
    }

    @Test
    public void parse_markNumberOutsideList_returnsMarkCommand() throws ThomasException {
        // Whether a task carries that number is the list's to answer, so a number outside the list still parses; only
        // the digits are checked here.
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 999"));
    }

    @Test
    public void parse_markWithoutNumber_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("mark"));
        assertEquals("Which wagon do you want me to mark? Give me its number.", e.getMessage());
    }

    @Test
    public void parse_deleteWithoutNumber_exceptionThrown() {
        // Each of the three names itself in the missing-argument message.
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("delete"));
        assertEquals("Which wagon do you want me to delete? Give me its number.", e.getMessage());
    }

    @Test
    public void parse_markNonInteger_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("mark two"));
        assertEquals("Bust my buffers! That's not a number. My wagons are numbered 1, 2, 3...", e.getMessage());
    }

    @Test
    public void parse_markTwoNumbers_exceptionThrown() {
        // Two numbers is a request for two tasks at once, and the answer is that there is no such thing.
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("mark 1 2"));
        assertEquals("One wagon at a time! Give me a single number to mark.", e.getMessage());
    }

    @Test
    public void parse_deleteNumberTooBigForAnInt_exceptionThrown() {
        // All digits, so it is a number -- just one no list will ever reach. It gets the range answer, not "not a
        // number".
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("delete 99999999999"));
        assertEquals("There's no wagon 99999999999 on my train! No train is that long.", e.getMessage());
    }

    @Test
    public void parse_markDecimalNumber_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("mark 1.5"));
        assertEquals("Bust my buffers! That's not a number. My wagons are numbered 1, 2, 3...", e.getMessage());
    }

    @Test
    public void parse_unmarkWithoutNumber_exceptionThrown() {
        // Each of the three names itself in the message.
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("unmark"));
        assertEquals("Which wagon do you want me to unmark? Give me its number.", e.getMessage());
    }

    @Test
    public void parse_deleteNonInteger_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("delete two"));
        assertEquals("Bust my buffers! That's not a number. My wagons are numbered 1, 2, 3...", e.getMessage());
    }

    @Test
    public void parse_markZero_returnsMarkCommand() throws ThomasException {
        // Zero is a whole number, so the parser lets it through; whether any task
        // carries it is the list's to answer, and TaskListTest pins that refusal.
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 0"));
    }

    @Test
    public void parse_markNegativeNumber_returnsMarkCommand() throws ThomasException {
        // "-1" is a whole number to parseInt and not all digits, so it is
        // neither refused as text nor as too long: it reaches the list, which
        // refuses it by range.
        assertInstanceOf(MarkCommand.class, Parser.parse("mark -1"));
    }

    @Test
    public void parse_markPlusSignedNumber_returnsMarkCommand() throws ThomasException {
        // parseInt accepts a leading plus, so "+1" is task 1.
        assertInstanceOf(MarkCommand.class, Parser.parse("mark +1"));
    }

    // ---- find ----

    @Test
    public void parse_findWithKeyword_returnsFindCommand() throws ThomasException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    /**
     * The whole argument is the keyword, so a search of several words looks for
     * the phrase rather than for any one of them. Splitting on spaces here would
     * quietly change what the user asked for.
     */
    @Test
    public void parse_findWithSeveralWords_returnsFindCommand() throws ThomasException {
        assertInstanceOf(FindCommand.class, Parser.parse("find read book"));
    }

    @Test
    public void parse_findWithoutKeyword_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("find"));
        assertEquals("What am I looking for? Give me a word to search the yard for.", e.getMessage());
    }

    /** A keyword of spaces alone is as missing as no keyword at all. */
    @Test
    public void parse_findBlankKeyword_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("find    "));
        assertEquals("What am I looking for? Give me a word to search the yard for.", e.getMessage());
    }

    // ---- on ----

    @Test
    public void parse_onWithDay_returnsOnCommand() throws ThomasException {
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-12-02"));
    }

    @Test
    public void parse_onWithoutDay_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("on"));
        assertEquals("Which day's timetable do you want to see?", e.getMessage());
    }

    @Test
    public void parse_onUnreadableDay_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("on tomorrow"));
        assertEquals("I can't find 'tomorrow' on my timetable! Write the day as 2019-12-02.", e.getMessage());
    }

    @Test
    public void parse_onDayWithTime_exceptionThrown() {
        // The on command asks about a whole day, so a time is refused.
        ThomasException e = assertThrows(ThomasException.class, () -> Parser.parse("on 2019-12-02 1800"));
        assertEquals("I can't find '2019-12-02 1800' on my timetable! Write the day as 2019-12-02.", e.getMessage());
    }
}
