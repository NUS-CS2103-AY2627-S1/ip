package magnus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import magnus.exception.MagnusException;

/**
 * Tests the command-response interface exposed by {@link Magnus}.
 */
public class MagnusTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_validCommand_returnsCommandMessage() throws MagnusException {
        Magnus magnus = createMagnus();

        String response = magnus.getResponse("todo read book");

        assertEquals("\tOpening move complete - I've added this To-Do task:\n\n\t[T][ ] read book\n\n"
                + "\tYou now have 1 task in the list.", response);
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorMessage() throws MagnusException {
        Magnus magnus = createMagnus();

        String response = magnus.getResponse("unknown");

        assertEquals("\tThat move is not in my playbook - I don't recognize the command 'unknown'.", response);
        assertFalse(magnus.isExitRequested());
    }

    @Test
    public void getResponseResult_validCommand_marksResponseAsSuccessful() throws MagnusException {
        Magnus magnus = createMagnus();

        MagnusResponse response = magnus.getResponseResult("todo read book");

        assertEquals("\tOpening move complete - I've added this To-Do task:\n\n\t[T][ ] read book\n\n"
                        + "\tYou now have 1 task in the list.",
                response.message());
        assertFalse(response.isError());
    }

    @Test
    public void getResponseResult_invalidCommand_marksResponseAsError() throws MagnusException {
        Magnus magnus = createMagnus();

        MagnusResponse response = magnus.getResponseResult("unknown");

        assertEquals("\tThat move is not in my playbook - I don't recognize the command 'unknown'.",
                response.message());
        assertTrue(response.isError());
    }

    @Test
    public void getResponse_byeCommand_returnsMessageAndRequestsExit() throws MagnusException {
        Magnus magnus = createMagnus();

        String response = magnus.getResponse("bye");

        assertEquals("\tThe board is set aside for now. Goodbye, and see you next game!", response);
        assertTrue(magnus.isExitRequested());
    }

    @Test
    public void getResponse_commandAfterBye_clearsExitRequest() throws MagnusException {
        Magnus magnus = new Magnus(this.temporaryDirectory.resolve("magnus.txt"));
        magnus.getResponse("bye");

        magnus.getResponse("list");

        assertFalse(magnus.isExitRequested());
    }

    @Test
    public void constructor_corruptedDataFile_throwsMagnusException() throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("magnus.txt");
        Files.writeString(dataFile, "corrupted", StandardCharsets.UTF_8);

        assertThrows(MagnusException.class, () -> new Magnus(dataFile));
    }

    @Test
    public void getResponse_validUpdateFlow_updatesTaskAndPersistsResult() throws MagnusException {
        Magnus magnus = createMagnus();
        magnus.getResponse("todo read book");

        String prompt = magnus.getResponse("update 1");
        String updateResponse = magnus.getResponse("read two books");
        Magnus reloadedMagnus = createMagnus();

        assertEquals("\tYour move - enter the updated To-Do task in this format:\n"
                + "\t<new task name>", prompt);
        assertEquals("\tPosition updated - I've updated this task:\n\n\t[T][ ] read two books",
                updateResponse);
        assertEquals("\tHere's the current position - your full task list:\n\n"
                        + "\t1. [T][ ] read two books",
                reloadedMagnus.getResponse("list"));
    }

    @Test
    public void getResponse_invalidUpdateInput_failsThenProcessesNextCommandNormally() throws MagnusException {
        Magnus magnus = createMagnus();
        magnus.getResponse("deadline submit report /by 02/09/2026 1500");
        magnus.getResponse("update 1");

        String failureResponse = magnus.getResponse("submit report tomorrow");
        String listResponse = magnus.getResponse("list");

        assertEquals("\tThat move does not match the required format - the task was not updated.",
                failureResponse);
        assertEquals("\tHere's the current position - your full task list:\n\n"
                + "\t1. [D][ ] submit report (by: Sep 02, 2026 15:00)", listResponse);
    }

    @Test
    public void getResponse_duplicateTask_rejectsTaskAndKeepsSingleCopy() throws MagnusException {
        Magnus magnus = createMagnus();
        magnus.getResponse("todo read book");

        MagnusResponse duplicateResponse = magnus.getResponseResult("todo read book");
        String listResponse = magnus.getResponse("list");

        assertTrue(duplicateResponse.isError());
        assertEquals("\tThat piece is already on the board - an identical task already exists.",
                duplicateResponse.message());
        assertEquals("\tHere's the current position - your full task list:\n\n"
                + "\t1. [T][ ] read book", listResponse);
    }

    @Test
    public void getResponse_eventWithNonIncreasingTimes_rejectsEvent() throws MagnusException {
        MagnusResponse response = createMagnus().getResponseResult(
                "event meeting /from 02/09/2026 1500 /to 02/09/2026 1500");

        assertTrue(response.isError());
        assertEquals("\tThe clock rejects that event - use valid start and end times "
                + "in dd/MM/yyyy HHmm format, with exactly one /from followed by one /to and the start "
                + "strictly before the end.\n"
                + "\tUsage: event <task description> /from <dd/MM/yyyy HHmm> /to <dd/MM/yyyy HHmm>",
                response.message());
    }

    @Test
    public void getResponse_surroundingAndRepeatedWhitespace_acceptsCommand() throws MagnusException {
        MagnusResponse response = createMagnus().getResponseResult(
                "   deadline   submit report   /by   02/09/2026 1500   ");

        assertFalse(response.isError());
        assertTrue(response.message().contains("[D][ ] submit report"));
    }

    @Test
    public void getResponse_deadlineWithWrongDelimiter_rejectsCommand() throws MagnusException {
        MagnusResponse response = createMagnus().getResponseResult(
                "deadline submit report /from 02/09/2026 1500");

        assertTrue(response.isError());
        assertTrue(response.message().contains("provide exactly one /by field"));
    }

    @Test
    public void getResponse_eventWithReversedDelimiters_rejectsCommand() throws MagnusException {
        MagnusResponse response = createMagnus().getResponseResult(
                "event meeting /to 02/09/2026 1600 /from 02/09/2026 1500");

        assertTrue(response.isError());
        assertTrue(response.message().contains("exactly one /from followed by one /to"));
    }

    @Test
    public void getResponse_repeatedParameter_rejectsCommand() throws MagnusException {
        MagnusResponse response = createMagnus().getResponseResult(
                "deadline submit report /by 02/09/2026 1500 /by 03/09/2026 1500");

        assertTrue(response.isError());
        assertTrue(response.message().contains("provide exactly one /by field"));
    }

    @Test
    public void getResponse_nonExistentDate_rejectsCommand() throws MagnusException {
        MagnusResponse response = createMagnus().getResponseResult(
                "deadline submit report /by 30/02/2026 1500");

        assertTrue(response.isError());
        assertTrue(response.message().contains("clock rejects that deadline"));
    }

    @Test
    public void getResponse_multilineInput_rejectsCommand() throws MagnusException {
        MagnusResponse response = createMagnus().getResponseResult("todo read book\nlist");

        assertTrue(response.isError());
        assertEquals("\tThat move spans multiple lines - please enter one command at a time.",
                response.message());
    }

    @Test
    public void getResponse_nullInput_promptsForCommand() throws MagnusException {
        MagnusResponse response = createMagnus().getResponseResult(null);

        assertTrue(response.isError());
        assertEquals("\tYour move - please enter a command.", response.message());
    }

    @Test
    public void getResponse_saveFailure_rollsBackInMemoryChange() throws MagnusException, IOException {
        Path dataFile = this.temporaryDirectory.resolve("blocked-data-path");
        Magnus magnus = new Magnus(dataFile);
        Files.createDirectory(dataFile);

        MagnusResponse saveResponse = magnus.getResponseResult("todo read book");
        String listResponse = magnus.getResponse("list");

        assertTrue(saveResponse.isError());
        assertTrue(saveResponse.message().contains("I could not save your tasks"));
        assertTrue(saveResponse.message().contains("move was rolled back"));
        assertEquals("\tThere are no tasks on the board yet.", listResponse);
    }

    /**
     * Creates a Magnus instance with an isolated task data file.
     *
     * @return A Magnus instance for the current test.
     * @throws MagnusException If the test data file cannot be loaded.
     */
    private Magnus createMagnus() throws MagnusException {
        return new Magnus(this.temporaryDirectory.resolve("magnus.txt"));
    }
}
