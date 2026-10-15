package hu9o.errors;

/** Indicates that a todo command has no description. */
public class InvalidTodoFormatException extends Hu9oException {
    public InvalidTodoFormatException() {
        super("Ruff, I need a description for that todo! Try: todo DESCRIPTION");
    }
}
