package clue.tasks;

/** A basic task, does not store anything on top of the base class. */
public class ToDo extends Task {
    public ToDo(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String save(String separator) {
        return String.join(separator, "T", this.name);
    }

    /** Constructs a ToDo object using an array of string arguments, serialized from {@link save} */
    public static Task construct(String... args) {
        if (args.length != 2) {
            throw new InvalidTaskException("ToDo requires exactly 2 arguments, got " + args.length);
        }
        return new ToDo(args[1]);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ToDo && ((ToDo) obj).name.equals(this.name);
    }
}
