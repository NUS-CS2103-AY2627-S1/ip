package rice;

import java.util.List;

/**
 * Runs the Rice task manager application.
 */
public class Rice {
    private Parser parser = new Parser();
    private Storage storage = new Storage("data", "listOfTasks.txt");
    private Ui ui = new Ui();
    private TaskList tasks = new TaskList();
    private boolean hasLoadedSavedTasks;

    /**
     * Starts the read-evaluate loop for user commands.
     * Maintains the CLI version for RiceBot
     */
    public void run() {
        ui.greet();
        loadSavedTasks();
        while (true) {
            try {
                String input = ui.readInput().trim();
                if (input.equals("bye")) {
                    ui.show("Bye. Hope to see you again soon!");
                    return;
                }
                String[] parts = input.split(" ", 2);
                switch (parts[0]) {
                    case "list" -> ui.show(showTasks());
                    case "find" -> ui.show(findTasks(input));
                    case "sort" -> ui.show(sortTasks());
                    case "todo", "deadline", "event" -> ui.show(addTask(input));
                    case "mark", "unmark" -> ui.show(changeStatus(parts[0], input));
                    case "delete" -> ui.show(deleteTask(input));
                    default -> throw new RiceException("I'm sorry, but I dont know what that means :-(");
                }
            } catch (RiceException | IllegalArgumentException e) {
                ui.show(e.getMessage());
            }
        }
    }


    private void loadSavedTasks() {
        if (!hasLoadedSavedTasks) {
            tasks.addAll(parser.parseTasks(storage.load()));
            hasLoadedSavedTasks = true;
        }
    }

    private String showTasks() {
        if (tasks.size() == 0) {
            return "There are no tasks. Our bowl is empty :(";
        }
        StringBuilder response = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            response.append(i + 1).append(".").append(tasks.get(i)).append("\n");
        }
        if (response.length() > 0) {
            response.append("Our bowl is filled :)");
        }
        return response.toString().trim();
    }

    private String sortTasks() throws RiceException {
        if (tasks.size() == 0) {
            throw new RiceException("There are no tasks. Our bowl is empty :(");
        }
        tasks.sortTasks();
        storage.save(tasks.getTasks());
        return "Sorted tasks by deadline.";
    }

    private String findTasks(String input) throws RiceException {
        String[] parts = input.split(" ", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            throw new RiceException("Please provide a keyword to search for");
        }
        List<Task> matchingTasks = tasks.find(parts[1].trim());
        if (matchingTasks.isEmpty()) {
            return "No matching tasks found. Theres no rice of that type!";
        }

        StringBuilder response = new StringBuilder();
        for (Task task : matchingTasks) {
            response.append(task).append("\n");
        }
        return response.toString().trim();
    }

    private String addTask(String input) throws RiceException {
        Task task = parser.createTask(input);
        tasks.add(task);
        storage.save(tasks.getTasks());
        return "Got it. I've added this task:\n"
                + "  " + task + "\n"
                + String.format("Now you have %d tasks in the list, look at all that rice!", tasks.size());
    }

    private String changeStatus(String command, String input) throws RiceException {
        assert command.equals("mark") || command.equals("unmark");
        int index = taskIndex(input);
        if (command.equals("mark")) {
            tasks.mark(index);
            storage.save(tasks.getTasks());
            return "Nice! I've marked this task as done, we cooked this rice:\n"
                    + "  " + tasks.get(index);
        } else {
            tasks.unmark(index);
            storage.save(tasks.getTasks());
            return "OK, I've marked this task as not done yet, the rice is RAW!!:\n"
                    + "  " + tasks.get(index);
        }
    }

    private String deleteTask(String input) throws RiceException {
        Task removed = tasks.remove(taskIndex(input));
        storage.save(tasks.getTasks());
        return "Removed: " + removed;
    }

    private int taskIndex(String input) throws RiceException {
        String[] parts = input.split(" ", 2);
        if (parts.length < 2) {
            throw new RiceException("Task number is required");
        }

        try {
            int taskNumber = Integer.parseInt(parts[1].trim());
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new RiceException("Invalid task number");
            }
            int index = taskNumber - 1;
            assert index >= 0 && index < tasks.size();
            return index;
        } catch (NumberFormatException e) {
            throw new RiceException("Task number must be a number");
        }
    }

    /**
     * Returns the current tasks as a newline-separated list.
     *
     * @return formatted task list
     */
    public String displayList() {
        loadSavedTasks();
        if (tasks.size() == 0) {
            return "There are no tasks. Our bowl is empty :(";
        }
        return showTasks();
    }

    /**
     * Processes one user command and returns the response.
     *
     * @param input user command
     * @return response to the command
     */
    public String getResponse(String input) {
        loadSavedTasks();
        try {
            String trimmedInput = input.trim();
            if (trimmedInput.equals("bye")) {
                return "Bye. Hope to see you again soon!";
            }
            String[] parts = trimmedInput.split(" ", 2);
            return switch (parts[0]) {
                case "list" -> showTasks();
                case "find" -> findTasks(trimmedInput);
                case "sort" -> sortTasks();
                case "todo", "deadline", "event" -> addTask(trimmedInput);
                case "mark", "unmark" -> changeStatus(parts[0], trimmedInput);
                case "delete" -> deleteTask(trimmedInput);
                default -> throw new RiceException("I'm sorry, but I dont know what that means :-(");
            };
        } catch (RiceException | IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    /**
     * Returns a correction suggestion for an error response, if one applies.
     *
     * @param response response returned by { getResponse(String)}
     * @return correction suggestion, or null when the response is not an error
     */
    public String getSuggestion(String response) {
        if (response == null) {
            return null;
        }

        if (isUnknownCommand(response)) {
            return "Try list, find, sort, todo, deadline, event, mark, unmark, or delete.";
        }
        if (isFindError(response)) {
            return "Try: find <keyword>, e.g. find rice";
        }
        if (isTaskNumberError(response)) {
            return getTaskNumberSuggestion(response);
        }
        if (isEmptyDescriptionError(response)) {
            return "Add a description after the command";
        }
        if (isDeadlineError(response)) {
            return "Try: deadline <description> /by <YYYY-MM-DD>";
        }
        if (isEventError(response)) {
            return "Try: event <description> /from <YYYY-MM-DD> /to <YYYY-MM-DD>";
        }
        if (response.startsWith("Invalid date.")) {
            return "Use the date format yyyy-MM-DD, e.g. 2026-12-12";
        }
        return null;
    }

    private boolean isUnknownCommand(String response) {
        return "I'm sorry, but I dont know what that means :-(".equals(response);
    }

    private boolean isFindError(String response) {
        return "Please provide a keyword to search for".equals(response)
                || "Please provide a keyword to search for./".equals(response)
                || "No matching tasks found. Theres no rice of that type!".equals(response);
    }

    private boolean isTaskNumberError(String response) {
        return "Task number is required".equals(response)
                || "Invalid task number".equals(response)
                || "Task number must be a number".equals(response);
    }

    private String getTaskNumberSuggestion(String response) {
        if ("Task number is required".equals(response)) {
            return "Try: mark <number>, unmark <number>, or delete <number>";
        }
        if ("Invalid task number".equals(response)) {
            return "Use a task number from the list";
        }
        return "Replace the task number with a number, e.g. delete 1";
    }

    private boolean isEmptyDescriptionError(String response) {
        return "Task description cannot be empty".equals(response);
    }

    private boolean isDeadlineError(String response) {
        return "Use: deadline description /by yyyy-MM-dd".equals(response)
                || "Use: deadline description /by yyyy-MM-dd.".equals(response);
    }

    private boolean isEventError(String response) {
        return "Use: event description /from date /to date".equals(response)
                || "Use: event description /from date /to date.".equals(response);
    }

    public static void main(String[] args) {
        new Rice().run();
    }
}
