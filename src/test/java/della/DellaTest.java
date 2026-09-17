package della;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DellaTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void getResponse_listOnEmptyStorage_emptyResponseReturned() {
        Della della = new Della(temporaryDirectory.resolve("tasks.txt").toString());
        assertEquals("Wow you have no tasks!", della.getResponse("list"));
    }

    @Test
    public void getResponse_addListMarkUnmarkDelete_commandsHandled() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Della della = new Della(file.toString());

        della.getResponse("todo read book");
        assertEquals("1. [T][ ] read book", della.getResponse("list"));
        della.getResponse("mark 1");
        assertEquals("1. [T][X] read book", della.getResponse("list"));
        della.getResponse("unmark 1");
        assertEquals("1. [T][ ] read book", della.getResponse("list"));
        della.getResponse("delete 1");
        assertEquals("Wow you have no tasks!", della.getResponse("list"));
        assertEquals(false, Files.readAllLines(file).size() > 0);
    }

    @Test
    public void getResponse_invalidCommand_errorReturned() {
        Della della = new Della(temporaryDirectory.resolve("tasks.txt").toString());
        assertEquals("I don't recognise this command :( Try again pls tyvm", della.getResponse("unknown"));
    }

    @Test
    public void getResponse_findMatchingTask_matchingTaskDisplayed() {
        Della della = new Della(temporaryDirectory.resolve("tasks.txt").toString());
        della.getResponse("todo read book");
        assertEquals("1. [T][ ] read book", della.getResponse("find book"));
    }
}
