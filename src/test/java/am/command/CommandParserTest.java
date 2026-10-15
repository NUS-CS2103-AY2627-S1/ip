package am.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

import am.task.EventTask;
import am.task.TodoTask;

/** Tests command parsing, recovery messages, and preservation of description text. */
public class CommandParserTest {
    @Test
    void parsesCommandsWithoutArgumentsAndIgnoresBlankInput() {
        assertInstanceOf(Command.ByeCommand.class, CommandParser.parse("bye"));
        assertInstanceOf(Command.ListCommand.class, CommandParser.parse("  list  "));
        assertInstanceOf(Command.PastCommand.class, CommandParser.parse("\tpast\t"));
        for (String input : new String[]{"", "   ", "\t\r\n", "\u2003"}) {
            assertInstanceOf(Command.EmptyCommand.class, CommandParser.parse(input), input);
        }
    }

    @Test
    void parsesFindKeywordWithoutRestrictingSearchCharacters() {
        Command.FindCommand command = assertInstanceOf(Command.FindCommand.class,
                CommandParser.parse("  find   read/write  | notes   "));
        assertEquals("read/write  | notes", command.getKeyword());
    }

    @Test
    void parsesTaskIndexesAsZeroBasedIndexes() {
        Command.MarkCommand mark = assertInstanceOf(Command.MarkCommand.class, CommandParser.parse(" mark   +02 "));
        Command.UnmarkCommand unmark = assertInstanceOf(Command.UnmarkCommand.class, CommandParser.parse("unmark 2"));
        Command.DeleteTaskCommand delete = assertInstanceOf(Command.DeleteTaskCommand.class,
                CommandParser.parse("delete 2"));
        assertEquals(1, mark.getIndex());
        assertEquals(1, unmark.getIndex());
        assertEquals(1, delete.getIndex());
        assertEquals(Integer.MAX_VALUE - 1, assertInstanceOf(Command.MarkCommand.class,
                CommandParser.parse("mark 2147483647")).getIndex());
    }

    @Test
    void preservesDescriptionSpacingAndEmbeddedSlashesForEveryTaskType() {
        Map<String, String> examples = Map.of(
                "  todo   read/write  notes   ", "T | 0 | read/write  notes",
                " deadline   read/write  notes   /by   2026-09-12   1800  ",
                "D | 0 | read/write  notes | 2026-09-12T18:00",
                " event   read/write  notes   /from 2026-09-12 1400   /to 2026-09-12 1600  ",
                "E | 0 | read/write  notes | 2026-09-12T14:00 | 2026-09-12T16:00");
        for (Map.Entry<String, String> example : examples.entrySet()) {
            Command.AddTaskCommand command = assertInstanceOf(Command.AddTaskCommand.class,
                    CommandParser.parse(example.getKey()));
            assertEquals(example.getValue(), command.getTask().toSerialized());
        }
    }

    @Test
    void parsesDeadlineWithDateOnly() {
        Command.AddTaskCommand command = assertInstanceOf(Command.AddTaskCommand.class,
                CommandParser.parse("deadline submit report /by 2026-08-28"));
        assertEquals("D | 0 | submit report | 2026-08-28T23:59", command.getTask().toSerialized());
    }

    @Test
    void parsesEditFieldsInAnyOrderAndPreservesDescriptionSpacing() {
        Command.EditCommand command = assertInstanceOf(Command.EditCommand.class,
                CommandParser.parse("  edit   3   /to 1800 /name read/write  notes /from 1500  "));
        assertEquals(3, command.getTaskNumber());
        EventTask original = new EventTask("old", "2026-09-10 1400", "2026-09-10 1600");
        assertEquals("E | 0 | read/write  notes | 2026-09-10T15:00 | 2026-09-10T18:00",
                command.getEdit().applyTo(original).toSerialized());
    }

    @Test
    void acceptsSignedEditIndexes() {
        Command.EditCommand command = assertInstanceOf(Command.EditCommand.class,
                CommandParser.parse("edit +01 /name read/write"));
        assertEquals(1, command.getTaskNumber());
        assertEquals("T | 0 | read/write", command.getEdit().applyTo(new TodoTask("old")).toSerialized());
    }

    @Test
    void showsTheCommonSyntaxMessageAndAnExampleForEveryCommand() {
        Map<String, String> examples = Map.ofEntries(
                Map.entry("bye extra", "bye"),
                Map.entry("list extra", "list"),
                Map.entry("past extra", "past"),
                Map.entry("find", "find milk"),
                Map.entry("todo", "todo buy milk"),
                Map.entry("deadline", "deadline report /by 2026-09-12 1800"),
                Map.entry("event", "event meeting /from 2026-09-12 1400 /to 2026-09-12 1600"),
                Map.entry("edit", "edit 1 /name buy milk"),
                Map.entry("mark", "mark 1"),
                Map.entry("unmark", "unmark 1"),
                Map.entry("delete", "delete 1"));
        for (Map.Entry<String, String> example : examples.entrySet()) {
            assertSyntaxError(example.getKey(), example.getValue());
            CommandParser.parse(example.getValue());
        }
    }

    @Test
    void rejectsMalformedOversizedAndNonPositiveTaskNumbersUniformly() {
        for (String command : new String[]{"mark", "unmark", "delete", "edit"}) {
            String suffix = command.equals("edit") ? " /name x" : "";
            String example = command + " 1" + (command.equals("edit") ? " /name buy milk" : "");
            for (String value : new String[]{"abc", "1.5", "+", "1e2", "2147483648", "-2147483649",
                "999999999999999999999999999999", "0", "-0", "-1", "-2147483648"}) {
                assertSyntaxError(command + " " + value + suffix, example);
            }
        }
        assertSyntaxError("mark 1 extra", "mark 1");
        assertSyntaxError("edit /name x", "edit 1 /name buy milk");
    }

    @Test
    void rejectsMissingDescriptionsMarkersAndValues() {
        for (String input : new String[]{"deadline /by 2026-09-12", "deadline report", "deadline report /by"}) {
            assertSyntaxError(input, "deadline report /by 2026-09-12 1800");
        }
        for (String input : new String[]{"event /from 2026-09-12 /to 2026-09-12", "event meeting",
            "event meeting /to 2026-09-12", "event meeting /from 2026-09-12",
            "event meeting /from /to 2026-09-12", "event meeting /from 2026-09-12 /to"}) {
            assertSyntaxError(input, "event meeting /from 2026-09-12 1400 /to 2026-09-12 1600");
        }
        for (String input : new String[]{"edit 1", "edit 1 renamed", "edit 1 /name", "edit 1 /by",
            "edit 1 /from", "edit 1 /to", "edit 1 /to /name x", "edit 1 /from /to /name x"}) {
            assertSyntaxError(input, "edit 1 /name buy milk");
        }
    }

    @Test
    void rejectsRepeatedParametersIncludingEmptyAndSeparatedDuplicates() {
        for (String input : new String[]{"deadline report /by 2026-09-12 /by 2026-09-13",
            "deadline report /by /by 2026-09-13"}) {
            assertSyntaxError(input, "deadline report /by 2026-09-12 1800");
        }
        for (String marker : new String[]{"/from", "/to"}) {
            assertSyntaxError("event meeting /from 2026-09-12 /to 2026-09-12 " + marker + " 2026-09-13",
                    "event meeting /from 2026-09-12 1400 /to 2026-09-12 1600");
        }
        for (String marker : new String[]{"/name", "/by", "/from", "/to"}) {
            assertSyntaxError("edit 1 " + marker + " a " + marker + " b", "edit 1 /name buy milk");
            assertSyntaxError("edit 1 " + marker + " " + marker + " b", "edit 1 /name buy milk");
        }
        assertSyntaxError("edit 1 /name first /by 1800 /name second", "edit 1 /name buy milk");
    }

    @Test
    void rejectsUnknownCreationMarkersInsteadOfIgnoringThem() {
        for (String input : new String[]{"deadline report /by 2026-09-12 /until 1800",
            "deadline report /until 1800 /by 2026-09-12", "deadline report /By 2026-09-12"}) {
            assertSyntaxError(input, "deadline report /by 2026-09-12 1800");
        }
        assertSyntaxError("event meeting /from 2026-09-12 /to 2026-09-12 /by 2026-09-13",
                "event meeting /from 2026-09-12 1400 /to 2026-09-12 1600");
    }

    @Test
    void rejectsDescriptionsThatWouldCorruptStorage() {
        for (String description : new String[]{"a|b", "|", "a\nb", "a\rb"}) {
            assertSyntaxError("todo " + description, "todo buy milk");
            assertSyntaxError("deadline " + description + " /by 2026-09-12",
                    "deadline report /by 2026-09-12 1800");
            assertSyntaxError("event " + description + " /from 2026-09-12 /to 2026-09-12",
                    "event meeting /from 2026-09-12 1400 /to 2026-09-12 1600");
            assertSyntaxError("edit 1 /name " + description, "edit 1 /name buy milk");
        }
    }

    @Test
    void rejectsMalformedEditMarkers() {
        for (String input : new String[]{"edit 1 text /name a", "edit 1 /name /tmp", "edit 1 /Name a",
            "edit 1 /name a /unknown b"}) {
            assertSyntaxError(input, "edit 1 /name buy milk");
        }
    }

    @Test
    void acceptsReorderedEventMarkersAndNormalizesDateSpacing() {
        Command.AddTaskCommand command = assertInstanceOf(Command.AddTaskCommand.class,
                CommandParser.parse("event meeting /to 2026-09-12   1600 /from 2026-09-12   1400"));
        assertEquals("E | 0 | meeting | 2026-09-12T14:00 | 2026-09-12T16:00", command.getTask().toSerialized());
    }

    @Test
    void showsUnknownCommandErrorsAndSyntaxExamplesForInvalidDates() {
        for (String input : new String[]{"wat", "LIST", "EDIT 1 /name a"}) {
            UnknownCommandException exception = assertThrows(
                    UnknownCommandException.class, () -> CommandParser.parse(input));
            assertEquals("AAAAHHHHHHHHHHHHHHH\nYou can't tell me to '" + input + "'", exception.getMessage());
        }
        for (String input : new String[]{"deadline report /by Sunday", "deadline report /by 2026-02-30",
            "event meeting /from tomorrow /to 2026-09-12"}) {
            InvalidCommandException exception = assertThrows(
                    InvalidCommandException.class, () -> CommandParser.parse(input));
            assertEquals("You messed up the command.\nExample: "
                    + (input.startsWith("deadline") ? "deadline report /by 2026-09-12 1800"
                            : "event meeting /from 2026-09-12 1400 /to 2026-09-12 1600"), exception.getMessage());
        }
    }

    /** Checks the complete user-facing syntax response for an invalid input. */
    private static void assertSyntaxError(String input, String example) {
        InvalidCommandException exception = assertThrows(
                InvalidCommandException.class, () -> CommandParser.parse(input), input);
        assertEquals("You messed up the command.\nExample: " + example, exception.getMessage(), input);
    }
}
