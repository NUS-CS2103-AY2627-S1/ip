package sage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class MarkCommandTest {

    @Test
    void repeatedMarkCommand_reportsTaskIsAlreadyDone() throws IOException {
        Path filePath = Files.createTempFile("sage-mark-test", ".txt");
        Sage sage = new Sage(filePath.toString());

        sage.processCommand("todo read book");
        sage.processCommand("mark 1");

        assertEquals("Task 1 is already marked as done.", sage.processCommand("mark 1"));
    }

    @Test
    void repeatedUnmarkCommand_reportsTaskIsAlreadyNotDone() throws IOException {
        Path filePath = Files.createTempFile("sage-unmark-test", ".txt");
        Sage sage = new Sage(filePath.toString());

        sage.processCommand("todo read book");

        assertEquals("Task 1 is already marked as not done.", sage.processCommand("unmark 1"));
    }
}
