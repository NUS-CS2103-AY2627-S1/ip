package bonbon.tasklist;

import bonbon.exception.BonBonException;
import bonbon.exception.BonBonOutOfBoundsException;
import bonbon.exception.BonBonTaskListFullException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the collection of tasks and handles task-related operations such as adding and deleting entries.
 */
public class TaskList {
    private List<Task> tasks;
    private final int size;
    private int currSize;

    /**
     * Initializes the task list.
     *
     * @param size the maximum size of the task list.
     */
    public TaskList(int size) {
        tasks = new ArrayList<Task>(size);
        this.size = size;
        currSize = 0;
    }

    /**
     * Adds a new task
     *
     * @param
     * @return the index of the added task;
     * @throws BonBonException when list is full.
     */
    public int addTask(Task t) throws BonBonTaskListFullException {
        if (currSize == size) {
            throw new BonBonTaskListFullException(size);
        }
        tasks.add(t);
        currSize++;
        return currSize - 1;
    }

    /**
     * Adds a new ToDo task to the task list.
     *
     * @param s the details or name of the task to add.
     * @return the zero-based index of the newly added task, or -1 if the list is full.
     */
    public int addToDo(String s) throws BonBonTaskListFullException {
        ToDo td = new ToDo(s);
        return addTask(td);
    }

    /**
     * Adds a new Deadline task to the task list.
     *
     * @param desc the details or name of the task to add.
     * @param date the due date in format 'yyyy-MM-dd HHmm'.
     * @return the zero-based index of the newly added task, or -1 if the list is full.
     */
    public int addDeadline(String desc, LocalDateTime date) throws BonBonTaskListFullException {
        Deadline deadline = new Deadline(desc, date);
        return addTask(deadline);
    }


     /**
     * Adds a new Event task to the task list.
     *
     * @param desc the details or name of the task to add.
     * @param start the start date in format 'yyyy-MM-dd HHmm'.
     * @param end the end date in format 'yyyy-MM-dd HHmm'
     * @return the zero-based index of the newly added task
     * @throws BonBonException
     */
    public int addEvent(String desc, LocalDateTime start, LocalDateTime end, String place) throws BonBonException{
        Event event = new Event(desc, start, end, place);
        return addTask(event);
    }

    /**
     * Removes task from the task list, and displays the task removed.
     *
     * @param i the zero-based index of the task to be removed.
     * @return 1 if successfully deleted, and -1 if the index is out of range.
     */
    public int removeTask (int i) throws BonBonOutOfBoundsException {
        if (i >= currSize || i < 0) {
            throw new BonBonOutOfBoundsException(currSize);
        }
        tasks.remove(i);
        currSize--;
        return 1;
    }

    /**
     * Marks task as done in the task list.
     *
     * @param i
     */
    public void mark(int i) throws BonBonOutOfBoundsException {
        if (i >= currSize || i < 0) {
            throw new BonBonOutOfBoundsException(currSize);
        }
        tasks.get(i).markDone();
    }

    /**
     * Marks task as undone in the task list.
     *
     * @param i
     */
    public void unmark(int i) throws BonBonOutOfBoundsException {
        if (i >= currSize || i < 0) {
            throw new BonBonOutOfBoundsException(currSize);
        }
        tasks.get(i).markUndone();
    }

    /**
     * Returns all tasks that have given string as a substring in its description.
     *
     * @param s the substring that the task must have.
     * @return formatted string with all matching tasks.
     */
    public String find(String s) {
        boolean foundTask = false;
        String finalString = "Matching tasks found: ";
        for (int i = 0; i < currSize; i++) {
            Task task = tasks.get(i);
            if (task.getName().contains(s)) {
                finalString += String.format("\n%d. %s", i + 1, tasks.get(i).toString());
                foundTask = true;
            }
        }
        if (foundTask) {
            return finalString;
        }
        return "No tasks found!";
    }

    public Task getLastTask() throws BonBonOutOfBoundsException {
        if (currSize <= 0) {
            throw new BonBonOutOfBoundsException(currSize);
        }
        return tasks.get(currSize - 1);
    }

    /**
     * Returns formatted string displaying all tasks in task list.
     *
     * @return the formatted string displaying all tasks in task list.
     */
    @Override
    public String toString() {
        if (currSize == 0) {
            return "No tasks currently! :)";
        }
        String finalString = "Your current tasks are:";
        for (int i = 0; i < currSize; i++) {
            finalString += String.format("\n%d. %s", i + 1, tasks.get(i).toString());
        }
        return finalString;
    }

    public Task getTask(int i) {
        return tasks.get(i);
    }

    public int getCurrSize() {
        return currSize;
    }
}