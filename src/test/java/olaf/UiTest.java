package olaf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests console output produced by the user interface.
 */
class UiTest {
    private static final String DIVIDER = "_".repeat(60);
    private static final String LINE_SEPARATOR = System.lineSeparator();

    @Test
    void showResponse_multipleMatches_tasksNumberedAndFormatted() {
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui ui = new Ui(InputStream.nullInputStream(), new PrintStream(outputBytes));
        Task todo = new Todo("read book");
        Task deadline = new Deadline("return book", LocalDate.of(2026, 9, 1));
        deadline.markAsDone();

        try (ui) {
            ui.showResponse(ResponseFormatter.formatMatchingTasks(
                    List.of(new TaskMatch(2, todo), new TaskMatch(4, deadline))));
        }

        String expectedOutput = String.join(LINE_SEPARATOR,
                DIVIDER,
                " Here are the matching tasks in your list:",
                " 2.[T][ ] read book",
                " 4.[D][X] return book (by: Sep 01 2026)",
                DIVIDER,
                "");
        assertEquals(expectedOutput, outputBytes.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showResponse_noMatches_headingAndDividersShown() {
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui ui = new Ui(InputStream.nullInputStream(), new PrintStream(outputBytes));

        try (ui) {
            ui.showResponse(ResponseFormatter.formatMatchingTasks(List.of()));
        }

        String expectedOutput = String.join(LINE_SEPARATOR,
                DIVIDER,
                " Here are the matching tasks in your list:",
                DIVIDER,
                "");
        assertEquals(expectedOutput, outputBytes.toString(StandardCharsets.UTF_8));
    }
}
