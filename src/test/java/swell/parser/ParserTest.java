package swell.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import swell.exception.SwellException;
import swell.task.Deadline;
import swell.task.Event;
import swell.task.Task;
import swell.task.Todo;

public class ParserTest {
    @Test
    public void getTask_todoCommand_returnsTodoTask() throws SwellException {
        Task task = Parser.getTask("todo read book");

        assertInstanceOf(Todo.class, task);
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void getTask_todoCommandWithTags_returnsTaggedTodoTask() throws SwellException {
        Task task = Parser.getTask("todo email Alice #Acme #followup");

        assertInstanceOf(Todo.class, task);
        assertEquals("[T][ ] email Alice #Acme #followup", task.toString());
        assertIterableEquals(List.of("Acme", "followup"), task.getTags());
    }

    @Test
    public void getTask_deadlineCommandWithIsoDate_returnsFormattedDeadline() throws SwellException {
        Task task = Parser.getTask("deadline return book /by 2019-10-15");

        assertInstanceOf(Deadline.class, task);
        assertEquals("[D][ ] return book (by: Oct 15 2019)", task.toString());
    }

    @Test
    public void getTask_deadlineCommandWithIsoDateTime_returnsFormattedDeadline() throws SwellException {
        Task task = Parser.getTask("deadline return book /by 2019-10-15 1800");

        assertInstanceOf(Deadline.class, task);
        assertEquals("[D][ ] return book (by: Oct 15 2019 6:00 PM)", task.toString());
    }

    @Test
    public void getTask_deadlineCommandWithTag_returnsTaggedDeadlineTask() throws SwellException {
        Task task = Parser.getTask("deadline submit resume #Acme /by 2019-10-15");

        assertInstanceOf(Deadline.class, task);
        assertEquals("[D][ ] submit resume #Acme (by: Oct 15 2019)", task.toString());
        assertIterableEquals(List.of("Acme"), task.getTags());
    }

    @Test
    public void getTask_eventCommand_returnsEventTask() throws SwellException {
        Task task = Parser.getTask("event meeting /from 2019-10-15 1400 /to 2019-10-15 1600");

        assertInstanceOf(Event.class, task);
        assertEquals("[E][ ] meeting (from: Oct 15 2019 2:00 PM to: Oct 15 2019 4:00 PM)",
                task.toString());
    }

    @Test
    public void getTask_eventCommandWithIsoDates_returnsFormattedEvent() throws SwellException {
        Task task = Parser.getTask("event meeting /from 2019-10-15 /to 2019-10-16");

        assertInstanceOf(Event.class, task);
        assertEquals("[E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)", task.toString());
    }

    @Test
    public void getTagKeyword_tagWithHash_returnsTagWithoutHash() throws SwellException {
        assertEquals("followup", Parser.getTagKeyword("findtag #followup"));
    }

    @Test
    public void getTagKeyword_emptyHashTag_throwsSwellException() {
        assertThrows(SwellException.class, () -> Parser.getTagKeyword("findtag #"));
    }

    @Test
    public void getTagKeyword_keywordWithSpaces_throwsSwellException() {
        assertThrows(SwellException.class, () -> Parser.getTagKeyword("findtag follow up"));
    }

    @Test
    public void getTask_deadlineCommandWithInvalidDate_throwsSwellException() {
        assertThrows(SwellException.class, () -> Parser.getTask("deadline return book /by Sunday"));
    }

    @Test
    public void getTask_deadlineCommandWithMultipleByPrefixes_throwsSwellException() {
        assertThrows(SwellException.class, () ->
                Parser.getTask("deadline return book /by 2019-10-15 /by 2019-10-16"));
    }

    @Test
    public void getTask_eventCommandWithMultipleFromPrefixes_throwsSwellException() {
        assertThrows(SwellException.class, () ->
                Parser.getTask("event meeting /from Mon /from Tue /to Wed"));
    }

    @Test
    public void getTask_eventCommandWithSameFromAndTo_returnsEventTask() throws SwellException {
        Task task = Parser.getTask("event meeting /from 2019-10-15 1400 /to 2019-10-15 1400");

        assertInstanceOf(Event.class, task);
        assertEquals("[E][ ] meeting (from: Oct 15 2019 2:00 PM to: Oct 15 2019 2:00 PM)",
                task.toString());
    }

    @Test
    public void getTask_eventCommandWithInvalidDateTime_throwsSwellException() {
        assertThrows(SwellException.class, () ->
                Parser.getTask("event meeting /from Monday 2pm /to 2019-10-15 1600"));
    }

    @Test
    public void getTaskNumber_validMarkCommand_returnsTaskNumber() throws SwellException {
        assertEquals(12, Parser.getTaskNumber("mark 12", "mark"));
    }

    @Test
    public void getTaskNumber_missingNumber_throwsSwellException() {
        assertThrows(SwellException.class, () -> Parser.getTaskNumber("delete", "delete"));
    }

    @Test
    public void getTaskNumber_extraArgument_throwsSwellException() {
        assertThrows(SwellException.class, () -> Parser.getTaskNumber("mark 1 2", "mark"));
    }

    @Test
    public void getTaskNumber_zero_throwsSwellException() {
        assertThrows(SwellException.class, () -> Parser.getTaskNumber("delete 0", "delete"));
    }
}
