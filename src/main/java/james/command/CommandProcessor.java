package james.command;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

import james.exception.UserInputException;
import james.parser.Parser;
import james.storage.Storage;
import james.task.Task;
import james.task.TaskList;

/**
 * Processes commands independently of the console or graphical interface.
 */
public class CommandProcessor {
    private static final List<Sticker> STICKERS = List.of(Sticker.values());

    private static final int MAX_UNDO_CHANGES = 20;

    private static final String DELETE_COMMAND_STRING = "delete";
    private static final String MARK_COMPLETE_MESSAGE_PREFIX = "Yatta! Task done. Time for a bread break:\n";
    private static final String TASK_LIST_ENTRY_FORMAT = "\n%d.%s";
    private static final String LINE_BREAK = "\n";

    private final RandomGenerator random;
    private final Storage storage;
    private TaskList taskList;
    private final Deque<TaskList> undoSnapshots = new ArrayDeque<>();

    /**
     * Initializes a processor with tasks loaded from the given file.
     *
     * @param filePath Persistent storage file path.
     */
    public CommandProcessor(String filePath) {
        this(filePath, new Random());
    }

    /**
     * Initializes a processor with a supplied random generator for reproducible selection.
     *
     * @param filePath Persistent storage file path.
     * @param random Generator used to select stickers.
     */
    public CommandProcessor(String filePath, RandomGenerator random) {
        this.random = random;
        storage = new Storage(filePath);
        taskList = new TaskList(storage.load());
    }

    /**
     * Returns startup diagnostics for tasks that could not be loaded.
     *
     * @return Warning and recovery guidance, or an empty string when loading succeeded.
     */
    public String getLoadWarning() {
        return storage.getLoadWarning();
    }

    /**
     * Processes one command and returns its displayable result.
     *
     * @param input Raw command input.
     * @return Result containing message, category, and exit state.
     */
    public CommandResponse process(String input) {
        String exceptionMessage = "Gomen! A little flour in the gears.\n";

        try {
            String[] parts = Parser.parseCommand(input);
            Command command = Parser.parseCommandType(parts[0]);
            String remainingArguments = Parser.extractArguments(parts);

            if ((command == Command.LIST || command == Command.BYE || command == Command.RANDOM_STICKER) &&
                    remainingArguments != null) {
                throw new UserInputException(command.name() + " does not take arguments.");
            }
            return executeCommand(command, remainingArguments);
        } catch (UserInputException e) {
            Sticker sticker = switch (e.getCategory()) {
            case INPUT -> Sticker.NANKORE;
            case DUPLICATE, STORAGE -> Sticker.GOMEN;
            };
            return CommandResponse.createWithSticker(exceptionMessage + e.getMessage(),
                    CommandResponse.Type.ERROR, sticker);
        }
    }

    /**
     * Dispatches a parsed command to its handler.
     */
    private CommandResponse executeCommand(Command command, String arguments) throws UserInputException {
        String unknownCommandMessage = "I don't know that recipe! Try list or todo <description>.";

        return switch (command) {
        case LIST_BY_DATE -> listByDate(arguments);
        case FIND -> find(arguments);
        case UNDO -> undo(arguments);
        case DELETE -> delete(arguments);
        case TODO -> add(Parser.parseTodo(arguments));
        case EVENT -> add(Parser.parseEvent(arguments));
        case DEADLINE -> add(Parser.parseDeadline(arguments));
        case MARK -> mark(arguments);
        case UNMARK -> unmark(arguments);
        case LIST -> listTasks();
        case RANDOM_STICKER -> selectRandomSticker();
        case BYE -> exit();
        default -> throw new UserInputException(unknownCommandMessage);
        };
    }

    /**
     * Selects a sticker uniformly without modifying tasks or undo history.
     */
    private CommandResponse selectRandomSticker() {
        int index = random.nextInt(STICKERS.size());
        return CommandResponse.createStickerOnly("Hehe! A little treat from my secret stash!", STICKERS.get(index));
    }

    /**
     * Lists tasks matching the supplied date.
     */
    private CommandResponse listByDate(String arguments) throws UserInputException {
        LocalDate date = Parser.parseDate(arguments);
        ArrayList<Task> tasks = taskList.getTasksOnDate(date);
        return createNormalResponse(formatTasksOnDate(date, tasks), tasks.isEmpty() ? Sticker.GOMEN : Sticker.OTSU);
    }

    /**
     * Finds tasks matching the supplied keyword.
     */
    private CommandResponse find(String arguments) throws UserInputException {
        ArrayList<Task> tasks = taskList.findTasks(Parser.parseFindKeyword(arguments));
        return createNormalResponse(formatMatchingTasks(tasks), tasks.isEmpty() ? Sticker.GOMEN : Sticker.OTSU);
    }

    /**
     * Deletes a task and records its previous state after saving.
     */
    private CommandResponse delete(String arguments) throws UserInputException {
        String taskRemovedPrefixMessage = "Poof! Snatched this task out of the basket:\n";
        String allTasksMessage = "\nNow you have %d tasks in the list.\n";

        TaskList beforeDelete = taskList.copy();
        Task deleted = taskList.deleteTask(
                Parser.parseTaskNumber(arguments, DELETE_COMMAND_STRING, taskList.getSize()));
        saveChange(beforeDelete);
        return CommandResponse.createWithSticker(
                taskRemovedPrefixMessage + deleted + allTasksMessage.formatted(taskList.getSize()),
                CommandResponse.Type.DELETE, Sticker.NAISU);
    }

    /**
     * Marks a task complete and records only an actual status change.
     */
    private CommandResponse mark(String arguments) throws UserInputException {
        String markCommandString = "mark";

        Task marked = taskList.getTask(Parser.parseTaskNumber(arguments, markCommandString, taskList.getSize()));
        if (!marked.isDone()) {
            TaskList beforeMark = taskList.copy();
            marked.markDone();
            saveChange(beforeMark);
        }
        return CommandResponse.createWithSticker(MARK_COMPLETE_MESSAGE_PREFIX + marked,
                CommandResponse.Type.MARK, Sticker.NAISU);
    }

    /**
     * Returns the application exit response.
     */
    private CommandResponse exit() {
        String exitMessage = "Mata ne! Rest up. I smell fresh bread!\n";

        return new CommandResponse(exitMessage, CommandResponse.Type.NORMAL, true);
    }

    /**
     * Returns the current task list.
     */
    private CommandResponse listTasks() {
        String listTaskMessage = "Let's peek in the basket. Your tasks:\n";

        return createNormalResponse(listTaskMessage + taskList, taskList.getSize() == 0 ? Sticker.GOMEN : Sticker.OTSU);
    }

    /**
     * Marks a task incomplete and records only an actual status change.
     */
    private CommandResponse unmark(String arguments) throws UserInputException {
        String unmarkCommandString = "unmark";
        String unmarkCompleteMessagePrefix = "Back in the oven! This task is not done yet:\n";

        Task unmarked = taskList.getTask(Parser.parseTaskNumber(arguments, unmarkCommandString, taskList.getSize()));
        if (unmarked.isDone()) {
            TaskList beforeUnmark = taskList.copy();
            unmarked.markNotDone();
            saveChange(beforeUnmark);
        }
        return CommandResponse.createWithSticker(unmarkCompleteMessagePrefix + unmarked,
                CommandResponse.Type.MARK, Sticker.OTSU);
    }

    /**
     * Adds and saves a task while retaining the previous state for undo.
     */
    private CommandResponse add(Task task) throws UserInputException {
        assert task != null : "Parser must return a task";
        for (Task existing : taskList.getTasks()) {
            if (existing.hasSameDetails(task)) {
                throw new UserInputException("Already in the basket! This task exists in your list.",
                        UserInputException.Category.DUPLICATE);
            }
        }
        TaskList beforeAdd = taskList.copy();
        taskList.addTask(task);
        saveChange(beforeAdd);
        return CommandResponse.createWithSticker("Hehe! Tucked this task into my bread basket:\n" + task +
                "\nNow you have %d tasks in the list.".formatted(taskList.getSize()),
                CommandResponse.Type.ADD, Sticker.YATTA);
    }

    /**
     * Saves a change and records its snapshot, or rolls back on failure.
     */
    private void saveChange(TaskList previous) throws UserInputException {
        if (!storage.save(taskList)) {
            taskList = previous;
            if (storage.hasLoadErrors()) {
                throw new UserInputException("Saved tasks could not be fully loaded. No changes were made.\n" +
                        "Repair the saved file or restore read access, then restart James.",
                        UserInputException.Category.STORAGE);
            }
            if (storage.wasSaveLocked()) {
                throw new UserInputException("Another instance is saving tasks. No changes were made; try again.",
                        UserInputException.Category.STORAGE);
            }
            throw new UserInputException("Could not save tasks. No changes were made.",
                    UserInputException.Category.STORAGE);
        }
        undoSnapshots.push(previous);
        if (undoSnapshots.size() > MAX_UNDO_CHANGES) {
            undoSnapshots.removeLast();
        }
    }

    /**
     * Restores and saves the latest snapshot without recording another undo entry.
     */
    private CommandResponse undo(String arguments) throws UserInputException {
        if (arguments != null && !arguments.isBlank()) {
            throw new UserInputException("Undo does not take arguments.\nTry: undo");
        }
        if (undoSnapshots.isEmpty()) {
            return createNormalResponse("Not a crumb to retrace. Nothing to undo.", Sticker.GOMEN);
        }
        TaskList previous = undoSnapshots.peek();
        if (!storage.save(previous)) {
            if (storage.wasSaveLocked()) {
                throw new UserInputException("Another instance is saving tasks. Undo was not applied; try again.",
                        UserInputException.Category.STORAGE);
            }
            throw new UserInputException("Could not save tasks. Undo was not applied; try again.",
                    UserInputException.Category.STORAGE);
        }
        taskList = undoSnapshots.pop();
        return createNormalResponse(
                "Tiptoe back! Undid the last change.\nNow you have %d tasks in the list.".formatted(taskList.getSize()),
                Sticker.NAISU);
    }

    /**
     * Formats tasks occurring on the given date.
     */
    private String formatTasksOnDate(LocalDate date, List<Task> tasks) {
        String tasksMatchingDateMessage = "Tasks on the menu for %s:".formatted(date);

        StringBuilder message = new StringBuilder(tasksMatchingDateMessage);
        for (int i = 0; i < tasks.size(); i++) {
            message.append(TASK_LIST_ENTRY_FORMAT.formatted(i + 1, tasks.get(i)));
        }
        return message.append(LINE_BREAK).toString();
    }

    /**
     * Formats tasks matching the given keyword.
     */
    private String formatMatchingTasks(List<Task> tasks) {
        String matchingTasksMessage = "Sniff sniff... here are the matching tasks:";

        StringBuilder message = new StringBuilder(matchingTasksMessage);
        for (int i = 0; i < tasks.size(); i++) {
            message.append(TASK_LIST_ENTRY_FORMAT.formatted(i + 1, tasks.get(i)));
        }
        return message.toString();
    }

    /**
     * Wraps a message in a normal response.
     */
    private CommandResponse createNormalResponse(String message, Sticker sticker) {
        return CommandResponse.createWithSticker(message, CommandResponse.Type.NORMAL, sticker);
    }
}
