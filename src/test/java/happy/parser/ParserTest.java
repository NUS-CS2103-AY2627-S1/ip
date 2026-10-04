package happy.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import happy.exception.HappyException;
import happy.storage.Storage;
import happy.task.TaskList;

/**
 * Unit tests for the Parser class.
 */
public class ParserTest {
    private TaskList tasks;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        storage = new Storage(new File("build", "test_happy.txt").getPath());
    }

    @Test
    public void parseAndExecuteForGui_validTodo_addsTask() throws HappyException {
        String response = Parser.parseAndExecuteForGui("todo read book", tasks, storage);
        assertTrue(response.contains("Got it. I've added this task:"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void parseAndExecuteForGui_validDeadline_addsTask() throws HappyException {
        String response = Parser.parseAndExecuteForGui("deadline submit assignment /by 2026-12-01", tasks, storage);
        assertTrue(response.contains("Got it. I've added this task:"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void parseAndExecuteForGui_validEvent_addsTask() throws HappyException {
        String response = Parser.parseAndExecuteForGui(
                "event workshop /from 2026-10-10 /to 2026-10-12", tasks, storage);
        assertTrue(response.contains("Got it. I've added this task:"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void parseAndExecuteForGui_emptyTodo_throwsException() {
        assertThrows(HappyException.class, () -> Parser.parseAndExecuteForGui("todo", tasks, storage));
    }

    @Test
    public void parseAndExecuteForGui_pipeCharacter_throwsException() {
        assertThrows(HappyException.class, () -> Parser.parseAndExecuteForGui("todo buy | milk", tasks, storage));
    }

    @Test
    public void parseAndExecuteForGui_duplicateTask_throwsException() throws HappyException {
        Parser.parseAndExecuteForGui("todo read book", tasks, storage);
        assertThrows(HappyException.class, () -> Parser.parseAndExecuteForGui("todo read book", tasks, storage));
    }

    @Test
    public void parseAndExecuteForGui_eventStartDateAfterEndDate_throwsException() {
        assertThrows(HappyException.class, () -> Parser.parseAndExecuteForGui(
                "event party /from 2026-10-12 /to 2026-10-10", tasks, storage));
    }

    @Test
    public void parseAndExecuteForGui_validDateFilter_returnsMatchingTasks() throws HappyException {
        Parser.parseAndExecuteForGui("deadline submit assignment /by 2026-10-15", tasks, storage);
        String response = Parser.parseAndExecuteForGui("date 2026-10-15", tasks, storage);
        assertTrue(response.contains("Here are the tasks occurring on Oct 15 2026:"));
    }

    @Test
    public void parseAndExecuteForGui_unrecognizedCommand_throwsException() {
        assertThrows(HappyException.class, () -> Parser.parseAndExecuteForGui("invalidcommand", tasks, storage));
    }
}
