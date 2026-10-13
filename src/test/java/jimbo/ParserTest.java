package jimbo;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import jimbo.command.ByeCommand;
import jimbo.command.Command;
import jimbo.command.DeleteCommand;
import jimbo.command.FindCommand;
import jimbo.command.ListCommand;
import jimbo.command.MarkCommand;
import jimbo.command.SaveCommand;
import jimbo.command.TagCommand;
import jimbo.command.TaskCommand;
import jimbo.command.UnmarkCommand;
import jimbo.command.UntagCommand;

// Tests generated using Claude Sonnet 5

public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parse_todo_returnsTaskCommand() throws JimboException {
        assertInstanceOf(TaskCommand.class, parser.parse("todo buy pineapples"));
    }

    @Test
    public void parse_deadline_validInput_returnsTaskCommand() throws JimboException {
        assertInstanceOf(TaskCommand.class, parser.parse("deadline eat pineapples /by 2026-12-12 1212"));
    }

    @Test
    public void parse_deadline_missingByFlag_throwsJimboException() {
        assertThrows(JimboException.class, () -> parser.parse("deadline eat pineapples"));
        assertThrows(JimboException.class, () -> parser.parse("deadline eat pineapples 2026-12-12 1212"));
    }

    @Test
    public void parse_deadline_wrongFlagInsteadOfBy_throwsJimboException() {
        assertThrows(JimboException.class, () -> parser.parse("deadline eat pineapples /from 2026-12-12 1212"));
    }

    @Test
    public void parse_deadline_malformedDate_throwsJimboException() {
        assertThrows(JimboException.class, () -> parser.parse("deadline eat pineapples /by tomorrow"));
        assertThrows(JimboException.class, () -> parser.parse("deadline eat pineapples /by 12-12-2026 1212"));
    }

    @Test
    public void parse_deadline_extraParameter_throwsJimboException() {
        assertThrows(JimboException.class,
                () -> parser.parse("deadline eat pineapples /by 2026-12-12 1212 /extra oops"));
    }

    @Test
    public void parse_event_validInput_returnsTaskCommand() throws JimboException {
        assertInstanceOf(TaskCommand.class,
                parser.parse("event pineapple eating contest /from 2026-12-15 1200 /to 2026-12-15 1500"));
    }

    @Test
    public void parse_event_fromMustComeBeforeTo_throwsJimboExceptionIfReversed() {
        // User guide explicitly notes /from must come before /to.
        assertThrows(JimboException.class,
                () -> parser.parse("event pineapple eating contest /to 2026-12-15 1500 /from 2026-12-15 1200"));
    }

    @Test
    public void parse_event_endBeforeStart_throwsJimboException() {
        assertThrows(JimboException.class,
                () -> parser.parse("event pineapple eating contest /from 2026-12-15 1500 /to 2026-12-15 1200"));
    }

    @Test
    public void parse_event_missingFromOrTo_throwsJimboException() {
        assertThrows(JimboException.class,
                () -> parser.parse("event pineapple eating contest /from 2026-12-15 1200"));
        assertThrows(JimboException.class,
                () -> parser.parse("event pineapple eating contest /to 2026-12-15 1500"));
    }

    @Test
    public void parse_event_malformedDate_throwsJimboException() {
        assertThrows(JimboException.class,
                () -> parser.parse("event pineapple eating contest /from soon /to 2026-12-15 1500"));
    }

    @Test
    public void parse_list_returnsListCommand() throws JimboException {
        assertInstanceOf(ListCommand.class, parser.parse("list"));
    }

    @Test
    public void parse_bye_returnsByeCommandThatQuits() throws JimboException {
        Command command = parser.parse("bye");
        assertInstanceOf(ByeCommand.class, command);
        assertTrue(command.shouldQuit());
    }

    @Test
    public void parse_save_returnsSaveCommandThatSaves() throws JimboException {
        Command command = parser.parse("save");
        assertInstanceOf(SaveCommand.class, command);
        assertTrue(command.shouldSave());
    }

    @Test
    public void parse_delete_validIndex_returnsDeleteCommand() throws JimboException {
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1"));
    }

    @Test
    public void parse_delete_nonNumericIndex_throwsJimboException() {
        assertThrows(JimboException.class, () -> parser.parse("delete abc"));
    }

    @Test
    public void parse_delete_missingIndex_throwsJimboException() {
        assertThrows(JimboException.class, () -> parser.parse("delete"));
    }

    @Test
    public void parse_find_validInput_returnsFindCommand() throws JimboException {
        assertInstanceOf(FindCommand.class, parser.parse("find marbles"));
    }

    @Test
    public void parse_find_multiWordSearchText_isAccepted() throws JimboException {
        assertInstanceOf(FindCommand.class, parser.parse("find red marbles"));
    }

    @Test
    public void parse_find_withExtraFlag_throwsJimboException() {
        assertThrows(JimboException.class, () -> parser.parse("find marbles /extra oops"));
    }

    @Test
    public void parse_mark_validIndex_returnsMarkCommand() throws JimboException {
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1"));
    }

    @Test
    public void parse_unmark_validIndex_returnsUnmarkCommand() throws JimboException {
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1"));
    }

    @Test
    public void parse_markUnmark_nonNumericIndex_throwsJimboException() {
        assertThrows(JimboException.class, () -> parser.parse("mark one"));
        assertThrows(JimboException.class, () -> parser.parse("unmark one"));
    }

    @Test
    public void parse_markUnmark_missingIndex_throwsJimboException() {
        assertThrows(JimboException.class, () -> parser.parse("mark"));
        assertThrows(JimboException.class, () -> parser.parse("unmark"));
    }

    @Test
    public void parse_tag_validInput_returnsTagCommand() throws JimboException {
        assertInstanceOf(TagCommand.class, parser.parse("tag 1 /tag banana"));
    }

    @Test
    public void parse_untag_validInput_returnsUntagCommand() throws JimboException {
        assertInstanceOf(UntagCommand.class, parser.parse("untag 1 /tag banana"));
    }

    @Test
    public void parse_tag_missingTagFlag_throwsJimboException() {
        assertThrows(JimboException.class, () -> parser.parse("tag 1 banana"));
    }

    @Test
    public void parse_tag_nonNumericIndex_throwsJimboException() {
        assertThrows(JimboException.class, () -> parser.parse("tag one /tag banana"));
    }

    @Test
    public void parse_untag_missingTagFlag_throwsJimboException() {
        assertThrows(JimboException.class, () -> parser.parse("untag 1 banana"));
    }

    @Test
    public void parse_tagUntag_extraFlag_throwsJimboException() {
        assertThrows(JimboException.class, () -> parser.parse("tag 1 /tag banana /tag eggs"));
        assertThrows(JimboException.class, () -> parser.parse("untag 1 /tag banana /tag eggs"));
    }

    @Test
    public void parse_commandWord_isCaseInsensitive() throws JimboException {
        assertInstanceOf(ByeCommand.class, parser.parse("BYE"));
        assertInstanceOf(ListCommand.class, parser.parse("LiSt"));
        assertInstanceOf(TaskCommand.class, parser.parse("ToDo buy pineapples"));
    }

    @Test
    public void parse_unknownCommand_throwsJimboException() {
        assertThrows(JimboException.class, () -> parser.parse("dance"));
    }
}