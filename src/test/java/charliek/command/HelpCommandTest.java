package charliek.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import charliek.ui.Ui;

/**
 * Tests general and command-specific help rendering.
 */
class HelpCommandTest {
    /**
     * Verifies that general help includes every command's usage and example.
     */
    @Test
    void execute_withoutCommand_showsAllCommandHelp() {
        StringBuilder output = new StringBuilder();
        new HelpCommand(new Ui(output::append), "").execute();

        assertEquals("     Available commands - the winning playbook\n\n"
                + "     todo <description>\n"
                + "       Add an undated to-do task.\n"
                + "       Example: todo buy milk\n\n"
                + "     deadline <description> /by <date/time>\n"
                + "       Add a task with a deadline.\n"
                + "       Example: deadline submit report /by 2/12/2019\n\n"
                + "     event <description> /from <date/time> /to <date/time>\n"
                + "       Add an event task.\n"
                + "       Example: event project meeting /from 2026-08-06 2pm /to 2026-08-06 4pm\n\n"
                + "     list [time]\n"
                + "       Display tasks, optionally in chronological order.\n"
                + "       Example: list time\n\n"
                + "     find <keyword>\n"
                + "       Find tasks containing a keyword.\n"
                + "       Example: find book\n\n"
                + "     mark <number>\n"
                + "       Mark a task as complete.\n"
                + "       Example: mark 1\n\n"
                + "     unmark <number>\n"
                + "       Mark a task as incomplete.\n"
                + "       Example: unmark 1\n\n"
                + "     delete <number>\n"
                + "       Delete a task.\n"
                + "       Example: delete 1\n\n"
                + "     bye\n"
                + "       Exit CharlieK.\n"
                + "       Example: bye\n", output.toString().replace(System.lineSeparator(), "\n"));
    }

    /**
     * Verifies that detailed help shows the selected command's usage and example.
     */
    @Test
    void execute_withKnownCommand_showsDetailedHelp() {
        StringBuilder output = new StringBuilder();
        new HelpCommand(new Ui(output::append), " list ").execute();

        assertEquals("     Help: list\n\n"
                + "     Usage\n"
                + "       list [time]\n\n"
                + "     Description\n"
                + "       Display tasks, optionally in chronological order.\n\n"
                + "     Example\n"
                + "       list time\n", output.toString().replace(System.lineSeparator(), "\n"));
    }

    /**
     * Verifies that an unknown help target returns a useful response.
     */
    @Test
    void execute_withUnknownCommand_showsHelpfulError() {
        StringBuilder output = new StringBuilder();
        new HelpCommand(new Ui(output::append), "unknown").execute();

        assertEquals("     I do not have help for 'unknown'. Try 'help' to see the available commands.\n",
                output.toString().replace(System.lineSeparator(), "\n"));
    }

    /**
     * Verifies that help treats surrounding whitespace as presentation noise.
     */
    @Test
    void execute_withWhitespaceOnlyCommand_showsGeneralHelp() {
        StringBuilder output = new StringBuilder();

        new HelpCommand(new Ui(output::append), "   ").execute();

        assertTrue(output.toString().contains("Available commands"));
    }

    /**
     * Verifies that help requires both of its collaborators to be meaningful.
     */
    @Test
    void constructor_nullCollaborator_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new HelpCommand(null, "list"));
        assertThrows(NullPointerException.class, () -> new HelpCommand(new Ui(ignored -> { }), null));
    }
}
