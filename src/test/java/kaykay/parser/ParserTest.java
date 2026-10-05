package kaykay.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import kaykay.command.AddPlaceCommand;
import kaykay.command.DeadlineCommand;
import kaykay.command.DeleteCommand;
import kaykay.command.DeletePlaceCommand;
import kaykay.command.EditPlaceCommand;
import kaykay.command.EventCommand;
import kaykay.command.ExitCommand;
import kaykay.command.FindCommand;
import kaykay.command.FindPlacesCommand;
import kaykay.command.ListCommand;
import kaykay.command.ListPlacesCommand;
import kaykay.command.MarkCommand;
import kaykay.command.TodoCommand;
import kaykay.command.UnmarkCommand;
import kaykay.exception.KaykayException;

/** Tests the conversion of user input into executable commands. */
class ParserTest {
    private final Parser parser = new Parser();

    /** Checks that every supported command is mapped to the right command type. */
    @Test
    void parse_supportedCommands_returnsExpectedCommandTypes() throws KaykayException {
        assertInstanceOf(TodoCommand.class, parser.parse("todo revise notes"));
        assertInstanceOf(DeadlineCommand.class,
                parser.parse("deadline submit report /by 27 12 2026 09:00"));
        assertInstanceOf(EventCommand.class,
                parser.parse("event project meeting /from 26 12 2026 14:00 /to 26 12 2026 16:00"));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1"));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1"));
        assertInstanceOf(ListCommand.class, parser.parse("list"));
        assertInstanceOf(FindCommand.class, parser.parse("find notes"));
        assertInstanceOf(AddPlaceCommand.class,
                parser.parse("place add Burnt Ends /type restaurant /rating 5"));
        assertInstanceOf(ListPlacesCommand.class, parser.parse("place list"));
        assertInstanceOf(FindPlacesCommand.class, parser.parse("place find restaurant"));
        assertInstanceOf(EditPlaceCommand.class, parser.parse("place edit 1 /rating 4"));
        assertInstanceOf(DeletePlaceCommand.class, parser.parse("place delete 1"));
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
        assertTrue(parser.parse("bye").isExit());
        assertFalse(parser.parse("list").isExit());
    }

    /** Checks the parser's guidance for malformed commands and date/time values. */
    @Test
    void parse_invalidCommands_throwsKaykayExceptionWithGuidance() {
        assertParseError("", "Please enter a command.");
        assertParseError("   ", "Please enter a command.");
        assertParseError("todo", "A todo needs a description. Try: todo <description>.");
        assertParseError("find", "A find command needs a keyword. Try: find <keyword>.");
        assertParseError("deadline missing",
                "A deadline needs a description, date, and time. "
                        + "Use: deadline <description> /by dd MM yyyy HH:mm.");
        assertParseError("event meeting /from 31 12 2026 10:00",
                "An event needs a description plus start and end date/times. "
                        + "Use: event <description> /from dd MM yyyy HH:mm /to dd MM yyyy HH:mm.");
        assertParseError("event meeting /from 12 02 2004 /to 30 04 2005",
                "The event start date/time '12 02 2004' is invalid. Include both the date and 24-hour time: "
                        + "dd MM yyyy HH:mm (spaces between day, month, and year), e.g. 01 01 2026 18:30.");
        assertParseError("event meeting /from 31 12 2026 11:00 /to 31 12 2026 10:00",
                "The event end must be after its start.");
        assertParseError("event meeting /from 31 12 2026 11:00 /to 31 12 2026 11:00",
                "The event end must be after its start.");
        assertParseError("deadline report /by 31 12 2026 10:00 /by 01 01 2027 10:00",
                "The /by parameter can only be used once.");
        assertParseError("event meeting /from 31 12 2026 10:00 /from 31 12 2026 11:00 "
                        + "/to 31 12 2026 12:00",
                "The /from parameter can only be used once.");
        assertParseError("event meeting /from 31 12 2026 10:00 /to 31 12 2026 11:00 "
                        + "/to 31 12 2026 12:00",
                "The /to parameter can only be used once.");
        assertParseError("deadline report /by 31 02 2026 10:00",
                "The deadline date/time '31 02 2026 10:00' is invalid. Include both the date and 24-hour time: "
                        + "dd MM yyyy HH:mm (spaces between day, month, and year), e.g. 01 01 2026 18:30.");
        assertParseError("blah",
                "I don't recognise that command. Try todo, deadline, event, list, find, delete, mark, "
                        + "unmark, place, or bye.");
    }

    /** Checks validation and guidance for malformed place commands. */
    @Test
    void parse_invalidPlaceCommands_throwsKaykayExceptionWithGuidance() {
        assertParseError("place add",
                "A place needs a name. Try: place add <name> [/type <type>] "
                        + "[/location <location>] [/visited <date>] [/rating <1-5>] "
                        + "[/notes <notes>].");
        assertParseError("place add Cafe /rating 6",
                "A place rating must be a whole number from 1 to 5.");
        assertParseError("place add Cafe /visited 31 02 2026",
                "The place visit date '31 02 2026' is invalid. Please use dd MM yyyy, "
                        + "for example 10 09 2026.");
        assertParseError("place add Cafe /rating 5 /rating 4",
                "The /rating field can only be used once.");
        assertParseError("place add Cafe /Rating 5",
                "Unknown place field '/Rating'.");
        assertParseError("place find",
                "A place find command needs a keyword. Try: place find <keyword>.");
        assertParseError("place edit 1",
                "A place edit needs a place number and at least one field. "
                        + "Try: place edit <number> /rating <1-5>.");
        assertParseError("place delete nope",
                "Please provide an existing place number to delete.");
        assertParseError("place dance",
                "I don't recognise that place command. "
                        + "Try place add, place list, place find, place edit, or place delete.");
    }

    /** Checks that harmless extra whitespace is accepted at command boundaries. */
    @Test
    void parse_extraWhitespace_returnsExpectedCommandTypes() throws KaykayException {
        assertInstanceOf(TodoCommand.class, parser.parse("  todo   revise notes  "));
        assertInstanceOf(DeadlineCommand.class,
                parser.parse("deadline submit report   /by   27 12 2026 09:00"));
        assertInstanceOf(EventCommand.class,
                parser.parse("event meeting  /from   27 12 2026 09:00  /to   27 12 2026 10:00"));
        assertInstanceOf(AddPlaceCommand.class,
                parser.parse("place  add Cafe   /rating   5"));
    }

    /**
     * Checks that leading fields are not mistaken for a missing place name.
     */
    @Test
    void parse_placeWithoutName_rejectsLeadingFields() {
        String[] inputs = {
            "place add /rating 5",
            "place add   /type restaurant /rating 5",
            "place add /notes Nice coffee"
        };
        for (String input : inputs) {
            assertParseError(input,
                    "A place needs a name. Try: place add <name> [/type <type>] "
                            + "[/location <location>] [/visited <date>] [/rating <1-5>] "
                            + "[/notes <notes>].");
        }
    }

    /**
     * Checks missing values before another field, including edit and tab separators.
     */
    @Test
    void parse_placeWithMissingValue_rejectsAdjacentFields() {
        String[] fields = {"type", "location", "visited", "rating", "notes"};
        for (String field : fields) {
            String error = "The /" + field + " field needs a value.";
            assertParseError("place add Cafe /" + field + " /rating 5", error);
            assertParseError("place add Cafe /" + field, error);
            assertParseError("place edit 1 /" + field + "\t/notes Updated", error);
        }
        assertParseError("place edit 1 /name /rating 5", "The /name field needs a value.");
    }

    /** Verifies one parser failure and its user-facing message. */
    private void assertParseError(String input, String expectedMessage) {
        KaykayException exception = assertThrows(KaykayException.class, () -> parser.parse(input));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
