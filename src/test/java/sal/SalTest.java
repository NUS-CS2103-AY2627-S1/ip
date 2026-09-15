package sal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link Sal#getResponse(String)} command handling used by the GUI.
 */
public class SalTest {
    @TempDir
    Path tempDir;

    private Sal sal;

    @BeforeEach
    public void setUp() {
        sal = new Sal(tempDir.resolve("sal.txt").toString());
    }

    @Test
    public void getWelcomeMessage_returnsGreeting() {
        String welcome = sal.getWelcomeMessage();
        assertTrue(welcome.contains("Hello! I'm Sal."));
        assertTrue(welcome.contains("What can I do for you?"));
    }

    @Test
    public void getResponse_todo_addsTask() {
        String response = sal.getResponse("todo read book");
        assertTrue(response.contains("Got it. I've added this task:"));
        assertTrue(response.contains("read book"));
        assertTrue(response.contains("Now you have 1 tasks in the list."));
        assertFalse(sal.isExit());
    }

    @Test
    public void getResponse_listAfterAdding_showsTask() {
        sal.getResponse("todo read book");
        String response = sal.getResponse("list");
        assertTrue(response.contains("Here are the tasks in your list:"));
        assertTrue(response.contains("1.[T][ ] read book"));
    }

    @Test
    public void getResponse_markAndUnmark_updatesStatus() {
        sal.getResponse("todo read book");
        String marked = sal.getResponse("mark 1");
        assertTrue(marked.contains("Nice! I've marked this task as done:"));
        assertTrue(marked.contains("[T][X] read book"));

        String unmarked = sal.getResponse("unmark 1");
        assertTrue(unmarked.contains("OK, I've marked this task as not done yet:"));
        assertTrue(unmarked.contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_delete_removesTask() {
        sal.getResponse("todo read book");
        String response = sal.getResponse("delete 1");
        assertTrue(response.contains("Noted. I've removed this task:"));
        assertTrue(response.contains("Now you have 0 tasks in the list."));
        assertTrue(sal.getResponse("list").endsWith("Here are the tasks in your list:"));
    }

    @Test
    public void getResponse_find_returnsMatches() {
        sal.getResponse("todo read book");
        sal.getResponse("todo join sports club");
        String response = sal.getResponse("find book");
        assertTrue(response.contains("Here are the matching tasks in your list:"));
        assertTrue(response.contains("read book"));
        assertFalse(response.contains("join sports club"));
    }

    @Test
    public void getResponse_tag_addsTagToExistingTask() {
        sal.getResponse("todo read book");
        String response = sal.getResponse("tag 1 fun");
        assertTrue(response.contains("OK, I've tagged this task:"));
        assertTrue(response.contains("[T][ ] read book #fun"));
        assertTrue(sal.getResponse("list").contains("1.[T][ ] read book #fun"));
    }

    @Test
    public void getResponse_tagWithHashPrefix_stripsHash() {
        sal.getResponse("todo read book");
        String response = sal.getResponse("tag 1 #urgent");
        assertTrue(response.contains("[T][ ] read book #urgent"));
    }

    @Test
    public void getResponse_tagTwice_appendsBothTags() {
        sal.getResponse("todo read book");
        sal.getResponse("tag 1 fun");
        sal.getResponse("tag 1 cs2103");
        assertTrue(sal.getResponse("list").contains("1.[T][ ] read book #fun #cs2103"));
    }

    @Test
    public void getResponse_duplicateTag_returnsError() {
        sal.getResponse("todo read book");
        sal.getResponse("tag 1 fun");
        assertEquals("This task already has the tag #fun", sal.getResponse("tag 1 fun"));
    }

    @Test
    public void getResponse_tagPersistsAcrossReload() {
        sal.getResponse("todo read book");
        sal.getResponse("tag 1 fun");
        Sal reloaded = new Sal(tempDir.resolve("sal.txt").toString());
        assertTrue(reloaded.getResponse("list").contains("#fun"));
    }

    @Test
    public void getResponse_unknownCommand_returnsError() {
        assertEquals("Command not recognised.", sal.getResponse("blah"));
    }

    @Test
    public void getChatResponse_validCommand_isNotError() {
        ChatResponse response = sal.getChatResponse("todo read book");
        assertFalse(response.isError());
        assertTrue(response.getMessage().contains("Got it. I've added this task:"));
    }

    @Test
    public void getChatResponse_unknownCommand_isError() {
        ChatResponse response = sal.getChatResponse("blah");
        assertTrue(response.isError());
        assertEquals("Command not recognised.", response.getMessage());
    }

    @Test
    public void getResponse_invalidTodo_returnsFormatHint() {
        assertEquals("Correct format: todo <task name>", sal.getResponse("todo"));
    }

    @Test
    public void getResponse_bye_setsExitFlag() {
        String response = sal.getResponse("bye");
        assertEquals("Bye. Hope to see you again soon!", response);
        assertTrue(sal.isExit());
    }

    @Test
    public void getResponse_deadline_addsTask() {
        String response = sal.getResponse("deadline return book /by 2019-10-15");
        assertTrue(response.contains("Got it. I've added this task:"));
        assertTrue(response.contains("[D][ ] return book"));
        assertTrue(response.contains("(by:"));
        assertTrue(response.contains("Now you have 1 tasks in the list."));
    }

    @Test
    public void getResponse_event_addsTask() {
        String response = sal.getResponse("event meeting /from 2019-10-15 /to 2019-10-16");
        assertTrue(response.contains("Got it. I've added this task:"));
        assertTrue(response.contains("[E][ ] meeting"));
        assertTrue(response.contains("(from:"));
        assertTrue(response.contains(" to:"));
        assertTrue(response.contains("Now you have 1 tasks in the list."));
    }

    @Test
    public void getResponse_deadlineAndEvent_persistAcrossReload() {
        sal.getResponse("deadline return book /by 2019-10-15");
        sal.getResponse("event meeting /from 2/12/2019 1400 /to 2/12/2019 1600");
        Sal reloaded = new Sal(tempDir.resolve("sal.txt").toString());
        String list = reloaded.getResponse("list");
        assertTrue(list.contains("[D][ ] return book"));
        assertTrue(list.contains("[E][ ] meeting"));
    }

    @Test
    public void getResponse_emptyOrWhitespaceInput_returnsError() {
        assertEquals("Command not recognised.", sal.getResponse(""));
        assertEquals("Command not recognised.", sal.getResponse("   "));
    }

    @Test
    public void getResponse_outOfRangeIndex_returnsError() {
        sal.getResponse("todo read book");
        assertTrue(sal.getResponse("mark 2").contains("out of bounds"));
        assertTrue(sal.getResponse("unmark 0").contains("out of bounds"));
        assertTrue(sal.getResponse("delete 5").contains("out of bounds"));
        assertTrue(sal.getResponse("tag 2 fun").contains("out of bounds"));
        assertTrue(sal.getResponse("list").contains("1.[T][ ] read book"));
    }

    @Test
    public void constructor_unreadableDataFile_startsWithEmptyList() throws Exception {
        Path notAFile = tempDir.resolve("not-a-file");
        Files.createDirectory(notAFile);
        Sal broken = new Sal(notAFile.toString());
        assertTrue(broken.getResponse("list").endsWith("Here are the tasks in your list:"));
    }

    @Test
    public void getResponse_unwritableDataFile_returnsSaveError() throws Exception {
        Path notAFile = tempDir.resolve("not-a-file");
        Files.createDirectory(notAFile);
        Sal broken = new Sal(notAFile.toString());
        ChatResponse response = broken.getChatResponse("todo read book");
        assertTrue(response.isError());
        assertEquals("Could not save tasks to disk.", response.getMessage());
    }
}
