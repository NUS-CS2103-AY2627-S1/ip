package skynet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests command workflows, undo behavior, persistence, and storage failures.
 */
public class SkynetTest {

    @TempDir
    Path temporaryDirectory;

    private Skynet createSkynet() {
        return new Skynet(new Storage(temporaryDirectory.resolve("saveFile.txt")));
    }

    @Test
    void commands_addListFindMarkUnmarkAndDeleteTasks() {
        Skynet skynet = createSkynet();

        assertTrue(skynet.getResponse("todo buy milk").contains("buy milk"));
        assertTrue(skynet.getResponse("deadline submit report /by 2026-12-01 18:00")
                .contains("submit report"));
        assertTrue(skynet.getResponse(
                "event meeting /from 2026-12-02 10:00 /to 2026-12-02 11:00")
                .contains("meeting"));
        assertTrue(skynet.getResponse("find MILK").contains("buy milk"));

        assertTrue(skynet.getResponse("mark 1").contains("Neutralized"));
        assertTrue(skynet.getResponse("unmark 1").contains("Failed to Complete"));
        assertTrue(skynet.getResponse("delete 1").contains("Remaining targets: 2"));
        assertEquals("DeleteCommand", skynet.getCommandType());
        assertTrue(skynet.getResponse("list").contains("submit report"));
        assertTrue(skynet.getResponse("list").contains("meeting"));
    }

    @Test
    void invalidCommands_returnErrorsWithoutChangingState() {
        Skynet skynet = createSkynet();
        skynet.getResponse("todo valid task");

        assertTrue(skynet.getResponse(null).contains("Please enter"));
        assertTrue(skynet.getResponse("todo").contains("Please input"));
        assertTrue(skynet.getResponse("list extra").contains("does not accept"));
        assertTrue(skynet.getResponse("mark abc").contains("Target Number"));
        assertTrue(skynet.getResponse("delete 99").contains("does not exist"));

        String list = skynet.getResponse("list");
        assertTrue(list.contains("valid task"));
        assertTrue(!list.contains("Remaining targets"));
    }

    @Test
    void invalidModification_doesNotCreateUndoEntry() {
        Skynet skynet = createSkynet();
        skynet.getResponse("todo valid task");
        skynet.getResponse("deadline");

        assertTrue(skynet.getResponse("undo").contains("undone successfully"));
        assertTrue(skynet.getResponse("list").contains("Target List Display"));
        assertTrue(!skynet.getResponse("list").contains("valid task"));
    }

    @Test
    void undo_restoresPreviousStateAndRejectsExtraUndo() {
        Skynet skynet = createSkynet();
        skynet.getResponse("todo first");
        skynet.getResponse("todo second");

        assertTrue(skynet.getResponse("undo").contains("undone successfully"));
        assertTrue(skynet.getResponse("list").contains("first"));
        assertTrue(!skynet.getResponse("list").contains("second"));
        assertTrue(skynet.getResponse("undo").contains("undone successfully"));
        assertTrue(!skynet.getResponse("list").contains("first"));
        assertTrue(skynet.getResponse("undo").contains("No previous operations"));
    }

    @Test
    void commands_persistAcrossSkynetInstances() {
        Path saveFile = temporaryDirectory.resolve("persistent.txt");
        Storage storage = new Storage(saveFile);
        Skynet first = new Skynet(storage);
        first.getResponse("todo persistent task");

        Skynet second = new Skynet(new Storage(saveFile));

        assertTrue(second.getResponse("list").contains("persistent task"));
    }

    @Test
    void corruptedStorage_isReportedAndApplicationStartsEmpty() throws IOException {
        Path saveFile = temporaryDirectory.resolve("corrupted.txt");
        Files.write(saveFile, List.of("not a valid save row"));
        Skynet skynet = new Skynet(new Storage(saveFile));

        String response = skynet.getResponse("list");

        assertTrue(response.contains("Saved tasks could not be loaded"));
        assertTrue(response.contains("Target List Display"));
    }

    @Test
    void saveFailure_rollsBackInMemoryChange() {
        Skynet skynet = new Skynet(new FailingStorage(
                temporaryDirectory.resolve("failing.txt")));

        assertTrue(skynet.getResponse("todo unsaved task").contains("cannot save"));
        assertTrue(!skynet.getResponse("list").contains("unsaved task"));
        assertTrue(skynet.getResponse("undo").contains("No previous operations"));
    }

    @Test
    void simpleCommands_rejectParameters() {
        Skynet skynet = createSkynet();

        assertTrue(skynet.getResponse("bye now").contains("does not accept"));
        assertTrue(skynet.getResponse("undo now").contains("does not accept"));
        assertTrue(skynet.getResponse("unknown").contains("unrecognized"));
    }

    /**
     * Storage test double that simulates a file-system write failure.
     */
    private static class FailingStorage extends Storage {
        FailingStorage(Path path) {
            super(path);
        }

        @Override
        void save(List<Task> tasks) throws IOException {
            throw new IOException("cannot save tasks");
        }
    }
}
