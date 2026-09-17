package quu.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import quu.task.TaskList;
import quu.task.ToDo;

/**
 * Tests the exact wording {@link Ui} returns for each kind of response.
 */
public class UiTest {
    private Ui ui;

    @BeforeEach
    public void setUp() {
        ui = new Ui();
    }

    @Test
    public void getGreeting_nameGiven_returnsNameAndPrompt() {
        assertEquals(String.format("Hello! I'm Quu.%nWhat can I do for you?"), ui.getGreeting("Quu"));
    }

    @Test
    public void getAdded_taskGiven_returnsTaskAndNewCount() {
        assertEquals(String.format("Got it. I've added this task:%n  [T][ ] read book%n"
                + "Now you have 1 task in the list."), ui.getAdded(new ToDo("read book"), 1));
    }

    @Test
    public void getAdded_severalTasks_countReadsAsPlural() {
        assertTrue(ui.getAdded(new ToDo("read book"), 2).endsWith("Now you have 2 tasks in the list."));
    }

    @Test
    public void getRemoved_oneTaskLeft_countReadsAsSingular() {
        assertTrue(ui.getRemoved(new ToDo("read book"), 1).endsWith("Now you have 1 task in the list."));
    }

    @Test
    public void getRemoved_taskGiven_returnsTaskAndNewCount() {
        assertEquals(String.format("Noted. I've removed this task:%n [T][ ] read book%n"
                + "Now you have 0 tasks in the list."), ui.getRemoved(new ToDo("read book"), 0));
    }

    @Test
    public void getMarked_incompleteTaskGiven_returnsTaskWithTickedBox() {
        ToDo task = new ToDo("read book");
        task.mark();

        assertEquals(String.format("Nice! I've marked this task as done:%n [T][X] read book"),
                ui.getMarked(task));
    }

    @Test
    public void getUnmarked_taskGiven_returnsTaskWithEmptyBox() {
        assertEquals(String.format("OK, I've marked this task as not done yet:%n [T][ ] read book"),
                ui.getUnmarked(new ToDo("read book")));
    }

    @Test
    public void getList_multipleTasks_returnsNumberedTasksInInsertionOrder() {
        TaskList taskList = new TaskList();
        taskList.addTask(new ToDo("first"));
        taskList.addTask(new ToDo("second"));

        assertEquals(String.format("Here are the tasks in your list:%n"
                + "1.[T][ ] first%n2.[T][ ] second"), ui.getList(taskList));
    }

    @Test
    public void getList_emptyList_saysTheListIsEmptyAndHowToAdd() {
        assertEquals("There are no tasks in your list yet. Add one with todo, deadline or event.",
                ui.getList(new TaskList()));
    }

    @Test
    public void getException_exceptionGiven_returnsMessageAlone() {
        assertEquals("something went wrong", ui.getException(new IllegalStateException("something went wrong")));
    }

    @Test
    public void getGoodbye_noArguments_returnsFarewell() {
        assertEquals("Bye. Hope to see you again soon!", ui.getGoodbye());
    }

    @Test
    public void getFound_multipleMatches_returnsMatchesNumberedFromOne() {
        TaskList foundTasks = new TaskList();
        foundTasks.addTask(new ToDo("read book"));
        foundTasks.addTask(new ToDo("return book"));

        assertEquals(String.format("Here are the matching tasks in your list:%n"
                + "1.[T][ ] read book%n2.[T][ ] return book"), ui.getFound(foundTasks));
    }

    @Test
    public void getFound_emptyList_saysNothingMatched() {
        assertEquals("No tasks match that keyword.", ui.getFound(new TaskList()));
    }
}
