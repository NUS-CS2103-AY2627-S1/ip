package lebron.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import lebron.task.DateTime;
import lebron.task.Deadline;
import lebron.task.Task;

/**
 * Tests for {@link ParsedCommand}'s static factories: that each one sets
 * {@link ParsedCommand#getType()} correctly and carries only the field
 * meaningful for that command, leaving the others at their default (null or
 * zero). {@link ParserTest} already exercises these indirectly through
 * {@link Parser#parse(String)}; these tests check the factories directly.
 */
public class ParsedCommandTest {
    @Test
    public void list_hasListTypeAndNoOtherData() {
        ParsedCommand command = ParsedCommand.list();
        assertEquals(ParsedCommand.Type.LIST, command.getType());
        assertNull(command.getDescription());
        assertNull(command.getTask());
        assertEquals(0, command.getIndex());
    }

    @Test
    public void bye_hasByeType() {
        assertEquals(ParsedCommand.Type.BYE, ParsedCommand.bye().getType());
    }

    @Test
    public void todo_carriesDescription() {
        ParsedCommand command = ParsedCommand.todo("buy milk");
        assertEquals(ParsedCommand.Type.TODO, command.getType());
        assertEquals("buy milk", command.getDescription());
    }

    @Test
    public void find_carriesKeywordAsDescription() {
        ParsedCommand command = ParsedCommand.find("book");
        assertEquals(ParsedCommand.Type.FIND, command.getType());
        assertEquals("book", command.getDescription());
    }

    @Test
    public void ofTask_carriesGivenTypeAndTask() {
        Task task = new Deadline("pay rent", DateTime.parse("2025-01-01"));
        ParsedCommand command = ParsedCommand.ofTask(ParsedCommand.Type.DEADLINE, task);
        assertEquals(ParsedCommand.Type.DEADLINE, command.getType());
        assertSame(task, command.getTask());
    }

    @Test
    public void ofIndex_carriesGivenTypeAndOneBasedIndex() {
        ParsedCommand command = ParsedCommand.ofIndex(ParsedCommand.Type.DELETE, 3);
        assertEquals(ParsedCommand.Type.DELETE, command.getType());
        assertEquals(3, command.getIndex());
    }
}
