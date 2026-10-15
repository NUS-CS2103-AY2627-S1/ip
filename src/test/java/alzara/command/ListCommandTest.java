package alzara.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import alzara.storage.Storage;
import alzara.task.TaskList;
import alzara.task.ToDo;
import alzara.ui.Ui;

/**
 * Tests for {@link ListCommand}. Read-only, so {@link Storage} is only
 * passed through (never used) - each test still supplies one pointed at a
 * fresh JUnit {@code @TempDir} to match {@link Command#execute}'s signature.
 */
class ListCommandTest {
    // An empty task list should show just the heading, no numbered lines.
    @Test
    void execute_emptyList_showsHeadingOnly(@TempDir File dataDir) {
        TaskList memory = new TaskList(new ArrayList<>());
        Ui ui = new Ui(true);

        new ListCommand().execute(memory, ui, new Storage(dataDir));

        assertEquals("Behold, all that awaits you:", ui.getAndClearResponse());
    }

    // Multiple tasks should be listed in order, numbered from 1.
    @Test
    void execute_multipleTasks_showsNumberedList(@TempDir File dataDir) {
        TaskList memory = new TaskList(new ArrayList<>());
        memory.add(new ToDo("read book"));
        memory.add(new ToDo("borrow book"));
        Ui ui = new Ui(true);

        new ListCommand().execute(memory, ui, new Storage(dataDir));

        assertEquals("Behold, all that awaits you:\n1.[T][ ] read book\n2.[T][ ] borrow book",
                ui.getAndClearResponse());
    }
}
