package tem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for command processing in {@link Tem}.
 */
public class TemTest {

    @TempDir
    Path tempDir;

    @Test
    public void getResponse_extraWhitespace_addsNormalizedTask() throws TemException {
        Path filePath = tempDir.resolve("tem.txt");
        Tem tem = new Tem(filePath.toString());

        String response = tem.getResponse("  todo   read   book  ");
        List<Task> savedTasks = new Storage(filePath.toString()).load();

        assertTrue(response.startsWith("Logged."));
        assertEquals(1, savedTasks.size());
        assertEquals("read book", savedTasks.get(0).getDescription());
        assertFalse(tem.wasLastResponseAnError());
    }

    @Test
    public void getResponse_duplicateTask_returnsErrorAndDoesNotSaveSecondTask() throws TemException {
        Path filePath = tempDir.resolve("tem.txt");
        Tem tem = new Tem(filePath.toString());
        tem.getResponse("todo read book");

        String response = tem.getResponse("todo Read Book");
        List<Task> savedTasks = new Storage(filePath.toString()).load();

        assertEquals("That task is already on your radar:\n  [T][ ] Read Book", response);
        assertEquals(1, savedTasks.size());
        assertTrue(tem.wasLastResponseAnError());
    }

    @Test
    public void getResponse_listWithArgument_returnsHelpfulError() {
        Tem tem = new Tem(tempDir.resolve("tem.txt").toString());

        assertEquals("The list command does not take any arguments.", tem.getResponse("list everything"));
        assertTrue(tem.wasLastResponseAnError());
    }

    @Test
    public void getResponse_invalidEvent_returnsErrorWithoutSavingTask() throws TemException {
        Path filePath = tempDir.resolve("tem.txt");
        Tem tem = new Tem(filePath.toString());

        String response = tem.getResponse("event meeting /from 2pm /to 4pm /to 5pm");

        assertEquals("Use /from and /to only once each in an event.", response);
        assertTrue(new Storage(filePath.toString()).load().isEmpty());
        assertTrue(tem.wasLastResponseAnError());
    }

    @Test
    public void getResponse_markUnmarkAndDelete_updatesSavedTask() throws TemException {
        Path filePath = tempDir.resolve("tem.txt");
        Tem tem = new Tem(filePath.toString());
        tem.getResponse("todo read book");

        assertTrue(tem.getResponse("mark 1").contains("marked this task as done"));
        assertTrue(new Storage(filePath.toString()).load().get(0).isDone());

        assertTrue(tem.getResponse("unmark 1").contains("marked this task as not done"));
        assertFalse(new Storage(filePath.toString()).load().get(0).isDone());

        assertTrue(tem.getResponse("delete 1").contains("removed this task"));
        assertTrue(new Storage(filePath.toString()).load().isEmpty());
    }

    @Test
    public void getResponse_findAndSort_returnsExpectedTasks() throws TemException {
        Path filePath = tempDir.resolve("tem.txt");
        Tem tem = new Tem(filePath.toString());
        tem.getResponse("todo read book");
        tem.getResponse("deadline return book /by 2019-10-16");
        tem.getResponse("deadline submit report /by 2019-10-15");

        assertTrue(tem.getResponse("find BOOK").contains("read book"));
        assertTrue(tem.getResponse("sort").contains("sorted the deadlines chronologically"));

        List<Task> savedTasks = new Storage(filePath.toString()).load();
        assertEquals("submit report", savedTasks.get(0).getDescription());
        assertEquals("return book", savedTasks.get(1).getDescription());
    }

    @Test
    public void getResponse_bye_returnsPersonalityMessage() {
        Tem tem = new Tem(tempDir.resolve("tem.txt").toString());

        assertEquals("Take care. Tem will keep your plan ready for next time.", tem.getResponse("bye"));
        assertFalse(tem.wasLastResponseAnError());
    }

    @Test
    public void welcomeMessage_guiGreetingDoesNotContainAsciiBanner() {
        Tem tem = new Tem(tempDir.resolve("tem.txt").toString());

        assertEquals("Hello, I'm Tem. I help you keep tasks organized.\nWhat would you like to add today?",
                tem.welcomeMessage());
    }

    @Test
    public void isExit_extraWhitespaceAroundBye_returnsTrue() {
        Tem tem = new Tem(tempDir.resolve("tem.txt").toString());

        assertTrue(tem.isExit("  bye  "));
        assertFalse(tem.isExit("bye now"));
    }
}
