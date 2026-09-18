package habpyduck;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import habpyduck.storage.Storage;

public class HabpyDuckTest {
    @TempDir
    private Path tempDir;

    @Test
    public void getResponse_addAndListTasks_returnsCommandOutput() {
        HabpyDuck habpyDuck = new HabpyDuck(new Storage(tempDir.resolve("tasks.txt").toString()));

        String addResponse = habpyDuck.getResponse("todo read book");
        String listResponse = habpyDuck.getResponse("list");

        assertEquals(String.join(System.lineSeparator(),
                "Got it, friend. I've added this task:",
                "  [T][ ] read book",
                "Now you have 1 tasks in the list."), addResponse);
        assertEquals(String.join(System.lineSeparator(),
                "Here are the tasks in your list, friend:",
                "1.[T][ ] read book"), listResponse);
    }

    @Test
    public void getResponse_addDateOnlyDeadline_returnsDeadlineAtStartOfDay() {
        HabpyDuck habpyDuck = new HabpyDuck(new Storage(tempDir.resolve("tasks.txt").toString()));

        String addResponse = habpyDuck.getResponse("deadline return book /by 25/8/2026");

        assertEquals(String.join(System.lineSeparator(),
                "Got it, friend. I've added this task:",
                "  [D][ ] return book (by: 25/8/2026)",
                "Now you have 1 tasks in the list."), addResponse);
    }

    @Test
    public void getResponse_markAlreadyMarkedTask_returnsErrorMessageWithoutChangingTasks() {
        HabpyDuck habpyDuck = new HabpyDuck(new Storage(tempDir.resolve("tasks.txt").toString()));

        habpyDuck.getResponse("todo read book");
        habpyDuck.getResponse("mark 1");
        String duplicateMarkResponse = habpyDuck.getResponse("mark 1");
        String listResponse = habpyDuck.getResponse("list");

        assertEquals("OH NO!!! This task is already marked, friend.", duplicateMarkResponse);
        assertEquals(String.join(System.lineSeparator(),
                "Here are the tasks in your list, friend:",
                "1.[T][X] read book"), listResponse);
    }

    @Test
    public void getResponse_unmarkAlreadyUnmarkedTask_returnsErrorMessageWithoutChangingTasks() {
        HabpyDuck habpyDuck = new HabpyDuck(new Storage(tempDir.resolve("tasks.txt").toString()));

        habpyDuck.getResponse("todo read book");
        String duplicateUnmarkResponse = habpyDuck.getResponse("unmark 1");
        String listResponse = habpyDuck.getResponse("list");

        assertEquals("OH NO!!! This task is already unmarked, friend.", duplicateUnmarkResponse);
        assertEquals(String.join(System.lineSeparator(),
                "Here are the tasks in your list, friend:",
                "1.[T][ ] read book"), listResponse);
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorMessageWithoutChangingTasks() {
        HabpyDuck habpyDuck = new HabpyDuck(new Storage(tempDir.resolve("tasks.txt").toString()));

        String invalidResponse = habpyDuck.getResponse("todo");
        String listResponse = habpyDuck.getResponse("list");

        assertEquals("OH NO!!! A todo needs a description, friend. Try something like: todo read book",
                invalidResponse);
        assertEquals("Here are the tasks in your list, friend:", listResponse);
    }

    @Test
    public void getResponse_tagAndFindTagCommands_returnsTaggedTasks() {
        HabpyDuck habpyDuck = new HabpyDuck(new Storage(tempDir.resolve("tasks.txt").toString()));

        habpyDuck.getResponse("todo read book");
        String tagResponse = habpyDuck.getResponse("tag 1 #Fun #school_work");
        String findTagResponse = habpyDuck.getResponse("findtag #fun");

        assertEquals(String.join(System.lineSeparator(),
                "Got it, friend. I've tagged this task:",
                "  [T][ ] read book #fun #school_work"), tagResponse);
        assertEquals(String.join(System.lineSeparator(),
                "I found these tasks with that tag, friend:",
                "1.[T][ ] read book #fun #school_work"), findTagResponse);
    }

    @Test
    public void getResponse_untagCommand_removesTagFromTask() {
        HabpyDuck habpyDuck = new HabpyDuck(new Storage(tempDir.resolve("tasks.txt").toString()));

        habpyDuck.getResponse("todo read book");
        habpyDuck.getResponse("tag 1 #fun #school");
        String untagResponse = habpyDuck.getResponse("untag 1 #fun");

        assertEquals(String.join(System.lineSeparator(),
                "Got it, friend. I've removed that tag from this task:",
                "  [T][ ] read book #school"), untagResponse);
    }

    @Test
    public void getResponse_invalidTagCommand_returnsErrorMessageWithoutChangingTasks() {
        HabpyDuck habpyDuck = new HabpyDuck(new Storage(tempDir.resolve("tasks.txt").toString()));

        habpyDuck.getResponse("todo read book");
        String invalidResponse = habpyDuck.getResponse("tag 1 fun");
        String listResponse = habpyDuck.getResponse("list");

        assertEquals("OH NO!!! Tags must start with # and use only letters, numbers, hyphens, "
                + "or underscores. Try something like: tag 2 #fun", invalidResponse);
        assertEquals(String.join(System.lineSeparator(),
                "Here are the tasks in your list, friend:",
                "1.[T][ ] read book"), listResponse);
    }

    @Test
    public void getResponse_byeCommand_returnsFarewellMessage() {
        HabpyDuck habpyDuck = new HabpyDuck(new Storage(tempDir.resolve("tasks.txt").toString()));

        assertEquals("Bye friend. Hope to see you again soon!", habpyDuck.getResponse("bye"));
    }

    @Test
    public void getResponse_addTask_savesTaskToStorage() throws Exception {
        Path saveFile = tempDir.resolve("tasks.txt");
        HabpyDuck habpyDuck = new HabpyDuck(new Storage(saveFile.toString()));

        habpyDuck.getResponse("todo read book");

        assertEquals("T | 0 | read book", Files.readString(saveFile).stripTrailing());
    }

    @Test
    public void getCommandType_successfulCommands_returnsLastCommandClassName() {
        HabpyDuck habpyDuck = new HabpyDuck(new Storage(tempDir.resolve("tasks.txt").toString()));

        habpyDuck.getResponse("todo read book");
        assertEquals("AddCommand", habpyDuck.getCommandType());

        habpyDuck.getResponse("mark 1");
        assertEquals("MarkCommand", habpyDuck.getCommandType());

        habpyDuck.getResponse("delete 1");
        assertEquals("DeleteCommand", habpyDuck.getCommandType());

        habpyDuck.getResponse("todo write notes");
        habpyDuck.getResponse("tag 1 #fun");
        assertEquals("TagCommand", habpyDuck.getCommandType());

        habpyDuck.getResponse("findtag #fun");
        assertEquals("FindTagCommand", habpyDuck.getCommandType());

        habpyDuck.getResponse("untag 1 #fun");
        assertEquals("UntagCommand", habpyDuck.getCommandType());
    }

    @Test
    public void getCommandType_invalidCommand_returnsErrorResponseType() {
        HabpyDuck habpyDuck = new HabpyDuck(new Storage(tempDir.resolve("tasks.txt").toString()));

        habpyDuck.getResponse("todo");

        assertEquals(HabpyDuck.ERROR_RESPONSE_TYPE, habpyDuck.getCommandType());
    }
}
