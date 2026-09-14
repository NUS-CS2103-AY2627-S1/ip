package arthur.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import arthur.exceptions.InvalidArgumentException;
import arthur.exceptions.InvalidDateTimeException;
import arthur.exceptions.MissingArgumentException;
import arthur.messages.AddMessage;
import arthur.state.CommandContext;
import arthur.tasks.EventTask;

public class EventCommandTest {
    /*
        AI-assisted:
        * Cover valid and incomplete event arguments.
        * Cover rejection of past events and events with an invalid time range.
     */
    @Test
    public void handle_validEvent_addsEventTask() {
        CommandContext context = new CommandContext();
        assertInstanceOf(AddMessage.class, new EventCommand().handle(context,
                "team meeting /from 2026-09-15 0900 /to 2026-09-15 1000"));
        assertInstanceOf(EventTask.class, context.tasks().getFirst());
    }

    @Test
    public void handle_missingToArgument_throwsMissingArgumentException() {
        assertThrows(MissingArgumentException.class, () ->
                new EventCommand().handle(new CommandContext(), "meeting /from 2026-09-15 0900"));
    }

    @Test
    public void handle_invalidFromDate_throwsInvalidDateTimeException() {
        assertThrows(InvalidDateTimeException.class, () -> new EventCommand().handle(new CommandContext(),
                "meeting /from tomorrow /to 2026-09-15 1000"));
    }

    @Test
    public void handle_eventEndingInPast_throwsInvalidArgumentException() {
        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () ->
                new EventCommand().handle(new CommandContext(),
                        "meeting /from 2000-01-01 0900 /to 2000-01-01 1000"));
        assertEquals("An invalid argument was passed to the command: 2000-01-01 1000 (Event cannot be in the past)",
                exception.getMessage());
    }

    @Test
    public void handle_eventStartingAfterItEnds_throwsInvalidArgumentException() {
        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () ->
                new EventCommand().handle(new CommandContext(),
                        "meeting /from 2099-01-01 1000 /to 2099-01-01 0900"));
        assertEquals("An invalid argument was passed to the command: 2099-01-01 1000 "
                + "(Event cannot start after it ends)", exception.getMessage());
    }
}
