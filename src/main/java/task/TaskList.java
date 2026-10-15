package task;

import java.util.ArrayList;

import exception.ListEmptyException;

/**
 * Represents a collection of tasks in the application.
 */
public class TaskList extends ArrayList<Task> {

    /**
     * Constructs a task list.
     */
    public TaskList() {
        super();
    }

    /**
     * Marks a task as done, or not done.
     *
     * @param idx index of task to be marked
     * @param done if true, then the task is marked as done. If false, then the task is unmarked
     * @return status message describing the updated task state
     */
    public String setTask(int idx, boolean done) {
        try {
            if (idx < 0 || idx >= this.size()) {
                throw new NullPointerException("RIEEEEAAAU: INVALID INDEX!");
            } else {
                this.get(idx).setDone(done);
                int num = idx + 1;
                if (done) {
                    return "ok, marked Task" + num + " as done";
                } else {
                    return "ok, unmarked Task" + num;
                }
            }
        } catch (NullPointerException e) {
            return e.getMessage();
        }
    }

    /**
     * adds a task and returns the confirmation message for the user.
     * @param task the task to add
     * @return status message describing the added task
     */
    public String addTask(Task task) {
        assert task != null : "A task list must not contain null tasks";
        this.add(task);
        return "Added: " + task.toString();
    }

    /**
     * Checks whether this list already contains a task equivalent to the given task.
     * Completion state is ignored when comparing tasks.
     *
     * @param candidate task to compare against the tasks in this list
     * @return true if an equivalent task is already present
     */
    public boolean containsDuplicate(Task candidate) {
        assert candidate != null : "Duplicate checks require a task";
        for (Task existingTask : this) {
            assert existingTask != null : "A task list must not contain null tasks";
            if (existingTask.equals(candidate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * removes a task by index and returns the confirmation message for the user.
     * @param idx internal list index to remove
     * @return status message describing the removed task, or an error if invalid
     */
    public String removeTask(int idx) {
        try {
            Task removed = this.remove(idx);
            return "Removed: " + removed.toString();
        } catch (IndexOutOfBoundsException e) {
            return "RIEEEEAAAU: INVALID INDEX!";
        }
    }


    /**
     * finds tasks whose description contains the given keyword (case-insensitive) and
     * returns them as a numbered list, using each task's position in this task list.
     * @param keyword search term to match against task descriptions
     * @return formatted list of matching tasks, or an error message if none match
     */
    public String findTasks(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        StringBuilder matches = new StringBuilder();
        int matchCount = 0;

        for (int i = 0; i < this.size(); i++) {
            Task task = this.get(i);
            assert task != null : "A task list must not contain null tasks";
            if (task.getDescription().toLowerCase().contains(lowerKeyword)) {
                if (matchCount > 0) {
                    matches.append("\n");
                }
                matches.append(i + 1).append(". ").append(task.toString());
                matchCount++;
            }
        }

        if (matchCount == 0) {
            return "RIEEEEAAAU: NO MATCHING TASKS!";
        }
        return "Here are the matching tasks in your list:\n" + matches;
    }

    /**
     * gets the tasklist signature. a signature is an internal
     * string representation by the chatbot, it is not to be confused
     * with the user level string representation given by toString().
     * @return signature of tasklist(for internal use)
     */
    public String toSignature() {
        if (this.isEmpty()) {
            return "";
        } else {
            String out = "";

            for (int i = 0; i <= this.size() - 2; i++) {
                assert this.get(i) != null : "A task list must not contain null tasks";
                out = out + this.get(i).toSignature() + "\n";
            }

            assert this.get(this.size() - 1) != null : "A task list must not contain null tasks";
            out = out + this.get(this.size() - 1).toSignature();
            return out;
        }
    }

    //overrides
    /**
     * gets the user level String representation of this tasklist. unlike toSignature(),
     * this string is the String representation of a tasklist, at the user level.
     * @return user level String representation of tasklist
     */
    @Override
    public String toString() {
        try {
            if (this.isEmpty()) {
                throw new ListEmptyException("RIEEEEAAAU: HISTORY IS EMPTY");
            } else {
                String out = "1. ";

                for (int i = 0; i <= this.size() - 2; i++) {
                    assert this.get(i) != null : "A task list must not contain null tasks";
                    int nextIdx = i + 2;
                    out = out + this.get(i).toString() + "\n" + nextIdx + ". ";
                }

                assert this.get(this.size() - 1) != null : "A task list must not contain null tasks";
                out = out + this.get(this.size() - 1).toString();
                return out;
            }
        } catch (ListEmptyException e) {
            return e.getMessage();
        }
    }
}
