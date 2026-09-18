package geek;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import geek.task.Task;

/**
 * Tests the shared command-response interface used by the JavaFX UI.
 */
class GeekTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void getResponse_commandSequence_updatesSameTaskList() {
        Geek geek = new Geek(
                tempDirectory.resolve("geek.txt").toString()
        );

        String addResponse = geek.getResponse("todo read book");
        String markResponse = geek.getResponse("mark 1");
        String listResponse = geek.getResponse("list");

        assertTrue(addResponse.contains("Task logged."));
        assertTrue(markResponse.contains("Mission complete"));
        assertTrue(listResponse.contains("[T][X] read book"));
    }

    @Test
    void getResponse_newInstance_loadsPreviouslySavedTasks() {
        Path filePath = tempDirectory.resolve("geek.txt");
        Geek firstSession = new Geek(filePath.toString());
        firstSession.getResponse(
                "deadline submit report /by 2/12/2019 1800"
        );

        Geek secondSession = new Geek(filePath.toString());
        String listResponse = secondSession.getResponse("list");

        assertTrue(
                listResponse.contains(
                        "[D][ ] submit report "
                                + "(by: Dec 2 2019, 6:00 PM)"
                )
        );
    }

    @Test
    void getResponse_invalidAndByeCommands_returnFriendlyMessages() {
        Geek geek = new Geek(
                tempDirectory.resolve("geek.txt").toString()
        );

        assertEquals(
                "OOPS!!! Please enter a command.",
                geek.getResponse("   ")
        );
        assertEquals(
                "Mission paused. Powering down for now—"
                        + "see you next time!",
                geek.getResponse("bye")
        );
    }

    @Test
    void getResponse_sortCommand_sortsAndPersistsTaskOrder() {
        Path filePath = tempDirectory.resolve("geek.txt");
        Geek firstSession = new Geek(filePath.toString());
        firstSession.getResponse("todo read book");
        firstSession.getResponse(
                "deadline submit report /by 3/12/2019"
        );
        firstSession.getResponse(
                "event meeting /from 2/12/2019 0900 "
                        + "/to 2/12/2019 1000"
        );

        String sortResponse = firstSession.getResponse("sort");
        Geek secondSession = new Geek(filePath.toString());
        String reloadedList = secondSession.getResponse("list");

        assertAllTasksAreChronological(sortResponse);
        assertAllTasksAreChronological(reloadedList);
    }

    @Test
    void getResponse_corruptedSavedTask_warnsOnceAndLoadsValidTasks()
            throws IOException {
        Path filePath = tempDirectory.resolve("geek.txt");
        Task validTask = Task.newTodo("read book");
        Files.write(
                filePath,
                List.of(
                        validTask.toDataString(),
                        "not a valid saved task"
                ),
                StandardCharsets.UTF_8
        );
        Geek geek = new Geek(filePath.toString());

        String firstResponse = geek.getResponse("list");
        String secondResponse = geek.getResponse("list");

        assertAll(() -> assertTrue(
                        firstResponse.startsWith(
                                "Warning: Skipped corrupted "
                                        + "saved tasks on lines 2."
                        )
                ), () -> assertTrue(
                        firstResponse.contains("[T][ ] read book")
                ), () -> assertFalse(
                        secondResponse.contains("Warning:")
                ), () -> assertTrue(
                        secondResponse.contains("[T][ ] read book")
                )
        );
    }

    @Test
    void getResponse_parentPathIsFile_reportsLoadErrorAndContinues()
            throws IOException {
        Path blockingParent = tempDirectory.resolve("not-a-folder");
        Files.writeString(
                blockingParent,
                "This file prevents a directory from being created.",
                StandardCharsets.UTF_8
        );
        Geek geek = new Geek(
                blockingParent.resolve("geek.txt").toString()
        );

        String response = geek.getResponse("list");

        assertEquals(
                "OOPS!!! I could not load the saved tasks.\n\n"
                        + "Your mission board is clear.",
                response
        );
    }

    @Test
    void getResponse_dataFileBecomesDirectory_reportsSaveError()
            throws IOException {
        Path filePath = tempDirectory.resolve("geek.txt");
        Geek geek = new Geek(filePath.toString());
        geek.getResponse("list");
        Files.delete(filePath);
        Files.createDirectory(filePath);

        String addResponse = geek.getResponse("todo read book");
        String listResponse = geek.getResponse("list");

        assertAll(() -> assertTrue(
                        addResponse.contains(
                                "Task logged."
                        )
                ), () -> assertTrue(
                        addResponse.contains(
                                "OOPS!!! I could not save the tasks."
                        )
                ), () -> assertTrue(
                        listResponse.contains("[T][ ] read book")
                )
        );
    }

    @Test
    void getResponse_outOfRangeTaskNumbers_returnFriendlyErrors() {
        Geek geek = new Geek(
                tempDirectory.resolve("geek.txt").toString()
        );
        String expectedMessage =
                "OOPS!!! That task number does not exist.";

        String markResponse = geek.getResponse("mark 1");
        geek.getResponse("todo read book");
        String unmarkResponse = geek.getResponse("unmark 0");
        String deleteResponse = geek.getResponse("delete 2");

        assertAll(() -> assertEquals(
                        expectedMessage,
                        markResponse
                ), () -> assertEquals(
                        expectedMessage,
                        unmarkResponse
                ), () -> assertEquals(
                        expectedMessage,
                        deleteResponse
                )
        );
    }

    @Test
    void getResponse_invalidCalendarDate_returnsFormatGuidance() {
        Geek geek = new Geek(
                tempDirectory.resolve("geek.txt").toString()
        );

        String response = geek.getResponse(
                "deadline submit report /by 30/2/2026"
        );

        assertEquals(
                "OOPS!!! Use a supported date or time format, "
                        + "such as 2019-12-02, 2/12/2019 1800, or "
                        + "Dec 2 2019 6:00 PM.",
                response
        );
    }

    private static void assertAllTasksAreChronological(String response) {
        int eventIndex = response.indexOf("[E][ ] meeting");
        int deadlineIndex = response.indexOf("[D][ ] submit report");
        int todoIndex = response.indexOf("[T][ ] read book");

        assertTrue(eventIndex >= 0);
        assertTrue(eventIndex < deadlineIndex);
        assertTrue(deadlineIndex < todoIndex);
    }
}
