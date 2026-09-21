package janet.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import janet.task.Event;
import janet.task.Task;
import janet.task.Todo;

class UiTest {
    private ByteArrayOutputStream outputBytes;
    private Ui ui;

    @BeforeEach
    void setUp() {
        outputBytes = new ByteArrayOutputStream();
        ui = new Ui(new PrintStream(outputBytes, true, StandardCharsets.UTF_8));
    }

    @Test
    void showTaskList_emptyAndNonEmptyLists_formatsBothStates() {
        ui.showTaskList(List.of());
        assertEquals(" Your task list is empty. Everything is wonderfully under control.\n", getOutput());

        outputBytes.reset();
        Todo completedTodo = new Todo("read book");
        completedTodo.markAsDone();
        ui.showTaskList(List.of(completedTodo, new Event("meeting", "14:00", "16:00")));

        assertEquals("""
                 Absolutely! Here is everything on your list:
                 1.[T][X] read book
                 2.[E][ ] meeting (from: 14:00 to: 16:00)
                """, getOutput());
    }

    @Test
    void showMatchingTasks_emptyAndNonEmptyLists_formatsBothStates() {
        ui.showMatchingTasks(List.of());
        assertEquals(" I checked the whole list. No matching tasks found.\n", getOutput());

        outputBytes.reset();
        ui.showMatchingTasks(List.of(new Todo("read book")));
        assertEquals("""
                 I found these matching tasks:
                 1.[T][ ] read book
                """, getOutput());
    }

    @Test
    void showTaskChanges_formatsConfirmationsAndCounts() {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");

        ui.showTaskAdded(firstTask, 1);
        firstTask.markAsDone();
        ui.showTaskMarked(firstTask);
        firstTask.markAsUndone();
        ui.showTaskUnmarked(firstTask);
        ui.showTaskDeleted(firstTask, 1);
        ui.showTasksDeleted(List.of(firstTask, secondTask), 0);

        assertEquals("""
                 Done and done! I added this task:
                   [T][ ] first
                 You now have 1 task.
                 Excellent! This task is now complete:
                   [T][X] first
                 No problem! This task is back in progress:
                   [T][ ] first
                 Done! I removed this task:
                   [T][ ] first
                 You now have 1 task.
                 Done! I removed these tasks:
                   [T][ ] first
                   [T][ ] second
                 You now have 0 tasks.
                """, getOutput());
    }

    @Test
    void showErrorLineAndGoodbye_formatsUtilityMessages() {
        ui.showError("Something went wrong.");
        ui.showLine();
        ui.showGoodbye();

        assertEquals("""
                 Something went wrong.
                ____________________________________________________________
                ____________________________________________________________
                 All set! I'll be right here if you need me. Bye!
                """, getOutput());
    }

    @Test
    void showWelcome_containsIdentityAndPersonality() {
        ui.showWelcome();

        String output = getOutput();
        assertTrue(output.contains("Hi there! I'm Janet"));
        assertTrue(output.contains("not a robot"));
    }

    private String getOutput() {
        return outputBytes.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
