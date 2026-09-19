package ollie;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class OllieTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void getResponse_addListAndFindCommands_returnsExpectedResponses() {
        Ollie ollie = new Ollie(tempDirectory.resolve("data").resolve("tasks.txt"));

        assertTrue(ollie.getResponse("  todo   Read Book  ").contains("Mission logged"));
        assertTrue(ollie.getResponse("list").contains("[todo][ ] Read Book"));
        assertTrue(ollie.getResponse("find book").contains("[todo][ ] Read Book"));
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorResponse() {
        Ollie ollie = new Ollie(tempDirectory.resolve("tasks.txt"));

        Ollie.CommandResponse response = ollie.getCommandResponse("unknown");

        assertTrue(response.message().contains("Course correction needed"));
        assertTrue(response.isError());
        assertFalse(response.isExit());
    }

    @Test
    public void isExitCommand_byeCommand_returnsTrue() {
        Ollie ollie = new Ollie(tempDirectory.resolve("tasks.txt"));

        assertTrue(ollie.isExitCommand("bye"));
        assertTrue(ollie.isExitCommand("  bye  "));
        assertFalse(ollie.isExitCommand("todo say bye"));
        assertFalse(ollie.isExitCommand("bye now"));
    }

    @Test
    public void getCommandResponse_duplicateAndUnexpectedArguments_returnsErrors() {
        Ollie ollie = new Ollie(tempDirectory.resolve("tasks.txt"));

        assertFalse(ollie.getCommandResponse("todo read book").isError());
        assertTrue(ollie.getCommandResponse("todo Read  Book").isError());
        assertTrue(ollie.getCommandResponse("list now").isError());
        assertTrue(ollie.getCommandResponse("help please").isError());
        assertTrue(ollie.getCommandResponse("bye now").isError());
    }

    @Test
    public void getCommandResponse_repeatedStatusChange_returnsError() {
        Ollie ollie = new Ollie(tempDirectory.resolve("tasks.txt"));
        ollie.getResponse("todo read book");

        assertTrue(ollie.getCommandResponse("unmark 1").isError());
        assertFalse(ollie.getCommandResponse("mark 1").isError());
        assertTrue(ollie.getCommandResponse("mark 1").isError());
    }

    @Test
    public void getCommandResponse_saveFails_restoresTaskList() throws IOException {
        Path parentFile = tempDirectory.resolve("not-a-folder");
        Files.writeString(parentFile, "content");
        Ollie ollie = new Ollie(parentFile.resolve("tasks.txt"));

        Ollie.CommandResponse addResponse = ollie.getCommandResponse("todo read book");
        String listResponse = ollie.getResponse("list");

        assertTrue(addResponse.isError());
        assertFalse(listResponse.contains("read book"));
    }

    @Test
    public void getCommandResponse_updateSaveFails_restoresTaskState() throws IOException {
        Path filePath = tempDirectory.resolve("tasks.txt");
        Files.writeString(filePath, "T | 0 | read book");
        Ollie ollie = new Ollie(filePath);
        ollie.getResponse("list");
        Files.delete(filePath);
        Files.createDirectory(filePath);

        Ollie.CommandResponse markResponse = ollie.getCommandResponse("mark 1");
        Ollie.CommandResponse deleteResponse = ollie.getCommandResponse("delete 1");
        String listResponse = ollie.getResponse("list");

        assertTrue(markResponse.isError());
        assertTrue(deleteResponse.isError());
        assertTrue(listResponse.contains("[todo][ ] read book"));
    }

    @Test
    public void getCommandResponse_invalidStoredData_allowsHelpButRejectsTaskCommands()
            throws IOException {
        Path filePath = tempDirectory.resolve("tasks.txt");
        Files.writeString(filePath, "invalid data");
        Ollie ollie = new Ollie(filePath);

        assertFalse(ollie.getCommandResponse("help").isError());
        assertTrue(ollie.getCommandResponse("list").isError());
        assertTrue(ollie.getCommandResponse("todo read book").isError());
    }

    @Test
    public void getResponse_helpCommand_returnsUnindentedReadableUsage() {
        Ollie ollie = new Ollie(tempDirectory.resolve("tasks.txt"));

        String response = ollie.getResponse("help");

        assertTrue(response.startsWith("Flight manual — available commands:"));
        assertTrue(response.contains("• deadline <description>"
                + System.lineSeparator() + "    /by <yyyy-MM-dd>"));
        assertTrue(response.contains("• event <description>"
                + System.lineSeparator() + "    /from <yyyy-MM-dd>"
                + System.lineSeparator() + "    /to <yyyy-MM-dd>"));
    }
}
