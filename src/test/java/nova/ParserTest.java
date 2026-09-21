package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    public void parse_updateCommand_parsesNumberAndDescription() {
        Parser.ParsedCommand command = Parser.parse("update  2  revised  homework /by tomorrow  ");

        assertEquals(Parser.CommandType.UPDATE, command.getType());
        assertEquals(2, command.getTaskNumber());
        assertEquals("revised  homework /by tomorrow", command.getDescription());
    }

    @Test
    public void parse_incompleteUpdate_returnsUsageError() {
        for (String input : new String[] {"update", "update ", "update 1", "update 1   "}) {
            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class, () -> Parser.parse(input));
            assertEquals("Please use: update NUMBER DESCRIPTION", error.getMessage());
        }
    }

    @Test
    public void parse_updateInvalidNumber_returnsNumberError() {
        for (String number : new String[] {"abc", "1.5", "2147483648"}) {
            IllegalArgumentException error = assertThrows(
                    IllegalArgumentException.class, () -> Parser.parse("update " + number + " revised"));
            assertEquals("That task number does not exist.", error.getMessage());
        }
    }

    @Test
    public void parse_todoCommand_parsesDescription() {
        Parser.ParsedCommand command =
                Parser.parse("todo read book");

        assertEquals(
                Parser.CommandType.TODO,
                command.getType()
        );

        assertEquals(
                "read book",
                command.getDescription()
        );
    }

    @Test
    public void parse_deadlineCommand_parsesDateTime() {
        Parser.ParsedCommand command =
                Parser.parse(
                        "deadline return book /by 2019-12-02 1800"
                );

        assertEquals(
                Parser.CommandType.DEADLINE,
                command.getType()
        );

        assertEquals(
                "return book",
                command.getDescription()
        );

        assertEquals(
                LocalDateTime.of(
                        2019,
                        12,
                        2,
                        18,
                        0
                ),
                command.getDeadline()
        );
    }

    @Test
    public void parse_eventCommand_parsesTimes() {
        Parser.ParsedCommand command =
                Parser.parse(
                        "event meeting /from 2pm /to 4pm"
                );

        assertEquals(
                Parser.CommandType.EVENT,
                command.getType()
        );

        assertEquals(
                "meeting",
                command.getDescription()
        );

        assertEquals(
                "2pm",
                command.getFrom()
        );

        assertEquals(
                "4pm",
                command.getTo()
        );
    }

    @Test
    public void parse_markCommand_parsesTaskNumber() {
        Parser.ParsedCommand command =
                Parser.parse("mark 3");

        assertEquals(
                Parser.CommandType.MARK,
                command.getType()
        );

        assertEquals(
                3,
                command.getTaskNumber()
        );
    }

    @Test
    public void parse_invalidDeadline_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Parser.parse("deadline return book /by tomorrow"));
    }

    @Test
    public void parse_missingTodoDescription_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Parser.parse("todo"));
    }

    @Test
    public void parse_invalidTaskNumber_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Parser.parse("delete abc"));
    }

    @Test
    public void parse_unknownCommand_returnsUnknown() {
        Parser.ParsedCommand command =
                Parser.parse("something random");

        assertEquals(
                Parser.CommandType.UNKNOWN,
                command.getType()
        );
    }

    @Test
    public void parse_surroundingAndRepeatedWhitespace_parsesCommands() {
        Parser.ParsedCommand todo = Parser.parse("  todo    read book   ");
        Parser.ParsedCommand deadline = Parser.parse(
                "deadline return book   /by   2026-09-18 2100");
        Parser.ParsedCommand event = Parser.parse(
                "event consultation   /from   2pm   /to   4pm");

        assertEquals("read book", todo.getDescription());
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2026, 9, 18, 21, 0), deadline.getDeadline());
        assertEquals("consultation", event.getDescription());
        assertEquals("2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
    }

    @Test
    public void parse_missingArguments_returnsCommandSpecificErrors() {
        String[][] invalidInputs = {
            {"mark", "Please use: mark NUMBER"},
            {"delete", "Please use: delete NUMBER"},
            {"deadline", "Please use: deadline DESCRIPTION /by yyyy-MM-dd HHmm"},
            {"event", "Please provide both /from and /to."},
            {"find", "Please provide a keyword to find."}
        };

        for (String[] invalidInput : invalidInputs) {
            IllegalArgumentException error = assertInvalidInput(invalidInput[0]);
            assertEquals(invalidInput[1], error.getMessage());
        }
    }

    @Test
    public void parse_invalidCalendarDate_returnsDateError() {
        String invalidDeadline = "deadline invalid date /by 2026-02-30 1800";
        IllegalArgumentException error = assertInvalidInput(invalidDeadline);

        assertEquals("Please enter the deadline as yyyy-MM-dd HHmm.", error.getMessage());
    }

    @Test
    public void parse_blankEventFields_returnsEventError() {
        String[] invalidInputs = {
            "event /from 2pm /to 4pm",
            "event consultation /from /to 4pm",
            "event consultation /from 2pm /to"
        };

        for (String invalidInput : invalidInputs) {
            IllegalArgumentException error = assertInvalidInput(invalidInput);
            assertEquals("Please provide both /from and /to.", error.getMessage());
        }
    }

    @Test
    public void parse_storageBreakingDescription_returnsDescriptionError() {
        IllegalArgumentException error = assertInvalidInput("todo first line\nsecond line");

        assertEquals("Descriptions cannot contain tabs or line breaks.", error.getMessage());
    }

    private static IllegalArgumentException assertInvalidInput(String input) {
        return assertThrows(IllegalArgumentException.class, () -> Parser.parse(input));
    }
}
