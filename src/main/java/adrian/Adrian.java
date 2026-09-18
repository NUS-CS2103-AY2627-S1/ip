package adrian;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs the Adrian task manager and processes commands entered by the user.
 */
public class Adrian {
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final String WELCOME_MESSAGE =
            "Adrian online. Hello, Rocky.\nMission tasks ready. Amaze. Amaze. Amaze.";
    private static final String GOODBYE_MESSAGE =
            "Mission log secured, Rocky. We science again soon!";
    private static final String LOADING_ERROR_MESSAGE =
            "OOPS!!! Rocky, the mission log could not be loaded.";
    private static final String SAVING_ERROR_MESSAGE =
            "OOPS!!! Rocky, I could not save the mission log.";

    private final Storage storage;
    private final ArrayList<Task> tasks;
    private final boolean hasLoadingError;
    private boolean isExitRequested;

    /**
     * Creates an Adrian task manager using the default data file.
     */
    public Adrian() {
        this(new Storage());
    }

    /**
     * Creates an Adrian task manager using the specified storage service.
     *
     * @param storage storage service used to load and save tasks.
     */
    Adrian(Storage storage) {
        assert storage != null : "Storage dependency must not be null";
        this.storage = storage;

        ArrayList<Task> loadedTasks;
        boolean didLoadingFail = false;

        try {
            loadedTasks = storage.loadTasks();
        } catch (IOException e) {
            loadedTasks = new ArrayList<>();
            didLoadingFail = true;
        }

        tasks = loadedTasks;
        hasLoadingError = didLoadingFail;
    }

    /**
     * Starts the console application and processes commands until the user exits.
     *
     * @param args command-line arguments; not used by Adrian.
     */
    public static void main(String[] args) {
        Adrian adrian = new Adrian();
        Ui ui = new Ui();

        if (adrian.hasLoadingError) {
            ui.showLoadingError();
        }

        ui.showWelcome();

        while (!adrian.isExitRequested()) {
            String input = ui.readCommand();

            ui.showDivider();
            ui.showResponse(adrian.getResponse(input));
            ui.showDivider();
        }

        ui.close();
    }

    /**
     * Returns the greeting shown when the application starts.
     *
     * @return greeting, prefixed with a loading error if saved tasks could not be loaded.
     */
    public String getWelcomeMessage() {
        if (hasLoadingError) {
            return LOADING_ERROR_MESSAGE + "\n\n" + WELCOME_MESSAGE;
        }

        return WELCOME_MESSAGE;
    }

    /**
     * Processes one user command and returns the response to display.
     *
     * @param input command entered by the user.
     * @return response produced by the command.
     */
    public String getResponse(String input) {
        try {
            return executeCommand(input.trim());
        } catch (InvalidInputException e) {
            return "OOPS!!! Rocky, " + e.getMessage();
        } catch (IOException e) {
            return SAVING_ERROR_MESSAGE;
        }
    }

    /**
     * Returns whether the user has entered the exit command.
     *
     * @return true if Adrian should stop accepting commands; false otherwise.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Executes a validated command and updates stored tasks when necessary.
     *
     * @param input trimmed command entered by the user.
     * @return response produced by the command.
     * @throws InvalidInputException if the command or its arguments are invalid.
     * @throws IOException if updated tasks cannot be saved.
     */
    private String executeCommand(String input) throws InvalidInputException, IOException {
        if (input.equals("bye")) {
            isExitRequested = true;
            return GOODBYE_MESSAGE;
        }

        if (input.equals("list")) {
            return formatTaskList("Rocky, here is our mission task list:", tasks);
        } else if (input.equals("find")) {
            throw new InvalidInputException("Please specify a keyword to find.");
        } else if (input.startsWith("find ")) {
            return findTasks(input);
        } else if (input.equals("todo")) {
            throw new InvalidInputException("The description of a todo cannot be empty.");
        } else if (input.startsWith("todo ")) {
            return addTodo(input);
        } else if (input.equals("deadline")) {
            throw new InvalidInputException("A deadline needs a description and /by time.");
        } else if (input.startsWith("deadline ")) {
            return addDeadline(input);
        } else if (input.equals("event")) {
            throw new InvalidInputException("An event needs a description, /from time, and /to time.");
        } else if (input.startsWith("event ")) {
            return addEvent(input);
        } else if (input.equals("duration")) {
            throw new InvalidInputException(
                    "A fixed-duration task needs a description and /for minutes.");
        } else if (input.startsWith("duration ")) {
            return addFixedDurationTask(input);
        } else if (input.equals("mark")) {
            throw new InvalidInputException("Please specify which task to mark.");
        } else if (input.startsWith("mark ")) {
            return markTask(input);
        } else if (input.equals("unmark")) {
            throw new InvalidInputException("Please specify which task to unmark.");
        } else if (input.startsWith("unmark ")) {
            return unmarkTask(input);
        } else if (input.equals("delete")) {
            throw new InvalidInputException("Please specify which task to delete.");
        } else if (input.startsWith("delete ")) {
            return deleteTask(input);
        }

        throw new InvalidInputException("Sorry, I don't understand that command.");
    }

    /**
     * Finds tasks whose descriptions contain the supplied keyword.
     *
     * @param input find command containing the keyword.
     * @return numbered matching tasks.
     * @throws InvalidInputException if the keyword is empty.
     */
    private String findTasks(String input) throws InvalidInputException {
        String keyword = input.substring(5).trim();

        if (keyword.isEmpty()) {
            throw new InvalidInputException("Please specify a keyword to find.");
        }

        ArrayList<Task> matchingTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.matchesDescription(keyword)) {
                matchingTasks.add(task);
            }
        }

        return formatTaskList("I found these matching mission tasks, Rocky:", matchingTasks);
    }

    /**
     * Adds a todo described by the supplied command.
     *
     * @param input todo command containing the description.
     * @return confirmation containing the added task and updated task count.
     * @throws InvalidInputException if the description is empty.
     * @throws IOException if the updated task list cannot be saved.
     */
    private String addTodo(String input) throws InvalidInputException, IOException {
        String description = input.substring(5).trim();

        if (description.isEmpty()) {
            throw new InvalidInputException("The description of a todo cannot be empty.");
        }

        return addTask(new Todo(description));
    }

    /**
     * Adds a deadline described by the supplied command.
     *
     * @param input deadline command containing the description and due time.
     * @return confirmation containing the added deadline and updated task count.
     * @throws InvalidInputException if the command details are missing or invalid.
     * @throws IOException if the updated task list cannot be saved.
     */
    private String addDeadline(String input) throws InvalidInputException, IOException {
        String details = input.substring(9);
        int byIndex = details.indexOf(" /by ");

        if (byIndex == -1) {
            throw new InvalidInputException("Please specify the deadline using /by.");
        }

        String description = details.substring(0, byIndex).trim();
        String dueDateTimeText = details.substring(byIndex + 5).trim();

        if (description.isEmpty()) {
            throw new InvalidInputException("The description of a deadline cannot be empty.");
        }

        if (dueDateTimeText.isEmpty()) {
            throw new InvalidInputException("The deadline time cannot be empty.");
        }

        LocalDateTime dueDateTime = parseDateTime(dueDateTimeText);
        return addTask(new Deadline(description, dueDateTime));
    }

    /**
     * Adds an event described by the supplied command.
     *
     * @param input event command containing its description and time range.
     * @return confirmation containing the added event and updated task count.
     * @throws InvalidInputException if the command details are missing or invalid.
     * @throws IOException if the updated task list cannot be saved.
     */
    private String addEvent(String input) throws InvalidInputException, IOException {
        String details = input.substring(6);
        int fromIndex = details.indexOf(" /from ");
        int toIndex = details.indexOf(" /to ");

        if (fromIndex == -1 || toIndex == -1 || toIndex <= fromIndex) {
            throw new InvalidInputException("Please specify an event using /from and /to.");
        }

        String description = details.substring(0, fromIndex).trim();
        String startDateTimeText = details.substring(fromIndex + 7, toIndex).trim();
        String endDateTimeText = details.substring(toIndex + 5).trim();

        if (description.isEmpty()) {
            throw new InvalidInputException("The description of an event cannot be empty.");
        }

        if (startDateTimeText.isEmpty() || endDateTimeText.isEmpty()) {
            throw new InvalidInputException("The event start and end times cannot be empty.");
        }

        LocalDateTime startDateTime = parseDateTime(startDateTimeText);
        LocalDateTime endDateTime = parseDateTime(endDateTimeText);

        if (!endDateTime.isAfter(startDateTime)) {
            throw new InvalidInputException("The event end time must be after its start time.");
        }

        return addTask(new Event(description, startDateTime, endDateTime));
    }

    /**
     * Adds a fixed-duration task described by the supplied command.
     *
     * @param input duration command containing the description and number of minutes.
     * @return confirmation containing the added task and updated task count.
     * @throws InvalidInputException if the description or duration is missing or invalid.
     * @throws IOException if the updated task list cannot be saved.
     */
    private String addFixedDurationTask(String input) throws InvalidInputException, IOException {
        String details = input.substring(8);
        int forIndex = details.lastIndexOf(" /for");

        if (forIndex == -1) {
            throw new InvalidInputException("Please specify the task duration using /for.");
        }

        int durationStartIndex = forIndex + 5;

        if (durationStartIndex < details.length()
                && !Character.isWhitespace(details.charAt(durationStartIndex))) {
            throw new InvalidInputException("Please specify the task duration using /for.");
        }

        String description = details.substring(0, forIndex).trim();
        String durationText = details.substring(durationStartIndex).trim();

        if (description.isEmpty()) {
            throw new InvalidInputException(
                    "The description of a fixed-duration task cannot be empty.");
        }

        if (durationText.isEmpty()) {
            throw new InvalidInputException("The task duration cannot be empty.");
        }

        int durationInMinutes;

        try {
            durationInMinutes = Integer.parseInt(durationText);
        } catch (NumberFormatException e) {
            throw new InvalidInputException(
                    "Please specify the duration as a whole number of minutes.");
        }

        if (durationInMinutes <= 0) {
            throw new InvalidInputException("The task duration must be positive.");
        }

        return addTask(new FixedDurationTask(description, durationInMinutes));
    }

    /**
     * Adds a task, saves the updated list, and formats the confirmation message.
     *
     * @param task task to add.
     * @return confirmation containing the added task and updated task count.
     * @throws IOException if the updated task list cannot be saved.
     */
    private String addTask(Task task) throws IOException {
        tasks.add(task);

        try {
            storage.saveTasks(tasks);
        } catch (IOException e) {
            tasks.remove(tasks.size() - 1);
            throw e;
        }

        return formatTaskAdded(task);
    }

    /**
     * Marks the task identified by the supplied command as completed.
     *
     * @param input mark command containing a task number.
     * @return confirmation containing the marked task.
     * @throws InvalidInputException if the task number is invalid.
     * @throws IOException if the updated task list cannot be saved.
     */
    private String markTask(String input) throws InvalidInputException, IOException {
        int taskNumber = getTaskNumber(input, "mark", tasks.size());
        Task task = tasks.get(taskNumber - 1);
        boolean wasDone = task.isDone();
        task.markAsDone();

        try {
            storage.saveTasks(tasks);
        } catch (IOException e) {
            restoreTaskStatus(task, wasDone);
            throw e;
        }

        return "Amaze! This mission task is complete:\n  " + task;
    }

    /**
     * Marks the task identified by the supplied command as incomplete.
     *
     * @param input unmark command containing a task number.
     * @return confirmation containing the unmarked task.
     * @throws InvalidInputException if the task number is invalid.
     * @throws IOException if the updated task list cannot be saved.
     */
    private String unmarkTask(String input) throws InvalidInputException, IOException {
        int taskNumber = getTaskNumber(input, "unmark", tasks.size());
        Task task = tasks.get(taskNumber - 1);
        boolean wasDone = task.isDone();
        task.markAsNotDone();

        try {
            storage.saveTasks(tasks);
        } catch (IOException e) {
            restoreTaskStatus(task, wasDone);
            throw e;
        }

        return "Understood, Rocky. This task is back in the mission queue:\n  " + task;
    }

    /**
     * Deletes the task identified by the supplied command.
     *
     * @param input delete command containing a task number.
     * @return confirmation containing the deleted task and updated task count.
     * @throws InvalidInputException if the task number is invalid.
     * @throws IOException if the updated task list cannot be saved.
     */
    private String deleteTask(String input) throws InvalidInputException, IOException {
        int taskNumber = getTaskNumber(input, "delete", tasks.size());
        Task removedTask = tasks.remove(taskNumber - 1);

        try {
            storage.saveTasks(tasks);
        } catch (IOException e) {
            tasks.add(taskNumber - 1, removedTask);
            throw e;
        }

        return "Task removed from the mission log, Rocky:\n  " + removedTask
                + "\nMission log now has " + tasks.size() + " tasks.";
    }

    /**
     * Restores a task's completion status after a failed save operation.
     *
     * @param task task whose status should be restored.
     * @param wasDone completion status before the attempted update.
     */
    private static void restoreTaskStatus(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
    }

    /**
     * Parses a date and time supplied in Adrian's command format.
     *
     * @param dateTimeText date and time text to parse.
     * @return parsed date and time.
     * @throws InvalidInputException if the text does not use the required format.
     */
    private static LocalDateTime parseDateTime(String dateTimeText) throws InvalidInputException {
        try {
            return LocalDateTime.parse(dateTimeText, INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Please use date format yyyy-MM-dd HHmm.");
        }
    }

    /**
     * Formats a heading followed by one-based task numbers.
     *
     * @param heading heading displayed before the tasks.
     * @param tasksToDisplay tasks to include in the response.
     * @return formatted heading and task list.
     */
    private static String formatTaskList(String heading, List<Task> tasksToDisplay) {
        assert heading != null : "Task-list heading must not be null";
        assert tasksToDisplay != null : "Task list must not be null";

        StringBuilder response = new StringBuilder(heading);

        for (int i = 0; i < tasksToDisplay.size(); i++) {
            response.append("\n")
                    .append(i + 1)
                    .append(".")
                    .append(tasksToDisplay.get(i));
        }

        return response.toString();
    }

    /**
     * Formats confirmation that a task was added.
     *
     * @param task task that was added.
     * @return formatted confirmation and updated task count.
     */
    private String formatTaskAdded(Task task) {
        assert task != null : "Added task must not be null";
        assert tasks.contains(task) : "Task must be added before formatting confirmation";

        return "Amaze. Amaze. Amaze. New mission task recorded:\n  " + task
                + "\nMission log now has " + tasks.size() + " tasks.";
    }

    /**
     * Extracts and validates a task number from a command.
     *
     * @param input full command entered by the user.
     * @param command command word that precedes the task number.
     * @param numberOfTasks number of tasks currently available.
     * @return the validated one-based task number.
     * @throws InvalidInputException if the number is invalid or does not identify an existing task.
     */
    private static int getTaskNumber(String input, String command, int numberOfTasks)
            throws InvalidInputException {
        String numberText = input.substring(command.length()).trim();

        try {
            int taskNumber = Integer.parseInt(numberText);

            if (taskNumber < 1 || taskNumber > numberOfTasks) {
                throw new InvalidInputException("That task number does not exist.");
            }

            assert taskNumber >= 1 && taskNumber <= numberOfTasks
                    : "Validated task number must be within bounds";
            return taskNumber;
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Please enter a valid task number.");
        }
    }
}
