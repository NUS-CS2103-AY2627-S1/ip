package janet;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * An ordered unmodifiable collection of <code>Task</code> elements.
 */
public class TaskList {

    protected static final int DEFAULT_ORDER = 0;
    // default is based on entry order
    protected static final Comparator<Task> DEFAULT_COMPARATOR = (a, b) -> TaskList.DEFAULT_ORDER;
    protected static final Comparator<Task> LABEL_COMPARATOR = (a, b) -> a.compareTaskLabel(b);

    private static final String DEFAULT_COMPARATOR_KEY = "default";
    private final List<Task> tasks;
    private final Comparator<Task> currComparator;

    /**
     * Represents the return value of all <code>TaskList</code> operations.
     * A <code>CommandResult</code> corresponds to the new TaskList (if any) and string representation of the operation.
     *
     * @param updatedTaskList
     * @param message
     */
    public record CommandResult(Optional<TaskList> updatedTaskList, String message) {
    }

    /**
     * Creates an empty TaskList.
     */
    public TaskList() {
        this.tasks = List.of();
        this.currComparator = TaskList.DEFAULT_COMPARATOR;
    }

    /**
     * Creates an TaskList using the provided <code>List</code> of tasks.
     */
    public TaskList(List<Task> tasks, Comparator<Task> comparator) {
        this.tasks = tasks;
        this.currComparator = comparator;
    }

    /**
     * Returns a new <code>CommandResult</code>.
     * The <code>Task</code> is appended to the previous <code>TaskList</code>.
     *
     * @param task <code>Task</code> to be appended.
     * @return A <code>CommandResult</code> containing an updated <code>TaskList</code> and operation message.
     * @throws JanetException If task already exists.
     */
    public CommandResult addTask(Task task) throws JanetException {
        List<Task> matchingTasks = this.getMatchingTasks(task);
        if (!matchingTasks.isEmpty()) {
            throw new JanetException(
                    String.format(
                            "Duplicate tasks detected! Already existing match(es): %s",
                            matchingTasks.toString()
                    )
            );
        }

        List<Task> updatedTaskList = Stream.concat(
                        this.tasks.stream(),
                        Stream.of(task)
                )
                .toList();
        return new CommandResult(
                Optional.of(new TaskList(updatedTaskList, this.currComparator)), String.format("%s added!\n", task)
        );
    }

    private List<Task> getMatchingTasks(Task task) {
        return this.tasks
                .stream()
                .filter((x) -> x.equals(task))
                .toList();
    }

    /**
     * Deletes the task at the specified position and returns an updated <code>TaskList</code>
     *
     * @param pos The one-based position of the task to delete.
     * @return A <code>CommandResult</code> containing the updated <code>TaskList</code> and operation message.
     * @throws JanetException if the specified position is outside the {@code TaskList}.
     */
    public CommandResult deleteTask(int pos) throws JanetException {
        if (this.isOutOfIndex(pos)) {
            throw new JanetException(
                    String.format("Deletion out of index!")
            );
        }
        assert pos >= 1 && pos <= this.tasks.size() : "Validated position must be safe to convert to an index";
        TaskList retTaskList = new TaskList(
                IntStream.range(0, this.tasks.size())
                        .filter(i -> i != pos - 1)
                        .mapToObj(i -> this.tasks.get(i))
                        .toList(),
                this.currComparator
        );

        return new CommandResult(Optional.of(retTaskList), String.format("%d deleted!", pos));
    }

    /**
     * Lists all tasks currently stored in the <code>TaskList</code>.
     *
     * @return A <code>CommandResult</code> containing the current <code>TaskList</code> and formatted list.
     */
    public CommandResult listTasks() {
        List<Task> sortedTasks = this.tasks.stream().sorted(this.currComparator).toList();
        Optional<String> retString = IntStream.range(1, sortedTasks.size() + 1)
                .mapToObj(i -> Integer.toString(i) + ". " + sortedTasks.get(i - 1))
                .reduce((a, b) -> a + "\n" + b);

        return new CommandResult(
                Optional.of(this), retString.orElse("No tasks listed!")
        );
    }

    /**
     * Lists all tasks currently stored in the <code>TaskList</code> and matching the provided searchTerm
     * Matches occur when the searchTerm is a substring of the task label.
     *
     * @return A <code>CommandResult</code> containing the current <code>TaskList</code> and formatted result.
     */
    public CommandResult findTasks(String searchTerm) {
        Optional<String> retString = IntStream.range(1, this.tasks.size() + 1)
                .mapToObj(i -> Integer.toString(i) + ". " + this.tasks.get(i - 1))
                .filter(x -> x.contains(searchTerm))
                .reduce((a, b) -> a + "\n" + b);

        return new CommandResult(
                Optional.of(this),
                retString.map(x -> "Here are your matching tasks!\n" + x)
                        .orElse("No tasks found!")
        );
    }

    /**
     * Marks the task at the specified position as done.
     *
     * @return A <code>CommandResult</code> containing the updated <code>TaskList</code> and operation message.
     * @throws JanetException If the specified position is outside the <code>TaskList</code>.
     */
    public CommandResult markTask(int pos) throws JanetException {
        if (this.isOutOfIndex(pos)) {
            throw new JanetException(
                    String.format("No mark index ")
            );
        }
        assert pos >= 1 && pos <= this.tasks.size() : "Validated position must be safe to convert to an index";
        List<Task> front = this.tasks.subList(0, pos - 1);
        List<Task> back = this.tasks.subList(pos, this.tasks.size());
        List<Task> markedTask = List.of(
                this.tasks
                        .get(pos - 1)
                        .markDone()
        );

        TaskList retTaskList = new TaskList(Stream.of(front, markedTask, back)
                .flatMap(x -> x.stream())
                .toList(),
                this.currComparator
        );

        return new CommandResult(Optional.of(retTaskList), String.format("%d marked!\n", pos));
    }

    public CommandResult setTaskListComparator(Comparator<Task> comparator) throws JanetException {
        TaskList tasklist = new TaskList(this.tasks, comparator);
        return new CommandResult(Optional.of(tasklist), "Set sort!");
    }

    private boolean isOutOfIndex(int pos) {
        return (pos < 1) || (pos > this.tasks.size());
    }

    // also used as file format
    @Override
    public String toString() {
        return this.tasks.stream()
                .map(x -> x.toFileFormat())
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
    }
}
