package tuesday.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import tuesday.exceptions.TuesdayExceptions;
import tuesday.processor.UserInputParser.IndexValidation;
import tuesday.processor.UserInputParser.TaskValidation;
import tuesday.processor.UserInputParser.ValidationCode;
import tuesday.task.Event;
import tuesday.task.TaskList;
import tuesday.task.Todo;

/**
 * Tests user-input parsing and the exception-based validation helpers without file access.
 */
public class UserInputParserTest {
    @Test
    public void getCommand_commandBoundaries_preservesRecognition() {
        String[] inputs = {" todo read", "deadline work", "event meet", "list", "find book", "mark 1",
            "unmark 1", "delete 1", "new", "old", "bye extra"};
        Command[] commands = {Command.TODO, Command.DEADLINE, Command.EVENT, Command.LIST, Command.FIND,
            Command.MARK, Command.UNMARK, Command.DELETE, Command.NEW, Command.OLD, Command.BYE};
        for (int i = 0; i < inputs.length; i++) {
            assertEquals(commands[i], UserInputParser.getCommand(inputs[i]), inputs[i]);
        }
        for (String input : new String[] {null, "", "  ", "TODO book", "todoSomething", "unknown"}) {
            assertEquals(Command.UNKNOWN, UserInputParser.getCommand(input), input);
        }
    }

    @Test
    public void parseTaskCreation_validInput_returnsTaskWithoutAnError() {
        String[][] cases = {
            {"todo  read book ", "[T][ ] read book"},
            {"deadline work /by 2026-09-20 10:30", "[D][ ] work (by: Sep 20 2026 10:30)"},
            {"event meeting /from 10:30 /to 2026-09-20", "[E][ ] meeting (from: 10:30 to: Sep 20 2026)"}
        };
        for (String[] example : cases) {
            TaskValidation result = UserInputParser.parseTaskCreation(
                    example[0], UserInputParser.getCommand(example[0]));
            assertNull(result.error());
            assertEquals(example[1], result.task().toString());
        }
    }

    @Test
    public void parseTaskCreation_invalidInput_returnsErrorWithoutTask() {
        TaskValidation missingDescription = UserInputParser.parseTaskCreation("todo", Command.TODO);
        assertNull(missingDescription.task());
        assertEquals(ValidationCode.MISSING_DESCRIPTION, missingDescription.error().code());
        assertEquals("todo", missingDescription.error().detail());

        TaskValidation invalidDate = UserInputParser.parseTaskCreation("deadline work /by bad", Command.DEADLINE);
        assertNull(invalidDate.task());
        assertEquals(ValidationCode.INVALID_DATE_TIME, invalidDate.error().code());
        assertEquals("/by", invalidDate.error().field());
        assertEquals("bad", invalidDate.error().detail());
    }

    @Test
    public void validateTaskIndex_taskCount_preservesIndicesAndErrorPrecedence() {
        IndexValidation valid = UserInputParser.validateTaskIndex("mark +2", 2, Command.MARK);
        assertEquals(1, valid.index());
        assertNull(valid.error());

        IndexValidation outOfRange = UserInputParser.validateTaskIndex("delete 2", 1, Command.DELETE);
        assertEquals(-1, outOfRange.index());
        assertEquals(ValidationCode.DELETE_OUT_OF_RANGE, outOfRange.error().code());
        assertEquals("2", outOfRange.error().detail());

        IndexValidation extraArgument = UserInputParser.validateTaskIndex("delete 1 extra", 1, Command.DELETE);
        assertEquals(-1, extraArgument.index());
        assertEquals(ValidationCode.INVALID_COMMAND_SYNTAX, extraArgument.error().code());
        assertEquals(ValidationCode.INVALID_COMMAND_SYNTAX,
                UserInputParser.validateTaskIndex("delete 2 extra", 1, Command.DELETE).error().code());
    }

    @Test
    public void parseDuplicateChoice_pendingReply_requiresExactChoice() {
        assertEquals(Command.NEW, UserInputParser.parseDuplicateChoice(" new "));
        assertEquals(Command.OLD, UserInputParser.parseDuplicateChoice("old"));
        assertEquals(Command.BYE, UserInputParser.parseDuplicateChoice(" bye extra "));
        for (String input : new String[] {null, "", "NEW", "new extra", "old extra", "list", "todo work"}) {
            assertEquals(Command.UNKNOWN, UserInputParser.parseDuplicateChoice(input), input);
        }
    }

    @Test
    public void parseFindKeyword_whitespace_preservesExistingExtraction() {
        assertEquals("read book", UserInputParser.parseFindKeyword("find   read book "));
        assertEquals("", UserInputParser.parseFindKeyword("find  "));
        assertEquals("d book", UserInputParser.parseFindKeyword(" find book"));
    }

    @Test
    public void parseDateTime_supportedForms_returnsCorrectComponents() {
        LocalDate date = LocalDate.of(2026, 9, 20);
        LocalTime time = LocalTime.of(10, 30);
        assertEquals(new Event.DateTimeValue(date, null), UserInputParser.parseDateTime("2026-09-20"));
        assertEquals(new Event.DateTimeValue(null, time), UserInputParser.parseDateTime("10:30"));
        assertEquals(new Event.DateTimeValue(date, time), UserInputParser.parseDateTime("2026-09-20 10:30"));
        assertNull(UserInputParser.parseDateTime("Sep 20 2026"));
    }

    @Test
    public void validators_legacyCalls_preservesExceptionsAndPayloads() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("existing"));
        assertTrue(UserInputParser.isAvailableMark("mark +1", tasks));
        assertTrue(UserInputParser.isAvailableMark("unmark 1", tasks));
        assertTrue(UserInputParser.isAvailableMark("\u2003mark\u20031", tasks));
        assertTrue(UserInputParser.isAvailableDelete("\u2003delete\u20031", tasks));
        assertTrue(UserInputParser.isAvailableDelete("delete 1", tasks));
        assertFalse(UserInputParser.isAvailableMark("delete 1", tasks));
        assertFalse(UserInputParser.isAvailableDelete("mark 1", tasks));
        assertFalse(UserInputParser.isAvailableMark("mark 1 extra", tasks));
        assertFalse(UserInputParser.isAvailableTaskCommand(null));
        assertFalse(UserInputParser.isAvailableTaskCommand("list"));
        assertThrows(NullPointerException.class, () -> UserInputParser.isAvailableMark(null, tasks));
        assertThrows(NullPointerException.class, () -> UserInputParser.isAvailableDelete(null, tasks));
        assertEquals("0", assertThrows(TuesdayExceptions.MarkTaskNumberOutOfRangeException.class, () ->
                UserInputParser.isAvailableMark("mark 0", tasks)).getMessage());
        assertFalse(UserInputParser.isAvailableMark("mark 0 extra", tasks));
        assertEquals("2", assertThrows(TuesdayExceptions.DeleteTaskNumberOutOfRangeException.class, () ->
                UserInputParser.isAvailableDelete("delete 2", tasks)).getMessage());
        for (String command : new String[] {"todo", "deadline", "event"}) {
            assertEquals(command, assertThrows(TuesdayExceptions.NoDescriptionException.class, () ->
                    UserInputParser.isAvailableTaskCommand(command)).getMessage());
        }
        assertEquals("", assertThrows(TuesdayExceptions.DeadlineMissingByDateException.class, () ->
                UserInputParser.isAvailableTaskCommand("deadline work")).getMessage());
        assertFalse(UserInputParser.isAvailableTaskCommand("deadline work /by bad"));
        assertFalse(UserInputParser.isAvailableTaskCommand("event work /to 11:00 /from 10:00"));
        assertEquals("event", assertThrows(TuesdayExceptions.EventMissingTimeException.class, () ->
                UserInputParser.isAvailableTaskCommand("event work /from 10:00")).getMessage());
    }

    @Test
    public void parseDateTime_calendarAndSyntaxBoundaries_preservesAcceptedValues() {
        for (String value : new String[] {"0000-02-29", "2000-02-29", "2024-02-29", "23:59", "00:00",
                "2026-09-20 10:00"}) {
            assertNotNull(UserInputParser.parseDateTime(value), value);
        }
        for (String value : new String[] {"1900-02-29", "2025-02-29", "2026-00-10", "2026-13-10",
                "2026-01-00", "2026-04-31", "24:00", "12:60", "1:00", "2026-9-20", " 10:00",
                "2026-09-20  10:00", "2026-09-20T10:00", ""}) {
            assertNull(UserInputParser.parseDateTime(value), value);
        }
    }

    @Test
    public void parseTaskCreation_multipleErrors_reportsFirstActionableField() {
        String[][] cases = {
            {"deadline /by bad", "MISSING_DESCRIPTION", ""},
            {"deadline work /by bad /by", "INVALID_COMMAND_SYNTAX", ""},
            {"deadline work", "MISSING_DEADLINE", "/by"},
            {"event /to bad", "MISSING_DESCRIPTION", ""},
            {"event work", "MISSING_EVENT_TIME", "/from and /to"},
            {"event work /to bad", "MISSING_EVENT_TIME", "/from"},
            {"event work /from bad", "MISSING_EVENT_TIME", "/to"},
            {"event work /to bad /from bad", "INVALID_COMMAND_SYNTAX", ""},
            {"event work /from bad /to", "MISSING_EVENT_TIME", "/to"},
            {"event work /from /to", "MISSING_EVENT_TIME", "/from and /to"},
            {"event work /from bad /to bad", "INVALID_DATE_TIME", "/from"},
            {"event work /from 10:00 /to bad", "INVALID_DATE_TIME", "/to"}
        };
        for (String[] example : cases) {
            TaskValidation result = UserInputParser.parseTaskCreation(
                    example[0], UserInputParser.getCommand(example[0]));
            assertNull(result.task(), example[0]);
            assertEquals(ValidationCode.valueOf(example[1]), result.error().code(), example[0]);
            assertEquals(example[2], result.error().field(), example[0]);
        }
    }
}
