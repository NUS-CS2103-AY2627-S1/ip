package amigobot;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import amigobot.command.Command;
import amigobot.task.Deadline;
import amigobot.task.Event;
import amigobot.task.Task;
import amigobot.task.TaskList;
import amigobot.task.Todo;

/**
 * Main class for the AmigoBot chatbot application.
 * Processes user commands and manages the task list.
 */
public class AmigoBot {

    private static final String[] ADDED_PHRASES = {
        "Got it, compadre! I've added this task:",
        "Consider it noted, amigo! I've added this task:",
        "No problemo! I've added this task:",
        "Anotado! I've added this task:"
    };
    private static final String[] MARKED_PHRASES = {
        "Muy bien! I've marked this task as done:",
        "Excelente, amigo! I've marked this task as done:",
        "Fantastico! I've marked this task as done:"
    };
    private static final String[] UNMARKED_PHRASES = {
        "No worries, compadre! I've marked this task as not done yet:",
        "OK amigo, I've marked this task as not done yet:",
        "Entendido! I've marked this task as not done yet:"
    };
    private static final String[] DELETED_PHRASES = {
        "Noted, compadre! I've removed this task:",
        "Gone like the wind, amigo! I've removed this task:",
        "Hasta la vista! I've removed this task:"
    };
    private static final String[] BYE_PHRASES = {
        "Adios amigo! Hope to see you again soon!",
        "Hasta la vista, compadre! Come back anytime!",
        "Chao amigo! It was nice chatting with you!"
    };

    private final Random random = new Random();
    private final Storage storage;
    private TaskList tasks;

    /**
     * Constructs an AmigoBot that loads tasks from the default data file.
     */
    public AmigoBot() {
        this(java.nio.file.Paths.get("data", "amigobot.txt").toString());
    }

    /**
     * Constructs an AmigoBot that loads tasks from the given file path.
     *
     * @param filePath the path to the task data file.
     */
    AmigoBot(String filePath) {
        this.storage = new Storage(filePath);
        try {
            this.tasks = new TaskList(storage.load());
        } catch (IOException e) {
            this.tasks = new TaskList();
        }
    }

    /**
     * Processes the given user input and returns the response string.
     *
     * @param input the raw user input.
     * @return the response to display.
     */
    public String getResponse(String input) {
        try {
            String trimmed = input.trim();
            if (trimmed.isEmpty()) {
                throw new AmigoBotException("Ay caramba! Please enter a command, compadre.");
            }
            String[] words = trimmed.split("\\s+", 2);
            String commandWord = words[0].toUpperCase();
            String arguments = words.length > 1 ? words[1].trim() : "";

            Command command;
            try {
                command = Command.valueOf(commandWord);
            } catch (IllegalArgumentException e) {
                command = Command.UNKNOWN;
            }

            return executeCommand(command, arguments);
        } catch (AmigoBotException e) {
            return e.getMessage();
        } catch (IOException e) {
            return "Ay caramba! Could not save tasks: " + e.getMessage();
        }
    }

    /**
     * Executes the given command with the provided arguments and returns the response.
     */
    private String executeCommand(Command command, String arguments)
            throws AmigoBotException, IOException {
        switch (command) {
            case BYE:
                return randomPhrase(BYE_PHRASES);
            case LIST:
                return formatTaskList();
            case DELETE:
                return handleDelete(arguments);
            case MARK:
                return handleMark(arguments);
            case UNMARK:
                return handleUnmark(arguments);
            case TODO:
                return handleTodo(arguments);
            case DEADLINE:
                return handleDeadline(arguments);
            case EVENT:
                return handleEvent(arguments);
            case ON:
                return handleOn(arguments);
            case FIND:
                return handleFind(arguments);
            case UNKNOWN:
            default:
                throw new AmigoBotException("Ay caramba! I don't know what that means, compadre :-(");
        }
    }

    private String formatTaskList() {
        String items = IntStream.range(0, tasks.size())
                .mapToObj(i -> (i + 1) + "." + tasks.getTask(i))
                .collect(Collectors.joining("\n"));
        return "Here are the tasks in your list:" + (items.isEmpty() ? "" : "\n" + items);
    }

    private String handleDelete(String arguments) throws AmigoBotException, IOException {
        List<Integer> indices = parseTaskIndices(arguments, "delete 1 2 3 or delete 1-3");
        // Delete in descending order so earlier indices remain valid
        indices.sort(Collections.reverseOrder());
        List<Task> removed = new ArrayList<>();
        for (int index : indices) {
            removed.add(tasks.deleteTask(index));
        }
        storage.save(tasks);
        Collections.reverse(removed);
        StringBuilder sb = new StringBuilder(randomPhrase(DELETED_PHRASES));
        for (Task task : removed) {
            sb.append("\n  ").append(task);
        }
        sb.append("\nNow you have ").append(tasks.size()).append(" tasks in the list.");
        return sb.toString();
    }

    private String handleMark(String arguments) throws AmigoBotException, IOException {
        List<Integer> indices = parseTaskIndices(arguments, "mark 1 2 3 or mark 1-3");
        for (int index : indices) {
            tasks.getTask(index).markAsDone();
        }
        storage.save(tasks);
        StringBuilder sb = new StringBuilder(randomPhrase(MARKED_PHRASES));
        for (int index : indices) {
            sb.append("\n  ").append(tasks.getTask(index));
        }
        return sb.toString();
    }

    private String handleUnmark(String arguments) throws AmigoBotException, IOException {
        List<Integer> indices = parseTaskIndices(arguments, "unmark 1 2 3 or unmark 1-3");
        for (int index : indices) {
            tasks.getTask(index).markAsNotDone();
        }
        storage.save(tasks);
        StringBuilder sb = new StringBuilder(randomPhrase(UNMARKED_PHRASES));
        for (int index : indices) {
            sb.append("\n  ").append(tasks.getTask(index));
        }
        return sb.toString();
    }

    private String handleTodo(String arguments) throws AmigoBotException, IOException {
        if (arguments.isEmpty()) {
            throw new AmigoBotException("Ay caramba! The description of a todo cannot be empty.");
        }
        Task task = new Todo(arguments);
        tasks.addTask(task);
        storage.save(tasks);
        return formatTaskAdded(task);
    }

    private String handleDeadline(String arguments) throws AmigoBotException, IOException {
        if (arguments.isEmpty()) {
            throw new AmigoBotException("Ay caramba! The description of a deadline cannot be empty.");
        }
        if (!arguments.matches("(?i).*\\s*/by\\s+.*")) {
            throw new AmigoBotException(
                    "Ay caramba! A deadline needs a /by date. Example: deadline return book /by Sunday");
        }
        String[] parts = arguments.split("\\s*/by\\s+", 2);
        String desc = parts[0].trim();
        String byStr = parts[1].trim();
        if (desc.isEmpty()) {
            throw new AmigoBotException("Ay caramba! The description of a deadline cannot be empty.");
        }
        if (byStr.isEmpty()) {
            throw new AmigoBotException("Ay caramba! The /by date of a deadline cannot be empty.");
        }
        LocalDate byDate = tryParseDate(byStr);
        Task task = byDate != null ? new Deadline(desc, byDate) : new Deadline(desc, byStr);
        tasks.addTask(task);
        storage.save(tasks);
        return formatTaskAdded(task);
    }

    private String handleEvent(String arguments) throws AmigoBotException, IOException {
        if (arguments.isEmpty()) {
            throw new AmigoBotException("Ay caramba! The description of an event cannot be empty.");
        }
        if (!arguments.matches("(?i).*\\s*/from\\s+.*")) {
            throw new AmigoBotException(
                    "Ay caramba! An event needs a /from time. Example: event meeting /from Mon 2pm /to 4pm");
        }
        String[] parts = arguments.split("\\s*/from\\s+", 2);
        String desc = parts[0].trim();
        if (desc.isEmpty()) {
            throw new AmigoBotException("Ay caramba! The description of an event cannot be empty.");
        }
        if (!parts[1].matches("(?i).*\\s*/to\\s+.*")) {
            throw new AmigoBotException(
                    "Ay caramba! An event needs a /to time. Example: event meeting /from Mon 2pm /to 4pm");
        }
        String[] timeParts = parts[1].split("\\s*/to\\s+", 2);
        String from = timeParts[0].trim();
        String to = timeParts[1].trim();
        if (from.isEmpty()) {
            throw new AmigoBotException("Ay caramba! The /from time of an event cannot be empty.");
        }
        if (to.isEmpty()) {
            throw new AmigoBotException("Ay caramba! The /to time of an event cannot be empty.");
        }
        LocalDate fromDate = tryParseDate(from);
        LocalDate toDate = tryParseDate(to);
        Task task = new Event(desc, fromDate, fromDate == null ? from : null,
                toDate, toDate == null ? to : null);
        tasks.addTask(task);
        storage.save(tasks);
        return formatTaskAdded(task);
    }

    private String handleOn(String arguments) throws AmigoBotException {
        if (arguments.isEmpty()) {
            throw new AmigoBotException("Ay caramba! Please provide a date. Example: on 2025-08-20");
        }
        LocalDate target = tryParseDate(arguments);
        if (target == null) {
            throw new AmigoBotException(
                    "Ay caramba! Invalid date format. Please use yyyy-MM-dd or dd/mm/yyyy.");
        }
        ArrayList<Task> matching = IntStream.range(0, tasks.size())
                .mapToObj(tasks::getTask)
                .filter(task -> isOnDate(task, target))
                .collect(Collectors.toCollection(ArrayList::new));
        String dateDisplay = target.format(DateTimeFormatter.ofPattern("MMM d yyyy"));
        StringBuilder sb = new StringBuilder("Here are the tasks on " + dateDisplay + ":");
        if (matching.isEmpty()) {
            sb.append("\nNo tasks found on that date, compadre!");
        } else {
            for (int i = 0; i < matching.size(); i++) {
                appendNumberedItem(sb, i + 1, matching.get(i).toString());
            }
        }
        return sb.toString();
    }

    private String handleFind(String arguments) throws AmigoBotException {
        if (arguments.isEmpty()) {
            throw new AmigoBotException(
                    "Ay caramba! Please provide a keyword to search for. Example: find book");
        }
        ArrayList<Task> found = tasks.findTasks(arguments);
        StringBuilder sb = new StringBuilder("Here are the matching tasks in your list:");
        if (found.isEmpty()) {
            sb.append("\nNo matching tasks found, compadre!");
        } else {
            for (int i = 0; i < found.size(); i++) {
                appendNumberedItem(sb, i + 1, found.get(i).toString());
            }
        }
        return sb.toString();
    }

    /**
     * Parses one or more 1-based task indices from the arguments string.
     * Accepts individual numbers separated by spaces (e.g. "1 3 5"),
     * ranges with a dash (e.g. "1-5"), or a mix of both (e.g. "1-3 5 7").
     * Returns a sorted list of unique 0-based indices.
     */
    private List<Integer> parseTaskIndices(String arguments, String example) throws AmigoBotException {
        if (arguments.isEmpty()) {
            throw new AmigoBotException(
                    "Ay caramba! Please provide task number(s). Example: " + example);
        }
        List<Integer> indices = new ArrayList<>();
        String[] tokens = arguments.split("\\s+");
        for (String token : tokens) {
            if (token.contains("-")) {
                String[] rangeParts = token.split("-", 2);
                int start;
                int end;
                try {
                    start = Integer.parseInt(rangeParts[0]);
                    end = Integer.parseInt(rangeParts[1]);
                } catch (NumberFormatException e) {
                    throw new AmigoBotException(
                            "Ay caramba! '" + token + "' is not a valid range, compadre.");
                }
                if (start > end) {
                    throw new AmigoBotException(
                            "Ay caramba! Invalid range " + token + ". Start must not be greater than end.");
                }
                for (int i = start; i <= end; i++) {
                    indices.add(i - 1);
                }
            } else {
                try {
                    indices.add(Integer.parseInt(token) - 1);
                } catch (NumberFormatException e) {
                    throw new AmigoBotException(
                            "Ay caramba! '" + token + "' is not a valid number, compadre.");
                }
            }
        }
        // Remove duplicates and sort
        indices = indices.stream().distinct().sorted().collect(Collectors.toList());
        for (int index : indices) {
            if (index < 0 || index >= tasks.size()) {
                throw new AmigoBotException("Ay caramba! Task number " + (index + 1)
                        + " does not exist. You have " + tasks.size() + " tasks.");
            }
        }
        return indices;
    }

    private void appendNumberedItem(StringBuilder sb, int number, String item) {
        sb.append("\n").append(number).append(".").append(item);
    }

    private String randomPhrase(String[] phrases) {
        return phrases[random.nextInt(phrases.length)];
    }

    private String formatTaskAdded(Task task) {
        return randomPhrase(ADDED_PHRASES) + "\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    private boolean isOnDate(Task task, LocalDate target) {
        if (task instanceof Deadline) {
            Deadline d = (Deadline) task;
            return d.getByDate() != null && d.getByDate().equals(target);
        } else if (task instanceof Event) {
            Event e = (Event) task;
            if (e.getFromDate() != null && e.getToDate() != null) {
                return !target.isBefore(e.getFromDate()) && !target.isAfter(e.getToDate());
            } else if (e.getFromDate() != null) {
                return e.getFromDate().equals(target);
            } else if (e.getToDate() != null) {
                return e.getToDate().equals(target);
            }
        }
        return false;
    }

    /**
     * Tries to parse a date string into a LocalDate.
     * Normalizes separators (/, ., -) and detects whether the format is
     * year-first (yyyy-mm-dd) or day-first (dd-mm-yyyy).
     *
     * @param input the date string to parse.
     * @return the parsed LocalDate, or null if the string is not a recognizable date.
     */
    private static LocalDate tryParseDate(String input) {
        String normalized = input.replace("/", "-").replace(".", "-");
        String[] parts = normalized.split("-");
        if (parts.length != 3) {
            return null;
        }
        try {
            if (parts[0].length() == 4) {
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);
                return LocalDate.of(year, month, day);
            } else if (parts[2].length() == 4) {
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);
                return LocalDate.of(year, month, day);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Entry point for the text-based CLI mode.
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        AmigoBot bot = new AmigoBot();

        while (true) {
            String input = ui.readCommand();
            ui.showLine();
            System.out.println(bot.getResponse(input));
            ui.showLine();
            if (input.equalsIgnoreCase("bye")) {
                break;
            }
        }
        ui.close();
    }
}
