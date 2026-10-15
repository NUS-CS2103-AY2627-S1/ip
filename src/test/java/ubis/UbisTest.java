package ubis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests complete command sessions and startup recovery with isolated storage.
 */
public class UbisTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_taskLifecycle_persistsAcrossSessions() {
        Path savePath = temporaryDirectory.resolve("tasks.txt");
        Ubis ubis = new Ubis(savePath);
        assertNull(ubis.getStartupWarning());
        assertEquals("added: [T][ ] read book", ubis.getResponse("todo read book"));
        assertEquals("added: [D][ ] report (by: Sep 30 2026)",
                ubis.getResponse("deadline report /by 2026-09-30"));
        assertEquals("Nice! I've marked this task as DONE:\n  [T][X] read book", ubis.getResponse("mark 1"));
        assertEquals("1: [T][X] read book", ubis.getResponse("find book"));
        assertEquals("Okay, I've deleted [D][ ] report (by: Sep 30 2026)", ubis.getResponse("delete 2"));
        assertEquals("{T}{1}{read book}\n", new Ubis(savePath).getTaskList().toString());
        assertEquals("Okay, I've marked this task NOT done yet:\n  [T][ ] read book",
                ubis.getResponse("unmark 1"));
        assertEquals("{T}{0}{read book}\n", new Ubis(savePath).getTaskList().toString());
    }

    @Test
    public void getResponse_saveFailure_keepsChangeAndWarns() throws IOException {
        Path savePath = Files.createDirectory(temporaryDirectory.resolve("directory"));
        Ubis ubis = new Ubis(new TaskList(), savePath);
        assertEquals("added: [T][ ] read book\nWarning: Your change is available for this session, "
                + "but it could not be saved to disk.", ubis.getResponse("todo read book"));
        assertEquals("{T}{0}{read book}\n", ubis.getTaskList().toString());
        assertTrue(Files.isDirectory(savePath));
    }

    @Test
    public void constructor_corruptStorage_retainsValidTasksAndWarning() throws IOException {
        Path savePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(savePath, "broken\n{T}{1}{read book}\n");
        Ubis ubis = new Ubis(savePath);
        assertEquals("Ubis skipped 1 invalid saved task record. Valid tasks were loaded normally.",
                ubis.getStartupWarning());
        assertEquals("1: [T][X] read book", ubis.getResponse("list"));
    }

    @Test
    public void constructor_inaccessibleStorage_startsEmptyWithWarning() throws IOException {
        Path parentFile = temporaryDirectory.resolve("file");
        Files.writeString(parentFile, "keep me");
        Ubis ubis = new Ubis(parentFile.resolve("tasks.txt"));
        assertEquals("Ubis could not access the task data file. It started with an empty task list.",
                ubis.getStartupWarning());
        assertEquals("No tasks to show", ubis.getResponse("list"));
    }

    @Test
    public void getResponse_separatePaths_keepsSessionsIndependent() {
        Path firstPath = temporaryDirectory.resolve("first.txt");
        Path secondPath = temporaryDirectory.resolve("second.txt");
        Ubis first = new Ubis(new TaskList(), firstPath);
        Ubis second = new Ubis(new TaskList(), secondPath);
        first.getResponse("todo first");
        assertFalse(Files.exists(secondPath));
        assertEquals("No tasks to show", second.getResponse("list"));
        assertEquals("{T}{0}{first}\n", Storage.load(firstPath).toString());
    }

    @Test
    public void getResponse_searchNumbers_targetOriginalTasksAcrossSessions() throws Exception {
        Path savePath = temporaryDirectory.resolve("search.txt");
        Ubis ubis = new Ubis(savePath);
        ubis.getResponse("todo pay rent");
        ubis.getResponse("todo read book");
        ubis.getResponse("todo buy book");
        assertEquals("2: [T][ ] read book\n3: [T][ ] buy book", ubis.getResponse("find book"));
        ubis.getResponse("mark 2");
        assertEquals("2: [T][X] read book\n3: [T][ ] buy book", ubis.getResponse("find book"));
        ubis.getResponse("unmark 2");
        ubis.getResponse("delete 3");
        assertEquals("1: [T][ ] pay rent\n2: [T][ ] read book", new Ubis(savePath).getResponse("list"));
        ubis.getResponse("delete 1");
        assertEquals("1: [T][ ] read book", ubis.getResponse("find book"));
    }

}
