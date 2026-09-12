package minweeder.parser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import minweeder.command.CommandWord;
import minweeder.exception.MinweederException;
import minweeder.task.TaskList;
import minweeder.task.Todo;

public class ParserTest {

    @Test
    public void splitCommand_commandWithArguments_splitsIntoTwoParts() {
        String[] result = Parser.splitCommand("deadline return book /by 2/12/2019 1800");

        assertArrayEquals(new String[] {"deadline", "return book /by 2/12/2019 1800"}, result);
    }

    @Test
    public void splitCommand_commandWithoutArguments_returnsSinglePart() {
        String[] result = Parser.splitCommand("list");

        assertArrayEquals(new String[] {"list"}, result);
    }

    @Test
    public void parseCommandWord_validWord_returnsMatchingEnum() throws MinweederException {
        assertEquals(CommandWord.TODO, Parser.parseCommandWord(new String[] {"todo", "read book"}));
    }

    @Test
    public void parseCommandWord_invalidWord_throwsException() {
        assertThrows(MinweederException.class, () ->
                Parser.parseCommandWord(new String[] {"frobnicate"}));
    }

    @Test
    public void requireArguments_argumentsPresent_returnsTrimmedArguments() throws MinweederException {
        String result = Parser.requireArguments(
                new String[] {"todo", "  read book  "}, "todo", "todo read book");

        assertEquals("read book", result);
    }

    @Test
    public void requireArguments_missingArguments_throwsException() {
        assertThrows(MinweederException.class, () ->
                Parser.requireArguments(new String[] {"todo"}, "todo", "todo read book"));
    }

    @Test
    public void requireArguments_blankArguments_throwsException() {
        assertThrows(MinweederException.class, () ->
                Parser.requireArguments(new String[] {"todo", "   "}, "todo", "todo read book"));
    }

    @Test
    public void requireKeyword_keywordPresentWithBothSides_splitsAndTrims() throws MinweederException {
        String[] result = Parser.requireKeyword("return book /by 2/12/2019 1800", "/by",
                "return book /by 2/12/2019 1800");

        assertArrayEquals(new String[] {"return book", "2/12/2019 1800"}, result);
    }

    @Test
    public void requireKeyword_missingKeyword_throwsException() {
        assertThrows(MinweederException.class, () ->
                Parser.requireKeyword("return book", "/by", "return book /by 2/12/2019 1800"));
    }

    @Test
    public void requireKeyword_blankSide_throwsException() {
        assertThrows(MinweederException.class, () ->
                Parser.requireKeyword("/by 2/12/2019 1800", "/by", "return book /by 2/12/2019 1800"));
    }

    @Test
    public void requireKeyword_keywordUsedTwice_throwsException() {
        assertThrows(MinweederException.class, () ->
                Parser.requireKeyword("return book /by 2/12/2019 1800 /by 3/12/2019 1800", "/by",
                        "return book /by 2/12/2019 1800"));
    }

    @Test
    public void requireNoArguments_noArguments_doesNotThrow() {
        assertDoesNotThrow(() -> Parser.requireNoArguments(new String[] {"list"}, "list"));
    }

    @Test
    public void requireNoArguments_blankArguments_doesNotThrow() {
        assertDoesNotThrow(() -> Parser.requireNoArguments(new String[] {"list", "   "}, "list"));
    }

    @Test
    public void requireNoArguments_withArguments_throwsException() {
        assertThrows(MinweederException.class, () ->
                Parser.requireNoArguments(new String[] {"list", "extra"}, "list"));
    }

    @Test
    public void parseOnDate_validDate_parsesCorrectly() throws MinweederException {
        LocalDate result = Parser.parseOnDate("2/12/2024", "2/12/2024");

        assertEquals(LocalDate.of(2024, 12, 2), result);
    }

    @Test
    public void parseOnDate_invalidFormat_throwsException() {
        assertThrows(MinweederException.class, () -> Parser.parseOnDate("2 Dec 2024", "2/12/2024"));
    }

    @Test
    public void parseIndex_validIndexWithinRange_returnsZeroBasedIndex() throws MinweederException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));

        int index = Parser.parseIndex(new String[] {"mark", "2"}, tasks);

        assertEquals(1, index);
    }

    @Test
    public void parseIndex_missingArgument_throwsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(MinweederException.class, () -> Parser.parseIndex(new String[] {"mark"}, tasks));
    }

    @Test
    public void parseIndex_nonNumericArgument_throwsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(MinweederException.class, () -> Parser.parseIndex(new String[] {"mark", "two"}, tasks));
    }

    @Test
    public void parseIndex_emptyTaskList_throwsException() {
        TaskList tasks = new TaskList();

        assertThrows(MinweederException.class, () -> Parser.parseIndex(new String[] {"mark", "1"}, tasks));
    }

    @Test
    public void parseIndex_indexOutOfRange_throwsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(MinweederException.class, () -> Parser.parseIndex(new String[] {"mark", "5"}, tasks));
    }

    @Test
    public void parseLoanKeyword_toPresent_returnsTo() throws MinweederException {
        assertEquals("/to", Parser.parseLoanKeyword("50 /to Alice", "50 /to Alice"));
    }

    @Test
    public void parseLoanKeyword_fromPresent_returnsFrom() throws MinweederException {
        assertEquals("/from", Parser.parseLoanKeyword("50 /from Bob", "50 /to Alice"));
    }

    @Test
    public void parseLoanKeyword_neitherPresent_throwsException() {
        assertThrows(MinweederException.class, () -> Parser.parseLoanKeyword("50 Alice", "50 /to Alice"));
    }

    @Test
    public void parseLoanAmount_validAmount_parsesCorrectly() throws MinweederException {
        assertEquals(50.0, Parser.parseLoanAmount("50", "50 /to Alice"));
    }

    @Test
    public void parseLoanAmount_negativeAmount_throwsException() {
        assertThrows(MinweederException.class, () -> Parser.parseLoanAmount("-5", "50 /to Alice"));
    }

    @Test
    public void parseLoanAmount_nonNumericAmount_throwsException() {
        assertThrows(MinweederException.class, () -> Parser.parseLoanAmount("fifty", "50 /to Alice"));
    }

    @Test
    public void parseLoanAmount_zeroAmount_parsesCorrectly() throws MinweederException {
        assertEquals(0.0, Parser.parseLoanAmount("0", "50 /to Alice"));
    }

    @Test
    public void parseLoanAmount_nanText_throwsException() {
        assertThrows(MinweederException.class, () -> Parser.parseLoanAmount("NaN", "50 /to Alice"));
    }

    @Test
    public void parseLoanAmount_infinityText_throwsException() {
        assertThrows(MinweederException.class, () -> Parser.parseLoanAmount("Infinity", "50 /to Alice"));
    }

    @Test
    public void parseDeadlineBy_validDateTime_parsesCorrectly() throws MinweederException {
        LocalDateTime result = Parser.parseDeadlineBy("2/12/2024 1800", "2/12/2024 1800");

        assertEquals(LocalDateTime.of(2024, 12, 2, 18, 0), result);
    }

    @Test
    public void parseDeadlineBy_invalidFormat_throwsException() {
        assertThrows(MinweederException.class, () ->
                Parser.parseDeadlineBy("2 December 2024", "2/12/2024 1800"));
    }
}
