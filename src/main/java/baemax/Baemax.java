package baemax;

import java.util.List;

/**
 * The Baemax chatbot: a to-do assistant that remembers its tasks between runs.
 * This class wires together the {@link Storage} and {@link TaskList}
 * collaborators and turns each user command into a response. It can be driven
 * from the command line via {@link #run()} or from a GUI via
 * {@link #getResponse(String)}.
 */
public class Baemax {
    /** Reads tasks from and writes tasks to the save file. */
    private final Storage storage;

    /** The tasks the user is tracking. */
    private final TaskList tasks;

    /** Handles reading commands from and printing responses to the console. */
    private final Ui ui;

    /**
     * A copy of the tasks as they were just before the most recent
     * list-changing command, or {@code null} when there is nothing to undo
     * (either nothing has changed yet, or the last change was already undone).
     */
    private List<Task> undoSnapshot;

    /**
     * Creates a chatbot that loads from and saves to the given file.
     *
     * @param filePath the save file path, relative to the project root
     */
    public Baemax(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList(storage.load());
    }

    /**
     * Starts the chatbot on the command line.
     *
     * @param args command-line arguments, which this chatbot does not use
     */
    public static void main(String[] args) {
        new Baemax("data/baemax.txt").run();
    }

    /**
     * Returns the greeting shown when the chatbot starts.
     *
     * @return the welcome message
     */
    public String getWelcomeMessage() {
        return "Hello, I am Baemax, your personal task manager companion!\nWhat can I do for you today?";
    }

    /**
     * Handles one line of user input and returns Baemax's reply text.
     *
     * @param input the raw command entered by the user
     * @return the reply to show the user
     */
    public String getResponse(String input) {
        return getReply(input).text();
    }

    /**
     * Handles one line of user input and returns Baemax's reply, along with
     * whether that reply was an error - so a front end can style the two
     * differently, e.g. a GUI colouring an error reply's bubble.
     *
     * @param input the raw command entered by the user
     * @return the reply
     */
    public Reply getReply(String input) {
        if (input.trim().equals("bye")) {
            return new Reply("Bye! I am satisfied with the care I have provided today. Have a lovely day!", false);
        }
        try {
            return new Reply(processCommand(input), false);
        } catch (BaemaxException exception) {
            return new Reply(exception.getMessage(), true);
        }
    }

    /**
     * One reply from Baemax: the text to show, and whether it reports an
     * error rather than a successful command.
     */
    public static final class Reply {
        private final String text;
        private final boolean error;

        private Reply(String text, boolean error) {
            this.text = text;
            this.error = error;
        }

        /**
         * Returns the reply text to show the user.
         *
         * @return the reply text
         */
        public String text() {
            return text;
        }

        /**
         * Returns whether this reply reports an error.
         *
         * @return {@code true} for an error reply, {@code false} for a
         *     successful command's reply
         */
        public boolean isError() {
            return error;
        }
    }

    /** Greets the user, then handles commands until the user says goodbye. */
    public void run() {
        ui.showBanner();
        ui.show(getWelcomeMessage());

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showLine();
            ui.show(getResponse(command));
            ui.showLine();
            isExit = command.trim().equals("bye");
        }
        ui.close();
    }

    /**
     * Executes one non-exit command and returns its response text.
     *
     * @param command the complete command entered by the user
     * @return the response to show the user
     * @throws BaemaxException when the command is invalid
     */
    private String processCommand(String command) throws BaemaxException {
        String trimmedCommand = normalizeCommandWordCase(command.trim());
        if (trimmedCommand.isEmpty()) {
            throw new BaemaxException(
                    "Baemax did not catch a command. Try help, todo, list, find, mark, delete, undo, or bye.");
        }

        if (trimmedCommand.equals("list")) {
            return displayTasks();
        } else if (trimmedCommand.equals("mark") || trimmedCommand.startsWith("mark ")) {
            return updateTaskStatus(trimmedCommand, true);
        } else if (trimmedCommand.equals("unmark") || trimmedCommand.startsWith("unmark ")) {
            return updateTaskStatus(trimmedCommand, false);
        } else if (trimmedCommand.equals("delete") || trimmedCommand.startsWith("delete ")) {
            return deleteTask(trimmedCommand);
        } else if (trimmedCommand.equals("find") || trimmedCommand.startsWith("find ")) {
            return findTasks(trimmedCommand);
        } else if (trimmedCommand.equals("undo")) {
            return undo();
        } else if (trimmedCommand.equals("help")) {
            return help();
        } else {
            return addTask(Parser.parseTask(trimmedCommand));
        }
    }

    /**
     * Lower-cases just the command word so that {@code TODO}, {@code Mark},
     * and the like are recognised the same as their lowercase form, while
     * leaving everything after the first space - descriptions, keywords,
     * dates - exactly as the user typed it.
     *
     * @param trimmedCommand the command text with leading/trailing whitespace
     *     already removed
     * @return the same text with only its first word lower-cased
     */
    private static String normalizeCommandWordCase(String trimmedCommand) {
        int firstSpace = trimmedCommand.indexOf(' ');
        if (firstSpace < 0) {
            return trimmedCommand.toLowerCase();
        }
        return trimmedCommand.substring(0, firstSpace).toLowerCase() + trimmedCommand.substring(firstSpace);
    }

    /** Returns every stored task, numbered from one. */
    private String displayTasks() {
        return numberedList("Here are the tasks in your list:", tasks.asList());
    }

    /** Returns a summary of every command Baemax understands and how to use it. */
    private String help() {
        return lines(
                "Here's what I can do to help take care of your tasks:",
                "  todo <description>",
                "  deadline <description> /by <date> [time]",
                "  event <description> /from <date> [time] /to <date> [time]",
                "  list",
                "  find <keyword>",
                "  mark <task number>",
                "  unmark <task number>",
                "  delete <task number>",
                "  undo",
                "  bye",
                "",
                "Dates look like 2026-09-20, 20/9/2026, or 20 Sep 2026, optionally",
                "followed by a time like 1800, 6pm, or 6:30pm.");
    }

    /**
     * Returns the tasks whose description contains the keyword given after
     * {@code find}, numbered from one.
     *
     * @param command a command such as {@code find book}
     * @return the matching tasks, numbered
     * @throws BaemaxException when no keyword follows {@code find}
     */
    private String findTasks(String command) throws BaemaxException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new BaemaxException("Baemax needs a keyword to search for. Try: find <keyword>.");
        }
        return numberedList("Here are the matching tasks in your list:", tasks.find(keyword));
    }

    /**
     * Adds a new not-done task to the end of the list and saves it.
     *
     * @param task the task to add
     * @return the confirmation message
     */
    private String addTask(Task task) {
        rememberForUndo();
        tasks.add(task);
        storage.save(tasks.asList());
        return lines("Got it. I've added this task:",
                "  " + task,
                "Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Marks or unmarks a task selected by its one-based list number.
     *
     * @param command a command such as {@code mark 2} or {@code unmark 2}
     * @param completed whether the selected task should be marked done
     * @return the confirmation message
     * @throws BaemaxException when the command has no valid task number
     */
    private String updateTaskStatus(String command, boolean completed) throws BaemaxException {
        int taskNumber = Parser.parseTaskNumber(command, completed ? "mark" : "unmark", tasks.size());
        // Parser.parseTaskNumber already rejects anything outside [1, tasks.size()];
        // this only guards against Parser itself regressing that contract.
        assert taskNumber >= 1 && taskNumber <= tasks.size() : "parseTaskNumber returned an out-of-range number";
        rememberForUndo();
        Task task = tasks.get(taskNumber);

        String heading;
        String encouragement;
        if (completed) {
            task.markAsDone();
            heading = "Nice! I've marked this task as done:";
            encouragement = "Great job looking after yourself!";
        } else {
            task.markAsUndone();
            heading = "OK, I've marked this task as not done yet:";
            encouragement = "No worries, take your time.";
        }
        storage.save(tasks.asList());
        return lines(heading, "  " + task, encouragement);
    }

    /**
     * Removes a task selected by its one-based list number and saves the list.
     *
     * @param command a command such as {@code delete 2}
     * @return the confirmation message
     * @throws BaemaxException when the command has no valid task number
     */
    private String deleteTask(String command) throws BaemaxException {
        int taskNumber = Parser.parseTaskNumber(command, "delete", tasks.size());
        // Same contract as updateTaskStatus: Parser guarantees the range already.
        assert taskNumber >= 1 && taskNumber <= tasks.size() : "parseTaskNumber returned an out-of-range number";
        rememberForUndo();
        Task removedTask = tasks.remove(taskNumber);
        storage.save(tasks.asList());
        return lines("Noted. I've removed this task:",
                "  " + removedTask,
                "Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Restores the tasks to how they were just before the most recent
     * add, delete, mark, or unmark, and saves that restored state.
     *
     * @return the confirmation message, including the restored list
     * @throws BaemaxException when there is nothing to undo
     */
    private String undo() throws BaemaxException {
        if (undoSnapshot == null) {
            throw new BaemaxException("There's nothing to undo yet.");
        }
        tasks.restoreFrom(undoSnapshot);
        storage.save(tasks.asList());
        undoSnapshot = null;
        return numberedList("Undone! Here are the tasks in your list now:", tasks.asList());
    }

    /**
     * Snapshots the current tasks so a later {@link #undo()} can restore
     * exactly this state. Called just before a list-changing command takes
     * effect, once any validation that could still fail has passed - a
     * command that ends up throwing never overwrites a usable snapshot.
     */
    private void rememberForUndo() {
        undoSnapshot = tasks.snapshot();
    }

    /** Formats a heading followed by the given tasks, each on its own line and numbered from one. */
    private static String numberedList(String heading, List<Task> items) {
        StringBuilder builder = new StringBuilder(heading);
        for (int i = 0; i < items.size(); i++) {
            builder.append("\n").append(i + 1).append(". ").append(items.get(i));
        }
        return builder.toString();
    }

    /**
     * Joins the given lines with newlines into a single response block.
     *
     * @param lines the lines to join, in order
     * @return the lines separated by {@code \n}
     */
    private static String lines(String... lines) {
        return String.join("\n", lines);
    }
}
