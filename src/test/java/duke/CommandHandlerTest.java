package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/** Tests command parsing, validation, and recovery behavior. */
class CommandHandlerTest {
    @Test
    void handleCommand_invalidDate_returnsHelpfulError() {
        CommandHandler handler = new CommandHandler(new ArrayList<>());

        assertEquals("OOPS!!! Please provide a valid deadline date.",
                handler.handleCommand("deadline submit report /by 2019-02-30"));
    }

    @Test
    void handleCommand_duplicateTask_rejectsSecondCopy() {
        CommandHandler handler = new CommandHandler(new ArrayList<>());

        assertEquals("Got it. I've added this task:\n[T][ ] read book\nNow you have 1 tasks in the list.",
                handler.handleCommand("todo read book"));
        assertEquals("OOPS!!! That task is already in your list.",
                handler.handleCommand("todo read book"));
    }

    @Test
    void handleCommand_extraWhitespace_stillRecognizesCommand() {
        CommandHandler handler = new CommandHandler(new ArrayList<>());

        assertEquals("Got it. I've added this task:\n[T][ ] read book\nNow you have 1 tasks in the list.",
                handler.handleCommand("  todo   read book  "));
    }

    @Test
    void handleCommand_invalidEventTime_returnsHelpfulError() {
        CommandHandler handler = new CommandHandler(new ArrayList<>());

        assertEquals("OOPS!!! An event's end time must be after its start time.",
                handler.handleCommand("event meeting /from 2026-09-20 1800 /to 2026-09-20 1700"));
    }

    @Test
    void handleCommand_overflowingTaskNumber_returnsHelpfulError() {
        CommandHandler handler = new CommandHandler(new ArrayList<>());

        assertEquals("OOPS!!! Task numbers must be positive whole numbers.",
                handler.handleCommand("delete 999999999999999999999999"));
    }
}
