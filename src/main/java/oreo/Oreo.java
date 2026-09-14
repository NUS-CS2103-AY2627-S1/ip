package oreo;

import oreo.exception.OreoException;
import oreo.task.Task;
import oreo.task.TaskList;

/** Coordinates Oreo's command parser, task list, storage, and response messages. */
public class Oreo {
    private Storage storage;
    private TaskList tasks;
    private final Ui ui;
    private final Parser parser;
    private boolean hasExited;
    private String loadingMessage = "";

    /** Creates the chatbot and loads its saved tasks. */
    public Oreo(String filePath) {
        ui = new Ui();
        parser = new Parser();
        try {
            storage = new Storage(filePath);
            tasks = new TaskList(storage.load());
        } catch (OreoException e) {
            loadingMessage = "Unable to access saved tasks. Starting with an empty task list.";
            tasks = new TaskList();
        }
    }

    /** Returns Oreo's initial greeting, including any recoverable loading warning. */
    public String getWelcomeMessage() {
        return loadingMessage.isEmpty() ? ui.getWelcomeMessage()
                : loadingMessage + System.lineSeparator() + ui.getWelcomeMessage();
    }

    /**
     * Processes one command and returns the message that should be shown to the user.
     *
     * @param command command entered by the user
     * @return Oreo's response to the command
     */
    public String getResponse(String command) {
        if (hasExited) {
            return "Oreo has already said goodbye. Please restart the app to continue.";
        }

        ui.beginResponse();
        try {
            String normalizedCommand = normalizeCommand(command);
            return switch (parser.parseCommand(normalizedCommand)) {
                case BYE -> normalizedCommand.equals("bye") ? exit() : ui.getErrorMessage("Use: bye.");
                case LIST -> normalizedCommand.equals("list") ? ui.getTaskListMessage(tasks)
                        : ui.getErrorMessage("Use: list or list tags.");
                case LIST_TAGS -> listTags(normalizedCommand);
                case FIND -> findTasks(normalizedCommand);
                case MARK -> updateTaskStatus(normalizedCommand, true);
                case UNMARK -> updateTaskStatus(normalizedCommand, false);
                case TODO -> addTask(parser.parseTodo(normalizedCommand));
                case DEADLINE -> tryAddDeadline(normalizedCommand);
                case EVENT -> tryAddEvent(normalizedCommand);
                case DELETE -> deleteTask(normalizedCommand);
                case TAG -> updateTags(normalizedCommand, true);
                case UNTAG -> updateTags(normalizedCommand, false);
                case UNKNOWN -> ui.getErrorMessage("Unknown command. Try todo, list, find, mark, or bye.");
            };
        } catch (OreoException | IllegalArgumentException e) {
            return ui.getErrorMessage(e.getMessage());
        }
    }

    /** Normalizes harmless whitespace while keeping the command syntax predictable. */
    private String normalizeCommand(String command) {
        if (command == null || command.isBlank()) {
            throw new OreoException("Please enter a command.");
        }
        return command.trim().replaceAll("\\s+", " ");
    }

    /** Returns whether the user has ended this Oreo session. */
    public boolean hasExited() {
        return hasExited;
    }

    /** Returns whether the last response resulted from invalid user input. */
    public boolean wasLastResponseAnError() {
        return ui.wasLastMessageAnError();
    }

    /** Saves the task list and returns a farewell. */
    private String exit() {
        saveTasks();
        hasExited = true;
        return ui.getGoodbyeMessage();
    }

    /** Persists the current task list after a state-changing command. */
    private void saveTasks() {
        if (storage == null) {
            throw new OreoException("Task storage is unavailable; changes cannot be saved.");
        }
        storage.save(tasks.storageStringRepresentation());
    }

    /** Adds a task and returns its confirmation message. */
    private String addTask(Task task) {
        assert task != null : "A successfully parsed command must produce a task.";
        tasks.addTask(task);
        saveTasks();
        return ui.getTaskAddedMessage(task, tasks.getTaskCount());
    }

    /** Finds tasks whose descriptions contain the supplied keyword. */
    private String findTasks(String command) {
        try {
            return ui.getMatchingTasksMessage(tasks.findTasks(parser.parseFindKeyword(command)));
        } catch (OreoException e) {
            return ui.getErrorMessage(e.getMessage());
        }
    }

    /** Lists tags and the tasks currently associated with each tag. */
    private String listTags(String command) {
        try {
            parser.parseListTags(command);
            return ui.getTagListMessage(tasks.listTags());
        } catch (OreoException e) {
            return ui.getErrorMessage(e.getMessage());
        }
    }

    /** Adds or removes tags from an existing task. */
    private String updateTags(String command, boolean shouldAdd) {
        String commandWord = shouldAdd ? "tag" : "untag";
        try {
            Parser.TagUpdate tagUpdate = parser.parseTagUpdate(command, commandWord);
            Task task = shouldAdd
                    ? tasks.addTags(tagUpdate.taskNumber(), tagUpdate.tags())
                    : tasks.removeTags(tagUpdate.taskNumber(), tagUpdate.tags());
            saveTasks();
            return shouldAdd ? ui.getTagsAddedMessage(task) : ui.getTagsRemovedMessage(task);
        } catch (OreoException e) {
            return ui.getErrorMessage(e.getMessage());
        }
    }

    /** Marks or unmarks a task, depending on the supplied state. */
    private String updateTaskStatus(String command, boolean shouldMark) {
        try {
            String commandWord = shouldMark ? "mark " : "unmark ";
            if (!command.startsWith(commandWord)) {
                throw new OreoException("Please provide a task number to " + (shouldMark ? "mark." : "unmark."));
            }
            int taskNumber = Integer.parseInt(command.substring(commandWord.length()).trim());
            Task task = shouldMark ? tasks.markTask(taskNumber) : tasks.unmarkTask(taskNumber);
            saveTasks();
            return shouldMark ? ui.getTaskMarkedMessage(task) : ui.getTaskUnmarkedMessage(task);
        } catch (NumberFormatException e) {
            return ui.getErrorMessage("Please provide a task number to " + (shouldMark ? "mark." : "unmark."));
        } catch (OreoException e) {
            return ui.getErrorMessage(e.getMessage());
        }
    }

    /** Parses and adds a deadline, returning an error when it is invalid. */
    private String tryAddDeadline(String command) {
        try {
            return addTask(parser.parseDeadline(command));
        } catch (OreoException e) {
            return ui.getErrorMessage(e.getMessage());
        }
    }

    /** Parses and adds an event, returning an error when it is invalid. */
    private String tryAddEvent(String command) {
        try {
            return addTask(parser.parseEvent(command));
        } catch (OreoException e) {
            return ui.getErrorMessage(e.getMessage());
        }
    }

    /** Deletes a task and returns the outcome. */
    private String deleteTask(String command) {
        try {
            if (!command.startsWith("delete ")) {
                throw new OreoException("Please provide a task number to delete.");
            }
            int taskNumber = Integer.parseInt(command.substring("delete ".length()).trim());
            Task task = tasks.deleteTask(taskNumber);
            saveTasks();
            return ui.getTaskDeletedMessage(task, tasks.getTaskCount());
        } catch (NumberFormatException e) {
            return ui.getErrorMessage("Please provide a task number to delete.");
        } catch (OreoException e) {
            return ui.getErrorMessage(e.getMessage());
        }
    }
}
