package arthur.commands;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import arthur.messages.ByeMessage;
import arthur.messages.Message;
import arthur.state.CommandContext;

public class ByeCommandTest {
    @Test
    public void handle_noArguments_returnsFinalByeMessage() {
        Message message = new ByeCommand().handle(new CommandContext(), "");
        assertInstanceOf(ByeMessage.class, message);
        assertTrue(message.isFinal());
    }

    @Test
    public void handle_arguments_returnsFinalByeMessage() {
        Message message = new ByeCommand().handle(new CommandContext(), "some arguments");
        assertInstanceOf(ByeMessage.class, message);
        assertTrue(message.isFinal());
    }

}
