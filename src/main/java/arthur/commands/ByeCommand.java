package arthur.commands;

import arthur.messages.ByeMessage;
import arthur.messages.Message;
import arthur.state.CommandContext;

/**
 * Produces the final message that ends the application session.
 */
public class ByeCommand extends Command {
    @Override
    public Message handle(CommandContext context, String arg) {
        return new ByeMessage();
    }
}
