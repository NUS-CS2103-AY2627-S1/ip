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
import arthur.tasks.DeadlineTask;

public class DeadlineCommandTest {
    /*
        AI-assisted:
        * Cover valid and incomplete deadline arguments.
        * Cover rejection of deadlines in the past.
     */
    @Test
    public void handle_validDeadline_addsDeadlineTask() {
        CommandContext context = new CommandContext();
        assertInstanceOf(AddMessage.class,
                new DeadlineCommand().handle(context, "submit report /by 2026-09-15 1430"));
        assertInstanceOf(DeadlineTask.class, context.tasks().getFirst());
    }

    @Test
    public void handle_missingByArgument_throwsMissingArgumentException() {
        assertThrows(MissingArgumentException.class, () ->
                new DeadlineCommand().handle(new CommandContext(), "submit report"));
    }

    @Test
    public void handle_invalidByDate_throwsInvalidDateTimeException() {
        assertThrows(InvalidDateTimeException.class, () ->
                new DeadlineCommand().handle(new CommandContext(), "submit report /by tomorrow"));
    }

    @Test
    public void handle_pastDeadline_throwsInvalidArgumentException() {
        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class, () ->
                new DeadlineCommand().handle(new CommandContext(), "submit report /by 2000-01-01 0000"));
        assertEquals("An invalid argument was passed to the command: 2000-01-01 0000 (Deadline cannot be in the past)",
                exception.getMessage());
    }
}
