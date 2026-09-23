package sallman;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import sallman.task.Deadline;
import sallman.task.Task;
import sallman.task.Todo;

/**
 * Tests the wording of every reply {@link Ui} gives, and how it reads commands
 * and prints to the console.
 * <p>
 * A silent {@code Ui} collects its replies instead of printing them, so most
 * tests read them back with {@link Ui#drainText()} rather than capturing the
 * console.
 */
public class UiTest {

    private static final InputStream ORIGINAL_IN = System.in;
    private static final PrintStream ORIGINAL_OUT = System.out;

    private final Ui ui = new Ui(false);

    @AfterEach
    public void restoreConsole() {
        System.setIn(ORIGINAL_IN);
        System.setOut(ORIGINAL_OUT);
    }

    /**
     * Returns the lines said since the last call, however the platform ends lines.
     *
     * @return the reply lines in order
     */
    private List<String> reply() {
        return ui.drainText().lines().toList();
    }

    @Test
    public void showWelcome_silentUi_greetingWithoutTheBanner() {
        ui.showWelcome("saLLMan");

        assertEquals(List.of("Hello! I'm saLLMan, your Large Language (task) Manager.",
                "How can I assist you with your tasks today?"), reply());
    }

    @Test
    public void showGoodbye_saysGoodbye() {
        ui.showGoodbye();

        assertEquals(List.of("Thank you for chatting with me! I hope this was helpful. Goodbye!"), reply());
    }

    @Test
    public void showError_messageFollowedByEachHint() {
        ui.showError(new SallmanException("Something is wrong.", "First hint.", "Second hint."));

        assertEquals(List.of("Something is wrong.", "First hint.", "Second hint."), reply());
    }

    @Test
    public void showSkippedLines_noneSkipped_saysNothing() {
        ui.showSkippedLines(List.of());

        assertEquals("", ui.drainText());
    }

    @Test
    public void showSkippedLines_oneSkipped_singularWording() {
        ui.showSkippedLines(List.of("line 2: the description is empty"));

        assertEquals(List.of("I noticed 1 unreadable line in your saved data, so I skipped it:",
                "line 2: the description is empty",
                "Everything else loaded perfectly! I'll drop the unreadable line",
                "the next time your list changes."), reply());
    }

    @Test
    public void showSkippedLines_severalSkipped_pluralWordingAndEveryLineListed() {
        ui.showSkippedLines(List.of("line 2: first problem", "line 5: second problem"));

        List<String> lines = reply();
        assertEquals("I noticed 2 unreadable lines in your saved data, so I skipped them:", lines.get(0));
        assertEquals("line 2: first problem", lines.get(1));
        assertEquals("line 5: second problem", lines.get(2));
        assertEquals("Everything else loaded perfectly! I'll drop the unreadable lines", lines.get(3));
    }

    @Test
    public void showAdded_oneTask_singularCount() {
        ui.showAdded(new Todo("read book"), 1);

        assertEquals(List.of("Certainly! What a wonderful task. I've added it:", "  [T][ ] read book",
                "You now have 1 task in your list."), reply());
    }

    @Test
    public void showRemoved_severalLeft_pluralCount() {
        ui.showRemoved(new Todo("read book"), 3);

        assertEquals(List.of("Of course! I've removed this task:", "  [T][ ] read book",
                "You now have 3 tasks in your list."), reply());
    }

    @Test
    public void showMarked_andShowUnmarked_confirmTheNewState() {
        Task task = new Todo("read book");
        task.markAsDone();
        ui.showMarked(task);
        assertEquals(List.of("Great job! I've marked this task as done:", "  [T][X] read book"), reply());

        task.markAsNotDone();
        ui.showUnmarked(task);
        assertEquals(List.of("No problem! I've marked this task as not done yet:", "  [T][ ] read book"), reply());
    }

    @Test
    public void showTagged_changesMade_confirmsAddingOrRemoving() {
        Task task = new Todo("read book");
        task.addTag("fun");

        ui.showTagged(task, true, true);
        assertEquals("Absolutely! I've tagged this task:", reply().get(0));

        ui.showTagged(task, false, true);
        assertEquals("Sure thing! I've removed those tags:", reply().get(0));
    }

    @Test
    public void showTagged_nothingChanged_saysWhyForEachDirection() {
        Task task = new Todo("read book");

        ui.showTagged(task, true, false);
        assertEquals("It looks like that task already has every tag you named:", reply().get(0));

        ui.showTagged(task, false, false);
        assertEquals("It looks like that task has none of the tags you named:", reply().get(0));
    }

    @Test
    public void showUndone_reportsTheRestoredCount() {
        ui.showUndone(2);

        assertEquals(List.of("I apologise for any confusion! I've put your list back the way it was.",
                "You now have 2 tasks in your list."), reply());
    }

    @Test
    public void showSorted_namesTheOrderAndNumbersTheTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("apple"));
        tasks.add(new Todo("zebra"));

        ui.showSorted(tasks, "name");

        assertEquals(List.of("Here's a carefully sorted overview of your list, by name:", "1.[T][ ] apple",
                "2.[T][ ] zebra"), reply());
    }

    @Test
    public void showTaskList_emptyList_saysSoInsteadOfABareHeading() {
        ui.showTaskList(new TaskList());

        assertEquals(List.of("Great question! Your list is currently empty.",
                "Would you like me to help you add a task? Try: todo read book"), reply());
    }

    @Test
    public void showTaskList_someTasks_numberedFromOne() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", LocalDate.of(2019, 10, 15)));

        ui.showTaskList(tasks);

        assertEquals(List.of("Great question! Here are the tasks in your list:", "1.[T][ ] read book",
                "2.[D][ ] return book (by: Oct 15 2019)"), reply());
    }

    @Test
    public void showMatchingTasks_noMatches_quotesTheKeyword() {
        ui.showMatchingTasks(new TaskList(), List.of(), "book");

        assertEquals(List.of("I searched thoroughly, but no tasks match \"book\"."), reply());
    }

    @Test
    public void showMatchingTasks_someMatches_numberedByTheirPlaceInTheWholeList() {
        // The number shown must be the one mark, delete and tag act on, or
        // "delete 1" after a search could remove a different task.
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"));
        Task match = new Todo("read book");
        tasks.add(match);

        ui.showMatchingTasks(tasks, List.of(match), "book");

        assertEquals(List.of("I found some tasks that match \"book\":", "2.[T][ ] read book"), reply());
    }

    @Test
    public void showTasksOn_nothingThatDay_namesTheDate() {
        ui.showTasksOn(new TaskList(), List.of(), LocalDate.of(2019, 10, 15));

        assertEquals(List.of("Great news! You have nothing on Oct 15 2019."), reply());
    }

    @Test
    public void showTasksOn_somethingThatDay_listsIt() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Task deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        tasks.add(deadline);

        ui.showTasksOn(tasks, List.of(deadline), LocalDate.of(2019, 10, 15));

        assertEquals(List.of("Here is everything you have on Oct 15 2019:",
                "2.[D][ ] return book (by: Oct 15 2019)"), reply());
    }

    @Test
    public void drainText_calledTwice_secondCallIsEmpty() {
        ui.showGoodbye();
        ui.drainText();

        assertEquals("", ui.drainText());
    }

    @Test
    public void say_printingUi_wrapsTheReplyInDividersOnTheConsole() {
        ByteArrayOutputStream console = new ByteArrayOutputStream();
        System.setOut(new PrintStream(console, true, StandardCharsets.UTF_8));

        new Ui().say("first line", "second line");

        List<String> printed = console.toString(StandardCharsets.UTF_8).lines().toList();
        assertTrue(printed.get(0).trim().matches("_{20,}"));
        assertEquals("     first line", printed.get(1));
        assertEquals("     second line", printed.get(2));
        assertTrue(printed.get(3).trim().matches("_{20,}"));
    }

    @Test
    public void showWelcome_printingUi_showsTheBannerFirst() {
        ByteArrayOutputStream console = new ByteArrayOutputStream();
        System.setOut(new PrintStream(console, true, StandardCharsets.UTF_8));

        new Ui(true).showWelcome("saLLMan");

        String printed = console.toString(StandardCharsets.UTF_8);
        assertTrue(printed.indexOf("|___/") < printed.indexOf("Hello! I'm saLLMan"));
    }

    @Test
    public void readCommand_surroundingSpaces_trimmedUntilInputRunsOut() {
        System.setIn(new ByteArrayInputStream("  list  \nbye\n".getBytes(StandardCharsets.UTF_8)));
        Ui reader = new Ui(false);

        assertTrue(reader.hasNextCommand());
        assertEquals("list", reader.readCommand());
        assertEquals("bye", reader.readCommand());
        assertFalse(reader.hasNextCommand());
        reader.close();
    }
}
