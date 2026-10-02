package clsl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests end-to-end command responses for the application. */
public class ClslTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_addingDuplicateTask_returnsAlreadyAddedError() {
        Clsl clsl = new Clsl(temporaryDirectory.resolve("tasks.txt").toString());
        clsl.getResponse("todo Buy milk");

        assertEquals("\nERROR: already added\n", clsl.getResponse("todo buy   milk"));
        assertEquals("\nHere are your tasks in your list:\n1.[T][ ] Buy milk\n",
                clsl.getResponse("list"));
    }

    @Test
    void getResponse_usingTaskNumberBeyondList_returnsRangeError() {
        Clsl clsl = new Clsl(temporaryDirectory.resolve("tasks.txt").toString());
        clsl.getResponse("todo Buy milk");

        assertEquals("\nERROR: task number must be at least 1\n", clsl.getResponse("mark 0"));
        assertEquals("\nERROR: task number must be at least 1\n", clsl.getResponse("mark -1"));
        assertEquals("\nERROR: task number is out of range\n", clsl.getResponse("mark 2"));
        assertEquals("\nERROR: task number is out of range\n", clsl.getResponse("unmark 2"));
        assertEquals("\nERROR: task number is out of range\n", clsl.getResponse("delete 2"));
        assertEquals("\nHere are your tasks in your list:\n1.[T][ ] Buy milk\n",
                clsl.getResponse("list"));
    }

    @Test
    void constructor_duplicateTasksKeepsFirstAndRewritesStorage() throws Exception {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(taskFile, List.of(
                "T | 0 | Buy milk",
                "T | 1 | buy   milk",
                "T | 0 | Read book"));

        Clsl clsl = new Clsl(taskFile.toString());

        assertEquals("\nHere are your tasks in your list:\n1.[T][ ] Buy milk\n"
                + "2.[T][ ] Read book\n", clsl.getResponse("list"));
        assertEquals(List.of("T | 0 | Buy milk", "T | 0 | Read book"),
                Files.readAllLines(taskFile));
    }

    @Test
    void getResponse_successfulCommandsReturnExpectedMessages() {
        Clsl clsl = new Clsl(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("\nHere are your tasks in your list:\n", clsl.getResponse("list"));
        assertTrue(clsl.getResponse("todo read book").contains("I've added this task"));
        assertTrue(clsl.getResponse("deadline submit report /by 2026-09-02")
                .contains("I've added this task"));
        assertTrue(clsl.getResponse("event conference /from 2026-09-01 /to 2026-09-03")
                .contains("I've added this task"));
        assertTrue(clsl.getResponse("mark 1").contains("marked this task as Done"));
        assertTrue(clsl.getResponse("unmark 1").contains("marked this task as not done"));
        assertTrue(clsl.getResponse("find book").contains("matching tasks"));
        assertTrue(clsl.getResponse("on 2026-09-02").contains("tasks occurring on"));
        assertTrue(clsl.getResponse("delete 1").contains("removed this task"));
        assertEquals("\nHere are your tasks in your list:\n1.[D][ ] submit report (by: Sep 2 2026)\n"
                + "2.[E][ ] conference (from: Sep 1 2026 to: Sep 3 2026)\n",
                clsl.getResponse("list"));
    }

    @Test
    void getResponse_blankAndUnknownCommandsReturnPrefixedErrors() {
        Clsl clsl = new Clsl(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("\nERROR: command cannot be blank\n", clsl.getResponse("   "));
        assertEquals("\nERROR: I don't understand\n", clsl.getResponse("unknown"));
    }

    @Test
    void constructor_invalidTaskDataStartsWithAnEmptyList() throws Exception {
        Path taskFile = temporaryDirectory.resolve("invalid.txt");
        Files.writeString(taskFile, "X | 0 | invalid task\n");
        Clsl clsl = new Clsl(taskFile.toString());

        assertEquals("\nHere are your tasks in your list:\n", clsl.getResponse("list"));
    }

    @Test
    void getResponse_reportsStorageFailureWhenTaskCannotBeSaved() throws Exception {
        Path directoryPath = temporaryDirectory.resolve("task-directory");
        Files.createDirectory(directoryPath);
        Clsl clsl = new Clsl(directoryPath.toString());

        assertEquals("\nERROR: Unable to save task data. Please try again.\n",
                clsl.getResponse("todo read book"));
    }

    @Test
    void getResponse_byeReturnsGoodbyeMessage() {
        Clsl clsl = new Clsl(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("\nBye. Hope to see you again soon!", clsl.getResponse("bye"));
    }
}
