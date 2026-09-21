package es;

import java.util.ArrayList;

/**
 * Entry point for the Es chatbot application.
 */
public class Es {
    private static final String INDENT = "    ";
    private final Storage guiStorage = new Storage();
    private final TaskList guiTasks = new TaskList(loadTasks(new Storage()));
    private boolean lastResponseWasError;
    /** Returns a response for the graphical interface. */
    public String getResponse(String input) {
        lastResponseWasError = false;
        if (input != null && (!input.equals(input.trim()) || input.matches(".*\\s{2,}.*"))) {
            lastResponseWasError = true;
            return "Perhaps you added an extra space somewhere?";
        }
        String commandText = input == null ? "" : input.trim();
        if (commandText.isEmpty()) {
            return "... You are silent. Give me something to work with.";
        }
        String conversation = commandText.replaceFirst("\\?$", "").trim().toLowerCase();
        if (conversation.equals("hi") || conversation.equals("hello")) {
            return "Pleasure to make your acquaintance.";
        }
        if (conversation.equals("who are you")) {
            return "I am the librarian of Somniareves. You can call me Es.";
        }
        if (conversation.equals("what are you")) {
            return "...";
        }
        if (conversation.equals("where is this place")) {
            return "... I don't know. Perhaps we can figure it out together.";
        }
        Command command = Parser.parse(commandText);
        try {
            if (command == null) {
                throw new EsException("... I didn't expect you to say that.");
            }
            switch (command) {
            case TODO:
                String description = commandText.substring(4).trim();
                if (description.isEmpty()) {
                    throw new EsException("... give me something to work with.");
                }
                Todo todo = new Todo(description.replaceAll("(?:^|\\s)#[^\\s|]+", "").trim());
                addInlineTags(todo, description);
                return addGuiTask(todo);
            case DEADLINE:
                addGuiDeadline(commandText.substring(8).trim());
                return addGuiTaskMessage(guiTasks.get(guiTasks.size() - 1));
            case EVENT:
                addGuiEvent(commandText.substring(5).trim());
                return addGuiTaskMessage(guiTasks.get(guiTasks.size() - 1));
            case LIST:
                if (guiTasks.size() == 0) {
                    return "The shelves are empty for now.";
                }
                StringBuilder result = new StringBuilder("Here are the tasks in your list:");
                for (int i = 0; i < guiTasks.size(); i++) {
                    result.append("\n").append(i + 1).append(".").append(guiTasks.get(i));
                }
                return result.toString();
            case BYE: return "Until next time.";
            case MARK: return toggleGui(commandText, true);
            case UNMARK: return toggleGui(commandText, false);
            case DELETE: return deleteGui(commandText);
            case FIND:
                String keyword = commandText.substring(4).trim();
                if (keyword.isEmpty()) {
                    throw new EsException("... give me something to work with.");
                }
                StringBuilder matches = new StringBuilder("These entries match your search:");
                ArrayList<Integer> matchingIndexes = guiTasks.find(keyword);
                if (matchingIndexes.isEmpty()) {
                    return "Nothing in the collection matches that search.";
                }
                for (int i : matchingIndexes) {
                    matches.append("\n").append(i + 1).append(".").append(guiTasks.get(i));
                }
                return matches.toString();
            case TAG:
                return updateGuiTags(commandText, true);
            case UNTAG:
                return updateGuiTags(commandText, false);
            case FINDTAG:
                return findGuiTag(commandText);
            default: return "... I didn't expect you to say that.";
            }
        } catch (EsException e) {
            lastResponseWasError = true;
            return e.getMessage();
        } catch (RuntimeException e) {
            lastResponseWasError = true;
            return "I could not make sense of that command.";
        }
    }

    /** Indicates whether the most recent response represented an error. */
    public boolean wasLastResponseAnError() {
        return lastResponseWasError;
    }

    private String addGuiTask(Task task) throws EsException {
        assert task != null : "A task command must create a non-null task";
        if (guiTasks.asList().stream().anyMatch(existing -> existing.toString().equals(task.toString()))) {
            throw new EsException("That task is already in the collection.");
        }
        guiTasks.add(task);
        guiStorage.save(guiTasks.asList());
        return addGuiTaskMessage(task);
    }
    private String addGuiTaskMessage(Task task) {
            return "Noted. I have placed it in your collection:\n  " + task
                + "\nNow you have " + guiTasks.size() + " tasks in the list.";
    }
    private void addGuiDeadline(String details) throws EsException {
        int separator = details.indexOf("/by ");
        if (separator < 0) {
            throw new EsException("A deadline needs /by followed by a date or time.");
        }
        String description = details.substring(0, separator).trim();
        String deadline = details.substring(separator + 4).trim();
        if (description.isEmpty()) {
            throw new EsException("A deadline still needs a description.");
        }
        if (deadline.isEmpty()) {
            throw new EsException("Specify when it is due.");
        }
        addGuiTask(new Deadline(description, deadline));
    }
    private void addGuiEvent(String details) throws EsException {
        int fromSeparator = details.indexOf("/from ");
        int toSeparator = details.indexOf("/to ");
        if (fromSeparator < 0 || toSeparator < 0) {
            throw new EsException("An event needs /from and /to times.");
        }
        String description = details.substring(0, fromSeparator).trim();
        String from = details.substring(fromSeparator + 6, toSeparator).trim();
        String to = details.substring(toSeparator + 4).trim();
        if (description.isEmpty()) {
            throw new EsException("An event still needs a description.");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new EsException("Specify when the event begins and ends.");
        }
        addGuiTask(new Event(description, from, to));
    }
    private String toggleGui(String text, boolean mark) throws EsException {
        String command = mark ? "mark" : "unmark";
        int index = Integer.parseInt(text.substring(command.length()).trim()) - 1;
        if (index < 0 || index >= guiTasks.size()) {
            throw new EsException("There is no task with that number.");
        }
        assert guiTasks.get(index) != null : "A valid task index must reference a task";
        if (mark) {
            if (guiTasks.get(index).getStatusIcon().equals("X")) {
                return "That task is already marked as complete.";
            }
            guiTasks.get(index).markAsDone();
        } else {
            if (guiTasks.get(index).getStatusIcon().equals(" ")) {
                return "That task is already unfinished.";
            }
            guiTasks.get(index).markAsNotDone();
        }
        guiStorage.save(guiTasks.asList());
        String message = mark ? "It is now marked as complete."
                : "The task has been returned to your list of unfinished matters.";
        return message + "\n  " + guiTasks.get(index);
    }
    private String deleteGui(String text) throws EsException {
        int index = Integer.parseInt(text.substring("delete".length()).trim()) - 1;
        if (index < 0 || index >= guiTasks.size()) {
            throw new EsException("There is no task with that number.");
        }
        Task removed = guiTasks.remove(index);
        guiStorage.save(guiTasks.asList());
        return "It has been removed.\n  " + removed
                + "\nNow you have " + guiTasks.size() + " tasks in the list.";
    }

    private String updateGuiTags(String text, boolean adding) throws EsException {
        String[] parts = text.split("\\s+");
        if (parts.length < 3) {
            throw new EsException("... give me something to work with.");
        }
        int index = Integer.parseInt(parts[1]) - 1;
        if (index < 0 || index >= guiTasks.size()) {
            throw new EsException("There is no task with that number.");
        }
        for (int i = 2; i < parts.length; i++) {
            String tag = normalizeCommandTag(parts[i]);
            if (adding) {
                guiTasks.get(index).addTag(tag);
            } else {
                guiTasks.get(index).removeTag(tag);
            }
        }
        guiStorage.save(guiTasks.asList());
        String message = adding ? "The requested tags have been recorded." : "The requested tags have been removed.";
        return message + "\n  " + guiTasks.get(index);
    }

    private String findGuiTag(String text) throws EsException {
        String rawTag = text.substring("findtag".length()).trim();
        if (rawTag.isEmpty()) {
            throw new EsException("... give me something to work with.");
        }
        String tag = normalizeCommandTag(rawTag);
        StringBuilder result = new StringBuilder("These entries bear that tag:");
        for (int i = 0; i < guiTasks.size(); i++) {
            if (guiTasks.get(i).getTags().contains(tag)) {
                result.append("\n").append(i + 1).append(".").append(guiTasks.get(i));
            }
        }
        if (result.toString().equals("These entries bear that tag:")) {
            return "No entries bear that tag.";
        }
        return result.toString();
    }

    private static String normalizeCommandTag(String tag) throws EsException {
        String normalized = tag.startsWith("#") ? tag : "#" + tag;
        if (!normalized.matches("#[^\\s|]+")) {
            throw new EsException("... that tag will not do.");
        }
        return normalized;
    }
    public static void main(String[] args) {
        Es chatbot = new Es();
        Ui ui = new Ui();
        ui.showWelcome();
        String input;
        while ((input = ui.readCommand()) != null) {
            String response = chatbot.getResponse(input);
            ui.showLine();
            ui.show(response);
            ui.showLine();
            if (input.trim().equalsIgnoreCase("bye")) {
                break;
            }
        }
    }

    /** Legacy console implementation retained while the shared path is exercised. */
    private static void legacyMain(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        Storage storage = new Storage();
        TaskList tasks = new TaskList(loadTasks(storage));
            String input;
        while ((input = ui.readCommand()) != null) {
            if (!input.equals(input.trim()) || input.matches(".*\\s{2,}.*")) {
                ui.showLine();
                ui.showError("Perhaps you added an extra space somewhere?");
                ui.showLine();
                continue;
            }
            Command command = Parser.parse(input);
            CommandAction action = Parser.parseAction(input);
            if (action != null && action.isExit()) {
                action.execute();
                break;
            }

            ui.showLine();
            try {
                if (command == null) {
                    if (input.isEmpty()) {
                        throw new EsException("You are silent. Give me something to work with.");
                    }
                        throw new EsException("... I didn't expect you to say that.");
                }

                switch (command) {
                case LIST:
                    ui.show("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        ui.show((i + 1) + "." + tasks.get(i));
                    }
                    break;
                case FIND:
                    findTasks(tasks, input.substring(command.name().length()).trim(), ui);
                    break;
                case FINDTAG:
                    findTags(tasks, input.substring(command.name().length()).trim(), ui);
                    break;
                case TAG:
                    updateTags(storage, tasks, input, true, ui);
                    break;
                case UNTAG:
                    updateTags(storage, tasks, input, false, ui);
                    break;
                case MARK:
                    updateTaskStatus(storage, tasks, input, true);
                    break;
                case UNMARK:
                    updateTaskStatus(storage, tasks, input, false);
                    break;
                case DELETE:
                    deleteTask(storage, tasks, input);
                    break;
                case TODO:
                    String description = input.substring(command.name().length()).trim();
                    if (description.isEmpty()) {
                        throw new EsException("... give me something to work with.");
                    }
                    Todo todo = new Todo(description.replaceAll("(?:^|\\s)#[^\\s|]+", "").trim());
                    addInlineTags(todo, description);
                    addTask(storage, tasks, todo);
                    break;
                case DEADLINE:
                    addDeadline(storage, tasks, input.substring(command.name().length()).trim());
                    break;
                case EVENT:
                    addEvent(storage, tasks, input.substring(command.name().length()).trim());
                    break;
                default:
                    throw new EsException("... I didn't expect you to say that.");
                }
            } catch (EsException e) {
                ui.showError(e.getMessage());
            } catch (RuntimeException e) {
                ui.showError("I could not make sense of that command.");
            }
            ui.showLine();
        }

        ui.showLine();
        ui.show("Until next time.");
        ui.showLine();
    }

    /**
     * Loads tasks from disk, or starts with an empty list if none exist yet.
     * A corrupted or unreadable file is reported and replaced with an empty list.
     *
     * @param storage the file used to persist tasks
     * @return the loaded tasks, or an empty list when loading fails
     */
    private static ArrayList<Task> loadTasks(Storage storage) {
        try {
            return storage.load();
        } catch (EsException e) {
            System.out.println(INDENT + "____________________________________________________________");
            System.out.println(INDENT + "OOPS!!! " + e.getMessage());
            System.out.println(INDENT + "____________________________________________________________");
            return new ArrayList<>();
        }
    }

    /**
     * Adds a task to the list, prints a confirmation, and saves the updated list.
     *
     * @param storage the file used to persist tasks
     * @param tasks the task list
     * @param task the task to add
     */
    private static void addTask(Storage storage, TaskList tasks, Task task) throws EsException {
        if (task == null) {
            throw new EsException("... I could not create that task.");
        }
        if (tasks.asList().stream().anyMatch(existing -> existing.toString().equals(task.toString()))) {
            throw new EsException("That task is already in the collection.");
        }
        tasks.add(task);
        printAddedTask(task, tasks.size());
        storage.save(tasks.asList());
    }

    /**
     * Validates and adds a deadline task from its details after the command word.
     *
     * @param storage the file used to persist tasks
     * @param tasks the task list
     * @param details the deadline description and /by value
     */
    private static void addDeadline(Storage storage, TaskList tasks, String details) throws EsException {
        int byIndex = details.indexOf("/by ");
        if (byIndex < 0 && details.endsWith("/by")) {
            byIndex = details.length() - 3;
        }
        if (byIndex < 0) {
            throw new EsException("A deadline needs /by followed by a date or time.");
        }

        String description = details.substring(0, byIndex).trim();
        String by = details.substring(byIndex + 3).trim();
        if (description.isEmpty()) {
            throw new EsException("... a deadline still needs a description.");
        } else if (by.isEmpty()) {
            throw new EsException("Specify when it is due.");
        } else {
            addTask(storage, tasks, new Deadline(description, by));
        }
    }

    /**
     * Validates and adds an event task from its details after the command word.
     *
     * @param storage the file used to persist tasks
     * @param tasks the task list
     * @param details the event description, /from value, and /to value
     */
    private static void addEvent(Storage storage, TaskList tasks, String details) throws EsException {
        int fromIndex = details.indexOf("/from ");
        int toIndex = details.indexOf("/to ");
        if (toIndex < 0 && details.endsWith("/to")) {
            toIndex = details.length() - 3;
        }
        if (fromIndex < 0 || toIndex < 0) {
            throw new EsException("An event needs /from and /to times.");
        }
        if (toIndex < fromIndex) {
            throw new EsException("The event cannot end before it begins.");
        }

        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + 5, toIndex).trim();
        String to = details.substring(toIndex + 3).trim();
        if (description.isEmpty()) {
            throw new EsException("... an event still needs a description.");
        } else if (from.isEmpty()) {
            throw new EsException("... specify when the event begins.");
        } else if (to.isEmpty()) {
            throw new EsException("... specify when the event ends.");
        } else {
            addTask(storage, tasks, new Event(description, from, to));
        }
    }

    /**
     * Marks or unmarks a task after validating its one-based task number.
     *
     * @param storage the file used to persist tasks
     * @param tasks the task list
     * @param command the complete mark or unmark command
     * @param shouldMark whether the task should be marked done
     */
    private static void updateTaskStatus(Storage storage, TaskList tasks, String command, boolean shouldMark)
            throws EsException {
        String action = shouldMark ? "mark" : "unmark";
        String numberText = command.substring(action.length()).trim();
        if (numberText.isEmpty()) {
            throw new EsException("Give me a task number to " + action + ".");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new EsException("The task number to " + action + " must be positive.");
        }

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new EsException("There is no task with that number.");
        }

        Task task = tasks.get(taskNumber - 1);
        if (shouldMark) {
            task.markAsDone();
            System.out.println(INDENT + "It is now marked as complete.");
        } else {
            task.markAsNotDone();
            System.out.println(INDENT + "The task has been returned to your list of unfinished matters.");
        }
        System.out.println(INDENT + "  " + task);
        storage.save(tasks.asList());
    }

    /**
     * Removes a task after validating its one-based task number.
     *
     * @param storage the file used to persist tasks
     * @param tasks the task list
     * @param command the complete delete command
     * @throws EsException if the command has no valid task number
     */
    private static void deleteTask(Storage storage, TaskList tasks, String command) throws EsException {
        String numberText = command.substring("delete".length()).trim();
        if (numberText.isEmpty()) {
            throw new EsException("Give me a task number to delete.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new EsException("The task number to delete must be positive.");
        }

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new EsException("There is no task with that number.");
        }

        Task removedTask = tasks.remove(taskNumber - 1);

        System.out.println(INDENT + "It has been removed.");
        System.out.println(INDENT + "  " + removedTask);
        System.out.println(INDENT + "Now you have " + tasks.size() + " tasks in the list.");
        storage.save(tasks.asList());
    }

    /**
     * Displays the confirmation shown after adding a task.
     *
     * @param task the task that was added
     * @param taskCount the total number of tasks after adding it
     */
    private static void printAddedTask(Task task, int taskCount) {
        System.out.println(INDENT + "Got it. I've added this task:");
        System.out.println(INDENT + "  " + task);
        System.out.println(INDENT + "Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays tasks whose descriptions contain the requested keyword. */
    private static void findTasks(TaskList tasks, String keyword, Ui ui) throws EsException {
        if (keyword.isEmpty()) {
            throw new EsException("... give me something to work with.");
        }
        ui.show("These entries match your search:");
        for (int index : tasks.find(keyword)) {
            ui.show((index + 1) + "." + tasks.get(index));
        }
    }

    private static void addInlineTags(Task task, String text) throws EsException {
        for (String token : text.split("\\s+")) {
            if (token.startsWith("#")) {
                task.addTag(token);
            }
        }
    }

    private static void updateTags(Storage storage, TaskList tasks, String input, boolean adding, Ui ui)
            throws EsException {
        String[] parts = input.split("\\s+");
        if (parts.length < 3) {
            throw new EsException("Give me something to work with.");
        }
        int index = Integer.parseInt(parts[1]) - 1;
        if (index < 0 || index >= tasks.size()) {
            throw new EsException("There is no task with that number.");
        }
        for (int i = 2; i < parts.length; i++) {
            String tag = parts[i].startsWith("#") ? parts[i] : "#" + parts[i];
            if (adding) {
                tasks.get(index).addTag(tag);
            } else {
                tasks.get(index).removeTag(tag);
            }
        }
        storage.save(tasks.asList());
        ui.show(adding ? "The requested tags have been recorded." : "The requested tags have been removed.");
        ui.show("  " + tasks.get(index));
    }

    private static void findTags(TaskList tasks, String tagText, Ui ui) throws EsException {
        if (tagText.isEmpty()) {
            throw new EsException("Give me something to work with.");
        }
        String tag = tagText.startsWith("#") ? tagText : "#" + tagText;
        ui.show("These entries bear that tag:");
        for (int index = 0; index < tasks.size(); index++) {
            if (tasks.get(index).getTags().contains(tag)) {
                ui.show((index + 1) + "." + tasks.get(index));
            }
        }
    }
}
