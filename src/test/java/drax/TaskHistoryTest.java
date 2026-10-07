package drax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskHistoryTest {
    @Test
    public void getPreviousMemento_emptyHistory_returnsNull() {
        TaskHistory history = new TaskHistory();
        assertNull(history.getPreviousMemento());
    }

    @Test
    public void getPreviousMemento_onlyInitialState_returnsNull() {
        TaskHistory history = new TaskHistory();
        history.addMemento(createMemento("initial"));
        assertNull(history.getPreviousMemento());
    }

    @Test
    public void undoAndRedo_multipleStates_returnsStatesInExpectedOrder() {
        TaskHistory history = new TaskHistory();
        history.addMemento(createMemento("initial"));
        history.addMemento(createMemento("second"));
        history.addMemento(createMemento("third"));

        assertEquals(List.of("second"), getTaskDescriptions(history.getPreviousMemento()));
        assertEquals(List.of("initial"), getTaskDescriptions(history.getPreviousMemento()));
        assertNull(history.getPreviousMemento());
        assertEquals(List.of("second"), getTaskDescriptions(history.getNextMemento()));
        assertEquals(List.of("third"), getTaskDescriptions(history.getNextMemento()));
        assertNull(history.getNextMemento());
    }

    @Test
    public void addMemento_afterUndo_discardsRedoHistory() {
        TaskHistory history = new TaskHistory();
        history.addMemento(createMemento("initial"));
        history.addMemento(createMemento("discarded"));
        history.getPreviousMemento();

        history.addMemento(createMemento("replacement"));

        assertNull(history.getNextMemento(),
                "The undoneMementos stack should be empty so getNextMemento should return null");

        assertEquals(List.of("initial"), getTaskDescriptions(history.getPreviousMemento()));
    }

    @Test
    public void returnedMemento_modified_doesNotChangeStoredSnapshot() {
        TaskHistory history = new TaskHistory();
        history.addMemento(createMemento("initial"));
        history.addMemento(createMemento("second"));

        TaskMemento initialState = history.getPreviousMemento();
        Task initialTask = initialState.getSavedContent().get(0);
        initialTask.markAsDone();
        history.getNextMemento();

        TaskMemento restoredInitialState = history.getPreviousMemento();

        assertEquals("[T][ ] initial", restoredInitialState.getSavedContent().get(0).toString());
    }

    private static TaskMemento createMemento(String description) {
        TaskList tasks = new TaskList();
        tasks.add(new Todo(description));
        return tasks.createMemento();
    }

    private static List<String> getTaskDescriptions(TaskMemento memento) {
        return memento.getSavedContent()
                .asList()
                .stream()
                .map(Task::getTask)
                .toList();
    }
}
