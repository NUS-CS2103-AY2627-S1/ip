package elora;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import elora.task.Task;
import elora.task.TaskList;
import elora.task.Todo;

class UiTest {

    private final Ui ui = new Ui();

    @Test
    void chatWelcomeMessage_doesNotContainAsciiLogo() {
        assertTrue(ui.chatWelcomeMessage().contains("Elora"));
        assertFalse(ui.chatWelcomeMessage().contains("_____"));
    }

    @Test
    void welcomeMessage_containsAsciiLogo() {
        assertTrue(ui.welcomeMessage().contains("_____"));
    }

    @Test
    void taskAddedMessage_includesTaskAndCount() {
        Task task = new Todo("read book");
        String message = ui.taskAddedMessage(task, 3);
        assertTrue(message.contains("read book"));
        assertTrue(message.contains("3 tasks"));
    }

    @Test
    void taskListMessage_multipleTasks_numbersFromOne() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));
        String message = ui.taskListMessage(tasks);
        assertTrue(message.contains("1.[T]"));
        assertTrue(message.contains("2.[T]"));
    }

    @Test
    void matchingTasksMessage_noMatches_saysNone() {
        String message = ui.matchingTasksMessage(new ArrayList<>());
        assertTrue(message.contains("No tasks match"));
    }

    @Test
    void tasksOnDateMessage_noMatches_saysNothing() {
        String message = ui.tasksOnDateMessage(LocalDate.parse("2019-10-15"), new ArrayList<>());
        assertTrue(message.contains("Nothing on your list"));
    }

    @Test
    void tasksOnDateMessage_withMatch_includesTaskAndFormattedDate() {
        List<Task> matches = Collections.singletonList(new Todo("read book"));
        String message = ui.tasksOnDateMessage(LocalDate.parse("2019-10-15"), matches);
        assertTrue(message.contains("Oct 15 2019"));
        assertTrue(message.contains("read book"));
    }

    @Test
    void corruptedLinesWarning_singleLine_usesSingularWording() {
        String message = ui.corruptedLinesWarning(Collections.singletonList("bad line"));
        assertTrue(message.contains("1 line"));
        assertTrue(message.contains("bad line"));
    }

    @Test
    void corruptedLinesWarning_multipleLines_usesPluralWording() {
        String message = ui.corruptedLinesWarning(List.of("bad line 1", "bad line 2"));
        assertTrue(message.contains("2 lines"));
    }

    @Test
    void goodbyeMessage_isNotBlank() {
        assertFalse(ui.goodbyeMessage().isBlank());
    }
}
