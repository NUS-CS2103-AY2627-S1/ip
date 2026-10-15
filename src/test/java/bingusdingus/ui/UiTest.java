package bingusdingus.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bingusdingus.task.TaskList;
import bingusdingus.task.Todo;

/** Tests command-line response formatting without testing JavaFX GUI behavior. */
class UiTest {
    private static final Path STORAGE_PATH = Path.of(".", "data", "bingusdingus.txt");
    private byte[] originalStorage;
    private boolean storageExisted;

    @BeforeEach
    void isolateStorage() throws IOException {
        storageExisted = Files.exists(STORAGE_PATH);
        originalStorage = storageExisted ? Files.readAllBytes(STORAGE_PATH) : null;
        Files.deleteIfExists(STORAGE_PATH);
    }

    @AfterEach
    void restoreStorage() throws IOException {
        if (storageExisted) {
            Files.createDirectories(STORAGE_PATH.getParent());
            Files.write(STORAGE_PATH, originalStorage);
        } else {
            Files.deleteIfExists(STORAGE_PATH);
        }
    }

    @Test
    void showWelcomeAndGoodbye_returnExpectedMessages() {
        Ui ui = new Ui();

        assertEquals("        .-\"\"\"\"-.\n"
                        + "       /  o  o  \\\n"
                        + "      |    ∆     |     BINGUS\n"
                        + "      |  \\___/   |     DINGUS\n"
                        + "       \\        /\n"
                        + "        '-.__.-'\n"
                        + "Hey there, I'm Bingus Dingusss.\n"
                        + "How can I help ya?", ui.showWelcome());
        assertEquals("Goodbye!", ui.showGoodbye());
    }

    @Test
    void showTasksAndMatchingTasks_formatIndexesAndEmptyLists() {
        Ui ui = new Ui();
        TaskList emptyTaskList = new TaskList();
        TaskList taskList = new TaskList();
        taskList.add(new Todo("first"));
        taskList.add(new Todo("second"));

        assertEquals("Here are the tasks in your list:\n1. [T][ ] first\n2. [T][ ] second",
                ui.showTasks(taskList));
        assertEquals("Here are the matching tasks in your list:\n2. [T][ ] second",
                ui.showMatchingTasks(taskList, List.of(1)));
        assertEquals("Here are the tasks in your list:", ui.showTasks(emptyTaskList));
        assertEquals("Here are the matching tasks in your list:",
                ui.showMatchingTasks(taskList, List.of()));
    }

    @Test
    void responseHelpers_returnExpectedMessages() {
        Ui ui = new Ui();
        Todo todo = new Todo("buy milk");

        assertEquals("Please specify a keyword to search for.", ui.showMissingFindKeyword());
        assertEquals("Got it. I've added this task:\n  [T][ ] buy milk\nNow you have 1 tasks in the list.",
                ui.showTaskAdded(todo, 1));
        assertEquals("Nice! I've marked this task as done:\n[T][ ] buy milk", ui.showTaskMarkedDone(todo));
        assertEquals("OK, I've marked this task as not done yet:\n[T][ ] buy milk",
                ui.showTaskMarkedNotDone(todo));
        assertEquals("I've removed this task:\n  buy milk\nNow you have 0 tasks in the list.",
                ui.showTaskDeleted("buy milk", 0));
        assertEquals("I've undone your last command.", ui.showUndoSuccessful());
        assertEquals("There is nothing to undo.", ui.showNothingToUndo());
        assertEquals("Sorry, that task number is invalid.", ui.showInvalidTaskNumber());
        assertEquals("Sorry, please specify a valid task number.", ui.showInvalidTaskNumberFormat());
        assertEquals("That task has already been marked done", ui.showTaskAlreadyDone());
        assertEquals("That task has not been marked done yet", ui.showTaskNotDone());
        assertEquals("I couldn't save your tasks. Your latest change was not kept.", ui.showStorageError());
    }

    @Test
    void showInvalidCommand_includesMessageAndUsage() {
        assertEquals("bad command\nUse:\n"
                        + "todo <description>\n"
                        + "deadline <description> /by <date>, or\n"
                        + "event <description> /from <start> /to <end>.",
                new Ui().showInvalidCommand("bad command"));
    }
}
