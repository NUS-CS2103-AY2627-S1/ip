package apollo;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import apollo.parser.Parser;
import apollo.parser.Parser.Command;
import apollo.storage.Storage;
import apollo.task.Deadline;
import apollo.task.Event;
import apollo.task.Task;
import apollo.task.TaskList;

/**
 * Processes Apollo commands independently of the interface used to enter them.
 * Both the JavaFX and command-line front ends use the same Apollo instance as
 * the application's command-processing core.
 */
public class Apollo {
    private static final String DEFAULT_STORAGE_PATH = "data/apollo.txt";
    private static final int MAX_UNDO_HISTORY = 5;
    private static final String EXIT_MESSAGE =
            "Go now beneath the sun's bright path, mortal. Apollo awaits your return.";

    private final Storage storage;
    private final Parser parser;
    /** Stores inverse operations from newest to oldest for session-only undo. */
    private final Deque<Runnable> undoHistory = new ArrayDeque<>();
    private TaskList tasks;
    private boolean isExitRequested;
    private ResponseType responseType = ResponseType.DEFAULT;
    private String loadingError;

    /** Creates Apollo using the default task storage file. */
    public Apollo() {
        this(DEFAULT_STORAGE_PATH);
    }

    /**
     * Creates Apollo using the specified task storage file.
     *
     * @param storagePath path of the file used to load and save tasks
     */
    public Apollo(String storagePath) {
        this.storage = new Storage(storagePath);
        this.parser = new Parser();
        this.tasks = new TaskList();
        loadTasks();
    }

    /**
     * Processes one user command and returns the message to display.
     *
     * @param input complete command entered by the user
     * @return Apollo's response to the command
     */
    public String getResponse(String input) {
        this.responseType = ResponseType.DEFAULT;

        try {
            Command command = this.parser.parseCommand(input);
            // Invalid commands should cause the parser to throw, never return null.
            assert command != null : "Parser must return a command after successful parsing";
            this.responseType = mapResponseType(command);

            return switch (command) {
                case BYE -> processBye();
                case LIST -> formatTaskList();
                case MARK -> processMark(input, true);
                case UNMARK -> processMark(input, false);
                case TODO -> processTodo(input);
                case EVENT -> processEvent(input);
                case DEADLINE -> processDeadline(input);
                case DUE_TODAY -> formatDeadlinesDueOn(LocalDate.now());
                case ONGOING_NOW -> formatOngoingEvents();
                case DUE_THIS_DATE -> processDueThisDate(input);
                case DELETE -> processDelete(input);
                case FIND -> processFind(input);
                case UNDO -> processUndo();
            };
        } catch (Exception e) {
            this.responseType = ResponseType.ERROR;
            return "Even my oracle cannot divine that command, mortal. Try again.";
        }
    }

    /** Returns the category of the most recently produced response for GUI styling. */
    public ResponseType getResponseType() {
        return this.responseType;
    }

    /** Returns whether the current front-end session has received {@code bye}. */
    public boolean isExitRequested() {
        return this.isExitRequested;
    }

    /**
     * Returns the loading error encountered during construction, or {@code null}
     * if the task file loaded successfully.
     */
    public String getLoadingError() {
        return this.loadingError;
    }

    /** Returns whether the current session contains an action that can be undone. */
    public boolean canUndo() {
        return !this.undoHistory.isEmpty();
    }

    /** Loads saved tasks without depending on either the console or JavaFX UI. */
    private void loadTasks() {
        try {
            this.tasks = new TaskList(this.storage.load());
        } catch (IOException e) {
            this.loadingError = "A shadow has fallen across my records: " + e.getMessage();
        }
    }

    /** Processes a request to end the current front-end session. */
    private String processBye() {
        this.isExitRequested = true;
        return EXIT_MESSAGE;
    }

    /** Processes a command that changes a task's completion state. */
    private String processMark(String input, boolean isDone) {
        try {
            int index = this.parser.parseIndex(input);
            Task task = this.tasks.get(index);
            boolean previousState = task.getIsDone();
            task.markAsDone(isDone);
            if (previousState != isDone) {
                recordUndo(() -> task.markAsDone(previousState));
            }
            assert task.getIsDone() == isDone : "Task completion state must match the requested state";
            return appendSavingErrorIfNeeded(formatMarkChange(task));
        } catch (Exception e) {
            this.responseType = ResponseType.ERROR;
            return "Name a valid task number, mortal.";
        }
    }

    /** Processes a todo command and adds the resulting task. */
    private String processTodo(String input) {
        try {
            return addTask(this.parser.parseTodo(input));
        } catch (Exception e) {
            this.responseType = ResponseType.ERROR;
            return "Even Apollo cannot inscribe a task without a description, mortal.";
        }
    }

    /** Processes an event command and adds the resulting task. */
    private String processEvent(String input) {
        try {
            return addTask(this.parser.parseEvent(input));
        } catch (DateTimeParseException e) {
            this.responseType = ResponseType.ERROR;
            return "The stars reject that date. Use d/M/yyyy or d/M/yyyy HHmm, mortal.";
        } catch (Exception e) {
            this.responseType = ResponseType.ERROR;
            return "The oracle cannot read that event. "
                    + "Use: event DESCRIPTION /from DATE_TIME /to DATE_TIME.";
        }
    }

    /** Processes a deadline command and adds the resulting task. */
    private String processDeadline(String input) {
        try {
            return addTask(this.parser.parseDeadline(input));
        } catch (DateTimeParseException e) {
            this.responseType = ResponseType.ERROR;
            return "The stars reject that date. Use d/M/yyyy or d/M/yyyy HHmm, mortal.";
        } catch (Exception e) {
            this.responseType = ResponseType.ERROR;
            return "The oracle cannot read that deadline. "
                    + "Use: deadline DESCRIPTION /by DATE_TIME.";
        }
    }

    /** Processes a query for deadlines due on a specified date. */
    private String processDueThisDate(String input) {
        try {
            return formatDeadlinesDueOn(this.parser.parseDueDate(input));
        } catch (DateTimeParseException e) {
            this.responseType = ResponseType.ERROR;
            return "The oracle requires a date in d/M/yyyy format, mortal.";
        }
    }

    /** Processes a task deletion command. */
    private String processDelete(String input) {
        try {
            int index = this.parser.parseIndex(input);
            Task deletedTask = this.tasks.delete(index);
            recordUndo(() -> this.tasks.add(index, deletedTask));
            String response = String.format(
                    "So it is decreed. I have cast this task from your list:%n%s%n"
                            + "%d tasks remain beneath my gaze.",
                    deletedTask, this.tasks.size());
            return appendSavingErrorIfNeeded(response);
        } catch (Exception e) {
            this.responseType = ResponseType.ERROR;
            return "Name a valid task number, mortal.";
        }
    }

    /** Processes a task search command. */
    private String processFind(String input) {
        try {
            String searchText = this.parser.parseFindText(input);
            return formatMatchingTasks(this.tasks.find(searchText));
        } catch (IllegalArgumentException e) {
            this.responseType = ResponseType.ERROR;
            return "Give my light something to seek, mortal.";
        }
    }

    /** Adds a task and returns its confirmation message. */
    private String addTask(Task task) {
        // Only successfully parsed tasks should reach this internal method.
        assert task != null : "A successfully parsed task must not be null";
        int addedIndex = this.tasks.size();
        this.tasks.add(task);
        recordUndo(() -> this.tasks.delete(addedIndex));
        String response = String.format(
                "Your will is heard, child. I inscribe this task beneath the sun:%n%s", task);
        return appendSavingErrorIfNeeded(response);
    }

    /** Reverses the most recent undoable action in the current session. */
    private String processUndo() {
        if (this.undoHistory.isEmpty()) {
            this.responseType = ResponseType.ERROR;
            return "There is nothing for Apollo to undo, mortal.";
        }

        this.undoHistory.pop().run();
        return appendSavingErrorIfNeeded(
                "The threads of fate turn back; your previous action is undone, child.");
    }

    /** Records an inverse operation while retaining only the five newest operations. */
    private void recordUndo(Runnable undoAction) {
        assert undoAction != null : "An undo action must not be null";
        this.undoHistory.push(undoAction);
        if (this.undoHistory.size() > MAX_UNDO_HISTORY) {
            this.undoHistory.removeLast();
        }
    }

    /** Formats the confirmation for a mark or unmark operation. */
    private String formatMarkChange(Task task) {
        String change = task.getIsDone() ? "marked" : "unmarked";
        return String.format(
                "Your prayer is heard, child. I have %s this task:%n%s",
                change, task);
    }

    /** Formats every task with one-based numbering. */
    private String formatTaskList() {
        StringBuilder response = new StringBuilder("Behold the tasks recorded beneath my light:");
        appendNumberedTasks(response, this.tasks.getTasks());
        return response.toString();
    }

    /** Formats events that are ongoing at the current time. */
    private String formatOngoingEvents() {
        List<Event> events = this.tasks.getEventsOngoingAt(LocalDateTime.now());
        StringBuilder response = new StringBuilder("These events now unfold beneath the sun:");
        appendNumberedTasks(response, events);
        if (events.isEmpty()) {
            response.append(System.lineSeparator()).append("The present hour is free of events.");
        }
        return response.toString();
    }

    /** Formats deadlines due on the supplied date. */
    private String formatDeadlinesDueOn(LocalDate date) {
        List<Deadline> deadlines = this.tasks.getDeadlinesDueOn(date);
        StringBuilder response = new StringBuilder(String.format(
                "The oracle reveals these deadlines for %s:", date));
        appendNumberedTasks(response, deadlines);
        if (deadlines.isEmpty()) {
            response.append(System.lineSeparator())
                    .append("No deadline is written for this date.");
        }
        return response.toString();
    }

    /** Formats tasks whose descriptions match the supplied search text. */
    private String formatMatchingTasks(List<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            return "My light reveals no task bearing those words.";
        }

        StringBuilder response = new StringBuilder("My light has revealed these matching tasks:");
        appendNumberedTasks(response, matchingTasks);
        return response.toString();
    }

    /** Appends tasks to a message using one-based numbering. */
    private void appendNumberedTasks(StringBuilder response, List<? extends Task> listedTasks) {
        for (int i = 0; i < listedTasks.size(); i++) {
            response.append(System.lineSeparator())
                    .append(String.format("%d. %s", i + 1, listedTasks.get(i)));
        }
    }

    /** Saves a mutation and adds a user-facing warning if persistence fails. */
    private String appendSavingErrorIfNeeded(String response) {
        try {
            this.storage.save(this.tasks.getTasks());
            return response;
        } catch (IOException e) {
            return response + System.lineSeparator()
                    + "A shadow clouds the archive; I could not save your tasks.";
        }
    }

    /** Maps parser commands onto response categories understood by the GUI. */
    private ResponseType mapResponseType(Command command) {
        return switch (command) {
            case TODO, EVENT, DEADLINE -> ResponseType.ADD;
            case MARK, UNMARK -> ResponseType.MARK_CHANGE;
            case DELETE -> ResponseType.DELETE;
            case LIST, FIND, DUE_TODAY, ONGOING_NOW, DUE_THIS_DATE -> ResponseType.LIST;
            case UNDO -> ResponseType.DEFAULT;
            case BYE -> ResponseType.BYE;
        };
    }
}
