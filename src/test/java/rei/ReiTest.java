package rei;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the interface-independent command processing provided by Rei. */
public class ReiTest {
    @TempDir
    private Path testDirectory;

    @Test
    public void getResponse_invalidCommands_neverChangeSavedTasks() throws IOException {
        Path dataFile = testDirectory.resolve("validation.txt");
        Rei rei = new Rei(dataFile);
        rei.getResponse("todo original");
        String saved = Files.readString(dataFile);
        String listed = rei.getResponse("list").response();

        for (String command : List.of("", "unknown", "todo", "deadline", "event", "find", "tag",
                "mark", "unmark", "delete", "list extra", "bye extra", "mark -1", "delete 0",
                "unmark 2", "mark 999999999999999999999", "tag 1 #bad!", "tag 1 #two #tags",
                "deadline task /by 2026-09-18 1800 /by 2026-09-19 1800",
                "event task /from 2026-09-18 1900 /to 2026-09-18 1800")) {
            Rei.CommandResult result = rei.getResponse(command);
            assertTrue(result.isError(), command);
            assertFalse(result.shouldExit(), command);
            assertEquals(saved, Files.readString(dataFile), command);
            assertEquals(listed, rei.getResponse("list").response(), command);
        }
    }

    @Test
    public void getResponse_supportedDateFormats_findSameLeapDay() {
        Rei rei = new Rei(testDirectory.resolve("dates.txt"));
        for (String date : List.of("2028-02-29", "2028/02/29", "29/02/2028", "29-02-2028")) {
            assertFalse(rei.getResponse("deadline report /by " + date + " 2359").isError());
            assertTrue(rei.getResponse("find " + date).response().contains("report"));
        }
        assertTrue(rei.getResponse("find 2027-02-29").isError());
        assertTrue(rei.getResponse("deadline bad /by 2028-02-29 2400").isError());
    }

    @Test
    public void getResponse_eventDateSearch_includesBoundariesAndExcludesOutsideDates() {
        Rei rei = new Rei(testDirectory.resolve("range.txt"));
        rei.getResponse("todo unrelated");
        rei.getResponse("event retreat /from 2026-09-18 1800 /to 2026-09-20 0900");

        for (String date : List.of("2026-09-18", "2026-09-19", "2026-09-20")) {
            assertTrue(rei.getResponse("find " + date).response().contains("2.[E][ ] retreat"));
        }
        for (String date : List.of("2026-09-17", "2026-09-21")) {
            assertTrue(rei.getResponse("find " + date).response().contains("No deadlines or events"));
        }
    }

    @Test
    public void getResponse_unreadableData_remainsUsableWithoutWriting() throws IOException {
        Path directory = Files.createDirectory(testDirectory.resolve("not-a-file"));
        Rei rei = new Rei(directory);

        assertFalse(rei.getStartupMessage().isEmpty());
        assertFalse(rei.getResponse("list").isError());
        assertTrue(rei.getResponse("todo blocked").isError());
        assertTrue(rei.getResponse("bye").shouldExit());
        assertTrue(Files.isDirectory(directory));
    }

    @Test
    public void getResponse_failedSave_rollsBackEveryMutation() throws IOException {
        Path dataFile = testDirectory.resolve("tasks.txt");
        Rei rei = new Rei(dataFile);
        rei.getResponse("todo keep me");
        String original = rei.getResponse("list").response();
        Files.delete(dataFile);
        Files.createDirectory(dataFile);
        Files.writeString(dataFile.resolve("blocker"), "Prevent replacement");

        for (String command : List.of("todo new", "mark 1", "unmark 1", "tag 1 #school", "delete 1")) {
            Rei.CommandResult result = rei.getResponse(command);
            assertTrue(result.isError(), command);
            assertTrue(result.response().startsWith("I couldn't save your tasks."), command);
            assertEquals(original, rei.getResponse("list").response(), command);
        }
    }

    @Test
    public void getResponse_corruptedFile_preventsOverwritingOriginal() throws IOException {
        Path dataFile = testDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "damaged record");
        Rei rei = new Rei(dataFile);

        assertTrue(rei.getResponse("todo new").isError());
        assertEquals("damaged record", Files.readString(dataFile));
        assertFalse(rei.getResponse("list").isError());
    }

    @Test
    public void getResponse_whitespaceAndEqualTimes_validatesInput() {
        Rei rei = new Rei(testDirectory.resolve("tasks.txt"));

        assertFalse(rei.getResponse("  todo\tread book  ").isError());
        assertFalse(rei.getResponse("deadline\treport /by 2026-09-18   1800").isError());
        assertTrue(rei.getResponse("event study /from 2026-09-18 1800 /to 2026-09-18 1800").isError());
        assertTrue(rei.getResponse("deadline report /by 2026-02-30 1800").isError());
    }

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
