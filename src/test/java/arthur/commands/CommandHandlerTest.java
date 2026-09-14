package arthur.commands;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import arthur.InMemoryStorage;
import arthur.messages.AddMessage;
import arthur.messages.ByeMessage;
import arthur.messages.ErrorMessage;
import arthur.messages.ListMessage;
import arthur.messages.MarkMessage;
import arthur.messages.RemoveMessage;
import arthur.messages.UnmarkMessage;
import arthur.state.CommandContext;

public class CommandHandlerTest {
    // AI-assisted: Cover whitespace normalization and invalid-command error responses.
    @Test
    public void handle_taskLifecycleCommands_returnsExpectedResponses() {
        CommandContext context = new CommandContext();
        CommandHandler handler = new CommandHandler(context, new InMemoryStorage());

        assertInstanceOf(AddMessage.class, handler.handle("todo read book"));
        assertInstanceOf(MarkMessage.class, handler.handle("mark 1"));
        assertInstanceOf(UnmarkMessage.class, handler.handle("unmark 1"));
        assertInstanceOf(RemoveMessage.class, handler.handle("remove 1"));
        assertInstanceOf(ListMessage.class, handler.handle("list"));
        assertInstanceOf(ByeMessage.class, handler.handle("bye"));
        assertTrue(context.tasks().isEmpty());
    }

    @Test
    public void handle_unknownCommand_returnsErrorMessageAndKeepsSessionUsable() {
        CommandHandler handler = new CommandHandler(new CommandContext(), new InMemoryStorage());

        assertInstanceOf(ErrorMessage.class, handler.handle("unknown"));
        assertInstanceOf(AddMessage.class, handler.handle("todo read book"));
    }

    @Test
    public void handle_commandWithSurroundingWhitespace_executesCommand() {
        CommandHandler handler = new CommandHandler(new CommandContext(), new InMemoryStorage());

        assertInstanceOf(AddMessage.class, handler.handle("  todo read book  "));
    }

    @Test
    public void handle_emptyCommand_returnsErrorMessage() {
        CommandHandler handler = new CommandHandler(new CommandContext(), new InMemoryStorage());

        assertInstanceOf(ErrorMessage.class, handler.handle("   "));
    }
}
