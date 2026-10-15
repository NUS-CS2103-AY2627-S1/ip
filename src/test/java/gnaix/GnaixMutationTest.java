package gnaix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import gnaix.task.Deadline;
import gnaix.task.Task;
import gnaix.task.Todo;

class GnaixMutationTest {

    @Test
    void getGuiResponse_todoCommand_taskAddedAndPersisted(
            @TempDir Path tempDir) throws Exception {
        Storage storage = createTempStorage(tempDir);
        Gnaix gnaix = new Gnaix(storage);

        GuiResponse response = gnaix.getGuiResponse("todo buy milk #home");

        assertFalse(response.isError());
        assertEquals("Fine. I've added this:", response.getHeading());
        assertEquals("You now have 1 tasks.", response.getFooter());
        assertEquals(1, response.getTasks().size());
        assertEquals(1, response.getTasks().get(0).getNumber());
        assertEquals("buy milk", response.getTasks().get(0).getTask().getDescription());
        assertEquals(Set.of("home"), response.getTasks().get(0).getTask().getTags());

        ArrayList<Task> savedTasks = storage.load();
        assertEquals(1, savedTasks.size());
        assertInstanceOf(Todo.class, savedTasks.get(0));
        assertEquals("buy milk", savedTasks.get(0).getDescription());
        assertEquals(Set.of("home"), savedTasks.get(0).getTags());
    }

    @Test
    void getGuiResponse_markCommand_taskCompletedAndPersisted(
            @TempDir Path tempDir) throws Exception {
        Storage storage = createTempStorage(tempDir);
        Gnaix gnaix = new Gnaix(storage);
        gnaix.getGuiResponse("todo buy milk");

        GuiResponse response = gnaix.getGuiResponse("mark 1");

        assertFalse(response.isError());
        assertEquals("There. It's done:", response.getHeading());
        assertEquals(1, response.getTasks().size());
        assertTrue(response.getTasks().get(0).getTask().isCompleted());
        assertTrue(storage.load().get(0).isCompleted());
    }

    @Test
    void getGuiResponse_unmarkCommand_taskIncompleteAndPersisted(
            @TempDir Path tempDir) throws Exception {
        Storage storage = createTempStorage(tempDir);
        Gnaix gnaix = new Gnaix(storage);
        gnaix.getGuiResponse("todo buy milk");
        gnaix.getGuiResponse("mark 1");

        GuiResponse response = gnaix.getGuiResponse("unmark 1");

        assertFalse(response.isError());
        assertEquals("Apparently we're undoing that.", response.getHeading());
        assertEquals(1, response.getTasks().size());
        assertFalse(response.getTasks().get(0).getTask().isCompleted());
        assertFalse(storage.load().get(0).isCompleted());
    }

    @Test
    void getGuiResponse_deleteCommand_taskRemovedAndPersisted(
            @TempDir Path tempDir) throws Exception {
        Storage storage = createTempStorage(tempDir);
        Gnaix gnaix = new Gnaix(storage);
        gnaix.getGuiResponse("todo first");
        gnaix.getGuiResponse("todo second");

        GuiResponse response = gnaix.getGuiResponse("delete 1");

        assertFalse(response.isError());
        assertEquals("Gone. I've removed this:", response.getHeading());
        assertEquals("You now have 1 tasks left.", response.getFooter());
        assertEquals(1, response.getTasks().size());
        assertEquals("first", response.getTasks().get(0).getTask().getDescription());

        ArrayList<Task> savedTasks = storage.load();
        assertEquals(1, savedTasks.size());
        assertEquals("second", savedTasks.get(0).getDescription());
    }

    @Test
    void getResponse_deadlineCommand_taskAddedAndPersisted(
            @TempDir Path tempDir) throws Exception {
        Storage storage = createTempStorage(tempDir);
        Gnaix gnaix = new Gnaix(storage);

        String response = gnaix.getResponse(
                "deadline submit quiz /by 2026-09-10 #school");

        assertTrue(response.contains("Fine. I've added this:"));
        assertTrue(response.contains("You now have 1 tasks."));

        ArrayList<Task> savedTasks = storage.load();
        Deadline deadline = assertInstanceOf(Deadline.class, savedTasks.get(0));
        assertEquals("submit quiz", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 10), deadline.getDoBy());
        assertEquals(Set.of("school"), deadline.getTags());
    }

    private Storage createTempStorage(Path tempDir) {
        return new Storage(tempDir.resolve("tasks.txt"));
    }
}
