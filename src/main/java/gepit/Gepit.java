package gepit;

import java.util.List;

import gepit.parser.Parser;
import gepit.storage.Storage;
import gepit.task.Task;
import gepit.ui.Ui;

/**
 * Runs the Gepit chatbot and coordinates command processing.
 */
public class Gepit {
    private static final String HELP_MESSAGE = """
            Here are the commands I understand:
            list
            todo DESCRIPTION
            deadline DESCRIPTION /by yyyy-MM-dd
            event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd
            mark NUMBER | unmark NUMBER | delete NUMBER
            update NUMBER NEW_DESCRIPTION
            find KEYWORD
            help | bye""";

    private final Storage storage;
    private TaskList tasks;

    /**
     * Creates a Gepit chatbot and loads previously saved tasks.
     */
    public Gepit() {
        storage = new Storage("data/gepit.txt");

        try {
            tasks = new TaskList(storage.load());
        } catch (GepitException e) {
            tasks = new TaskList();
        }
    }

    /**
     * Runs Gepit using the text-based user interface.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Gepit gepit = new Gepit();
        Ui ui = new Ui();

        ui.showWelcome();

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();

            if (input.equals("bye")) {
                break;
            }

            ui.showMessage(gepit.getResponse(input));
        }

        ui.showGoodbye();
    }

    /**
     * Returns Gepit's response to the specified user input.
     *
     * @param input User input to process.
     * @return Response to display to the user.
     */
    public String getResponse(String input) {
        return process(input).message();
    }

    /**
     * Processes input and reports whether the resulting message is an error.
     *
     * @param input User input to process.
     * @return Structured response for presentation by the GUI.
     */
    public Response process(String input) {
        try {
            String normalizedInput = input == null ? "" : input.strip();
            if (normalizedInput.isEmpty()) {
                throw new GepitException("Type a command, or try 'help'.");
            }

            String command = Parser.getCommand(normalizedInput);
            if (command.equals("bye")) {
                validateNoArgument(normalizedInput, command);
                return Response.success("Bye, friendo!\nSee you again next time :)");
            }

            if (command.equals("help")) {
                validateNoArgument(normalizedInput, command);
                return Response.success(HELP_MESSAGE);
            }

            if (command.equals("list")) {
                validateNoArgument(normalizedInput, command);
                return Response.success(getTaskListMessage());
            }

            if (command.equals("find")) {
                String keyword = Parser.getArgument(normalizedInput);
                return Response.success(getTaskMatchesMessage(tasks.find(keyword)));
            }

            if (command.equals("delete")) {
                return Response.success(deleteTask(parseTaskIndex(normalizedInput)));
            }

            if (command.equals("mark")) {
                return Response.success(markTask(tasks.get(parseTaskIndex(normalizedInput))));
            }

            if (command.equals("unmark")) {
                return Response.success(unmarkTask(tasks.get(parseTaskIndex(normalizedInput))));
            }

            if (command.equals("update")) {
                String argument = Parser.getArgument(normalizedInput);
                Parser.UpdateArguments updateArguments =
                        Parser.parseUpdate(argument);
                int index = getTaskIndex(updateArguments.taskNumber());

                return Response.success(updateTask(
                        tasks.get(index),
                        updateArguments.description()));
            }

            if (command.equals("todo")) {
                String argument = Parser.getArgument(normalizedInput);
                Task task = Parser.parseTodo(argument);
                return Response.success(addTask(task));
            }

            if (command.equals("deadline")) {
                String argument = Parser.getArgument(normalizedInput);
                Task task = Parser.parseDeadline(argument);
                return Response.success(addTask(task));
            }

            if (command.equals("event")) {
                String argument = Parser.getArgument(normalizedInput);
                Task task = Parser.parseEvent(argument);
                return Response.success(addTask(task));
            }

            throw new GepitException(
                    "Sorry, friendo—I don't know '" + command
                            + "'. Try 'help' for the command list.");

        } catch (GepitException e) {
            return Response.error(e.getMessage());
        }
    }

    /**
     * Represents a chatbot message and its presentation category.
     *
     * @param message Text to show.
     * @param isError Whether the message describes invalid input or failed processing.
     */
    public record Response(String message, boolean isError) {
        private static Response success(String message) {
            return new Response(message, false);
        }

        private static Response error(String message) {
            return new Response(message, true);
        }
    }

    private String getTaskListMessage() {
        if (tasks.size() == 0) {
            return "You don't have any tasks yet.";
        }

        StringBuilder taskOutput = new StringBuilder("Here are your tasks:");

        for (int i = 0; i < tasks.size(); i++) {
            taskOutput.append("\n")
                    .append(i + 1)
                    .append(". ")
                    .append(tasks.get(i));
        }

        return taskOutput.toString();
    }

    private String getTaskMatchesMessage(List<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            return "I couldn't find any matching tasks.";
        }

        StringBuilder output =
                new StringBuilder("Here are the matching tasks in your list:");

        for (int i = 0; i < matchingTasks.size(); i++) {
            output.append("\n")
                    .append(i + 1)
                    .append(". ")
                    .append(matchingTasks.get(i));
        }

        return output.toString();
    }

    private String deleteTask(int index) throws GepitException {
        Task task = tasks.remove(index);
        storage.save(tasks);

        return "I've done it boss. Deleted this guy:"
                + "\n" + task
                + getTaskCountMessage();
    }

    private String markTask(Task task) throws GepitException {
        task.markDone();
        storage.save(tasks);

        return "Gr8 job it's done mate"
                + "\n" + task;
    }

    private String unmarkTask(Task task) throws GepitException {
        task.markNotDone();
        storage.save(tasks);

        return "Get to it soon bruv"
                + "\n" + task;
    }

    private String updateTask(Task task, String description)
            throws GepitException {
        task.updateDescription(description);
        storage.save(tasks);

        return "I've changed it up boss:"
                + "\n" + task;
    }

    private String getTaskCountMessage() {
        return "\nNow you have " + tasks.size() + " tasks in the list";
    }

    private String addTask(Task task) throws GepitException {
        tasks.add(task);
        storage.save(tasks);

        return "Got it. Task added:"
                + "\n" + task
                + getTaskCountMessage();
    }

    private int getTaskIndex(int taskNumber) throws GepitException {
        int index = taskNumber - 1;

        if (index < 0 || index >= tasks.size()) {
            throw new GepitException(
                    "Task " + taskNumber + " doesn't exist");
        }

        assert index >= 0 && index < tasks.size()
                : "Validated task index must be within task list bounds";

        return index;
    }

    private int parseTaskIndex(String input) throws GepitException {
        String argument = Parser.getArgument(input);
        int taskNumber = Parser.parseTaskNumber(argument);
        return getTaskIndex(taskNumber);
    }

    private static void validateNoArgument(String input, String command)
            throws GepitException {
        if (!input.equalsIgnoreCase(command)) {
            throw new GepitException(
                    "The '" + command + "' command does not take extra information.");
        }
    }
}
