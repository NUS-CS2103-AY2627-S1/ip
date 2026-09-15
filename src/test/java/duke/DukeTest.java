package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DukeTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getWelcomeMessage_returnsGreetingWithoutTerminalBanner() {
        Duke duke = new Duke(temporaryDirectory.resolve("duke.txt").toString());

        String welcomeMessage = duke.getWelcomeMessage();

        assertEquals("HELLO! I'm GARY!\n"
                + "How can I help you today?\n"
                + "(Type bye to exit)", welcomeMessage);
    }

    @Test
    public void getCommandResponse_unknownCommand_returnsErrorResponse() {
        Duke duke = new Duke(temporaryDirectory.resolve("duke.txt").toString());

        CommandResponse response = duke.getCommandResponse("unknown");

        assertTrue(response.isError());
        assertEquals("I'm sorry, but Gary doesn't know what that means!", response.text());
    }

    @Test
    public void getCommandResponse_invalidCommandArguments_returnsErrorResponse() {
        Duke duke = new Duke(temporaryDirectory.resolve("duke.txt").toString());

        CommandResponse response = duke.getCommandResponse("todo");

        assertTrue(response.isError());
        assertEquals("The Todo description cannot be empty!", response.text());
    }

    @Test
    public void getCommandResponse_validCommand_returnsStandardResponse() {
        Duke duke = new Duke(temporaryDirectory.resolve("duke.txt").toString());

        CommandResponse response = duke.getCommandResponse("todo read book");

        assertFalse(response.isError());
        assertEquals("Got it! Here's the task you added:\n"
                + "  [T] [ ] read book\n"
                + "Now you have 1 tasks in your list!", response.text());
    }

    @Test
    public void getCommandResponse_emptyInput_returnsErrorResponse() {
        Duke duke = new Duke(temporaryDirectory.resolve("duke.txt").toString());

        CommandResponse response = duke.getCommandResponse("   ");

        assertTrue(response.isError());
        assertEquals("Please enter a command.", response.text());
    }

    @Test
    public void getCommandResponse_nullInput_returnsErrorResponse() {
        Duke duke = new Duke(temporaryDirectory.resolve("duke.txt").toString());

        CommandResponse response = duke.getCommandResponse(null);

        assertTrue(response.isError());
        assertEquals("Please enter a command.", response.text());
    }

    @Test
    public void getResponse_validCommand_returnsResponseText() {
        Duke duke = new Duke(temporaryDirectory.resolve("duke.txt").toString());

        String response = duke.getResponse("todo read book");

        assertTrue(response.contains("[T] [ ] read book"));
    }

    @Test
    public void getStartupError_validStorage_returnsEmptyOptional() {
        Duke duke = new Duke(temporaryDirectory.resolve("duke.txt").toString());

        assertTrue(duke.getStartupError().isEmpty());
    }

    @Test
    public void getCommandResponse_duplicateTask_returnsErrorAndKeepsOriginal() {
        Duke duke = new Duke(temporaryDirectory.resolve("duke.txt").toString());
        duke.getCommandResponse("todo read book");

        CommandResponse duplicateResponse = duke.getCommandResponse("todo READ   BOOK");
        CommandResponse listResponse = duke.getCommandResponse("list");

        assertTrue(duplicateResponse.isError());
        assertEquals("That task already exists in your list.", duplicateResponse.text());
        assertEquals("These are the tasks you have in your list!\n1. [T] [ ] read book", listResponse.text());
    }

    @Test
    public void getCommandResponse_malformedEvent_returnsFormatError() {
        Duke duke = new Duke(temporaryDirectory.resolve("duke.txt").toString());

        CommandResponse response = duke.getCommandResponse("event meeting /from 2026-08-25");

        assertTrue(response.isError());
        assertEquals("Use: event <description> /from <YYYY-MM-DD> /to <YYYY-MM-DD>.", response.text());
    }

    @Test
    public void constructor_invalidStoredData_reportsStartupErrorAndRemainsUsable() throws Exception {
        Path taskFile = temporaryDirectory.resolve("duke.txt");
        Files.writeString(taskFile, "D | 0 | return book | 2026-02-30\n");

        Duke duke = new Duke(taskFile.toString());
        CommandResponse response = duke.getCommandResponse("list");

        assertTrue(duke.getStartupError().isPresent());
        assertTrue(duke.getStartupError().orElseThrow().contains("Task file data is invalid on line 1"));
        assertFalse(response.isError());
        assertEquals("These are the tasks you have in your list!", response.text());
    }

    @Test
    public void getCommandResponse_storageSaveFails_returnsErrorAndRollsBackTask() throws Exception {
        Path blockingParent = temporaryDirectory.resolve("not-a-directory");
        Duke duke = new Duke(blockingParent.resolve("duke.txt").toString());
        Files.writeString(blockingParent, "content");

        CommandResponse addResponse = duke.getCommandResponse("todo read book");
        CommandResponse listResponse = duke.getCommandResponse("list");

        assertTrue(addResponse.isError());
        assertEquals("Could not save tasks. Check file permissions and available disk space.", addResponse.text());
        assertEquals("These are the tasks you have in your list!", listResponse.text());
    }

    @Test
    public void getCommandResponse_byeCommandSucceeds_requestsExit() {
        Duke duke = new Duke(temporaryDirectory.resolve("duke.txt").toString());

        CommandResponse response = duke.getCommandResponse("bye");

        assertFalse(response.isError());
        assertTrue(response.shouldExit());
    }

    @Test
    public void getCommandResponse_byeSaveFails_doesNotRequestExit() throws Exception {
        Path blockingParent = temporaryDirectory.resolve("not-a-directory");
        Duke duke = new Duke(blockingParent.resolve("duke.txt").toString());
        Files.writeString(blockingParent, "content");

        CommandResponse response = duke.getCommandResponse("bye");

        assertTrue(response.isError());
        assertFalse(response.shouldExit());
        assertEquals("Could not save tasks. Check file permissions and available disk space.", response.text());
    }

    @Test
    public void run_commandsUntilBye_executesAndPersistsChanges() throws Exception {
        Path taskFile = temporaryDirectory.resolve("duke.txt");
        Duke duke = new Duke(taskFile.toString());

        String output = runWithInput(duke, "   \ntodo read book\nmark 1\nunknown\nlist\nbye\n");

        assertTrue(output.contains("Please enter a command."));
        assertTrue(output.contains("Gary marked task 1 as done!"));
        assertTrue(output.contains("I'm sorry, but Gary doesn't know what that means!"));
        assertTrue(output.contains("1. [T] [X] read book"));
        assertTrue(output.contains("Bye! Hope to see you again soon!"));
        assertEquals(List.of("T | 1 | read book"), Files.readAllLines(taskFile));
    }

    @Test
    public void run_endOfInput_savesAndReturns() throws Exception {
        Path taskFile = temporaryDirectory.resolve("duke.txt");
        Duke duke = new Duke(taskFile.toString());

        String output = runWithInput(duke, "");

        assertTrue(output.contains("HELLO! I'm GARY!"));
        assertEquals("", Files.readString(taskFile));
    }

    @Test
    public void run_startupError_displaysWarningAndRemainsUsable() throws Exception {
        Path taskFile = temporaryDirectory.resolve("duke.txt");
        Files.writeString(taskFile, "invalid data\n");
        Duke duke = new Duke(taskFile.toString());

        String output = runWithInput(duke, "list\nbye\n");

        assertTrue(output.contains("Task file data is invalid on line 1"));
        assertTrue(output.contains("Gary started with an empty list"));
        assertTrue(output.contains("These are the tasks you have in your list!"));
    }

    /** Runs the terminal application with isolated standard input and output. */
    private String runWithInput(Duke duke, String input) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
            duke.run();
            return capturedOutput.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }
}
