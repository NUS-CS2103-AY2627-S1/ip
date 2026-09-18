package blud.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import blud.command.CommandValidator;
import blud.exception.CommandFormatException;
import blud.exception.DeletionException;
import blud.exception.DuplicateTaskException;
import blud.exception.TaskIndexException;

/** Stores and updates tasks in their user-visible order. */
public class TaskList {
    private final List<Task> taskList;
    private SortCriterion sortCriterion;
    private SortDirection sortDirection;

    /** Represents the fields that can be used to order tasks. */
    public enum SortCriterion {
        DEADLINE,
        EVENT,
        STATUS
    }

    /** Represents the direction in which tasks are ordered. */
    public enum SortDirection {
        ASCENDING,
        DESCENDING
    }

    /** Creates an empty task list. */
    public TaskList() {
        this(new ArrayList<>());
    }

    /** Creates a task list containing the supplied tasks. */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "A task list must be backed by a non-null collection";
        this.taskList = new ArrayList<>(tasks);
        this.sortCriterion = null;
        this.sortDirection = null;
    }

    /** Removes and returns the task identified by a one-based command index. */
    public Task delete(String[] splitInput) {
        Task removedTask;
        if (splitInput.length < 2) {
            throw new DeletionException();
        } else {
            try {
                int index = CommandValidator.parseTaskIndex(splitInput[1]);
                removedTask = getTaskForCommandIndex(index);
                this.taskList.remove(index);
                assert removedTask != null : "A stored task must not be null";
            } catch (CommandFormatException | TaskIndexException e) {
                if (!this.taskList.isEmpty()) {
                    throw new DeletionException(e.getMessage());
                } else {
                    throw new DeletionException("task list is empty, nothing to delete");
                }
            }
        }
        return removedTask;
    }

    /** Adds a task to the end of this list. */
    public void addTask(Task newTask) {
        assert newTask != null : "Only valid tasks may be added to the task list";
        if (taskList.stream().anyMatch(task -> task.hasSameDetails(newTask))) {
            throw new DuplicateTaskException();
        }
        int sizeBeforeAdding = this.taskList.size();
        if (this.sortCriterion == null) {
            this.taskList.add(newTask);
        } else {
            addInSortedPosition(newTask);
        }
        assert this.taskList.size() == sizeBeforeAdding + 1
                : "Adding one task must increase the task-list size by one";
    }

    /** Sorts the tasks stably by the selected criterion and direction. */
    public void sort(SortCriterion criterion, SortDirection direction) {
        assert criterion != null : "A sort criterion must be specified";
        assert direction != null : "A sort direction must be specified";
        this.sortCriterion = criterion;
        this.sortDirection = direction;
        this.taskList.sort((firstTask, secondTask) -> compareTasks(firstTask, secondTask));
    }

    /** Returns the number of tasks in this list. */
    public int getSize() {
        return this.taskList.size();
    }

    /** Returns the backing list of tasks in display order. */
    public List<Task> getTaskList() {
        return Collections.unmodifiableList(this.taskList);
    }

    /** Returns the task at the supplied zero-based index. */
    public Task getTask(int i) {
        return this.taskList.get(i);
    }

    /** Marks the task at the supplied zero-based index as completed. */
    public void markTask(int i) {
        getTaskForCommandIndex(i).mark();
    }

    /** Returns a task for a zero-based command index with a user-facing validation error. */
    public Task getTaskForCommandIndex(int i) {
        if (i < 0 || i >= taskList.size()) {
            if (taskList.isEmpty()) {
                throw new TaskIndexException("There are no tasks in the list");
            }
            throw new TaskIndexException(
                    String.format("Please specify a task number from 1 to %d", taskList.size()));
        }
        return taskList.get(i);
    }

    /** Inserts a task after existing tasks with an equal sort value. */
    private void addInSortedPosition(Task newTask) {
        int insertionIndex = 0;
        while (insertionIndex < taskList.size()
                && compareTasks(taskList.get(insertionIndex), newTask) <= 0) {
            insertionIndex++;
        }
        taskList.add(insertionIndex, newTask);
    }

    /** Compares two tasks using the current session sort. */
    private int compareTasks(Task firstTask, Task secondTask) {
        if (sortCriterion == SortCriterion.STATUS) {
            int statusComparison = Boolean.compare(firstTask.isDone(), secondTask.isDone());
            return sortDirection == SortDirection.ASCENDING ? statusComparison : -statusComparison;
        }

        LocalDateTime firstDate = getSortDate(firstTask);
        LocalDateTime secondDate = getSortDate(secondTask);
        if (firstDate == null) {
            return secondDate == null ? 0 : -1;
        }
        if (secondDate == null) {
            return 1;
        }
        int dateComparison = firstDate.compareTo(secondDate);
        return sortDirection == SortDirection.ASCENDING ? dateComparison : -dateComparison;
    }

    /** Returns the date associated with the current date-based sort criterion. */
    private LocalDateTime getSortDate(Task task) {
        if (sortCriterion == SortCriterion.DEADLINE && task instanceof Deadline deadline) {
            return deadline.getDeadline();
        }
        if (sortCriterion == SortCriterion.EVENT && task instanceof Event event) {
            return event.getStartDate();
        }
        return null;
    }
}
