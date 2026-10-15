package spoon.task;

import java.time.LocalDate;

/**
 * Represents a generic task with fields name and isCompleted.
 */
public abstract class Task {

    // Symbol Definitions
    private static final String COMPLETED_SYMBOL = "[X] ";
    private static final String UNCOMPLETED_SYMBOL = "[ ] ";

    private final String name;
    private boolean isCompleted = false;

    // Constructor
    public Task(String name) {
        this.name = name;
    }

    // Methods
    public void complete() {
        this.isCompleted = true;
    }

    public void uncomplete() {
        this.isCompleted = false;
    }

    /**
     * Determines if the task is due on a specific date.
     *
     * @return boolean if the task is due on a specific date.
     */
    public boolean isDueOn(LocalDate date) {
        return false;
    }

    /**
     * Determines if the task is due on a specific date.
     *
     * @return boolean if the task is due on a specific date.
     */
    public boolean isDueBy(LocalDate date) {
        return false;
    }

    /**
     * Checks if the task contains the specfied word.
     *
     * @param word the word to search for.
     * @return true if the task contains the specified word, else false.
     */
    public boolean containsWord(String word) {
        return this.name.toLowerCase().contains(word.toLowerCase());
    }

    /**
     * Formats the task into string for writing to the file.
     *
     * @return file-formatted representation of task.
     */
    public String format() {
        String status = this.isCompleted ? "1" : "0";
        return status + " | " + name;
    }

    @Override
    public String toString() {
        String status = this.isCompleted ? COMPLETED_SYMBOL : UNCOMPLETED_SYMBOL;
        return status + this.name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Task otherTask)) {
            return false;
        }
        return this.name.equalsIgnoreCase(otherTask.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}
