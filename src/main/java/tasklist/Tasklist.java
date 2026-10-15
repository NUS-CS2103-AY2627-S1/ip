package tasklist;

import java.util.ArrayList;
import java.util.Scanner;

import cxception.InvalidDateException;

/**
 * Represents an ordered collection of {@link Task} objects, supporting add, delete,
 * mark/unmark, search, and loading from a saved file format.
 */
public class Tasklist {
    private ArrayList<Task> tasklist;
    private int size = 0;

    /**
     * Constructs an empty task list.
     */
    public Tasklist() {
        this.tasklist = new ArrayList<>();
    }

    /**
     * Constructs a task list by reading saved tasks from the given scanner.
     * Each line is expected in pipe-delimited save format ("T|...", "D|...", "E|...").
     * Malformed or unrecognized lines are skipped with a console message.
     *
     * @param s the scanner to read saved task data from.
     */
    public Tasklist(Scanner s) {
        this.tasklist = new ArrayList<>();
        while (s.hasNextLine()) {
            String newl = s.nextLine();
            if (newl.isBlank()) {
                continue;
            }
            String[] parts = newl.split("\\|");
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }
            try {
                switch (parts[0]) {
                    case "T":
                        String savedPlace = parts.length > 2 ? parts[2] : null;
                        this.tasklist.add(new Task(parts[1], savedPlace));
                        this.size++;
                        break;
                    case "D":
                        this.tasklist.add(new Deadline(parts[1], parts[2]));
                        this.size++;
                        break;
                    case "E":
                        this.tasklist.add(new Event(parts[1], parts[2], parts[3]));
                        this.size++;
                        break;
                    default:
                        System.out.println("Ignoring this line, it doesn't belong to us: " + newl);
                        break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("This line is broken... I'll pretend I never saw it: " + newl);
            } catch (InvalidDateException e) {
                System.out.println("This date is lying to me. Discarding: " + newl);
            }
        }
    }

    /**
     * Returns a numbered, newline-separated string representation of all tasks in the list.
     *
     * @return the formatted task list.
     */
    @Override
    public String toString() {
        String out = "";
        for (int i = 0; i < this.size; i++) {
            out += String.format("%d. %s\n", i + 1, this.tasklist.get(i));
        }
        return out;
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return the task count.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Adds a task to the list and returns a confirmation message.
     *
     * @param t the task to add.
     * @return the confirmation message.
     */
    public String addItem(Task t) {
        this.tasklist.add(t);
        this.size++;
        return "Another one, huh? Good. Stay busy. Stay close to me.\n"
                + t + '\n'
                + String.format("%d tasks now bind you to me. Don't you dare forget any of them.\n", this.size);
    }

    /**
     * Deletes the task at the given index and returns a confirmation message.
     *
     * @param idx the zero-based index of the task to delete.
     * @return the confirmation message.
     */
    public String deleteItem(int idx) {
        Task t = tasklist.get(idx);
        this.tasklist.remove(idx);
        this.size--;
        return "Gone. Just like that. I hope you didn't get attached to it.\n"
                + t + '\n'
                + String.format("%d tasks remain. I'm still watching the rest.\n", this.size);
    }

    /**
     * Marks the task at the given index as done and returns a confirmation message.
     *
     * @param idx the zero-based index of the task to mark.
     * @return the confirmation message.
     */
    public String markItem(int idx) {
        Task marked = this.tasklist.get(idx);
        marked.setDone();
        return "You finished it... for me? I knew you wouldn't disappoint.\n"
                + marked + '\n'
                + String.format("%d tasks left. Don't make me wait for the rest.\n", this.size);
    }

    /**
     * Marks the task at the given index as not done and returns a confirmation message.
     *
     * @param idx the zero-based index of the task to unmark.
     * @return the confirmation message.
     */
    public String unmarkItem(int idx) {
        Task unmarked = this.tasklist.get(idx);
        unmarked.setUndone();
        return "Unfinished again? Fine. I'll keep watching until you get it right.\n"
                + unmarked + '\n'
                + String.format("%d tasks left. I'm not going anywhere.\n", this.size);
    }

    /**
     * Returns all tasks formatted for writing to the save file.
     *
     * @return the file-format representation of all tasks.
     */
    public String fileFormat() {
        String s = "";
        for (int i = 0; i < this.size; i++) {
            s += tasklist.get(i).fileFormatText() + "\n";
        }
        return s;
    }

    /**
     * Returns tasks matching the search term, formatted for display.
     *
     * @param search the search term.
     * @return the matching tasks, or a "no tasks found" message.
     */
    public String findItems(String search) {
        String out = "";
        for (int i = 0; i < this.size; i++) {
            out += tasklist.get(i).contains(search) ? tasklist.get(i) + "\n" : "";
        }
        return out.isEmpty()
                ? String.format("Nothing with \"%s\". I searched everywhere for you.\n", search)
                : out;
    }
}
