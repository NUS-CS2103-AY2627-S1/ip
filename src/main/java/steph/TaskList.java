package steph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import steph.task.Event;
import steph.task.Task;

/**
 * The list of tasks Steph is tracking, together with the operations the
 * commands need: adding, removing, retrieving by position, and reporting the
 * count.
 *
 * <p>This class holds the tasks and nothing else -- no input/output and no
 * command parsing -- so it can be created and exercised in a test without a
 * console or a save file.
 */
public class TaskList {

    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list holding the given tasks, e.g. the ones just read
     * from the save file. The tasks are copied into a new list, so later
     * changes here do not affect the list that was passed in.
     *
     * @param initialTasks The tasks to start with.
     */
    public TaskList(List<Task> initialTasks) {
        assert initialTasks != null : "Storage#load always returns a list, empty at worst, never null";
        this.tasks = new ArrayList<>(initialTasks);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns the task at a 0-based position. The caller is expected to have
     * already validated the index (see Steph's task-number parsing).
     *
     * @param index 0-based position in the list.
     * @return The task at that position.
     */
    public Task get(int index) {
        assert index >= 0 && index < this.tasks.size()
                : "index must already be validated by the caller (see Steph's task-number parsing)";
        return this.tasks.get(index);
    }

    /**
     * Appends a task to the end of the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        this.tasks.add(task);
    }

    /**
     * Removes the task at a 0-based position and returns it.
     *
     * @param index 0-based position in the list.
     * @return The task that was removed.
     */
    public Task remove(int index) {
        assert index >= 0 && index < this.tasks.size()
                : "index must already be validated by the caller (see Steph's task-number parsing)";
        return this.tasks.remove(index);
    }

    /**
     * Returns a read-only view of the tasks, in order, for callers that need
     * the whole list at once (e.g. Storage when writing the save file).
     *
     * @return An unmodifiable list of the tasks.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(this.tasks);
    }

    /**
     * Returns the tasks whose name contains {@code keyword}, matched
     * case-insensitively and kept in their current order.
     *
     * <p>The result is a fresh snapshot: it is unmodifiable, and later changes
     * to this list are not reflected in it. An empty list means nothing matched.
     *
     * @param keyword the text to look for within each task's name
     * @return the matching tasks, in list order
     */
    public List<Task> findMatch(String keyword) {
        String loweredKeyword = keyword.toLowerCase();
        return this.tasks.stream()
                .filter(task -> task.getName().toLowerCase().contains(loweredKeyword))
                .toList();
    }

    /**
     * Returns the not-done Events already in this list whose span overlaps
     * {@code candidate}'s, in list order. ToDo and Deadline tasks, and any
     * Event already marked done, are never included.
     *
     * @param candidate The event being considered for addition.
     * @return The clashing events, in list order; empty if none clash.
     */
    public List<Event> findClashingEvents(Event candidate) {
        return this.tasks.stream()
                .filter(task -> task instanceof Event)
                .map(task -> (Event) task)
                .filter(event -> !event.isDone())
                .filter(event -> event.clashesWith(candidate))
                .toList();
    }

    /**
     * Adds a parsed event, unless it clashes with an existing not-done event
     * and wasn't forced -- in which case it is rejected and nothing is added.
     *
     * @param parsedEvent The event to add and whether "/force" was given.
     * @return The confirmation message.
     * @throws StephException If it clashes with an existing event and isn't forced.
     */
    public String addEvent(ParsedEvent parsedEvent) throws StephException {
        Event event = parsedEvent.event();
        if (!parsedEvent.isForce()) {
            List<Event> clashes = findClashingEvents(event);
            if (!clashes.isEmpty()) {
                throw new StephException(buildClashMessage(clashes));
            }
        }
        this.tasks.add(event);
        return "Got it. I've added this task:\n  " + event
                + "\nNow you have " + this.tasks.size() + " tasks in the list.";
    }

    /**
     * Builds the rejection message listing every event a new event clashes
     * with, and how to override the rejection.
     *
     * @param clashes The clashing events, in list order.
     * @return The assembled multi-line message.
     */
    private static String buildClashMessage(List<Event> clashes) {
        StringBuilder message = new StringBuilder("This clashes with an existing event:");
        for (Event clash : clashes) {
            message.append("\n  ").append(clash);
        }
        message.append("\nAdd '/force' to the command if you want to schedule it anyway.");
        return message.toString();
    }
}
