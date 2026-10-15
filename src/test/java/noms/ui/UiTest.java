package noms.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import noms.task.Deadline;
import noms.task.Task;
import noms.task.ToDo;

/** Tests console input, output, and error-state behavior provided by {@link Ui}. */
public class UiTest {
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    private ByteArrayOutputStream output;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        setInput("");
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        ui = new Ui();
    }

    @AfterEach
    public void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    public void hasNextCommand_inputAvailable_returnsTrue() {
        setInput("list\n");
        Ui inputUi = new Ui();

        assertTrue(inputUi.hasNextCommand());
        inputUi.close();
    }

    @Test
    public void hasNextCommand_endOfInput_returnsFalse() {
        assertFalse(ui.hasNextCommand());
    }

    @Test
    public void readCommand_multipleLines_returnsLinesInOrder() {
        setInput("todo read book\nlist\n");
        Ui inputUi = new Ui();

        assertTrue(inputUi.hasNextCommand());
        assertEquals("todo read book", inputUi.readCommand());
        assertEquals("list", inputUi.readCommand());
        assertFalse(inputUi.hasNextCommand());
        inputUi.close();
    }

    @Test
    public void close_closedUi_rejectsFurtherReads() {
        ui.close();

        assertThrows(IllegalStateException.class, ui::hasNextCommand);
    }

    @Test
    public void showWelcome_printsBannerGreetingAndDividers() {
        ui.showWelcome();

        String printed = printedOutput();
        assertTrue(printed.contains("_   _  ___  __  __  ____"));
        assertTrue(printed.contains("Hi! I'm Noms, your hungry little task monster."));
        assertTrue(printed.contains("____________________________________________________________"));
    }

    @Test
    public void showTaskList_emptyList_printsEmptyMessage() {
        ui.showTaskList(List.of());

        assertTrue(printedOutput().contains("Noms's menu is empty"));
    }

    @Test
    public void showTaskList_multipleTasks_printsNumberedTasksInOrder() {
        ui.showTaskList(List.of(new ToDo("read book"), new ToDo("buy milk")));

        String printed = printedOutput();
        assertTrue(printed.indexOf("1.[T][ ] read book") < printed.indexOf("2.[T][ ] buy milk"));
    }

    @Test
    public void showTasksOn_matchingTasks_printsDateAndNumberedMatches() {
        LocalDate date = LocalDate.of(2026, 9, 20);
        Task deadline = new Deadline("submit report", date);

        ui.showTasksOn(date, List.of(deadline));

        String printed = printedOutput();
        assertTrue(printed.contains("Tasks on Sep 20 2026:"));
        assertTrue(printed.contains("1. [D][ ] submit report"));
        assertFalse(printed.contains("nothing on the menu that day"));
    }

    @Test
    public void showTasksOn_noMatches_printsEmptyDayMessage() {
        ui.showTasksOn(LocalDate.of(2026, 9, 20), List.of());

        assertTrue(printedOutput().contains("nothing on the menu that day"));
    }

    @Test
    public void showMatchingTasks_matchesFound_printsKeywordAndMatches() {
        ui.showMatchingTasks("book", List.of(new ToDo("read book")));

        String printed = printedOutput();
        assertTrue(printed.contains("matching tasks for \"book\""));
        assertTrue(printed.contains("1. [T][ ] read book"));
    }

    @Test
    public void showMatchingTasks_noMatches_printsNotFoundMessage() {
        ui.showMatchingTasks("holiday", List.of());

        String printed = printedOutput();
        assertTrue(printed.contains("no tasks matching \"holiday\""));
        assertTrue(printed.contains("Nothing on the menu to nibble on"));
    }

    @Test
    public void showTaskAdded_oneTask_usesSingularNoun() {
        ui.showTaskAdded(new ToDo("read book"), 1);

        assertTrue(printedOutput().contains("1 task."));
    }

    @Test
    public void showTaskAdded_multipleTasks_usesPluralNoun() {
        ui.showTaskAdded(new ToDo("read book"), 2);

        assertTrue(printedOutput().contains("2 tasks."));
    }

    @Test
    public void showTaskDeleted_zeroTasks_usesPluralNoun() {
        ui.showTaskDeleted(new ToDo("read book"), 0);

        assertTrue(printedOutput().contains("0 tasks."));
    }

    @Test
    public void resetErrorState_afterError_clearsErrorState() {
        ui.showError("something went wrong");

        assertTrue(ui.hasShownError());
        assertTrue(printedOutput().contains("Oops! something went wrong"));

        ui.resetErrorState();

        assertFalse(ui.hasShownError());
    }

    private void setInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }

    private String printedOutput() {
        return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
