package labubu;

/**
 * Provides the user-facing introduction and exit messages for Labubu.
 */
public class Ui {
    private static final String INTRO =
            "Hello! I'm Labubu. A task tracker bot.\n\n"
                    + "Tasks available: \n"
                    + "To-do: todo [task-title] \n"
                    + "Deadline: deadline [task-title] /by [date-time] \n"
                    + "Event: event [task-title] /from [date-time] /to [date-time] \n\n"
                    + "Toggle help menu with 'help' to see more commands.\n\n"
                    + "Enter dates in the following format: dd/MM/yyyy <optional>HH:mm</optional>  "
                    + "e.g: 06/07/2026 18:30\n";

    private static final String EXIT_MESSAGE =
            "____________________________________________________________\n"
                    + "Bye! Hope to see you again soon!\n"
                    + "____________________________________________________________\n";

    private static final String HELP_MESSAGE =
            "Hello! I'm Labubu, your personal task-tracking bot.\n\n"
                    + "Here are the commands you can use:\n"
                    + "______________________________________________________\n"
                    + "ADD TASKS\n"
                    + "______________________________________________________\n"
                    + "  todo [task title]\n"
                    + "  Adds a task without a date or time.\n\n"
                    + "  deadline [task title] /by [date and time]\n"
                    + "  Adds a task that must be completed by a specified date and time.\n\n"
                    + "  event [task title] /from [start date and time] /to [end date and time]\n"
                    + "  Adds a task that takes place during a specified period.\n\n"
                    + "Dates should use this format:\n"
                    + "  dd/MM/yyyy\n"
                    + "  dd/MM/yyyy HH:mm\n\n"
                    + "Examples:\n"
                    + "  todo read book\n"
                    + "  deadline return book /by 06/07/2026\n"
                    + "  event team meeting /from 06/07/2026 14:00 /to 06/07/2026 15:00\n\n"
                    + "______________________________________________________\n"
                    + "MANAGE TASKS\n"
                    + "______________________________________________________\n"
                    + "  list\n"
                    + "  Displays all tasks.\n\n"
                    + "  find [keyword]\n"
                    + "  Displays tasks whose titles contain the keyword.\n\n"
                    + "  mark [task number]\n"
                    + "  Marks a task as completed.\n\n"
                    + "  unmark [task number]\n"
                    + "  Marks a task as incomplete.\n\n"
                    + "  delete [task number]\n"
                    + "  Deletes a task.\n\n"
                    + "______________________________________________________\n"
                    + "OTHER\n"
                    + "______________________________________________________\n"
                    + "  help\n"
                    + "  Opens/closes this help guide.\n\n"
                    + "  bye\n"
                    + "  exit\n"
                    + "  quit\n"
                    + "  Saves your tasks and closes Labubu.\n\n"
                    + "Task status indicators:\n"
                    + "  [ ] Incomplete\n"
                    + "  [X] Completed\n\n"
                    + "Task numbers are based on their position in the task list.";

    /**
     * Returns the application's introduction message.
     *
     * @return Introduction message.
     */
    public String getIntro() {
        return INTRO;
    }

    /**
     * Returns the application's exit message.
     *
     * @return Exit message.
     */
    public String getExit() {
        return EXIT_MESSAGE;
    }

    /**
     * Returns the detailed command guide.
     *
     * @return Help guide message.
     */
    public String getHelp() {
        return HELP_MESSAGE;
    }
}
