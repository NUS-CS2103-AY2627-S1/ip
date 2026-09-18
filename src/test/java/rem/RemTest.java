package rem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class RemTest {
    @TempDir
    Path directory;

    @Test
    public void getResponse_invalidCommands_providesActionableExamples() {
        Rem rem = new Rem(directory.resolve("rem.txt").toString());
        assertEquals("Hi! I'm Rem.\nI can help! Then maybe a nap.", rem.getWelcome());
        assertTrue(rem.getResponse("todos").text().contains("Try todo read book"));
        assertTrue(rem.getResponse("deadline report").text().contains("/by YYYY-MM-DD [HHmm]"));
        assertTrue(rem.getResponse("event meeting").text().contains("The end must be after the start."));
        assertTrue(rem.getResponse("mark 1").text().contains("Use list to see task numbers"));
        assertFalse(rem.getResponse("list").text().contains("Example:"));
    }

    @Test
    public void getResponse_addMarkReload_collectsLinesAndPersists() {
        String path = directory.resolve("rem.txt").toString();
        Rem rem = new Rem(path);
        assertEquals("Got it! I put it on the list:\n[T][ ] read book\nYay! Our first task!",
                rem.getResponse("todo read book").text());
        rem.getResponse("mark 1");
        assertEquals("Hmm... what to do now?\n1.[T][X] read book",
                new Rem(path).getResponse("list").text());
        rem.getResponse("unmark 1");
        assertTrue(rem.getResponse("find book").text().contains("[T][ ] read book"));
        rem.getResponse("delete 1");
        assertEquals("Hmm... what to do now?\nNo tasks yet. Got something for us to do?",
                new Rem(path).getResponse("list").text());
    }

    @Test
    public void getResponse_invalidThenValid_doesNotLeakMessages() {
        Rem rem = new Rem(directory.resolve("rem.txt").toString());
        Response invalid = rem.getResponse("invalid");
        assertFalse(invalid.isExit());
        assertTrue(invalid.isError());
        assertTrue(rem.getResponse("todo").isError());
        assertTrue(rem.getResponse("mark 1").isError());
        assertFalse(rem.getResponse("list").isError());
        assertEquals("You didn't say what you wanna do...", rem.getResponse("todo").text());
        assertEquals("Hmm... what to do now?\nNo tasks yet. Got something for us to do?",
                rem.getResponse("list").text());
        Response farewell = rem.getResponse("BYE");
        assertTrue(farewell.isExit());
        assertFalse(farewell.isError());
        assertEquals("[Yawn] Need more sleep. Time for bed...", farewell.text());
    }

    @Test
    public void getResponse_noteReplaceFindDeleteNote_behavesAndPersists() {
        String path = directory.resolve("rem.txt").toString();
        Rem rem = new Rem(path);
        rem.getResponse("todo read book /note Borrow it from Alice");

        assertEquals("I'll keep this note with your task:\n"
                + "[T][ ] read book\n  Note: Return it tomorrow",
                rem.getResponse("note 1 Return it tomorrow").text());
        assertTrue(rem.getResponse("find TOMORROW").text().contains("Note: Return it tomorrow"));
        assertEquals("Okay. Took the note off this task:\n[T][ ] read book",
                rem.getResponse("deletenote 1").text());
        assertEquals("This task doesn't have a note to remove.",
                rem.getResponse("deletenote 1").text());
        assertFalse(new Rem(path).getResponse("list").text().contains("Note:"));
    }

    @Test
    public void getResponse_invalidNoteText_exactMessagesReturned() {
        Rem rem = new Rem(directory.resolve("rem.txt").toString());
        rem.getResponse("todo read book");

        assertEquals("What should I keep in this note?",
                rem.getResponse("note 1").text());
        assertEquals("Rem can't remember more than 200 characters...",
                rem.getResponse("note 1 " + "a".repeat(201)).text());
        assertFalse(rem.getResponse("list").text().contains("Note:"));
    }

    @Test
    public void getResponse_emptySearchAndSchedule_reportsAbsenceWithoutInventingAvailability() {
        Rem rem = new Rem(directory.resolve("rem.txt").toString());
        assertFalse(rem.hasTasks());
        assertEquals("Didn't find any tasks matching 'pillow'. Try another word?",
                rem.getResponse("find pillow").text());
        rem.getResponse("todo wash pillow");
        assertTrue(rem.hasTasks());
        assertEquals("Found these!\n1.[T][ ] wash pillow", rem.getResponse("find pillow").text());
        assertEquals("Nothing scheduled on Oct 01 2026. Maybe nap time?",
                rem.getResponse("on 2026-10-01").text());
        rem.getResponse("delete 1");
        assertFalse(rem.hasTasks());
    }

    @Test
    public void getResponse_scheduledTasksOnDate_returnsMatchingTasksInOrder() {
        Rem rem = new Rem(directory.resolve("rem.txt").toString());
        rem.getResponse("todo buy snacks");
        rem.getResponse("deadline submit report /by 2026-10-01 1800");
        rem.getResponse("event conference /from 2026-09-30 0900 /to 2026-10-02 1700");
        rem.getResponse("deadline later /by 2026-10-03");

        assertEquals("Here's what's scheduled on Oct 01 2026:\n"
                + "1.[D][ ] submit report (by: Oct 01 2026, 6:00 PM)\n"
                + "2.[E][ ] conference (from: Sep 30 2026, 9:00 AM to: Oct 02 2026, 5:00 PM)",
                rem.getResponse("on 2026-10-01").text());
    }

    @Test
    public void getResponse_storageFailure_reportsError() throws IOException {
        Path blocked = Files.createDirectory(directory.resolve("blocked"));
        Rem rem = new Rem(blocked.toString());
        assertTrue(rem.getWelcome().contains("I couldn't load your saved tasks"));
        Response failure = rem.getResponse("todo read book");
        assertTrue(failure.text().contains("Your changes were not applied or saved."));
        assertFalse(rem.hasTasks());
        assertTrue(failure.isError());
        assertFalse(failure.isExit());
    }

    @Test
    public void getResponse_corruptFile_preservesOriginalAndReportsLine() throws IOException {
        Path file = directory.resolve("corrupt.txt");
        String original = "T | 0 | keep me\nD | maybe | broken\n";
        Files.writeString(file, original);
        Rem rem = new Rem(file.toString());
        assertTrue(rem.getWelcome().contains("line 2"));
        assertTrue(rem.getResponse("todo new task").isError());
        assertFalse(rem.hasTasks());
        assertEquals(original, Files.readString(file));
    }

    @Test
    public void startFresh_corruptFile_backsUpOriginalAndAcceptsCommands() throws IOException {
        Path file = directory.resolve("rem.txt");
        String original = "D | maybe | broken\n";
        Files.writeString(file, original);
        Rem rem = new Rem(file.toString());
        assertTrue(rem.canStartFresh());

        Path backup = rem.startFresh();

        assertEquals(original, Files.readString(backup));
        assertEquals("", Files.readString(file));
        assertFalse(rem.canStartFresh());
        assertFalse(rem.getResponse("todo replacement").isError());
        assertTrue(rem.hasTasks());
        assertEquals(List.of("T | 0 | replacement"), Files.readAllLines(file));
    }

    @Test
    public void getResponse_saveFailures_allMutationsLeaveMemoryUnchanged() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Rem rem = new Rem(file.toString());
        rem.getResponse("todo read /note original");
        String before = rem.getResponse("list").text();
        Files.delete(file);
        Files.createDirectory(file);
        // A nonempty directory cannot be replaced even on filesystems allowing directory moves.
        Files.writeString(file.resolve("keep.txt"), "keep");
        for (String command : List.of("todo new", "delete 1", "mark 1", "unmark 1",
                "note 1 replacement", "deletenote 1")) {
            assertTrue(rem.getResponse(command).isError(), command);
            assertEquals(before, rem.getResponse("list").text(), command);
            assertEquals("keep", Files.readString(file.resolve("keep.txt")));
        }
        Files.delete(file.resolve("keep.txt"));
        Files.delete(file);
        assertFalse(rem.getResponse("todo new").isError());
        assertEquals(rem.getResponse("list").text(), new Rem(file.toString()).getResponse("list").text());
    }

    @Test
    public void getResponse_duplicates_ignoresStatusButDistinguishesDetails() {
        Rem rem = new Rem(directory.resolve("tasks.txt").toString());
        assertFalse(rem.getResponse("todo read").isError());
        rem.getResponse("mark 1");
        assertTrue(rem.getResponse("todo read").text().contains("already exists as task 1"));
        assertFalse(rem.getResponse("todo read /note again").isError());
        assertFalse(rem.getResponse("deadline read /by 2026-10-01").isError());
        assertTrue(rem.getResponse("deadline read /by 1/10/2026").isError());
        assertFalse(rem.getResponse("deadline read /by 2026-10-02").isError());
        assertFalse(rem.getResponse("event read /from 2026-10-01 /to 2026-10-02").isError());
        assertTrue(rem.getResponse("event read /from 2026-10-01 /to 2026-10-02").isError());
        assertFalse(rem.getResponse("event read /from 2026-10-01 /to 2026-10-03").isError());
    }

    @Test
    public void getResponse_badInput_followingValidCommandStillWorks() {
        Rem rem = new Rem(directory.resolve("tasks.txt").toString());
        for (String input : List.of("list extra", "bye later", "mark +1", "mark 9999999999999999",
                "todo a\nb", "todo a\u0000b", "todo a /note b\rc", "note 1 a\u2028b")) {
            Response response = rem.getResponse(input);
            assertTrue(response.isError(), input);
            assertFalse(response.isExit(), input);
        }
        assertFalse(rem.getResponse("  todo\tread  book  ").isError());
        assertTrue(rem.getResponse("list").text().contains("read  book"));
    }

    @Test
    public void getResponse_dataParentBecomesFile_reportsRecoveryWithoutApplyingTask() throws IOException {
        Path parent = directory.resolve("data");
        Rem rem = new Rem(parent.resolve("rem.txt").toString());
        Files.writeString(parent, "preserve this file");
        Response response = rem.getResponse("todo read");
        assertTrue(response.isError());
        assertTrue(response.text().contains("Check folder permissions and free disk space"));
        assertFalse(rem.hasTasks());
        assertEquals("preserve this file", Files.readString(parent));
    }

}
