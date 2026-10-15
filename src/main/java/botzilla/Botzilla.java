package botzilla;

import java.time.LocalDate;

import botzilla.parser.Parser;
import botzilla.storage.Storage;
import botzilla.task.Task;
import botzilla.task.TaskList;
import botzilla.ui.Ui;

/**
 * Entry point for the Botzilla chatbot application.
 * Wires together the Ui, Storage, Parser, and TaskList components
 * and runs the main command loop.
 */
public class Botzilla {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    // Set during construction if some saved data couldn't be loaded (e.g.
    // corrupted lines in the save file), so it can be surfaced to the user
    // alongside the greeting — the constructor runs before either the
    // console loop or the GUI window exists, so it can't display it directly.
    private String startupWarning;

    /**
     * Constructs a Botzilla instance, loading any previously saved tasks
     * from the given file path. If loading fails entirely, starts with an
     * empty task list and notifies the user via the Ui. If loading
     * succeeds but some lines were corrupted and skipped, keeps the
     * successfully loaded tasks and remembers a warning to show once the
     * console/GUI is ready to display it (see {@link #getGreeting()} and
     * {@link #run()}).
     *
     * @param filePath Path to the file used for loading and saving tasks.
     */
    public Botzilla(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
            int skipped = storage.getSkippedLineCount();
            if (skipped > 0) {
                startupWarning = "Heads up: " + skipped + " line" + (skipped == 1 ? "" : "s")
                        + " in your saved tasks file " + (skipped == 1 ? "was" : "were")
                        + " corrupted, so " + (skipped == 1 ? "it was" : "they were") + " skipped.";
            }
        } catch (BotzillaException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    /**
     * Runs the main command loop, reading user input and dispatching it
     * to {@link #executeCommand} until the user issues "bye".
     */
    public void run() {
        ui.showWelcome();
        if (startupWarning != null) {
            System.out.println(ui.formatError(startupWarning));
            ui.showLine();
        }

        Parser.CommandType type;
        do {
            String input = ui.readCommand().trim();
            type = Parser.parseCommandType(input);

            try {
                System.out.println(executeCommand(type, input));
            } catch (BotzillaException e) {
                System.out.println(ui.formatError(e.getMessage()));
            }
            ui.showLine();
        } while (type != Parser.CommandType.BYE);

        ui.close();
    }

    /**
     * Returns Botzilla's opening greeting, including a warning about any
     * corrupted save-file lines that were skipped on startup, if any. Used
     * by the GUI to display an initial message when the window first
     * opens, mirroring the welcome banner shown at the start of the
     * console loop.
     *
     * @return Botzilla's greeting message.
     */
    public String getGreeting() {
        String greeting = ui.formatGreeting();
        return startupWarning == null ? greeting : greeting + "\n" + startupWarning;
    }

    /**
     * Returns whether the given input is Botzilla's exit command. Used by
     * the GUI to know when to close the window after showing the goodbye
     * message.
     *
     * @param input Raw user input.
     * @return True if the input is the "bye" command.
     */
    public boolean isExit(String input) {
        return Parser.parseCommandType(input.trim()) == Parser.CommandType.BYE;
    }

    /**
     * Generates Botzilla's response to a single line of user input. Used by
     * the GUI, which calls this once per message instead of running the
     * console read-eval-print loop.
     *
     * @param input Raw user input.
     * @return Botzilla's reply, ready to display.
     */
    public String getResponse(String input) {
        String trimmed = input.trim();
        Parser.CommandType type = Parser.parseCommandType(trimmed);
        try {
            return executeCommand(type, trimmed);
        } catch (BotzillaException e) {
            return ui.formatError(e.getMessage());
        }
    }

    /**
     * Executes a single parsed command against the current task list,
     * saving to disk as needed, and returns the response text to display.
     *
     * @param type Kind of command to execute.
     * @param input Raw user input, used to extract command arguments.
     * @return The response text describing the result of the command.
     * @throws BotzillaException If the command's arguments are invalid.
     */
    private String executeCommand(Parser.CommandType type, String input) throws BotzillaException {
        switch (type) {
            case BYE:
                return ui.formatGoodbye();
            case LIST:
                return ui.formatList(tasks);
            case MARK:
                return markTask(input, Parser.markKeywordLength(), true);
            case UNMARK:
                return markTask(input, Parser.unmarkKeywordLength(), false);
            case DELETE: {
                int deleteKeywordLength = Parser.deleteKeywordLength();
                String numberText = input.length() > deleteKeywordLength
                        ? input.substring(deleteKeywordLength).trim() : "";
                int index = Parser.parseTaskNumber(numberText, tasks.size());
                Task removed = tasks.remove(index);
                return ui.formatTaskDeleted(removed, tasks.size()) + trySave();
            }
            case ON: {
                LocalDate targetDate = Parser.parseOnDate(input);
                return ui.formatOnDate(targetDate, tasks.getTasksOnDate(targetDate));
            }
            case TODO:
                return addTask(Parser.parseTodo(input));
            case DEADLINE:
                return addTask(Parser.parseDeadline(input));
            case EVENT:
                return addTask(Parser.parseEvent(input));
            case FIND: {
                String keyword = Parser.parseFindKeyword(input);
                return ui.formatFindResults(tasks.findTasks(keyword));
            }
            case TAG:
                return tagTask(input, Parser.tagKeywordLength(), true);
            case UNTAG:
                return tagTask(input, Parser.untagKeywordLength(), false);
            default:
                throw new BotzillaException("Sorry bestie I don't know what that means :(");
        }
    }

    /**
     * Adds a newly parsed task to the list, persists the updated list to
     * disk, and returns the confirmation message to display. Shared by the
     * TODO, DEADLINE, and EVENT commands, which differ only in how the
     * task itself is parsed.
     *
     * @param task the task to add.
     * @return the formatted confirmation message.
     * @throws BotzillaException if an equivalent task (same type, name, and
     *                           date(s)) is already in the list.
     */
    private String addTask(Task task) throws BotzillaException {
        if (tasks.hasDuplicate(task)) {
            throw new BotzillaException("You've already got \"" + task.getName()
                    + "\" on your list with the same details — no need to add it twice!");
        }
        tasks.add(task);
        return ui.formatTaskAdded(task, tasks.size()) + trySave();
    }

    /**
     * Marks or unmarks the task referenced by a "mark"/"unmark" command,
     * persists the updated list to disk, and returns the confirmation
     * message to display. Shared by the MARK and UNMARK commands, which
     * differ only in their keyword length and the direction of the mark.
     *
     * @param input         raw user input, starting with the "mark "/"unmark " keyword.
     * @param keywordLength length of the leading keyword, to strip before parsing the task number.
     * @param markAsDone    true to mark the task as done, false to unmark it.
     * @return the formatted confirmation message.
     * @throws BotzillaException if the task number is missing, invalid, or out of range.
     */
    private String markTask(String input, int keywordLength, boolean markAsDone) throws BotzillaException {
        String numberText = input.length() > keywordLength ? input.substring(keywordLength).trim() : "";
        int index = Parser.parseTaskNumber(numberText, tasks.size());
        Task task = tasks.get(index);
        String result = ui.formatMarkResult(markAsDone ? task.mark() : task.unmark());
        return result + trySave();
    }

    /**
     * Adds or removes tag(s) on the task referenced by a "tag"/"untag"
     * command, persists the updated list to disk, and returns the
     * confirmation message(s) to display. Shared by the TAG and UNTAG
     * commands, which differ only in their keyword length and whether
     * tags are added or removed.
     *
     * @param input         raw user input, starting with the "tag "/"untag " keyword.
     * @param keywordLength length of the leading keyword, to strip before parsing.
     * @param addTags       true to add the given tags, false to remove them.
     * @return the formatted confirmation message(s).
     * @throws BotzillaException if the task number or tag name(s) are missing or invalid.
     */
    private String tagTask(String input, int keywordLength, boolean addTags) throws BotzillaException {
        Parser.TagCommand command = Parser.parseTagCommand(input, keywordLength, tasks.size());
        Task task = tasks.get(command.index());
        StringBuilder messages = new StringBuilder();
        for (String tagName : command.tagNames()) {
            String message = addTags ? task.addTag(tagName) : task.removeTag(tagName);
            if (messages.length() > 0) {
                messages.append("\n");
            }
            messages.append(message);
        }
        return ui.formatTagResult(messages.toString()) + trySave();
    }

    /**
     * Persists the current task list to disk, catching and reporting any
     * failure instead of letting it interrupt the command that triggered
     * it (the in-memory change has already been made either way). Console
     * and GUI users alike would otherwise have no way to know a save
     * silently failed.
     *
     * @return a warning suffix to append to the calling command's
     *         confirmation message if the save failed, or an empty string
     *         if it succeeded.
     */
    private String trySave() {
        try {
            storage.save(tasks.getAll());
            return "";
        } catch (BotzillaException e) {
            return "\n" + ui.formatError("I couldn't save your tasks to disk (" + e.getMessage()
                    + "). This change will be lost if you close the app!");
        }
    }

    /**
     * Starts the Botzilla application, using "./data/botzilla.txt" as the
     * default save file location.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        new Botzilla("./data/botzilla.txt").run();
    }
}
