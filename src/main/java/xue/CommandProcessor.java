package xue;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import xue.model.Task;
import xue.model.TaskList;
import xue.storage.Storage;

/** Processes one Xue command and returns the user-facing response. */
public class CommandProcessor {
    private static final String DEADLINE_MARKER = "/by";
    private static final String EVENT_FROM_MARKER = "/from";
    private static final String EVENT_TO_MARKER = "/to";
    private final TaskList tasks;
    private final Storage storage;
    private final String loadError;
    private static final int MAX_HISTORY = 50;
    private final Deque<HistoryEntry> undoHistory = new ArrayDeque<>();
    private final Deque<HistoryEntry> redoHistory = new ArrayDeque<>();

    /** Creates a processor and restores tasks saved on disk. */
    public CommandProcessor(Storage storage) {
        this.storage = storage;
        List<Task> savedTasks;
        String loadingError;
        try {
            savedTasks = storage.load();
            loadingError = storage.getLoadWarning();
        } catch (XueException e) {
            savedTasks = List.of();
            loadingError = e.getMessage();
        }
        tasks = new TaskList(savedTasks);
        loadError = loadingError;
    }

    /** Returns a storage-load error or warning, or null when all records loaded successfully. */
    public String getLoadError() {
        return loadError;
    }

    /** Processes a command and returns its response, or an error response. */
    public String process(String command) {
        if (command == null) {
            return "";
        }
        command = command.trim();
        try {
            if (command.equals("bye")) {
                return "Finally, you're leaving. Bye. Don't make me miss you.";
            } else if (command.equals("list")) {
                return "Here are your tasks. Yes, I did all the work for you:\n" + formatTasks(tasks.asList());
            } else if (command.equals("undo") || command.startsWith("undo ")) {
                return undo(command);
            } else if (command.equals("redo") || command.startsWith("redo ")) {
                return redo(command);
            } else if (command.equals("find") || command.startsWith("find ")) {
                String keyword = command.substring("find".length()).trim();
                if (keyword.isEmpty()) {
                    throw new XueException("Tell me what to find. I am not a mind reader.");
                }
                return "Here are the matching tasks in your list:\n" + formatTasks(tasks.find(keyword));
            } else if (command.equals("mark") || command.startsWith("mark ")) {
                return markTask(command, true);
            } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                return markTask(command, false);
            } else if (command.equals("delete") || command.startsWith("delete ")) {
                return deleteTask(command);
            } else if (command.equals("todo") || command.startsWith("todo ")) {
                return addTask(new Task(command.substring("todo".length()).trim()));
            } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                String[] parts = splitDateCommand(command, "deadline", DEADLINE_MARKER);
                return addTask(new Task("D", parts[0], null, parts[1]));
            } else if (command.equals("event") || command.startsWith("event ")) {
                String[] parts = splitEventCommand(command);
                return addTask(new Task("E", parts[0], parts[1], parts[2]));
            }
            throw new XueException("I don't know what that means. Use a proper command next time.");
        } catch (XueException e) {
            return "OOPS!!! " + e.getMessage();
        }
    }

    private String markTask(String command, boolean shouldMark) {
        String action = shouldMark ? "mark" : "unmark";
        int index = getTaskIndex(command, action);
        List<Task> before = snapshot();
        if (shouldMark) {
            tasks.get(index).markAsDone();
        } else {
            tasks.get(index).markAsNotDone();
        }
        saveMutation(before);
        String response = shouldMark
                ? "Fine, I've marked this task as done. Happy now?\n  [X] "
                : "There. I've undone it. Try to make up your mind next time:\n  [ ] ";
        return response + tasks.get(index).getDescription();
    }

    private String deleteTask(String command) {
        int index = getTaskIndex(command, "delete");
        List<Task> before = snapshot();
        Task deletedTask = tasks.delete(index);
        saveMutation(before);
        return "Fine, I've removed this task:\n  [" + deletedTask.getType() + "]["
                + deletedTask.getStatusIcon() + "] " + deletedTask.getDescription()
                + deletedTask.getDateTimeDescription() + "\nNow you have " + tasks.size()
                + " tasks in the list.";
    }

    private String addTask(Task task) {
        List<Task> before = snapshot();
        tasks.add(task);
        saveMutation(before);
        return "Got it. I've added this task: [" + task.getType() + "][ ] " + task.getDescription()
                + task.getDateTimeDescription() + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    private String formatTasks(List<Task> taskList) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);
            result.append(i + 1).append(".[").append(task.getType()).append("][")
                    .append(task.getStatusIcon()).append("] ").append(task.getDescription())
                    .append(task.getDateTimeDescription());
            if (i < taskList.size() - 1) {
                result.append('\n');
            }
        }
        return result.toString();
    }

    private int getTaskIndex(String command, String action) {
        String taskNumber = command.substring(action.length()).trim();
        if (taskNumber.isEmpty()) {
            throw new XueException("Tell me which task to " + action + ". I am not a mind reader.");
        }
        if (!taskNumber.matches("\\d+")) {
            throw new XueException("That is not a valid task number. Numbers are not that complicated.");
        }
        try {
            int index = Integer.parseInt(taskNumber) - 1;
            if (index < 0 || index >= tasks.size()) {
                throw new XueException("That task number does not exist. Did you just invent it?");
            }
            // The range check above is the contract required by every TaskList access here.
            assert index >= 0 && index < tasks.size();
            return index;
        } catch (NumberFormatException e) {
            throw new XueException("That is not a valid task number. Numbers are not that complicated.");
        }
    }

    private String[] splitDateCommand(String command, String keyword, String marker) {
        String body = command.substring(keyword.length()).trim();
        int markerIndex = body.indexOf(marker);
        if (markerIndex < 0) {
            throw new XueException("A deadline needs a /by date. Please do the minimum.");
        }
        String[] parts = new String[] {body.substring(0, markerIndex).trim(),
            body.substring(markerIndex + marker.length()).trim()};
        // The split always returns exactly a description and a deadline for a valid marker.
        assert parts.length == 2;
        return parts;
    }

    private String[] splitEventCommand(String command) {
        String body = command.substring("event".length()).trim();
        int fromIndex = body.indexOf(EVENT_FROM_MARKER);
        int toIndex = body.indexOf(EVENT_TO_MARKER, fromIndex + EVENT_FROM_MARKER.length());
        if (fromIndex < 0 || toIndex < 0) {
            throw new XueException("An event needs /from and /to times. I cannot guess your schedule.");
        }
        return new String[] {body.substring(0, fromIndex).trim(),
            body.substring(fromIndex + EVENT_FROM_MARKER.length(), toIndex).trim(),
            body.substring(toIndex + EVENT_TO_MARKER.length()).trim()};
    }

    private String undo(String command) {
        validateHistoryCommand(command, "undo");
        if (undoHistory.isEmpty()) {
            throw new XueException("Hey you need to DO before you can undo!");
        }
        HistoryEntry entry = undoHistory.removeLast();
        List<Task> current = snapshot();
        try {
            tasks.replaceWith(entry.before());
            storage.save(tasks.asList());
        } catch (XueException e) {
            tasks.replaceWith(current);
            undoHistory.addLast(entry);
            throw e;
        }
        pushHistory(redoHistory, entry);
        return "Undo completed. Be alert next time.\n"
                + "Here are your tasks. Yes, I did all the work for you:\n" + formatTasks(tasks.asList());
    }

    private String redo(String command) {
        validateHistoryCommand(command, "redo");
        if (redoHistory.isEmpty()) {
            throw new XueException("There is nothing to redo.");
        }
        HistoryEntry entry = redoHistory.removeLast();
        List<Task> current = snapshot();
        try {
            tasks.replaceWith(entry.after());
            storage.save(tasks.asList());
        } catch (XueException e) {
            tasks.replaceWith(current);
            redoHistory.addLast(entry);
            throw e;
        }
        pushHistory(undoHistory, entry);
        return "Redo completed.\nHere are your tasks. Yes, I did all the work for you:\n"
                + formatTasks(tasks.asList());
    }

    private void validateHistoryCommand(String command, String action) {
        if (!command.equals(action)) {
            throw new XueException("The " + action + " command does not accept any arguments.");
        }
    }

    private List<Task> snapshot() {
        List<Task> copy = new java.util.ArrayList<>();
        for (Task task : tasks.asList()) {
            copy.add(new Task(task));
        }
        return copy;
    }

    private void saveMutation(List<Task> before) {
        List<Task> after = snapshot();
        try {
            storage.save(tasks.asList());
        } catch (XueException e) {
            tasks.replaceWith(before);
            throw e;
        }
        pushHistory(undoHistory, new HistoryEntry(before, after));
        redoHistory.clear();
    }

    private void pushHistory(Deque<HistoryEntry> history, HistoryEntry entry) {
        history.addLast(entry);
        while (history.size() > MAX_HISTORY) {
            history.removeFirst();
        }
    }

    private record HistoryEntry(List<Task> before, List<Task> after) { }
}
