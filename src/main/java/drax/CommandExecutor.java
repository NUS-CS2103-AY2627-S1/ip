package drax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Executes parsed commands and coordinates changes to the task list and storage.
 */
public class CommandExecutor {
    /** Indicates whether the application should continue accepting commands after execution. */
    public enum Outcome {
        CONTINUE, EXIT
    }

    /**
     * Contains the text produced by a command and whether the application should continue.
     *
     * @param response text to present to the user in a string formatForDisplay.
     * @param outcome whether command processing should continue.
     */
    public record ExecutionResult(String response, Outcome outcome) {
    }

    private final TaskList tasks;
    private final Storage storage;
    private final TaskHistory taskHistory;

    /**
     * Creates an executor that coordinates commands using the given application components.
     *
     * @param tasks task list that commands query and modify.
     * @param storage storage used to persist task changes.
     */
    public CommandExecutor(TaskList tasks, Storage storage) {
        this.tasks = tasks;
        this.storage = storage;
        this.taskHistory = new TaskHistory();
        taskHistory.addMemento(tasks.createMemento());
    }

    /**
     * Executes a parsed command and returns its display text and control-flow outcome.
     *
     * @param command command to execute.
     * @return the response to display in string formatForDisplay and whether the application should continue
     */
    public ExecutionResult execute(Parser.Command command) {
        return switch (command.type()) {
            case BYE -> executeBye();
            case LIST -> executeList();
            case MARK -> executeMark(command);
            case UNMARK -> executeUnmark(command);
            case DELETE -> executeDelete(command);
            case TODO -> createTodo(command);
            case DEADLINE -> createDeadline(command);
            case EVENT -> createEvent(command);
            case FIND -> executeFind(command);
            case UNDO -> executeUndo();
            case REDO -> executeRedo();
            default -> executeUnknown();
        };
    }

    private ExecutionResult executeBye() {
        return new ExecutionResult("Godspeed. Hope to see ya again soon!", Outcome.EXIT);
    }

    private ExecutionResult executeList() {
        List<String> messages = new ArrayList<>();
        if (tasks.isEmpty()) {
            messages.add("Oops! You currently have no tasks.");
        }
        messages.add("Here are the tasks in your list!");

        int count = 1;
        for (Task task : tasks) {
            messages.add(formatTaskWithNumber(count, task));
            count++;
        }
        assert count == tasks.getSize() + 1 : "Each task number should get the correct corresponding number";
        return returnWithContinue(messages);
    }

    private ExecutionResult executeMark(Parser.Command command) {
        try {
            int index = parseTaskIndex(command.argument());
            Task task = tasks.get(index);
            task.markAsDone();

            List<String> messages = new ArrayList<>();
            saveTasks(messages);
            messages.add("I've marked this task as done:");
            messages.add(task.toString());
            taskHistory.addMemento(tasks.createMemento());
            return returnWithContinue(messages);
        } catch (DraxException e) {
            return parseWithContinue(e.getMessage());
        } catch (NumberFormatException e) {
            return parseWithContinue("Please enter a valid number!");
        }
    }

    private ExecutionResult executeUnmark(Parser.Command command) {
        try {
            int index = parseTaskIndex(command.argument());
            Task task = tasks.get(index);
            task.unmarkAsDone();

            List<String> messages = new ArrayList<>();
            saveTasks(messages);
            messages.add("I've marked this task as not done:");
            messages.add(task.toString());
            taskHistory.addMemento(tasks.createMemento());
            return returnWithContinue(messages);
        } catch (DraxException e) {
            return parseWithContinue(e.getMessage());
        } catch (NumberFormatException e) {
            return parseWithContinue("Please enter a valid number!");
        }
    }

    private ExecutionResult executeDelete(Parser.Command command) {
        try {
            int index = parseTaskIndex(command.argument());

            List<String> messages = new ArrayList<>();
            messages.add("I've deleted this task");
            messages.add(tasks.get(index).toString());

            int previousSize = tasks.getSize();
            tasks.remove(index);
            saveTasks(messages);
            messages.add(getTaskCountMessage());
            assert tasks.getSize() == previousSize - 1 : "Deleting a task should decrease task count by one";
            taskHistory.addMemento(tasks.createMemento());
            return returnWithContinue(messages);
        } catch (NumberFormatException e) {
            return parseWithContinue("Please enter a valid number!");
        } catch (DraxException | IllegalArgumentException e) {
            return parseWithContinue(e.getMessage());
        }
    }

    private ExecutionResult executeUnknown() {
        return parseWithContinue("Sorry! But that's not a function I can perform. :(");
    }

    private ExecutionResult createTodo(Parser.Command command) {
        try {
            String newTask = command.task();
            if (newTask.isEmpty()) {
                throw new DraxException("You didn't provide a task!?");
            }

            Todo newTodo = new Todo(newTask);
            int previousSize = tasks.getSize();
            tasks.add(newTodo);
            assert tasks.getSize() == previousSize + 1 : "Adding a todo should increase task count by one";
            taskHistory.addMemento(tasks.createMemento());
            return getTaskCreatedResult(newTodo);
        } catch (DraxException e) {
            return parseWithContinue(e.getMessage());
        }
    }

    private ExecutionResult createDeadline(Parser.Command command) {
        try {
            String newTask = command.task();
            if (newTask.isEmpty()) {
                throw new DraxException("You didn't provide a task!?");
            }

            if (command.endDate().isEmpty()) {
                throw new DraxException("You didn't provide a end date! Use /by [deadline]");
            }

            Deadline newDeadline = new Deadline(newTask, ScheduleDateTime.parse(command.endDate()));
            int previousSize = tasks.getSize();
            tasks.add(newDeadline);
            assert tasks.getSize() == previousSize + 1 : "Adding a deadline should increase task count by one";
            taskHistory.addMemento(tasks.createMemento());
            return getTaskCreatedResult(newDeadline);
        } catch (DraxException | IllegalArgumentException e) {
            return parseWithContinue(e.getMessage());
        }
    }

    private ExecutionResult createEvent(Parser.Command command) {
        try {
            String newTask = command.task();
            if (newTask.isEmpty()) {
                throw new DraxException("You didn't provide a task!?");
            }

            if (command.startDate().isEmpty() || command.endDate().isEmpty()) {
                throw new DraxException("You didn't provide when this event is happening! "
                        + "Use /from [date] /to [date]");
            }

            Event newEvent = new Event(newTask, ScheduleDateTime.parse(
                    command.startDate()), ScheduleDateTime.parse(command.endDate()));
            int previousSize = tasks.getSize();
            tasks.add(newEvent);
            assert tasks.getSize() == previousSize + 1 : "Adding an event should increase task count by one";
            taskHistory.addMemento(tasks.createMemento());
            return getTaskCreatedResult(newEvent);
        } catch (DraxException | IllegalArgumentException e) {
            return parseWithContinue(e.getMessage());
        }
    }

    private ExecutionResult executeFind(Parser.Command command) {
        try {
            String keyword = command.argument().toLowerCase();
            if (keyword.isEmpty()) {
                throw new DraxException("You didn't provide a keyword!?");
            }

            List<String> messages = new ArrayList<>();
            int count = 1;
            for (Task task : tasks) {
                String currentTask = task.getTask().toLowerCase();
                if (currentTask.contains(keyword)) {
                    if (count == 1) {
                        messages.add("Here are the matching tasks in your list:");
                    }
                    messages.add(formatTaskWithNumber(count, task));
                    count++;
                }
            }

            if (count == 1) {
                assert messages.isEmpty() : "No header messages or tasks should be added to messages";
                throw new DraxException("Oops! No matching tasks found!");
            }
            return returnWithContinue(messages);
        } catch (DraxException e) {
            return parseWithContinue(e.getMessage());
        }
    }
    private ExecutionResult executeUndo() {
        try {
            TaskMemento previousMemento = taskHistory.getPreviousMemento();
            if (previousMemento == null) {
                throw new DraxException("There's nothin' to undo!");
            }
            tasks.restoreTaskList(previousMemento);

            List<String> messages = new ArrayList<>();
            saveTasks(messages);

            messages.add("Your last command was undone!");
            messages.add(executeList().response());
            return returnWithContinue(messages);
        } catch (DraxException e) {
            return parseWithContinue(e.getMessage());
        }
    }

    private ExecutionResult executeRedo() {
        try {
            TaskMemento nextMemento = taskHistory.getNextMemento();
            if (nextMemento == null) {
                throw new DraxException("There's nothin' to redo!");
            }
            tasks.restoreTaskList(nextMemento);

            List<String> messages = new ArrayList<>();
            saveTasks(messages);

            messages.add("Your last command was redone!");
            messages.add(executeList().response());
            return returnWithContinue(messages);
        } catch (DraxException e) {
            return parseWithContinue(e.getMessage());
        }
    }

    private ExecutionResult getTaskCreatedResult(Task task) {
        assert !tasks.isEmpty() || tasks.get(tasks.getSize() - 1) == task
                : "Created task must be added before response";
        List<String> messages = new ArrayList<>();
        saveTasks(messages);
        messages.add("I've added this task");
        messages.add(task.toString());
        messages.add(getTaskCountMessage());
        return returnWithContinue(messages);
    }

    private int parseTaskIndex(String taskNumber) throws DraxException {
        int index = Integer.parseInt(taskNumber) - 1;
        boolean exceedsTaskSize = index >= tasks.getSize();
        if (exceedsTaskSize || index < 0) {
            throw new DraxException("This task doesn't exist. You don't have that many tasks!");
        }
        assert tasks.get(index) != null : "The task at the index exists in tasks";
        return index;
    }

    private String getTaskCountMessage() {
        if (tasks.getSize() == 1) {
            return "Now you have 1 task!";
        }
        return "Now you have " + tasks.getSize() + " tasks!";
    }

    private String formatTaskWithNumber(int number, Task task) {
        assert number >= 1 : "Task number should be 1 or greater";
        return number + "." + task;
    }

    private void saveTasks(List<String> messages) {
        try {
            storage.save(tasks);
        } catch (IOException | IllegalArgumentException e) {
            messages.add("Sorry! I couldn't save your tasks :(. They are available till you exit the program!");
        }
    }

    private static ExecutionResult parseWithContinue(String... messages) {
        return returnWithContinue(List.of(messages));
    }

    private static ExecutionResult returnWithContinue(List<String> messages) {
        return new ExecutionResult(String.join("\n", messages), Outcome.CONTINUE);
    }
}
