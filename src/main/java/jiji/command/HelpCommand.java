package jiji.command;

import jiji.storage.Storage;
import jiji.task.TaskList;
import jiji.ui.Ui;

/**
 * Command that displays general or command-specific help information.
 */
public class HelpCommand extends Command {

    private final String topic;

    /**
     * Constructs a HelpCommand for general help or a specific topic.
     *
     * @param topic The specific command topic to show help for, or empty string for general help.
     */
    public HelpCommand(String topic) {
        this.topic = (topic != null) ? topic.trim().toLowerCase() : "";
    }

    /**
     * Constructs a HelpCommand for general help.
     */
    public HelpCommand() {
        this("");
    }

    /**
     * Executes the help command by displaying relevant guidance via the UI.
     *
     * @param tasks The task list.
     * @param ui The UI handler for displaying output.
     * @param storage The storage handler.
     * @return The formatted help text.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        assert ui != null : "Ui instance cannot be null";
        String helpText = getHelpText();
        ui.showHelp(helpText);
        return helpText;
    }

    /**
     * Generates the appropriate help text based on the requested topic.
     *
     * @return Formatted help string.
     */
    public String getHelpText() {
        if (topic.isEmpty()) {
            return getGeneralHelp();
        }
        return getSpecificHelp(topic);
    }

    /**
     * Returns a summary overview of all available commands.
     *
     * @return General help text string.
     */
    private String getGeneralHelp() {
        return "Available commands in Jiji:\n\n"
                + "[Add Tasks]\n"
                + "• todo <description>\n"
                + "• deadline <description> /by <time>\n"
                + "• event <desc> /from <start> /to <end>\n\n"
                + "[Manage Tasks]\n"
                + "• list [filter] - View tasks (pending/done)\n"
                + "• schedule <date> - View schedule for date\n"
                + "• mark <index> - Mark as done\n"
                + "• unmark <index> - Mark as not done\n"
                + "• delete <index> - Delete a task\n"
                + "• find <keyword> - Search by keyword\n\n"
                + "[General]\n"
                + "• stats - View task statistics\n"
                + "• help [command] - View command guide\n"
                + "• bye - Exit Jiji\n\n"
                + "Tip: Type 'help <command>' (e.g. 'help deadline') for details!";
    }

    /**
     * Returns detailed syntax and examples for a specific command.
     *
     * @param commandName The lowercase command word.
     * @return Command-specific help text.
     */
    private String getSpecificHelp(String commandName) {
        switch (commandName) {
            case "todo":
                return "Command: todo\n"
                        + "Syntax: todo <description>\n"
                        + "Description: Adds a task without date/time constraints.\n"
                        + "Example: todo read textbook";

            case "deadline":
                return "Command: deadline\n"
                        + "Syntax: deadline <desc> /by <time>\n"
                        + "Description: Adds a task due by a specific date/time.\n"
                        + "Formats: yyyy-MM-dd, d/M/yyyy, HHmm\n"
                        + "Example: deadline submit report /by 2026-08-30 1800";

            case "event":
                return "Command: event\n"
                        + "Syntax: event <desc> /from <start> /to <end>\n"
                        + "Description: Adds a task within a time frame.\n"
                        + "Formats: yyyy-MM-dd, d/M/yyyy, HHmm\n"
                        + "Example: event team sync /from Mon 2pm /to 4pm";

            case "list":
                return "Command: list\n"
                        + "Syntax: list [pending|done]\n"
                        + "Description: Displays all tasks, or filter by pending/done status.\n"
                        + "Example: list pending";

            case "mark":
                return "Command: mark\n"
                        + "Syntax: mark <index>\n"
                        + "Description: Marks the 1-based task as completed.\n"
                        + "Example: mark 2";

            case "unmark":
                return "Command: unmark\n"
                        + "Syntax: unmark <index>\n"
                        + "Description: Marks the 1-based task as incomplete.\n"
                        + "Example: unmark 2";

            case "delete":
                return "Command: delete\n"
                        + "Syntax: delete <index>\n"
                        + "Description: Removes the 1-based task from the list.\n"
                        + "Example: delete 1";

            case "find":
                return "Command: find\n"
                        + "Syntax: find <keyword>\n"
                        + "Description: Searches tasks containing the keyword.\n"
                        + "Example: find book";

            case "stats":
            case "statistics":
                return "Command: stats\n"
                        + "Syntax: stats\n"
                        + "Description: Displays overall progress, completion rate, and type breakdown.\n"
                        + "Example: stats";

            case "schedule":
                return "Command: schedule\n"
                        + "Syntax: schedule <date|today>\n"
                        + "Description: Displays tasks occurring on or due by the specified date.\n"
                        + "Formats: yyyy-MM-dd, d/M/yyyy, today\n"
                        + "Example: schedule 2026-08-30";

            case "help":
                return "Command: help\n"
                        + "Syntax: help [command]\n"
                        + "Description: Shows command guide or details.\n"
                        + "Example: help deadline";

            case "bye":
                return "Command: bye\n"
                        + "Syntax: bye\n"
                        + "Description: Saves all tasks and exits Jiji.";

            default:
                return "Unknown command: " + commandName + ".\n"
                        + "Type 'help' to see the list of all available commands.";
        }
    }
}
