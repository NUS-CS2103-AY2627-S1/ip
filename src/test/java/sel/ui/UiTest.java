package sel.ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sel.exception.SelException;
import sel.task.Deadline;
import sel.task.Event;
import sel.task.Task;
import sel.task.TaskList;
import sel.task.ToDo;

/**
 * Tests the command-line interface by swapping {@code System.out} and
 * {@code System.in} for in-memory streams, so what Ui prints and reads can
 * be inspected without a terminal.
 */
public class UiTest {

    private ByteArrayOutputStream captured;
    private PrintStream originalOut;
    private InputStream originalIn;

    @BeforeEach
    public void redirectStreams() {
        originalOut = System.out;
        originalIn = System.in;
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private String printed() {
        return captured.toString(StandardCharsets.UTF_8);
    }

    /**
     * Creates a Ui reading from the given text. Ui wraps {@code System.in}
     * when it is constructed, so the stream has to be swapped first.
     *
     * @param input the lines the Ui should read.
     * @return a Ui reading from that text.
     */
    private Ui uiReading(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        return new Ui();
    }

    // ---------- reading input ----------

    @Test
    public void readCommand_returnsEachLineInTurn() {
        Ui ui = uiReading("todo read book\nlist\n");

        assertEquals("todo read book", ui.readCommand());
        assertEquals("list", ui.readCommand());
    }

    @Test
    public void readCommand_atEndOfInput_returnsNull() {
        Ui ui = uiReading("list\n");

        assertEquals("list", ui.readCommand());
        assertNull(ui.readCommand());
    }

    @Test
    public void readCommand_noInputAtAll_returnsNull() {
        assertNull(uiReading("").readCommand());
    }

    @Test
    public void readCommand_preservesWhitespaceForTheParserToTidyUp() {
        Ui ui = uiReading("   todo    read book   \n");

        assertEquals("   todo    read book   ", ui.readCommand());
    }

    @Test
    public void readCommand_blankLine_isReturnedAsAnEmptyString() {
        Ui ui = uiReading("\nlist\n");

        assertEquals("", ui.readCommand());
        assertEquals("list", ui.readCommand());
    }

    @Test
    public void close_doesNotThrow() {
        Ui ui = uiReading("list\n");

        assertDoesNotThrow(ui::close);
    }

    // ---------- greeting and farewell ----------

    @Test
    public void showWelcome_printsTheBannerAndGreeting() {
        uiReading("").showWelcome();

        assertTrue(printed().contains("Sup, I'm Sel."));
        // The banner is ASCII art spelling SEL; this is its bottom row.
        assertTrue(printed().contains("|____/|_____|_____|"), printed());
    }

    @Test
    public void showGoodbye_printsTheFarewell() {
        uiReading("").showGoodbye();

        assertTrue(printed().contains("Bye see ya later alligator."));
    }

    // ---------- errors ----------

    @Test
    public void showError_includesTheMessage() {
        uiReading("").showError("Bro, that task doesn't exist :(");

        assertTrue(printed().contains("Bro, that task doesn't exist :("));
    }

    @Test
    public void showError_multiLineMessage_isPrintedInFull() {
        uiReading("").showError("first line\nsecond line");

        assertTrue(printed().contains("first line"));
        assertTrue(printed().contains("second line"));
    }

    @Test
    public void showError_messageMatchesWhatSelExceptionCarries() {
        SelException exception = new SelException("something went wrong");

        uiReading("").showError(exception.getMessage());

        assertTrue(printed().contains("something went wrong"));
    }

    // ---------- listing tasks ----------

    @Test
    public void showTaskList_printsEveryTaskNumberedFromOne() throws SelException {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0)));
        tasks.add(new Event("meeting",
            LocalDateTime.of(2019, 12, 2, 14, 0), LocalDateTime.of(2019, 12, 2, 16, 0)));

        uiReading("").showTaskList(tasks);

        String output = printed();
        assertTrue(output.contains("1.[T][ ] read book"), output);
        assertTrue(output.contains("2.[D][ ] return book"), output);
        assertTrue(output.contains("3.[E][ ] meeting"), output);
    }

    @Test
    public void showTaskList_emptyList_stillPrintsTheHeaderWithoutAnyNumbers() {
        uiReading("").showTaskList(new TaskList());

        String output = printed();
        assertTrue(output.contains("anyway here it is"), output);
        assertEquals(0, output.lines().filter(line -> line.matches("^\\d+\\..*")).count());
    }

    @Test
    public void showTaskList_markedTask_showsItAsDone() {
        TaskList tasks = new TaskList();
        Task done = new ToDo("read book");
        done.mark();
        tasks.add(done);

        uiReading("").showTaskList(tasks);

        assertTrue(printed().contains("1.[T][X] read book"));
    }

    // ---------- confirmations ----------

    @Test
    public void showTaskMarked_showsTheTaskAsDone() {
        Task task = new ToDo("read book");
        task.mark();

        uiReading("").showTaskMarked(task);

        String output = printed();
        assertTrue(output.contains("Marked task as done"), output);
        assertTrue(output.contains("[T][X] read book"), output);
    }

    @Test
    public void showTaskUnmarked_showsTheTaskAsNotDone() {
        uiReading("").showTaskUnmarked(new ToDo("read book"));

        String output = printed();
        assertTrue(output.contains("Unmarked"), output);
        assertTrue(output.contains("[T][ ] read book"), output);
    }

    @Test
    public void showTaskDeleted_showsTheTaskAndTheRemainingCount() {
        uiReading("").showTaskDeleted(new ToDo("read book"), 4);

        String output = printed();
        assertTrue(output.contains("[T][ ] read book"), output);
        assertTrue(output.contains("4 task(s)"), output);
    }

    @Test
    public void showTaskAdded_showsTheTaskAndTheNewCount() {
        uiReading("").showTaskAdded(new ToDo("read book"), 1);

        String output = printed();
        assertTrue(output.contains("[T][ ] read book"), output);
        assertTrue(output.contains("1 task(s)"), output);
    }

    // ---------- search results ----------

    @Test
    public void showMatchingTasks_printsMatchesNumberedFromOne() {
        uiReading("").showMatchingTasks(List.of(new ToDo("read book"), new ToDo("return book")));

        String output = printed();
        assertTrue(output.contains("Here are the matching tasks"), output);
        assertTrue(output.contains("1.[T][ ] read book"), output);
        assertTrue(output.contains("2.[T][ ] return book"), output);
    }

    @Test
    public void showMatchingTasks_noMatches_saysSoInsteadOfPrintingAnEmptyList() {
        uiReading("").showMatchingTasks(List.of());

        String output = printed();
        assertTrue(output.contains("nothing in your list matches"), output);
        assertEquals(0, output.lines().filter(line -> line.matches("^\\d+\\..*")).count());
    }
}
