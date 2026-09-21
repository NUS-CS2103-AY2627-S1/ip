package auto.task;

import java.time.LocalDate;

import auto.util.DateUtil;

/** Represents a task that is due on a specific date. */
public class Deadline extends Task {
    private final LocalDate dueDate;

    /**
     * Creates an incomplete deadline with the specified description and due date.
     */
    public Deadline(String name, LocalDate dueDate) {
        super(name);
        this.dueDate = dueDate;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return dueDate.equals(date);
    }

    @Override
    public String toDataString() {
        return String.format("D | %d | %s | %s",
                isCompleted() ? 1 : 0, encode(getName()), encode(DateUtil.format(dueDate)));
    }

    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), DateUtil.format(dueDate));
    }
}
