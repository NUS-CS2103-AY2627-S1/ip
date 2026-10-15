package samantha.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import samantha.model.TaskList;
import samantha.storage.Storage;

class HelpCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_noTasks_returnsCommandGuide() throws Exception {
        String output = new HelpCommand().execute(
                new TaskList(), new Storage(temporaryDirectory.resolve("tasks.txt")));

        assertEquals("Here's how to use Samantha:\n"
                + "  help\n"
                + "    Show this guide.\n"
                + "  todo DESCRIPTION\n"
                + "    Add a task.\n"
                + "  deadline DESCRIPTION /by DATE [TIME]\n"
                + "    Add a task due on DATE, optionally at TIME.\n"
                + "  event DESCRIPTION /from DATE TIME /to DATE TIME\n"
                + "    Add an event.\n"
                + "  list [DATE]\n"
                + "    List every task, or only tasks on DATE.\n"
                + "  find KEYWORD\n"
                + "    Find tasks and notes whose text contains KEYWORD.\n"
                + "  mark N | unmark N | delete N\n"
                + "    Update or remove task number N.\n"
                + "  note TEXT\n"
                + "    Save a note.\n"
                + "  notes\n"
                + "    List your notes.\n"
                + "  edit-note N TEXT\n"
                + "    Edit note number N.\n"
                + "  delete-note N\n"
                + "    Remove note number N.\n"
                + "  undo\n"
                + "    Undo the most recent state-changing command.\n"
                + "  bye\n"
                + "    Close Samantha.\n"
                + "\n"
                + "Use dates as d/M/yyyy or d-M-yyyy. Use times as HHmm.", output);
    }
}
