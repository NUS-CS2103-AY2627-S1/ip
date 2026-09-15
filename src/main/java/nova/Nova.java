package nova;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Entry point for the Nova chatbot.
 * A Nova instance owns the three collaborators it needs — a Ui to talk to the
 * user, a Storage to persist tasks, and a TaskList holding them — so command
 * handling reads them as fields rather than passing them from method to method.
 *
 * <p>Two interfaces sit on top of the same {@link #processCommand(String)}:
 * {@link #run()} for the console, and {@link #getResponse(String)} for the
 * JavaFX GUI. Both turn one line of input into one line of response text;
 * only how that text is shown differs.
 */
public class Nova {
    /** Location of the file tasks are persisted to, relative to the working directory. */
    private static final String DATA_FILE_PATH = "data/nova.txt";

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * False when the saved file exists but could not be read. Saving is then
     * refused, so a file we failed to load is never overwritten with an empty list.
     */
    private final boolean canSave;

    /** Set when loading failed, so run() can report it after the greeting. Null otherwise. */
    private final String loadError;

    /**
     * Creates a chatbot backed by the given data file, loading any saved tasks.
     * A missing file simply means an empty list. A file that exists but cannot
     * be read disables saving for the session, so the unreadable file is never
     * overwritten. Nothing is printed here; run() reports it in the right order.
     *
     * @param filePath where tasks are loaded from and saved to.
     */
    public Nova(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);

        TaskList loadedTasks;
        String error;

        try {
            loadedTasks = new TaskList(storage.load());
            error = null;
        } catch (FileNotFoundException e) {
            loadedTasks = new TaskList(new ArrayList<>());
            error = "Could not read your saved tasks: " + e.getMessage()
                    + "\nStarting with an empty list, and saving is off so the file is left alone.";
        }

        this.tasks = loadedTasks;
        this.loadError = error;
        this.canSave = (error == null);
    }

    /** Creates a chatbot backed by the default save file. */
    public Nova() {
        this(DATA_FILE_PATH);
    }

    /**
     * Starts the chatbot, reading commands from standard input until the user
     * says goodbye.
     *
     * @param args not used.
     */
    public static void main(String[] args) {
        new Nova().run();
    }

    /**
     * Greets the user, reports any unreadable saved lines, then handles
     * commands until the user says goodbye or the input runs out.
     */
    public void run() {
        ui.showWelcome();

        for (String notice : getStartupNotices()) {
            ui.printMessage(notice);
        }

        runLoop();
    }

    /**
     * Returns anything worth telling the user once the saved tasks are loaded:
     * a file that could not be read, lines that had to be skipped, and events
     * already in the list that clash. Returned rather than printed so the
     * console and the GUI can both show them in their own way.
     *
     * @return the notices in the order they should be shown, possibly empty.
     */
    public ArrayList<String> getStartupNotices() {
        ArrayList<String> notices = new ArrayList<>();

        if (loadError != null) {
            notices.add(loadError);
        }

        if (storage.getSkippedLineCount() > 0) {
            notices.add(String.format("Skipped %d unreadable line(s) in %s.",
                    storage.getSkippedLineCount(), storage.getFilePath()));
        }

        int clashingPairs = tasks.countClashingPairs();
        if (clashingPairs > 0) {
            notices.add(ui.getClashSummary(clashingPairs));
        }

        return notices;
    }

    /**
     * Returns Nova's response to one line of input, for the GUI. Unlike the
     * console, the GUI has no separate exit step: "bye" simply returns its
     * farewell text like any other command, and the window stays open.
     *
     * @param input line of input entered by the user.
     * @return Nova's response.
     */
    public String getResponse(String input) {
        return processCommand(input.trim());
    }

    /**
     * Returns the greeting shown when the GUI starts, with no ASCII banner.
     *
     * @return the greeting text.
     */
    public String getGreeting() {
        return ui.getGreetingMessage();
    }

    /**
     * Writes the current tasks to disk.
     * Every command that changes the list saves through here, so the error
     * handling and the "did the load succeed" check live in one place.
     *
     * @return null on success, or an error message to show the user.
     */
    private String save() {
        if (!canSave) {
            return null;
        }

        try {
            storage.save(tasks);
            return null;
        } catch (IOException e) {
            return "Error. Could not save your tasks: " + e.getMessage();
        }
    }

    /**
     * Builds a list of tasks as one block of text under the given heading.
     * Numbering restarts at 1 for whatever is shown, so a filtered result
     * reads as its own list rather than exposing positions in the full one.
     *
     * @param taskList the tasks to display.
     * @param heading  the line shown above them.
     * @return the block of text.
     */
    private String buildTaskListMessage(TaskList taskList, String heading) {
        ArrayList<String> lines = new ArrayList<>();
        lines.add(heading);

        for (int i = 0; i < taskList.size(); i++) {
            lines.add(String.format("%d.%s", i + 1, taskList.get(i)));
        }

        return String.join("\n", lines);
    }

    /**
     * Returns whether a line of input is the command to exit the console
     * loop. Only run() uses this; getResponse() has no loop to exit.
     *
     * @param input line of input entered by the user.
     * @return true if the command word is "bye", regardless of case.
     */
    private static boolean isExitCommand(String input) {
        return input.split("\\s+", 2)[0].equalsIgnoreCase("bye");
    }

    /**
     * Appends a save-failure message to a response, if saving failed.
     *
     * @param message   the response built so far.
     * @param saveError null on success, or the error save() returned.
     * @return the combined response.
     */
    private static String withSaveResult(String message, String saveError) {
        return saveError == null ? message : message + "\n" + saveError;
    }

    /**
     * Turns one line of input into Nova's response text. This is the single
     * place command dispatch happens; both run() and getResponse() call it,
     * so the console and the GUI can never disagree about what a command does.
     *
     * @param input one line of input, already trimmed.
     * @return the response text.
     */
    private String processCommand(String input) {
        // Both entry points trim before calling. If a third one ever forgets,
        // a leading space would make the command word "" and every command
        // would silently fall through to the default branch.
        assert input.equals(input.trim()) : "processCommand() given untrimmed input: \"" + input + "\"";

        if (input.isEmpty()) { // Accounts for empty inputs so we don't get "empty" tasks in the arraylist
            return "Type something!";
        }

        String[] parts = input.split("\\s+", 2);
        String command = parts[0].toLowerCase();

        String rawArgument = parts.length > 1 ? parts[1].trim() : "";

        // Stripped here rather than inside handleEvent, because the event
        // command reads everything after "/to" as the end date and would
        // otherwise try to parse the marker as part of it.
        boolean isForced = Parser.hasForceMarker(rawArgument);
        if (isForced && !command.equals("event")) {
            return Parser.FORCE_MARKER
                    + " only applies to events, which are the only tasks that can clash.";
        }

        String argument = isForced ? Parser.removeForceMarker(rawArgument) : rawArgument;

        return switch (command) {
            case "bye" -> ui.getFarewellMessage();
            case "list" -> handleList();
            case "find" -> handleFind(argument);
            case "mark", "unmark" -> handleMarkOrUnmark(command, argument);
            case "delete" -> handleDelete(argument);
            case "todo" -> handleTodo(argument);
            case "deadline" -> handleDeadline(argument);
            case "event" -> handleEvent(argument, isForced);
            default -> "Input valid command - start with todo, deadline, event,"
                    + " list, find, mark, unmark or delete";
        };
    }

    /**
     * Shows every task currently stored.
     *
     * @return the numbered list, or a prompt if nothing is stored yet.
     */
    private String handleList() {
        if (tasks.isEmpty()) {
            return "Your list is empty! Add something.";
        }

        return buildTaskListMessage(tasks, "Here are the tasks in your list:");
    }

    /**
     * Shows the tasks whose description contains the given keyword.
     *
     * @param keyword text after the "find" command word.
     * @return the matching tasks, or a message explaining why there are none.
     */
    private String handleFind(String keyword) {
        if (keyword.isBlank()) {
            return "Pls specify a keyword to search for, e.g. find book";
        }

        TaskList matches = tasks.find(keyword);
        if (matches.isEmpty()) {
            return "No tasks in your list mention \"" + keyword + "\".";
        }

        return buildTaskListMessage(matches, "Here are the matching tasks in your list:");
    }

    /**
     * Changes the done status of one task.
     *
     * @param command  either "mark" or "unmark", deciding which way to set it.
     * @param argument text after the command word, expected to be a task number.
     * @return confirmation of the change, or an explanation of what was wrong.
     */
    private String handleMarkOrUnmark(String command, String argument) {
        if (!Parser.isInteger(argument)) {
            return "Invalid argument! Specify which task you wish to mark/unmark";
        }

        int index = Integer.parseInt(argument) - 1; // the user counts from 1
        if (index < 0 || index >= tasks.size()) {
            return "The number you have entered does not exist in your list."
                    + " Try again!";
        }

        boolean isMarking = command.equals("mark");
        Task updated = isMarking ? tasks.mark(index) : tasks.unmark(index);

        assert updated.isDone() == isMarking : "task status does not match the command applied";

        return withSaveResult(ui.getTaskMarkedMessage(updated, isMarking), save());
    }

    /**
     * Removes one task from the list.
     *
     * @param argument text after the "delete" command word, expected to be a task number.
     * @return confirmation of the removal, or an explanation of what was wrong.
     */
    private String handleDelete(String argument) {
        if (argument.isBlank()) {
            return "Pls specify which task to delete!";
        }

        if (!Parser.isInteger(argument)) {
            return "Specify a number after the command delete";
        }

        int index = Integer.parseInt(argument) - 1; // the user counts from 1
        if (index < 0 || index >= tasks.size()) {
            return "Specify a valid task number!";
        }

        Task removed = tasks.remove(index);
        return withSaveResult(ui.getTaskRemovedMessage(removed, tasks.size()), save());
    }

    /**
     * Adds a task with no date attached.
     *
     * @param description text after the "todo" command word.
     * @return confirmation of the addition, or the usage hint.
     */
    private String handleTodo(String description) {
        if (description.isBlank()) {
            return "Pls specify your to-do task after the command todo!";
        }

        return addTask(new ToDo(description, false));
    }

    /**
     * Adds a task due by a given date, written "{@code <description>} /by {@code <date>}".
     *
     * @param argument text after the "deadline" command word.
     * @return confirmation of the addition, or the usage hint.
     */
    private String handleDeadline(String argument) {
        MarkerParts descriptionAndBy = Parser.splitOnMarker(argument, "/by");
        if (descriptionAndBy == null) {
            return "Use: deadline <task name> /by <end>";
        }

        LocalDateTime by = Parser.parseDateTime(descriptionAndBy.after());
        if (by == null) {
            return "I couldn't understand that date. " + Parser.DATE_FORMAT_HINT;
        }

        return addTask(new Deadline(descriptionAndBy.before(), false, by));
    }

    /**
     * Adds a task spanning two dates, written
     * "{@code <description>} /from {@code <start>} /to {@code <end>}".
     *
     * @param argument text after the "event" command word, with any "/force"
     *                 marker already removed.
     * @param isForced true if the user asked to add the event even though it
     *                 clashes with one already in the list.
     * @return confirmation of the addition, the usage hint, or a refusal.
     */
    private String handleEvent(String argument, boolean isForced) {
        String usageHint = "Use: event <task name> /from <start> /to <end>";

        MarkerParts descriptionAndRest = Parser.splitOnMarker(argument, "/from");
        if (descriptionAndRest == null) {
            return usageHint;
        }

        MarkerParts fromAndTo = Parser.splitOnMarker(descriptionAndRest.after(), "/to");
        if (fromAndTo == null) {
            return usageHint;
        }

        LocalDateTime from = Parser.parseDateTime(fromAndTo.before());
        LocalDateTime to = Parser.parseDateTime(fromAndTo.after());
        if (from == null || to == null) {
            return "I couldn't understand that date. " + Parser.DATE_FORMAT_HINT;
        }

        // A backwards range is invalid rather than merely inconvenient, so
        // /force does not override it: there is nothing sensible to compare
        // such an event against.
        if (from.isAfter(to)) {
            return "An event cannot end before it starts.";
        }

        Event newEvent = new Event(descriptionAndRest.before(), false, from, to);

        if (!isForced) {
            ArrayList<Event> clashes = tasks.findClashes(newEvent);
            if (!clashes.isEmpty()) {
                return ui.getClashMessage(clashes);
            }
        }

        return addTask(newEvent);
    }

    /**
     * Stores a newly created task and reports it, saving the updated list.
     * The three add commands differ only in how they build the task, so the
     * steps that follow are shared here rather than repeated in each.
     *
     * @param task the task to store.
     * @return confirmation of the addition, with any save failure appended.
     */
    private String addTask(Task task) {
        tasks.add(task);
        return withSaveResult(ui.getTaskAddedMessage(task, tasks.size()), save());
    }

    private void runLoop() {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim(); // Removes leading and trailing whitespaces
            ui.printMessage(processCommand(input));

            if (isExitCommand(input)) {
                return;
            }
        }
        ui.printMessage(ui.getFarewellMessage());
    }
}
