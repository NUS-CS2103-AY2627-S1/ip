package arthur.commands;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import arthur.messages.ListMessage;
import arthur.state.CommandContext;

public class ListCommandTest {
    // AI-assisted: Verify list produces a list response for an empty context.
    @Test
    public void handle_emptyContext_returnsListMessage() {
        assertInstanceOf(ListMessage.class, new ListCommand().handle(new CommandContext(), ""));
    }
}
