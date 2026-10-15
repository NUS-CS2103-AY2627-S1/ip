package walnut;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates a Walnut task manager and loads existing tasks from storage.
 *
 * <p>Any tasks successfully parsed from the stored data are added to
 * the task list.</p>
 */
public class Walnut {
    private static final String CMD_TODO = "todo";
    private static final String CMD_DEADLINE = "deadline";
    private static final String CMD_EVENT = "event";
    private static final String CMD_FIND = "find";
    private static final String SEP_BY = "/by";
    private static final String SEP_FROM = "/from";
    private static final String SEP_TO = "/to";

    private Ui ui;
    private TaskList tasks;
    private Storage storage;
    private List<String> data;
    private String startupMessage;

    /**
     * Creates a Walnut task manager and initializes the user interface,
     * task list, storage, and task data.
     *
     * <p>Loads existing tasks from storage and adds successfully parsed
     * tasks to the task list.</p>
     */
    public Walnut() {
        ui = new Ui();
        tasks = new TaskList();
        storage = new Storage();
        data = new ArrayList<>();

        try {
            data = storage.load();
        } catch (IOException e) {
            startupMessage = ui.showStorageLoadError();
        }

        for (String line : data) {
            Task task = Parser.parseTask(line);
            if (task != null) {
                tasks.add(task);
            }
        }
    }

    /**
     * Returns the message generated during application startup.
     *
     * @return the startup message, or {@code null} if no startup error occurred
     */
    public String getStartupMessage() {
        return startupMessage;
    }

    /**
     * Parses and validates a 1-based task index from the request tokens.
     *
     * @param request tokenized user request
     * @param tasks the current TaskList
     * @return zero-based index
     * @throws IllegalArgumentException with message "missing" or "invalid"
     */
    private int parseAndValidateIndex(String[] request, TaskList tasks) throws IllegalArgumentException {
        if (request.length < 2) {
            throw new IllegalArgumentException("missing");
        }
        try {
            int idx = Integer.parseInt(request[1]) - 1;
            if (idx < 0 || idx >= tasks.size()) {
                throw new IllegalArgumentException("invalid");
            }
            return idx;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid");
        }
    }

    /**
     * Checks whether a command contains exactly one task number.
     *
     * @param request tokenized command
     * @return true if the command has exactly two tokens
     */
    private boolean hasExactlyOneTaskNumber(String[] request) {
        return request.length == 2;
    }

    /**
     * Processes a user command and returns Walnut's response.
     *
     * <p>The command is parsed and executed based on its type. Commands may
     * add, remove, modify, list, or search for tasks.</p>
     *
     * @param input the command entered by the user
     * @return Walnut's response to the command
     */
    public String getResponse(String input) {
        if (input == null || input.isBlank()) {
            return ui.showEmptyCommand();
        }

        input = input.trim();
        String[] request = input.split("\\s+");

        if (request.length == 0 || request[0].isEmpty()) {
            return ui.showInvalidCommand();
        }

        Command command = Parser.parseCommand(request[0]);

        if (command == null) {
            return ui.showInvalidCommand();
        }
        assert command == Parser.parseCommand(request[0])
                : "Parsed command should remain consistent";

        switch (command) {
            case BYE: {
                return ui.showFarewell();
            }

            case LIST: {
                return ui.showTaskList(tasks);
            }

            case MARK: {
                if (!hasExactlyOneTaskNumber(request)) {
                    return request.length < 2
                            ? ui.showMissingTaskNumber()
                            : ui.showInvalidCommand();
                }
                if (tasks.isEmpty()) {
                    return ui.showEmptyTaskListMessage();
                }
                int index;
                try {
                    index = parseAndValidateIndex(request, tasks);
                } catch (IllegalArgumentException e) {
                    if ("missing".equals(e.getMessage())) {
                        return ui.showMissingTaskNumber();
                    } else {
                        return ui.showInvalidTaskNumber(tasks.size());
                    }
                }
                Task task = tasks.get(index);
                boolean previousStatus = task.isDone();

                task.markAsDone();

                try {
                    storage.save(tasks);
                } catch (IOException e) {
                    if (!previousStatus) {
                        task.markAsNotDone();
                    } else {
                        task.markAsDone();
                    }

                    return ui.showStorageSaveError();
                }
                return ui.showTaskMarkedAsDone(task);
            }

            case UNMARK: {
                if (!hasExactlyOneTaskNumber(request)) {
                    return request.length < 2
                            ? ui.showMissingTaskNumber()
                            : ui.showInvalidCommand();
                }
                if (tasks.isEmpty()) {
                    return ui.showEmptyTaskListMessage();
                }
                int index;
                try {
                    index = parseAndValidateIndex(request, tasks);
                } catch (IllegalArgumentException e) {
                    if ("missing".equals(e.getMessage())) {
                        return ui.showMissingTaskNumber();
                    } else {
                        return ui.showInvalidTaskNumber(tasks.size());
                    }
                }
                Task task = tasks.get(index);
                boolean previousStatus = task.isDone();

                task.markAsNotDone();

                try {
                    storage.save(tasks);
                } catch (IOException e) {
                    if (previousStatus) {
                        task.markAsDone();
                    } else {
                        task.markAsNotDone();
                    }

                    return ui.showStorageSaveError();
                }
                return ui.showTaskMarkedAsNotDone(task);
            }

            case TODO: {
                String args = input.substring(CMD_TODO.length()).trim();
                if (args.isEmpty()) {
                    return ui.showEmptyDescription("todo");
                }
                Task task = new ToDo(args);
                if (tasks.containsEquivalent(task)) {
                    return ui.showDuplicateTask();
                }
                tasks.add(task);
                try {
                    storage.save(tasks);
                } catch (IOException e) {
                    return ui.showStorageSaveError();
                }
                return ui.showTaskAdded(task, tasks.size());
            }

            case DEADLINE: {
                String args = input.substring(CMD_DEADLINE.length()).trim();
                int byIndex = args.indexOf(SEP_BY);
                int secondByIndex = args.indexOf(SEP_BY, byIndex + SEP_BY.length());

                if (byIndex == -1 || secondByIndex != -1) {
                    return ui.showInvalidDeadlineFormat();
                }
                String description = args.substring(0, byIndex).trim();

                if (description.contains("/from") || description.contains("/to")) {
                    return ui.showInvalidDeadlineFormat();
                }
                if (description.isEmpty()) {
                    return ui.showEmptyDescription("deadline");
                }

                String by = args.substring(byIndex + SEP_BY.length()).trim();
                if (by.isEmpty()) {
                    return ui.showInvalidDateTime();
                }
                LocalDateTime formattedByDateTime;
                try {
                    formattedByDateTime = Parser.parseUserDateTime(by);
                } catch (DateTimeParseException e) {
                    return ui.showInvalidDateTime();
                }

                Task task = new Deadline(description, formattedByDateTime);
                if (tasks.containsEquivalent(task)) {
                    return ui.showDuplicateTask();
                }
                tasks.add(task);
                try {
                    storage.save(tasks);
                } catch (IOException e) {
                    return ui.showStorageSaveError();
                }
                return ui.showTaskAdded(task, tasks.size());
            }

            case EVENT: {
                String args = input.substring(CMD_EVENT.length()).trim();
                int fromIndex = args.indexOf(SEP_FROM);
                int toIndex = args.indexOf(SEP_TO);

                int secondFromIndex = args.indexOf(SEP_FROM, fromIndex + SEP_FROM.length());
                int secondToIndex = args.indexOf(SEP_TO, toIndex + SEP_TO.length());

                if (fromIndex == -1
                        || toIndex == -1
                        || secondFromIndex != -1
                        || secondToIndex != -1
                        || fromIndex > toIndex) {
                    return ui.showInvalidEventFormat();
                }
                String description = args.substring(0, fromIndex).trim();
                if (description.isEmpty()) {
                    return ui.showEmptyDescription("event");
                }
                String from = args.substring(fromIndex + SEP_FROM.length(), toIndex).trim();
                if (from.isEmpty()) {
                    return ui.showInvalidDateTime();
                }
                LocalDateTime formattedFromDateTime;
                LocalDateTime formattedToDateTime;
                try {
                    formattedFromDateTime = Parser.parseUserDateTime(from);
                } catch (DateTimeParseException e) {
                    return ui.showInvalidDateTime();
                }

                String to = args.substring(toIndex + SEP_TO.length()).trim();
                if (to.isEmpty()) {
                    return ui.showInvalidDateTime();
                }
                try {
                    formattedToDateTime = Parser.parseUserDateTime(to);
                } catch (DateTimeParseException e) {
                    return ui.showInvalidDateTime();
                }
                if (!formattedToDateTime.isAfter(formattedFromDateTime)) {
                    return ui.showInvalidEventTimeRange();
                }
                Task task = new Event(description, formattedFromDateTime, formattedToDateTime);
                if (tasks.containsEquivalent(task)) {
                    return ui.showDuplicateTask();
                }
                tasks.add(task);
                try {
                    storage.save(tasks);
                } catch (IOException e) {
                    return ui.showStorageSaveError();
                }
                return ui.showTaskAdded(task, tasks.size());
            }

            case REMOVE: {
                if (tasks.isEmpty()) {
                    return ui.showEmptyTaskListMessage();
                }
                if (!hasExactlyOneTaskNumber(request)) {
                    return request.length < 2
                            ? ui.showMissingTaskNumber()
                            : ui.showInvalidCommand();
                }
                int index;
                try {
                    index = parseAndValidateIndex(request, tasks);
                } catch (IllegalArgumentException e) {
                    if ("missing".equals(e.getMessage())) {
                        return ui.showMissingTaskNumber();
                    } else {
                        return ui.showInvalidTaskNumber(tasks.size());
                    }
                }
                Task task = tasks.get(index);
                tasks.remove(index);

                try {
                    storage.save(tasks);
                } catch (IOException e) {
                    tasks.add(task);
                    return ui.showStorageSaveError();
                }
                return ui.showTaskRemoved(task, tasks.size());
            }

            case FIND: {
                if (tasks.isEmpty()) {
                    return ui.showEmptyTaskListMessage();
                }
                String keyword = input.substring(CMD_FIND.length()).trim();
                if (keyword.isEmpty()) {
                    return ui.showEmptyKeyword();
                }
                ArrayList<Task> foundTasks = tasks.find(keyword);
                return ui.showFoundTasks(foundTasks);
            }

            default: {
                return ui.showInvalidCommand();
            }
        }
    }
}
