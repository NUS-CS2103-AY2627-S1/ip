package arthur.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import arthur.messages.AddMessage;
import arthur.state.CommandContext;
import arthur.tasks.TodoTask;

public class TodoCommandTest {
    // AI-assisted: Verify todo commands add the expected task type.
    @Test
    public void handle_description_addsTodoTask() {
        CommandContext context = new CommandContext();
        assertInstanceOf(AddMessage.class, new TodoCommand().handle(context, "read book"));
        assertEquals(1, context.tasks().size());
        assertInstanceOf(TodoTask.class, context.tasks().getFirst());
    }
}
