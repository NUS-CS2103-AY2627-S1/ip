package quackers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the command-handling seam that both front ends share.
 *
 * <p>These tests exercise {@link Quackers#getResponse(String)} rather than the individual
 * classes, so they cover parsing, task operations, storage, and message wording together.
 * Every test uses a temporary data file, so no test can see or corrupt real user data.
 */
class QuackersTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_todoCommand_addsTaskAndShowsItInListing() {
        Quackers quackers = createQuackers();

        assertEquals("Got it. I've added this task:\n  [T][ ] read book\n"
                + "Now you have 1 tasks in the list.", quackers.getResponse("todo read book"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book",
                quackers.getResponse("list"));
    }

    @Test
    void getResponse_everyTaskType_listsTasksInTheOrderAdded() {
        Quackers quackers = createQuackers();

        quackers.getResponse("todo read book");
        quackers.getResponse("deadline submit report /by 2027-02-28");
        quackers.getResponse("event project meeting /from 2pm /to 4pm");
        quackers.getResponse("note bring a laptop charger");

        assertEquals("Here are the tasks in your list:"
                + "\n1.[T][ ] read book"
                + "\n2.[D][ ] submit report (by: Feb 28 2027)"
                + "\n3.[E][ ] project meeting (from: 2pm to: 4pm)"
                + "\n4.[N] bring a laptop charger", quackers.getResponse("list"));
    }

    @Test
    void getResponse_unknownCommand_returnsErrorInsteadOfThrowing() {
        Quackers quackers = createQuackers();

        assertEquals("Quack? I don't know what that means :-(",
                quackers.getResponse("quack loudly"));
    }

    @Test
    void getResponse_commandWithInvalidArgument_leavesTaskListUnchanged() {
        Quackers quackers = createQuackers();
        quackers.getResponse("todo read book");

        assertEquals("Quack? Give me a todo description!", quackers.getResponse("todo"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book",
                quackers.getResponse("list"));
    }

    @Test
    void getResponse_byeCommand_requestsExit() {
        Quackers quackers = createQuackers();
        assertFalse(quackers.isExitRequested());

        assertEquals("Bye. Hope to see you again soon!", quackers.getResponse("bye"));

        assertTrue(quackers.isExitRequested());
    }

    @Test
    void getResponse_markThenUnmark_togglesTheDisplayedStatus() {
        Quackers quackers = createQuackers();
        quackers.getResponse("todo read book");

        assertEquals("Nice! I've marked this task as done:\n  [T][X] read book",
                quackers.getResponse("mark 1"));
        assertEquals("OK, I've marked this task as not done yet:\n  [T][ ] read book",
                quackers.getResponse("unmark 1"));
    }

    @Test
    void getResponse_markOnNote_returnsError() {
        Quackers quackers = createQuackers();
        quackers.getResponse("note bring a laptop charger");

        assertEquals("Quack? Notes do not have a completion status.",
                quackers.getResponse("mark 1"));
    }

    @Test
    void getResponse_deleteCommand_removesTaskAndReportsRemainingCount() {
        Quackers quackers = createQuackers();
        quackers.getResponse("todo read book");
        quackers.getResponse("todo return book");

        assertEquals("Noted. I've removed this task:\n  [T][ ] read book\n"
                + "Now you have 1 tasks in the list.", quackers.getResponse("delete 1"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] return book",
                quackers.getResponse("list"));
    }

    @Test
    void getResponse_taskNumberOutsideList_returnsError() {
        Quackers quackers = createQuackers();
        quackers.getResponse("todo read book");

        assertEquals("Quack? Please enter a task number from the list.",
                quackers.getResponse("delete 2"));
        assertEquals("Quack? Please enter a task number from the list.",
                quackers.getResponse("mark 0"));
    }

    @Test
    void getResponse_findCommand_returnsOnlyMatchingTasks() {
        Quackers quackers = createQuackers();
        quackers.getResponse("todo read book");
        quackers.getResponse("todo buy milk");

        assertEquals("Here are the matching tasks in your list:\n1.[T][ ] read book",
                quackers.getResponse("find book"));
    }

    @Test
    void getResponse_addedTasks_surviveIntoTheNextSession() {
        Path filePath = temporaryDirectory.resolve("data").resolve("tasks.txt");
        Quackers firstSession = new Quackers(filePath);
        firstSession.getResponse("todo read book");
        firstSession.getResponse("mark 1");

        // A fresh instance reads the same file, as a relaunch of the application would.
        Quackers secondSession = new Quackers(filePath);

        assertNull(secondSession.getLoadingError());
        assertEquals("Here are the tasks in your list:\n1.[T][X] read book",
                secondSession.getResponse("list"));
    }

    @Test
    void constructor_missingDataFile_startsEmptyWithoutError() {
        Quackers quackers = createQuackers();

        assertNull(quackers.getLoadingError());
        assertEquals("Here are the tasks in your list:", quackers.getResponse("list"));
    }

    @Test
    void constructor_corruptedDataFile_reportsErrorAndStaysUsable() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(filePath, "D\t1\tsubmit report\tnot-a-date", StandardCharsets.UTF_8);

        Quackers quackers = new Quackers(filePath);

        assertEquals("Quack! The saved task file contains invalid data.", quackers.getLoadingError());
        // The chatbot must still accept commands rather than refuse to start.
        assertEquals("Got it. I've added this task:\n  [T][ ] read book\n"
                + "Now you have 1 tasks in the list.", quackers.getResponse("todo read book"));
    }

    /**
     * Returns a chatbot whose data file lives in this test's temporary directory.
     */
    private Quackers createQuackers() {
        return new Quackers(temporaryDirectory.resolve("data").resolve("tasks.txt"));
    }
}
