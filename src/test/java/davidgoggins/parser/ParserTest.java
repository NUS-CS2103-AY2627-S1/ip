package davidgoggins.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import davidgoggins.DavidGogginsException;
import davidgoggins.task.Deadline;

/**
 * Tests {@link Parser}, which turns what the user typed into commands and tasks.
 *
 * <p>The parser is a good fit for unit testing: it is static, it reads nothing from
 * disk and prints nothing, so a test only has to hand it a string and look at what
 * comes back. Built tasks are checked through {@code toString()} and
 * {@code toSaveFormat()}, since the task classes expose no getters for their fields.
 */
public class ParserTest {

    @Test
    public void parseDeadline_descriptionAndDate_deadlineCreated() throws DavidGogginsException {
        Deadline deadline = Parser.parseDeadline("return book /by 2026-09-10");
        assertEquals("[D][ ] return book (by: 2026-09-10)", deadline.toString());
    }

    /** A new deadline should never start out ticked off, whatever the input. */
    @Test
    public void parseDeadline_descriptionAndDate_notDone() throws DavidGogginsException {
        Deadline deadline = Parser.parseDeadline("return book /by 2026-09-10");
        assertEquals("D | 0 | return book | 2026-09-10", deadline.toSaveFormat());
    }

    /** The parser trims, so stray spaces around either part must not survive. */
    @Test
    public void parseDeadline_extraSpacesAroundParts_partsTrimmed() throws DavidGogginsException {
        Deadline deadline = Parser.parseDeadline("   return book    /by    2026-09-10   ");
        assertEquals("[D][ ] return book (by: 2026-09-10)", deadline.toString());
    }

    /** Only the command word is lower-cased, so a description keeps the user's capitals. */
    @Test
    public void parseDeadline_mixedCaseDescription_capitalisationKept() throws DavidGogginsException {
        Deadline deadline = Parser.parseDeadline("Return CS2103T Book /by 2026-09-10");
        assertEquals("[D][X] Return CS2103T Book (by: 2026-09-10)", markedDone(deadline).toString());
    }

    /**
     * The split is on the bare keyword rather than {@code " /by "}, so the spaces
     * around it are optional. This test pins that behaviour down so a future change to
     * the split cannot break it unnoticed.
     */
    @Test
    public void parseDeadline_noSpacesAroundKeyword_stillSplit() throws DavidGogginsException {
        Deadline deadline = Parser.parseDeadline("return book/by2026-09-10");
        assertEquals("[D][ ] return book (by: 2026-09-10)", deadline.toString());
    }

    /** A second {@code /by} is named as the problem, rather than reported as a bad date. */
    @Test
    public void parseDeadline_secondByKeyword_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline("return book /by 2026-09-10 /by 2026-09-11"));
        assertTrue(e.getMessage().contains("/by more than once"), e.getMessage());
    }

    @Test
    public void parseDeadline_missingByKeyword_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline("return book 2026-09-10"));
        assertTrue(e.getMessage().contains("needs a /by part"), e.getMessage());
    }

    /** The user typed a bare {@code deadline}, so the argument reaching the parser is empty. */
    @Test
    public void parseDeadline_emptyArgument_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline(""));
        assertTrue(e.getMessage().contains("needs a /by part"), e.getMessage());
    }

    @Test
    public void parseDeadline_emptyDescription_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline("/by 2026-09-10"));
        assertTrue(e.getMessage().contains("description of a deadline cannot be empty"), e.getMessage());
    }

    /** Whitespace alone is not a description either, because the parts are trimmed first. */
    @Test
    public void parseDeadline_blankDescription_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline("    /by 2026-09-10"));
        assertTrue(e.getMessage().contains("description of a deadline cannot be empty"), e.getMessage());
    }

    @Test
    public void parseDeadline_missingDate_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline("return book /by"));
        assertTrue(e.getMessage().contains("when it is due after /by"), e.getMessage());
    }

    @Test
    public void parseDeadline_blankDate_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline("return book /by    "));
        assertTrue(e.getMessage().contains("when it is due after /by"), e.getMessage());
    }

    /** A date the user wrote in words cannot be turned into a {@code LocalDate}. */
    @Test
    public void parseDeadline_dateNotADate_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline("return book /by next Friday"));
        assertTrue(e.getMessage().contains("yyyy-mm-dd"), e.getMessage());
    }

    /** The right day, but written the wrong way round. */
    @Test
    public void parseDeadline_dateInWrongOrder_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline("return book /by 10-09-2026"));
        assertTrue(e.getMessage().contains("yyyy-mm-dd"), e.getMessage());
    }

    /** A date shaped correctly but naming a day that does not exist. */
    @Test
    public void parseDeadline_impossibleDate_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline("return book /by 2026-13-40"));
        assertTrue(e.getMessage().contains("not a real date"), e.getMessage());
    }

    @Test
    public void parseKeyword_keywordGiven_keywordReturned() throws DavidGogginsException {
        assertEquals("book", Parser.parseKeyword("book"));
    }

    /** The search ignores case itself, so the keyword must reach it as typed. */
    @Test
    public void parseKeyword_mixedCaseKeyword_capitalisationKept() throws DavidGogginsException {
        assertEquals("Book", Parser.parseKeyword("Book"));
    }

    /** Several words are one keyword: the whole argument is searched for. */
    @Test
    public void parseKeyword_severalWords_wholeArgumentReturned() throws DavidGogginsException {
        assertEquals("read book", Parser.parseKeyword("read book"));
    }

    /** The user typed a bare {@code find}, so the argument reaching the parser is empty. */
    @Test
    public void parseKeyword_emptyArgument_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseKeyword(""));
        assertTrue(e.getMessage().contains("what to search for"), e.getMessage());
    }

    /** Marks a deadline done so the ticked-off rendering can be checked in one line. */
    private static Deadline markedDone(Deadline deadline) {
        deadline.markAsDone();
        return deadline;
    }

    @Test
    public void parseCommand_mixedCaseWithArgument_commandWordLowerCased() {
        assertEquals("mark", Parser.parseCommand("MaRk 2"));
    }

    @Test
    public void parseCommand_emptyInput_emptyStringReturned() {
        assertEquals("", Parser.parseCommand(""));
    }

    /** Runs of spaces after the command word go, but spaces inside the argument stay. */
    @Test
    public void parseArgument_severalSpacesAfterCommand_innerSpacesKept() {
        assertEquals("read  book", Parser.parseArgument("todo   read  book"));
    }

    @Test
    public void parseArgument_commandOnly_emptyStringReturned() {
        assertEquals("", Parser.parseArgument("list"));
    }

    @Test
    public void rejectSeparator_noSeparator_argumentReturned() throws DavidGogginsException {
        assertEquals("read book", Parser.rejectSeparator("read book"));
    }

    @Test
    public void rejectSeparator_separatorInArgument_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.rejectSeparator("read book | now"));
        assertTrue(e.getMessage().contains("\"|\""), e.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"7, 7", "+3, 3", "-1, -1", "007, 7"})
    public void parseTaskNumber_wholeNumber_numberReturned(String argument, int expected)
            throws DavidGogginsException {
        assertEquals(expected, Parser.parseTaskNumber(argument, "mark"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"two", "2.5", "1a", "#1"})
    public void parseTaskNumber_notAWholeNumber_exceptionThrown(String argument) {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseTaskNumber(argument, "delete"));
        assertTrue(e.getMessage().contains("is not a task number"), e.getMessage());
        assertTrue(e.getMessage().contains("delete 2"), "advice should name the command used");
    }

    @Test
    public void parseTaskNumber_severalNumbers_oneAtATimeMessage() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseTaskNumber("1 2", "mark"));
        assertTrue(e.getMessage().contains("One task at a time"), e.getMessage());
    }

    /** Too big for an int, but still a number, so "not a number" would be wrong advice. */
    @Test
    public void parseTaskNumber_numberTooLargeForInt_noSuchTaskMessage() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseTaskNumber("99999999999", "mark"));
        assertTrue(e.getMessage().contains("There's no task 99999999999"), e.getMessage());
    }

    @Test
    public void parseTodo_description_todoCreated() throws DavidGogginsException {
        assertEquals("[T][ ] read book", Parser.parseTodo("read book").toString());
    }

    @Test
    public void parseTodo_emptyArgument_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () -> Parser.parseTodo(""));
        assertTrue(e.getMessage().contains("description of a todo cannot be empty"), e.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"read book /by 2026-09-10", "run /from 2026-01-01", "rest /to 2026-01-02"})
    public void parseTodo_dateFlag_exceptionThrown(String argument) {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () -> Parser.parseTodo(argument));
        assertTrue(e.getMessage().contains("A todo has no dates"), e.getMessage());
    }

    /** Slashes inside a path are not flags, so a todo about files is still accepted. */
    @Test
    public void parseTodo_pathInDescription_todoCreated() throws DavidGogginsException {
        assertEquals("[T][ ] fix /etc/hosts", Parser.parseTodo("fix /etc/hosts").toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"return book /from 2026-09-10", "return book /at 2026-09-10 /by 2026-09-11"})
    public void parseDeadline_flagNotForDeadlines_exceptionThrown(String argument) {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                Parser.parseDeadline(argument));
        assertTrue(e.getMessage().contains("A deadline does not take a"), e.getMessage());
    }

    @Test
    public void parseEvent_descriptionAndDates_eventCreated() throws DavidGogginsException {
        assertEquals("[E][ ] project meeting (from: 2026-09-10 to: 2026-09-11)",
                Parser.parseEvent("project meeting /from 2026-09-10 /to 2026-09-11").toString());
    }

    /** Each malformed event must be refused with advice that names what is actually wrong. */
    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
        "project meeting; needs a /from part",
        "project meeting /from 2026-09-10; needs a /to part",
        "/from 2026-09-10 /to 2026-09-11; description of an event cannot be empty",
        "meeting /from /to 2026-09-11; starts after /from",
        "meeting /from 2026-09-10 /to; ends after /to",
        "meeting /from 2026-09-10 /from 2026-09-11 /to 2026-09-12; /from more than once",
        "meeting /from 2026-09-10 /to 2026-09-11 /to 2026-09-12; /to more than once",
        "meeting /to 2026-09-11 /from 2026-09-10; Put /from before /to",
        "meeting /from 2026-09-10 /to 2026-09-11 /by 2026-09-12; does not take a /by part",
        "meeting /from 2026-09-12 /to 2026-09-11; cannot end before it starts",
        "meeting /from 2026-09-31 /to 2026-10-01; not a real date",
    })
    public void parseEvent_malformedInput_exceptionNamesProblem(String argument, String expectedAdvice) {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () -> Parser.parseEvent(argument));
        assertTrue(e.getMessage().contains(expectedAdvice), e.getMessage());
    }

    /**
     * In a Turkish locale, lower-casing "I" gives a dotless "ı", so a command typed in
     * capitals would not be recognised if the machine's language were used.
     */
    @Test
    public void parseCommand_upperCaseIOnTurkishMachine_commandRecognised() {
        Locale original = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));
        try {
            assertEquals("list", Parser.parseCommand("LIST"));
        } finally {
            Locale.setDefault(original);
        }
    }
}
