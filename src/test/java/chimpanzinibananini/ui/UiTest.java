package chimpanzinibananini.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import chimpanzinibananini.task.Task;
import chimpanzinibananini.task.Todo;

/** Tests console output presented to chatbot users. */
class UiTest {

    @Test
    void showFoundTasks_multipleMatches_printsNumberedResults() {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        try {
            Ui ui = new Ui();
            List<Task> matches = List.of(new Todo("read book"), new Todo("return book"));

            ui.showFoundTasks(matches);

            String expected = String.join(System.lineSeparator(),
                    "Here are the matching tasks in your list:",
                    "1. [T][ ] read book",
                    "2. [T][ ] return book",
                    "");
            assertEquals(expected, output.toString(StandardCharsets.UTF_8));
        } finally {
            System.setOut(originalOutput);
        }
    }
}
