package elsa.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import elsa.Dates;
import elsa.ElsaException;
import elsa.storage.Storage;
import elsa.task.TaskList;
import elsa.ui.Ui;

/** Tests {@link HelpCommand}, which lists how every command is written. */
public class HelpCommandTest {

    private static Storage storeIn(Path folder) {
        return new Storage(folder.resolve("tasks.txt").toString());
    }

    /**
     * Help is where a window user discovers the chatbot, so a command missing
     * from it is one they have no way of finding.
     */
    @Test
    public void execute_always_namesEveryCommandAUserCanType(@TempDir Path folder)
            throws ElsaException {
        String help = new HelpCommand().execute(new TaskList(), new Ui(), storeIn(folder));

        for (String usage : CommandType.getUsages()) {
            assertTrue(help.contains(usage), "help never mentions: " + usage);
        }
    }

    @Test
    public void execute_always_showsTheCommandsUnderTheirHeadings(@TempDir Path folder)
            throws ElsaException {
        String help = new HelpCommand().execute(new TaskList(), new Ui(), storeIn(folder));

        for (String heading : CommandType.getUsagesByGroup().keySet()) {
            assertTrue(help.contains(heading), "help never shows the heading: " + heading);
        }
    }

    @Test
    public void execute_always_saysHowToWriteADate(@TempDir Path folder) throws ElsaException {
        String help = new HelpCommand().execute(new TaskList(), new Ui(), storeIn(folder));

        assertTrue(help.contains(Dates.ACCEPTED_FORMS), "help never says how to write a date");
    }

    @Test
    public void execute_always_leavesTheStoreAlone(@TempDir Path folder) throws ElsaException {
        Path file = folder.resolve("tasks.txt");

        new HelpCommand().execute(new TaskList(), new Ui(), new Storage(file.toString()));

        assertFalse(Files.exists(file), "asking for help wrote to the disk");
    }
}
