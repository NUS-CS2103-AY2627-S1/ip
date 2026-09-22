package longfrog.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import longfrog.task.Todo;

class UiTest {
    @Test
    void constructor_nullMessageConsumer_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Ui(null));
    }

    @Test
    void messageHelpers_variousMessages_forwardExpectedText() {
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.showMessage("custom");
        ui.showExitMessage();
        ui.showLoadingError();
        ui.showSavingError();
        ui.showLine();
        ui.showEmptyLine();

        assertEquals(List.of(
                "custom",
                "Pond secured. Rest well—ribbit.",
                "Warning: Save-file decoding failed. Booting with an empty task set.",
                "I/O error: Task data could not be persisted to the pond archive.",
                "─".repeat(50),
                ""), messages);
    }

    @Test
    void showNumberedTasks_multipleTasks_numbersFromOneInOrder() {
        List<String> messages = new ArrayList<>();

        new Ui(messages::add).showNumberedTasks(List.of(new Todo("first"), new Todo("second")));

        assertEquals(List.of("1: [T][ ] first", "2: [T][ ] second"), messages);
    }

    @Test
    void showInvalidTaskIndex_emptyAndPopulatedLists_displaysContextualMessages() {
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        ui.showInvalidTaskIndex(0, 0, "mark");
        ui.showInvalidTaskIndex(7, 3, "delete");

        assertEquals(List.of(
                "The pond is clear—there are no tasks to mark.",
                "There is no task 8. Choose a task number from 1 to 3."), messages);
    }

    @Test
    void showInvalidTaskIndex_invalidArguments_violateUiInvariants() {
        Ui ui = new Ui(message -> { });

        assertThrows(AssertionError.class, () -> ui.showInvalidTaskIndex(-1, 1, "mark"));
        assertThrows(AssertionError.class, () -> ui.showInvalidTaskIndex(0, -1, "mark"));
        assertThrows(AssertionError.class, () -> ui.showInvalidTaskIndex(0, 1, null));
        assertThrows(AssertionError.class, () -> ui.showInvalidTaskIndex(0, 1, " "));
    }
}
