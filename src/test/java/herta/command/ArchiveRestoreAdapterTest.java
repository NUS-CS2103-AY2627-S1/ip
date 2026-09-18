package herta.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import herta.parser.ArchiveRange;
import herta.parser.ArchiveSelection;
import herta.storage.Storage;
import herta.task.TaskList;
import herta.task.Todo;
import herta.ui.UiOutput;

/**
 * Tests legacy command adapters that derive the sibling archive from active storage.
 */
class ArchiveRestoreAdapterTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void legacyCommands_useSiblingArchiveForArchiveViewAndRestore() throws Exception {
        Path activeFile = temporaryDirectory.resolve("legacy").resolve("tasks.txt");
        Storage activeStorage = new Storage(activeFile.toString());
        Todo task = new Todo("legacy task");
        task.markAsDone();
        TaskList activeTasks = new TaskList(List.of(task));
        activeStorage.save(activeTasks);
        RecordingOutput output = new RecordingOutput();

        new ArchiveCommand(new ArchiveSelection(List.of(new ArchiveRange(1, 1)), false))
                .execute(activeTasks, output, activeStorage);
        new ArchivedCommand().execute(activeTasks, output, activeStorage);
        new RestoreCommand(0).execute(activeTasks, output, activeStorage);

        assertEquals(1, activeTasks.size());
        assertEquals(0, new Storage(activeFile.resolveSibling("archive.txt").toString())
                .loadArchived().size());
        assertTrue(output.messages.contains("1. [T][X] legacy task"));
    }

    /** Captures output emitted by archive and restore adapter tests. */
    private static final class RecordingOutput implements UiOutput {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void showMessage(String message) {
            messages.add(message);
        }

        @Override
        public void showGoodbye() {
            messages.add(UiOutput.GOODBYE_MESSAGE);
        }
    }
}
