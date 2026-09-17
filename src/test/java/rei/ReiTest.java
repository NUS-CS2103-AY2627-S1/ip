package rei;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the interface-independent command processing provided by Rei. */
public class ReiTest {
    @TempDir
    private Path testDirectory;

    @Test
    public void getResponse_addAndListTask_returnsExpectedMessages() {
        Rei rei = new Rei(testDirectory.resolve("tasks.txt"));

        Rei.CommandResult addResult = rei.getResponse("todo read book");
        Rei.CommandResult listResult = rei.getResponse("list");

        assertTrue(addResult.response().contains("One less thing to remember. I've added: [T][ ] read book"));
        assertTrue(listResult.response().contains("1.[T][ ] read book"));
        assertFalse(addResult.shouldExit());
    }

    @Test
    public void getResponse_byeCommand_requestsExit() {
        Rei rei = new Rei(testDirectory.resolve("tasks.txt"));

        Rei.CommandResult result = rei.getResponse("bye");

        assertTrue(result.response().contains("See you soon. Make room for a little rest, too."));
        assertTrue(result.shouldExit());
    }

    @Test
    public void getResponse_findKeyword_preservesOriginalIndexesAndOrder() {
        Rei rei = new Rei(testDirectory.resolve("tasks.txt"));
        rei.getResponse("todo unrelated");
        rei.getResponse("todo Read book");
        rei.getResponse("todo buy milk");
        rei.getResponse("todo book tickets");

        String matches = rei.getResponse("find BOOK").response();

        assertTrue(matches.contains("2.[T][ ] Read book"));
        assertTrue(matches.contains("4.[T][ ] book tickets"));
        assertTrue(matches.indexOf("Read book") < matches.indexOf("book tickets"));
        assertFalse(matches.contains("unrelated"));
        assertFalse(matches.contains("buy milk"));
    }

    @Test
    public void getResponse_findMissingKeyword_reportsNoMatches() {
        Rei rei = new Rei(testDirectory.resolve("tasks.txt"));
        String emptyResult = rei.getResponse("find book").response();
        rei.getResponse("todo buy milk");

        assertTrue(emptyResult.contains("No matching tasks"));
        assertTrue(rei.getResponse("find book").response().contains("No matching tasks"));
    }

    @Test
    public void getResponse_tagAndFind_persistsNormalizedTagAndRejectsDuplicates() {
        Path dataFile = testDirectory.resolve("tasks.txt");
        Rei rei = new Rei(dataFile);
        rei.getResponse("todo prepare slides");

        String tagResponse = rei.getResponse("tag 1 #School").response();
        String duplicateResponse = rei.getResponse("tag 1 #SCHOOL").response();
        String invalidResponse = rei.getResponse("tag 1 school").response();
        String savedMatches = new Rei(dataFile).getResponse("find #school").response();

        assertTrue(tagResponse.contains("Added #school to task 1"));
        assertTrue(tagResponse.contains("prepare slides #school"));
        assertTrue(duplicateResponse.contains("already has tag #school"));
        assertTrue(invalidResponse.contains("A tag must start with #"));
        assertTrue(savedMatches.contains("1.[T][ ] prepare slides #school"));
    }

    @Test
    public void getResponse_taskCommands_persistChangesAcrossInstances() {
        Path dataFile = testDirectory.resolve("tasks.txt");
        Rei rei = new Rei(dataFile);
        rei.getResponse("todo alpha");
        rei.getResponse("deadline beta /by 2026-09-10 1800");
        rei.getResponse("event gamma /from 2026-09-10 1800 /to 2026-09-10 1900");
        rei.getResponse("mark 1");
        assertTrue(new Rei(dataFile).getResponse("list").response().contains("1.[T][X] alpha"));

        rei.getResponse("unmark 1");
        rei.getResponse("delete 2");
        String savedTasks = new Rei(dataFile).getResponse("list").response();
        assertTrue(savedTasks.contains("1.[T][ ] alpha"));
        assertTrue(savedTasks.contains("2.[E][ ] gamma"));
        assertFalse(savedTasks.contains("beta"));
    }

    @Test
    public void getResponse_invalidByeDetails_doesNotRequestExit() {
        Rei rei = new Rei(testDirectory.resolve("tasks.txt"));

        Rei.CommandResult result = rei.getResponse("bye now");

        assertFalse(result.shouldExit());
        assertTrue(result.response().contains("does not take any extra text"));
    }

    @Test
    public void getResponse_markUnmarkAndDeleteBoundaryTasks_updatesCorrectTasks() {
        Rei rei = new Rei(testDirectory.resolve("tasks.txt"));
        rei.getResponse("  todo alpha  ");
        rei.getResponse("todo beta");

        rei.getResponse("mark 1");
        rei.getResponse("mark 2");
        rei.getResponse("unmark 1");
        String tasks = rei.getResponse("list").response();
        assertTrue(tasks.contains("1.[T][ ] alpha"));
        assertTrue(tasks.contains("2.[T][X] beta"));

        rei.getResponse("delete 2");
        assertFalse(rei.getResponse("list").response().contains("beta"));
        rei.getResponse("delete 1");
        assertFalse(rei.getResponse("list").response().contains("alpha"));
    }

    @Test
    public void getResponse_outOfBoundsTaskNumbers_returnsErrors() {
        Rei rei = new Rei(testDirectory.resolve("tasks.txt"));
        rei.getResponse("todo alpha");

        assertTrue(rei.getResponse("mark 0").response().contains("at least 1"));
        assertTrue(rei.getResponse("unmark 2").response().contains("does not exist"));
        assertTrue(rei.getResponse("delete 2").response().contains("does not exist"));
        assertTrue(rei.getResponse("list").response().contains("1.[T][ ] alpha"));
    }

    @Test
    public void getResponse_invalidCommand_preservesExistingTasks() {
        Rei rei = new Rei(testDirectory.resolve("tasks.txt"));
        rei.getResponse("todo alpha");

        Rei.CommandResult invalidResult = rei.getResponse("delete first");
        Rei.CommandResult listResult = rei.getResponse("list");

        assertTrue(invalidResult.response().contains("task number must be a whole number"));
        assertTrue(listResult.response().contains("1.[T][ ] alpha"));
    }
}
