package serangooner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import serangooner.command.AddCommand;
import serangooner.command.CommandType;
import serangooner.command.DeleteCommand;
import serangooner.command.ExitCommand;
import serangooner.command.FindCommand;
import serangooner.command.HelpCommand;
import serangooner.command.ListCommand;
import serangooner.command.MarkCommand;
import serangooner.command.OnCommand;
import serangooner.command.UndoCommand;
import serangooner.command.UnmarkCommand;
import serangooner.storage.Storage;
import serangooner.task.DateRange;
import serangooner.task.Deadline;
import serangooner.task.TaskList;
import serangooner.task.Todo;
import serangooner.ui.Ui;

public class ParserTest {
    @Test
    public void parse_eachKeyword_returnsTheMatchingCommand() {
        assertTrue(Parser.parse("todo read book") instanceof AddCommand);
        assertTrue(Parser.parse("deadline submit ip by 2026-09-01") instanceof AddCommand);
        assertTrue(Parser.parse("event orbital from 2026-09-03 to 2026-09-04") instanceof AddCommand);
        assertTrue(Parser.parse("mark 1") instanceof MarkCommand);
        assertTrue(Parser.parse("unmark 1") instanceof UnmarkCommand);
        assertTrue(Parser.parse("delete 1") instanceof DeleteCommand);
        assertTrue(Parser.parse("on 2026-09-01") instanceof OnCommand);
        assertTrue(Parser.parse("list") instanceof ListCommand);
        assertTrue(Parser.parse("undo") instanceof UndoCommand);
        assertTrue(Parser.parse("help") instanceof HelpCommand);
        assertTrue(Parser.parse("bye") instanceof ExitCommand);
    }

    @Test
    public void parse_onlyTheExitCommand_saysToExit() {
        assertTrue(Parser.parse("bye").isExit());
        assertFalse(Parser.parse("list").isExit());
    }

    @Test
    public void parse_unusableInput_exceptionThrownBeforeAnyCommandIsBuilt() {
        assertThrows(SerangoonerException.class, () -> Parser.parse("blah"));
        assertThrows(SerangoonerException.class, () -> Parser.parse("todo"));
        assertThrows(SerangoonerException.class, () -> Parser.parse("mark abc"));
        assertThrows(SerangoonerException.class, () -> Parser.parse("deadline submit ip"));
        assertThrows(SerangoonerException.class, () -> Parser.parse("on 2026-09-05 to 2026-09-01"));
    }

    @Test
    public void parseCommandType_knownKeyword_returnsThatCommand() {
        assertEquals(CommandType.TODO, Parser.parseCommandType("todo read book"));
        assertEquals(CommandType.LIST, Parser.parseCommandType("list"));
    }

    @Test
    public void parseCommandType_unknownKeyword_exceptionThrown() {
        assertThrows(SerangoonerException.class, () -> Parser.parseCommandType("blah"));
    }

    @Test
    public void parseCommandType_blankInput_exceptionThrown() {
        assertThrows(SerangoonerException.class, () -> Parser.parseCommandType("   "));
    }

    @Test
    public void parseTodo_validCommand_returnsTodo() {
        assertEquals("[T][ ] read book", Parser.parseTodo("todo read book").toString());
    }

    @Test
    public void parseTodo_noDescription_exceptionThrown() {
        assertThrows(SerangoonerException.class, () -> Parser.parseTodo("todo"));
        assertThrows(SerangoonerException.class, () -> Parser.parseTodo("todo   "));
    }

    @Test
    public void parseTodo_leadingSpace_ignoresIt() {
        assertEquals("[T][ ] read book", Parser.parseTodo("  todo read book").toString());
    }

    @Test
    public void parseDeadline_validCommand_returnsDeadline() {
        assertEquals("[D][ ] submit ip (by: 01 Sep 2026)",
                Parser.parseDeadline("deadline submit ip by 2026-09-01").toString());
    }

    @Test
    public void parseDeadline_dateWithTime_keepsTimeOfDay() {
        assertEquals("[D][ ] submit ip (by: 01 Sep 2026, 6:00PM)",
                Parser.parseDeadline("deadline submit ip by 2026-09-01 1800").toString());
    }

    @Test
    public void parseDeadline_missingParts_exceptionThrown() {
        assertThrows(SerangoonerException.class, () -> Parser.parseDeadline("deadline submit ip"));
        assertThrows(SerangoonerException.class, () -> Parser.parseDeadline("deadline submit ip by "));
        assertThrows(SerangoonerException.class, () -> Parser.parseDeadline("deadline by 2026-09-01"));
    }

    @Test
    public void parseDeadline_unreadableDate_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                Parser.parseDeadline("deadline submit ip by tomorrow"));
    }

    @Test
    public void parseEvent_validCommand_returnsEvent() {
        assertEquals("[E][ ] orbital (from: 03 Sep 2026 to: 04 Sep 2026)",
                Parser.parseEvent("event orbital from 2026-09-03 to 2026-09-04").toString());
    }

    @Test
    public void parseEvent_leadingSpace_ignoresIt() {
        assertEquals("[E][ ] orbital (from: 03 Sep 2026 to: 04 Sep 2026)",
                Parser.parseEvent("   event orbital from 2026-09-03 to 2026-09-04").toString());
    }

    @Test
    public void parseEvent_missingParts_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                Parser.parseEvent("event orbital from 2026-09-03"));
        assertThrows(SerangoonerException.class, () ->
                Parser.parseEvent("event orbital to 2026-09-04"));
        assertThrows(SerangoonerException.class, () ->
                Parser.parseEvent("event from 2026-09-03 to 2026-09-04"));
    }

    @Test
    public void parseEvent_endBeforeStart_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                Parser.parseEvent("event orbital from 2026-09-05 to 2026-09-01"));
    }

    @Test
    public void parseTaskNumber_validNumber_returnsIt() {
        assertEquals(3, Parser.parseTaskNumber("mark 3", CommandType.MARK));
        assertEquals(1, Parser.parseTaskNumber("delete   1  ", CommandType.DELETE));
    }

    @Test
    public void parseTaskNumber_leadingSpace_ignoresIt() {
        assertEquals(2, Parser.parseTaskNumber("  mark 2", CommandType.MARK));
    }

    @Test
    public void parseTaskNumber_numberNamingNoTask_returnsIt() {
        // Whether a number names a task depends on the list, so the parser lets it through.
        assertEquals(99, Parser.parseTaskNumber("unmark 99", CommandType.UNMARK));
    }

    @Test
    public void parseTaskNumber_missingNumber_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                Parser.parseTaskNumber("mark", CommandType.MARK));
    }

    @Test
    public void parseTaskNumber_notANumber_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                Parser.parseTaskNumber("mark abc", CommandType.MARK));
    }

    @Test
    public void parseDateRange_singleDate_coversThatDayAlone() {
        DateRange range = Parser.parseDateRange("on 2026-09-01");
        assertEquals(LocalDate.of(2026, 9, 1), range.start());
        assertEquals(LocalDate.of(2026, 9, 1), range.end());
    }

    @Test
    public void parseDateRange_dateWithTime_keepsTheDateAlone() {
        DateRange range = Parser.parseDateRange("on 2026-09-01 1800");
        assertEquals(LocalDate.of(2026, 9, 1), range.start());
        assertEquals(LocalDate.of(2026, 9, 1), range.end());
    }

    @Test
    public void parseDateRange_twoDates_coversBothEnds() {
        DateRange range = Parser.parseDateRange("on 2026-09-01 to 2026-09-04");
        assertEquals(LocalDate.of(2026, 9, 1), range.start());
        assertEquals(LocalDate.of(2026, 9, 4), range.end());
    }

    @Test
    public void parseDateRange_noDate_exceptionThrown() {
        assertThrows(SerangoonerException.class, () -> Parser.parseDateRange("on"));
    }

    @Test
    public void parseDateRange_unreadableDate_exceptionThrown() {
        assertThrows(SerangoonerException.class, () -> Parser.parseDateRange("on someday"));
    }

    @Test
    public void parseDateRange_endBeforeStart_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                Parser.parseDateRange("on 2026-09-05 to 2026-09-01"));
    }


    @Test
    public void parseCommandType_keywordInAnyOtherCase_returnsThatCommand() {
        assertEquals(CommandType.LIST, Parser.parseCommandType("LIST"));
        assertEquals(CommandType.TODO, Parser.parseCommandType("Todo read book"));
    }

    @Test
    public void parse_keywordInAnyOtherCase_carriesTheArgumentIntoTheCommand() {
        assertEquals("[D][ ] submit ip (by: 01 Sep 2026)",
                Parser.parseDeadline("DEADLINE submit ip by 2026-09-01").toString());
        assertTrue(Parser.parse("Bye") instanceof ExitCommand);
    }

    @Test
    public void parse_commandTakingNoArgumentGivenOne_exceptionThrown() {
        assertThrows(SerangoonerException.class, () -> Parser.parse("list all"));
        assertThrows(SerangoonerException.class, () -> Parser.parse("undo 3"));
        assertThrows(SerangoonerException.class, () -> Parser.parse("help me"));
        assertThrows(SerangoonerException.class, () -> Parser.parse("bye now"));
    }

    @Test
    public void parseCommandType_wordMerelyStartingWithAKeyword_exceptionThrown() {
        assertThrows(SerangoonerException.class, () -> Parser.parseCommandType("listing"));
        assertThrows(SerangoonerException.class, () -> Parser.parseCommandType("byebye"));
    }

    @Test
    public void parseCommandType_surroundingWhitespace_ignoresIt() {
        assertEquals(CommandType.LIST, Parser.parseCommandType("   list   "));
    }

    @Test
    public void parseCommandType_whitespaceOnly_exceptionThrown() {
        assertThrows(SerangoonerException.class, () -> Parser.parseCommandType("   "));
    }

    @Test
    public void parseTodo_descriptionWithInnerSpaces_keepsThemAsTyped() {
        assertEquals("[T][ ] read  two  books", Parser.parseTodo("todo read  two  books").toString());
    }

    @Test
    public void parseDeadline_descriptionContainingTheSeparator_splitsAtTheLastOne() {
        assertEquals("[D][ ] pay by phone (by: 01 Sep 2026)",
                Parser.parseDeadline("deadline pay by phone by 2026-09-01").toString());
    }

    @Test
    public void parseDeadline_emptyDescription_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                Parser.parseDeadline("deadline  by 2026-09-01"));
    }

    @Test
    public void parseDeadline_descriptionOfSpacesOnly_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                Parser.parseDeadline("deadline     by 2026-09-01"));
    }

    @Test
    public void parseDeadline_extraSpacesAroundParts_trimsThem() {
        assertEquals("[D][ ] submit ip (by: 01 Sep 2026)",
                Parser.parseDeadline("deadline   submit ip   by   2026-09-01").toString());
    }

    @Test
    public void parseDeadline_descriptionCarryingTheSaveDelimiter_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                Parser.parseDeadline("deadline submit ip | tp by 2026-09-01"));
    }

    @Test
    public void parseDeadline_dateThatDoesNotExist_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                Parser.parseDeadline("deadline submit ip by 2026-02-30"));
    }

    @Test
    public void parseEvent_endSeparatorBeforeStartSeparator_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                Parser.parseEvent("event demo to 2026-09-04 from 2026-09-03"));
    }

    @Test
    public void parseEvent_sameStartAndEndDate_returnsEvent() {
        assertEquals("[E][ ] demo (from: 03 Sep 2026 to: 03 Sep 2026)",
                Parser.parseEvent("event demo from 2026-09-03 to 2026-09-03").toString());
    }

    @Test
    public void parseEvent_sameStartAndEndTime_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                Parser.parseEvent("event demo from 2026-09-03 1400 to 2026-09-03 1400"));
    }

    @Test
    public void parseEvent_descriptionCarryingBothSeparators_splitsAtTheLastOnes() {
        assertEquals("[E][ ] talk from the heart to all (from: 03 Sep 2026 to: 04 Sep 2026)",
                Parser.parseEvent("event talk from the heart to all from 2026-09-03 to 2026-09-04")
                        .toString());
    }

    @Test
    public void parseEvent_blankParts_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                Parser.parseEvent("event     from 2026-09-03 to 2026-09-04"));
        assertThrows(SerangoonerException.class, () ->
                Parser.parseEvent("event demo from    to 2026-09-04"));
        assertThrows(SerangoonerException.class, () ->
                Parser.parseEvent("event demo from to 2026-09-04"));
    }

    @Test
    public void parseEvent_extraSpacesAroundParts_trimsThem() {
        assertEquals("[E][ ] orbital (from: 03 Sep 2026 to: 04 Sep 2026)",
                Parser.parseEvent("event  orbital  from  2026-09-03  to  2026-09-04").toString());
    }

    @Test
    public void parseTaskNumber_zeroOrNegative_returnsItForTheListToRefuse() {
        assertEquals(0, Parser.parseTaskNumber("mark 0", CommandType.MARK));
        assertEquals(-1, Parser.parseTaskNumber("delete -1", CommandType.DELETE));
    }

    @Test
    public void parseTaskNumber_numberTooLargeForAnInt_exceptionThrown() {
        assertThrows(SerangoonerException.class, () ->
                Parser.parseTaskNumber("mark 99999999999", CommandType.MARK));
    }

    @Test
    public void parseDateRange_sameDateAtBothEnds_coversThatDayAlone() {
        DateRange range = Parser.parseDateRange("on 2026-09-01 to 2026-09-01");
        assertEquals(LocalDate.of(2026, 9, 1), range.start());
        assertEquals(LocalDate.of(2026, 9, 1), range.end());
    }

    @Test
    public void parse_surroundingWhitespace_stillFindsTheCommand() {
        assertTrue(Parser.parse("   list   ") instanceof ListCommand);
        assertTrue(Parser.parse("  bye ") instanceof ExitCommand);
    }

    @Test
    public void parse_commandTakingANumber_carriesThatNumberIntoTheCommand(
            @TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("write essay")));
        Storage storage = new Storage(directory.resolve("tasks.txt"));

        Parser.parse("delete 2").execute(tasks, new Ui(), storage);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.getTasks().get(0).toString());
    }

    @Test
    public void parseEvent_descriptionCarryingTheEndSeparator_looksForItAfterTheStartSeparator() {
        // " to " inside the description must not be mistaken for the one that
        // introduces the end date, so the search starts at " from ".
        assertEquals("[E][ ] go to town (from: 03 Sep 2026 to: 04 Sep 2026)",
                Parser.parseEvent("event go to town from 2026-09-03 to 2026-09-04").toString());
    }

    @Test
    public void parse_markAndUnmark_carryTheirNumbersIntoTheCommand(@TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("write essay")));
        Ui ui = new Ui();
        Storage storage = new Storage(directory.resolve("tasks.txt"));

        Parser.parse("mark 2").execute(tasks, ui, storage);

        assertFalse(tasks.getTasks().get(0).isDone());
        assertTrue(tasks.getTasks().get(1).isDone());

        Parser.parse("unmark 2").execute(tasks, ui, storage);

        assertFalse(tasks.getTasks().get(1).isDone());
    }

    @Test
    public void parse_onCommand_carriesTheWholeRangeIntoTheCommand(@TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(
                new Deadline("submit ip", "2026-09-01"),
                new Deadline("submit tp", "2026-09-09")));
        String response = Parser.parse("on 2026-09-01 to 2026-09-09")
                .execute(tasks, new Ui(), new Storage(directory.resolve("tasks.txt")));

        assertTrue(response.contains("submit ip"));
        assertTrue(response.contains("submit tp"));
    }

    @Test
    public void parse_findKeyword_returnsFindCommand() {
        assertTrue(Parser.parse("find book") instanceof FindCommand);
    }

    @Test
    public void parseKeyword_singleWord_returnsIt() {
        assertEquals("book", Parser.parseKeyword("find book"));
    }

    @Test
    public void parseKeyword_severalWords_returnsTheWholePhrase() {
        assertEquals("read book", Parser.parseKeyword("find read book"));
    }

    @Test
    public void parseKeyword_surroundingWhitespace_ignoresIt() {
        assertEquals("book", Parser.parseKeyword("   find   book   "));
    }

    @Test
    public void parseKeyword_noKeyword_exceptionThrown() {
        assertThrows(SerangoonerException.class, () -> Parser.parseKeyword("find"));
        assertThrows(SerangoonerException.class, () -> Parser.parseKeyword("find   "));
    }

    @Test
    public void parse_findWithoutAKeyword_exceptionThrown() {
        assertThrows(SerangoonerException.class, () -> Parser.parse("find"));
    }

    @Test
    public void parse_findCommand_carriesTheKeywordIntoTheCommand(@TempDir Path directory) {
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("write essay")));
        String response = Parser.parse("find book")
                .execute(tasks, new Ui(), new Storage(directory.resolve("tasks.txt")));

        assertTrue(response.contains("read book"));
        assertFalse(response.contains("write essay"));
    }

    @Test
    public void parseDeadline_chineseDescription_keepsItAsTyped() {
        assertEquals("[D][ ] 交报告 (by: 01 Sep 2026)",
                Parser.parseDeadline("deadline 交报告 by 2026-09-01").toString());
    }
}
