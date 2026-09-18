package sozius.parser;

import sozius.exception.SoziusException;
import sozius.task.DeadlineTask;
import sozius.task.DueDate;
import sozius.task.EventTask;
import sozius.task.Task;
import sozius.task.TodoTask;
import sozius.tasklist.TaskList;

/**
 * The Parser class parses and executes commands
 */
public class Parser {
    private static final String LIST_USAGE = "Usage: list";
    private static final String MARK_USAGE = "Usage: mark <index>";
    private static final String UNMARK_USAGE = "Usage: unmark <index>";
    private static final String DELETE_USAGE = "Usage: delete <index>";
    private static final String TODO_USAGE = "Usage: todo <description>";
    private static final String DEADLINE_USAGE = "Usage: deadline <description> /by <duedate>";
    private static final String EVENT_USAGE = "Usage: event <description> /from <fromdate> /to <todate>";
    private static final String FIND_USAGE = "Usage: find <keyword>";
    private static final String HELP_USAGE = "Usage: help";
    private static final String HELP_MESSAGE =
            "List of commands:\n"
                    + "list, ls: Show all tasks. " + LIST_USAGE + "\n"
                    + "mark, m: Mark a task as done. " + MARK_USAGE + "\n"
                    + "unmark, um: Mark a task as not done. " + UNMARK_USAGE + "\n"
                    + "todo, td: Create a todo task. " + TODO_USAGE + "\n"
                    + "deadline, dl: Create a deadline task. " + DEADLINE_USAGE + "\n"
                    + "event, e: Create an event task. " + EVENT_USAGE + "\n"
                    + "delete, del: Delete a task. " + DELETE_USAGE + "\n"
                    + "find, f: Search task descriptions. " + FIND_USAGE + "\n"
                    + "help, h: Show this command list. " + HELP_USAGE + "\n"
                    + "\nUse task indices from list, starting at 1. Aliases work the same as full command names.";
    private final TaskList tasks;

    /**
     * Creates a parser
     * @param tasks used by the parser to store tasks
     */
    public Parser(TaskList tasks) {
        this.tasks = tasks;
    }

    private String listTasks(String args) throws SoziusException {
        if (!args.isEmpty()) {
            throw new SoziusException("The list command does not take any arguments.\n"
                    + LIST_USAGE);
        }
        if (tasks.size() == 0) {
            return "No tasks found";
        }
        StringBuilder response = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            response.append((i + 1)).append(". ").append(tasks.get(i).toUserString()).append("\n");
        }
        return response.toString();
    }

    private int parseIndex(String args) throws SoziusException {
        if (args.isEmpty()) {
            throw new SoziusException("Missing task number.");
        }
        try {
            int index = Integer.parseInt(args);

            if (index < 1 || index > tasks.size()) {
                throw new SoziusException("Invalid task number."
                        + "You have " + tasks.size() + (tasks.size() == 1 ? " task" : " tasks")
                        + " (1 to " + tasks.size() + ")");
            }

            return index;
        } catch (NumberFormatException e) {
            throw new SoziusException("Not a number.");
        }
    }

    private String markTask(String args) throws SoziusException {
        int index = parseIndex(args);
        Task task = tasks.get(index - 1);
        if (task.isDone()) {
            return "This task is already marked as done:\n" + task.toUserString();
        }
        tasks.markTask(index - 1);
        return "Marked as done:\n" + task.toUserString();
    }

    private String unmarkTask(String args) throws SoziusException {
        int index = parseIndex(args);
        Task task = tasks.get(index - 1);
        if (!task.isDone()) {
            return "This task is not marked as done yet:\n" + task.toUserString();
        }
        tasks.unmarkTask(index - 1);
        return "Marked as not done:\n" + task.toUserString();
    }

    private String deleteTask(String args) throws SoziusException {
        int index = parseIndex(args);
        return "Task deleted:\n" + tasks.remove(index - 1).toUserString();
    }

    private String createTask(Task task) {
        tasks.add(task);
        StringBuilder response = new StringBuilder();
        response.append("Got it. I've added this task:\n");
        response.append(tasks.getLast().toUserString()).append("\n");
        response.append("Now you have " + tasks.size() + " tasks in the list");
        return response.toString();
    }

    private String createTodoTask(String args) throws SoziusException {
        if (args.isEmpty()) {
            throw new SoziusException("Missing description.\n" + TODO_USAGE);
        }
        TodoTask task = new TodoTask(args);
        return createTask(task);
    }

    private String createDeadlineTask(String args) throws SoziusException {

        if (args.isEmpty()) {
            throw new SoziusException("Missing description and duedate.\n" + DEADLINE_USAGE);
        }
        String[] splitArgs = args.split("\\s+/by\\s+", 2);

        boolean tooManyBy = splitArgs.length > 2;
        boolean noBy = splitArgs.length == 1;
        if (tooManyBy || noBy) {
            throw new SoziusException("The /by parameter should be provided exactly once.\n"
                    + DEADLINE_USAGE);
        }

        String desc = splitArgs[0].trim();
        String byString = splitArgs[1].trim();
        if (desc.isEmpty()) {
            throw new SoziusException("Missing task description.\n"
                    + DEADLINE_USAGE);
        }
        if (byString.isEmpty()) {
            throw new SoziusException("Missing date after /by.\n"
                    + DEADLINE_USAGE);
        }
        DueDate by = DueDate.parse(byString);
        DeadlineTask task = new DeadlineTask(desc, by);
        return createTask(task);
    }

    private String createEventTask(String args) throws SoziusException {
        if (args.isEmpty()) {
            throw new SoziusException("Missing description and duedate.\n" + EVENT_USAGE);
        }

        String[] splitArgs1 = args.split("\\s+/from\\s+", 2);
        boolean tooManyFrom = splitArgs1.length > 2;
        boolean noFrom = splitArgs1.length == 1;
        if (tooManyFrom || noFrom) {
            throw new SoziusException("The /from parameter should be provided exactly once.\n"
                    + EVENT_USAGE);
        }

        String desc = splitArgs1[0].trim();
        if (desc.isEmpty()) {
            throw new SoziusException("Missing description.\n"
                    + EVENT_USAGE);
        }

        String[] splitArgs2 = splitArgs1[1].split("\\s+/to\\s+", 2);
        boolean tooManyTo = splitArgs2.length > 2;
        boolean noTo = splitArgs2.length == 1;
        if (tooManyTo || noTo) {
            throw new SoziusException("The /to parameter should be provided exactly once.\n"
                    + EVENT_USAGE);
        }

        String fromString = splitArgs2[0].trim();
        String toString = splitArgs2[1].trim();
        if (fromString.isEmpty()) {
            throw new SoziusException("Missing date after /from.\n"
                    + EVENT_USAGE);
        }
        if (toString.isEmpty()) {
            throw new SoziusException("Missing date after /to.\n"
                    + EVENT_USAGE);
        }
        DueDate from = DueDate.parse(fromString);
        DueDate to = DueDate.parse(toString);
        EventTask task = new EventTask(desc, from, to);
        return createTask(task);
    }

    private String findTasks(String args) throws SoziusException {
        if (args.isEmpty()) {
            throw new SoziusException("Missing keyword.\n" + FIND_USAGE);
        }
        int cnt = 0;
        StringBuilder response = new StringBuilder();
        response.append("Searching for tasks:\n");
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(args.toLowerCase())) {
                ++cnt;
                response.append(task.toUserString()).append("\n");
            }
        }
        response.append("Found ").append(cnt).append(cnt == 1 ? " task" : " tasks").append("\n");
        return response.toString();
    }

    private String help(String args) throws SoziusException {
        if (!args.isEmpty()) {
            throw new SoziusException("The help command does not take any arguments.\n"
                    + HELP_USAGE);
        }
        return HELP_MESSAGE;
    }

    /**
     * Parses a line of text entered by the user
     * Executes the command
     * Prints error message and returns if command is invalid
     * @param line the line of text
     * @see Command
     */
    public String parse(String line) throws SoziusException {
        int firstSpace = line.indexOf(' ');
        Command command = firstSpace == -1
                ? Command.getCommand(line)
                : Command.getCommand(line.substring(0, firstSpace));
        String args = firstSpace == -1 ? "" : line.substring(firstSpace + 1).trim();
        if (command == null) {
            throw new SoziusException("Unknown command. Try \"help\" for help.");
        }
        switch (command) {
            case Command.LIST:
                return listTasks(args);
            case Command.MARK:
                return markTask(args);
            case Command.UNMARK:
                return unmarkTask(args);
            case Command.TODO:
                return createTodoTask(args);
            case Command.DEADLINE:
                return createDeadlineTask(args);
            case Command.EVENT:
                return createEventTask(args);
            case Command.DELETE:
                return deleteTask(args);
            case Command.FIND:
                return findTasks(args);
            case Command.HELP:
                return help(args);
            default:
                throw new SoziusException("Unknown command. Try \"help\" for help.");
        }
    }
}
