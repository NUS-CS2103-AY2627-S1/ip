package koko;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controls koko.Koko's application logic.
 */
public class Koko {
    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;

    /**
     * Creates a new Koko chatbot and loads saved tasks.
     */
    public Koko() {
        this(new Storage());
    }

    /**
     * Creates a chatbot using the given task storage.
     *
     * @param storage the storage used to load and save tasks
     */
    Koko(Storage storage) {
        ui = new Ui();
        this.storage = storage;
        try {
            tasks = new TaskList(storage.load());
        } catch (KokoException exception) {
            ui.showError(exception.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Runs the chatbot and processes user commands.
     */
    public void run() {
        ui.showWelcome();
        while (true) {
            String command = ui.readCommand();
            try {
                if (Parser.isBye(command)) {
                    ui.showBye();
                    return;
                }
                if (Parser.isList(command)) {
                    ui.showTasks(tasks);
                } else if (Parser.isCommand(command, "find")) {
                    findTasks(command);
                } else if (Parser.isCommand(command, "mark")) {
                    markTask(command);
                } else if (Parser.isCommand(command, "unmark")) {
                    unmarkTask(command);
                } else if (Parser.isCommand(command, "delete")) {
                    deleteTask(command);
                } else if (Parser.isCommand(command, "update")) {
                    updateTask(command);
                } else if (Parser.isCommand(command, "todo")) {
                    addTodo(command);
                } else if (Parser.isCommand(command, "deadline")) {
                    addDeadline(command);
                } else if (Parser.isCommand(command, "event")) {
                    addEvent(command);
                } else {
                    throw new KokoException("I don't recognise that command. "
                            + "Try todo, deadline, event, list, mark, unmark, delete, or update.");
                }
            } catch (KokoException exception) {
                ui.showError(exception.getMessage());
            }
        }
    }

    private void markTask(String command) throws KokoException {
        int taskIndex = Parser.getTaskIndex(command, "mark", tasks.size());
        tasks.get(taskIndex).markAsDone();
        storage.save(tasks.getTasks());
        ui.showMarkDone(tasks.get(taskIndex));
    }

    private void unmarkTask(String command) throws KokoException {
        int taskIndex = Parser.getTaskIndex(command, "unmark", tasks.size());
        tasks.get(taskIndex).markAsNotDone();
        storage.save(tasks.getTasks());
        ui.showMarkNotDone(tasks.get(taskIndex));
    }

    private void deleteTask(String command) throws KokoException {
        int taskIndex = Parser.getTaskIndex(command, "delete", tasks.size());
        Task removedTask = tasks.remove(taskIndex);
        storage.save(tasks.getTasks());
        ui.showDeletedTask(removedTask, tasks.size());
    }

    /**
     * Updates one task and displays the confirmation in the CLI.
     *
     * @param command the user's update command
     * @throws KokoException if the update details are invalid or cannot be saved
     */
    private void updateTask(String command) throws KokoException {
        UpdateRequest request = Parser.parseUpdate(command, tasks.size());
        Task updatedTask = createUpdatedTask(tasks.get(request.getTaskIndex()), request);
        tasks.set(request.getTaskIndex(), updatedTask);
        storage.save(tasks.getTasks());
        ui.showUpdatedTask(updatedTask);
    }

    private void addTodo(String command) throws KokoException {
        String description = Parser.parseTodo(command);
        addTask(new Todo(description));
    }

    private void addDeadline(String command) throws KokoException {
        Deadline deadline = Parser.parseDeadline(command);
        addTask(deadline);
    }

    private void addEvent(String command) throws KokoException {
        Event event = Parser.parseEvent(command);
        addTask(event);
    }

    private void addTask(Task task) throws KokoException {
        tasks.add(task);
        storage.save(tasks.getTasks());
        ui.showAddedTask(task, tasks.size());
    }

    private void findTasks(String command) throws KokoException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new KokoException(
                    "Chirp a keyword so I can search. Try: find book.");
        }
        ui.showMatchingTasks(tasks.find(keyword));
    }

    /**
     * Processes one line of user input and returns koko.Koko's reply as a String.
     * Used by the GUI (does not touch Ui, which prints to the console for the CLI).
     *
     * @param input the user's input line
     * @return koko.Koko's response text
     */
    public String getResponse(String input) {
        return getGuiResponse(input).text();
    }

    /**
     * Processes one line of user input and returns a response with its display type.
     *
     * @param input the user's input line
     * @return Koko's response and whether it describes an invalid command
     */
    public GuiResponse getGuiResponse(String input) {
        try {
            if (Parser.isBye(input)) {
                return new GuiResponse("Fly high! Koko will keep the nest cozy. See you soon!", false);
            }

            if (Parser.isList(input)) {
                return new GuiResponse(
                        formatTaskList(tasks.getTasks(), "Nest check! Here are the tasks in your nest:"),
                        false);
            }

            return new GuiResponse(getCommandResponse(input), false);
        } catch (KokoException exception) {
            return new GuiResponse(exception.getMessage(), true);
        }
    }

    /**
     * A GUI response and whether it should be displayed as an error.
     *
     * @param text the response shown to the user
     * @param isError whether the response was caused by invalid user input
     */
    public record GuiResponse(String text, boolean isError) {
    }

    /**
     * Returns the GUI response for a non-list, non-bye command.
     *
     * @param input the user's input line
     * @return Koko's response text
     * @throws KokoException if the command is invalid or cannot be saved
     */
    private String getCommandResponse(String input) throws KokoException {
        if (Parser.isCommand(input, "find")) {
            return findTasksForGui(input);
        }

        if (Parser.isCommand(input, "mark")) {
            return markTaskForGui(input);
        }

        if (Parser.isCommand(input, "unmark")) {
            return unmarkTaskForGui(input);
        }

        if (Parser.isCommand(input, "delete")) {
            return deleteTaskForGui(input);
        }

        if (Parser.isCommand(input, "update")) {
            return updateTaskForGui(input);
        }

        if (Parser.isCommand(input, "todo")) {
            return addTaskForGui(new Todo(Parser.parseTodo(input)));
        }

        if (Parser.isCommand(input, "deadline")) {
            return addTaskForGui(Parser.parseDeadline(input));
        }

        if (Parser.isCommand(input, "event")) {
            return addTaskForGui(Parser.parseEvent(input));
        }

        throw new KokoException("That command flew past me. "
                + "Try todo, deadline, event, list, mark, unmark, delete, or update.");
    }

    /**
     * Returns the GUI response for a find command.
     *
     * @param input the user's find command
     * @return the formatted matching tasks
     * @throws KokoException if the keyword is missing
     */
    private String findTasksForGui(String input) throws KokoException {
        String keyword = input.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new KokoException(
                    "Chirp a keyword so I can search. Try: find book.");
        }

        return formatTaskList(
                tasks.find(keyword),
                "Nest check! Here are the matching tasks in your nest:");
    }

    /**
     * Marks a task as done and returns the GUI response.
     *
     * @param input the user's mark command
     * @return the confirmation response
     * @throws KokoException if the task number is invalid or the tasks cannot be saved
     */
    private String markTaskForGui(String input) throws KokoException {
        int taskIndex = Parser.getTaskIndex(input, "mark", tasks.size());
        Task task = tasks.get(taskIndex);
        task.markAsDone();
        storage.save(tasks.getTasks());
        return "Wing-tastic! This task is now done:\n  " + task;
    }

    /**
     * Marks a task as not done and returns the GUI response.
     *
     * @param input the user's unmark command
     * @return the confirmation response
     * @throws KokoException if the task number is invalid or the tasks cannot be saved
     */
    private String unmarkTaskForGui(String input) throws KokoException {
        int taskIndex = Parser.getTaskIndex(input, "unmark", tasks.size());
        Task task = tasks.get(taskIndex);
        task.markAsNotDone();
        storage.save(tasks.getTasks());
        return "No flap—this task is back in flight:\n  " + task;
    }

    /**
     * Deletes a task and returns the GUI response.
     *
     * @param input the user's delete command
     * @return the confirmation response
     * @throws KokoException if the task number is invalid or the tasks cannot be saved
     */
    private String deleteTaskForGui(String input) throws KokoException {
        int taskIndex = Parser.getTaskIndex(input, "delete", tasks.size());
        Task removedTask = tasks.remove(taskIndex);
        storage.save(tasks.getTasks());
        return "Poof! I've sent this task flying from the nest:\n  " + removedTask
                + "\nNow you have " + tasks.size() + " tasks in the nest.";
    }

    /**
     * Updates a task and returns the GUI response.
     *
     * @param input the user's update command
     * @return the confirmation response
     * @throws KokoException if the update details are invalid or cannot be saved
     */
    private String updateTaskForGui(String input) throws KokoException {
        UpdateRequest request = Parser.parseUpdate(input, tasks.size());
        Task updatedTask = createUpdatedTask(tasks.get(request.getTaskIndex()), request);
        tasks.set(request.getTaskIndex(), updatedTask);
        storage.save(tasks.getTasks());
        return "Freshly fluffed! I've updated this task:\n  " + updatedTask;
    }

    /**
     * Creates a same-type replacement task containing the requested changes.
     *
     * @param task the task being updated
     * @param request the requested replacement fields
     * @return the updated task, retaining the original completion status
     * @throws KokoException if a field is unsuitable for the task type or an event range is invalid
     */
    private Task createUpdatedTask(Task task, UpdateRequest request) throws KokoException {
        Task updatedTask;

        if (task instanceof Todo) {
            ensureNoDateFields(request);
            String description = request.getDescription() == null
                    ? task.getDescription()
                    : request.getDescription();
            updatedTask = new Todo(description);
        } else if (task instanceof Deadline) {
            ensureNoEventFields(request);
            Deadline deadline = (Deadline) task;
            String description = request.getDescription() == null
                    ? task.getDescription()
                    : request.getDescription();
            LocalDateTime by = request.getBy() == null ? deadline.getBy() : request.getBy();
            updatedTask = new Deadline(description, by);
        } else {
            assert task instanceof Event : "Tasks can only be to-dos, deadlines, or events.";
            Event event = (Event) task;
            String description = request.getDescription() == null
                    ? task.getDescription()
                    : request.getDescription();
            LocalDateTime from = request.getFrom() == null ? event.getFrom() : request.getFrom();
            LocalDateTime to = request.getTo() == null ? event.getTo() : request.getTo();

            if (request.hasBy()) {
                throw new KokoException("That field cannot be updated for this task.");
            }
            if (to.isBefore(from)) {
                throw new KokoException("An event cannot end before it starts.");
            }

            updatedTask = new Event(description, from, to);
        }

        if (task.isDone()) {
            updatedTask.markAsDone();
        }

        return updatedTask;
    }

    /**
     * Rejects date and time fields supplied for a to-do task.
     *
     * @param request the requested update fields
     * @throws KokoException if a date or time field was supplied
     */
    private void ensureNoDateFields(UpdateRequest request) throws KokoException {
        if (request.hasBy() || request.hasFrom() || request.hasTo()) {
            throw new KokoException("That field cannot be updated for this task.");
        }
    }

    /**
     * Rejects event-only fields supplied for a deadline task.
     *
     * @param request the requested update fields
     * @throws KokoException if an event time field was supplied
     */
    private void ensureNoEventFields(UpdateRequest request) throws KokoException {
        if (request.hasFrom() || request.hasTo()) {
            throw new KokoException("That field cannot be updated for this task.");
        }
    }

    private String addTaskForGui(Task task) throws KokoException {
        tasks.add(task);
        storage.save(tasks.getTasks());
        return "Chirp-chirp! I've tucked this task into your nest:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the nest.";
    }

    private String formatTaskList(List<Task> taskList, String header) {
        StringBuilder sb = new StringBuilder(header);
        for (int i = 0; i < taskList.size(); i++) {
            sb.append("\n").append(i + 1).append(". ").append(taskList.get(i));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        new Koko().run();
    }
}
