package bos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests command execution through the interface used by the GUI.
 */
public class BosTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_addThenList_addedTaskDisplayed() {
        Bos bos = new Bos(temporaryDirectory.resolve("tasks.txt").toString());

        String addResponse = bos.getResponse("todo read book");
        String listResponse = bos.getResponse("list");

        assertTrue(addResponse.contains("[T][ ] read book"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book", listResponse);
    }

    @Test
    public void getResponse_commandHasExtraWhitespace_commandHandledAndTaskNormalized() {
        Bos bos = new Bos(temporaryDirectory.resolve("tasks.txt").toString());

        String addResponse = bos.getResponse("   todo    read   book    ");
        String listResponse = bos.getResponse("   list   ");

        assertTrue(addResponse.contains("[T][ ] read book"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book", listResponse);
    }

    @Test
    public void getResponse_addSameDescriptionWithDifferentTypeAndDuration_duplicatesRejected() {
        Bos bos = new Bos(temporaryDirectory.resolve("tasks.txt").toString());
        bos.getResponse("event meeting /from 2026-09-11 1000 /to 2026-09-11 1100");

        String differentTypeResponse = bos.getResponse("todo meeting");
        String differentDurationResponse = bos.getResponse(
                "event meeting /from 2026-09-12 1400 /to 2026-09-12 1600");
        String listResponse = bos.getResponse("list");

        assertEquals("OOPS!!! This task already exists in the task list.", differentTypeResponse);
        assertEquals("OOPS!!! This task already exists in the task list.", differentDurationResponse);
        assertEquals(
                "Here are the tasks in your list:\n"
                        + "1.[E][ ] meeting (from: Sep 11 2026, 10:00 AM to: Sep 11 2026, 11:00 AM)",
                listResponse);
    }

    @Test
    public void constructor_savedTaskExists_taskLoaded() {
        String filePath = temporaryDirectory.resolve("tasks.txt").toString();
        Bos firstBos = new Bos(filePath);
        firstBos.getResponse("deadline submit report /by 2026-09-18 1800");

        Bos reloadedBos = new Bos(filePath);

        assertTrue(reloadedBos.getResponse("list").contains("submit report"));
    }

    @Test
    public void getResponse_unknownCommand_errorResponseReturned() {
        Bos bos = new Bos(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals(
                "OOPS!!! I'm sorry, but I don't know what that means :-(",
                bos.getResponse("sing"));
    }

    @Test
    public void getResponse_blankOrNullCommand_specificErrorReturned() {
        Bos bos = new Bos(temporaryDirectory.resolve("tasks.txt").toString());
        String expectedResponse = "OOPS!!! Please enter a command.";

        assertEquals(expectedResponse, bos.getResponse("   "));
        assertEquals(expectedResponse, bos.getResponse(null));
    }

    @Test
    public void getResponse_noArgumentCommandHasArguments_formatErrorReturned() {
        Bos bos = new Bos(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals(
                "OOPS!!! Please use this command format: list",
                bos.getResponse("list now"));
        assertEquals(
                "OOPS!!! Please use this command format: bye",
                bos.getResponse("bye now"));
    }

    @Test
    public void getResponse_invalidDatesAndRanges_errorsReturned() {
        Bos bos = new Bos(temporaryDirectory.resolve("tasks.txt").toString());

        String invalidDateResponse = bos.getResponse(
                "deadline submit report /by 2026-02-30 1200");
        String reversedEventResponse = bos.getResponse(
                "event meeting /from 2026-09-11 1100 /to 2026-09-11 1000");
        String sameTimeEventResponse = bos.getResponse(
                "event meeting /from 2026-09-11 1000 /to 2026-09-11 1000");

        assertTrue(invalidDateResponse.contains("must be a real date and time"));
        assertEquals("OOPS!!! The event end must be later than its start.", reversedEventResponse);
        assertEquals("OOPS!!! The event end must be later than its start.", sameTimeEventResponse);
    }

    @Test
    public void getResponse_parameterRepeated_specificErrorReturned() {
        Bos bos = new Bos(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals(
                "OOPS!!! The /by parameter may only be specified once.",
                bos.getResponse("deadline report /by Friday /by Saturday"));
        assertEquals(
                "OOPS!!! The /from parameter may only be specified once.",
                bos.getResponse("event meeting /from Monday /from Tuesday /to Wednesday"));
    }

    @Test
    public void getResponse_duplicateDiffersByCaseAndSpacing_duplicateRejected() {
        Bos bos = new Bos(temporaryDirectory.resolve("tasks.txt").toString());
        bos.getResponse("todo Read   Book");

        String duplicateResponse = bos.getResponse("deadline read book /by 2026-09-18 1800");

        assertEquals("OOPS!!! This task already exists in the task list.", duplicateResponse);
    }

    @Test
    public void constructor_corruptedTaskFile_fileProtectedFromOverwrite() throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        String corruptedContent = "D | 0 | report | 2026-02-30 1200\n";
        Files.writeString(taskFile, corruptedContent);

        Bos bos = new Bos(taskFile.toString());
        String addResponse = bos.getResponse("todo read book");

        assertTrue(bos.getGreeting().contains("task file is corrupted"));
        assertTrue(addResponse.contains("task file was not loaded safely"));
        assertEquals(corruptedContent, Files.readString(taskFile));
    }
}
