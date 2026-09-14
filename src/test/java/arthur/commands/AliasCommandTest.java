package arthur.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import arthur.InMemoryStorage;
import arthur.exceptions.InvalidArgumentException;
import arthur.exceptions.MissingArgumentException;
import arthur.messages.AliasMessage;
import arthur.state.CommandContext;

public class AliasCommandTest {
    // AI-assisted: Verified alias addition stores the requested alias mapping.
    @Test
    public void handle_addAlias_storesAliasMapping() {
        CommandContext context = new CommandContext();

        assertInstanceOf(AliasMessage.class,
                new AliasCommand().handle(context, "add /from abc /to todo"));

        assertEquals("todo", context.commandAliases().get("abc"));
    }

    // AI-assisted: Verified alias removal deletes the requested alias mapping.
    @Test
    public void handle_removeAlias_removesAliasMapping() {
        CommandContext context = new CommandContext();
        context.commandAliases().put("abc", "todo");

        assertInstanceOf(AliasMessage.class,
                new AliasCommand().handle(context, "remove /name abc"));

        assertFalse(context.commandAliases().containsKey("abc"));
    }

    // AI-assisted: Verified alias listing returns aliases in alphabetical order.
    @Test
    public void handle_listAliases_returnsAliasesInAlphabeticalOrder() {
        CommandContext context = new CommandContext();
        context.commandAliases().put("work", "deadline");
        context.commandAliases().put("abc", "todo");

        String output = new AliasCommand().handle(context, "list").toString();

        assertTrue(output.indexOf("abc -> todo") < output.indexOf("work -> deadline"));
    }

    // AI-assisted: Verified an empty alias list explains that no aliases are stored.
    @Test
    public void handle_listAliasesWithNoAliases_returnsEmptyListMessage() {
        String output = new AliasCommand().handle(new CommandContext(), "list").toString();

        assertTrue(output.contains("No aliases to display."));
    }

    // AI-assisted: Verified incomplete alias additions report their missing target command.
    @Test
    public void handle_addAliasWithoutTarget_throwsMissingArgumentException() {
        assertThrows(MissingArgumentException.class, () ->
                new AliasCommand().handle(new CommandContext(), "add /from abc"));
    }

    // AI-assisted: Verified unrecognized alias operations are rejected.
    @Test
    public void handle_unknownOperation_throwsInvalidArgumentException() {
        assertThrows(InvalidArgumentException.class, () ->
                new AliasCommand().handle(new CommandContext(), "rename /name abc"));
    }

    // AI-assisted: Verified the command registry dispatches alias commands through the command handler.
    @Test
    public void commandHandler_aliasList_returnsAliasMessage() {
        CommandHandler handler = new CommandHandler(new CommandContext(), new InMemoryStorage());

        assertInstanceOf(AliasMessage.class, handler.handle("alias list"));
    }
}
