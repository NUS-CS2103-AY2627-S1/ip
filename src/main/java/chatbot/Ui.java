package chatbot;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Formats chatbot responses for display to the user.
 */
public class Ui {
    private static final String BANNER = "\\ \\ / /| | | || \\ | |\n"
                            + " \\ V / | | | ||  \\| |\n"
                            + "  | |  | |_| || |\\  |\n"
                            + "  |_|   \\___/ |_| \\_|\n";

    /**
     * Returns the welcome message with the application banner.
     *
     * @return Welcome message with the application banner.
     */
    public String showWelcomeMessage() {
        return BANNER + "\nHello! I'm Yun.\nWhat can I do for you?\nYou can use 'help' to see all commands :)";
    }

    /**
     * Returns the exit message.
     *
     * @return Exit message.
     */
    public String showExitMessage() {
        return "Buh bai. Cya again soon!\n\n";
    }

    /**
     * Returns the message from an encountered exception.
     *
     * @param e Exception encountered.
     * @return Exception message.
     */
    public String showError(Exception e) {
        return e.getMessage();
    }

    /**
     * Formats all tasks in the given task list.
     *
     * @param taskList Task list to format.
     * @return Formatted task list.
     */
    public String showTasks(TaskList taskList) {
        StringBuilder result = new StringBuilder("Okai here are the tasks in your list:\n");
        for (int i = 0; i < taskList.size(); i++) {
            result.append(i + 1).append(".").append(taskList.get(i)).append("\n");
        }
        return result.append("\n").toString();
    }

    /**
     * Lists tasks whose descriptions contain the given keyword, ignoring case.
     *
     * @param taskList Tasks to search.
     * @param keyword Text to search for.
     * @return Formatted matching tasks.
     */
    public String showMatchingTasks(TaskList taskList, String keyword) {
        StringBuilder result = new StringBuilder("Bet here's what we pulled up from yo list:\n");
        int count = 1;
        String searchTerm = keyword.toLowerCase();
        for (Task task : taskList) {
            if (task.getDescription().toLowerCase().contains(searchTerm)) {
                result.append(count).append(".").append(task).append("\n");
                count++;
            }
        }
        return result.append("\n").toString();
    }

    /**
     * Returns a message confirming that a task was marked complete.
     *
     * @param task Task that was marked.
     * @return Formatted confirmation message.
     */
    public String showMarked(Task task) {
        return "Nice one broski! I've marked this task as done for ya:\n" + task.toString()
            + "\n\n";
    }

    /**
     * Returns a message confirming that a task was marked incomplete.
     *
     * @param task Task that was marked incomplete.
     * @return Formatted confirmation message.
     */
    public String showUnmarked(Task task) {
        return "Aw man ok lemme mark this undone:\n"
            + task.toString() + "\n\n";
    }

    /**
     * Returns a message confirming that a task was added to the list.
     *
     * @param task Task that was added.
     * @param size Current number of tasks in the list.
     * @return Formatted confirmation message.
     */
    public String showAdded(Task task, int size) {
        return "Bet. I've added this task:\n"
            + task.toString()
            + "\n\nNow you have " + size + " task(s) in the list." + "\n\n";
    }

    /**
     * Returns a message confirming that a task was deleted from the list.
     *
     * @param task Task that was deleted.
     * @param newSize New number of tasks in the list.
     * @return Formatted confirmation message.
     */
    public String showDeleted(Task task, int newSize) {
        return "Ok! I've removed this task:\n"
            + task.toString()
            + "\n\nNow you have " + newSize + " task(s) in the list." + "\n\n";
    }

    /**
     * Returns a message showing a list of all commands.
     *
     * @return List of all commands.
     */
    public String showHelp() {
        return "Ok! Here is the list of all available commands:\n"
            + "on, find, mark, unmark, delete, todo, event, deadline, bye, list, help";
    }

    /**
     * Lists all events and deadlines that occur on a specific date.
     * For events, only lists events that occur on start/end date.
     *
     * @param taskList Task list to search.
     * @param dateText Date specified by the user.
     * @return Formatted tasks occurring on the specified date.
     */
    public String listTasksOnDate(TaskList taskList, String dateText) {
        try {
            LocalDate date = LocalDate.parse(dateText, DateTimeFormats.DATE);
            StringBuilder result = new StringBuilder(
                "Ok I've pulled up the list of events and deadlines occurring on "
                    + date.format(DateTimeFormats.DISPLAY_DATE) + ":\n"
            );
            int count = 1;
            for (Task task : taskList) {
                if (occursOnDate(task, date)) {
                    result.append(count)
                        .append(".")
                        .append(task)
                        .append("\n");
                    count++;
                }

            }
            return result.append("\n").toString();
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Broski please input a date in the format YYYY-MM-DD");
        }
    }

    /**
     * Determines whether a deadline or event occurs on the specified date.
     *
     * @param task Task to check.
     * @param date Date against which the task is checked.
     * @return Whether the task occurs on the specified date.
     */
    private boolean occursOnDate(Task task, LocalDate date) {
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return deadline.getDeadline().toLocalDate().equals(date);
        }

        if (task instanceof Event) {
            Event event = (Event) task;
            LocalDate eventStart = event.getStart().toLocalDate();
            LocalDate eventEnd = event.getEnd().toLocalDate();
            return !date.isBefore(eventStart) && !date.isAfter(eventEnd);
        }

        return false;
    }


}
