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
 * Tests for {@link FindCommand}. Read-only, so {@link Storage} is only
 * passed through (never used) - each test still supplies one pointed at a
 * fresh JUnit {@code @TempDir} to match {@link Command#execute}'s signature.
 */
class FindCommandTest {
    // A keyword matching one task, case-insensitively, should show just that task.
    @Test
    void execute_keywordMatchesOneTaskDifferentCase_showsThatTask(@TempDir File dataDir) {
        TaskList memory = new TaskList(new ArrayList<>());
        memory.add(new ToDo("read book"));
        memory.add(new ToDo("go jogging"));
        Ui ui = new Ui(true);

        new FindCommand("BOOK").execute(memory, ui, new Storage(dataDir));

        assertEquals("The great Alzara finds these:\n1.[T][ ] read book",
                ui.getAndClearResponse());
    }

    // A keyword matching nothing should show just the heading, no numbered lines.
    @Test
    void execute_keywordMatchesNothing_showsHeadingOnly(@TempDir File dataDir) {
        TaskList memory = new TaskList(new ArrayList<>());
        memory.add(new ToDo("read book"));
        Ui ui = new Ui(true);

        new FindCommand("xyz").execute(memory, ui, new Storage(dataDir));

        assertEquals("The great Alzara finds these:", ui.getAndClearResponse());
    }

    // With several keywords, only a task matching every one of them should show.
    @Test
    void execute_multipleKeywords_onlyTaskMatchingAllShows(@TempDir File dataDir) {
        TaskList memory = new TaskList(new ArrayList<>());
        memory.add(new ToDo("read book"));
        memory.add(new ToDo("read magazine"));
        Ui ui = new Ui(true);

        new FindCommand("read", "book").execute(memory, ui, new Storage(dataDir));

        assertEquals("The great Alzara finds these:\n1.[T][ ] read book",
                ui.getAndClearResponse());
    }
}
