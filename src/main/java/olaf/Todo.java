package olaf;

import java.util.List;

/**
 * Represents a task without a date.
 */
final class Todo extends Task {
    /** Identifies a to-do task in storage and displayed responses. */
    static final String TYPE_CODE = "T";

    Todo(String description) {
        super(description);
    }

    private Todo(Todo original) {
        super(original);
    }

    /** {@inheritDoc} */
    @Override
    Task copy() {
        return new Todo(this);
    }

    /** {@inheritDoc} */
    @Override
    String getTypeCode() {
        return TYPE_CODE;
    }

    /** {@inheritDoc} */
    @Override
    List<String> getAdditionalStorageFields() {
        return List.of();
    }
}
