package niulai.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import niulai.NiuLaiException;
import niulai.gui.GuiUi;
import niulai.model.Deadline;
import niulai.model.Task;
import niulai.model.TaskList;
import niulai.model.TaskStatus;
import niulai.model.Todo;
import niulai.service.Storage;

/** Tests command matching, successful execution, and reversible state changes. */
class CommandTest {
    /** Temporary directory isolated from the application's real data file. */
    @TempDir
    Path temporaryDirectory;

    /** Verifies exact and prefix matching at command-word boundaries. */
    @Test
    void commandMatching_keywordBoundaries_returnsExpectedResult() {
        Command command = new MarkCommand(0);

        assertEquals("mark", command.getKeyword());
        assertTrue(command.matchesExactly("mark"));
        assertFalse(command.matchesExactly("mark 1"));
        assertTrue(command.matches("mark"));
        assertTrue(command.matches("mark 1"));
        assertTrue(command.matches("mark\t1"));
        assertTrue(command.matches("mark\u00A01"));
        assertFalse(command.matches("mar"));
        assertFalse(command.matches("marked"));
        assertFalse(command.matches("MARK 1"));
        assertFalse(command.isExit());
        assertFalse(command.isUndo());
    }

    /** Verifies that a successful add persists the task and returns a working inverse. */
    @Test
    void execute_addThenUndo_persistsBothStates() throws NiuLaiException, IOException {
        Path file = temporaryDirectory.resolve("add.txt");
        Storage storage = new Storage(file.toString());
        Task task = new Todo("read book");
        TaskList tasks = new TaskList();
        GuiUi ui = new GuiUi();

        Command.UndoAction undo = new AddCommand(task).execute(tasks, ui, storage);

        assertSame(task, tasks.get(0));
        assertEquals("T | 0 | read book", Files.readString(file).strip());
        assertTrue(ui.consumeOutput().contains("I've added this task"));

        undo.undo(tasks, storage);

        assertEquals(0, tasks.size());
        assertEquals("", Files.readString(file));
    }

    /** Verifies that duplicate additions are rejected without writing another task. */
    @Test
    void execute_duplicateAdd_exceptionThrownWithoutMutation() {
        Task original = new Todo("read book");
        TaskList tasks = new TaskList(original);

        NiuLaiException exception = assertThrows(NiuLaiException.class,
                () -> new AddCommand(new Todo("READ BOOK")).execute(
                        tasks, new GuiUi(), new Storage(temporaryDirectory.resolve("duplicate.txt").toString())));

        assertEquals("NOOO!!! That task already exists.", exception.getMessage());
        assertEquals(1, tasks.size());
        assertSame(original, tasks.get(0));
    }

    /** Verifies that a successful delete and its inverse preserve the original position. */
    @Test
    void execute_deleteThenUndo_restoresOriginalOrder() throws NiuLaiException, IOException {
        Path file = temporaryDirectory.resolve("delete.txt");
        Storage storage = new Storage(file.toString());
        Task first = new Todo("first");
        Task deleted = new Todo("deleted");
        Task last = new Todo("last");
        TaskList tasks = new TaskList(first, deleted, last);

        Command.UndoAction undo = new DeleteCommand(1).execute(tasks, new GuiUi(), storage);

        assertEquals(2, tasks.size());
        assertSame(last, tasks.get(1));
        assertFalse(Files.readString(file).contains("deleted"));

        undo.undo(tasks, storage);

        assertEquals(3, tasks.size());
        assertSame(deleted, tasks.get(1));
        assertTrue(Files.readString(file).contains("deleted"));
    }

    /** Verifies successful mark and unmark commands update state and show confirmations. */
    @Test
    void execute_markAndUnmark_updatesStatusAndOutput() throws NiuLaiException {
        Storage storage = new Storage(temporaryDirectory.resolve("status.txt").toString());
        Task task = new Todo("read book");
        TaskList tasks = new TaskList(task);
        GuiUi ui = new GuiUi();

        Command.UndoAction markUndo = new MarkCommand(0).execute(tasks, ui, storage);

        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertTrue(ui.consumeOutput().contains("marked this task as done"));

        markUndo.undo(tasks, storage);
        new MarkCommand(0).execute(tasks, ui, storage);
        new UnmarkCommand(0).execute(tasks, ui, storage);

        assertEquals(TaskStatus.PENDING, task.getStatus());
        assertTrue(ui.consumeOutput().contains("marked this task as not done yet"));
    }

    /** Verifies date and keyword find commands select the intended search mode. */
    @Test
    void execute_findVariants_showMatchingTasks() {
        TaskList tasks = new TaskList(
                new Todo("read book"),
                new Deadline("return book", "2026-09-10"));
        GuiUi ui = new GuiUi();

        assertNull(new FindCommand(LocalDate.of(2026, 9, 10))
                .execute(tasks, ui, new Storage("unused.txt")));
        String dateOutput = ui.consumeOutput();
        assertTrue(dateOutput.contains("return book"));
        assertFalse(dateOutput.contains("read book"));

        assertNull(new FindCommand("READ")
                .execute(tasks, ui, new Storage("unused.txt")));
        String keywordOutput = ui.consumeOutput();
        assertTrue(keywordOutput.contains("read book"));
        assertFalse(keywordOutput.contains("return book"));
    }

    /** Verifies missing find criteria are rejected at construction time. */
    @Test
    void findCommand_nullCriterion_exceptionThrown() {
        assertThrows(NullPointerException.class, () -> new FindCommand((LocalDate) null));
        assertThrows(NullPointerException.class, () -> new FindCommand((String) null));
    }

    /** Verifies exit and undo commands expose their special orchestration flags. */
    @Test
    void execute_exitAndUndo_exposesExpectedFlagsAndOutput() {
        TaskList tasks = new TaskList();
        GuiUi ui = new GuiUi();
        Storage storage = new Storage("unused.txt");
        ExitCommand exit = new ExitCommand();
        UndoCommand undo = new UndoCommand();

        assertNull(exit.execute(tasks, ui, storage));
        assertTrue(exit.isExit());
        assertTrue(ui.consumeOutput().contains("Bye. Hope not to see you again."));
        assertNull(undo.execute(tasks, ui, storage));
        assertTrue(undo.isUndo());
        assertFalse(undo.isExit());
    }
}
