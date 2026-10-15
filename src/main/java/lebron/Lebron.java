package lebron;

import java.io.IOException;
import java.time.format.DateTimeParseException;

/**
 * Represents the Lebron chatbot.
 */
public class Lebron {

    private Storage storage;
    private TaskList tasks;
    private Ui ui;
    private Parser parser;

    /**
     * Creates a Lebron chatbot and loads saved tasks.
     *
     * @param folderName folder containing the data file
     * @param fileName file used to store tasks
     */
    public Lebron(String folderName, String fileName) {
        ui = new Ui();
        parser = new Parser();
        storage = new Storage(folderName, fileName);

        try {
            tasks = storage.loadTasks();
        } catch (IOException
                 | DateTimeParseException
                 | IllegalArgumentException e) {

            ui.showMessage(
                    "Lebron: I couldn't read the saved task data. "
                            + "I'll start with an empty list."
            );

            tasks = new TaskList();
        }
    }

    /**
     * Runs the text-based version of Lebron.
     */
    public void run() {
        ui.showGreeting();

        boolean isRunning = true;

        while (isRunning) {
            String input = ui.readCommand();
            String response = getResponse(input);

            ui.showMessage(response);

            if (parser.getCommand(input).equals("bye")
                    && !parser.hasArguments(input)) {
                isRunning = false;
            }
        }

        ui.close();
    }

    /**
     * Generates Lebron's response to a command.
     *
     * @param input command entered by the user
     * @return Lebron's response
     */
    public String getResponse(String input) {
        String command = parser.getCommand(input);

        switch (command) {
            case "":
                return "Lebron: Enter a command so I know the next play!";

            case "bye":
                if (parser.hasArguments(input)) {
                    return "Lebron: Just type 'bye' to leave the court!";
                }

                return "That's game. See you next time!";

            case "list":
                if (parser.hasArguments(input)) {
                    return "Lebron: Use 'list' without anything after it.";
                }

                return formatTaskList(
                        "Here are your tasks:",
                        tasks
                );

            case "sort":
                if (parser.hasArguments(input)) {
                    return "Lebron: Use 'sort' without anything after it.";
                }

                return sortTasks();

            case "find":
                return findTasks(input);

            case "mark":
                return markTask(input);

            case "delete":
                return deleteTask(input);

            case "todo":
                return addTodo(input);

            case "deadline":
                return addDeadline(input);

            case "event":
                return addEvent(input);

            default:
                return "Lebron: I don't know that command.\n"
                        + "Try todo, deadline, event, list, find, mark, "
                        + "delete, sort, or bye.";
        }
    }

    /**
     * Finds tasks containing the specified keyword.
     *
     * @param input find command entered by the user
     * @return response containing matching tasks
     */
    private String findTasks(String input) {
        String keyword = input.substring(4).trim();

        if (keyword.isEmpty()) {
            return "Lebron: Tell me what you want to find!";
        }

        TaskList matchingTasks = tasks.find(keyword);

        if (matchingTasks.size() == 0) {
            return "Lebron: I couldn't find any matching tasks.";
        }

        return formatTaskList(
                "Here are the matching tasks in your list:",
                matchingTasks
        );
    }

    /**
     * Marks a specified task as completed.
     *
     * @param input mark command entered by the user
     * @return response describing the result
     */
    private String markTask(String input) {
        try {
            int taskNumber = parser.parseTaskNumber(input);

            if (taskNumber < 1 || taskNumber > tasks.size()) {
                return "Lebron: That task number doesn't exist!";
            }

            Task task = tasks.mark(taskNumber);

            return saveAndReturn(
                    "Nice! I've marked this task as done:\n" + task
            );

        } catch (NumberFormatException e) {
            return "Lebron: Give me one valid task number, "
                    + "for example 'mark 2'.";
        }
    }

    /**
     * Deletes a specified task.
     *
     * @param input delete command entered by the user
     * @return response describing the result
     */
    private String deleteTask(String input) {
        try {
            int taskNumber = parser.parseTaskNumber(input);

            if (taskNumber < 1 || taskNumber > tasks.size()) {
                return "Lebron: That task number doesn't exist!";
            }

            Task deletedTask = tasks.delete(taskNumber);

            return saveAndReturn(
                    "Alright, I've removed this task:\n"
                            + deletedTask
                            + "\nNow you have "
                            + tasks.size()
                            + " tasks in the list."
            );

        } catch (NumberFormatException e) {
            return "Lebron: Give me one valid task number, "
                    + "for example 'delete 2'.";
        }
    }

    /**
     * Adds a todo task.
     *
     * @param input todo command entered by the user
     * @return response describing the result
     */
    private String addTodo(String input) {
        try {
            Todo todo = parser.parseTodo(input);
            tasks.add(todo);

            return saveAndReturn(
                    "Got it. I've added this task:\n" + todo
            );

        } catch (IllegalArgumentException e) {
            return "Lebron: " + e.getMessage();
        }
    }

    /**
     * Adds a deadline task.
     *
     * @param input deadline command entered by the user
     * @return response describing the result
     */
    private String addDeadline(String input) {
        try {
            Deadline deadline = parser.parseDeadline(input);
            tasks.add(deadline);

            return saveAndReturn(
                    "Got it. I've added this task:\n" + deadline
            );

        } catch (DateTimeParseException e) {
            return "Lebron: Enter a valid date as yyyy-MM-dd, "
                    + "for example 2026-09-18.";

        } catch (IllegalArgumentException e) {
            return "Lebron: " + e.getMessage();
        }
    }

    /**
     * Adds an event task.
     *
     * @param input event command entered by the user
     * @return response describing the result
     */
    private String addEvent(String input) {
        try {
            Event event = parser.parseEvent(input);
            tasks.add(event);

            return saveAndReturn(
                    "Got it. I've added this task:\n" + event
            );

        } catch (IllegalArgumentException e) {
            return "Lebron: " + e.getMessage();
        }
    }

    /**
     * Formats a task list for display.
     *
     * @param heading heading to display before the tasks
     * @param taskList task list to format
     * @return formatted task list
     */
    private String formatTaskList(
            String heading,
            TaskList taskList) {

        if (taskList.size() == 0) {
            return heading + "\nNo tasks on the board.";
        }

        StringBuilder result = new StringBuilder(heading);

        for (int i = 0; i < taskList.size(); i++) {
            result.append("\n")
                    .append(i + 1)
                    .append(". ")
                    .append(taskList.get(i));
        }

        return result.toString();
    }

    /**
     * Saves the task list and returns the supplied response.
     *
     * @param response response to return after saving
     * @return supplied response, with an error message if saving fails
     */
    private String saveAndReturn(String response) {
        try {
            storage.saveTasks(tasks);
            return response;

        } catch (IOException e) {
            return response
                    + "\nLebron: The play worked, but I couldn't "
                    + "save the task data.";
        }
    }

    /**
     * Sorts the task list alphabetically and saves the new order.
     *
     * @return response showing the sorted task list
     */
    private String sortTasks() {
        if (tasks.size() == 0) {
            return "Lebron: There aren't any tasks to sort yet!";
        }

        tasks.sort();

        return saveAndReturn(
                formatTaskList(
                        "I've sorted your tasks alphabetically:",
                        tasks
                )
        );
    }

    /**
     * Starts the text-based version of Lebron.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        new Lebron("data", "lebron.txt").run();
    }
}
