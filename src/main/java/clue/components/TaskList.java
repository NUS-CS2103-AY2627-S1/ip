package clue.components;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;

import clue.tasks.Task;
import clue.ui.Ui;

/**
 * A list of tasks with reporting to keep the user updated.
 */
public class TaskList extends ArrayList<Task> {
    /**
     * Adds a task to the list, reporting to the given Ui object.
     * Validates the task before adding it.
     */
    public void addTask(Ui ui, Task task) {
        if (task == null) {
            ui.println("Error: Cannot add null task");
            return;
        }

        // Check for duplicate task (same name and details)
        for (Task existingTask : this) {
            if (existingTask.equals(task)) {
                ui.println("Warning: A task with the same name '" + task.getName() + "' and details already exists.");
                ui.println("Please use a different name or delete the existing task first.");
                return;
            }
        }

        add(task);
        ui.println("Noted. I've added this task:");
        ui.print("\t");
        ui.println(task);
        reportCount(ui);
    }

    /**
     * Outputs all items in the list to the given Ui object.
     */
    public void list(Ui ui) {
        if (isEmpty()) {
            ui.println("nothing here...");
            return;
        }

        ui.println("Here are your saved tasks:");
        for (int i = 1; i <= size(); i++) {
            printTask(ui, i, get(i - 1));
        }
    }

    /**
     * Prints tasks whose displayed text approximately contains the supplied keyword.
     *
     * @param ui the interface used to display the matching tasks
     * @param keyword the case-insensitive text to search for
     */
    public void find(Ui ui, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            ui.println("Please provide a valid keyword to search for!");
            return;
        }

        String searchTerm = keyword.toLowerCase();
        TreeMap<Integer, LinkedList<Task>> found = new TreeMap<>();

        for (Task task : this) {
            if (task == null) {
                continue;
            }
            int similar = fuzzy_contains(task.getName().toLowerCase(), searchTerm);
            if (similar != -1) {
                found.putIfAbsent(similar, new LinkedList<>());
                // in insertion order
                found.get(similar).add(task);
            }
        }

        if (found.isEmpty()) {
            ui.println("No matching tasks found.");
            return;
        }

        int i = 1;
        ui.println("Here are some tasks found, sorted by relevance:");
        for (LinkedList<Task> tasks : found.values()) {
            for (Task t : tasks) {
                printTask(ui, i++, t);
            }
        }
    }

    /**
     * Fuzzy searches the given string for the given term.
     * @param string - the string to search through
     * @param term - the term we are looking for
     * @return - how approximately does the term appear in the string.
     *     larger numbers are worse. -1 is returned if the term does not really appear.
     */
    static int fuzzy_contains(String string, String term) {
        int n = term.length();
        if (n <= 2) {
            // Plain contains for short terms
            return string.contains(term) ? 0 : -1;
        }
        int skips = Integer.min(n - 2, 2);
        int[] needed = new int[n];

        // initialize skips needed array
        for (int i = 0; i < n; i++) {
            needed[i] = i < skips ? i + 1 : 9999;
        }

        int lowest = 9999;

        for (int i = 0; i < string.length(); i++) {
            for (int j = n - 1; j >= 0; j--) {
                // update skips needed
                boolean m = string.charAt(i) == term.charAt(j);
                if (j == 0) {
                    needed[j] = m ? j : 1;
                    continue;
                }
                int prev = needed[j - 1];
                if (m) {
                    // current is match: minimum of prev, current + 1, next + 1
                    needed[j] = Integer.min(prev, Integer.min(
                            needed[j],
                            j + 1 == n ? 99 : needed[j + 1]
                    ) + 1);
                } else {
                    // minimum of prev + 1, current + 1
                    needed[j] = Integer.min(prev, needed[j]) + 1;
                }
            }
            lowest = Integer.min(lowest, needed[n - 1]);
        }

        // account for end of string
        for (int i = 0; i < skips; i++) {
            lowest = Integer.min(lowest, needed[n - i - 1] + i);
        }

        return lowest <= skips ? lowest : -1;
    }

    /**
     * Reports the count of items in the list.
     */
    public void reportCount(Ui ui) {
        ui.printf(
                "Now, there %s %d task%s in the list.\n",
                size() == 1 ? "is" : "are",
                size(),
                size() == 1 ? "" : "s");
    }

    /**
     * Prints a single task to the UI as part of a list with the given index.
     * @param ui the {@link Ui} object to output to.
     * @param index the index of this task.
     * @param task the task to output.
     */
    static void printTask(Ui ui, int index, Task task) {
        if (task == null) {
            ui.printf(" %d. [INVALID TASK]\n", index);
            return;
        }
        ui.printf(" %d. %s\n", index, task);
    }
}
