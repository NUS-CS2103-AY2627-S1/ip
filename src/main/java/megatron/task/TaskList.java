package megatron.task;
import java.util.ArrayList;
import java.util.stream.Collectors;

/** Owns the collection of tasks and its basic operations. */
public class TaskList {
    private final ArrayList<Task> tasks;
    /** Creates a task list backed by the supplied tasks. */
    public TaskList(ArrayList<Task> tasks) { this.tasks = tasks; }
    /** Creates an empty task list. */
    public TaskList() { this(new ArrayList<>()); }
    /** Adds a task to the list. */
    public void add(Task task) {
        boolean duplicate = tasks.stream().anyMatch(existing -> existing.toString().equals(task.toString()));
        if (duplicate) {
            throw new IllegalArgumentException("a task with the same details already exists");
        }
        tasks.add(task);
    }
    /** Returns the task at the zero-based index. */
    public Task get(int index) { return tasks.get(index); }
    /** Removes and returns the task at the zero-based index. */
    public Task delete(int index) { return tasks.remove(index); }
    /** Returns the number of tasks. */
    public int size() { return tasks.size(); }
    /** Returns the underlying list for persistence and display. */
    public ArrayList<Task> asList() { return tasks; }
    /** Returns tasks whose descriptions contain the keyword, ignoring case. */
    public ArrayList<Task> find(String keyword) {
        String lowercaseKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowercaseKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
