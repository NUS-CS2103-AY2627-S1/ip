package clover.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import clover.exception.CloverException;
import clover.storage.Storage;
import clover.task.TaskList;
import clover.task.ToDo;
import clover.tutoree.Tutoree;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

/** Tests commands whose effect is to present information or close the application. */
class DisplayAndExitCommandTest {
    @TempDir
    Path tempDir;

    @Test
    void listAndHelpCommands_execute_buildExpectedResponses() {
        TaskList tasks = new TaskList(List.of(new ToDo("read book")));
        Ui ui = new Ui();

        new ListCommand().execute(tasks, new TutoreeList(), ui, storage());
        assertEquals("The grove has gathered your study quests:" + System.lineSeparator()
                + "1.[T] [ ] read book", ui.buildResponse());
        ui.clearResponse();
        new HelpCommand().execute(tasks, new TutoreeList(), ui, storage());
        assertTrue(ui.buildResponse().startsWith("How to use Clover"));
    }

    @Test
    void listTutoreesAndFindTutoree_emptyResults_buildExpectedResponses() throws CloverException {
        TutoreeList tutorees = new TutoreeList(List.of(new Tutoree("Alice", "Home", "50")));
        Ui ui = new Ui();

        new ListTutoreesCommand().execute(new TaskList(), tutorees, ui, storage());
        assertTrue(ui.buildResponse().contains("1. Alice"));
        ui.clearResponse();
        new FindTutoreeCommand("nobody").execute(new TaskList(), tutorees, ui, storage());
        assertEquals("No matching learning companions found.", ui.buildResponse());
    }

    @Test
    void findTutoree_blankKeywordAndTurkishLocale_validatesAndSearchesIndependentlyOfDefaultLocale()
            throws CloverException {
        org.junit.jupiter.api.Assertions.assertThrows(CloverException.class, () -> new FindTutoreeCommand("  "));
        java.util.Locale originalLocale = java.util.Locale.getDefault();
        java.util.Locale.setDefault(java.util.Locale.forLanguageTag("tr-TR"));
        try {
            TutoreeList tutorees = new TutoreeList(List.of(new Tutoree("FIX", "Home", "50")));
            Ui ui = new Ui();
            new FindTutoreeCommand("fix").execute(new TaskList(), tutorees, ui, storage());
            assertTrue(ui.buildResponse().contains("FIX"));
        } finally {
            java.util.Locale.setDefault(originalLocale);
        }
    }

    @Test
    void exitCommand_identifiesExit() {
        ExitCommand command = new ExitCommand();

        assertTrue(command.isExit());
    }

    private Storage storage() {
        return new Storage(tempDir.resolve("tasks.txt"), tempDir.resolve("tutorees.txt"));
    }
}
