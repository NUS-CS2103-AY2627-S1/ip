package ramly.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ramly.model.TaskList;
import ramly.model.Todo;
import ramly.storage.Storage;
import ramly.ui.Ui;

/** Tests undo persistence and output behavior. */
public class UndoCommandTest {
    @Test
    public void execute_noUndoHistory_doesNotSave() {
        TaskList tasks = new TaskList(new ArrayList<>());
        TrackingStorage storage = new TrackingStorage();
        ArrayList<String> responses = new ArrayList<>();

        new UndoCommand().execute(tasks, new Ui(responses::add), storage);

        assertEquals(List.of("No steps to retrace yet."), responses);
        assertEquals(0, storage.saveCount);
    }

    @Test
    public void execute_withUndoHistory_savesRestoredList() {
        TaskList tasks = new TaskList(new ArrayList<>());
        tasks.add(new Todo("buy milk"));
        TrackingStorage storage = new TrackingStorage();

        new UndoCommand().execute(tasks, new Ui(message -> { }), storage);

        assertEquals(1, storage.saveCount);
    }

    /** Storage test double that counts save requests without writing a file. */
    private static class TrackingStorage extends Storage {
        private int saveCount;

        TrackingStorage() {
            super("unused.txt");
        }

        @Override
        public void save(TaskList tasks) {
            saveCount++;
        }
    }
}
