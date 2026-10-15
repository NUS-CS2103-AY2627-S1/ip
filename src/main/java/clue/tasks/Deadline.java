package clue.tasks;

import java.time.LocalDateTime;

/** A Deadline, storing a {@link by} time on top of the base class. */
public class Deadline extends Task {
    private final LocalDateTime by;

    /** Constructs a Deadline with the given name and by time. */
    public Deadline(String name, LocalDateTime by) {
        super(name);
        this.by = Task.validateDate(by, "Deadline");
    }

    /**
     * Returns the deadline time.
     */
    public LocalDateTime getBy() {
        return by;
    }

    @Override
    public String toString() {
        return String.format("[D]%s (by %s)", super.toString(), displayDate(this.by));
    }

    @Override
    public String save(String separator) {
        return String.join(separator, "D", this.name, this.by.toString());
    }

    /** Constructs a Deadline object using an array of string arguments, serialized from {@link save} */
    public static Task construct(String... args) {
        if (args.length != 3) {
            throw new InvalidTaskException("Deadline requires exactly 3 arguments, got " + args.length);
        }
        LocalDateTime by = tryParseDate(args[2]);
        Task.validateDate(by, "Deadline");
        return new Deadline(args[1], by);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Deadline && ((Deadline) obj).name.equals(this.name)
                && ((Deadline) obj).by.equals(this.by);
    }
}
