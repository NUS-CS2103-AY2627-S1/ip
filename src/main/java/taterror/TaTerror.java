package taterror;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import taterror.parser.Parser;
import taterror.storage.Storage;
import taterror.task.Deadline;
import taterror.task.Event;
import taterror.task.Priority;
import taterror.task.Task;
import taterror.task.TaskList;
import taterror.task.Todo;
import taterror.ui.Ui;

/**
 * Core logic of the "TA Terror" task-tracking chatbot: a sarcastic Duke-clone that
 * parses user commands (todo/deadline/event/list/mark/unmark/delete/find/bye),
 * mutates a {@link TaskList}, and persists it via {@link Storage}.
 *
 * <p>This class is UI-agnostic - both the text CLI ({@link #main(String[])}) and the
 * JavaFX GUI ({@link taterror.gui.MainWindow}) drive it purely through
 * {@link #getResponse(String)}. Command recognition/extraction is delegated to
 * {@link Parser}.
 */
public class TaTerror {
    /**
     * Farewell lines for {@code bye}, picked at random so repeat sessions don't
     * see the exact same sign-off every time.
     */
    static final List<String> BYE_RESPONSES = List.of(
            "Bye. Try to disappoint someone else next time.",
            "Bye. Go be someone else's problem now.",
            "Bye. Don't let the door hit you on the way out.",
            "Bye. I wasn't enjoying this either.");

    /**
     * Error lines for an unrecognized command, picked at random for the same
     * reason as {@link #BYE_RESPONSES}.
     */
    static final List<String> UNKNOWN_COMMAND_RESPONSES = List.of(
            "OOPS!!! I have no idea what you just said. Try again, slower this time.",
            "OOPS!!! That's not a command. That's just noise.",
            "OOPS!!! Yeah, no. Try that again, but make sense this time.",
            "OOPS!!! I don't do guesswork. Type something real.");

    private static final String DATA_FILE_PATH = "." + File.separator + "data" + File.separator + "tasks.txt";

    private final Storage storage;
    private final TaskList tasks;
    private final Random random;

    /**
     * Creates a new TA Terror instance, loading any previously saved tasks from
     * the save file (starting with an empty list if none exists).
     */
    public TaTerror() {
        storage = new Storage(DATA_FILE_PATH);
        tasks = new TaskList(storage.load());
        random = new Random();
    }

    /**
     * Parses one line of user input, applies its effect to the task list (if any),
     * and returns the chatbot's reply text.
     *
     * <p>Supported commands: {@code bye}, {@code list}, {@code mark <n>},
     * {@code unmark <n>}, {@code delete <n>}, {@code todo <description>},
     * {@code deadline <description> /by <date>},
     * {@code event <description> /from <start> /to <end>},
     * {@code find <keyword>}, and {@code priority <n> <level>}. The
     * {@code todo}/{@code deadline}/{@code event} commands also accept an
     * optional trailing {@code /priority <level>} flag. A level is one of
     * {@code none}, {@code low}, {@code medium}, or {@code high}
     * (case-insensitive). Anything else, or a malformed index for
     * mark/unmark/delete/priority, produces an error reply rather than
     * throwing.
     *
     * <p>Leading/trailing whitespace around the command is ignored.
     *
     * @param rawInput the raw command line typed by the user
     * @return the chatbot's reply, ready to display as-is
     */
    public String getResponse(String rawInput) {
        String input = rawInput.trim();
        try {
            switch (Parser.parseCommandType(input)) {
                case BYE:
                    return pickRandom(BYE_RESPONSES);
                case LIST:
                    return handleList();
                case MARK:
                case UNMARK:
                    return handleMarkOrUnmark(input);
                case DELETE:
                    return handleDelete(input);
                case TODO:
                    return handleTodo(input);
                case DEADLINE:
                    return handleDeadline(input);
                case EVENT:
                    return handleEvent(input);
                case FIND:
                    return handleFind(input);
                case PRIORITY:
                    return handlePriority(input);
                case UNKNOWN:
                default:
                    return pickRandom(UNKNOWN_COMMAND_RESPONSES);
            }
        } catch (NumberFormatException e) {
            return "OOPS!!! That's not even a number. Are you okay?";
        }
    }

    /**
     * Returns whether {@code rawInput} is the {@code bye} command (ignoring
     * surrounding whitespace), so a front end knows when to end the session.
     */
    public boolean isExitCommand(String rawInput) {
        return Parser.isByeCommand(rawInput.trim());
    }

    /**
     * Handles the {@code list} command: every task, numbered from 1.
     */
    private String handleList() {
        return "Here are the tasks in your list:\n" + renderNumbered(tasks.asList());
    }

    /**
     * Handles a {@code mark}/{@code unmark} command: flips the target task's
     * done state and persists the change.
     */
    private String handleMarkOrUnmark(String input) {
        boolean marking = Parser.isMarkCommand(input);
        int markIndex = Parser.parseTaskIndex(input, marking ? "mark" : "unmark");
        if (!tasks.isValidIndex(markIndex)) {
            return "OOPS!!! That task number doesn't even exist. Try again.";
        }
        Task task = tasks.get(markIndex);
        if (marking) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        storage.save(tasks.asList());
        String verb = marking
                ? "Nice! I've marked this task as done:\n"
                : "OK, I've marked this task as not done yet:\n";
        return verb + "  " + task;
    }

    /**
     * Handles a {@code delete} command: removes the target task and persists
     * the change.
     */
    private String handleDelete(String input) {
        int deleteIndex = Parser.parseTaskIndex(input, "delete");
        if (!tasks.isValidIndex(deleteIndex)) {
            return "OOPS!!! That task number doesn't even exist. Try again.";
        }
        Task removed = tasks.remove(deleteIndex);
        storage.save(tasks.asList());
        return "Noted. I've removed this task:\n  " + removed
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Handles a {@code todo} command: adds the task if a description was
     * given, and persists the change.
     */
    private String handleTodo(String input) {
        String[] priorityResult = Parser.extractPriorityFlag(Parser.parseArguments(input, "todo"));
        String description = priorityResult[0].trim();
        if (description.isEmpty()) {
            return "OOPS!!! A todo needs an actual description. Use your words.";
        }
        Priority priority = parsePriorityOrNone(priorityResult[1]);
        if (priority == null) {
            return "OOPS!!! That's not a real priority. Try none, low, medium, or high.";
        }
        Task todo = new Todo(description);
        todo.setPriority(priority);
        boolean isDuplicate = tasks.hasDuplicateOf(todo);
        tasks.add(todo);
        storage.save(tasks.asList());
        return addTaskMessage(todo, isDuplicate);
    }

    /**
     * Handles a {@code deadline} command: adds the task if it has both a
     * description and a {@code /by} date, and persists the change.
     */
    private String handleDeadline(String input) {
        String[] priorityResult = Parser.extractPriorityFlag(Parser.parseArguments(input, "deadline"));
        String[] deadlineParts = Parser.splitDeadlineArgs(priorityResult[0]);
        if (deadlineParts == null) {
            return "OOPS!!! A deadline needs a description AND a '/by' date (e.g. 2019-10-15).";
        }
        assert deadlineParts.length >= 2 : "splitDeadlineArgs guarantees at least [description, by]";
        Priority priority = parsePriorityOrNone(priorityResult[1]);
        if (priority == null) {
            return "OOPS!!! That's not a real priority. Try none, low, medium, or high.";
        }
        Task deadline = new Deadline(deadlineParts[0], deadlineParts[1]);
        deadline.setPriority(priority);
        boolean isDuplicate = tasks.hasDuplicateOf(deadline);
        tasks.add(deadline);
        storage.save(tasks.asList());
        return addTaskMessage(deadline, isDuplicate);
    }

    /**
     * Handles an {@code event} command: adds the task if it has a
     * description, a {@code /from}, and a {@code /to}, and persists the
     * change.
     */
    private String handleEvent(String input) {
        String[] priorityResult = Parser.extractPriorityFlag(Parser.parseArguments(input, "event"));
        String[] eventParts = Parser.splitEventArgs(priorityResult[0]);
        if (eventParts == null) {
            return "OOPS!!! An event needs '/from' and '/to' details. Don't skip steps.";
        }
        assert eventParts.length == 3 : "splitEventArgs guarantees [description, from, to]";
        Priority priority = parsePriorityOrNone(priorityResult[1]);
        if (priority == null) {
            return "OOPS!!! That's not a real priority. Try none, low, medium, or high.";
        }
        Task event = new Event(eventParts[0], eventParts[1], eventParts[2]);
        event.setPriority(priority);
        boolean isDuplicate = tasks.hasDuplicateOf(event);
        tasks.add(event);
        storage.save(tasks.asList());
        return addTaskMessage(event, isDuplicate);
    }

    /**
     * Handles a {@code priority} command: updates an existing task's priority
     * level and persists the change.
     */
    private String handlePriority(String input) {
        String[] parts = Parser.parseArguments(input, "priority").split(" ", 2);
        if (parts.length < 2 || parts[0].isEmpty() || parts[1].isBlank()) {
            return "OOPS!!! Usage: priority <task number> <none|low|medium|high>.";
        }
        int index = Integer.parseInt(parts[0]) - 1;
        if (!tasks.isValidIndex(index)) {
            return "OOPS!!! That task number doesn't even exist. Try again.";
        }
        Priority priority = Priority.fromString(parts[1].trim());
        if (priority == null) {
            return "OOPS!!! That's not a real priority. Try none, low, medium, or high.";
        }
        Task task = tasks.get(index);
        task.setPriority(priority);
        storage.save(tasks.asList());
        return "Fine, priority updated:\n  " + task;
    }

    /**
     * Parses an optional {@code /priority} flag's raw text, e.g. from
     * {@link Parser#extractPriorityFlag}: {@code null} (flag absent) becomes
     * {@link Priority#NONE}, and anything else is parsed via
     * {@link Priority#fromString}.
     *
     * @return the resulting priority, or {@code null} if the flag's text
     *         wasn't a real priority level
     */
    private Priority parsePriorityOrNone(String rawLevel) {
        return rawLevel == null ? Priority.NONE : Priority.fromString(rawLevel);
    }

    /**
     * Handles a {@code find} command: every task whose description contains
     * the given keyword, numbered from 1.
     */
    private String handleFind(String input) {
        String keyword = Parser.parseArguments(input, "find").trim();
        if (keyword.isEmpty()) {
            return "OOPS!!! Find what, exactly? Give me a keyword.";
        }
        List<Task> matches = tasks.findByKeyword(keyword);
        if (matches.isEmpty()) {
            return "Here are the matching tasks in your list:\nNo matches. Shocking, I know.";
        }
        return "Here are the matching tasks in your list:\n" + renderNumbered(matches);
    }

    /**
     * Runs TA Terror as a text-based CLI: prints the greeting banner, then reads
     * commands from standard input and prints each reply until {@code bye}.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        TaTerror taTerror = new TaTerror();
        Ui ui = new Ui();
        ui.showGreeting();

        String input = ui.readCommand();
        while (!taTerror.isExitCommand(input)) {
            ui.showResponse(taTerror.getResponse(input));
            input = ui.readCommand();
        }
        ui.showResponse(taTerror.getResponse("bye"));
        ui.close();
    }

    /**
     * Renders {@code taskList} as newline-separated, 1-indexed lines (e.g.
     * {@code "1.[T][ ] read book"}), the way both {@code list} and
     * {@code find} display their results.
     */
    private String renderNumbered(List<Task> taskList) {
        return IntStream.range(0, taskList.size())
                .mapToObj(i -> (i + 1) + "." + taskList.get(i))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Returns a uniformly random element of {@code responses}, e.g. one of
     * {@link #BYE_RESPONSES} or {@link #UNKNOWN_COMMAND_RESPONSES}.
     */
    private String pickRandom(List<String> responses) {
        return responses.get(random.nextInt(responses.size()));
    }

    /**
     * Builds the standard "task added" confirmation message for {@code task},
     * including the updated task count and, if {@code isDuplicate} is true, a
     * heads-up that an identical task was already on the list.
     */
    private String addTaskMessage(Task task, boolean isDuplicate) {
        String duplicateWarning = isDuplicate
                ? "Heads up, that looks identical to a task you already added.\n"
                : "";
        return duplicateWarning + "Got it. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }
}
