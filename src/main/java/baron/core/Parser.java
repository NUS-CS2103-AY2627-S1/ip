package baron.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;

import baron.core.exception.BaronException;
import baron.core.task.Deadline;
import baron.core.task.Event;
import baron.core.task.Task;
import baron.core.task.TaskList;
import baron.core.task.Todo;

/**
 * Parses user commands and updates the task list and persistent storage.
 */
class Parser {
    private final Storage storage;
    private TaskList allTasks;
    private String snapshot;

    /**
     * Creates a parser that uses the specified persistent storage.
     *
     * @param storage The persistent task storage.
     */
    public Parser(Storage storage, TaskList allTasks) {
        this.storage = storage;
        this.allTasks = allTasks;
    }

    /**
     * Returns the response produced after processing the specified command.
     *
     * @param command The command to process.
     * @return The response to show to the user.
     */
    public String parse(String command) {
        if (command == null || command.isBlank()) {
            return "Please enter a command";
        }
        try {
            return routeCommand(command.trim().replaceAll("\\s+", " "));
        } catch (BaronException e) {
            return Response.respondWithBaronException(e);
        }
    }

    /** Routes the specified command to the handler responsible for it. */
    private String routeCommand(String command) throws BaronException {
        if (command.equals("bye")) {
            return Response.respondWithOutro();
        } else if (!storage.isAvailable()) {
            throw new BaronException(
                    "Could not access the task file. Task commands are disabled until storage is available.");
        } else {
            validateCommandFormat(command);
        }
        snapshot = allTasks.toFileString();
        if (command.matches("^list(\\s+.*)?$")) {
            return handleList(command);
        } else if (command.matches("^mark(\\s+.*)?$")) {
            return handleMark(command);
        } else if (command.matches("^unmark(\\s+.*)?$")) {
            return handleUnmark(command);
        } else if (command.matches("^todo(\\s+.*)?$")) {
            return handleTodo(command);
        } else if (command.matches("^deadline(\\s+.*)?$")) {
            return handleDeadline(command);
        } else if (command.matches("^event(\\s+.*)?$")) {
            return handleEvent(command);
        } else if (command.matches("^delete(\\s+.*)?$")) {
            return handleDelete(command);
        } else if (command.matches("^find(\\s+.*)?$")) {
            return handleFind(command);
        } else if (command.matches("^specify(\\s+.*)?$")) {
            return handleSpecify(command);
        }
        throw new BaronException("Unknown command");
    }

    private void validateCommandFormat(String command) throws BaronException {
        String commandName = command.split(" ", 2)[0];
        Set<String> allowedFlags = switch (commandName) {
            case "list", "specify" -> Set.of("/requires", "/unlocks");
            case "deadline" -> Set.of("/by");
            case "event" -> Set.of("/from", "/to");
            default -> Set.of();
        };
        String[] tokens = command.split(" ");
        Set<String> seenFlags = new HashSet<>();
        for (String token : tokens) {
            if (!token.startsWith("/")) {
                continue;
            }
            String flag = token.split(" ", 2)[0];
            if (!allowedFlags.contains(flag)) {
                throw new BaronException("Unknown or misplaced flag " + flag);
            }
            if (!seenFlags.add(flag)) {
                throw new BaronException("Flag " + flag + " must not be specified more than once");
            }
        }
        validateDescription(commandName, command);
    }

    private void validateDescription(String commandName, String command) throws BaronException {
        if (!Set.of("todo", "deadline", "event", "find").contains(commandName)) {
            return;
        }
        String description = command.substring(commandName.length()).trim();
        int firstFlag = description.indexOf(" /");
        if (firstFlag >= 0) {
            description = description.substring(0, firstFlag).trim();
        }
        if (description.contains("|") || description.contains("\n") || description.contains("\r")) {
            throw new BaronException("Descriptions must not contain '|', newlines, or carriage returns");
        }
    }

    /** Returns the response for a list command. */
    private String handleList(String command) throws BaronException {
        if (allTasks.size() == 0) {
            throw new BaronException("There are no tasks in your list");
        }
        boolean willShowRequiredTasks = command.matches("^.*\\s+/requires(\\s+.*)?$");
        boolean willShowUnlockedTasks = command.matches("^.*\\s+/unlocks(\\s+.*)?$");
        return Response.respondWithAllTasks(allTasks, willShowRequiredTasks, willShowUnlockedTasks);
    }

    /** Processes a mark command. */
    private String handleMark(String command) throws BaronException {
        int taskIndex = parseTaskIndex(getRequiredArgument("mark", command));
        Task markedTask = allTasks.markTask(taskIndex);
        saveOrRecover();
        return Response.respondWithMarkedTask(markedTask, allTasks);
    }

    /** Processes an unmark command. */
    private String handleUnmark(String command) throws BaronException {
        int taskIndex = parseTaskIndex(getRequiredArgument("unmark", command));
        Task unmarkedTask = allTasks.unmarkTask(taskIndex);
        saveOrRecover();
        return Response.respondWithUnmarkedTask(unmarkedTask, allTasks);
    }

    /** Processes a to-do command. */
    private String handleTodo(String command) throws BaronException {
        String description = getRequiredArgument("todo", command);
        Todo newTask = new Todo(description);
        allTasks.addTask(newTask);
        saveOrRecover();
        return Response.respondWithAddedTask(newTask, allTasks);
    }

    /** Processes a deadline command. */
    private String handleDeadline(String command) throws BaronException {
        String description = getRequiredArgument("deadline", command);
        LocalDateTime deadline = parseDateTime(getRequiredArgument("/by", command));
        Deadline newTask = new Deadline(description, deadline);
        allTasks.addTask(newTask);
        saveOrRecover();
        return Response.respondWithAddedTask(newTask, allTasks);
    }

    /** Processes an event command. */
    private String handleEvent(String command) throws BaronException {
        String description = getRequiredArgument("event", command);
        LocalDateTime fromDate = parseDateTime(getRequiredArgument("/from", command));
        LocalDateTime toDate = parseDateTime(getRequiredArgument("/to", command));
        if (!fromDate.isBefore(toDate)) {
            throw new BaronException("/to date must be after /from date");
        }
        Event newTask = new Event(description, fromDate, toDate);
        allTasks.addTask(newTask);
        saveOrRecover();
        return Response.respondWithAddedTask(newTask, allTasks);
    }

    /** Processes a delete command. */
    private String handleDelete(String command) throws BaronException {
        int taskIndex = parseTaskIndex(getRequiredArgument("delete", command));
        Task deletedTask = allTasks.deleteTask(taskIndex);
        saveOrRecover();
        return Response.respondWithDeletedTask(deletedTask, allTasks);
    }

    /** Processes a find command. */
    private String handleFind(String command) throws BaronException {
        String keyword = getRequiredArgument("find", command);
        TaskList matchingTasks = allTasks.findTasks(keyword);
        if (matchingTasks.size() == 0) {
            throw new BaronException("None of your tasks match '" + keyword + "'");
        }
        return Response.respondWithMatchingTasks(matchingTasks, allTasks);
    }

    /** Processes a specify command. */
    private String handleSpecify(String command) throws BaronException {
        int taskIndex = parseTaskIndex(getRequiredArgument("specify", command));
        Task task = allTasks.getTasks().get(taskIndex);
        task.clearRequiredTasks();
        task.clearUnlockedTasks();
        try {
            String requiredTaskNumbers = getRequiredArgument("/requires", command);
            if (!requiredTaskNumbers.equals("-")) {
                task.setRequiredTasks(allTasks, parseTaskNumbers(requiredTaskNumbers));
            }
            String unlockedTaskNumbers = getRequiredArgument("/unlocks", command);
            if (!unlockedTaskNumbers.equals("-")) {
                task.setUnlockedTasks(allTasks, parseTaskNumbers(unlockedTaskNumbers));
            }
        } catch (BaronException baronException) {
            allTasks = storage.loadTasks(snapshot);
            throw baronException;
        }
        saveOrRecover();
        return Response.respondWithSpecifiedTask(task, allTasks);
    }

    /** Returns the non-blank argument that follows the specified command flag. */
    private String getRequiredArgument(String flag, String command) throws BaronException {
        if (flag.charAt(0) == '/' && !command.matches("^.*\\s+" + flag + "(\\s+.*)?$")) {
            throw new BaronException("Missing flag " + flag);
        }

        int argumentStartIndex = command.indexOf(flag) + flag.length();
        int nextFlagIndex = command.indexOf('/', argumentStartIndex);
        int argumentEndIndex = nextFlagIndex == -1 ? command.length() : nextFlagIndex;
        String argument = command.substring(argumentStartIndex, argumentEndIndex);

        if (argument.trim().isEmpty()) {
            throw new BaronException("Missing argument for " + flag);
        }
        return argument.trim();
    }

    /** Returns a date and time parsed from the specified command argument. */
    private LocalDateTime parseDateTime(String dateTime) throws BaronException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy HHmm");
            return LocalDateTime.parse(dateTime, formatter);
        } catch (DateTimeParseException e) {
            throw new BaronException("Date/time must be in ddMMyyyy HHmm");
        }
    }

    /** Returns the task index parsed from the specified argument. */
    private int parseTaskIndex(String taskNumber) throws BaronException {
        try {
            int index = Integer.parseInt(taskNumber) - 1;
            allTasks.checkTaskIndex(index);
            return index;
        } catch (NumberFormatException e) {
            throw new BaronException("Task number must be an integer");
        }
    }

    /** Returns the tasks for the task numbers supplied in the argument. */
    private TaskList parseTaskNumbers(String argument) throws BaronException {
        String[] taskNumbers = argument.split("\\s+");

        int[] indices = new int[taskNumbers.length];
        for (int i = 0; i < taskNumbers.length; i++) {
            indices[i] = parseTaskIndex(taskNumbers[i]);
        }

        Set<Integer> setOfIndices = new HashSet<>();
        for (int index : indices) {
            setOfIndices.add(index);
        }

        TaskList tasks = new TaskList();
        setOfIndices.stream()
                .map(index -> allTasks.getTasks().get(index))
                .forEach(tasks::addTask);
        return tasks;
    }

    private void saveOrRecover() throws BaronException {
        try {
            storage.writeTasks(allTasks);
        } catch (BaronException baronException) {
            allTasks = storage.loadTasks(snapshot);
            throw baronException;
        }
    }
}
