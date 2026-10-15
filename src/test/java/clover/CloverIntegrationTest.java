package clover;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import clover.command.CommandResponseStyle;
import clover.storage.Storage;
import clover.ui.Ui;

/** Tests chat-command integration and startup recovery using isolated data files. */
class CloverIntegrationTest {
    @TempDir
    Path tempDir;

    @Test
    void getResponse_commandCategories_returnsExpectedStyle() {
        Clover clover = clover();

        clover.getResponse("todo read book");
        assertEquals(CommandResponseStyle.TASK_ADDED, clover.getResponseStyle());
        clover.getResponse("mark 1");
        assertEquals(CommandResponseStyle.TASK_MARKED, clover.getResponseStyle());
        clover.getResponse("delete 1");
        assertEquals(CommandResponseStyle.TASK_DELETED, clover.getResponseStyle());
        clover.getResponse("add-tutoree Alice /address Home /fee 50");
        assertEquals(CommandResponseStyle.TUTOREE_ADDED, clover.getResponseStyle());
        clover.getResponse("list");
        assertEquals(CommandResponseStyle.STANDARD, clover.getResponseStyle());
    }

    @Test
    void getResponse_byeRequestsExitAndSubsequentErrorClearsExitRequest() {
        Clover clover = clover();

        assertTrue(clover.getResponse("bye").startsWith("The grove closes for now."));
        assertTrue(clover.isExitRequested());
        String errorResponse = clover.getResponse("unknown-command");
        assertTrue(errorResponse.startsWith("The forest path is unclear."));
        assertTrue(!errorResponse.contains("The grove closes for now."));
        assertTrue(!clover.isExitRequested());
    }

    @Test
    void close_calledTwice_doesNotThrow() {
        Clover clover = clover();

        clover.close();
        clover.close();
    }

    @Test
    void constructor_corruptTaskData_createsBackupAndStartsWithEmptyTasks() throws IOException {
        Path tasks = tempDir.resolve("tasks.txt");
        Files.writeString(tasks, "Q | 0 | invalid");

        Clover clover = clover();

        assertTrue(Files.exists(tempDir.resolve("tasks.txt.bak")));
        assertEquals("There are no study quests in the grove yet.", clover.getResponse("list"));
    }

    @Test
    void constructor_corruptTutoreeData_createsBackupAndStartsWithEmptyTutorees() throws IOException {
        Files.writeString(tempDir.resolve("tutorees.txt"), "Q | Alice | Home | 50");

        Clover clover = clover();

        assertTrue(Files.exists(tempDir.resolve("tutorees.txt.bak")));
        assertEquals("There are no learning companions in the grove yet.", clover.getResponse("list-tutorees"));
    }

    @Test
    void constructor_orphanedTaskLink_createsBackupAndRejectsAllLoadedTasks() throws IOException {
        Files.writeString(tempDir.resolve("tasks.txt"), "T | 0 | read book | Missing Learner");

        Clover clover = clover();

        assertTrue(Files.exists(tempDir.resolve("tasks.txt.bak")));
        assertEquals("There are no study quests in the grove yet.", clover.getResponse("list"));
    }

    private Clover clover() {
        return new Clover(new Storage(tempDir.resolve("tasks.txt"), tempDir.resolve("tutorees.txt")), new Ui());
    }
}
