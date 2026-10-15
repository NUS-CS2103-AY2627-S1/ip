package peter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Ui}.
 */
public class UiTest {
    private final Ui ui = new Ui();
    private InputStream originalIn;

    @AfterEach
    public void restoreSystemIn() {
        if (originalIn != null) {
            System.setIn(originalIn);
            originalIn = null;
        }
    }

    @Test
    public void showWelcome_containsGreeting() {
        assertTrue(ui.showWelcome().contains("Your friendly neighbourhood Peter"));
    }

    @Test
    public void showGoodbye_returnsFarewellMessage() {
        assertEquals("Catch you on the web-swing! Later, pal.", ui.showGoodbye());
    }

    @Test
    public void showLoadingResult_zeroTasks_returnsEmptyMessage() {
        assertEquals("Web's empty - starting fresh with a clean strand.", ui.showLoadingResult(0));
    }

    @Test
    public void showLoadingResult_nonZeroTasks_includesCount() {
        assertTrue(ui.showLoadingResult(3).contains("3"));
    }

    @Test
    public void showLoadingError_returnsErrorMessage() {
        assertEquals("My spidey-sense says that save file's a lost cause - starting fresh.", ui.showLoadingError());
    }

    @Test
    public void showTaskList_emptyList_returnsHeaderOnly() {
        assertEquals("Here's everything currently stuck to the web:", ui.showTaskList(List.of()));
    }

    @Test
    public void showTaskList_multipleTasks_numbersEachOne() {
        List<Task> tasks = List.of(new Todo("read book"), new Todo("return book"));

        String message = ui.showTaskList(tasks);

        assertEquals("Here's everything currently stuck to the web:\n"
                + "1.[T][ ] read book\n"
                + "2.[T][ ] return book", message);
    }

    @Test
    public void showTaskMarked_includesTaskText() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertTrue(ui.showTaskMarked(todo).contains("[T][X] read book"));
    }

    @Test
    public void showTaskUnmarked_includesTaskText() {
        assertTrue(ui.showTaskUnmarked(new Todo("read book")).contains("[T][ ] read book"));
    }

    @Test
    public void showTaskRemoved_includesTaskTextAndCount() {
        String message = ui.showTaskRemoved(new Todo("read book"), 2);
        assertTrue(message.contains("[T][ ] read book"));
        assertTrue(message.contains("2 thing(s)"));
    }

    @Test
    public void showTaskAdded_includesTaskTextAndCount() {
        String message = ui.showTaskAdded(new Todo("read book"), 1);
        assertTrue(message.contains("[T][ ] read book"));
        assertTrue(message.contains("1 thing(s)"));
    }

    @Test
    public void showSchedule_emptyList_returnsQuietDayMessage() {
        assertEquals("Quiet day - nothing on the web for then.",
                ui.showSchedule(List.of(), LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void showSchedule_nonEmptyList_listsEachTaskOnItsOwnLine() {
        List<Task> schedule = List.of(new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0)));

        String message = ui.showSchedule(schedule, LocalDate.of(2019, 12, 2));

        assertEquals("Here's what's on the web for that day, deadlines first:\n"
                + "  [D][ ] return book (by: Dec 02 2019, 6:00PM)", message);
    }

    @Test
    public void showFoundTasks_emptyList_returnsNoMatchMessage() {
        assertEquals("Swung all over the city and came up empty on that search.", ui.showFoundTasks(List.of()));
    }

    @Test
    public void showFoundTasks_nonEmptyList_numbersEachMatch() {
        List<Task> matches = List.of(new Todo("read book"));

        String message = ui.showFoundTasks(matches);

        assertEquals("Here's what stuck to the web when I searched for that:\n1.[T][ ] read book", message);
    }

    @Test
    public void readCommand_trimsSurroundingWhitespace() {
        originalIn = System.in;
        System.setIn(new ByteArrayInputStream("  todo read book  \n".getBytes(StandardCharsets.UTF_8)));

        assertEquals("todo read book", new Ui().readCommand());
    }

    @Test
    public void close_doesNotThrow() {
        originalIn = System.in;
        System.setIn(new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)));

        assertDoesNotThrow(() -> new Ui().close());
    }
}
