package turtley.command;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

/** Tests dispatch from raw user input to the corresponding command type. */
class ParserTest {

    @Test
    void parse_emptyInput_returnsEmptyCommand() {
        assertAll(
                () -> assertInstanceOf(EmptyCommand.class, Parser.parse(null)),
                () -> assertInstanceOf(EmptyCommand.class, Parser.parse("")));
    }

    @Test
    void parse_recognizedCommands_returnsMatchingCommandTypes() {
        assertAll(
                () -> assertInstanceOf(ExitCommand.class, Parser.parse("bye")),
                () -> assertInstanceOf(ListCommand.class, Parser.parse("list")),
                () -> assertInstanceOf(FindCommand.class, Parser.parse("find book")),
                () -> assertInstanceOf(FindCommand.class, Parser.parse("find")),
                () -> assertInstanceOf(FilterCommand.class, Parser.parse("filter #fun")),
                () -> assertInstanceOf(HelpCommand.class, Parser.parse("help")),
                () -> assertInstanceOf(MarkCommand.class, Parser.parse("mark 2")),
                () -> assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 2")),
                () -> assertInstanceOf(TagCommand.class, Parser.parse("tag 2 #fun")),
                () -> assertInstanceOf(UntagCommand.class, Parser.parse("untag 2 #fun")),
                () -> assertInstanceOf(DeleteCommand.class, Parser.parse("delete")),
                () -> assertInstanceOf(DeleteCommand.class, Parser.parse("delete 2")),
                () -> assertInstanceOf(TimecheckCommand.class, Parser.parse("timecheck")),
                () -> assertInstanceOf(TimecheckCommand.class,
                        Parser.parse("timecheck 2026-08-27")),
                () -> assertInstanceOf(TodoCommand.class, Parser.parse("todo")),
                () -> assertInstanceOf(TodoCommand.class, Parser.parse("todo read book")),
                () -> assertInstanceOf(DeadlineCommand.class,
                        Parser.parse("deadline report /by 2026-08-27")),
                () -> assertInstanceOf(EventCommand.class,
                        Parser.parse("event meeting /from 2026-08-27 14:00 /to 15:00")));
    }

    @Test
    void parse_unrecognizedInput_returnsUnknownCommand() {
        assertAll(
                () -> assertInstanceOf(UnknownCommand.class, Parser.parse("unknown command")),
                () -> assertInstanceOf(UnknownCommand.class, Parser.parse(" ")),
                () -> assertInstanceOf(UnknownCommand.class, Parser.parse("mark")));
    }

    @Test
    void parse_invalidWhitespace_returnsUnknownCommand() {
        assertAll(
                () -> assertInstanceOf(UnknownCommand.class, Parser.parse(" todo read book")),
                () -> assertInstanceOf(UnknownCommand.class, Parser.parse("todo read book ")),
                () -> assertInstanceOf(UnknownCommand.class, Parser.parse("todo read  book")),
                () -> assertInstanceOf(UnknownCommand.class, Parser.parse("todo\tread book")));
    }
}
