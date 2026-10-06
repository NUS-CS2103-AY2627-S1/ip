package moon;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests Moon's state-changing commands and task search behavior. */
public class MoonEngineCommandTest {
    private static final Path DATA_FILE = Path.of("data", "moon.txt");

    private byte[] originalData;
    private boolean dataFileExisted;

    /** Replaces the real task file with an empty isolated file for each test. */
    @BeforeEach
    public void isolateTaskFile() throws IOException {
        dataFileExisted = Files.exists(DATA_FILE);
        originalData = dataFileExisted ? Files.readAllBytes(DATA_FILE) : null;
        Files.createDirectories(DATA_FILE.getParent());
        Files.writeString(DATA_FILE, "", StandardCharsets.UTF_8);
    }

    /** Restores the task file after each state-changing test. */
    @AfterEach
    public void restoreTaskFile() throws IOException {
        if (dataFileExisted) {
            Files.write(DATA_FILE, originalData);
        } else {
            Files.deleteIfExists(DATA_FILE);
        }
    }

    /** Verifies that to-do, deadline, and event commands add displayable tasks. */
    @Test
    public void addCommands_addAllTaskTypes() {
        MoonEngine engine = new MoonEngine();

        engine.getResponse("todo buy milk");
        engine.getResponse("deadline submit report /by 2026-09-25");
        engine.getResponse("event team meeting /from 2026-09-20 /to 2026-09-21");
        String response = engine.getResponse("list");

        assertTrue(response.contains("Here's the current tasks:"));
        assertFalse(response.contains("current task vibe"));
        assertTrue(response.contains("[T][ ] buy milk"));
        assertTrue(response.contains("[D][ ] submit report (by: Sep 25 2026)"));
        assertTrue(response.contains("[E][ ] team meeting (from: Sep 20 2026 to: Sep 21 2026)"));
    }

    /** Verifies that find returns matching tasks and excludes non-matches. */
    @Test
    public void findCommand_returnsOnlyMatchingTasks() {
        MoonEngine engine = new MoonEngine();

        engine.getResponse("todo plan weekend");
        engine.getResponse("todo read book");
        engine.getResponse("todo write code");
        String response = engine.getResponse("find BOOK");

        assertTrue(response.contains(" 2.[T][ ] read book"));
        assertFalse(response.contains(" 1.[T][ ] read book"));
        assertFalse(response.contains("write code"));
    }

    /** Verifies mark, unmark, delete, and undo restore the expected task state. */
    @Test
    public void stateCommands_updateAndRestoreTasks() {
        MoonEngine engine = new MoonEngine();

        engine.getResponse("todo read book");
        assertTrue(engine.getResponse("mark 1").contains("[T][X] read book"));
        assertTrue(engine.getResponse("unmark 1").contains("[T][ ] read book"));
        assertTrue(engine.getResponse("delete 1").contains("read book"));
        assertFalse(engine.getResponse("list").contains("read book"));

        engine.getResponse("undo");
        assertTrue(engine.getResponse("list").contains("read book"));
    }

    /** Verifies that an exact duplicate task is rejected without changing the list. */
    @Test
    public void addCommand_duplicateTask_reportsError() {
        MoonEngine engine = new MoonEngine();

        engine.getResponse("todo read book");
        engine.getResponse("mark 1");
        String response = engine.getResponse("todo read book");

        assertTrue(response.contains("exact task is already on your list"));
        assertTrue(engine.getResponse("list").indexOf("read book")
                == engine.getResponse("list").lastIndexOf("read book"));
    }

    /** Verifies a failed save does not leave an unpersisted task in memory. */
    @Test
    public void addCommand_failedSave_rollsBackInMemoryState() {
        FailingStorage storage = new FailingStorage();
        MoonEngine engine = new MoonEngine(storage);
        storage.failSaves = true;

        String response = engine.getResponse("todo read book");

        assertTrue(response.contains("couldn't save your task list"));
        assertFalse(engine.getResponse("list").contains("read book"));
    }

    private static class FailingStorage extends Storage {
        private boolean failSaves;

        @Override
        public List<Task> load() {
            return new ArrayList<>();
        }

        @Override
        public void save(List<Task> tasks) throws IOException {
            if (failSaves) {
                throw new IOException("simulated save failure");
            }
        }
    }
}
