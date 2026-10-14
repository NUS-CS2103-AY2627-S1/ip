package milo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command responses using isolated storage for every test. */
public class MiloTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_validCommand_returnsCommandOutput() {
        Milo milo = new Milo(temporaryDirectory.resolve("valid-command.txt").toString());

        String response = milo.getResponse("todo read book");

        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 tasks in the list.", response);
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorOutput() {
        Milo milo = new Milo(temporaryDirectory.resolve("invalid-command.txt").toString());

        String response = milo.getResponse("unknown");

        assertEquals("OOPS!!! I don't recognize that command :-(", response);
    }

    @Test
    public void getResponse_eventEndNotAfterStart_rejectsWithoutSaving() {
        Path taskFile = temporaryDirectory.resolve("invalid-event.txt");
        Milo milo = new Milo(taskFile.toString());

        assertEquals("OOPS!!! An event's end date must be after its start date.",
                milo.getResponse("event meeting /from 2026-09-18 /to 2026-09-17"));
        assertEquals("OOPS!!! An event's end date must be after its start date.",
                milo.getResponse("event meeting /from 2026-09-18 /to 2026-09-18"));
        assertFalse(Files.exists(taskFile));
        assertEquals("Here are the tasks in your list:", milo.getResponse("list"));
    }

    @Test
    public void getResponse_invalidTaskNumber_preservesTaskAcrossRestart() {
        Path taskFile = temporaryDirectory.resolve("task-number.txt");
        Milo milo = new Milo(taskFile.toString());
        milo.getResponse("todo read book");

        assertEquals("OOPS!!! There is no task numbered 2.", milo.getResponse("delete 2"));
        assertEquals("OOPS!!! Please specify a task number, for example: mark 2",
                milo.getResponse("mark several"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book",
                new Milo(taskFile.toString()).getResponse("list"));
    }

    @Test
    public void getResponse_helpCommand_returnsAvailableCommands() {
        Milo milo = new Milo(temporaryDirectory.resolve("help-command.txt").toString());

        String response = milo.getResponse("help");

        assertEquals("Here are the commands you can use:\n"
                + "todo <description> - add a todo\n"
                + "deadline <description> /by <yyyy-MM-dd> - add a deadline\n"
                + "event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd> - add an event\n"
                + "list - show all tasks\n"
                + "find <keyword> - find matching tasks\n"
                + "mark <number> - mark a task as done\n"
                + "unmark <number> - mark a task as not done\n"
                + "delete <number> - remove a task\n"
                + "stats - show task statistics\n"
                + "help - show this help message\n"
                + "bye - exit Milo", response);
    }
}
