package allmind.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import allmind.parser.InvalidCommandException;

/** Owns the application's collection of tasks and its task operations. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Describes an unfinished event that overlaps a newly added event. */
    public record EventConflict(int taskNumber, String description, LocalDate overlapStart,
            LocalDate overlapEnd) {
    }

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /** Creates a task list containing the supplied tasks. */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "A task list must have a backing collection";
        assert !tasks.contains(null) : "A task list cannot contain null tasks";
        this.tasks = tasks;
    }

    /** Returns a read-only view of the tasks. */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /** Adds a task to the list. */
    public void add(Task task) {
        assert task != null : "A task list cannot contain null tasks";
        tasks.add(task);
    }

    /** Returns all unfinished events that overlap the supplied event, in list order. */
    public List<EventConflict> findEventConflicts(Event newEvent) {
        assert newEvent != null : "The event to check must not be null";

        List<EventConflict> conflicts = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (!(task instanceof Event existingEvent) || existingEvent.isDone()) {
                continue;
            }

            LocalDate overlapStart = existingEvent.getFrom().isAfter(newEvent.getFrom())
                    ? existingEvent.getFrom() : newEvent.getFrom();
            LocalDate overlapEnd = existingEvent.getTo().isBefore(newEvent.getTo())
                    ? existingEvent.getTo() : newEvent.getTo();
            if (!overlapStart.isAfter(overlapEnd)) {
                conflicts.add(new EventConflict(i + 1, existingEvent.getDescription(),
                        overlapStart, overlapEnd));
            }
        }
        return conflicts;
    }

    /** Prints all tasks with their one-based list numbers. */
    public void printTasks() {
        System.out.println("The tasks in your list are as follows:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /** Prints tasks whose descriptions contain the keyword, ignoring letter case. */
    public void findTasks(String command) throws InvalidCommandException {
        String keyword = command.substring("find ".length()).trim();
        if (keyword.isEmpty()) {
            throw new InvalidCommandException("Please provide a keyword to search for.");
        }

        String lowercaseKeyword = keyword.toLowerCase(Locale.ROOT);
        System.out.println("Here are the matching tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(lowercaseKeyword)) {
                System.out.println((i + 1) + "." + task);
            }
        }
    }

    /** Marks the task selected by the command as done. */
    public void markTask(String command) throws InvalidCommandException {
        updateTaskStatus(command, true);
    }

    /** Marks the task selected by the command as not done. */
    public void unmarkTask(String command) throws InvalidCommandException {
        updateTaskStatus(command, false);
    }

    /** Deletes the task selected by the command. */
    public void deleteTask(String command) throws InvalidCommandException {
        try {
            int taskNumber = Integer.parseInt(command.substring("delete ".length()).trim());
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                System.out.println("That task number does not exist.");
                return;
            }
            Task deletedTask = tasks.remove(taskNumber - 1);
            System.out.println("Noted. I've removed this task:");
            System.out.println("  " + deletedTask);
            System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Please provide a valid task number.");
        }
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Updates the completion status of the task selected by a mark command. */
    private void updateTaskStatus(String command, boolean isDone) throws InvalidCommandException {
        try {
            String prefix = isDone ? "mark " : "unmark ";
            int taskNumber = Integer.parseInt(command.substring(prefix.length()).trim());
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                System.out.println("That task number does not exist.");
                return;
            }
            Task task = tasks.get(taskNumber - 1);
            if (isDone) {
                task.markAsDone();
                System.out.println("Affirmative. I have marked this task as isDone:");
            } else {
                task.markAsNotDone();
                System.out.println("Affirmative, I have marked this task as not isDone yet:");
            }
            System.out.println(task);
        } catch (NumberFormatException e) {
            throw new InvalidCommandException("Please provide a valid task number.");
        }
    }
}
