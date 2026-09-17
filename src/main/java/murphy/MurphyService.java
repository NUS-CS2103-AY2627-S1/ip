package murphy;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.IntStream;
import murphy.parser.Parser;
import murphy.storage.Storage;
import murphy.task.Deadline;
import murphy.task.Event;
import murphy.task.Task;
import murphy.task.TaskList;
import murphy.task.Todo;

/** Provides command processing independently of Murphy's user interface. */
public class MurphyService {
    private static final int MAX_TASKS = 100;
    private static final Path DATA_FILE_PATH = Paths.get("data", "duke.txt");
    private final Parser parser = new Parser();
    private final TaskList tasks;
    private final Storage storage;

    /** Creates a service and loads Murphy's saved tasks. */
    public MurphyService() {
        this(DATA_FILE_PATH);
    }

    /** Creates a service using the supplied task data path. */
    public MurphyService(Path dataFilePath) {
        storage = new Storage(dataFilePath, MAX_TASKS, message -> { });
        tasks = storage.load();
    }

    /** Returns the current tasks. */
    public TaskList getTasks() {
        return tasks;
    }

    /** Processes one command and returns Murphy's response text. */
    public String respond(String input) {
        Parser.ParsedCommand parsed = parser.parse(input);
        try {
            return switch (parsed.command()) {
            case BYE -> "Bye! I’ll be here guarding your task list. Tiny paws, big responsibility.";
            case LIST -> listTasks();
            case FIND -> findTasks(parsed.argument());
            case ON -> tasksOn(parsed.argument());
            case DELETE -> deleteTask(parsed.argument());
            case MARK -> changeStatus(parsed.argument(), true);
            case UNMARK -> changeStatus(parsed.argument(), false);
            case TODO -> addTodo(parsed);
            case DEADLINE -> addDeadline(parsed.argument());
            case EVENT -> addEvent(parsed.argument());
            case UNKNOWN -> throw new MurphyException("I don't recognise that command. Try todo, deadline, event, list, find, on, delete, mark, or unmark.");
            };
        } catch (MurphyException exception) {
            return "OOPS! " + exception.getMessage();
        }
    }

    private String listTasks() {
        StringBuilder result = new StringBuilder("Here are the tasks in your list. I sniffed them all:");
        for (int i = 0; i < tasks.size(); i++) {
            result.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return result.toString();
    }

    private String findTasks(String keyword) throws MurphyException {
        if (keyword.isBlank()) throw new MurphyException("Please provide a keyword to find, like: find book");
        StringBuilder result = new StringBuilder("Here are the matching tasks in your list:");
        String lowerKeyword = keyword.toLowerCase();
        List<Task> matchingTasks = tasks.asList().stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerKeyword))
                .toList();
        IntStream.range(0, matchingTasks.size()).forEach(index -> result.append("\n")
                .append(index + 1).append(".").append(matchingTasks.get(index)));
        return matchingTasks.isEmpty()
                ? result + "\nNo matching tasks found. Even Murphy's magnifying glass came up empty."
                : result.toString();
    }

    private String tasksOn(String dateText) throws MurphyException {
        LocalDate date;
        try { date = LocalDate.parse(dateText); } catch (DateTimeParseException exception) {
            throw new MurphyException("Please enter the date as yyyy-MM-dd, like: 2019-10-15");
        }
        StringBuilder result = new StringBuilder("Tasks on " + date + ":");
        int count = 0;
        for (Task task : tasks) if ((task instanceof Deadline deadline && deadline.occursOn(date))
                || (task instanceof Event event && event.occursOn(date))) result.append("\n").append(++count).append(".").append(task);
        return count == 0 ? result + "\nNo deadlines or events found on that date." : result.toString();
    }

    private String deleteTask(String value) throws MurphyException { int index = parseIndex(value); Task task = tasks.remove(index); storage.save(tasks); return "Noted. I've removed this task:\n" + task + "\nNow you have " + tasks.size() + " tasks in the list."; }
    private String changeStatus(String value, boolean done) throws MurphyException { int index = parseIndex(value); if (done) tasks.get(index).markAsDone(); else tasks.get(index).markAsNotDone(); storage.save(tasks); return done ? "Nice! I've marked this task as done:\n" + tasks.get(index) : "OK, I've marked this task as not done yet:\n" + tasks.get(index); }
    private int parseIndex(String value) throws MurphyException { try { int index = Integer.parseInt(value) - 1; if (index < 0 || index >= tasks.size()) throw new MurphyException("I couldn't find that task. Please choose a number from 1 to " + tasks.size() + "."); return index; } catch (NumberFormatException exception) { throw new MurphyException("Please tell me which task number to change, like: mark 2"); } }
    private String addTodo(Parser.ParsedCommand parsed) throws MurphyException { String description = parsed.argument(); if (description.isBlank()) throw new MurphyException("A todo needs a description. Try: todo buy groceries"); return add(new Todo(description)); }
    private String addDeadline(String input) throws MurphyException { int marker = input.indexOf(" /by "); if (marker < 0 || input.indexOf(" /by ", marker + 5) >= 0 || input.substring(0, marker).isBlank() || input.substring(marker + 5).isBlank()) throw new MurphyException("A deadline needs one description and date, like: deadline submit report /by 2019-10-15"); try { return add(new Deadline(input.substring(0, marker).trim(), LocalDate.parse(input.substring(marker + 5).trim()))); } catch (DateTimeParseException exception) { throw new MurphyException("Please enter the deadline date as yyyy-MM-dd, like: 2019-10-15"); } }
    private String addEvent(String input) throws MurphyException { int from = input.indexOf(" /from "); int to = input.indexOf(" /to ", from + 7); if (from < 0 || to < 0 || input.indexOf(" /from ", from + 7) >= 0 || input.indexOf(" /to ", to + 5) >= 0 || input.substring(0, from).isBlank() || input.substring(from + 7, to).isBlank() || input.substring(to + 5).isBlank()) throw new MurphyException("An event needs one description, start, and end time, like: event meeting /from 2pm /to 4pm"); return add(new Event(input.substring(0, from).trim(), input.substring(from + 7, to).trim(), input.substring(to + 5).trim())); }
    private String add(Task task) throws MurphyException {
        Task duplicate = tasks.findDuplicate(task);
        if (duplicate != null) {
            return "OOPS! This task is already in your list:\n" + duplicate
                    + "\nI kept the existing task and did not add a duplicate.";
        }
        if (tasks.size() >= MAX_TASKS) {
            throw new MurphyException("I can't remember more than " + MAX_TASKS + " tasks.");
        }
        tasks.add(task);
        storage.save(tasks);
        return "Got it. I've added this task:\n" + task + "\nNow you have " + tasks.size() + " tasks in the pack.";
    }
}
