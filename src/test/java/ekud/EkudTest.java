package ekud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ekud.storage.Storage;
import ekud.ui.DialogStyle;
import ekud.ui.Ui;

/**
 * Tests GUI-oriented replies from {@link Ekud#getResponse} without touching
 * the default save file.
 */
public class EkudTest {
    @TempDir
    Path tempDir;

    @Test
    public void getResponse_todo_returnsAddedMessage() {
        Ekud ekud = newChatbot();
        String response = ekud.getResponse("todo read book");
        assertTrue(response.contains("Got it. I've added this task:"));
        assertTrue(response.contains("[T][ ] read book"));
        assertTrue(response.contains("Now you have 1 task in the list."));
        assertEquals(DialogStyle.ADD, ekud.getDialogStyle());
        assertFalse(ekud.isExit());
    }

    @Test
    public void getResponse_unknownCommand_returnsError() {
        Ekud ekud = newChatbot();
        String response = ekud.getResponse("blah");
        assertTrue(response.contains("I don't recognize that command"));
        assertEquals(DialogStyle.ERROR, ekud.getDialogStyle());
        assertFalse(ekud.isExit());
    }

    @Test
    public void getStartupError_missingSaveFile_returnsNull() {
        Ekud ekud = newChatbot();
        assertNull(ekud.getStartupError());
    }

    @Test
    public void getResponse_help_returnsCommandGuide() {
        Ekud ekud = newChatbot();
        String response = ekud.getResponse("help");
        assertTrue(response.contains("Here are the commands I understand:"));
        assertTrue(response.contains("todo DESCRIPTION"));
        assertTrue(response.contains("deadline DESCRIPTION /by DATE"));
        assertTrue(response.contains("Type help anytime to see this guide again."));
        assertEquals(DialogStyle.NONE, ekud.getDialogStyle());
        assertFalse(ekud.isExit());
    }

    @Test
    public void getResponse_listWhenEmpty_returnsEmptyMessage() {
        Ekud ekud = newChatbot();
        assertEquals("Your task list is empty.", ekud.getResponse("list"));
        assertEquals(DialogStyle.NONE, ekud.getDialogStyle());
    }

    @Test
    public void getWelcomeMessage_containsNameAndCommandHint() {
        Ekud ekud = newChatbot();
        String welcome = ekud.getWelcomeMessage();
        assertTrue(welcome.contains("Hello! I'm Ekud."));
        assertTrue(welcome.contains("todo, deadline, or event"));
        assertFalse(ekud.isExit());
    }

    @Test
    public void getStartupError_unreadableSavePath_returnsMessage() throws Exception {
        Path notAFile = tempDir.resolve("not-a-file");
        Files.createDirectory(notAFile);
        Ekud ekud = new Ekud(Ui.forGui(), new Storage(notAFile.toString()));
        assertNotNull(ekud.getStartupError());
        assertTrue(ekud.getStartupError().contains("Could not read saved tasks"));
        assertTrue(ekud.getWelcomeMessage().contains("Hello! I'm Ekud."));
    }

    @Test
    public void getResponse_deadlineAndEvent_returnsAddedStyle() {
        Ekud ekud = newChatbot();

        String deadline = ekud.getResponse("deadline return book /by 2019-12-02");
        assertTrue(deadline.contains("[D][ ] return book (by: Dec 02 2019)"));
        assertEquals(DialogStyle.ADD, ekud.getDialogStyle());

        String event = ekud.getResponse("event meeting /from 2019-12-02 1400 /to 2019-12-02 1600");
        assertTrue(event.contains("[E][ ] meeting"));
        assertTrue(event.contains("Now you have 2 tasks in the list."));
        assertEquals(DialogStyle.ADD, ekud.getDialogStyle());
    }

    @Test
    public void getResponse_listFindOnUnmark_returnsExpectedReplies() {
        Ekud ekud = newChatbot();
        ekud.getResponse("todo read book");
        ekud.getResponse("deadline return book /by 2019-12-02");
        ekud.getResponse("mark 1");

        String list = ekud.getResponse("list");
        assertTrue(list.contains("Here are the tasks in your list:"));
        assertTrue(list.contains("1. [T][X] read book"));
        assertTrue(list.contains("2. [D][ ] return book (by: Dec 02 2019)"));
        assertEquals(DialogStyle.NONE, ekud.getDialogStyle());

        String found = ekud.getResponse("find BOOK");
        assertTrue(found.contains("Here are the matching tasks in your list:"));
        assertTrue(found.contains("read book"));
        assertEquals(DialogStyle.NONE, ekud.getDialogStyle());

        String noneFound = ekud.getResponse("find lecture");
        assertEquals("No tasks matching \"lecture\".", noneFound);

        String onDate = ekud.getResponse("on 2019-12-02");
        assertTrue(onDate.contains("Here are the deadlines/events on Dec 02 2019:"));
        assertTrue(onDate.contains("return book"));

        String onEmpty = ekud.getResponse("on 2019-12-01");
        assertEquals("No deadlines or events on Dec 01 2019.", onEmpty);

        String unmarked = ekud.getResponse("unmark 1");
        assertTrue(unmarked.contains("OK, I've marked this task as not done yet:"));
        assertTrue(unmarked.contains("[T][ ] read book"));
        assertEquals(DialogStyle.CHANGE_MARK, ekud.getDialogStyle());
    }

    @Test
    public void getResponse_invalidTaskNumber_returnsErrorStyle() {
        Ekud ekud = newChatbot();
        ekud.getResponse("todo read book");
        String response = ekud.getResponse("delete 3");
        assertEquals("Task number 3 does not exist.", response);
        assertEquals(DialogStyle.ERROR, ekud.getDialogStyle());
        assertFalse(ekud.isExit());
    }

    @Test
    public void getResponse_persistsTasksForNewSession() {
        Storage storage = new Storage(tempDir.resolve("ekud.txt").toString());
        Ekud first = new Ekud(Ui.forGui(), storage);
        first.getResponse("todo read book");
        first.getResponse("mark 1");

        Ekud second = new Ekud(Ui.forGui(), storage);
        String list = second.getResponse("list");
        assertTrue(list.contains("[T][X] read book"));
        assertNull(second.getStartupError());
    }

    @Test
    public void getResponse_bye_setsExitAndShowsGoodbye() {
        Ekud ekud = newChatbot();
        String response = ekud.getResponse("bye");
        assertEquals("Bye. Hope to see you again soon!", response);
        assertTrue(ekud.isExit());
    }

    @Test
    public void getResponse_markThenDelete_updatesListAndStyles() {
        Ekud ekud = newChatbot();
        ekud.getResponse("todo read book");

        String marked = ekud.getResponse("mark 1");
        assertTrue(marked.contains("Nice! I've marked this task as done:"));
        assertTrue(marked.contains("[T][X] read book"));
        assertEquals(DialogStyle.CHANGE_MARK, ekud.getDialogStyle());

        String deleted = ekud.getResponse("delete 1");
        assertTrue(deleted.contains("Noted. I've removed this task:"));
        assertTrue(deleted.contains("Now you have 0 tasks in the list."));
        assertEquals(DialogStyle.DELETE, ekud.getDialogStyle());
        assertEquals("Your task list is empty.", ekud.getResponse("list"));
    }

    /**
     * Returns a chatbot that stores tasks in a temporary file.
     */
    private Ekud newChatbot() {
        return new Ekud(Ui.forGui(), new Storage(tempDir.resolve("ekud.txt").toString()));
    }
}
