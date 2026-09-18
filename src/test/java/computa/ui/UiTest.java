package computa.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import computa.task.Deadline;
import computa.task.Task;
import computa.task.Todo;

/** Tests user-interface output for task searches. */
class UiTest {
    private final PrintStream originalOutput = System.out;
    private ByteArrayOutputStream capturedOutput;

    @BeforeEach
    void captureOutput() {
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOutput);
    }

    @Test
    void showTasksContaining_keywordMatchesCaseInsensitively_preservesTaskNumbers() {
        ArrayList<Task> tasks = new ArrayList<>();
        Todo readBook = new Todo("read book");
        readBook.markAsDone();
        tasks.add(readBook);
        tasks.add(new Todo("buy bread"));
        Deadline returnBook = new Deadline("return book", "June 6th");
        returnBook.markAsDone();
        tasks.add(returnBook);

        new Ui().showTasksContaining(tasks, "BOOK");
        String output = capturedOutput.toString(StandardCharsets.UTF_8);

        assertTrue(output.contains("Goshujin-Sama, can't you do this yourself?"));
        assertTrue(output.contains("1.[T][X] read book"));
        assertTrue(output.contains("3.[D][X] return book (by: June 6th)"));
        assertFalse(output.contains("2.[T][ ] buy bread"));
    }

    @Test
    void showTasksContaining_unknownKeyword_reportsNoMatches() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));

        new Ui().showTasksContaining(tasks, "cake");
        String output = capturedOutput.toString(StandardCharsets.UTF_8);

        assertTrue(output.contains("No matching tasks found."));
        assertFalse(output.contains("read book"));
    }

    @Test
    void showGreeting_customOutputReceiver_receivesGreetingLines() {
        List<String> output = new ArrayList<>();

        new Ui(output::add).showGreeting();

        assertEquals(3, output.size());
        assertTrue(output.get(1).contains("COMPUTA"));
        assertTrue(output.get(1).contains("What can I do for you?"));
    }

    @Test
    void showTasks_normalListMessage_doesNotUseTheErrorPrefix() {
        List<String> output = new ArrayList<>();
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));

        new Ui(output::add).showTasks(tasks);

        assertEquals(1, output.size());
        assertTrue(output.get(0).contains("I guess I'll have to spend more time with you"));
        assertTrue(output.get(0).contains("1.[T][ ] read book"));
        assertFalse(output.get(0).contains("Hmph! I guess I'll have to spend more time with you"));
    }
}
