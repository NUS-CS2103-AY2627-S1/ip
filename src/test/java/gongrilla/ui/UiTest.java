package gongrilla.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import gongrilla.task.Todo;

/** Tests that multiline responses retain their text and line separators. */
class UiTest {
    @Test
    void readCommand_mixedLineEndingsAndWhitespace_preservesContentUntilEndOfInput() {
        byte[] commands = "  todo read  \r\n\nhelp\n".getBytes(StandardCharsets.UTF_8);
        Ui ui = new Ui(new ByteArrayInputStream(commands), new PrintStream(new ByteArrayOutputStream()));
        assertTrue(ui.hasNextCommand());
        assertEquals("todo read", ui.readCommand());
        assertTrue(ui.hasNextCommand());
        assertEquals("", ui.readCommand());
        assertEquals("help", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    @Test
    void showPersistenceErrors_explainsFailureAndUnchangedTaskList() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(InputStream.nullInputStream(), new PrintStream(output));
        ui.showLoadingError("broken file");
        assertTrue(output.toString().contains("cannot read saved tasks: broken file"));
        output.reset();
        ui.showSavingError("disk full");
        assertTrue(output.toString().contains("cannot save that change: disk full"));
        assertTrue(output.toString().contains("Task list was not changed."));
    }

    @Test
    void showTaskList_multipleTasks_preservesOrderAndCompletionMarkers() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(InputStream.nullInputStream(), new PrintStream(output));
        Todo completed = new Todo("second");
        completed.markDone();

        ui.showTaskList(List.of(new Todo("first"), completed));

        assertEquals(String.join(System.lineSeparator(), "Gongrilla find tasks in list:",
                "  1.[T][ ] first", "  2.[T][X] second", ""), output.toString());
    }

    @Test
    void showMatchingTasks_filteredResults_numbersFromOne() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(InputStream.nullInputStream(), new PrintStream(output));

        ui.showMatchingTasks(List.of(new Todo("second"), new Todo("fourth")));

        assertEquals(String.join(System.lineSeparator(), "Here are the matching tasks in your list:",
                "  1.[T][ ] second", "  2.[T][ ] fourth", ""), output.toString());
    }

    @Test
    void showTaskLists_emptyResults_printsOnlyHeaders() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(InputStream.nullInputStream(), new PrintStream(output));

        ui.showTaskList(List.of());
        ui.showMatchingTasks(List.of());

        assertEquals(String.join(System.lineSeparator(), "Gongrilla find tasks in list:",
                "Here are the matching tasks in your list:", ""), output.toString());
    }

    @Test
    void showAddedTask_printsAllLinesInOrder() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(InputStream.nullInputStream(),
                new PrintStream(output, true, StandardCharsets.UTF_8));

        ui.showAddedTask("todo", new Todo("read book"), 1);

        assertEquals(String.join(System.lineSeparator(),
                "higa higa click click. New todo:", "  [T][ ] read book", "Gongrilla count 1 tasks.", ""),
                output.toString(StandardCharsets.UTF_8));
    }
}
