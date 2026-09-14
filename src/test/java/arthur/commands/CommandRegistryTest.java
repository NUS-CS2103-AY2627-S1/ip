package arthur.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import arthur.state.CommandContext;

// AI-assisted: Tests command-registry lookup, alias resolution, and unknown-command behavior.
public class CommandRegistryTest {
    @Test
    public void getCommand_exactAndUniquePrefix_returnRegisteredCommand() {
        CommandContext context = new CommandContext();

        assertInstanceOf(TodoCommand.class, CommandRegistry.getCommand(context, "todo"));
        assertInstanceOf(EventCommand.class, CommandRegistry.getCommand(context, "eve"));
    }

    @Test
    public void getCommand_alias_resolvesAliasedCommand() {
        CommandContext context = new CommandContext();
        context.commandAliases().put("t", "todo");

        assertInstanceOf(TodoCommand.class, CommandRegistry.getCommand(context, "t"));
    }

    @Test
    public void getCommand_ambiguousOrUnknownCommand_returnsNull() {
        CommandContext context = new CommandContext();

        assertNull(CommandRegistry.getCommand(context, ""));
        assertNull(CommandRegistry.getCommand(context, "unknown"));
    }

    @Test
    public void getCommandName_registeredAndUnknownClasses_returnExpectedResult() {
        assertEquals("todo", CommandRegistry.getCommandName(TodoCommand.class));
        assertNull(CommandRegistry.getCommandName(Command.class));
    }
}
