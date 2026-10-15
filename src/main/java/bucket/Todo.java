package bucket;

import java.time.LocalDate;
import java.util.Optional;

/** A task with no date attached, e.g. "borrow book". */
public class Todo extends Task {

    /**
     * Creates a todo.
     *
     * @param name Description the user typed.
     */
    public Todo(String name) {
        super(name);
    }

    @Override
    public String getTypeIcon() {
        return "T";
    }

    /**
     * Returns nothing to sort on, since a todo carries no date at all.
     * Sorting puts these after every dated task rather than guessing one.
     *
     * @return Always empty.
     */
    @Override
    public Optional<LocalDate> getSortDate() {
        return Optional.empty();
    }
}
