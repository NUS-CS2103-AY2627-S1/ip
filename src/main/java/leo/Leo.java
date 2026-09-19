package leo;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Core of the Leo chatbot: loads any previously-saved tasks, then can
 * either run as a console chat loop ({@link #run()}, used by
 * {@link #main(String[])}) or answer one command at a time
 * ({@link #getReply(String)}/{@link #getResponse(String)}, used by the
 * JavaFX GUI - see {@link Launcher} and {@link MainWindow}). Both entry
 * points share the exact same command-handling code in
 * {@link #processCommand}; only where that code's output goes differs
 * (the console, or back to the GUI as a {@link Reply}/String), via the
 * {@link Ui}/{@link GuiUi} passed in.
 */
public class Leo {
    // A-BetterGui: a small rotating set of friendly closing lines shown
    // after a task is successfully added (see pickFlourish/
    // handleTodo/handleDeadline/handleEvent below) - purely decorative,
    // appended as an extra line after the existing required output, so
    // it never changes any of the wording other code (or a test) might
    // depend on.
    private static final String[] TASK_ADDED_FLOURISHES = {
            "You're on a roll! 🎉",
            "Nice, one step closer to done.",
            "Look at you, staying organised!",
            "Small steps add up - great work.",
            "That's another one sorted."
    };

    private final Ui ui;
    private final TaskList tasks;

    /**
     * Creates a new Leo, loading any previously-saved tasks from disk.
     */
    public Leo() {
        this.ui = new Ui();

        List<Task> loadedTasks;

        try {
            loadedTasks = Storage.loadTasks();
        } catch (IOException e) {
            ui.showMessage(
                    "I couldn't load your saved tasks: " + e.getMessage()
            );
            loadedTasks = new ArrayList<>();
        }

        this.tasks = new TaskList(
                loadedTasks.toArray(new Task[0]), loadedTasks.size());
    }

    /**
     * Runs Leo as an interactive console chat loop: shows the welcome
     * banner, then repeatedly reads a command from standard input and
     * handles it, until the user says "bye".
     */
    public void run() {
        ui.showWelcome();

        while (true) {
            String input = ui.readCommand();
            processCommand(input, ui);

            if (Parser.getCommandWord(input).equals("bye")) {
                break;
            }
        }

        ui.close();
    }

    /**
     * Handles a single command and returns Leo's reply as a String,
     * instead of printing it - for use by the GUI, which shows this
     * text in a chat bubble rather than on a console.
     *
     * @param input the raw command text, exactly as the user typed it
     * @return Leo's reply, with multiple lines (if any) separated by
     *     "\n"
     */
    public String getResponse(String input) {
        return getReply(input).getText();
    }

    /**
     * Handles a single command like {@link #getResponse(String)}, but
     * also reports whether the reply was an error message - used by
     * the GUI to display error replies differently from normal ones
     * (see {@link Reply}).
     *
     * @param input the raw command text, exactly as the user typed it
     * @return Leo's reply, together with whether it was an error
     */
    public Reply getReply(String input) {
        GuiUi guiUi = new GuiUi();
        processCommand(input, guiUi);
        return new Reply(guiUi.getCapturedText(), guiUi.isError());
    }

    /**
     * Parses and executes one command, writing whatever it needs to say
     * back to the user through {@code outputUi}. This is the one place
     * all of Leo's command-handling logic lives, shared by both
     * {@link #run()} (console) and {@link #getResponse(String)} (GUI).
     *
     * <p>A-CodeQuality: this method used to contain the full body of
     * every command (roughly 220 lines, one big if-else chain) - well
     * past the "avoid long methods (over 30 lines)" guideline from the
     * course's Code Quality chapter, and hard to read as a result: to
     * find out what "mark" does, you had to skim past unrelated code
     * for every other command first. Each command's own logic has been
     * pulled out into its own private handleXxx method below, so this
     * method's job is now just "dispatch to the right handler" - what
     * it does is visible at a glance, and each handler can be read (or
     * changed) on its own, independent of the others.
     *
     * @param input raw command text, exactly as the user typed it
     * @param outputUi where Leo's reply to this command should go
     */
    private void processCommand(String input, Ui outputUi) {
        String commandWord = Parser.getCommandWord(input);
        String arguments = Parser.getArguments(input);

        try {
            dispatch(commandWord, arguments, outputUi);
        } catch (LeoException e) {
            outputUi.showError(e.getMessage());
        } catch (IOException e) {
            outputUi.showError(
                    "I couldn't update the saved task file: " + e.getMessage()
            );
        } catch (DateTimeParseException e) {
            outputUi.showError(
                    "Please enter dates and times in the format yyyy-MM-dd HHmm."
            );
        }
    }

    /**
     * Routes a command word to the handler responsible for it - kept
     * separate from {@link #processCommand} so that method's own job
     * (running the chosen handler, then turning any exception it
     * throws into a user-facing error message) stays a single, easy
     * to follow block, instead of being interleaved with this routing
     * logic.
     *
     * @param commandWord the command name, e.g. "todo" in "todo book"
     * @param arguments text after the command word, e.g. "book" in
     *     "todo book"
     * @param outputUi where the handler's reply (or error) should go
     * @throws LeoException if the command word is not recognised, or
     *     the chosen handler rejects its arguments
     * @throws IOException if the chosen handler cannot save the task
     *     list
     */
    private void dispatch(String commandWord, String arguments, Ui outputUi)
            throws LeoException, IOException {
        if (commandWord.equals("bye")) {
            outputUi.showGoodbye();
        } else if (commandWord.equals("list")) {
            handleList(outputUi);
        } else if (commandWord.equals("mark")) {
            handleMark(arguments, outputUi);
        } else if (commandWord.equals("unmark")) {
            handleUnmark(arguments, outputUi);
        } else if (commandWord.equals("todo")) {
            handleTodo(arguments, outputUi);
        } else if (commandWord.equals("deadline")) {
            handleDeadline(arguments, outputUi);
        } else if (commandWord.equals("event")) {
            handleEvent(arguments, outputUi);
        } else if (commandWord.equals("delete")) {
            handleDelete(arguments, outputUi);
        } else if (commandWord.equals("find")) {
            handleFind(arguments, outputUi);
        } else {
            throw new LeoException(
                    "Sorry, I don't understand what you are trying to say."
            );
        }
    }

    /**
     * Handles the "list" command: shows every task currently stored.
     *
     * @param outputUi where the task list should be shown
     */
    private void handleList(Ui outputUi) {
        // A-Streams: builds every numbered line first, then shows
        // them all in one call, instead of an indexed for loop
        // calling showMessage once per line. IntStream.range keeps
        // each task paired with its 1-based display number without
        // a separate counter variable to maintain by hand.
        List<String> lines = IntStream.range(0, tasks.size())
                .mapToObj(i -> (i + 1) + ". " + tasks.get(i))
                .collect(Collectors.toList());

        outputUi.showMessage(
                "Here are the things I've saved in your to-do list so far:"
        );
        outputUi.showMessage(lines.toArray(new String[0]));
    }

    /**
     * Handles the "mark" command: marks the specified task as done.
     *
     * @param arguments text after the command word, e.g. "2" in "mark 2"
     * @param outputUi where the confirmation (or error) should be shown
     * @throws LeoException if the task number is missing or invalid
     * @throws IOException if the task list cannot be saved
     */
    private void handleMark(String arguments, Ui outputUi)
            throws LeoException, IOException {
        if (arguments.isEmpty()) {
            throw new LeoException(
                    "Please specify which task to mark, for example: mark 2"
            );
        }

        int taskIndex = parseTaskNumber(arguments, "mark") - 1;

        if (!tasks.isValidIndex(taskIndex)) {
            throw new LeoException(
                    "That task number does not exist."
            );
        }

        tasks.get(taskIndex).markAsDone();
        Storage.saveTasks(tasks.toArray(), tasks.size());

        outputUi.showMessage(
                "Nice! I've marked this task as done:",
                tasks.get(taskIndex).toString()
        );
    }

    /**
     * Handles the "unmark" command: marks the specified task as not
     * done.
     *
     * @param arguments text after the command word, e.g. "2" in
     *     "unmark 2"
     * @param outputUi where the confirmation (or error) should be shown
     * @throws LeoException if the task number is missing or invalid
     * @throws IOException if the task list cannot be saved
     */
    private void handleUnmark(String arguments, Ui outputUi)
            throws LeoException, IOException {
        if (arguments.isEmpty()) {
            throw new LeoException(
                    "Please specify which task to unmark, for example: unmark 2"
            );
        }

        int taskIndex = parseTaskNumber(arguments, "unmark") - 1;

        if (!tasks.isValidIndex(taskIndex)) {
            throw new LeoException(
                    "That task number does not exist."
            );
        }

        tasks.get(taskIndex).markAsNotDone();
        Storage.saveTasks(tasks.toArray(), tasks.size());

        outputUi.showMessage(
                "OK, I've marked this task as not done yet:",
                tasks.get(taskIndex).toString()
        );
    }

    /**
     * Handles the "todo" command: adds a new todo task.
     *
     * @param arguments text after the command word, i.e. the todo's
     *     description
     * @param outputUi where the confirmation (or error) should be shown
     * @throws LeoException if the description is empty
     * @throws IOException if the task list cannot be saved
     */
    private void handleTodo(String arguments, Ui outputUi)
            throws LeoException, IOException {
        if (arguments.isEmpty()) {
            throw new LeoException(
                    "The description of a todo cannot be empty."
            );
        }

        Todo todo = new Todo(arguments);
        boolean isDuplicate = tasks.containsSimilar(todo);
        tasks.add(todo);
        Storage.saveTasks(tasks.toArray(), tasks.size());

        showTaskAddedConfirmation(outputUi, isDuplicate);
    }

    /**
     * Handles the "deadline" command: adds a new deadline task.
     *
     * @param arguments text after the command word, in the form
     *     "DESCRIPTION /by TIME"
     * @param outputUi where the confirmation (or error) should be shown
     * @throws LeoException if the command is malformed
     * @throws IOException if the task list cannot be saved
     */
    private void handleDeadline(String arguments, Ui outputUi)
            throws LeoException, IOException {
        if (arguments.isEmpty() || !arguments.contains(" /by ")) {
            throw new LeoException(
                    "Please enter a deadline in this format: deadline DESCRIPTION /by TIME"
            );
        }

        if (Parser.hasDuplicateDelimiter(arguments, " /by ")) {
            throw new LeoException(
                    "Please use '/by' only once when entering a deadline."
            );
        }

        String[] parts = arguments.split(" /by ", 2);
        String description = parts[0].trim();
        String by = parts[1].trim();

        if (description.isEmpty()) {
            throw new LeoException(
                    "The description of a deadline cannot be empty."
            );
        }

        if (by.isEmpty()) {
            throw new LeoException(
                    "Please specify when the task is due after '/by'."
            );
        }

        Deadline deadline = new Deadline(description, by);
        boolean isDuplicate = tasks.containsSimilar(deadline);
        tasks.add(deadline);
        Storage.saveTasks(tasks.toArray(), tasks.size());

        showTaskAddedConfirmation(outputUi, isDuplicate);
    }

    /**
     * Handles the "event" command: adds a new event task.
     *
     * @param arguments text after the command word, in the form
     *     "DESCRIPTION /from START /to END"
     * @param outputUi where the confirmation (or error) should be shown
     * @throws LeoException if the command is malformed
     * @throws IOException if the task list cannot be saved
     */
    private void handleEvent(String arguments, Ui outputUi)
            throws LeoException, IOException {
        String[] parsed = parseEventArguments(arguments);
        Event event = new Event(parsed[0], parsed[1], parsed[2]);
        boolean isDuplicate = tasks.containsSimilar(event);
        tasks.add(event);
        Storage.saveTasks(tasks.toArray(), tasks.size());

        showTaskAddedConfirmation(outputUi, isDuplicate);
    }

    /**
     * Parses and validates an event command's arguments, kept
     * separate from {@link #handleEvent} so that method reads as
     * "parse, then add the task", rather than mixing every validation
     * check in with the actual task-adding logic.
     *
     * @param arguments text after the command word, in the form
     *     "DESCRIPTION /from START /to END"
     * @return an array of the parsed description, start time, and end
     *     time, in that order - each already trimmed and confirmed
     *     non-empty
     * @throws LeoException if the arguments are malformed
     */
    private String[] parseEventArguments(String arguments) throws LeoException {
        if (arguments.isEmpty() || !arguments.contains(" /from ")) {
            throw new LeoException(
                    "Please enter an event in this format: event DESCRIPTION /from START /to END"
            );
        }

        if (Parser.hasDuplicateDelimiter(arguments, " /from ")) {
            throw new LeoException(
                    "Please use '/from' only once when entering an event."
            );
        }

        String[] fromParts = arguments.split(" /from ", 2);
        String description = fromParts[0].trim();
        String eventTimes = fromParts[1].trim();

        if (!eventTimes.contains(" /to ")) {
            throw new LeoException(
                    "Please enter an event in this format: event DESCRIPTION /from START /to END"
            );
        }

        if (Parser.hasDuplicateDelimiter(eventTimes, " /to ")) {
            throw new LeoException(
                    "Please use '/to' only once when entering an event."
            );
        }

        String[] timeParts = eventTimes.split(" /to ", 2);
        String from = timeParts[0].trim();
        String to = timeParts[1].trim();

        if (description.isEmpty()) {
            throw new LeoException(
                    "The description of an event cannot be empty."
            );
        }

        if (from.isEmpty()) {
            throw new LeoException(
                    "Please specify the event's starting time after '/from'."
            );
        }

        if (to.isEmpty()) {
            throw new LeoException(
                    "Please specify the event's ending time after '/to'."
            );
        }

        return new String[] {description, from, to};
    }

    /**
     * Shows the standard "task added" confirmation for the task most
     * recently added to {@code tasks} - shared by the todo/deadline/
     * event handlers above so they don't each repeat the same
     * message-building logic (previously identical 3-line blocks in
     * all three).
     *
     * @param outputUi where the confirmation should be shown
     * @param isDuplicate whether an equivalent task was already in the
     *     list before this one was added (see
     *     {@link TaskList#containsSimilar}) - appends an extra warning
     *     line when true, without blocking the add
     */
    private void showTaskAddedConfirmation(Ui outputUi, boolean isDuplicate) {
        List<String> lines = new ArrayList<>();
        lines.add("Got it. I've added this task:");
        lines.add("  " + tasks.get(tasks.size() - 1));
        lines.add("Now you have " + tasks.size() + " tasks in the list.");

        if (isDuplicate) {
            lines.add("Note: this looks like a task you already have - I've added it anyway.");
        }

        lines.add(pickFlourish());

        outputUi.showMessage(lines.toArray(new String[0]));
    }

    /**
     * Picks a friendly closing line at random, shown after a task is
     * successfully added (see {@link #TASK_ADDED_FLOURISHES}) - purely
     * decorative, so which one shows on any given call is unimportant.
     *
     * @return a randomly-chosen flourish line
     */
    private String pickFlourish() {
        int index = (int) (Math.random() * TASK_ADDED_FLOURISHES.length);
        return TASK_ADDED_FLOURISHES[index];
    }

    /**
     * Handles the "delete" command: removes the specified task.
     *
     * @param arguments text after the command word, e.g. "2" in
     *     "delete 2"
     * @param outputUi where the confirmation (or error) should be shown
     * @throws LeoException if the task number is missing or invalid
     * @throws IOException if the task list cannot be saved
     */
    private void handleDelete(String arguments, Ui outputUi)
            throws LeoException, IOException {
        if (arguments.isEmpty()) {
            throw new LeoException(
                    "Please specify which task to delete, for example: delete 2"
            );
        }

        int taskIndex = parseTaskNumber(arguments, "delete") - 1;

        if (!tasks.isValidIndex(taskIndex)) {
            throw new LeoException(
                    "That task number does not exist."
            );
        }

        Task deletedTask = tasks.remove(taskIndex);
        Storage.saveTasks(tasks.toArray(), tasks.size());

        outputUi.showMessage(
                "Okay, noted. I've removed this task:",
                "  " + deletedTask,
                "Now you have " + tasks.size() + " tasks in the list."
        );
    }

    /**
     * Handles the "find" command: shows tasks whose description
     * contains the given keyword.
     *
     * @param arguments text after the command word, i.e. the keyword
     * @param outputUi where the matching tasks (or error) should be
     *     shown
     * @throws LeoException if the keyword is empty
     */
    private void handleFind(String arguments, Ui outputUi)
            throws LeoException {
        if (arguments.isEmpty()) {
            throw new LeoException(
                    "Please specify a keyword to search for, for example: find book"
            );
        }

        List<Task> matches = tasks.find(arguments);

        if (matches.isEmpty()) {
            outputUi.showMessage(
                    "I couldn't find any matching tasks in your list."
            );
        } else {
            // A-Streams: same approach as handleList above - number
            // each match via IntStream.range, then show every line
            // in one call.
            List<String> matchLines = IntStream.range(0, matches.size())
                    .mapToObj(i -> (i + 1) + ". " + matches.get(i))
                    .collect(Collectors.toList());

            outputUi.showMessage("Here are the matching tasks in your list:");
            outputUi.showMessage(matchLines.toArray(new String[0]));
        }
    }

    /**
     * Converts a task-number argument (e.g. the "2" in "mark 2") into
     * a positive integer.
     *
     * @param numberText the raw argument text to parse
     * @param command name of the command this argument belongs to,
     *     used only to build a helpful error message
     * @return the parsed task number
     * @throws LeoException if numberText is not a valid positive
     *     integer
     */
    public static int parseTaskNumber(String numberText, String command)
            throws LeoException {
        try {
            int taskNumber = Integer.parseInt(numberText.trim());

            if (taskNumber <= 0) {
                throw new LeoException(
                        "The task number must be greater than zero."
                );
            }

            // A-Assertions: documents this method's own postcondition
            // (stated in its Javadoc @return) for whoever calls it next
            // - by this point the only way out is the checked-above
            // exception or this return, so taskNumber > 0 always holds
            // here; this isn't re-validating anything, just naming the
            // guarantee explicitly.
            assert taskNumber > 0 : "a returned task number should always be positive";

            return taskNumber;

        } catch (NumberFormatException e) {
            throw new LeoException(
                    "Please enter a valid task number, for example: "
                            + command + " 1"
            );
        }
    }

    public static void main(String[] args) {
        new Leo().run();
    }
}
