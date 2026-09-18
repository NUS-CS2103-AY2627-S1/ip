package com.benthecat.kachow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import com.benthecat.kachow.ui.printer.ConsolePrinter;

/** Tests console input, graceful EOF, and the public entry point with isolated storage. */
class KachowConsoleTest {
    @TempDir
    Path tempDirectory;

    @Test
    void run_byeInMiddleOfInput_stopsBeforeLaterCommands() throws Exception {
        String output = runConsole("todo first\nbye\ntodo ignored\n");
        assertTrue(output.contains("Ka-chow! I'm Kachow"));
        assertTrue(output.contains("Green light, buddy!"));
        assertTrue(output.contains("Time to refuel at Flo's."));
        assertFalse(output.contains("ignored"));
        assertEquals("T | 0 | first\n", Files.readString(tempDirectory.resolve("kachow.txt")));
    }

    @Test
    void run_invalidCommandThenEof_reportsErrorAndFinishesLastCommand() throws Exception {
        String output = runConsole("dance\ntodo last");
        assertTrue(output.contains("That command took a wrong turn."));
        assertTrue(output.contains("[T][ ] last"));
        assertFalse(output.contains("Time to refuel"));
        assertEquals("T | 0 | last\n", Files.readString(tempDirectory.resolve("kachow.txt")));
    }

    @Test
    void run_emptyInput_printsWelcomeWithoutCreatingStorage() throws Exception {
        String output = runConsole("");
        assertTrue(output.startsWith("    ____________________________________________________________"));
        assertTrue(output.contains("Ka-chow! I'm Kachow"));
        assertEquals(2, output.lines().filter(line -> line.equals(
                "    ____________________________________________________________")).count());
        assertTrue(Files.notExists(tempDirectory.resolve("kachow.txt")));
    }

    @Test
    void run_corruptStorage_reportsLoadingErrorBeforeProcessingCommands() throws Exception {
        Files.writeString(tempDirectory.resolve("kachow.txt"), "broken\n");
        String output = runConsole("list\nbye\n");
        assertTrue(output.indexOf("Saving is disabled") < output.indexOf("Quiet as Radiator Springs"));
        assertTrue(output.contains("Time to refuel"));
        assertEquals("broken\n", Files.readString(tempDirectory.resolve("kachow.txt")));
    }

    @Test
    @Timeout(30)
    void main_freshProcesses_useDefaultRelativePathAndRestoreTasks() throws Exception {
        assertTrue(runMain("todo entry point\nbye\n").contains("[T][ ] entry point"));
        assertEquals("T | 0 | entry point\n", Files.readString(tempDirectory.resolve("data/kachow.txt")));
        String restarted = runMain("list\n");
        assertTrue(restarted.contains("1.[T][ ] entry point"));
        assertFalse(restarted.contains("Pit stop, buddy!"));
    }

    /** Runs the console loop with temporary streams and restores the host process in all cases. */
    private String runConsole(String input) throws Exception {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(capturedOutput);
            new Kachow(tempDirectory.resolve("kachow.txt").toString(), new ConsolePrinter()).run();
            return output.toString(StandardCharsets.UTF_8);
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }

    /** Starts the real entry point without touching the repository's default data directory. */
    private String runMain(String input) throws Exception {
        Path java = Path.of(System.getProperty("java.home"), "bin", "java");
        Path classes = Path.of(Kachow.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Process process = new ProcessBuilder(java.toString(), "-cp", classes.toString(), Kachow.class.getName())
                .directory(tempDirectory.toFile()).redirectErrorStream(true).start();
        try {
            try (var commandStream = process.getOutputStream()) {
                commandStream.write(input.getBytes(StandardCharsets.UTF_8));
            }
            assertTrue(process.waitFor(10, TimeUnit.SECONDS), "Console process should exit after EOF or bye.");
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(0, process.exitValue(), output);
            return output;
        } finally {
            process.destroyForcibly();
        }
    }
}
