package dobby.command;

import dobby.logic.DobbyLogic;

/**
 * Displays guidance for the commands supported by Dobby.
 */
public final class HelpCommand extends Command {
    private static final String HELP_MESSAGE = "> Dobby can help with these commands:\n"
            + "Create tasks:\n"
            + "  todo <description> - Add a todo task.\n"
            + "  deadline <description> /by <date/time> - Add a task with a deadline.\n"
            + "  event <description> /from <date/time> /to <date/time> - Add an event.\n"
            + "View tasks:\n"
            + "  list - Show all tasks.\n"
            + "  find <search text> - Show tasks matching text.\n"
            + "Update tasks:\n"
            + "  mark <task number> - Mark a task as done.\n"
            + "  unmark <task number> - Mark a task as not done.\n"
            + "  delete <task number> - Delete a task.\n"
            + "Other:\n"
            + "  help - Show this help page.\n"
            + "  bye - Exit Dobby.\n"
            + "Dates: use yyyy-MM-dd or d/M/yyyy. Times are optional; use HHmm or HH:mm.\n"
            + "Example: deadline return book /by 2019-12-02 1800";

    /** Returns the command guide without changing application state. */
    @Override
    public String execute(DobbyLogic logic) {
        return HELP_MESSAGE;
    }
}
