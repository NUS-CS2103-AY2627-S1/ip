package luckynoslacky.luckyui.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import luckynoslacky.luckyresponse.LuckyNoQuips;

/** Tests GUI exit and persistence behavior through isolated Java processes. */
class LuckyNoGuiSubprocessTest {
    private static final long OUTPUT_TIMEOUT_SECONDS = 15L;
    private static final long EARLY_EXIT_CHECK_SECONDS = 1L;
    private static final long EXIT_TIMEOUT_SECONDS = 5L;
    private static final String DATA_FILE = "data/luckyNoSlacky.csv";

    @TempDir
    Path temporaryDirectory;

    /** Verifies that goodbye is shown before the delayed GUI process exit. */
    @Test
    void guiProcess_byeCommand_delaysExitAfterShowingGoodbye() throws Exception {
        Process process = startProcess("bye");
        try {
            assertEquals(
                    "GOODBYE_RESULT=" + LuckyNoQuips.goodbye()
                            + "|inputEmpty=true|inputDisabled=true",
                    readFirstOutputLine(process));
            assertTrue(process.isAlive());
            assertFalse(process.waitFor(
                    EARLY_EXIT_CHECK_SECONDS, TimeUnit.SECONDS));
            assertTrue(process.waitFor(EXIT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            assertEquals(0, process.exitValue());
            assertEquals("", readStandardError(process));
        } finally {
            terminate(process);
        }
    }

    /** Verifies that a task survives two complete GUI process launches. */
    @Test
    void guiProcess_taskAcrossRelaunches_loadsPersistedTask() throws Exception {
        ProcessResult createResult = runProcess("create");

        assertEquals(
                0,
                createResult.exitCode(),
                createResult.standardOutput() + createResult.standardError());
        assertEquals("TASK_CREATED=true", firstLine(createResult.standardOutput()));
        assertEquals("", createResult.standardError());
        assertTrue(Files.isRegularFile(temporaryDirectory.resolve(DATA_FILE)));

        ProcessResult listResult = runProcess("list");

        assertEquals(
                0,
                listResult.exitCode(),
                listResult.standardOutput() + listResult.standardError());
        assertEquals("", listResult.standardError());
        assertEquals("LOAD_ERROR=false", firstLine(listResult.standardOutput()));
        assertTrue(listResult.standardOutput().contains(
                "increment six gui persistence"));
    }

    /** Starts one isolated GUI subprocess in the temporary working directory. */
    private Process startProcess(String scenario) throws IOException {
        String javaExecutable = Path.of(
                System.getProperty("java.home"), "bin", "java").toString();
        List<String> command = new ArrayList<>();
        command.add(javaExecutable);
        command.add("--enable-native-access=javafx.graphics");
        command.add("--patch-module");
        command.add("javafx.graphics="
                + System.getProperty("test.monocle.path"));
        command.add("-Dtestfx.headless=true");
        command.add("-Dglass.platform=Monocle");
        command.add("-Dmonocle.platform=Headless");
        command.add("-Dprism.order=sw");
        command.add("-cp");
        command.add(System.getProperty(
                "test.runtime.classpath",
                System.getProperty("java.class.path")));
        command.add(LuckyNoGuiProcessProbe.class.getName());
        command.add(scenario);
        return new ProcessBuilder(command)
                .directory(temporaryDirectory.toFile())
                .start();
    }

    /** Runs a GUI scenario until it exits and captures its output. */
    private ProcessResult runProcess(String scenario)
            throws IOException, InterruptedException {
        Process process = startProcess(scenario);
        try {
            assertTrue(
                    process.waitFor(OUTPUT_TIMEOUT_SECONDS, TimeUnit.SECONDS),
                    "GUI subprocess did not exit in time.");
            return new ProcessResult(
                    process.exitValue(),
                    readStandardOutput(process),
                    readStandardError(process));
        } finally {
            terminate(process);
        }
    }

    /** Reads the first subprocess output line within a bounded wait. */
    private String readFirstOutputLine(Process process)
            throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<String> outputLine = executor.submit(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                process.getInputStream(), StandardCharsets.UTF_8))) {
                    return reader.readLine();
                }
            });
            return outputLine.get(OUTPUT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
        }
    }

    /** Reads all standard output after a subprocess has exited. */
    private String readStandardOutput(Process process) throws IOException {
        return new String(
                process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    /** Reads all standard error after a subprocess has exited. */
    private String readStandardError(Process process) throws IOException {
        return new String(
                process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    /** Returns the first line of captured output. */
    private String firstLine(String output) {
        return output.lines().findFirst().orElse("");
    }

    /** Terminates a subprocess if it is still running. */
    private void terminate(Process process) {
        if (process.isAlive()) {
            process.destroyForcibly();
        }
    }

    /** Captures the result of one GUI subprocess. */
    private record ProcessResult(
            int exitCode,
            String standardOutput,
            String standardError) {
    }
}
