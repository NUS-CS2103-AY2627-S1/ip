package jeryl;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import jeryl.exception.JerylException;
import jeryl.parser.Command;
import jeryl.parser.Parser;
import jeryl.storage.Storage;
import jeryl.task.Deadline;
import jeryl.task.Event;
import jeryl.task.Priority;
import jeryl.task.Task;
import jeryl.task.Todo;
import jeryl.ui.Ui;

/**
 * The Jeryl chatbot's core logic: given one line of user input, updates
 * the task list (and saves it to disk) and returns the response text.
 * Used by both the CLI loop in main() and the JavaFX GUI, so the two
 * front ends stay behaviorally identical.
 */
public class Jeryl {
    private static final String DATA_FILE_PATH = "./data/jeryl.txt";

    private final Storage storage;
    private final Ui ui;
    private final TaskList tasks;

    /**
     * Creates a Jeryl instance backed by the default save file location.
     */
    public Jeryl() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates a Jeryl instance backed by the given save file location,
     * loading any tasks already saved there.
     */
    public Jeryl(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.tasks = new TaskList(storage.load());
    }

    /**
     * Runs Jeryl's CLI read-parse-execute loop until the user says "bye".
     */
    public static void main(String[] args) {
        new Jeryl().runCli();
    }

    private void runCli() {
        System.out.println(ui.welcomeMessage());
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            System.out.println(getResponse(input));
            if (isExit(input)) {
                break;
            }
        }
        ui.close();
    }

    /**
     * Returns true if the given raw input is the "bye" command.
     */
    public boolean isExit(String input) {
        return Parser.parse(input).command() == Command.BYE;
    }

    /**
     * Returns Jeryl's welcome/greeting message, shown once at startup.
     */
    public String welcomeMessage() {
        return ui.welcomeMessage();
    }

    /**
     * Jeryl's reply to one line of user input: the text to show, and
     * whether it's an error message (so the GUI can style it
     * differently) rather than a normal response.
     */
    public record Response(String text, boolean isError) {
    }

    /**
     * Processes one line of raw user input and returns Jeryl's response
     * text, updating the task list (and persisting it to disk) as a
     * side effect where applicable. Any JerylException raised while
     * handling the command is caught and its message returned as the
     * response, rather than propagated.
     */
    public String getResponse(String input) {
        return respond(input).text();
    }

    /**
     * Like {@link #getResponse}, but also reports whether the reply is
     * an error message.
     */
    public Response respond(String input) {
        Parser.ParsedInput parsed = Parser.parse(input);
        Command command = parsed.command();
        String args = parsed.arguments();

        try {
            switch (command) {
                case LIST:
                    return new Response(ui.taskListMessage(tasks), false);
                case MARK:
                    return new Response(withSave(markTask(args)), false);
                case UNMARK:
                    return new Response(withSave(unmarkTask(args)), false);
                case DELETE:
                    return new Response(withSave(deleteTask(args)), false);
                case TODO:
                    return new Response(withSave(addTodo(args)), false);
                case DEADLINE:
                    return new Response(withSave(addDeadline(args)), false);
                case EVENT:
                    return new Response(withSave(addEvent(args)), false);
                case FIND:
                    return new Response(findTasks(args), false);
                case BYE:
                    return new Response(ui.goodbyeMessage(), false);
                default:
                    throw new JerylException(
                            "Wow. I genuinely don't know what that means, and I've seen some things.");
            }
        } catch (JerylException e) {
            return new Response(e.getMessage(), true);
        }
    }

    private String withSave(String response) {
        storage.save(tasks.asArrayList());
        return response;
    }

    private String markTask(String args) throws JerylException {
        int index = parseTaskIndex(args, "mark", tasks.size());
        tasks.get(index).markAsDone();
        return ui.markedMessage(tasks.get(index));
    }

    private String unmarkTask(String args) throws JerylException {
        int index = parseTaskIndex(args, "unmark", tasks.size());
        tasks.get(index).markAsNotDone();
        return ui.unmarkedMessage(tasks.get(index));
    }

    private String deleteTask(String args) throws JerylException {
        int index = parseTaskIndex(args, "delete", tasks.size());
        Task removed = tasks.delete(index);
        return ui.removedMessage(removed, tasks.size());
    }

    private String addTodo(String args) throws JerylException {
        ArgsAndPriority parsed = extractPriority(args);
        String description = parsed.args().trim();
        if (description.isEmpty()) {
            throw new JerylException("A todo with no description. Bold strategy. Try adding actual words.");
        }
        tasks.add(new Todo(description, parsed.priority()));
        return ui.addedMessage(tasks.get(tasks.size() - 1), tasks.size());
    }

    private String addDeadline(String args) throws JerylException {
        ArgsAndPriority parsed = extractPriority(args);
        String remaining = parsed.args();
        requireNoDuplicateFlag(remaining, "/by ");
        int byIndex = remaining.indexOf("/by ");
        if (byIndex == -1) {
            throw new JerylException(
                    "A deadline without \"/by <when>\" is just a todo wearing a costume. Add one.");
        }
        String description = remaining.substring(0, byIndex).trim();
        String by = remaining.substring(byIndex + 4).trim();
        if (description.isEmpty()) {
            throw new JerylException("A nameless deadline. Spooky, but not useful. Give it a description.");
        }
        if (by.isEmpty()) {
            throw new JerylException("You wrote \"/by\" and then just... stopped. When is it due?");
        }
        tasks.add(new Deadline(description, parseDate(by), parsed.priority()));
        return ui.addedMessage(tasks.get(tasks.size() - 1), tasks.size());
    }

    private String addEvent(String args) throws JerylException {
        ArgsAndPriority parsed = extractPriority(args);
        String remaining = parsed.args();
        requireNoDuplicateFlag(remaining, "/from ");
        requireNoDuplicateFlag(remaining, "/to ");
        int fromIndex = remaining.indexOf("/from ");
        int toIndex = remaining.indexOf("/to ");
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new JerylException(
                    "An event needs \"/from <start>\" and \"/to <end>\", in that order. "
                            + "Events don't end before they begin, even for you.");
        }
        String description = remaining.substring(0, fromIndex).trim();
        String from = remaining.substring(fromIndex + 6, toIndex).trim();
        String to = remaining.substring(toIndex + 4).trim();
        if (description.isEmpty()) {
            throw new JerylException("An event with no description. Very mysterious. Also unhelpful.");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new JerylException("The \"/from\" and \"/to\" fields are empty. I can't schedule a void.");
        }
        LocalDate fromDate = parseDate(from);
        LocalDate toDate = parseDate(to);
        if (!toDate.isAfter(fromDate)) {
            throw new JerylException(
                    "An event's \"/to\" date must be after its \"/from\" date. Time only moves one way, sorry.");
        }
        tasks.add(new Event(description, fromDate, toDate, parsed.priority()));
        return ui.addedMessage(tasks.get(tasks.size() - 1), tasks.size());
    }

    /**
     * Rejects argument text that repeats the given flag (e.g. "/by ") more
     * than once, since only the first occurrence would otherwise be used
     * and the rest silently swallowed into the description.
     */
    private static void requireNoDuplicateFlag(String args, String flag) throws JerylException {
        int firstIndex = args.indexOf(flag);
        if (firstIndex != -1 && args.indexOf(flag, firstIndex + flag.length()) != -1) {
            throw new JerylException(
                    "You typed \"" + flag.trim() + "\" more than once. I'll need that only once, thanks.");
        }
    }

    /**
     * A command's argument text with any trailing "/priority <level>" flag
     * pulled out, along with the Priority it specified (NONE if the flag
     * wasn't present).
     */
    private record ArgsAndPriority(String args, Priority priority) {
    }

    /**
     * Extracts an optional "/priority <level>" flag from a command's raw
     * argument text, wherever it appears. Returns the argument text with
     * the flag removed (so existing "/by"/"/from"/"/to" parsing is
     * unaffected) and the Priority it specified, or Priority.NONE if the
     * flag wasn't present.
     */
    private static ArgsAndPriority extractPriority(String args) throws JerylException {
        int priorityIndex = args.indexOf("/priority");
        if (priorityIndex == -1) {
            return new ArgsAndPriority(args, Priority.NONE);
        }
        if (args.indexOf("/priority", priorityIndex + "/priority".length()) != -1) {
            throw new JerylException("\"/priority\" only once, please. It's a priority flag, not a chant.");
        }
        String before = args.substring(0, priorityIndex);
        String level = args.substring(priorityIndex + "/priority".length()).trim();
        if (level.isEmpty()) {
            throw new JerylException(
                    "The \"/priority\" flag needs a level: high, medium, or low. Guessing isn't my job.");
        }
        return new ArgsAndPriority(before.trim(), Priority.fromKeyword(level));
    }

    private String findTasks(String args) throws JerylException {
        String keyword = args.trim();
        if (keyword.isEmpty()) {
            throw new JerylException("Find... what, exactly? I can't read minds, only task lists.");
        }
        return ui.matchingTasksMessage(tasks.find(keyword));
    }

    /**
     * Parses a date string in yyyy-MM-dd format (e.g. 2019-10-15), as
     * used by both the "/by" field of a deadline and the "/from"/"/to"
     * fields of an event.
     */
    private static LocalDate parseDate(String dateString) throws JerylException {
        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            throw new JerylException(
                    "\"" + dateString + "\" is not a date I recognize. Try yyyy-mm-dd, e.g. 2019-10-15.");
        }
    }

    /**
     * Parses the 1-based task index that follows a "mark"/"unmark"/"delete"
     * command and validates it against the current task count.
     */
    private static int parseTaskIndex(String args, String commandWord, int taskCount) throws JerylException {
        String indexString = args.trim();
        if (indexString.isEmpty()) {
            throw new JerylException("Which task number, exactly? I don't do " + commandWord + " by vibes.");
        }
        int index;
        try {
            index = Integer.parseInt(indexString) - 1;
        } catch (NumberFormatException e) {
            throw new JerylException("The task number must be a whole number. Not whatever that was.");
        }
        if (index < 0 || index >= taskCount) {
            throw new JerylException("There is no task with that number. I checked. Twice.");
        }
        return index;
    }
}
