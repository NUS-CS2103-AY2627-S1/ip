package emma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks the chatbot as a whole: one line of input in, one reply out, with the tasks
 * ending up on disk. This is the layer both the console and the window sit on, so it is
 * tested without either of them.
 *
 * <p>That includes whether Emma marks a reply as a complaint, which is how the window
 * knows to show it in its own colours.
 */
public class EmmaTest {

    @TempDir
    Path tempDir;

    private Path saveFile() {
        return tempDir.resolve("emma.json");
    }

    private Emma emma() {
        return new Emma(saveFile().toString());
    }

    @Test
    public void defaultSavePath_always_pointsIntoTheDataFolder() {
        assertEquals("data/emma.json", Emma.DEFAULT_SAVE_PATH);
    }

    @Test
    public void loadTasks_noSaveFileYet_saysNothingAndStartsEmpty() {
        Emma emma = emma();
        assertEquals("", emma.loadTasks());
        assertEquals("You haven't given me anything to track yet!", emma.getResponse("list"));
    }

    @Test
    public void loadTasks_saveFileThatCannotBeRead_explainsAndStartsEmpty() throws IOException {
        Files.writeString(saveFile(), "not json at all");
        Emma emma = emma();

        String message = emma.loadTasks();
        assertTrue(message.contains("looks wrong on line 1"), message);
        assertTrue(message.endsWith("I'll start with an empty list."), message);
        assertEquals("You haven't given me anything to track yet!", emma.getResponse("list"));
    }

    @Test
    public void loadTasks_savedTasks_areTheOnesShown() {
        Emma first = emma();
        first.loadTasks();
        first.getResponse("todo read book");
        first.getResponse("deadline return book /by 2019-10-15");

        Emma second = emma();
        assertEquals("", second.loadTasks());
        assertEquals("Here's your tasks:\n"
                + "1. [T][ ] read book\n"
                + "2. [D][ ] return book (by: Oct 15 2019)", second.getResponse("list"));
    }

    @Test
    public void getResponse_beforeLoadTasksIsCalled_worksOnAnEmptyList() {
        assertEquals("Got it, I've added this:\n  [T][ ] read book",
                emma().getResponse("todo read book"));
    }

    @Test
    public void getResponse_eachKindOfCommand_repliesAsTheCommandDoes() {
        Emma emma = emma();
        assertEquals("Got it, I've added this:\n  [T][ ] read book",
                emma.getResponse("todo read book"));
        assertEquals("Nice! I've marked this as done:\n  [T][x] read book",
                emma.getResponse("mark 1"));
        assertEquals("Here's what I found:\n1. [T][x] read book", emma.getResponse("find book"));
        assertEquals("Here's what matches:\n1. [T][x] read book",
                emma.getResponse("filter /type todo"));
        assertEquals("Okay, I've removed this:\n  [T][x] read book", emma.getResponse("delete 1"));
    }

    @Test
    public void getResponse_commandThatCannotBeCarriedOut_returnsTheComplaintInsteadOfThrowing() {
        Emma emma = emma();
        assertEquals("Sorry, I don't know what that means!", emma.getResponse("blah"));
        assertEquals("You don't have a task numbered 1.", emma.getResponse("mark 1"));
        assertEquals("A todo needs a description, like \"todo read book\".",
                emma.getResponse("todo"));
    }

    @Test
    public void getResponse_afterAFailedCommand_stillAnswersTheNextOne() {
        Emma emma = emma();
        emma.getResponse("blah");
        assertEquals("Got it, I've added this:\n  [T][ ] read book",
                emma.getResponse("todo read book"));
    }

    @Test
    public void getResponse_duplicateTask_isRefused() {
        Emma emma = emma();
        emma.getResponse("todo read book");
        assertEquals("You're already tracking this:\n  [T][ ] read book",
                emma.getResponse("todo read book"));
        assertEquals("Here's your tasks:\n1. [T][ ] read book", emma.getResponse("list"));
    }

    @Test
    public void isExit_beforeAnyCommand_isFalse() {
        assertFalse(emma().isExit());
    }

    @Test
    public void isExit_afterBye_isTrue() {
        Emma emma = emma();
        assertEquals("Bye for now! Hope to see you again soon.", emma.getResponse("bye"));
        assertTrue(emma.isExit());
    }

    @Test
    public void isExit_afterAnOrdinaryCommand_staysFalse() {
        Emma emma = emma();
        emma.getResponse("todo read book");
        assertFalse(emma.isExit());
        emma.getResponse("list");
        assertFalse(emma.isExit());
    }

    @Test
    public void isError_beforeAnyCommand_isFalse() {
        assertFalse(emma().isError());
    }

    @Test
    public void getResponse_unrecognisedCommand_isFlaggedAsAnError() {
        Emma emma = emma();
        assertEquals("Sorry, I don't know what that means!", emma.getResponse("blah"));
        assertTrue(emma.isError());
    }

    @Test
    public void getResponse_commandThatWasCarriedOut_isNotFlagged() {
        Emma emma = emma();
        emma.getResponse("todo read book");
        assertFalse(emma.isError());
    }

    @Test
    public void getResponse_validCommandAfterAnError_clearsTheFlag() {
        Emma emma = emma();
        emma.getResponse("blah");
        assertTrue(emma.isError());
        emma.getResponse("list");
        assertFalse(emma.isError());
    }

    @Test
    public void getResponse_refusedDuplicate_isFlaggedAsAnError() {
        Emma emma = emma();
        emma.getResponse("todo read book");
        emma.getResponse("todo read book");
        assertTrue(emma.isError());
    }

    @Test
    public void getResponse_bye_endsWithoutBeingAnError() {
        Emma emma = emma();
        emma.getResponse("bye");
        assertTrue(emma.isExit());
        assertFalse(emma.isError());
    }

    @Test
    public void getResponse_commandThatChangesTheList_writesTheChangeToDisk() throws IOException {
        Emma emma = emma();
        emma.getResponse("todo read book");
        assertTrue(Files.exists(saveFile()));
        assertTrue(Files.readString(saveFile()).contains("\"description\": \"read book\""));
    }

    @Test
    public void getResponse_commandThatChangesNothing_writesNoFile() {
        Emma emma = emma();
        emma.getResponse("list");
        emma.getResponse("find book");
        assertFalse(Files.exists(saveFile()));
    }

    @Test
    public void getResponse_descriptionNeedingEscapes_survivesASaveAndReload() {
        Emma first = emma();
        first.getResponse("todo say \"hi\" to Bob");

        Emma second = emma();
        assertEquals("", second.loadTasks());
        assertEquals("Here's your tasks:\n1. [T][ ] say \"hi\" to Bob",
                second.getResponse("list"));
    }
}
