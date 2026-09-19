package nova;

/**
 * Handles the user-facing output of the text-based chatbot interface, and holds
 * the messages that both interfaces show to the user.
 */
public class Ui {
    public static final String MESSAGE_TODO_EMPTY =
            "A todo needs a description, e.g. todo read book";
    public static final String MESSAGE_INVALID_DEADLINE =
            "A deadline needs a description and a /by date, e.g. deadline return book /by 2026-08-28";
    public static final String MESSAGE_DUPLICATE_DEADLINE_DATE =
            "One /by date is enough, e.g. deadline return book /by 2026-08-28";
    public static final String MESSAGE_INVALID_EVENT =
            "An event needs a description with /from and /to, e.g. event meeting /from 2026-08-28 /to 2026-08-29";
    public static final String MESSAGE_DUPLICATE_EVENT_DATE =
            "One /from and one /to is enough, e.g. event meeting /from 2026-08-28 /to 2026-08-29";
    public static final String MESSAGE_INVALID_EVENT_RANGE =
            "That event ends before it starts. The /from date must come before the /to date.";
    public static final String MESSAGE_INVALID_DATE =
            "That date doesn't look right. Use yyyy-MM-dd, e.g. deadline return book /by 2026-08-28";
    public static final String MESSAGE_INVALID_NUMBER =
            "That isn't a task number. Try: mark 2";
    public static final String MESSAGE_NUMBER_REQUIRED = "Which task? Add a number, e.g. mark 2";
    public static final String MESSAGE_ONE_TASK_NUMBER =
            "One task number at a time, e.g. mark 2";
    public static final String MESSAGE_FIND_EMPTY =
            "What should I look for? Try: find book";
    public static final String MESSAGE_EMPTY_INPUT =
            "I didn't catch that. Try: help";
    public static final String MESSAGE_EXTRA_ARGUMENTS =
            "The %s command takes no arguments.";
    public static final String MESSAGE_INVALID_COMMAND =
            "I don't know that one. I can do: todo, deadline, event, find, list, mark, unmark, delete,"
                    + " archive, help, bye";
    public static final String MESSAGE_LIST_EMPTY =
            "Nothing on the list yet. Add one with todo, deadline or event.";
    public static final String MESSAGE_COMMAND_HINT =
            "Type help for the full list, or try: todo read book, deadline return book /by 2026-09-20, list";
    public static final String MESSAGE_HELP =
            "Here is everything I can do:\n"
                    + "  todo <description>                        add a task\n"
                    + "  deadline <description> /by <date>         add a task with a deadline\n"
                    + "  event <description> /from <date> /to <date>\n"
                    + "                                            add an event\n"
                    + "  list                                      show every task\n"
                    + "  find <keyword>                            show the matching tasks\n"
                    + "  mark <number> / unmark <number>           change a task's status\n"
                    + "  delete <number>                           remove a task\n"
                    + "  archive                                   move done tasks to the archive\n"
                    + "  bye                                       save and close\n"
                    + "Dates look like 2026-09-20. The up and down arrow keys recall what you typed,"
                    + " Tab completes a command, and Ctrl+D switches between the dark and light themes.";
    public static final String MESSAGE_ARCHIVE_EMPTY = "Nothing completed to archive yet.";
    public static final String MESSAGE_ARCHIVE_HEADER =
            "Tidied up. These completed tasks are now in the archive:";

    private static final String BANNER = " _   _\n"
            + "| \\ | | _____   ____ _\n"
            + "|  \\| |/ _ \\ \\ / / _` |\n"
            + "| |\\  | (_) \\ V / (_| |\n"
            + "|_| \\_|\\___/ \\_/ \\__,_|\n";

    /**
     * Prints the chatbot banner, the greeting and any startup warning.
     *
     * @param startupWarning the warning to show, or an empty string when there is none
     */
    public void greet(String startupWarning) {
        showMessage(BANNER, Nova.MESSAGE_GREETING);
        if (!startupWarning.isEmpty()) {
            showMessage(startupWarning);
        }
    }

    /**
     * Prints each given message on its own line.
     *
     * @param messages the messages to print
     */
    public void showMessage(String... messages) {
        for (String message : messages) {
            System.out.println(message);
        }
    }
}
