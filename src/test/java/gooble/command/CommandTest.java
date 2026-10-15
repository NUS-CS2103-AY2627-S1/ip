package gooble.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import gooble.storage.Storage;
import gooble.task.TaskList;
import gooble.ui.Ui;

/** Tests command execution against real task and storage services. */
class CommandTest {
    @Test
    void taskCommands_updateTaskListAndReportResults(@TempDir Path tempDir) throws Exception {
        StringBuilder output = new StringBuilder();
        CommandContext context = context(tempDir, output);

        new AddCommand("add notebook").execute(context);
        new TodoCommand("todo read book").execute(context);
        new DeadlineCommand("deadline submit report /by 2026-02-01 0900").execute(context);
        new EventCommand("event meeting /from 2026-02-01 1000 /to 2026-02-01 1100").execute(context);

        assertTrue(output.toString().contains("added: notebook"));
        assertTrue(output.toString().contains("[T][ ] read book"));
        assertTrue(output.toString().contains("[D][ ] submit report"));
        assertTrue(output.toString().contains("[E][ ] meeting"));
    }

    @Test
    void updateAndSearchCommands_applyChanges(@TempDir Path tempDir) throws Exception {
        StringBuilder output = new StringBuilder();
        CommandContext context = context(tempDir, output);

        new TodoCommand("todo read book").execute(context);
        new MarkCommand("mark 1").execute(context);
        new UnmarkCommand("unmark 1").execute(context);
        new TagCommand("tag 1 #School").execute(context);
        new FindCommand("find #school").execute(context);
        new UntagCommand("untag 1").execute(context);

        assertTrue(output.toString().contains("Nice! I've marked this task as done:"));
        assertTrue(output.toString().contains("OK, I've marked this task as not done yet:"));
        assertTrue(output.toString().contains("[#school]"));
        assertTrue(output.toString().contains("I've removed the tags from this task:"));
    }

    @Test
    void listDeleteAndListFromCommands_reportExpectedTasks(@TempDir Path tempDir) throws Exception {
        StringBuilder output = new StringBuilder();
        CommandContext context = context(tempDir, output);

        new TodoCommand("todo keep me").execute(context);
        new EventCommand("event in range /from 2026-03-10 0900 /to 2026-03-10 1000").execute(context);
        new ListCommand("list").execute(context);
        new ListFromCommand("list from 2026-03-10 0000 to 2026-03-10 2359").execute(context);
        new DeleteCommand("delete 1").execute(context);

        assertTrue(output.toString().contains("Here are the tasks in your list:"));
        assertTrue(output.toString().contains("Here are the events in your list for that period:"));
        assertTrue(output.toString().contains("Noted. I've removed this task:"));
        assertFalse(output.toString().contains("That task number does not exist"));
    }

    @Test
    void informationalAndInvalidCommands_reportMessages(@TempDir Path tempDir) throws Exception {
        StringBuilder output = new StringBuilder();
        CommandContext context = context(tempDir, output);

        new HelpCommand("help todo").execute(context);
        new ByeCommand("bye").execute(context);
        assertTrue(new ByeCommand("bye").isExit());
        assertFalse(new HelpCommand("help").isExit());

        assertThrows(gooble.GoobleException.class, () -> new UnknownCommand("wat").execute(context));
    }

    private CommandContext context(Path tempDir, StringBuilder output) {
        TaskList tasks = new TaskList(new Storage(tempDir.resolve("tasks.txt")));
        return new CommandContext(tasks, new Ui(output::append), new Parser());
    }
}
