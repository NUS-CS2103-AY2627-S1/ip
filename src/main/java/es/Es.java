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
            return error("Perhaps you added an extra space somewhere?");
        }
        String commandText = input == null ? "" : input.trim();
        if (commandText.isEmpty()) {
            return "You are silent. Give me something to work with.";
        }
        String conversationResponse = getConversationResponse(commandText);
        if (conversationResponse != null) {
            return conversationResponse;
        }
        Command command = Parser.parse(commandText);
        try {
            if (command == null) {
                throw new EsException("... I didn't expect you to say that.");
            }
            return handleCommand(command, commandText);
        } catch (EsException e) {
            return error(e.getMessage());
        } catch (RuntimeException e) {
            return error("I could not make sense of that command.");
        }
    }

    private String error(String message) {
        lastResponseWasError = true;
        return message;
    }

    private String getConversationResponse(String commandText) {
        String conversation = commandText.replaceFirst("\\?$", "").trim().toLowerCase();
        switch (conversation) {
        case "hi":
        case "hello":
            return "Pleasure to make your acquaintance.";
        case "who are you":
            return "I am the librarian of Somniareves. You can call me Es.";
        case "what are you":
            return "...";
        case "where is this place":
            return "... I don't know. Perhaps we can figure it out together.";
        default:
            return null;
        }
    }

    private String handleCommand(Command command, String commandText) throws EsException {
        switch (command) {
        case TODO:
            return handleTodo(commandText);
        case DEADLINE:
            addGuiDeadline(commandText.substring(8).trim());
            return addGuiTaskMessage(guiTasks.get(guiTasks.size() - 1));
        case EVENT:
            addGuiEvent(commandText.substring(5).trim());
            return addGuiTaskMessage(guiTasks.get(guiTasks.size() - 1));
        case LIST:
            return listTasks();
        case BYE:
            return "Until next time.";
        case MARK:
            return toggleGui(commandText, true);
        case UNMARK:
            return toggleGui(commandText, false);
        case DELETE:
            return deleteGui(commandText);
        case FIND:
            return findTasks(commandText);
        case TAG:
            return updateGuiTags(commandText, true);
        case UNTAG:
            return updateGuiTags(commandText, false);
        case FINDTAG:
            return findGuiTag(commandText);
        default:
            throw new EsException("... I didn't expect you to say that.");
        }
    }

    private String handleTodo(String commandText) throws EsException {
        String description = commandText.substring(4).trim();
        if (description.isEmpty()) {
            throw new EsException("... give me something to work with.");
        }
        Todo todo = new Todo(description.replaceAll("(?:^|\\s)#[^\\s|]+", "").trim());
        addInlineTags(todo, description);
        return addGuiTask(todo);
    }

    private String listTasks() {
        if (guiTasks.size() == 0) {
            return "The shelves are empty for now.";
        }
        StringBuilder result = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < guiTasks.size(); i++) {
            result.append("\n").append(i + 1).append(".").append(guiTasks.get(i));
        }
        return result.toString();
    }

    private String findTasks(String commandText) throws EsException {
        String keyword = commandText.substring(4).trim();
        if (keyword.isEmpty()) {
            throw new EsException("... give me something to work with.");
        }
        ArrayList<Integer> matchingIndexes = guiTasks.find(keyword);
        if (matchingIndexes.isEmpty()) {
            return "Nothing in the collection matches that search.";
        }
        StringBuilder matches = new StringBuilder("These entries match your search:");
        for (int i : matchingIndexes) {
            matches.append("\n").append(i + 1).append(".").append(guiTasks.get(i));
        }
        return matches.toString();
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
                return "That task is already unmarked.";
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

    private static void addInlineTags(Task task, String text) throws EsException {
        for (String token : text.split("\\s+")) {
            if (token.startsWith("#")) {
                task.addTag(token);
            }
        }
    }

}
