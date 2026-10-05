package aegis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import aegis.task.Task;
import aegis.task.Todo;

public class StorageTest {

    @Test
    public void parseTaskLine_todoLine_addsTodoTask() throws AegisException {
        Storage storage = new Storage("unused.txt");
        ArrayList<Task> loadedTasks = new ArrayList<>();

        storage.parseTaskLine("T | 0 | borrow book", loadedTasks);

        assertEquals(1, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("borrow book", loadedTasks.get(0).getDescription());
    }

    @Test
    public void parseTaskLine_eventEndBeforeStart_throwsAegisException() {
        Storage storage = new Storage("unused.txt");
        ArrayList<Task> loadedTasks = new ArrayList<>();

        AegisException e = assertThrows(AegisException.class, () ->
                storage.parseTaskLine("E | 0 | meeting | 2026-08-23 | 2026-08-22", loadedTasks));

        assertEquals("Incorrect event date order in file", e.getMessage());
    }

    @Test
    public void parseTaskLine_duplicateTodo_throwsAegisException() throws AegisException {
        Storage storage = new Storage("unused.txt");
        ArrayList<Task> loadedTasks = new ArrayList<>();
        storage.parseTaskLine("T | 0 | borrow book", loadedTasks);

        AegisException e = assertThrows(AegisException.class, () ->
                storage.parseTaskLine("T | 1 | borrow book", loadedTasks));

        assertEquals("Duplicate task in file", e.getMessage());
    }
}
