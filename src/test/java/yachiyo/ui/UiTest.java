package yachiyo.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import yachiyo.task.Task;
import yachiyo.task.ToDo;

public class UiTest {
    private static final String GREETING_SUFFIX =
            "! Yachiyo here!\nWhat shall we accomplish today?";

    @Test
    public void getGreeting_morning_ohayouReturned() {
        assertEquals("Ohayou" + GREETING_SUFFIX, Ui.getGreeting(LocalTime.of(11, 59)));
    }

    @Test
    public void getGreeting_afternoon_konnichiwaReturned() {
        assertEquals("Konnichiwa" + GREETING_SUFFIX, Ui.getGreeting(LocalTime.NOON));
        assertEquals("Konnichiwa" + GREETING_SUFFIX, Ui.getGreeting(LocalTime.of(17, 59)));
    }

    @Test
    public void getGreeting_evening_konbanwaReturned() {
        assertEquals("Konbanwa" + GREETING_SUFFIX, Ui.getGreeting(LocalTime.of(18, 0)));
    }

    @Test
    public void showTaskMarked_noTasksRemaining_celebrationReturned() {
        Task task = new ToDo("Read book");
        task.markAsDone();

        String output = captureOutput(ui -> ui.showTaskMarked(task, 0));

        assertEquals(lines(
                "Woohoo! Another task is complete:",
                "- [T][X] Read book",
                "Yayyy! Everything in our lineup is complete!🥳🎉",
                "Good job!"
        ), output);
    }

    @Test
    public void showTaskList_emptyList_emptyMessageReturned() {
        String output = captureOutput(ui -> ui.showTaskList(List.of()));

        assertEquals(lines(
                "Our lineup is empty for now. What shall we take on next?"
        ), output);
    }

    @Test
    public void showTaskList_tasksPresent_numberedListReturned() {
        Task completedTask = new ToDo("Return book");
        completedTask.markAsDone();

        String output = captureOutput(ui -> ui.showTaskList(List.of(
                new ToDo("Read book"), completedTask)));

        assertEquals(lines(
                "Here's everything in our lineup:",
                "1. [T][ ] Read book",
                "2. [T][X] Return book"
        ), output);
    }

    @Test
    public void showTaskAdded_singularAndPluralCounts_correctGrammarReturned() {
        Task task = new ToDo("Read book");

        String singularOutput = captureOutput(ui -> ui.showTaskAdded(task, 1));
        String pluralOutput = captureOutput(ui -> ui.showTaskAdded(task, 2));

        assertEquals(lines(
                "All right, I've added this to our lineup:",
                "- [T][ ] Read book",
                "And with that, our lineup now has 1 task in total!"
        ), singularOutput);
        assertEquals(lines(
                "All right, I've added this to our lineup:",
                "- [T][ ] Read book",
                "And with that, our lineup now has 2 tasks in total!"
        ), pluralOutput);
    }

    @Test
    public void showTaskMarked_tasksRemaining_remainingCountReturned() {
        Task task = new ToDo("Read book");
        task.markAsDone();

        String output = captureOutput(ui -> ui.showTaskMarked(task, 2));

        assertEquals(lines(
                "Woohoo! Another task is complete:",
                "- [T][X] Read book",
                "And with that, our lineup now has 2 tasks remaining!"
        ), output);
    }

    @Test
    public void showTaskUnmarked_taskProvided_remainingCountReturned() {
        Task task = new ToDo("Read book");

        String output = captureOutput(ui -> ui.showTaskUnmarked(task, 1));

        assertEquals(lines(
                "Not quite finished? No worries, I've marked it as not done:",
                "- [T][ ] Read book",
                "Our lineup now has 1 task remaining!"
        ), output);
    }

    @Test
    public void showTaskDeleted_tasksRemain_totalCountReturned() {
        Task task = new ToDo("Read book");

        String output = captureOutput(ui -> ui.showTaskDeleted(task, 2));

        assertEquals(lines(
                "All right, I've taken this task out of our lineup:",
                "- [T][ ] Read book",
                "And with that, our lineup now has 2 tasks in total!"
        ), output);
    }

    @Test
    public void showTaskDeleted_noTasksRemain_emptyMessageReturned() {
        Task task = new ToDo("Read book");

        String output = captureOutput(ui -> ui.showTaskDeleted(task, 0));

        assertEquals(lines(
                "All right, I've taken this task out of our lineup:",
                "- [T][ ] Read book",
                "And with that, our lineup is empty again. What shall we take on next?"
        ), output);
    }

    @Test
    public void showFindMessages_keywordProvided_keywordIncluded() {
        String matchingOutput = captureOutput(
                ui -> ui.showMatchingTasksHeader("book"));
        String noMatchOutput = captureOutput(
                ui -> ui.showNoMatchingTasks("book"));

        assertEquals(lines(
                "Here are the tasks in our lineup matching \"book\":"
        ), matchingOutput);
        assertEquals(lines(
                "I couldn't find any tasks matching \"book\"."
        ), noMatchOutput);
    }

    @Test
    public void showDateMessages_dateProvided_formattedDateIncluded() {
        LocalDate date = LocalDate.of(2026, 8, 20);

        String matchingOutput = captureOutput(ui -> ui.showTasksOnDateHeader(date));
        String noMatchOutput = captureOutput(ui -> ui.showNoTasksOnDate(date));

        assertEquals(lines(
                "Here are the deadlines and events on Aug 20 2026:"
        ), matchingOutput);
        assertEquals(lines(
                "There are no deadlines or events on Aug 20 2026."
        ), noMatchOutput);
    }

    @Test
    public void showTaskStateMessages_taskProvided_correctMessagesReturned() {
        Task task = new ToDo("Read book");
        String editedOutput = captureOutput(ui -> ui.showTaskEdited(task));
        String alreadyUnmarkedOutput = captureOutput(ui -> ui.showAlreadyUnmarked(task));
        task.markAsDone();
        String alreadyMarkedOutput = captureOutput(ui -> ui.showAlreadyMarked(task));

        assertEquals(lines(
                "All right, I've updated this task:",
                "- [T][ ] Read book"
        ), editedOutput);
        assertEquals(lines(
                "No changes needed-this task is already waiting in our lineup!",
                "- [T][ ] Read book"
        ), alreadyUnmarkedOutput);
        assertEquals(lines(
                "This task is already shining as complete!",
                "- [T][X] Read book"
        ), alreadyMarkedOutput);
    }

    @Test
    public void showSimpleMessages_messageProvided_messageReturned() {
        String errorOutput = captureOutput(ui -> ui.showError("Something went wrong"));
        String exitOutput = captureOutput(Ui::showExit);

        assertEquals(lines("Something went wrong"), errorOutput);
        assertEquals(lines("Until we meet again. Take care!~"), exitOutput);
    }

    /**
     * Captures output written by one UI display operation.
     *
     * @param operation display operation to invoke.
     * @return complete output including its trailing line separator.
     */
    private static String captureOutput(Consumer<Ui> operation) {
        StringWriter output = new StringWriter();
        try (Ui ui = new Ui(output)) {
            operation.accept(ui);
        }
        return output.toString();
    }

    /**
     * Joins expected output lines using the platform line separator.
     *
     * @param expectedLines lines expected from the UI.
     * @return joined lines including the final line separator.
     */
    private static String lines(String... expectedLines) {
        return String.join(System.lineSeparator(), expectedLines)
                + System.lineSeparator();
    }
}
