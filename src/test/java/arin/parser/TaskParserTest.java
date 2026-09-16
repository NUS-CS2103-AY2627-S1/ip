package arin.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import arin.exception.ArinException;
import arin.task.Deadline;
import arin.task.Event;
import arin.task.Todo;

/**
 * Preserves task-command behavior while parsing is separated from the main loop.
 */
class TaskParserTest {
    @Test
    void parse_todoWithExtraSpaces_returnsTrimmedTodo() throws ArinException {
        Todo task = assertInstanceOf(Todo.class, TaskParser.parse("todo   read a book  "));

        assertEquals("read a book", task.getDescription());
        assertEquals("T |   | read a book", task.storageToString());
    }

    @Test
    void parse_validDeadline_preservesDateAndStorageFormat() throws ArinException {
        Deadline task = assertInstanceOf(Deadline.class,
                TaskParser.parse("deadline return book /by 2019-12-02"));

        assertEquals("[D][ ] return book (by: Dec 02 2019)", task.toString());
        assertEquals("D |   | return book | 2019-12-02", task.storageToString());
    }

    @Test
    void parse_validEvent_preservesTimesAndStorageFormat() throws ArinException {
        Event task = assertInstanceOf(Event.class,
                TaskParser.parse("event meeting /from Aug 6th 2pm /to Aug 6th 4pm"));

        assertEquals("[E][ ] meeting (from: Aug 6th 2pm to: Aug 6th 4pm)", task.toString());
        assertEquals("E |   | meeting | Aug 6th 2pm | Aug 6th 4pm", task.storageToString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"todo", "todo ", "todo \t "})
    void parse_missingTodoDescription_preservesErrorMessage(String command) {
        ArinException exception = assertThrows(ArinException.class, () -> TaskParser.parse(command));

        assertEquals("Please include a description after 'todo'.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"deadline", "deadline return book", "deadline /by 2019-12-02"})
    void parse_missingDeadlineParts_preservesUsageMessage(String command) {
        ArinException exception = assertThrows(ArinException.class, () -> TaskParser.parse(command));

        assertEquals("Use: deadline <description> /by <date and time>.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"deadline book /by tomorrow", "deadline book /by 2026-02-30"})
    void parse_invalidDeadlineDate_preservesDateError(String command) {
        ArinException exception = assertThrows(ArinException.class, () -> TaskParser.parse(command));

        assertEquals("Please use the date format yyyy-MM-dd, for example 2019-10-15.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"event", "event meeting /from 2pm", "event meeting /to 4pm /from 2pm"})
    void parse_invalidEventSeparators_preservesUsageMessage(String command) {
        ArinException exception = assertThrows(ArinException.class, () -> TaskParser.parse(command));

        assertEquals("Use: event <description> /from <start> /to <end>.", exception.getMessage());
    }

    @Test
    void parse_blankEventStart_preservesMissingDetailsMessage() {
        ArinException exception = assertThrows(ArinException.class,
                () -> TaskParser.parse("event meeting /from  /to 4pm"));

        assertEquals("An event needs a description, start time, and end time.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "todoextra read book", "deadlineextra book /by 2019-12-02", "EVENT meeting"})
    void parse_unknownCommand_preservesUnknownCommandMessage(String command) {
        ArinException exception = assertThrows(ArinException.class, () -> TaskParser.parse(command));

        assertEquals("I don't recognise that command. Try todo, deadline, event, list, mark, unmark, "
                + "delete, find, sort, or bye.", exception.getMessage());
    }
}
