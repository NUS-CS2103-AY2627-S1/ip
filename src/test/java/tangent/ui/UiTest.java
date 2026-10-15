package tangent.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import tangent.task.TaskList;
import tangent.task.ToDo;

public class UiTest {
    @Test
    public void showGuiWelcome_displaysGreetingWithoutBanner() {
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.showGuiWelcome();

        assertEquals(List.of("good morning/afternoon/evening ^-^\nwhat do you want me to do?"), messages);
    }

    @Test
    public void showTaskStatusChanged_batchDisplaysSummaryAndTasks() {
        ToDo first = new ToDo("first");
        ToDo second = new ToDo("second");
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.showTaskStatusChanged(true, List.of(first, second));

        assertEquals(List.of("i've marked 2 tasks as done!", "[T][ ] first", "[T][ ] second"), messages);
    }

    @Test
    public void showTasksDeleted_batchDisplaysTasksAndRemainingCount() {
        ToDo first = new ToDo("first");
        ToDo second = new ToDo("second");
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.showTasksDeleted(List.of(first, second), new TaskList(new ToDo("remaining")));

        assertEquals(List.of("got it! i've removed these tasks:", "[T][ ] first", "[T][ ] second",
                "removed 2 tasks, 1 task(s) remaining!"), messages);
    }

    @Test
    public void showWelcome_outputsDividerBannerAndGreeting() {
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.showWelcome();

        assertEquals(4, messages.size());
        assertEquals("____________________________________________________________", messages.get(0));
        assertEquals("____________________________________________________________", messages.get(3));
        assertEquals("good morning/afternoon/evening ^-^ I'm TANGENT.\nwhat do you want me to do?",
                messages.get(2));
        assertTrue(messages.get(1).contains("████████╗"));
    }

    @Test
    public void readCommand_trimsInput() {
        Ui ui = new Ui(_ -> { });

        assertEquals("todo read book", ui.readCommand(new Scanner("  todo read book  \n")));
    }

    @Test
    public void showDividerAndGoodbye_outputExpectedMessages() {
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.showDivider();
        ui.showGoodbye();

        List<String> expected = List.of(
                "____________________________________________________________", "bye o/ hope to see you again soon");
        assertEquals(expected, messages);
    }

    @Test
    public void showError_usesErrorHandlerOnly() {
        List<String> output = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Ui ui = new Ui(output::add, errors::add);

        ui.showError("bad command");

        assertEquals(List.of("bad command"), errors);
        assertEquals(List.of(), output);
    }

    @Test
    public void showTaskList_emptyListDisplaysEmptyMessage() {
        List<String> messages = new ArrayList<>();

        new Ui(messages::add).showTaskList(new TaskList());

        assertEquals(List.of("no tasks yet!"), messages);
    }

    @Test
    public void showTaskList_nonEmptyListDisplaysOneBasedIndexes() {
        List<String> messages = new ArrayList<>();
        TaskList tasks = new TaskList(new ToDo("first"), new ToDo("second"));

        new Ui(messages::add).showTaskList(tasks);

        assertEquals(List.of("1. [T][ ] first", "2. [T][ ] second"), messages);
    }

    @Test
    public void showCurrentTasks_displaysHeadingBeforeTasks() {
        List<String> messages = new ArrayList<>();
        TaskList tasks = new TaskList(new ToDo("first"));

        new Ui(messages::add).showCurrentTasks(tasks);

        assertEquals(List.of("hello! here are your current tasks:", "1. [T][ ] first"), messages);
    }

    @Test
    public void showCurrentTasks_emptyListDisplaysOnlyEmptyMessage() {
        List<String> messages = new ArrayList<>();

        new Ui(messages::add).showCurrentTasks(new TaskList());

        assertEquals(List.of("no tasks yet!"), messages);
    }

    @Test
    public void showTaskStatusChanged_singularAndEmptyListsUseCorrectSummary() {
        List<String> oneTaskMessages = new ArrayList<>();
        ToDo task = new ToDo("first");
        new Ui(oneTaskMessages::add).showTaskStatusChanged(false, List.of(task));

        List<String> noTaskMessages = new ArrayList<>();
        new Ui(noTaskMessages::add).showTaskStatusChanged(true, List.of());

        assertEquals(List.of("i've marked 1 task as undone!", "[T][ ] first"), oneTaskMessages);
        assertEquals(List.of("i've marked 0 tasks as done!"), noTaskMessages);
    }

    @Test
    public void showStatusAlreadyApplied_usesRequestedStatus() {
        List<String> markMessages = new ArrayList<>();
        List<String> unmarkMessages = new ArrayList<>();

        new Ui(markMessages::add).showStatusAlreadyApplied(true);
        new Ui(unmarkMessages::add).showStatusAlreadyApplied(false);

        assertEquals(List.of("all selected tasks are already marked as done!"), markMessages);
        assertEquals(List.of("all selected tasks are already marked as undone!"), unmarkMessages);
    }

    @Test
    public void showTaskStatusChanged_displaysUnchangedTasksSeparately() {
        ToDo changedTask = new ToDo("changed");
        ToDo unchangedTask = new ToDo("unchanged");
        List<String> messages = new ArrayList<>();

        new Ui(messages::add).showTaskStatusChanged(true, List.of(changedTask), List.of(unchangedTask));

        assertEquals(List.of("i've marked 1 task as done!", "[T][ ] changed",
                "the following task(s) were already marked as done:", "[T][ ] unchanged"), messages);
    }

    @Test
    public void showSingleStatusAlreadyApplied_usesRequestedStatus() {
        List<String> markMessages = new ArrayList<>();
        List<String> unmarkMessages = new ArrayList<>();

        new Ui(markMessages::add).showSingleStatusAlreadyApplied(true);
        new Ui(unmarkMessages::add).showSingleStatusAlreadyApplied(false);

        assertEquals(List.of("the selected task is already marked as done!"), markMessages);
        assertEquals(List.of("the selected task is already marked as undone!"), unmarkMessages);
    }

    @Test
    public void showTasksDeleted_singularAndEmptyListsUseCorrectMessages() {
        ToDo task = new ToDo("first");
        List<String> oneTaskMessages = new ArrayList<>();
        new Ui(oneTaskMessages::add).showTasksDeleted(List.of(task), new TaskList());

        List<String> noTaskMessages = new ArrayList<>();
        new Ui(noTaskMessages::add).showTasksDeleted(List.of(), new TaskList());

        assertEquals(List.of("got it! i've removed this task:", "[T][ ] first",
                "you now have no tasks in the list!"), oneTaskMessages);
        assertEquals(List.of("got it! i've removed these tasks:", "removed 0 tasks, 0 task(s) remaining!"),
                noTaskMessages);
    }

    @Test
    public void showTaskAdded_formatsZeroOneAndManyTaskCounts() {
        List<String> oneTaskMessages = new ArrayList<>();
        ToDo task = new ToDo("first");
        new Ui(oneTaskMessages::add).showTaskAdded(task, new TaskList(task));

        List<String> manyTaskMessages = new ArrayList<>();
        TaskList tasks = new TaskList(task, new ToDo("second"));
        new Ui(manyTaskMessages::add).showTaskAdded(task, tasks);

        List<String> zeroTaskMessages = new ArrayList<>();
        new Ui(zeroTaskMessages::add).showTaskAdded(task, new TaskList());

        assertEquals("you now have 1 task in the list!", oneTaskMessages.get(2));
        assertEquals("you now have 2 tasks in the list!", manyTaskMessages.get(2));
        assertEquals("you now have no tasks in the list!", zeroTaskMessages.get(2));
    }

    @Test
    public void showMatchingTasks_nonEmptyAndEmptyListsUseCorrectMessages() {
        List<String> matchMessages = new ArrayList<>();
        new Ui(matchMessages::add).showMatchingTasks("book", new TaskList(new ToDo("read book")));

        List<String> noMatchMessages = new ArrayList<>();
        new Ui(noMatchMessages::add).showMatchingTasks("book", new TaskList());

        assertEquals(List.of("here are the tasks matching the keyword book:", "1. [T][ ] read book"), matchMessages);
        assertEquals(List.of("there were no matching tasks in the task list!"), noMatchMessages);
    }
}
