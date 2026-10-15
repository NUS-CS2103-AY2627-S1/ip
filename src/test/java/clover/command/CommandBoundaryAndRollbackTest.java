package clover.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import clover.exception.CloverException;
import clover.storage.Storage;
import clover.task.Deadline;
import clover.task.TaskList;
import clover.task.ToDo;
import clover.tutoree.Tutoree;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

/** Tests command validation boundaries and restoration after a persistence failure. */
class CommandBoundaryAndRollbackTest {
    @TempDir
    Path tempDir;

    @Test
    void taskCommands_unknownParametersAndInvalidTutoreeLink_exceptionWithoutAddingTask() {
        assertThrows(CloverException.class, () -> new ToDoCommand("read /due Friday")
                .execute(new TaskList(), new TutoreeList(), new Ui(), storage()));
        assertThrows(CloverException.class, () -> new DeadlineCommand("read /by 2026-09-01 /due Friday")
                .execute(new TaskList(), new TutoreeList(), new Ui(), storage()));
        assertThrows(CloverException.class, () -> new EventCommand("read /from 2026-09-01 /to 2026-09-02 /due Friday")
                .execute(new TaskList(), new TutoreeList(), new Ui(), storage()));
        assertThrows(CloverException.class, () -> new DeadlineCommand("read /by 2026-09-01 /for Nobody")
                .execute(new TaskList(), new TutoreeList(), new Ui(), storage()));
    }

    @Test
    void taskCommands_equivalentExistingTask_exceptionThrown() throws CloverException {
        TutoreeList tutorees = new TutoreeList();
        tutorees.add(new Tutoree("Alice", "Home", "50"));
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("Read book", LocalDate.of(2026, 9, 1), "Alice"));

        CloverException exception = assertThrows(CloverException.class, () -> new DeadlineCommand(
                "  read   BOOK /by 2026-09-01 /for alice ").execute(tasks, tutorees, new Ui(), storage()));

        assertEquals("That study quest is already in the grove.", exception.getMessage());
        assertEquals(1, tasks.size());
    }

    @Test
    void addTutoree_boundaryFeeAndRepeatedMarkers_validatesInput() throws CloverException {
        for (String rate : new String[] {"hour", "session", "lesson", "month"}) {
            TutoreeList tutorees = new TutoreeList();
            new AddTutoreeCommand("Alice /address Home /fee 50/" + rate)
                    .execute(new TaskList(), tutorees, new Ui(), storage());
            assertEquals("50/" + rate, tutorees.asList().getFirst().getFee());
        }
        assertThrows(CloverException.class, () -> new AddTutoreeCommand("Alice /address Home /fee 0")
                .execute(new TaskList(), new TutoreeList(), new Ui(), storage()));
        assertThrows(CloverException.class, () -> new AddTutoreeCommand("Alice /address Home /fee 1.234")
                .execute(new TaskList(), new TutoreeList(), new Ui(), storage()));
        assertThrows(CloverException.class, () -> new AddTutoreeCommand("Alice /address Home /address Else /fee 50")
                .execute(new TaskList(), new TutoreeList(), new Ui(), storage()));
    }

    @Test
    void addTutoree_emptyAddressAndFeeVariants_validatesInput() {
        assertThrows(CloverException.class, () -> new AddTutoreeCommand("Alice /address /fee 50")
                .execute(new TaskList(), new TutoreeList(), new Ui(), storage()));
        assertThrows(CloverException.class, () -> new AddTutoreeCommand("Alice /address Home /fee 0.00")
                .execute(new TaskList(), new TutoreeList(), new Ui(), storage()));
        assertThrows(CloverException.class, () -> new AddTutoreeCommand("Alice /address Home /fee 50/HOUR")
                .execute(new TaskList(), new TutoreeList(), new Ui(), storage()));
    }

    @Test
    void taskCommands_repeatedOrMisplacedMarkers_exceptionWithoutAddingTask() {
        assertThrows(CloverException.class, () -> new DeadlineCommand("read /by 2026-09-01 /by 2026-09-02")
                .execute(new TaskList(), new TutoreeList(), new Ui(), storage()));
        assertThrows(CloverException.class, () -> new EventCommand(
                "read /from 2026-09-01 /from 2026-09-02 /to 2026-09-03")
                .execute(new TaskList(), new TutoreeList(), new Ui(), storage()));
        assertThrows(CloverException.class, () -> new EventCommand("read /to 2026-09-02 /from 2026-09-01")
                .execute(new TaskList(), new TutoreeList(), new Ui(), storage()));
        assertThrows(CloverException.class, () -> new ToDoCommand("read /for Alice /for Bob")
                .execute(new TaskList(), new TutoreeList(), new Ui(), storage()));
    }

    @Test
    void mutations_saveFails_stateIsRestored() throws IOException, CloverException {
        Path directory = Files.createDirectory(tempDir.resolve("unwritable-target"));
        Storage failingStorage = new Storage(directory, directory);
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("first"));
        TutoreeList tutorees = new TutoreeList();

        assertThrows(CloverException.class, () -> new ToDoCommand("second")
                .execute(tasks, tutorees, new Ui(), failingStorage));
        assertEquals(1, tasks.size());
        assertThrows(CloverException.class, () -> new AddTutoreeCommand("Alice /address Home /fee 50")
                .execute(tasks, tutorees, new Ui(), failingStorage));
        assertEquals(0, tutorees.size());
        assertThrows(CloverException.class, () -> new MarkCommand("1")
                .execute(tasks, tutorees, new Ui(), failingStorage));
        assertFalse(tasks.get(0).isDone());

        tasks.get(0).markAsDone();
        assertThrows(CloverException.class, () -> new UnmarkCommand("1")
                .execute(tasks, tutorees, new Ui(), failingStorage));
        assertTrue(tasks.get(0).isDone());
        assertThrows(CloverException.class, () -> new DeleteCommand("1")
                .execute(tasks, tutorees, new Ui(), failingStorage));
        assertEquals("first", tasks.get(0).getDescription());
    }

    private Storage storage() {
        return new Storage(tempDir.resolve("tasks.txt"), tempDir.resolve("tutorees.txt"));
    }
}
