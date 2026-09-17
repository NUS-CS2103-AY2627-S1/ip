package megatron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import megatron.storage.TaskStorage;

/**
 * Tests the non-GUI application loop and command-line entry point.
 */
class MegatronTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void run_validWorkflow_processesCommandsAndPersistsTask() throws Exception {
        Path storageFile = tempDirectory.resolve("tasks.csv");

        String output = runApplication(storageFile, "todo study\nmark 1\nfind stu\nbye\n");

        assertTrue(output.contains("Rawr! Megatron Griffin reporting for duty!"));
        assertTrue(output.contains("Victory! This task has fallen before my mighty intellect:"));
        assertTrue(output.contains("1.[T][X] study"));
        assertTrue(new TaskStorage(storageFile.toString()).load().getTask(1).isDone());
    }

    @Test
    void run_invalidCommand_reportsErrorAndContinuesToExit() {
        String output = runApplication(tempDirectory.resolve("tasks.csv"), "blah\nbye\n");

        assertTrue(output.contains("That command is not part of my master plan. Try again."));
        assertTrue(output.contains("Retreat accepted. Try not to create more tasks while I'm gone!"));
    }

    @Test
    void run_endOfInput_stopsWithoutGoodbyeMessage() {
        String output = runApplication(tempDirectory.resolve("tasks.csv"), "");

        assertTrue(output.contains("What command shall I execute?"));
        assertFalse(output.contains("Retreat accepted. Try not to create more tasks while I'm gone!"));
    }

    @Test
    void run_loadFailure_reportsErrorAndContinues() {
        String output = runApplication(tempDirectory, "bye\n");

        assertTrue(output.contains("Could not load tasks. Check that the data file is readable."));
        assertTrue(output.contains("Retreat accepted. Try not to create more tasks while I'm gone!"));
    }

    @Test
    void main_pathArgument_startsApplicationWithSuppliedStoragePath() throws Exception {
        Path storageFile = tempDirectory.resolve("main-tasks.csv");

        String output = runMain(storageFile, "bye\n");

        assertTrue(output.contains("Rawr! Megatron Griffin reporting for duty!"));
        assertTrue(output.contains("Retreat accepted. Try not to create more tasks while I'm gone!"));
        assertEquals(0, new TaskStorage(storageFile.toString()).load().size());
    }

    private static String runApplication(Path storagePath, String input) {
        return runWithStreams(() -> new Megatron(storagePath.toString()).run(), input);
    }

    private static String runMain(Path storagePath, String input) {
        return runWithStreams(() -> Megatron.main(new String[] {storagePath.toString()}), input);
    }

    private static String runWithStreams(Runnable application, String input) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
            application.run();
            return capturedOutput.toString(StandardCharsets.UTF_8);
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }
}
