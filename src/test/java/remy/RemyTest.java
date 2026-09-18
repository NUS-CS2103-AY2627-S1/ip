package remy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests command processing through Remy's GUI-facing response interface.
 */
class RemyTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_taskCommands_commandsExecuteAgainstSameTaskList() {
        Remy remy = createRemy();

        String addResponse = remy.getResponse("todo prepare ingredients");
        String listResponse = remy.getResponse("list");
        String markResponse = remy.getResponse("mark 1");

        assertTrue(addResponse.contains("prepare ingredients"));
        assertTrue(listResponse.contains("1. [T][ ] prepare ingredients"));
        assertTrue(markResponse.contains("[T][X] prepare ingredients"));
    }

    @Test
    void getResponse_invalidAndBlankCommands_errorMessagesReturned() {
        Remy remy = createRemy();

        String unknownCommandResponse = remy.getResponse("cook dinner");
        String blankCommandResponse = remy.getResponse("   ");
        String nullCommandResponse = remy.getResponse(null);

        assertTrue(unknownCommandResponse.contains("no clue what you're talking about"));
        assertTrue(blankCommandResponse.contains("Please type a command"));
        assertTrue(nullCommandResponse.contains("Please type a command"));
    }

    @Test
    void main_unknownCommandThenBye_errorHandledAndCommandLoopEnds() {
        InputStream originalStandardIn = System.in;
        PrintStream originalStandardOut = System.out;
        ByteArrayInputStream input = new ByteArrayInputStream("cook dinner\nbye\n"
                .getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setIn(input);
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

            Remy.main(new String[0]);
        } finally {
            System.setIn(originalStandardIn);
            System.setOut(originalStandardOut);
        }

        String response = output.toString(StandardCharsets.UTF_8);
        assertTrue(response.contains("Yo! I am Remy the rat from Ratatouille."));
        assertTrue(response.contains("no clue what you're talking about"));
        assertTrue(response.contains("Cya. Call me again when you need me!"));
    }

    @Test
    void getResponse_listCommandWithNoTasks_emptyListMessageReturned() {
        Remy remy = createRemy();

        String response = remy.getResponse("list");

        assertTrue(response.contains("There are no tasks in your list yet."));
        assertFalse(remy.getGreeting().contains("Skipped invalid saved task data"));
    }

    @Test
    void getResponse_invalidTaskIndices_errorsReturnedAndTasksRetained() {
        Remy remy = createRemy();

        String emptyDeleteResponse = remy.getResponse("delete 1");
        remy.getResponse("todo prepare ingredients");
        String zeroMarkResponse = remy.getResponse("mark 0");
        String largeMarkResponse = remy.getResponse("mark 2");
        String zeroDeleteResponse = remy.getResponse("delete 0");
        String largeDeleteResponse = remy.getResponse("delete 2");
        String listResponse = remy.getResponse("list");

        assertTrue(emptyDeleteResponse.contains("no task for you to delete"));
        assertTrue(zeroMarkResponse.contains("index is out of range"));
        assertTrue(largeMarkResponse.contains("index is out of range"));
        assertTrue(zeroDeleteResponse.contains("index is out of range"));
        assertTrue(largeDeleteResponse.contains("index is out of range"));
        assertTrue(listResponse.contains("1. [T][ ] prepare ingredients"));
    }

    @Test
    void getResponse_sortCommand_tasksReorderedAndExistingListOutputReturned() {
        Remy remy = createRemy();
        remy.getResponse("todo buy ingredients");
        remy.getResponse("deadline submit report /by 23/8/2026 1800");
        remy.getResponse("event workshop /from 25/8/2026 0900 /to 25/8/2026 1100");
        remy.getResponse("deadline pay fees /by 23/8/2026");
        remy.getResponse("event tutorial /from 23/8/2026 1400 /to 23/8/2026 1500");

        String response = remy.getResponse("sort /by date");

        String expectedResponse = """
                Here are the tasks in your list:
                1. [D][ ] pay fees (by: Aug 23 2026)
                2. [E][ ] tutorial (from: Aug 23 2026 14:00 to: Aug 23 2026 15:00)
                3. [D][ ] submit report (by: Aug 23 2026 18:00)
                4. [E][ ] workshop (from: Aug 25 2026 09:00 to: Aug 25 2026 11:00)
                5. [T][ ] buy ingredients
                """.strip();
        assertEquals(expectedResponse, response.replace(System.lineSeparator(), "\n"));
    }

    @Test
    void getResponse_sortThenReload_sortedIndicesAndPersistencePreserved() {
        Path taskFile = temporaryDirectory.resolve("persistent-tasks.txt");
        Remy remy = new Remy(taskFile.toString());
        remy.getResponse("deadline later task /by 25/9/2026");
        remy.getResponse("deadline earlier task /by 23/9/2026");
        remy.getResponse("sort /by date");

        String markResponse = remy.getResponse("mark 1");
        Remy reloadedRemy = new Remy(taskFile.toString());
        String reloadedList = reloadedRemy.getResponse("list");

        assertTrue(markResponse.contains("[D][X] earlier task"));
        assertTrue(reloadedList.indexOf("[D][X] earlier task")
                < reloadedList.indexOf("[D][ ] later task"));
    }

    @Test
    void getResponse_addAfterSorting_newTaskAppendedWithoutAutomaticResort() {
        Remy remy = createRemy();
        remy.getResponse("deadline later task /by 25/9/2026");
        remy.getResponse("deadline earlier task /by 23/9/2026");
        remy.getResponse("sort /by date");

        remy.getResponse("deadline newly added task /by 20/9/2026");
        String response = remy.getResponse("list");

        assertTrue(response.indexOf("earlier task") < response.indexOf("later task"));
        assertTrue(response.indexOf("later task") < response.indexOf("newly added task"));
    }

    @Test
    void getResponse_invalidSortCommand_errorReturnedAndOrderUnchanged() {
        Remy remy = createRemy();
        remy.getResponse("deadline later task /by 25/9/2026");
        remy.getResponse("deadline earlier task /by 23/9/2026");

        String errorResponse = remy.getResponse("sort /by name");
        String listResponse = remy.getResponse("list");

        String expectedError = """
                Invalid sort command.
                Use: sort /by date [/order asc|desc]
                Example: sort /by date /order asc
                """.strip();
        assertEquals(expectedError, errorResponse);
        assertTrue(listResponse.indexOf("later task") < listResponse.indexOf("earlier task"));
    }

    @Test
    void getResponse_findAfterSorting_matchesFollowCanonicalRelativeOrder() {
        Remy remy = createRemy();
        remy.getResponse("todo draft report notes");
        remy.getResponse("deadline submit report later /by 25/9/2026");
        remy.getResponse("deadline submit report earlier /by 23/9/2026");
        remy.getResponse("sort /by date");

        String response = remy.getResponse("find report");

        assertTrue(response.indexOf("submit report earlier") < response.indexOf("submit report later"));
        assertTrue(response.indexOf("submit report later") < response.indexOf("draft report notes"));
    }

    @Test
    void getResponse_sortEmptyTaskList_emptyResponseAndTaskFileCreated() throws IOException {
        Path taskFile = temporaryDirectory.resolve("empty-tasks.txt");
        Remy remy = new Remy(taskFile.toString());

        String response = remy.getResponse("sort /by date");

        assertEquals("There are no tasks in your list yet.", response);
        assertTrue(Files.exists(taskFile));
        assertTrue(Files.readString(taskFile).isEmpty());
    }

    @Test
    void getResponse_byeCommand_sessionEndsAndFurtherCommandsAreRejected() {
        Remy remy = createRemy();

        String byeResponse = remy.getResponse("bye");
        String laterResponse = remy.getResponse("list");

        assertTrue(remy.hasExited());
        assertEquals("Cya. Call me again when you need me!", byeResponse);
        assertEquals("This chat has ended. Restart Remy to begin a new session.", laterResponse);
    }

    @Test
    void getResponse_invalidByeCommands_sessionRemainsActive() {
        Remy remy = createRemy();

        String argumentResponse = remy.getResponse("bye now");
        String capitalizationResponse = remy.getResponse("Bye");
        String spacingResponse = remy.getResponse(" bye");
        String laterResponse = remy.getResponse("list");

        assertEquals("The bye command does not accept parameters.\nUse: bye", argumentResponse);
        assertEquals("Wha-? My bad bruh, I have no clue what you're talking about :/", capitalizationResponse);
        assertEquals("Invalid command spacing.\n"
                + "Use one space between command parts and remove leading or trailing spaces.", spacingResponse);
        assertFalse(remy.hasExited());
        assertEquals("There are no tasks in your list yet.", laterResponse);
    }

    @Test
    void constructor_taskFileContainsInvalidData_validTasksLoadedAndGreetingWarnsUser() throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, String.join(System.lineSeparator(),
                "[T][ ] prepare ingredients",
                "unexpected content",
                "[T][X] wash dishes"));

        Remy remy = new Remy(taskFile.toString());

        assertTrue(remy.getGreeting().contains("line(s) 2"));
        assertTrue(remy.getGreeting().contains(taskFile.toString()));
        assertTrue(remy.getResponse("list").contains("prepare ingredients"));
        assertTrue(remy.getResponse("list").contains("wash dishes"));
    }

    @Test
    void constructor_taskPathIsDirectory_emptyTaskListAndActionableGreetingReturned() throws IOException {
        Path taskPath = temporaryDirectory.resolve("tasks");
        Files.createDirectory(taskPath);

        Remy remy = new Remy(taskPath.toString());

        assertTrue(remy.getGreeting().contains("is a directory, not a file"));
        assertTrue(remy.getGreeting().contains("started with an empty task list"));
        assertTrue(remy.getResponse("list").contains("There are no tasks"));
    }

    @Test
    void getResponse_taskFileCannotBeSaved_warningReturnedAndSessionChangeRetained() throws IOException {
        Path parentPath = temporaryDirectory.resolve("not-a-folder");
        Files.writeString(parentPath, "blocking file");
        Path taskFile = parentPath.resolve("tasks.txt");
        Remy remy = new Remy(taskFile.toString());

        String response = remy.getResponse("todo prepare ingredients");
        String listResponse = remy.getResponse("list");

        assertTrue(response.contains("Unable to save tasks to \"" + taskFile + "\""));
        assertTrue(response.contains("kept only for this session"));
        assertTrue(response.contains("prepare ingredients"));
        assertTrue(listResponse.contains("prepare ingredients"));
    }

    @Test
    void getResponse_markCannotBeSaved_warningShownBeforeConfirmationAndChangeRetained() throws IOException {
        Path parentPath = Files.createDirectory(temporaryDirectory.resolve("data"));
        Path taskFile = parentPath.resolve("tasks.txt");
        Remy remy = new Remy(taskFile.toString());
        remy.getResponse("todo prepare ingredients");
        Files.delete(taskFile);
        Files.delete(parentPath);
        Files.writeString(parentPath, "blocking file");

        String response = remy.getResponse("mark 1");
        String listResponse = remy.getResponse("list");

        assertTrue(response.indexOf("Unable to save tasks") < response.indexOf("marked this task as done"));
        assertTrue(response.contains("kept only for this session"));
        assertTrue(listResponse.contains("[T][X] prepare ingredients"));
    }

    @Test
    void getResponse_duplicateTask_errorReturnedAndDuplicateNotSaved() throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Remy remy = new Remy(taskFile.toString());
        remy.getResponse("deadline submit report /by 24/9/2026");

        String duplicateResponse = remy.getResponse("deadline submit report /by 24/9/2026");

        assertTrue(duplicateResponse.contains("already exists"));
        assertEquals(1, Files.readAllLines(taskFile).size());
    }

    @Test
    void getResponse_invalidDatesAndEventRanges_errorsReturnedAndTasksNotAdded() {
        Remy remy = createRemy();

        String deadlineResponse = remy.getResponse("deadline submit report /by 30/2/2026");
        String nonExistentEventResponse = remy.getResponse(
                "event conference /from 29/2/2025 /to 1/3/2025");
        String equalRangeResponse = remy.getResponse(
                "event conference /from 24/9/2026 1500 /to 24/9/2026 1500");
        String reversedRangeResponse = remy.getResponse(
                "event conference /from 24/9/2026 1600 /to 24/9/2026 1500");
        String listResponse = remy.getResponse("list");

        assertTrue(deadlineResponse.contains("deadline date is invalid"));
        assertTrue(nonExistentEventResponse.contains("event dates are invalid"));
        assertTrue(equalRangeResponse.contains("must start before it ends"));
        assertTrue(reversedRangeResponse.contains("must start before it ends"));
        assertTrue(listResponse.contains("There are no tasks"));
        assertFalse(Files.exists(temporaryDirectory.resolve("tasks.txt")));
    }

    @Test
    void getResponse_commandFormatErrors_actionableErrorsReturnedAndTasksNotAdded() {
        Remy remy = createRemy();

        String leadingSpaceResponse = remy.getResponse(" todo prepare ingredients");
        String repeatedSpaceResponse = remy.getResponse("todo  prepare ingredients");
        String repeatedParameterResponse = remy.getResponse(
                "deadline submit report /by 24/9/2026 /by 25/9/2026");
        String specialIndexResponse = remy.getResponse("mark #1");
        String listResponse = remy.getResponse("list");

        assertTrue(leadingSpaceResponse.contains("Invalid command spacing"));
        assertTrue(repeatedSpaceResponse.contains("Invalid command spacing"));
        assertTrue(repeatedParameterResponse.contains("Specify /by exactly once"));
        assertTrue(specialIndexResponse.contains("integer"));
        assertTrue(listResponse.contains("There are no tasks"));
    }

    /** Returns a Remy instance with isolated test storage. */
    private Remy createRemy() {
        return new Remy(temporaryDirectory.resolve("tasks.txt").toString());
    }
}
