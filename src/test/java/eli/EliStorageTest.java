package eli;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests persistence and storage failures without touching the user's data file. */
public class EliStorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void missingDataFile_addAndReload_preservesTask() {
        Path dataFile = tempDirectory.resolve("tasks.dat");
        Eli firstRun = new Eli(dataFile);

        assertTrue(firstRun.getResponse("list").contains("task list is clear"));
        assertTrue(firstRun.getResponse("todo read notes").contains("Task captured!"));
        assertTrue(Files.exists(dataFile));

        Eli secondRun = new Eli(dataFile);
        assertTrue(secondRun.getResponse("list").contains("read notes"));
    }

    @Test
    public void corruptDataFile_warnsAndDoesNotOverwriteFile() throws IOException {
        Path dataFile = tempDirectory.resolve("tasks.dat");
        Files.writeString(dataFile, "not serialized task data");
        byte[] originalContent = Files.readAllBytes(dataFile);
        Eli eli = new Eli(dataFile);

        String firstResponse = eli.getResponse("list");
        String saveResponse = eli.getResponse("todo read notes");

        assertTrue(firstResponse.contains("could not read tasks.dat"));
        assertTrue(firstResponse.contains("task list is clear"));
        assertTrue(saveResponse.contains("cannot save until tasks.dat"));
        assertArrayEquals(originalContent, Files.readAllBytes(dataFile));
    }

    @Test
    public void unavailableParentFolder_returnsSaveError() {
        Path dataFile = tempDirectory.resolve("missing").resolve("tasks.dat");
        Eli eli = new Eli(dataFile);

        String response = eli.getResponse("todo read notes");

        assertTrue(response.contains("could not save your tasks"));
        assertTrue(eli.getResponse("list").contains("task list is clear"));
        assertFalse(Files.exists(dataFile));
    }

    @Test
    public void unavailableParentFolder_mutatingCommandsRollBackChanges() {
        Path sourceFile = tempDirectory.resolve("source.dat");
        Eli savedEli = new Eli(sourceFile);
        savedEli.getResponse("todo second task");
        savedEli.getResponse("todo first task");

        Path unavailableFile = tempDirectory.resolve("missing").resolve("tasks.dat");
        Eli eli = new Eli(unavailableFile, sourceFile);

        assertTrue(eli.getResponse("mark 1").contains("could not save your tasks"));
        assertTrue(eli.getResponse("delete 1").contains("could not save your tasks"));
        assertTrue(eli.getResponse("sort").contains("could not save your tasks"));

        String tasks = eli.getResponse("list");
        assertTrue(tasks.contains("1.[T][ ] second task"));
        assertTrue(tasks.contains("2.[T][ ] first task"));
    }

    @Test
    public void legacyDataFile_loadsAndMigratesOnNextSave() {
        Path legacyFile = tempDirectory.resolve("duke.txt");
        Path currentFile = tempDirectory.resolve("eli.txt");
        Eli legacyEli = new Eli(legacyFile);
        legacyEli.getResponse("todo existing task");

        Eli eli = new Eli(currentFile, legacyFile);
        assertTrue(eli.getResponse("list").contains("existing task"));
        assertTrue(eli.getResponse("todo new task").contains("Task captured!"));
        assertTrue(Files.exists(currentFile));

        Eli migratedEli = new Eli(currentFile);
        String tasks = migratedEli.getResponse("list");
        assertTrue(tasks.contains("existing task"));
        assertTrue(tasks.contains("new task"));
    }
}
