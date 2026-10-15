package jeff.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import jeff.task.Event;
import jeff.task.Todo;
import jeff.task.UpdateField;

public class ParserAdditionalTest {

    @Test
    public void getCommandWord_whitespaceAroundCommand_returnsCommand() {
        assertEquals("todo", Parser.getCommandWord("  todo read book  "));
        assertEquals("mark", Parser.getCommandWord("\tmark\t1"));
    }

    @Test
    public void getCommandWord_blankInput_returnsEmptyString() {
        assertEquals("", Parser.getCommandWord(""));
        assertEquals("", Parser.getCommandWord(" \t "));
    }

    @Test
    public void parseTodo_validDescription_returnsIncompleteTodo() {
        Todo todo = Parser.parseTodo("todo   read  Java book   ");

        assertEquals("read  Java book", todo.getDescription());
        assertFalse(todo.isDone());
    }

    @Test
    public void parseTodo_missingDescription_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, (
                ) -> Parser.parseTodo("todo   "));

        assertEquals("Missing description.", exception.getMessage());
    }

    @Test
    public void parseTaskNumber_validInput_returnsOneBasedNumber() {
        assertEquals(1, Parser.parseTaskNumber("mark 1"));
        assertEquals(12, Parser.parseTaskNumber("  delete   12  "));
    }

    @Test
    public void parseTaskNumber_missingNumber_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, (
                ) -> Parser.parseTaskNumber("mark"));

        assertEquals("Please enter a task number.", exception.getMessage());
    }

    @Test
    public void parseTaskNumber_invalidNumber_exceptionThrown() {
        String[] commands = {
            "mark abc",
            "mark 1.5",
            "mark 1 2",
            "mark 2147483648"
        };

        for (String command : commands) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class, (
                    ) -> Parser.parseTaskNumber(command));

            assertEquals(
                    "Please enter a valid task number.",
                    exception.getMessage(),
                    command);
        }
    }

    @Test
    public void parseEvent_validTextTimes_preservesAllDetails() {
        Event event = Parser.parseEvent(
                "event project meeting /from Monday afternoon /to Tuesday morning");

        assertEquals("project meeting", event.getDescription());
        assertEquals("Monday afternoon", event.getStartTime());
        assertEquals("Tuesday morning", event.getEndTime());
        assertFalse(event.isDone());
    }

    @Test
    public void parseEvent_missingSeparator_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, (
                ) -> Parser.parseEvent("event project meeting 1400 1600"));

        assertEquals(
                "An event must use: "
                        + "event <description> /from <time> /to <time>",
                exception.getMessage());
    }

    @Test
    public void parseFindKeyword_phrase_preservesCaseAndInternalSpaces() {
        assertEquals(
                "Java  book",
                Parser.parseFindKeyword("find   Java  book   "));
    }

    @Test
    public void parseUpdate_eachField_returnsExpectedField() {
        assertEquals(
                UpdateField.DESCRIPTION,
                Parser.parseUpdate("update 1 /description revised").getField());
        assertEquals(
                UpdateField.DUE_DATE,
                Parser.parseUpdate("update 1 /by 2026-10-01").getField());
        assertEquals(
                UpdateField.START_TIME,
                Parser.parseUpdate("update 1 /from 1400").getField());
        assertEquals(
                UpdateField.END_TIME,
                Parser.parseUpdate("update 1 /to 1600").getField());
    }

    @Test
    public void parseUpdate_descriptionWithSpaces_returnsCompleteValue() {
        UpdateRequest request = Parser.parseUpdate(
                "update 2 /description read  Java book");

        assertEquals(2, request.getTaskNumber());
        assertEquals(UpdateField.DESCRIPTION, request.getField());
        assertEquals("read  Java book", request.getValue());
    }
}
