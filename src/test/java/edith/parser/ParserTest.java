package edith.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import edith.command.AddCommand;
import edith.command.DeleteCommand;
import edith.command.ExitCommand;
import edith.command.FindCommand;
import edith.command.HelpCommand;
import edith.command.ListCommand;
import edith.command.MarkCommand;
import edith.command.TagCommand;
import edith.command.UnmarkCommand;
import edith.command.UntagCommand;
import edith.exception.EdithException;

/** Tests translation of user input into executable commands. */
public class ParserTest {
    @Test
    public void parse_whitespaceAndRepeatedMarkers_handlesOrRejectsInput() throws EdithException {
        assertInstanceOf(AddCommand.class, Parser.parse("  todo   read book  "));
        assertThrows(EdithException.class, () ->
                Parser.parse("deadline report /by 2026-09-20 /by 2026-09-21"));
        assertThrows(EdithException.class, () ->
                Parser.parse("event meeting /from 2026-09-20 /to 2026-09-21 /to 2026-09-22"));
        assertThrows(EdithException.class, () -> Parser.parse("todo book /tags #a /tags #b"));
    }

    @Test
    public void parse_eventWithNonIncreasingOrInvalidDates_rejectsInput() {
        assertThrows(EdithException.class, () ->
                Parser.parse("event meeting /from 2026-09-21 /to 2026-09-20"));
        assertThrows(EdithException.class, () ->
                Parser.parse("event meeting /from 2026-09-20 /to 2026-09-20"));
        assertThrows(EdithException.class, () ->
                Parser.parse("deadline report /by 2026-02-30 1200"));
    }
    @Test
    public void parse_validCommands_returnsExpectedCommandTypes() throws EdithException {
        assertInstanceOf(HelpCommand.class, Parser.parse(" help "));
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class, Parser.parse("deadline submit work /by 2026-09-06"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("event meeting /from 2026-09-06 1400 /to 2026-09-06 1500"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(TagCommand.class, Parser.parse("tag 2 #fun #school"));
        assertInstanceOf(UntagCommand.class, Parser.parse("untag 2 #fun"));
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        EdithException exception = assertThrows(EdithException.class, () -> Parser.parse("remind me"));

        assertEquals("I don't know what that means. Type 'help' to list all commands.",
                exception.getMessage());
    }

    @Test
    public void parse_findWithoutKeyword_exceptionThrown() {
        assertEquals("Please provide a word or phrase to find. Use: find KEYWORD",
                assertThrows(EdithException.class, () -> Parser.parse("find")).getMessage());
    }

    @Test
    public void parse_todoWithoutDescription_exceptionThrown() {
        assertEquals("The description of a todo cannot be empty.",
                assertThrows(EdithException.class, () -> Parser.parse("todo")).getMessage());
    }

    @Test
    public void parse_invalidDeadlineForms_exceptionThrown() {
        assertThrows(EdithException.class, () -> Parser.parse("deadline submit work"));
        assertThrows(EdithException.class, () -> Parser.parse("deadline /by 2026-09-06"));
        assertThrows(EdithException.class, () -> Parser.parse("deadline submit work /by"));
        assertThrows(EdithException.class, () -> Parser.parse("deadline submit work /by tomorrow"));
    }

    @Test
    public void parse_invalidEventForms_exceptionThrown() {
        assertThrows(EdithException.class, () -> Parser.parse("event meeting"));
        assertThrows(EdithException.class, () -> Parser.parse("event /from 2026-09-06 /to 2026-09-07"));
        assertThrows(EdithException.class, () -> Parser.parse("event meeting /from /to 2026-09-07"));
        assertThrows(EdithException.class, () -> Parser.parse("event meeting /from 2026-09-06 /to"));
        assertThrows(EdithException.class, () -> Parser.parse("event meeting /to 2026-09-07 /from 2026-09-06"));
        assertThrows(EdithException.class, () -> Parser.parse("event meeting /from tomorrow /to 2026-09-07"));
        assertThrows(EdithException.class, () -> Parser.parse("event meeting /from 2026-09-06 /to tomorrow"));
    }

    @Test
    public void parse_taskCommandWithoutWholeNumber_exceptionThrown() {
        assertEquals("Please provide a whole-number task number. Use: mark NUMBER",
                assertThrows(EdithException.class, () -> Parser.parse("mark two")).getMessage());
        assertEquals("Please provide a whole-number task number. Use: unmark NUMBER",
                assertThrows(EdithException.class, () -> Parser.parse("unmark 1.5")).getMessage());
        assertEquals("Please provide a whole-number task number. Use: delete NUMBER",
                assertThrows(EdithException.class, () -> Parser.parse("delete")).getMessage());
    }

    @Test
    public void parse_taggedTaskCreation_acceptsAllTaskTypes() throws EdithException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book /tags #fun #school"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("deadline submit work /by 2026-09-20 1800 /tags #school"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("event meeting /from 2026-09-20 /to 2026-09-21 /tags #team"));
    }

    @Test
    public void parse_taggedTaskCreation_rejectsMissingOrMalformedTags() {
        assertEquals("Please provide at least one tag after /tags.",
                assertThrows(EdithException.class, () -> Parser.parse("todo read book /tags")).getMessage());
        assertEquals("Tags must start with # and contain only letters, digits, underscores, or hyphens.",
                assertThrows(EdithException.class, () ->
                        Parser.parse("todo read book /tags #fun #bad!")).getMessage());
        assertThrows(EdithException.class, () ->
                Parser.parse("todo read book /tags #fun /tags #school"));
        assertThrows(EdithException.class, () ->
                Parser.parse("deadline report /by 2026-09-20 /tags #"));
    }

    @Test
    public void parse_tagCommands_rejectsMissingNumbersTagsAndMalformedTags() {
        assertEquals("Please provide a whole-number task number. Use: tag NUMBER #tag [#tag ...]",
                assertThrows(EdithException.class, () -> Parser.parse("tag")).getMessage());
        assertEquals("Please provide a whole-number task number. Use: untag NUMBER #tag [#tag ...]",
                assertThrows(EdithException.class, () -> Parser.parse("untag two #fun")).getMessage());
        assertEquals("Please provide at least one tag. Use: tag NUMBER #tag [#tag ...]",
                assertThrows(EdithException.class, () -> Parser.parse("tag 2")).getMessage());
        assertEquals("Please provide at least one tag. Use: untag NUMBER #tag [#tag ...]",
                assertThrows(EdithException.class, () -> Parser.parse("untag 2")).getMessage());
        assertEquals("Tags must start with # and contain only letters, digits, underscores, or hyphens.",
                assertThrows(EdithException.class, () -> Parser.parse("tag 2 #fun #bad!")).getMessage());
        assertThrows(EdithException.class, () -> Parser.parse("untag 2 fun"));
        assertThrows(EdithException.class, () -> Parser.parse("tag 2 /as #fun"));
    }
}
