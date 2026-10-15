package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Verifies complete successful command workflows, including persistence and undo. */
class SevenSixCommandsTest {
    /** Isolates persisted tasks from other tests and the user's data. */
    @TempDir
    private Path temporaryDirectory;

    /** All three task types should display in insertion order and survive a restart. */
    @Test
    void addMixedTasksPersistsTheOrderedList() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        SevenSix chatbot = new SevenSix(file);
        assertFalse(Ui.isErrorResponse(chatbot.getResponse("todo read book")));
        assertFalse(Ui.isErrorResponse(chatbot.getResponse("deadline return book /by 2024-02-29")));
        assertFalse(Ui.isErrorResponse(chatbot.getResponse("event meeting /from 2024-03-01 1200 /to 2024-03-01 1300")));
        String expected = String.join(System.lineSeparator(), "Here is your pipeline:", "1.[T][ ] read book",
                "2.[D][ ] return book (by: Feb 29 2024)",
                "3.[E][ ] meeting (from: Mar 01 2024 12:00 PM to: Mar 01 2024 1:00 PM)");

        assertEquals(expected, chatbot.getResponse("list"));
        assertEquals(expected, new SevenSix(file).getResponse("list"));
        assertEquals("", chatbot.getStartupWarning());
    }

    /** A successful unmark must persist and undo must restore the completed state. */
    @Test
    void unmarkPersistsAndUndoRestoresCompletion() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        SevenSix chatbot = new SevenSix(file);
        chatbot.getResponse("todo first");
        chatbot.getResponse("mark 1");

        assertEquals("Understood. I've moved this deliverable back into the pipeline:"
                + System.lineSeparator() + "  [T][ ] first", chatbot.getResponse("unmark 1"));
        assertEquals("Here is your pipeline:" + System.lineSeparator() + "1.[T][ ] first",
                new SevenSix(file).getResponse("list"));
        assertEquals("Undid mark as not done: [T][X] first", chatbot.getResponse("undo"));
        assertEquals("Here is your pipeline:" + System.lineSeparator() + "1.[T][X] first",
                new SevenSix(file).getResponse("list"));
    }

    /** Deleting the first task renumbers the list and undo restores its original position. */
    @Test
    void deleteRenumbersAndUndoRestoresOrder() {
        SevenSix chatbot = new SevenSix(temporaryDirectory.resolve("tasks.txt"));
        chatbot.getResponse("todo first");
        chatbot.getResponse("todo second");

        assertEquals(String.join(System.lineSeparator(), "Noted. I've descoped this deliverable:",
                "  [T][ ] first", "Your pipeline now holds 1 deliverable."), chatbot.getResponse("delete 1"));
        assertEquals("Here is your pipeline:" + System.lineSeparator() + "1.[T][ ] second",
                chatbot.getResponse("list"));
        chatbot.getResponse("undo");
        assertEquals("Here is your pipeline:" + System.lineSeparator() + "1.[T][ ] first"
                + System.lineSeparator() + "2.[T][ ] second", chatbot.getResponse("list"));
    }

    /** Removing the final task must persist an empty list rather than resurrect it at restart. */
    @Test
    void deleteLastTaskPersistsEmptyList() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        SevenSix chatbot = new SevenSix(file);
        chatbot.getResponse("todo first");

        assertEquals(String.join(System.lineSeparator(), "Noted. I've descoped this deliverable:",
                "  [T][ ] first", "Your pipeline now holds 0 deliverables."), chatbot.getResponse("delete 1"));
        assertEquals("Your pipeline is empty. Nothing to action right now.", new SevenSix(file).getResponse("list"));
    }

    /** Search is case-insensitive and neither matching nor empty results consume undo history. */
    @Test
    void findReturnsMatchesWithoutChangingTasksOrUndo() {
        SevenSix chatbot = new SevenSix(temporaryDirectory.resolve("tasks.txt"));
        chatbot.getResponse("todo read book");
        chatbot.getResponse("todo exercise");

        assertEquals("Here is what surfaced in your pipeline:" + System.lineSeparator() + "1.[T][ ] read book",
                chatbot.getResponse("find BOOK"));
        assertEquals("Nothing in your pipeline matches that search.", chatbot.getResponse("find holiday"));
        chatbot.getResponse("undo");
        assertEquals("Here is your pipeline:" + System.lineSeparator() + "1.[T][ ] read book",
                chatbot.getResponse("list"));
    }

    /** Identical schedules are duplicates, but changing an endpoint yields a distinct task. */
    @Test
    void duplicateDatedTasksAreRejectedButDifferentSchedulesAreAllowed() {
        SevenSix chatbot = new SevenSix(temporaryDirectory.resolve("tasks.txt"));
        chatbot.getResponse("deadline report /by 2024-02-29");
        assertTrue(Ui.isErrorResponse(chatbot.getResponse("deadline report /by 29/2/2024")));
        assertFalse(Ui.isErrorResponse(chatbot.getResponse("deadline report /by 2024-03-01")));
        chatbot.getResponse("event meeting /from 2024-03-01 /to 2024-03-02");
        assertTrue(Ui.isErrorResponse(chatbot.getResponse("event meeting /from 1/3/2024 /to 2/3/2024")));
        assertFalse(Ui.isErrorResponse(chatbot.getResponse("event meeting /from 2024-03-01 /to 2024-03-03")));
    }

    /** Saying goodbye returns a farewell without changing tasks or the previous undo. */
    @Test
    void byeDoesNotChangeTaskState() {
        SevenSix chatbot = new SevenSix(temporaryDirectory.resolve("tasks.txt"));
        chatbot.getResponse("todo first");

        assertEquals("Great sync. Let's touch base again soon!", chatbot.getResponse("\tbye  "));
        assertEquals("Here is your pipeline:" + System.lineSeparator() + "1.[T][ ] first",
                chatbot.getResponse("list"));
        chatbot.getResponse("undo");
        assertEquals("Your pipeline is empty. Nothing to action right now.", chatbot.getResponse("list"));
    }
}
