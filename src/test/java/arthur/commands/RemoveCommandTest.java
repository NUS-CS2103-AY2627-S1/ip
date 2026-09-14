package arthur.commands;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import arthur.exceptions.InvalidArgumentException;
import arthur.exceptions.MissingArgumentException;
import arthur.messages.RemoveMessage;
import arthur.state.CommandContext;
import arthur.tasks.TodoTask;

public class RemoveCommandTest {
    // AI-assisted: Verify remove deletes the selected task.
    @Test
    public void handle_firstTask_removesTask() {
        CommandContext context = new CommandContext();
        context.tasks().add(new TodoTask("read book"));
        assertInstanceOf(RemoveMessage.class, new RemoveCommand().handle(context, "1"));
        assertTrue(context.tasks().isEmpty());
    }

    @Test
    public void handle_missingIndex_throwsMissingArgumentException() {
        assertThrows(MissingArgumentException.class, () ->
                new RemoveCommand().handle(new CommandContext(), ""));
    }

    @Test
    public void handle_negativeIndex_throwsInvalidArgumentException() {
        CommandContext context = new CommandContext();
        context.tasks().add(new TodoTask("read book"));
        assertThrows(InvalidArgumentException.class, () -> new RemoveCommand().handle(context, "-1"));
    }
}
