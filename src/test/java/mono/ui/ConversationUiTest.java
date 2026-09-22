package mono.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import mono.task.ToDo;

/**
 * Tests that conversation responses can be sent to a non-console destination.
 */
public class ConversationUiTest {
    @Test
    public void showGreeting_sendsOneCompleteResponse() {
        ArrayList<String> responses = new ArrayList<>();
        ConversationUi ui = new ConversationUi(responses::add);

        ui.showGreeting();

        assertEquals(1, responses.size());
        assertTrue(responses.get(0).contains("Hello! I'm Mono."));
    }

    @Test
    public void showTaskList_sendsTasksInOneResponse() {
        ArrayList<String> responses = new ArrayList<>();
        ConversationUi ui = new ConversationUi(responses::add);

        ui.showTaskList(java.util.List.of(new ToDo("read a book")));

        assertEquals(1, responses.size());
        assertTrue(responses.get(0).contains("1.[T][ ] read a book"));
    }

    @Test
    public void showExit_sendsFarewellResponse() {
        ArrayList<String> responses = new ArrayList<>();

        new ConversationUi(responses::add).showExit();

        assertEquals(1, responses.size());
        assertTrue(responses.get(0).contains("Bye. Hope to see you again soon!"));
    }

    @Test
    public void taskResponses_includeTaskAndUpdatedCount() {
        ArrayList<String> responses = new ArrayList<>();
        ConversationUi ui = new ConversationUi(responses::add);
        ToDo task = new ToDo("read a book");

        ui.showTaskAdded(task, 1);
        ui.showTaskDeleted(task, 0);
        ui.showTaskMarkedDone(task);
        ui.showTaskUnmarked(task);

        assertEquals(4, responses.size());
        assertTrue(responses.get(0).contains("Now you have 1 tasks in the list."));
        assertTrue(responses.get(1).contains("Now you have 0 tasks in the list."));
        assertTrue(responses.get(2).contains("marked this task as done"));
        assertTrue(responses.get(3).contains("not done yet"));
    }

    @Test
    public void showTaskList_emptyList_stillFramesResponse() {
        ArrayList<String> responses = new ArrayList<>();

        new ConversationUi(responses::add).showTaskList(List.of());

        assertEquals(1, responses.size());
        assertTrue(responses.get(0).contains("Here are the tasks in your list:"));
        assertTrue(responses.get(0).endsWith("____________________________________________________________\n"));
    }

    @Test
    public void constructor_nullOutput_throwsNullPointerException() {
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () ->
                new ConversationUi(null));
    }
}
