package fein;

import java.util.List;

import fein.task.Task;
import fein.task.TaskList;

/** Coordinates Fein's user interface, parser, task list, and storage. */
public class Fein {
    /** The in-app guide describing every command available in Fein. */
    private static final String HELP_MESSAGE = String.join("\n",
            "FEIN command list:",
            "",
            "todo <description>",
            "Purpose: Add a task without a date or time.",
            "Example: todo buy milk",
            "",
            "deadline <description> /by <due date>",
            "Purpose: Add a task with a deadline.",
            "Example: deadline submit report /by Friday",
            "",
            "event <description> /from <start> /to <end>",
            "Purpose: Add a task with a start and end time.",
            "Example: event team meeting /from 2pm /to 4pm",
            "",
            "find <keyword>",
            "Purpose: Find tasks containing a word or phrase.",
            "Example: find report",
            "",
            "mark <task number>",
            "Purpose: Mark a task as completed.",
            "Example: mark 1",
            "",
            "unmark <task number>",
            "Purpose: Mark a completed task as not completed.",
            "Example: unmark 1",
            "",
            "delete <task number>",
            "Purpose: Remove a task from your list.",
            "Example: delete 1",
            "",
            "Quick commands:",
            "list",
            "Purpose: Show all tasks in your list.",
            "",
            "help",
            "Purpose: Show this command guide.",
            "",
            "bye",
            "Purpose: Exit Fein.");

    /** The guidance shown when no command was entered. */
    private static final String EMPTY_COMMAND_MESSAGE = "Tell me what you would like to remember. "
            + "Try `todo buy milk` or `bye`.";

    /** Handles command-line input and output. */
    private final Ui ui;

    /** Converts user commands into tasks and arguments. */
    private final Parser parser;

    /** Owns the current tasks. */
    private TaskList tasks;

    /** Loads and saves tasks. */
    private final Storage storage;

    /** Identifies the most recently processed command for GUI styling. */
    private String commandType;

    /** Creates Fein using the default task file. */
    public Fein() {
        this("data/fein.txt");
    }

    /** Creates Fein using the supplied task file. */
    public Fein(String filePath) {
        ui = new Ui();
        parser = new Parser();
        storage = new Storage(filePath);
        commandType = "";
        try {
            tasks = new TaskList(storage.load());
        } catch (FeinException exception) {
            ui.showError(exception.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Returns Fein's response to a command and applies any requested task-list changes.
     *
     * @param command the command entered by the user
     * @return the response that should be displayed in the chat
     */
    public String getResponse(String command) {
        // The GUI and CLI both provide an actual line of input to this method.
        assert command != null : "A command must be present before it is processed";
        String normalizedCommand = command.trim();
        commandType = "error";
        try {
            if (normalizedCommand.isEmpty()) {
                return EMPTY_COMMAND_MESSAGE;
            }
            if (hasRepeatedWhitespace(normalizedCommand)) {
                return "Please use a single space between words, for example: `todo buy milk`.";
            }
            if (normalizedCommand.equals("bye")) {
                commandType = "bye";
                return "Bye! Keeping feining and stay LIT!";
            }
            if (normalizedCommand.equals("help")) {
                commandType = "help";
                return HELP_MESSAGE;
            }
            if (normalizedCommand.equals("list")) {
                commandType = "list";
                return formatTasks(tasks.asList(), "Here are the tasks in your list:",
                        "Your list is empty! Try adding a task with `todo <description>`.\nExample: todo buy milk");
            }
            if (normalizedCommand.equals("find") || normalizedCommand.startsWith("find ")) {
                commandType = "find";
                String keyword = parser.parseFindKeyword(normalizedCommand);
                return formatTasks(tasks.find(keyword), "Here are the matching tasks in your list:",
                        "No matching tasks found");
            }
            if (normalizedCommand.equals("mark") || normalizedCommand.startsWith("mark ")) {
                commandType = "mark";
                Task task = tasks.mark(parser.parseTaskNumber(normalizedCommand, "mark"));
                storage.save(tasks);
                return "Nice! I've marked this task as done:\n" + task;
            }
            if (normalizedCommand.equals("unmark") || normalizedCommand.startsWith("unmark ")) {
                commandType = "unmark";
                Task task = tasks.unmark(parser.parseTaskNumber(normalizedCommand, "unmark"));
                storage.save(tasks);
                return "OK, I've marked this task as not done yet:\n" + task;
            }
            if (normalizedCommand.equals("delete") || normalizedCommand.startsWith("delete ")) {
                commandType = "delete";
                Task task = tasks.delete(parser.parseTaskNumber(normalizedCommand, "delete"));
                storage.save(tasks);
                return "Noted. I've removed this task:\n" + task
                        + "\nNow you have " + tasks.size() + " tasks in your list.";
            }

            Task task = parser.parseTask(normalizedCommand);
            tasks.add(task);
            storage.save(tasks);
            commandType = "add";
            return "Got it. I've added this task:\n" + task
                    + "\nNow you have " + tasks.size() + " tasks in your list.";
        } catch (FeinException exception) {
            commandType = "error";
            return exception.getMessage();
        }
    }

    /** Returns the type of the most recently processed command. */
    public String getCommandType() {
        return commandType;
    }

    /** Returns a formatted response containing a heading and numbered tasks. */
    private String formatTasks(List<Task> matchingTasks, String heading, String emptyMessage) {
        // All three arguments are produced internally by Fein's command handlers.
        assert matchingTasks != null : "A task result list must be present for formatting";
        assert heading != null && emptyMessage != null : "Task-list messages must be present";

        if (matchingTasks.isEmpty()) {
            return emptyMessage;
        }

        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < matchingTasks.size(); i++) {
            response.append("\n").append(i + 1).append(".").append(matchingTasks.get(i));
        }
        return response.toString();
    }

    /** Returns whether a command contains adjacent whitespace characters between its words. */
    private boolean hasRepeatedWhitespace(String command) {
        return command.matches(".*\\s{2,}.*");
    }

    /** Runs Fein until the user enters {@code bye} or closes input. */
    public void run() {
        ui.showWelcome();
        while (true) {
            String command = ui.readCommand();
            if (command == null) {
                break;
            }
            ui.showSeparator();
            try {
                if (command.equals("bye")) {
                    ui.showGoodbye();
                    break;
                }
                handleCommand(command);
            } catch (FeinException exception) {
                ui.showError(exception.getMessage());
            }
            ui.showSeparator();
        }
    }

    /** Dispatches one command to the object responsible for that operation. */
    private void handleCommand(String command) throws FeinException {
        if (command.trim().isEmpty()) {
            ui.showMessage(EMPTY_COMMAND_MESSAGE);
            return;
        }
        if (hasRepeatedWhitespace(command)) {
            throw new FeinException("Please use a single space between words, for example: `todo buy milk`.");
        }
        if (command.equals("list")) {
            ui.showTasks(tasks);
        } else if (command.equals("help")) {
            ui.showMessage(HELP_MESSAGE);
        } else if (command.equals("find") || command.startsWith("find ")) {
            String keyword = parser.parseFindKeyword(command);
            ui.showMatchingTasks(tasks.find(keyword));
        } else if (command.equals("mark") || command.startsWith("mark ")) {
            Task task = tasks.mark(parser.parseTaskNumber(command, "mark"));
            storage.save(tasks);
            ui.showTaskMarked(task);
        } else if (command.equals("unmark") || command.startsWith("unmark ")) {
            Task task = tasks.unmark(parser.parseTaskNumber(command, "unmark"));
            storage.save(tasks);
            ui.showTaskUnmarked(task);
        } else if (command.equals("delete") || command.startsWith("delete ")) {
            Task deletedTask = tasks.delete(parser.parseTaskNumber(command, "delete"));
            storage.save(tasks);
            ui.showTaskDeleted(deletedTask, tasks.size());
        } else {
            Task task = parser.parseTask(command);
            tasks.add(task);
            storage.save(tasks);
            ui.showTaskAdded(task, tasks.size());
        }
    }

    /** Starts Fein with its default storage file. */
    public static void main(String[] args) {
        new Fein().run();
    }
}
