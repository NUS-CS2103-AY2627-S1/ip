package jeff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class JeffTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void updateCommand_validUpdate_preservesStatusAndPersistsChange() {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Jeff jeff = new Jeff(filePath.toString());
        jeff.getResponse(
                "event project meeting /from 1400 /to 1600");
        jeff.getResponse("mark 1");

        String updateResponse =
                jeff.getResponse("update 1 /to 1800");

        assertTrue(
                updateResponse.contains(
                        "I've updated this task:"));
        assertTrue(updateResponse.contains("[E][X]"));
        assertTrue(
                updateResponse.contains(
                        "from: 1400 to: 1800"));

        Jeff reloadedJeff = new Jeff(filePath.toString());
        String listResponse =
                reloadedJeff.getResponse("list");

        assertTrue(listResponse.contains("[E][X]"));
        assertTrue(
                listResponse.contains(
                        "from: 1400 to: 1800"));
    }

    @Test
    public void getResponse_surroundingWhitespace_matchesUnpaddedCommands() {
        Jeff expectedJeff = new Jeff(
                temporaryDirectory.resolve("expected.txt").toString());
        Jeff actualJeff = new Jeff(
                temporaryDirectory.resolve("actual.txt").toString());

        String[] commands = {
            "todo read book",
            "deadline return book /by 2026-09-30",
            "event meeting /from 1400 /to 1600",
            "find book",
            "update 1 /description read novel",
            "mark 2",
            "unmark 2",
            "list",
            "delete 1",
            "list",
            "bye"
        };

        for (String command : commands) {
            String expectedResponse = expectedJeff.getResponse(command);
            String actualResponse = actualJeff.getResponse(
                    " \t " + command + " \t ");

            assertEquals(expectedResponse, actualResponse, command);
        }

        Jeff reloadedExpectedJeff = new Jeff(
                temporaryDirectory.resolve("expected.txt").toString());
        Jeff reloadedActualJeff = new Jeff(
                temporaryDirectory.resolve("actual.txt").toString());

        assertEquals(
                reloadedExpectedJeff.getResponse("list"),
                reloadedActualJeff.getResponse("list"));
    }

    @Test
    public void getResponse_whitespaceOnly_returnsInvalidCommand() {
        Jeff jeff = new Jeff(
                temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals(
                "Invalid command." + System.lineSeparator(),
                jeff.getResponse(" \t "));
    }

    @Test
    public void getResponse_surroundingWhitespace_preservesInternalSpaces() {
        Jeff jeff = new Jeff(
                temporaryDirectory.resolve("tasks.txt").toString());

        jeff.getResponse("  todo read  Java book  ");

        Jeff reloadedJeff = new Jeff(
                temporaryDirectory.resolve("tasks.txt").toString());

        assertTrue(
                reloadedJeff.getResponse("list")
                        .contains("read  Java book"));
    }

    @Test
    public void getResponse_pipeInTaskDetails_rejectsWithoutChangingTasks()
            throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Jeff jeff = new Jeff(filePath.toString());
        jeff.getResponse("todo original task");
        jeff.getResponse("event meeting /from 1400 /to 1600");

        String originalList = jeff.getResponse("list");
        String originalFile = Files.readString(filePath);

        String[] commands = {
            "todo buy milk | bread",
            "deadline submit | report /by 2026-09-30",
            "event team | meeting /from 1400 /to 1600",
            "event meeting /from 1400 | 1500 /to 1600",
            "event meeting /from 1400 /to 1600 | 1700",
            "update 1 /description changed | task",
            "update 2 /from 1300 | 1400",
            "update 2 /to 1700 | 1800"
        };

        for (String command : commands) {
            assertEquals(
                    "Task details cannot contain the | character."
                            + System.lineSeparator(),
                    jeff.getResponse(command),
                    command);
            assertEquals(originalList, jeff.getResponse("list"));
            assertEquals(originalFile, Files.readString(filePath));
        }
    }

    @Test
    public void getResponse_lineBreak_rejectsWithoutChangingTasks()
            throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Jeff jeff = new Jeff(filePath.toString());
        jeff.getResponse("todo original task");

        String originalList = jeff.getResponse("list");
        String originalFile = Files.readString(filePath);

        String[] commands = {
            "todo first\nsecond",
            "todo first\rsecond",
            "todo first\r\nsecond",
            "update 1 /description first\nsecond",
            "\ntodo another task",
            "todo another task\n"
        };

        for (String command : commands) {
            assertEquals(
                    "Please enter one command on a single line."
                            + System.lineSeparator(),
                    jeff.getResponse(command),
                    command);
            assertEquals(originalList, jeff.getResponse("list"));
            assertEquals(originalFile, Files.readString(filePath));
        }
    }

    @Test
    public void getResponse_loadFailure_blocksCommandsAndPreservesFile()
            throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        String originalContents = "T | 0 | valid task\n"
                + "T | invalid | damaged task\n";
        Files.writeString(filePath, originalContents);

        Jeff jeff = new Jeff(filePath.toString());

        String[] commands = {
            "todo new task",
            "deadline report /by 2026-09-30",
            "event meeting /from 1400 /to 1600",
            "mark 1",
            "unmark 1",
            "delete 1",
            "update 1 /description changed",
            "list",
            "find task"
        };

        for (String command : commands) {
            String response = jeff.getResponse(command);

            assertTrue(response.contains("Unable to load saved tasks."));
            assertTrue(response.contains("Invalid saved task at line 2."));
            assertTrue(response.contains("Task commands are disabled"));
            assertEquals(originalContents, Files.readString(filePath));
        }

        assertEquals(
                "Bye. Hope to see you again soon!" + System.lineSeparator(),
                jeff.getResponse("bye"));
    }

    @Test
    public void getResponse_saveFailure_reportsUnsavedChangesAndCanRecover()
            throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Jeff jeff = new Jeff(filePath.toString());

        // A directory at the file path makes writes fail after loading succeeds.
        Files.createDirectory(filePath);

        String expectedWarning =
                "Unable to save tasks. The change is kept in memory only."
                        + System.lineSeparator()
                        + "Check the data folder and file permissions."
                        + System.lineSeparator()
                        + "Keep Jeff open: unsaved changes will be lost when you exit."
                        + System.lineSeparator();

        String[] commands = {
            "todo read book",
            "deadline report /by 2026-09-30",
            "event meeting /from 1400 /to 1600",
            "mark 1",
            "unmark 1",
            "update 1 /description read novel",
            "delete 2"
        };

        for (String command : commands) {
            assertEquals(expectedWarning, jeff.getResponse(command), command);
        }

        String listResponse = jeff.getResponse("list");
        assertTrue(listResponse.contains("read novel"));
        assertTrue(listResponse.contains("meeting"));

        // Remove only the empty directory created by this test.
        Files.delete(filePath);

        String response = jeff.getResponse("mark 1");
        assertTrue(response.contains("Nice! I've marked this task as done:"));
        assertTrue(Files.isRegularFile(filePath));

        Jeff reloadedJeff = new Jeff(filePath.toString());
        assertEquals(
                jeff.getResponse("list"),
                reloadedJeff.getResponse("list"));
    }
}
