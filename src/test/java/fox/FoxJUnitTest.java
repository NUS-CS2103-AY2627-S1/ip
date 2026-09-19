package fox;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import fox.storage.Storage;
import fox.task.Task;

/** JUnit integration tests for Fox command handling and persistence wiring. */
class FoxJUnitTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void handlesCommandsAndReportsMalformedInput() {
        Fox fox = new Fox(new Storage(temporaryDirectory.resolve("fox.txt")));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        try {
            System.setOut(new PrintStream(output));
            fox.run(new Scanner("todo remember this\nmark 1\nlist\nmark nope\nbye\n"));
        } finally {
            System.setOut(originalOutput);
        }

        String text = output.toString();
        assertTrue(text.contains("remember this"));
        assertTrue(text.contains("[X] remember this"));
        assertTrue(text.contains("whole number"));
        assertTrue(text.contains("Bye for now!"));
    }

    @Test
    void guiResponseUsesSameCommandsAndPersistsTasks() {
        Path dataFile = temporaryDirectory.resolve("gui-fox.txt");
        Fox fox = new Fox(new Storage(dataFile));

        assertTrue(fox.getResponse("todo remember this").contains("remember this"));
        assertTrue(fox.getResponse("list").contains("remember this"));
        fox.getResponse("bye");

        Fox reloadedFox = new Fox(new Storage(dataFile));
        assertTrue(reloadedFox.getResponse("list").contains("remember this"));
    }

    @Test
    void duplicateCommandIsRejectedWithoutAddingAnotherTask() {
        Fox fox = new Fox(new Storage(temporaryDirectory.resolve("duplicates.txt")));

        fox.getResponse("todo read book");
        fox.getResponse("mark 1");
        String duplicateResponse = fox.getResponse("todo read book");
        String listResponse = fox.getResponse("list");

        assertTrue(duplicateResponse.contains("already in your task list"));
        assertTrue(listResponse.contains("1.[T][X] read book"));
        assertFalse(listResponse.contains("2.[T]"));
    }

    @Test
    void rejectsUnexpectedArgumentsAndInvalidTaskNumbers() {
        Fox fox = new Fox(new Storage(temporaryDirectory.resolve("commands.txt")));

        assertTrue(fox.getResponse("list now").contains("does not accept arguments"));
        assertTrue(fox.getResponse("bye now").contains("does not accept arguments"));
        assertTrue(fox.getResponse("mark").contains("exactly one task number"));
        assertTrue(fox.getResponse("delete 1 2").contains("exactly one task number"));
        assertTrue(fox.getResponse("unmark 0").contains("positive whole number"));
        assertTrue(fox.getResponse("mark -1").contains("positive whole number"));
    }

    @Test
    void reportsLoadFailuresInGuiAndProtectsUnavailableData() throws Exception {
        Path directoryAsDataFile = temporaryDirectory.resolve("data-directory");
        java.nio.file.Files.createDirectory(directoryAsDataFile);
        Fox fox = new Fox(new Storage(directoryAsDataFile));

        String response = fox.getResponse("list");

        assertTrue(response.contains("could not load your tasks safely"));
        assertTrue(response.contains("Could not read data file"));
    }

    @Test
    void reportsSaveFailuresAndBlocksFurtherChanges() {
        Fox fox = new Fox(new SaveFailingStorage(temporaryDirectory.resolve("save-failure.txt")));

        String failedSaveResponse = fox.getResponse("todo remember this");
        String blockedChangeResponse = fox.getResponse("todo another task");

        assertTrue(failedSaveResponse.contains("could not save your tasks"));
        assertTrue(failedSaveResponse.contains("simulated write failure"));
        assertTrue(blockedChangeResponse.contains("data file is unavailable"));
    }

    @Test
    void findsTasksWithoutChangingTheirOriginalNumbers() {
        Fox fox = new Fox(new Storage(temporaryDirectory.resolve("find.txt")));
        fox.getResponse("todo unrelated task");
        fox.getResponse("todo read project notes");

        String response = fox.getResponse("find PROJECT");

        assertTrue(response.contains("2.[T][ ] read project notes"));
        assertFalse(response.contains("1.[T][ ] unrelated task"));
        assertTrue(fox.getResponse("find").contains("at least one keyword"));
    }

    /** Storage double that permits loading but simulates an environmental write failure. */
    private static final class SaveFailingStorage extends Storage {
        SaveFailingStorage(Path dataFile) {
            super(dataFile);
        }

        @Override
        public Task[] load() {
            return new Task[0];
        }

        @Override
        public void save(Task[] tasks, int taskCount) throws StorageException {
            throw new StorageException("simulated write failure");
        }
    }
}
