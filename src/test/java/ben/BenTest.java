package ben;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Integration tests for {@link Ben#getResponse(String)}, exercising full
 * command lines the way the GUI does. Each test builds its own {@link Ben}
 * against a scratch data file (via {@code @TempDir}) so tests cannot see
 * each other's tasks or touch the real {@code data/ben.txt}.
 */
class BenTest {
    @TempDir
    Path tempDir;

    private Ben newBen() {
        return new Ben(tempDir.resolve("tasks.txt").toString());
    }

    @Test
    void getResponse_bye_returnsFarewellWithoutTouchingTaskList() {
        assertEquals("Bye. Hope to see you again soon!", newBen().getResponse("bye"));
    }

    @Test
    void getResponse_unknownCommand_returnsErrorMessage() {
        String reply = newBen().getResponse("frobnicate");
        assertTrue(reply.startsWith("OOPS!!!"), reply);
    }

    @Test
    void getResponse_addTodoThenList_showsTheNewTask() {
        Ben ben = newBen();
        ben.getResponse("todo read book");
        String reply = ben.getResponse("list");
        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book", reply);
    }

    @Test
    void getResponse_addDeadline_showsDateInConfirmation() {
        String reply = newBen().getResponse("deadline return book /by 2019-12-02");
        assertEquals("Got it. I've added this task:\n"
                + "  [D][ ] return book (by: Dec 2 2019)\n"
                + "Now you have 1 task in the list.", reply);
    }

    @Test
    void getResponse_addEvent_showsFromAndToInConfirmation() {
        String reply = newBen().getResponse("event trip /from Mon /to Fri");
        assertEquals("Got it. I've added this task:\n"
                + "  [E][ ] trip (from: Mon to: Fri)\n"
                + "Now you have 1 task in the list.", reply);
    }

    @Test
    void getResponse_emptyTodoDescription_returnsError() {
        String reply = newBen().getResponse("todo");
        assertTrue(reply.contains("description of a todo cannot be empty"), reply);
    }

    @Test
    void getResponse_duplicateTodo_returnsErrorAndKeepsOneCopy() {
        Ben ben = newBen();
        ben.getResponse("todo read book");
        String reply = ben.getResponse("todo read book");
        assertTrue(reply.contains("already in your list"), reply);
        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book", ben.getResponse("list"));
    }

    @Test
    void getResponse_markThenUnmark_toggleStatusIcon() {
        Ben ben = newBen();
        ben.getResponse("todo read book");
        assertTrue(ben.getResponse("mark 1").contains("[T][X] read book"));
        assertTrue(ben.getResponse("unmark 1").contains("[T][ ] read book"));
    }

    @Test
    void getResponse_markInvalidIndex_returnsError() {
        String reply = newBen().getResponse("mark 5");
        assertTrue(reply.startsWith("OOPS!!!"), reply);
    }

    @Test
    void getResponse_delete_removesTaskAndReportsNewCount() {
        Ben ben = newBen();
        ben.getResponse("todo a");
        ben.getResponse("todo b");
        String reply = ben.getResponse("delete 1");
        assertEquals("Noted. I've removed this task:\n  [T][ ] a\nNow you have 1 task in the list.", reply);
        assertEquals("Here are the tasks in your list:\n1.[T][ ] b", ben.getResponse("list"));
    }

    @Test
    void getResponse_find_returnsOnlyMatchingTasks() {
        Ben ben = newBen();
        ben.getResponse("todo read book");
        ben.getResponse("todo buy milk");
        String reply = ben.getResponse("find book");
        assertEquals("Here are the matching tasks in your list:\n1.[T][ ] read book", reply);
    }

    @Test
    void getResponse_findNoMatches_returnsEmptyMessage() {
        Ben ben = newBen();
        ben.getResponse("todo buy milk");
        assertEquals("Here are the matching tasks in your list:\n(no matching tasks)",
                ben.getResponse("find book"));
    }

    @Test
    void getResponse_findWithoutKeyword_returnsError() {
        String reply = newBen().getResponse("find");
        assertTrue(reply.startsWith("OOPS!!!"), reply);
    }

    @Test
    void getResponse_tagExistingTask_appendsTagsToDisplay() {
        Ben ben = newBen();
        ben.getResponse("todo read book");
        String reply = ben.getResponse("tag 1 urgent #errand");
        assertTrue(reply.contains("#urgent #errand"), reply);
    }

    @Test
    void getResponse_addTaskWithHashTagInDescription_extractsTagAndStripsItFromText() {
        Ben ben = newBen();
        ben.getResponse("todo read book #urgent");
        String reply = ben.getResponse("list");
        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book #urgent", reply);
    }

    @Test
    void newBenInstance_reloadsTasksPreviouslySavedByAnotherInstance() {
        Path file = tempDir.resolve("tasks.txt");
        new Ben(file.toString()).getResponse("todo read book");

        Ben reloaded = new Ben(file.toString());
        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book", reloaded.getResponse("list"));
    }
}
