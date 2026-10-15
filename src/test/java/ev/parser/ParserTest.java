package ev.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ev.EvException;
import ev.command.AddCommand;
import ev.command.Command;
import ev.command.DeleteCommand;
import ev.command.ExitCommand;
import ev.command.FindCommand;
import ev.command.ListCommand;
import ev.command.MarkCommand;
import ev.command.OnCommand;
import ev.command.UpdateCommand;

public class ParserTest {

    @Test
    public void parse_eachKeyword_matchingCommandReturned() throws EvException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class, Parser.parse("deadline return book /by 2019-12-02"));
        assertInstanceOf(AddCommand.class, Parser.parse("event trip /from 2019-12-01 /to 2019-12-03"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-12-02"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(MarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    public void parse_bye_isExitCommand() throws EvException {
        assertTrue(Parser.parse("bye").isExit());
    }

    @Test
    public void parse_listCommand_isNotExitCommand() throws EvException {
        assertFalse(Parser.parse("list").isExit());
    }

    @Test
    public void parse_unknownKeyword_messageListsKeywords() throws EvException {
        EvException thrown = assertThrows(EvException.class, () -> Parser.parse("blah"));
        assertTrue(thrown.getMessage().contains("blah"));
        assertTrue(thrown.getMessage().contains(CommandWord.listKeywords()));
    }

    @Test
    public void parse_keywordAsPrefixOfAnotherWord_notRecognised() throws EvException {
        assertThrows(EvException.class, () -> Parser.parse("listing"));
        assertThrows(EvException.class, () -> Parser.parse("todos read book"));
    }

    @Test
    public void parse_descriptionContainingKeyword_treatedAsDescription() throws EvException {
        Command command = Parser.parse("todo list the books");
        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    public void parseTodo_description_kept() throws EvException {
        assertEquals("[T][ ] read book", Parser.parseTodo("read book").toString());
    }

    @Test
    public void parseTodo_emptyDescription_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> Parser.parseTodo(""));
    }

    @Test
    public void parseDeadline_descriptionAndTime_bothKept() throws EvException {
        assertEquals("[D][ ] return book (by: Dec 2 2019, 6:00 PM)",
                Parser.parseDeadline("return book /by 2019-12-02 1800").toString());
    }

    @Test
    public void parseDeadline_extraSpaces_trimmed() throws EvException {
        assertEquals("[D][ ] return book (by: Dec 2 2019)",
                Parser.parseDeadline("  return book   /by   2019-12-02  ").toString());
    }

    @Test
    public void parseDeadline_missingBy_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> Parser.parseDeadline("return book"));
    }

    @Test
    public void parseDeadline_missingDescription_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> Parser.parseDeadline("/by 2019-12-02"));
    }

    @Test
    public void parseDeadline_missingTime_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> Parser.parseDeadline("return book /by"));
    }

    @Test
    public void parseDeadline_unreadableTime_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> Parser.parseDeadline("return book /by Sunday"));
    }

    @Test
    public void parseEvent_descriptionAndBothTimes_allKept() throws EvException {
        assertEquals("[E][ ] trip (from: Dec 1 2019, 2:00 PM to: Dec 3 2019, 4:00 PM)",
                Parser.parseEvent("trip /from 2019-12-01 1400 /to 2019-12-03 1600").toString());
    }

    @Test
    public void parseEvent_missingFrom_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> Parser.parseEvent("trip /to 2019-12-03"));
    }

    @Test
    public void parseEvent_missingTo_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> Parser.parseEvent("trip /from 2019-12-01"));
    }

    @Test
    public void parseEvent_toBeforeFrom_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> Parser.parseEvent("trip /to 2019-12-03 /from 2019-12-01"));
    }

    @Test
    public void parseEvent_missingDescription_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> Parser.parseEvent("/from 2019-12-01 /to 2019-12-03"));
    }

    @Test
    public void parseTaskNumber_number_returnsSameNumber() throws EvException {
        assertEquals(2, Parser.parseTaskNumber("2"));
    }

    @Test
    public void parseTaskNumber_notANumber_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> Parser.parseTaskNumber("two"));
    }

    @Test
    public void parseTaskNumber_empty_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> Parser.parseTaskNumber(""));
    }

    @Test
    public void parseDate_dateWithTime_timeDropped() throws EvException {
        assertEquals(LocalDate.of(2019, 12, 2), Parser.parseDate("2019-12-02 1800"));
    }

    @Test
    public void parseDate_empty_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> Parser.parseDate(""));
    }

    @Test
    public void parseKeyword_word_returnedUnchanged() throws EvException {
        assertEquals("book", Parser.parseKeyword("book"));
    }

    @Test
    public void parseKeyword_severalWords_keptTogether() throws EvException {
        assertEquals("read book", Parser.parseKeyword("read book"));
    }

    @Test
    public void parseKeyword_empty_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> Parser.parseKeyword(""));
    }


    @Test
    public void parse_update_returnsUpdateCommand() throws EvException {
        assertInstanceOf(UpdateCommand.class, Parser.parse("update 1 /desc read book"));
    }

    @Test
    public void parseUpdate_eachOption_accepted() throws EvException {
        assertInstanceOf(UpdateCommand.class, Parser.parseUpdate("1 /desc read book"));
        assertInstanceOf(UpdateCommand.class, Parser.parseUpdate("1 /by 2019-12-05 1800"));
        assertInstanceOf(UpdateCommand.class, Parser.parseUpdate("1 /from 2019-12-05"));
        assertInstanceOf(UpdateCommand.class, Parser.parseUpdate("1 /to 2019-12-05"));
    }

    @Test
    public void parseUpdate_empty_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> Parser.parseUpdate(""));
    }

    @Test
    public void parseUpdate_noOption_exceptionThrown() throws EvException {
        EvException thrown = assertThrows(EvException.class, () -> Parser.parseUpdate("2"));
        assertTrue(thrown.getMessage().contains("/desc"));
    }

    @Test
    public void parseUpdate_noValue_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> Parser.parseUpdate("2 /by"));
    }

    @Test
    public void parseUpdate_twoOptions_exceptionThrown() throws EvException {
        EvException thrown = assertThrows(EvException.class, () ->
                Parser.parseUpdate("2 /desc a /by 2019-12-05"));
        assertTrue(thrown.getMessage().contains("One field at a time"));
    }

    @Test
    public void parseUpdate_noTaskNumber_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> Parser.parseUpdate("/desc read book"));
    }

    @Test
    public void parseUpdate_notANumber_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> Parser.parseUpdate("two /desc x"));
    }

    @Test
    public void parseDeadline_byGivenTwice_exceptionThrown() {
        EvException thrown = assertThrows(EvException.class, () ->
                Parser.parseDeadline("return book /by 2019-12-01 /by 2019-12-02"));
        assertTrue(thrown.getMessage().contains("may only appear once"));
    }

    @Test
    public void parseEvent_fromOrToGivenTwice_exceptionThrown() {
        assertThrows(EvException.class, () ->
                Parser.parseEvent("camp /from 2019-12-01 /from 2019-12-02 /to 2019-12-03"));
        assertThrows(EvException.class, () ->
                Parser.parseEvent("camp /from 2019-12-01 /to 2019-12-02 /to 2019-12-03"));
    }

    @Test
    public void parseTodo_descriptionHoldingTheSeparator_exceptionThrown() {
        EvException thrown = assertThrows(EvException.class, () -> Parser.parseTodo("a | b"));
        assertTrue(thrown.getMessage().contains("cannot contain"));
    }

    @Test
    public void parseEvent_endBeforeStart_exceptionThrown() {
        assertThrows(EvException.class, () ->
                Parser.parseEvent("camp /from 2019-12-05 /to 2019-12-01"));
    }
}
