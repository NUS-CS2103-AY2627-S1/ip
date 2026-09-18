package geek.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Tests console response rendering through the varargs output helper.
 */
class UiTest {
    @Test
    void showResponse_multilineMessage_printsBoundaryAndBlankLine() {
        ByteArrayOutputStream outputBytes =
                new ByteArrayOutputStream();
        Ui ui = new Ui(
                new ByteArrayInputStream(new byte[0]),
                new PrintStream(
                        outputBytes,
                        true,
                        StandardCharsets.UTF_8
                )
        );

        ui.showResponse("first line\nsecond line");

        String newline = System.lineSeparator();
        assertEquals(
                "----------------------" + newline
                        + "first line" + newline
                        + "second line" + newline
                        + "----------------------" + newline
                        + newline,
                outputBytes.toString(StandardCharsets.UTF_8)
        );
        ui.close();
    }
}
