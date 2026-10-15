package pebby;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class PebbyTest {

    @Test
    void getResponse_todoThenList_runsTheSameCommandsAsTheConsole() throws IOException {
        Path storagePath = Files.createTempFile("pebby-gui-test", ".txt");
        Files.delete(storagePath);
        System.setProperty("pebby.storage.path", storagePath.toString());
        Pebby pebby = new Pebby();

        String addResponse = pebby.getResponse("todo read book");
        String listResponse = pebby.getResponse("list");

        assertTrue(addResponse.contains("[T] [ ] read book"));
        assertTrue(listResponse.contains("1. [T] [ ] read book"));
    }

    @Test
    void getResponse_help_returnsTheCommandReference() {
        Pebby pebby = new Pebby();

        String response = pebby.getResponse("help");

        assertTrue(response.contains("ADD TASKS"));
        assertTrue(response.contains("• todo <task description>"));
        assertTrue(response.contains("event <task description> /from <yyyy-MM-dd or d MMMM yyyy>"));
        assertTrue(response.contains("schedule <yyyy-MM-dd or d MMMM yyyy>"));
    }

    @Test
    void getResponse_malformedCommands_returnsHelpfulErrors() {
        Pebby pebby = new Pebby();

        assertTrue(pebby.getResponse(" todo trimmed input ").contains("[T] [ ] trimmed input"));
        assertTrue(pebby.getResponse("todo  read book").contains("Use single spaces"));
        assertTrue(pebby.getResponse("list extra").contains("does not take an argument"));
    }

    @Test
    void getResponse_invalidEventRangeAndDuplicate_returnsHelpfulErrors() {
        Pebby pebby = new Pebby();

        assertTrue(pebby.getResponse("event meeting /from 2019-12-03 /to 2019-12-03")
                .contains("start date must be before"));
        pebby.getResponse("todo unique task");
        assertTrue(pebby.getResponse("todo unique task").contains("identical task"));
    }

    @Test
    void getResponse_writtenDate_addsAndSchedulesTask() {
        Pebby pebby = new Pebby();

        assertTrue(pebby.getResponse("deadline return book /by 15 June 2026")
                .contains("by: Jun 15 2026"));
        assertTrue(pebby.getResponse("schedule 15 June 2026").contains("return book"));
    }
}
