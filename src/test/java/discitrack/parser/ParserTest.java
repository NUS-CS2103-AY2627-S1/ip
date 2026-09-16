package discitrack.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import discitrack.command.Command;
import discitrack.command.FindCommand;
import discitrack.command.HelpCommand;
import discitrack.exception.DisciTrackException;
import discitrack.storage.Storage;
import discitrack.task.Deadline;
import discitrack.task.Task;
import discitrack.task.TaskList;
import discitrack.task.Todo;
import discitrack.ui.Ui;

public class ParserTest {
    @TempDir
    public Path tempDir;

    @Test
    public void parse_todoWithExtraSpaces_addsTrimmedTodo() throws DisciTrackException, IOException {
        Command command = Parser.parse("todo      sleep");
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("discitrack.txt").toString());

        command.execute(tasks, new Ui(), storage);

        Task task = tasks.get(1);
        assertInstanceOf(Todo.class, task);
        assertEquals("sleep", task.getActivity());
    }

    @Test
    public void parse_deadlineWithExtraSpaces_addsTrimmedDeadline() throws DisciTrackException, IOException {
        Command command = Parser.parse("deadline      homework       /by         2026-08-29");
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("discitrack.txt").toString());

        command.execute(tasks, new Ui(), storage);

        Task task = tasks.get(1);
        assertInstanceOf(Deadline.class, task);

        Deadline deadline = (Deadline) task;
        assertEquals("homework", deadline.getActivity());
        assertEquals(LocalDate.parse("2026-08-29"), deadline.getTime());
    }

    @Test
    public void parse_deadlineWithoutBy_throwsDisciTrackException() {
        assertThrows(DisciTrackException.class, () -> Parser.parse("deadline homework 2026-08-29"));
    }

    @Test
    public void parse_findWithKeyword_returnsFindCommand() throws DisciTrackException {
        Command command = Parser.parse("find book");

        assertInstanceOf(FindCommand.class, command);
    }

    @Test
    public void parse_help_returnsHelpCommand() throws DisciTrackException {
        Command command = Parser.parse("help");

        assertInstanceOf(HelpCommand.class, command);
    }

    @Test
    public void parse_findWithoutKeyword_throwsDisciTrackException() {
        assertThrows(DisciTrackException.class, () -> Parser.parse("find     "));
    }

    @Test
    public void parse_invalidCommand_throwsDisciTrackException() {
        assertThrows(DisciTrackException.class, () -> Parser.parse("blah sleep"));
    }
}
