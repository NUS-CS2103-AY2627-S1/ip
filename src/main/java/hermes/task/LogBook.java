package hermes.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import hermes.HermesException;

/**
 * Holds the tasks Hermes is keeping track of and the operations that change them.
 *
 * <p>The list in memory is the single source of truth. Every change is handed
 * to {@link Storage} straight away, so the file always matches the list.
 */
public class LogBook {
    private final Storage storage;
    private ArrayList<Task> tasks;
    private Stack<ArrayList<Task>> cachedTasks;

    /**
     * Starts from whatever the given storage already holds.
     *
     * @param storage where tasks are read from and written back to.
     */
    public LogBook(Storage storage) {
        this.storage = storage;
        this.tasks = storage.load();
        this.cachedTasks = new Stack<>();
    }

    /**
     * Returns how many stored records could not be read when these tasks were
     * loaded.
     *
     * <p>Storage counts them, since it is the class that reads the file. This
     * exists so a caller can ask the task list whether it holds everything that
     * was saved, without needing a reference to storage of its own.
     *
     * @return the number of skipped records, zero if everything loaded.
     */
    public int getSkippedLines() {
        return this.storage.getSkippedLines();
    }

    /**
     * Returns whether the data file existed but could not be opened when these
     * tasks were loaded.
     *
     * @return true if the file could not be opened.
     */
    public boolean isFileUnreadable() {
        return this.storage.isFileUnreadable();
    }

    /**
     * Stores an already-built task and reports how many tasks are now held.
     *
     * <p>The caller decides which kind of {@link Task} to create, so this
     * method works unchanged for todos, deadlines and events.
     *
     * @param task the task to store.
     * @return the message confirming the task was added, headed by a warning
     *     when an identical task is listed already.
     * @throws HermesException if the task could not be written to storage.
     */
    public String log(Task task) throws HermesException {
        // The task is still added. A repeated errand may well be meant, so the
        // user is told and left to decide, rather than having it refused.
        boolean isAlreadyListed = this.tasks.stream().anyMatch(listed -> listed.hasSameDetails(task));
        String warning = isAlreadyListed
                ? "Take heed: this task already stands upon thy scroll.\n"
                : "";

        cache();
        this.tasks.add(task);
        this.storage.save(this.tasks);
        return warning + String.format("""
                It is recorded. I have set down this task:
                  %s
                Thy scroll now holds %d task%s.
                """, task, this.tasks.size(), (this.tasks.size() == 1 ? "" : "s"));
    }

    /**
     * Marks every task listed as completed.
     *
     * @param indexes the tasks' positions in the list, counting from zero.
     * @return the message confirming the change.
     * @throws HermesException if the number names no task, or the change could
     *                         not be written to storage.
     */
    public String mark(int... indexes) throws HermesException {
        return applyStatusChange(true, indexes);
    }

    /**
     * Marks every task listed as no longer completed.
     *
     * @param indexes the tasks' positions in the list, counting from zero.
     * @return the message confirming the change.
     * @throws HermesException if the number names no task, or the change could
     *     not be written to storage.
     */
    public String unmark(int... indexes) throws HermesException {
        return applyStatusChange(false, indexes);
    }

    /**
     * Marks every task listed as completed, or as no longer completed.
     *
     * <p>Marking and unmarking differ only in which method of {@link Task} is
     * called and in the wording of the reply, so the work is written here once
     * rather than twice.
     *
     * <p>Tasks that were already in the state asked for are reported apart from
     * those the change touched, so the user can see which of the numbers they
     * gave made no difference.
     *
     * @param isMarking true to mark the tasks, false to unmark them.
     * @param indexes the tasks' positions in the list, counting from zero.
     * @return the message confirming the change.
     * @throws HermesException if the number names no task, or the change could
     *     not be written to storage.
     */
    private String applyStatusChange(boolean isMarking, int... indexes) throws HermesException {
        // Every index is checked before any task is changed. Checking as each
        // task goes would leave the list half changed.
        for (int index : indexes) {
            checkIndex(index);
        }

        cache();

        List<Task> changed = new ArrayList<>();
        List<Task> unchanged = new ArrayList<>();

        for (int index : indexes) {
            Task task = this.tasks.get(index);
            boolean wasChanged = isMarking ? task.mark() : task.unmark();

            if (wasChanged) {
                changed.add(task);
            } else {
                unchanged.add(task);
            }
        }

        this.storage.save(this.tasks);

        String state = isMarking ? "fulfilled" : "unfulfilled";
        String changedMessage = "";
        String unchangedMessage = "";

        if (!changed.isEmpty()) {
            changedMessage = String.format("%s I declare the following task%s as %s:\n  %s\n",
                    isMarking ? "Well done." : "As thou wishest.",
                    changed.size() == 1 ? "" : "s", state,
                    changed.stream().map(Task::toString).collect(Collectors.joining("\n  ")));
        }

        if (!unchanged.isEmpty()) {
            unchangedMessage = String.format("The following task%s %s already %s:\n  %s\n",
                    unchanged.size() == 1 ? "" : "s",
                    unchanged.size() == 1 ? "was" : "were", state,
                    unchanged.stream().map(Task::toString).collect(Collectors.joining("\n  ")));
        }

        // A blank line separates the two groups, but only when both are spoken of.
        boolean hasBothGroups = !changedMessage.isEmpty() && !unchangedMessage.isEmpty();
        return changedMessage + (hasBothGroups ? "\n" : "") + unchangedMessage;
    }

    /**
     * Removes every task named and reports how many are left.
     *
     * <p>All the numbers are checked before any task is removed, so one bad
     * number cancels the whole delete. Removing the tasks that could be
     * understood and only then failing would leave the list half changed and,
     * because the failure comes before the save, disagreeing with the file.
     *
     * @param indexes the tasks' positions in the list, counting from zero, in
     *     any order.
     * @return the message naming what was removed.
     * @throws HermesException if any number names no task, or the shortened
     *     list could not be written to storage.
     */
    public String delete(int... indexes) throws HermesException {
        Arrays.sort(indexes);

        for (int index : indexes) {
            checkIndex(index);
        }

        cache();

        StringBuilder output = new StringBuilder();
        int numberOfTasksRemoved = 0;

        for (int index : indexes) {
            assert (index - numberOfTasksRemoved >= 0) && (index - numberOfTasksRemoved < this.tasks.size())
                    : "Compensated index out of range, indexes must be sorted and distinct";

            Task removed = this.tasks.remove(index - numberOfTasksRemoved);
            numberOfTasksRemoved++;
            output.append(removed).append("\n  ");
        }

        int remaining = this.tasks.size();
        this.storage.save(this.tasks);

        return String.format("""
                It is done. I have guided %s down to the Underworld:
                  %s
                Thy scroll now holds %d task%s.
                """,
                numberOfTasksRemoved == 1 ? "this task" : "these tasks",
                output.toString().trim(), remaining, (remaining == 1 ? "" : "s"));
    }

    /**
     * Lists the outstanding tasks falling due no later than a given moment.
     *
     * @param deadline the moment tasks are measured against.
     * @return the matching tasks, soonest first, or a notice if none match.
     */
    public String listTasksDueBy(LocalDateTime deadline) {
        String output = this.tasks.stream()
                .filter(task -> task.isDueBy(deadline) && !task.isCompleted())
                .sorted(Comparator.comparing(Task::getDueDateTime))
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
        String outIfEmpty = "Nothing is due by " + deadline.format(Task.DISPLAY_FORMATTER) + ".";
        return output.isEmpty() ? outIfEmpty : output;
    }

    /**
     * Reorders the tasks by deadline, soonest first. Tasks without a date follow
     * the dated ones, and completed tasks come last. The new order is written to
     * storage, so it is still in place the next time Hermes starts.
     *
     * @return the reordered tasks, numbered exactly as the list command shows them.
     * @throws HermesException if the reordered tasks could not be written to storage.
     */
    public String sort() throws HermesException {
        if (this.tasks.isEmpty()) {
            return "There is naught to set in order; thy scroll is empty.";
        }

        cache();

        // Comparator.naturalOrder() routes through Task.compareTo, which already
        // defines this ordering, rather than restating it here.
        this.tasks.sort(Comparator.naturalOrder());
        this.storage.save(this.tasks);

        return String.format("Swift as my winged sandals, I have ordered thy tasks, soonest first:%n%s",
                this);
    }

    /**
     * Lists the tasks whose taskDescription contains a given keyword.
     *
     * @param keyword the text to look for, already in lower case.
     * @return the matching tasks, or a notice if none match.
     */
    public String findTasks(String keyword) {
        if (this.tasks.isEmpty()) {
            return "There is naught to search; thy scroll is empty.";
        }

        String output = this.tasks.stream()
                .filter(task -> task.getTaskDescription().toLowerCase().contains(keyword))
                .map(Task::toString)
                .collect(Collectors.joining("\n"));

        String outputIfEmpty = "I have searched from Olympus to the Underworld, yet no task speaks of '"
                + keyword + "'.";

        return output.isEmpty() ? outputIfEmpty : output;
    }

    /**
     * Reverts the list to its previous state by one step.
     *
     * @return the updated tasks after the undo action was performed.
     * @throws HermesException if the previous tasks could not be written to storage.
     */
    public String undo() throws HermesException {
        if (this.cachedTasks.isEmpty()) {
            return "There is naught to undo.";
        }

        this.tasks = cachedTasks.pop();
        this.storage.save(this.tasks);
        return String.format("Fast like myself, I have reversed thy last deed. "
                + "The scroll stands as such:%n%s", this);
    }

    /**
     * Saves the current state of the task list before making any updates,
     * enables easy undo action.
     */
    private void cache() {
        ArrayList<Task> tasksToSave = new ArrayList<>();

        for (Task task : this.tasks) {
            tasksToSave.add(task.copy());
        }

        cachedTasks.push(tasksToSave);
    }

    /**
     * Checks that the index names a task that exists.
     *
     * @param index index of task we are manipulating.
     * @throws HermesException error indicating index out of bounds exception.
     */
    private void checkIndex(int index) throws HermesException {
        if (index < 0 || index >= this.tasks.size()) {
            throw new HermesException("Alas, no task numbered " + (index + 1) + " is to be found.");
        }
    }

    /**
     * Returns every task, numbered as the user refers to them.
     *
     * <p>An empty list gets a hint on how to add a task instead, since a blank
     * reply gives a new user no clue what to do next. The example is written out
     * here rather than taken from the parser's keywords, which would make this
     * package depend on the parser that already depends on it.
     *
     * @return the numbered tasks, or how to add the first one.
     */
    public String listTasks() {
        if (this.tasks.isEmpty()) {
            return "Thy scroll is blank. Begin it with a todo, deadline or event, "
                    + "for instance: todo borrow book";
        }

        return this.toString();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < this.tasks.size(); i++) {
            String temp = String.format("%d. %s\n", i + 1, this.tasks.get(i));
            output.append(temp);
        }

        return output.toString();
    }
}
