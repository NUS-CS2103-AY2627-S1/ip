package arthur.commands;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import arthur.exceptions.InvalidArgumentException;
import arthur.exceptions.MissingArgumentException;
import arthur.messages.MarkMessage;
import arthur.state.CommandContext;
import arthur.tasks.TodoTask;

public class MarkCommandTest {
    // AI-assisted: Verify mark updates the selected task.
    @Test
    public void handle_firstTask_marksTask() {
        CommandContext context = new CommandContext();
        context.tasks().add(new TodoTask("read book"));
        assertInstanceOf(MarkMessage.class, new MarkCommand().handle(context, "1"));
        assertTrue(context.tasks().getFirst().toString().contains("[X]"));
    }

    @Test
    public void handle_missingIndex_throwsMissingArgumentException() {
        assertThrows(MissingArgumentException.class, () ->
                new MarkCommand().handle(new CommandContext(), ""));
    }

    @Test
    public void handle_nonNumericIndex_throwsInvalidArgumentException() {
        assertThrows(InvalidArgumentException.class, () ->
                new MarkCommand().handle(new CommandContext(), "first"));
    }

    @Test
    public void handle_outOfRangeIndex_throwsInvalidArgumentException() {
        CommandContext context = new CommandContext();
        context.tasks().add(new TodoTask("read book"));
        assertThrows(InvalidArgumentException.class, () -> new MarkCommand().handle(context, "2"));
    }
}
