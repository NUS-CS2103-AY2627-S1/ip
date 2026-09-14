package arthur.commands;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import arthur.exceptions.InvalidDateTimeException;
import arthur.exceptions.MissingArgumentException;
import arthur.messages.ListMessage;
import arthur.state.CommandContext;

public class FilterCommandTest {
    // AI-assisted: Cover valid and malformed filter dates.
    @Test
    public void handle_validDate_returnsListMessage() {
        assertInstanceOf(ListMessage.class,
                new FilterCommand().handle(new CommandContext(), "/on 2026-09-15"));
    }

    @Test
    public void handle_invalidDate_throwsInvalidDateTimeException() {
        assertThrows(InvalidDateTimeException.class, () ->
                new FilterCommand().handle(new CommandContext(), "/on 2026-02-29"));
    }

    @Test
    public void handle_missingOnArgument_throwsMissingArgumentException() {
        assertThrows(MissingArgumentException.class, () ->
                new FilterCommand().handle(new CommandContext(), ""));
    }
}
