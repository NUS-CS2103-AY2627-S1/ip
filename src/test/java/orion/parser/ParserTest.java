package orion.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import orion.exception.OrionException;
import orion.format.ResponseFormatter;
import orion.storage.Storage;
import orion.task.TaskList;

class ParserTest {
    @Test
    public void isExit_byeCommand_returnsTrue() {
        assertTrue(Parser.isExit("bye"));
        assertTrue(Parser.isExit("BYE"));
        assertTrue(Parser.isExit("bye  "));
    }

    @Test
    public void isExit_nonByeCommand_returnsFalse() {
        assertFalse(Parser.isExit("todo read book"));
        assertFalse(Parser.isExit(""));
        assertFalse(Parser.isExit("list"));
    }

    @Test
    public void parseAndExecute_invalidCommand_throwsException() {
        TaskList tasks = new TaskList();
        ResponseFormatter formatter = new ResponseFormatter();
        Storage storage = new Storage("./data/dummy.txt");
        assertThrows(OrionException.class, () ->
            Parser.parseAndExecute("invalidCommand", tasks, formatter, storage)
        );
    }

    @Test
    public void parseAndExecute_todoWithoutDescription_throwsException() {
        TaskList tasks = new TaskList();
        ResponseFormatter formatter = new ResponseFormatter();
        Storage storage = new Storage("./data/dummy.txt");
        assertThrows(OrionException.class, () ->
            Parser.parseAndExecute("todo", tasks, formatter, storage)
        );
    }
}
