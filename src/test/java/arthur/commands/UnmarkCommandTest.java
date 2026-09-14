package arthur.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import arthur.exceptions.InvalidArgumentException;
import arthur.exceptions.MissingArgumentException;
import arthur.messages.UnmarkMessage;
import arthur.state.CommandContext;
import arthur.tasks.TodoTask;

public class UnmarkCommandTest {
    // AI-assisted: Verify unmark clears the selected task state.
    @Test
    public void handle_markedTask_unmarksTask() {
        CommandContext context = new CommandContext();
        TodoTask task = new TodoTask("read book");
        task.mark();
        context.tasks().add(task);
        assertInstanceOf(UnmarkMessage.class, new UnmarkCommand().handle(context, "1"));
        assertFalse(context.tasks().getFirst().toString().contains("[X]"));
    }

    @Test
    public void handle_missingIndex_throwsMissingArgumentException() {
        assertThrows(MissingArgumentException.class, () ->
                new UnmarkCommand().handle(new CommandContext(), ""));
    }

    @Test
    public void handle_zeroIndex_throwsInvalidArgumentException() {
        CommandContext context = new CommandContext();
        context.tasks().add(new TodoTask("read book"));
        assertThrows(InvalidArgumentException.class, () -> new UnmarkCommand().handle(context, "0"));
    }
}
