/**
 * Starts the BENJI chatbot application.
 */
package benji;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Processes user commands and manages the user's task list.
 *
 * <p>The {@link #getResponse(String)} method allows both the console UI and a future GUI
 * to use the same chatbot logic.</p>
 */
public class Benji {
    private final TaskList tasks;

    /** Creates BENJI and loads tasks saved from earlier sessions. */
    public Benji() {
        tasks = new TaskList(Storage.loadTasks());
    }

    /**
     * Starts BENJI in the console.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Benji benji = new Benji();
        ui.showWelcome();

        while (true) {
            String userInput = ui.readCommand();
            System.out.println(benji.getResponse(userInput));

            if (Parser.getCommand(userInput) == Command.BYE) {
                break;
            }
        }
    }

    /**
     * Processes one command and returns BENJI's reply.
     *
     * @param userInput command entered by the user
     * @return BENJI's response to the command
     */
    public String getResponse(String userInput) {
        Command command = Parser.getCommand(userInput);

        try {
            switch (command) {
                case BYE:
                    return "Bye. Hope to see you again soon!";
                case LIST:
                    return listTasks();
                case MARK:
                    return updateTaskStatus(userInput, true);
                case UNMARK:
                    return updateTaskStatus(userInput, false);
                case TODO:
                    return addTodo(userInput);
                case DEADLINE:
                    return addDeadline(userInput);
                case EVENT:
                    return addEvent(userInput);
                case DELETE:
                    return deleteTask(userInput);
                case FIND:
                    return findTasks(userInput);
                case HELP:
                    return "Use the Help button in the JavaFx app to view the command guide.";
                default:
                    throw new BenjiException("I beg your pardon, I am afraid I do not recognise this command.");
            }
        } catch (BenjiException e) {
            return e.getMessage();
        }
    }

    /** Returns a numbered list of all tasks. */
    private String listTasks() {
        if (tasks.size() == 0) {
            return "Your list is empty";
        }
        StringBuilder reply = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            reply.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return reply.toString();
    }

    /** Marks or unmarks a task and saves the updated list. */
    private String updateTaskStatus(String userInput, boolean isDone) throws BenjiException {
        String commandWord = isDone ? "mark" : "unmark";
        try {
            int taskNumber = Integer.parseInt(userInput.substring(commandWord.length()).trim());
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new BenjiException("I do apologize, but this task number appears to be non-existent.");
            }

            Task task = tasks.get(taskNumber - 1);
            if (isDone) {
                task.markAsDone();
                Storage.saveTasks(tasks);
                return "Splendid! I have marked this task as completed:\n  " + task;
            }

            task.markAsNotDone();
            Storage.saveTasks(tasks);
            return "OK, I've marked this task as not done yet:\n  " + task;
        } catch (NumberFormatException e) {
            throw new BenjiException("Please enter a whole task number after " + commandWord + ".");
        }
    }

    /** Adds a todo task and saves the updated list. */
    private String addTodo(String userInput) throws BenjiException {
        String description = userInput.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new BenjiException("Please enter a description after todo");
        }

        Task task = new Todo(description);
        return addTaskAndSave(task);
    }

    /**
     * Returns the task-count message with correct singular or plural wording.
     *
     * @return message stating the current number of tasks
     */
    private String getTaskCountMessage() {
        int taskCount = tasks.size();
        String taskWord = taskCount == 1 ? "task" : "tasks";

        return "Now you have " + taskCount + " " + taskWord + " in the list.";
    }

    /**
     * Add a task, saves the updated list, and returns a confirmation message.
     *
     * @param task task to add
     * @return response confirming
     */
    private String addTaskAndSave(Task task) {
        tasks.add(task);
        Storage.saveTasks(tasks);
        return "Got it. I've added this task:\n  " + task + "\n"
                + getTaskCountMessage();
    }

    /** Adds a deadline task and saves the updated list. */
    private String addDeadline(String userInput) throws BenjiException {
        String taskDescription = userInput.substring("deadline".length()).trim();
        if (taskDescription.isEmpty()) {
            throw new BenjiException("Please enter a description after deadline.");
        }

        int byIndex = taskDescription.indexOf("/by");
        if (byIndex == -1) {
            throw new BenjiException("Please ensure '/by TIME' is included in your deadline description.");
        }

        String description = taskDescription.substring(0, byIndex).trim();
        if (description.isEmpty()) {
            throw new BenjiException("Please enter a description after deadline");
        }

        String by = taskDescription.substring(byIndex + "/by".length()).trim();
        if (by.isEmpty()) {
            throw new BenjiException("Please enter the timing after /by");
        }

        Task task = new Deadline(description, by);
        return addTaskAndSave(task);
    }

    /** Adds an event task and saves the updated list. */
    private String addEvent(String userInput) throws BenjiException {
        String taskDescription = userInput.substring("event".length()).trim();
        if (taskDescription.isEmpty()) {
            throw new BenjiException("Please enter a description after event.");
        }

        int startIndex = taskDescription.indexOf("/from");
        int endIndex = taskDescription.indexOf("/to");
        if (startIndex == -1 || endIndex == -1 || startIndex > endIndex) {
            throw new BenjiException("Please ensure both '/from START' appears before '/to END'"
                    + " in your event description.");
        }

        String description = taskDescription.substring(0, startIndex).trim();
        if (description.isEmpty()) {
            throw new BenjiException("Please enter a description after event");
        }

        String start = taskDescription.substring(startIndex + "/from".length(), endIndex).trim();
        if (start.isEmpty()) {
            throw new BenjiException("Please enter start timing after /from");
        }

        String end = taskDescription.substring(endIndex + "/to".length()).trim();
        if (end.isEmpty()) {
            throw new BenjiException("Please enter end timing after /to");
        }
        validateEventDateOrder(start, end);

        Task task = new Event(description, start, end);
        return addTaskAndSave(task);
    }

    /**
     * Validates that comparable event dates are in chronological order.
     *
     * <p>Events using free-form times, such as "2pm", remain supported.
     * Date ordering is checked only when both values use YYYY-MM-DD.</p>
     *
     * @param start event start date or time
     * @param end event end date or time
     * @throws BenjiException if the start date is after the end date
     */
    private void validateEventDateOrder(String start, String end)
            throws BenjiException {
        try {
            // only checks for YYYY-MM-DD format
            // if start and empty is not in YYYY-MM-DD format, startDate and endDate will
            // produce the DateTimeParseException
            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);

            if (startDate.isAfter(endDate)) {
                throw new BenjiException(
                        "Event start date must be on or before the end date.");
            }
        } catch (DateTimeParseException e) {
            // Preserve support for free-form event times such as "2pm" or "fri"
            // This exception can be safely ignore if start and end is not in YYYY-MM-DD format (refer to user guide)
        }
    }

    /** Deletes a task and saves the updated list. */
    private String deleteTask(String userInput) throws BenjiException {
        try {
            String taskNumberText = userInput.substring("delete".length()).trim();
            if (taskNumberText.isEmpty()) {
                throw new BenjiException("Please enter a task number after delete");
            }

            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new BenjiException("I do apologize, but this task number appears to be non-existent.");
            }

            Task deletedTask = tasks.delete(taskNumber - 1);
            Storage.saveTasks(tasks);
            return "Noted. I've removed this task:\n  " + deletedTask + "\n"
                    + getTaskCountMessage();
        } catch (NumberFormatException e) {
            throw new BenjiException("Please enter a whole task number after delete.");
        }
    }

    /** Finds tasks whose display text contains the supplied keyword. */
    private String findTasks(String userInput) throws BenjiException {
        String keyword = userInput.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new BenjiException("Please enter a keyword after find.");
        }

        // Use a stream to keep only tasks whose display text contains the keyword.
        List<Task> matchingTasks = tasks.getTasks().stream()
                .filter(task -> task.toString().contains(keyword))
                .toList();
        StringBuilder reply = new StringBuilder("Here are the matching tasks in your list:");

        // A normal loop keeps the result numbering clear and simple.
        for (int i = 0; i < matchingTasks.size(); i++) {
            reply.append("\n").append(i + 1).append(".").append(matchingTasks.get(i));
        }

        return reply.toString();
    }
}
