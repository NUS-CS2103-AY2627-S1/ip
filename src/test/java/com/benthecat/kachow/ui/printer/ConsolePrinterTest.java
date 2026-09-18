package com.benthecat.kachow.ui.printer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests console formatting and buffer isolation across successive responses. */
class ConsolePrinterTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOutput;
    private PrintStream capturedOutput;
    private final ConsolePrinter printer = new ConsolePrinter();

    @BeforeEach
    void captureOutput() {
        originalOutput = System.out;
        capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8);
        System.setOut(capturedOutput);
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOutput);
        capturedOutput.close();
    }

    @Test
    void addData_multipleMessages_buffersUntilFlushedAndIndentsEveryLine() {
        printer.addData("first\nsecond");
        printer.addData("third\r\nfourth");
        assertEquals("", output.toString(StandardCharsets.UTF_8));
        printer.outputData();
        assertEquals("    first\n    second\n    third\n    fourth\n".replace("\n", System.lineSeparator()),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void outputData_repeatedFlushAndNewResponse_doesNotRepeatEarlierMessages() {
        printer.outputData();
        assertEquals("", output.toString(StandardCharsets.UTF_8));
        printer.addData("first");
        printer.outputData();
        printer.outputData();
        printer.addData("second");
        printer.outputData();
        assertEquals("    first\n    second\n".replace("\n", System.lineSeparator()),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void addData_blankLineAndExistingIndent_preservesIntentionalWhitespace() {
        printer.addData("");
        printer.addData("  nested\n\nlast\n");
        printer.outputData();
        assertEquals("      nested\n    \n    last\n".replace("\n", System.lineSeparator()),
                output.toString(StandardCharsets.UTF_8));
    }
}
