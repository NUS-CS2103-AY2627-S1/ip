package kotha;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BooleanSupplier;

import kotha.tasks.Task;
import kotha.tasks.TaskList;

/** Executes Kotha commands independently of the user interface. */
public class KothaEngine {
    private final Storage storage;
    private final TaskList tasks;
    private final Parser parser = new Parser();
    private final BooleanSupplier isRoyalSelector;
    private String lastResponseStyle = Persona.ROYAL.getStyle();

    /** Creates an engine backed by the normal Kotha data file. */
    public KothaEngine() {
        this(() -> ThreadLocalRandom.current().nextBoolean());
    }

    /** Creates an engine with a supplied persona selector for deterministic testing. */
    KothaEngine(BooleanSupplier isRoyalSelector) {
        this("data/kotha.txt", isRoyalSelector);
    }

    /** Creates an engine with explicit storage, primarily for isolated tests. */
    KothaEngine(String storagePath, BooleanSupplier isRoyalSelector) {
        storage = new Storage(storagePath);
        tasks = new TaskList(storage.loadTasks());
        this.isRoyalSelector = isRoyalSelector;
    }

    /** Returns a randomly styled greeting and records its persona for the interface. */
    public String createGreeting() {
        Persona persona = choosePersona();
        return persona.choosePhrase(
                "Hail, Your Majesty. I am Kotha, thy devoted keeper of tasks. "
                        + "What service may I render unto thee?",
                "Yo, it's Kotha. Drop a command, bestie, and try not to make it cringe.");
    }

    /** Executes one command and returns the text that an interface should display. */
    public String processCommand(String command) {
        Persona persona = choosePersona();
        try {
            Parser.CommandType type = parser.parse(command);
            return executeCommand(type, command, persona);
        } catch (KothaException exception) {
            return formatError(exception.getMessage(), persona);
        } catch (IllegalArgumentException exception) {
            return formatError(exception.getMessage(), persona);
        } catch (RuntimeException exception) {
            return formatError("The command contains an invalid value.", persona);
        }
    }

    /** Executes a command after it has been parsed and records its effect. */
    private String executeCommand(Parser.CommandType type, String command, Persona persona) throws KothaException {
        switch (type) {
            case LIST:
                return formatTasks(tasks.asList(), persona.choosePhrase(
                        "Behold, Your Majesty, the duties inscribed upon thy royal ledger:",
                        "Bro really forgot their own tasks. Fine, here's the list, dummy:"), persona);
            case FIND:
                String keyword = command.substring("find".length()).trim();
                if (keyword.isEmpty()) {
                    throw new KothaException("Provide a keyword to search for.");
                }
                return formatTasks(tasks.find(keyword), persona.choosePhrase(
                        "Behold, Your Majesty, the tasks thy faithful servant hath found:",
                        "Your search was low-key vague, but I still found these. You're welcome:"), persona);
            case TODO:
                Task todo = Task.createTodo(command);
                assert todo != null : "Successful todo creation must produce a task";
                return addTask(todo, persona.choosePhrase(
                        "As thou commandest, Your Majesty. This humble to-do now grace thy ledger:",
                        "Bet. I added the to-do because apparently remembering it was a skill issue:"));
            case DEADLINE:
                Task deadline = Task.createDeadline(command);
                assert deadline != null : "Successful deadline creation must produce a task";
                return addTask(deadline, persona.choosePhrase(
                        "Thy appointed hour shall be remembered, Your Majesty. I have recorded this deadline:",
                        "Deadline saved. Missing this would be peak NPC behavior, just saying:"));
            case EVENT:
                Task event = Task.createEvent(command);
                assert event != null : "Successful event creation must produce a task";
                return addTask(event, persona.choosePhrase(
                        "Thy audience is duly appointed, Your Majesty. I have recorded this event:",
                        "Event added. Your calendar was giving absolutely nothing before this:"));
            case CONSTRAINT:
                Task constraint = Task.createConstraint(command);
                assert constraint != null : "Successful constraint creation must produce a task";
                return addTask(constraint, persona.choosePhrase(
                        "Thy decree is set, Your Majesty. I have bound this task to its condition:",
                        "Constraint added. Even you should be able to follow this one, no cap:"));
            case MARK:
            case UNMARK:
                return updateTaskStatus(command, type == Parser.CommandType.MARK, persona);
            case DELETE:
                int deleteIndex = taskIndex(command, "delete");
                Task removed = tasks.remove(deleteIndex);
                save();
                return persona.choosePhrase(
                        "At thy word, Your Majesty, this task is banished from the royal record:\n" + removed,
                        "Deleted. That task escaped your chaos. Honestly, a W for the task:\n" + removed);
            case BYE:
                return persona.choosePhrase(
                        "Fare thee well, Your Majesty. May fortune smile upon thy noble reign.",
                        "Finally. Log off, bestie; my patience is at one percent.");
            default:
                return persona.choosePhrase(
                        "Pardon me, Your Majesty, but no such command is known within thy realm.",
                        "That command is giving nonsense. Use your brain, dummy.");
        }
    }

    /** Adds a newly created task, persists it, and formats the confirmation response. */
    private String addTask(Task task, String responsePrefix) {
        for (Task existing : tasks.asList()) {
            if (existing.getDescription().equalsIgnoreCase(task.getDescription())) {
                throw new IllegalArgumentException("A task with the same description already exists.");
            }
        }
        tasks.add(task);
        save();
        return responsePrefix + "\n" + tasks.get(tasks.size() - 1);
    }

    /** Updates a task's completion status, persists it, and formats the response. */
    private String updateTaskStatus(String command, boolean isDone, Persona persona) throws KothaException {
        String commandWord = isDone ? "mark" : "unmark";
        int taskIndex = taskIndex(command, commandWord);
        tasks.get(taskIndex).setDone(isDone);
        save();
        String royalPrefix = isDone
                ? "Rejoice, Your Majesty, for I have marked this duty fulfilled:"
                : "As thou wishest, Your Majesty, I have restored this duty to thy ledger:";
        String rudePrefix = isDone
                ? "Task done. Rare productivity W from you, honestly:"
                : "Bro is unfinishing tasks now? That's wild. Fine, it's pending again:";
        return persona.choosePhrase(royalPrefix, rudePrefix) + "\n" + tasks.get(taskIndex);
    }

    /** Returns the style category for the most recently processed response. */
    public String getLastResponseStyle() {
        return lastResponseStyle;
    }

    private String formatTasks(List<Task> selected, String heading, Persona persona) {
        if (selected.isEmpty()) {
            return persona.choosePhrase(
                    "Thy royal ledger standeth empty, Your Majesty; no duties await thee.",
                    "The list is empty, bestie. You sent me on a whole side quest for nothing. Massive L.");
        }
        StringBuilder result = new StringBuilder(heading).append("\n");
        for (int index = 0; index < selected.size(); index++) {
            result.append(index + 1).append(". ").append(selected.get(index)).append("\n");
        }
        return result.toString().trim();
    }

    private String formatError(String message, Persona persona) {
        switch (message) {
            case "Provide a keyword to search for.":
                return persona.choosePhrase(
                        "Prithee, Your Majesty, grant me a word by which I may seek thy tasks.",
                        "You gave me zero keywords, bestie. Am I supposed to read your mind?");
            case "A description is required after 'todo'.":
                return persona.choosePhrase(
                        "Prithee, Your Majesty, speak the duty thou wouldst have me record after 'todo'.",
                        "A to-do with no description? That's literally nothing, dummy. Type the task.");
            case "A deadline requires a description and a '/by' date.":
                return persona.choosePhrase(
                        "Your Majesty, pray name the duty and its appointed '/by' date.",
                        "No task and no '/by' date? This deadline is cooked. Try again.");
            case "An event requires a description, '/from', and '/to' time.":
                return persona.choosePhrase(
                        "Your Majesty, pray provide the event, its '/from' hour, and its '/to' hour.",
                        "Your event has no proper '/from' and '/to'. Calendar literacy left the chat.");
            case "A constraint requires a description and a '/after' trigger.":
                return persona.choosePhrase(
                        "Your Majesty, pray name the duty and the '/after' condition that shall precede it.",
                        "That constraint is missing its task or '/after' trigger. It's not that deep, bro.");
            case "Use d/M, d/M/yy, or d/M/yyyy, optionally followed by HHmm.":
                return persona.choosePhrase(
                        "Your Majesty, pray write the date as d/M, d/M/yy, or d/M/yyyy, "
                                + "with HHmm shouldst thou desire an hour.",
                        "That date format is cursed. Use d/M, d/M/yy, or d/M/yyyy, then HHmm if needed.");
            case "Please provide a valid task number.":
                return persona.choosePhrase(
                        "Prithee, Your Majesty, bestow upon me a valid number from thy royal ledger.",
                        "That's not a valid task number. Numbers are not optional, bestie.");
            case "That task number does not exist.":
                return persona.choosePhrase(
                        "Alas, Your Majesty, no task bearing that number dwelleth within thy ledger.",
                        "That task number does not exist. Bro is selecting imaginary tasks now.");
            case "An event must end after it starts.":
                return persona.choosePhrase(
                        "Your Majesty, an event's ending hour must follow its appointed beginning.",
                        "That event ends before it starts. Time travel is not supported, bestie.");
            case "A task with the same description already exists.":
                return persona.choosePhrase(
                        "Your Majesty, that duty already graces thy royal ledger.",
                        "You already have a task with that description. We are not making clones today.");
            case "The command contains an invalid value.":
                return persona.choosePhrase(
                        "Your Majesty, one of the values in thy command is not fit for the royal ledger.",
                        "One of those values is busted. Check your command and try again, bestie.");
            default:
                return persona.choosePhrase(
                        "My deepest apologies, Your Majesty; thy command could not be fulfilled.",
                        "Yeah, that did not work. Huge skill issue.");
        }
    }

    private int taskIndex(String command, String keyword) throws KothaException {
        String number = command.substring(keyword.length()).trim();
        if (!number.matches("\\d+")) {
            throw new KothaException("Please provide a valid task number.");
        }
        int index;
        try {
            index = Math.subtractExact(Integer.parseInt(number), 1);
        } catch (NumberFormatException | ArithmeticException exception) {
            throw new KothaException("Please provide a valid task number.");
        }
        if (index < 0 || index >= tasks.size()) {
            throw new KothaException("That task number does not exist.");
        }
        assert index >= 0 && index < tasks.size()
                : "A validated task number must resolve to an existing task";
        return index;
    }

    private Persona choosePersona() {
        Persona persona = Persona.choose(isRoyalSelector);
        lastResponseStyle = persona.getStyle();
        return persona;
    }

    private void save() {
        storage.saveTasks(tasks.asList());
    }
}
