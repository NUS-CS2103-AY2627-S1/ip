package chim.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import chim.exception.ChimException;
import chim.storage.Storage;
import chim.task.TaskList;
import chim.ui.Ui;

public class ParserTest {

    @TempDir
    Path tempDir;

    private Parser parser;
    private TaskList tasks;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        parser = new Parser();
        tasks = new TaskList();
        ui = new Ui();
        storage = new Storage(tempDir.resolve("test.txt").toString());
    }

    @Test
    public void parseAndExecute_wordLikeTodo_throwsUnknownCommand() {
        assertThrows(ChimException.class, () -> parser.parseAndExecute("todoodle stuff", tasks, ui, storage));
    }

    @Test
    public void parseAndExecute_wordLikeFind_throwsUnknownCommand() {
        assertThrows(ChimException.class, () -> parser.parseAndExecute("findxyz book", tasks, ui, storage));
    }

    @Test
    public void parseAndExecute_validTodo_addsTask() throws ChimException {
        parser.parseAndExecute("todo read book", tasks, ui, storage);
        assertEquals(1, tasks.size());
    }

    @Test
    public void parseAndExecute_emptyTodo_throwsException() {
        assertThrows(ChimException.class, () -> parser.parseAndExecute("todo", tasks, ui, storage));
    }

    @Test
    public void parseAndExecute_duplicateByInDeadline_throwsException() {
        assertThrows(ChimException.class, () ->
                parser.parseAndExecute("deadline test /by 2020-01-01 /by 2020-02-01", tasks, ui, storage));
    }

    @Test
    public void parseAndExecute_duplicateFromInEvent_throwsException() {
        assertThrows(ChimException.class, () ->
                parser.parseAndExecute("event test /from 9am /from 10am /to 5pm", tasks, ui, storage));
    }

    @Test
    public void parseAndExecute_pipeCharacterInDescription_throwsException() {
        assertThrows(ChimException.class, () -> parser.parseAndExecute("todo read | book", tasks, ui, storage));
    }

    @Test
    public void parseAndExecute_eventStartAfterEnd_throwsException() {
        assertThrows(ChimException.class, () ->
                parser.parseAndExecute("event meeting /from 2020-01-05 /to 2020-01-01", tasks, ui, storage));
    }

    @Test
    public void parseAndExecute_eventStartBeforeEnd_addsTask() throws ChimException {
        parser.parseAndExecute("event meeting /from 2020-01-01 /to 2020-01-05", tasks, ui, storage);
        assertEquals(1, tasks.size());
    }

    @Test
    public void parseAndExecute_eventNonDateTimes_addsTaskWithoutValidation() throws ChimException {
        parser.parseAndExecute("event meeting /from 2pm /to 4pm", tasks, ui, storage);
        assertEquals(1, tasks.size());
    }

    @Test
    public void parseAndExecute_duplicateTodo_throwsException() throws ChimException {
        parser.parseAndExecute("todo read book", tasks, ui, storage);
        assertThrows(ChimException.class, () -> parser.parseAndExecute("todo read book", tasks, ui, storage));
    }

    @Test
    public void parseAndExecute_sameDescriptionDifferentType_notDuplicate() throws ChimException {
        parser.parseAndExecute("todo read book", tasks, ui, storage);
        parser.parseAndExecute("deadline read book /by 2020-01-01", tasks, ui, storage);
        assertEquals(2, tasks.size());
    }

    @Test
    public void parseAndExecute_deleteInvalidIndex_throwsException() throws ChimException {
        parser.parseAndExecute("todo read book", tasks, ui, storage);
        assertThrows(ChimException.class, () -> parser.parseAndExecute("delete 5", tasks, ui, storage));
    }

    @Test
    public void parseAndExecute_findNoKeyword_throwsException() {
        assertThrows(ChimException.class, () -> parser.parseAndExecute("find", tasks, ui, storage));
    }

    @Test
    public void parseAndExecute_findMatchingKeyword_returnsResults() throws ChimException {
        parser.parseAndExecute("todo read book", tasks, ui, storage);
        String response = parser.parseAndExecute("find book", tasks, ui, storage);
        assertTrue(response.contains("read book"));
    }

    @Test
    public void parseAndExecute_bye_returnsGoodbyeMessage() throws ChimException {
        String response = parser.parseAndExecute("bye", tasks, ui, storage);
        assertEquals(ui.getGoodbyeMessage(), response);
    }

    @Test
    public void parseAndExecute_setValidPriority_updatesTask() throws ChimException {
        parser.parseAndExecute("todo read book", tasks, ui, storage);
        parser.parseAndExecute("priority 1 high", tasks, ui, storage);
        assertEquals("high", tasks.get(0).getPriority());
    }

    @Test
    public void parseAndExecute_setInvalidPriority_throwsException() throws ChimException {
        parser.parseAndExecute("todo read book", tasks, ui, storage);
        assertThrows(ChimException.class, () -> parser.parseAndExecute("priority 1 urgent", tasks, ui, storage));
    }

    @Test
    public void parseAndExecute_priorityMissingArgs_throwsException() throws ChimException {
        parser.parseAndExecute("todo read book", tasks, ui, storage);
        assertThrows(ChimException.class, () -> parser.parseAndExecute("priority 1", tasks, ui, storage));
    }

    @Test
    public void parseAndExecute_priorityNoneResetsPriority() throws ChimException {
        parser.parseAndExecute("todo read book", tasks, ui, storage);
        parser.parseAndExecute("priority 1 high", tasks, ui, storage);
        parser.parseAndExecute("priority 1 none", tasks, ui, storage);
        assertEquals("none", tasks.get(0).getPriority());
    }

    @Test
    public void parseAndExecute_priorityInvalidIndex_throwsException() {
        assertThrows(ChimException.class, () -> parser.parseAndExecute("priority 5 high", tasks, ui, storage));
    }
}
